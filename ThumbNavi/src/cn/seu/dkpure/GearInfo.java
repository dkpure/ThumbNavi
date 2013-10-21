package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * A custom widget to show gear info
 * @author dkpure
 *
 */
public class GearInfo extends View implements Runnable{
	public enum GAER_ENUMS {GEAR_N, GEAR_1, GEAR_2, GEAR_3, GEAR_4, GEAR_5, GEAR_R};
	final static String GEAR_UNIT_STRING = "µµ";
	final static String	GEAR_ENUM_STRING = "¿Õ12345R";
	final static int	MIN_REFRESH_DURATION = 30; // refresh rate: 1000/30=33fps
	final static int	DEFAULT_ANIMATION_DURATION = 800; //in ms
	final static int	DEFAULT_TEXT_SIZE = 40;
	final static int	DEFAULT_NUMBER_COLOR = Color.rgb(177, 241, 24);
	final static int	DEFAULT_UNIT_COLOR = Color.WHITE;
	
	// attributes
	private	GAER_ENUMS	current_gear;
	private	int		text_size;
	private int		number_color;
	private	int		unit_color;
	
	// graphics related
	private	int		y_margin = 10;
	private	int		x_split	= 8;
	private	Bitmap	image_buffer;
	private Bitmap	show_buffer;
	private	Canvas	i_canvas;
	private	Rect	buffer_rect;
	private	Rect	show_rect;
	
	// custom animation related
	private volatile boolean animCanRun;
	private int		anim_y_total_delta;
	private int		anim_y0;
	private int		anim_duration;
	private long	anim_millis0;
	private AccelerateDecelerateInterpolator anim_interpolator;
	private Thread 	anim_thread = null;
	
	public GearInfo(Context context) {
		super(context);
		
		initGearInfo();
	}
	
	public GearInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initGearInfo();
	}
	
	/**
	 * Do some initialization work.
	 */
	private void initGearInfo() {
		animCanRun = false;
		anim_millis0 = 0;
		anim_y0 = 0;
		anim_y_total_delta = 0;
		anim_duration = DEFAULT_ANIMATION_DURATION;
		current_gear = GAER_ENUMS.GEAR_N;
		text_size = DEFAULT_TEXT_SIZE;
		number_color = DEFAULT_NUMBER_COLOR;
		unit_color = DEFAULT_UNIT_COLOR;
		
		buffer_rect = new Rect();
		show_rect = new Rect();
		anim_interpolator = new AccelerateDecelerateInterpolator();

		renderImageBuffer();		
	}
	
	/**
     * Return the baseline of text Paint when align to H vertically
     * @param H height to put text
     * @param p text paint
     * @return baseline of text to be drawn within H
     */
	private int getCenterAlignBaseline(int H, Paint p) {
		int text_ascent = -(int) p.ascent();
		int text_descent = (int) p.descent();
		
		return (H + text_ascent - text_descent) / 2;
	}
	
	/**
	 * Render image buffer and show buffer, 
	 * then refresh layout and interface
	 * image buffer: a bitmap contains all the UI content
	 * show buffer: a bitmap which is a copied area of image buffer 
	 * 				which is to be drawn on the UI canvas
	 */
	private	void renderImageBuffer() {
		// render image buffer
		Paint	text_paint;
		text_paint = new Paint();
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(text_size);
		text_paint.setColor(number_color);
		
		int text_ascent = - (int)text_paint.ascent();
		int text_h = text_ascent + (int) text_paint.descent();
		int text_w = (int) text_paint.measureText("¿Õ");
		int margined_h = text_h + y_margin;
		
		buffer_rect.set(0, 0, text_w, margined_h);
		image_buffer = Bitmap.createBitmap(text_w, GEAR_ENUM_STRING.length() * margined_h, Config.ARGB_8888);
		Canvas tmp_canvas= new Canvas(image_buffer);
		
		if (null == image_buffer || null == tmp_canvas)
			return;
		
		int x = 0;
		int y = 0;
		int base_line = getCenterAlignBaseline(margined_h, text_paint);
		
		/* FIXME: fille with blck color make this view not transparent. */
		tmp_canvas.drawColor(Color.BLACK);
		for (int i = 0; i < GEAR_ENUM_STRING.length(); ++i) {
			String cur_gear = GEAR_ENUM_STRING.substring(i, i + 1);
			int cur_text_w = (int) text_paint.measureText(cur_gear);
			x = (text_w - cur_text_w) / 2;
			y = base_line + i * margined_h;
			
			tmp_canvas.drawText(cur_gear, x, y, text_paint);			
		}
		
		// render show buffer
		int tmp_w = (int) text_paint.measureText(GEAR_UNIT_STRING);
		show_rect.set(0, 0, text_w, margined_h);
		show_buffer = Bitmap.createBitmap(text_w + x_split + tmp_w, margined_h, Config.ARGB_8888);
		text_paint.setColor(unit_color);
		i_canvas = new Canvas(show_buffer);
		i_canvas.drawText(GEAR_UNIT_STRING, 
							show_buffer.getWidth() - tmp_w, 
							base_line, 
							text_paint);
		
		// resize and refresh the interface
		requestLayout(); 	//invoke onMeasure
		invalidate(); 		//invoke onDraw
	}
	
	/**
	 * Convert GAER_ENUMS value to inner index
	 * @param g GAER_ENUMS value
	 * @return inner index
	 */
	private int gear2Index(GAER_ENUMS g) {
		int g_index = -1;
		
		switch (g) {
			case GEAR_N: g_index = 0; break;
			case GEAR_1: g_index = 1; break;
			case GEAR_2: g_index = 2; break;
			case GEAR_3: g_index = 3; break;
			case GEAR_4: g_index = 4; break;
			case GEAR_5: g_index = 5; break;
			case GEAR_R: g_index = 6; break;
			default: return -1;
		}
		
		return g_index;
	}
	
	/**
	 * Set the duration of animation
	 * @param d duration value
	 */
	public void setAnimDuration(int d) {
		if (d > 200 && d < 10000) {
			anim_duration = d;
		}
	}
	
	/**
	 * Set current gear value and refresh the interface
	 * @param g GAER_ENUMS value to be set
	 */
	public void setGear(GAER_ENUMS g) {
		int g_index = 0;
		
		g_index = gear2Index(g);
		current_gear = g;
		
		if (g_index >= 0 && g_index < GEAR_ENUM_STRING.length()) {
			if (i_canvas != null && show_buffer != null && image_buffer != null) {
				int y_delta = g_index * buffer_rect.height();

				buffer_rect.top += y_delta;
				buffer_rect.bottom += y_delta;
				i_canvas.drawBitmap(image_buffer, buffer_rect, show_rect, null);
				invalidate();
			}
		}
	}
	
	/**
	 * Switch current gear value and refresh the interface
	 * with an animation
	 * @param g GAER_ENUMS value to be set
	 */
	public void animateSwitchGear(GAER_ENUMS g) {
		
		if (g == current_gear || isInAnimation())
			return;
		
		int index_next =  gear2Index(g);
		int index_current =  gear2Index(current_gear);
		
		anim_y0 = buffer_rect.top;
		anim_y_total_delta = (index_next - index_current) * buffer_rect.height();
		
		startAnimThread();
		current_gear = g;
	}
	
	/**
	 * Check whether the animation thread is ALIVE
	 * @return last animation has finished or not
	 */
	private boolean isInAnimation() {
		return (anim_thread != null && anim_thread.isAlive()) ? true : false;
	}
	
	/**
	 * Start a thread to render animation effect
	 */
	private void startAnimThread() {
		//make sure we are not in animation now
		if (isInAnimation())
			return;
		
		anim_millis0 = System.currentTimeMillis();
		animCanRun = true;
		
		Thread localThread = new Thread(this);
		anim_thread = localThread;
		anim_thread.start();
	}
	
	/**
	 * 
	 * @return suitable width of layout
	 */
	private int getSuitableWidth() {
    	int ret = 10; // set minimal width to 10
    	
    	if (show_buffer != null)
    		ret = show_buffer.getWidth();
    	
    	return ret;
    }
	
	/**
	 * 
	 * @return suitable height of layout
	 */
    private int getSuitableHeight() {
    	int ret = 10; // set minimal height to 10
    	
    	if (show_buffer != null)
    		ret = show_buffer.getHeight();
    	
    	return ret;
    }
    
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = getSuitableWidth();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = getSuitableHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        
        return result;
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    setMeasuredDimension(measureWidth(widthMeasureSpec),
	            measureHeight(heightMeasureSpec));
	}
	
	@Override
	protected void onDraw (Canvas canvas) {
		super.onDraw(canvas);
		
		if (show_buffer != null) {
			canvas.drawBitmap(show_buffer, 0, 0, null);
		}		
	}
	
	/**
	 * Drawing process called in animation thread
	 * @param delta_mills delta millis from animation start
	 */
	private void myDraw(long delta_mills) {
		float x, y;
		int y_delta;
		
		if (delta_mills >= anim_duration) {
			animCanRun = false;
			y_delta = anim_y_total_delta;
		} else {		
			x = (float)delta_mills / (float)anim_duration;
			y = anim_interpolator.getInterpolation(x);
			y_delta = (int) (y * anim_y_total_delta);
		}
		
		//record buffer_rect.height() here (height = bottom - top)
		int h = buffer_rect.height();
		buffer_rect.top = anim_y0 + y_delta;
		buffer_rect.bottom = buffer_rect.top + h;
//		i_canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		i_canvas.drawBitmap(image_buffer, buffer_rect, show_rect, null);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (animCanRun) {
			long start = System.currentTimeMillis();
			myDraw(start - anim_millis0);
			postInvalidate(); // we are not in the UI thread
			long end = System.currentTimeMillis();
			
			try {
				if (end - start < MIN_REFRESH_DURATION) {
					Thread.sleep(MIN_REFRESH_DURATION - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
