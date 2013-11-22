package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * A custom widget to show speed info.
 * @author dkpure
 *
 */
public class SpeedInfo extends View {
//	final static int	DEFAULT_TEXT_SIZE = 120;//40;
	final static int	DEFAULT_NUMBER_COLOR = Color.rgb(177, 241, 24);
	final static int	DEFAULT_UNIT_COLOR = Color.WHITE;
	
	//private states
	private static final int S_IDLE 		= 0;
	private static final int S_STARTUP 		= 1;
	private static final int S_RUNNING 		= 2;
	private static final int S_SLOWDOWN 	= 3;
	private int m_state = S_IDLE;
	
	// attributes
	private	int		current_val;
//	private	int		text_size;
	private int		number_color;
	private	int		unit_color;
	
	// graphics related
	private	Paint	text_paint = null;
	private Paint	unit_paint = null;
//	private	int		y_margin = 0;//10;
	private int		base_line = 0;
	private int		text_w = 0;
	private int		unit_string_x = 0;
	private String	m_speed_unit;
	
	//Handler and Runnable
	private Handler 	m_tick_handler = null;
	private Runnable 	m_tick_runnable = null;
	private int			m_delay_cnt = 0;
	
	public SpeedInfo(Context context) {
		super(context);
		
		init();
	}
	
	public SpeedInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		init();
	}
	
	/**
	 * Set current speed value
	 * @param val speed value to be set
	 */
	public void setValue(int val) {
		if (val >= 0) {
			if (needResize(val))
				requestLayout(); // Invoke OnMeasure()
			
			current_val = val;
			invalidate(); // Invoke OnDraw()
		}
	}
	
	private boolean needResize(int v) {
		int len1 = String.valueOf(v).length();
		int len2 = String.valueOf(current_val).length();
		
		return (len1 != len2 && (len1 > 2 || len2 > 2)) ? true: false;
	}
	
	/**
	 * Do some initialization work.
	 */
	private void init() {
		current_val = 0;
//		text_size = DEFAULT_TEXT_SIZE;
		number_color = DEFAULT_NUMBER_COLOR;
		unit_color = DEFAULT_UNIT_COLOR;
		
		text_paint = new Paint();
		text_paint.setAntiAlias(true);
		
		text_paint.setColor(number_color);
		
		unit_paint = new Paint();
		unit_paint.setAntiAlias(true);
		unit_paint.setColor(unit_color);
		
		if (GlobalParams.RUN_720P) {
			text_paint.setTextSize(110);
			unit_paint.setTextSize(60);
		} else {
			text_paint.setTextSize(60);
			unit_paint.setTextSize(40);
		}
		
		m_speed_unit = getResources().getString(R.string.obd_Speed_unit);
		
		m_tick_handler = new Handler();	
		m_tick_runnable = new Runnable() {
			@Override
			public void run() {
				switch (m_state) {
				case S_IDLE:
					if (m_started) {
						m_state = S_STARTUP;
						m_tick_handler.postDelayed(this, 100);
					}
					break;
				case S_STARTUP:
					if (m_delay_cnt < 8) {
						// Assume this:
						// at startup period, speed grows form 0 to 48mph within 8 seconds
						setValue((int)((m_delay_cnt + Math.random()) * 6.0f));
						m_delay_cnt++;
						m_tick_handler.postDelayed(this, 1000);
					} else {
						m_delay_cnt = 0;
						m_state = S_RUNNING;
						m_tick_handler.postDelayed(this, 4000); // loop it
					}
					break;
				case S_RUNNING:
					if (!m_started) {
						m_state = S_SLOWDOWN;
						m_tick_handler.postDelayed(this, 100);
					} else {
						setValue(40 + (int)((Math.random() - 0.5f) * 12));
						m_tick_handler.postDelayed(this, 4000); // loop it
					}
					break;
				case S_SLOWDOWN:
					if (m_delay_cnt < 4) {
						// Assume this: 
						// at slow down period, speed decreases form 48 to 0 within 4 seconds
						setValue((int)(((3 - m_delay_cnt) + Math.random()) * 12.0f));
						++m_delay_cnt;
						m_tick_handler.postDelayed(this, 1000);
					} else {
						setValue(0);
						m_state = S_IDLE;
					}
					break;
				default:
					break;
				}
			}
		};
		
	}
	
	private boolean m_started = false;
	public void start() {
		m_started = true;
		m_tick_handler.postDelayed(m_tick_runnable, 100); // trick the runnable after 100ms
	}
	
	public void stop() {
		m_started = false;
	}

	/**
     * Return the baseline of text Paint when align to H vertically
     * @param H height to put text
     * @param p text paint
     * @return baseline of text to be drawn within H
     */
//	private int getCenterAlignBaseline(int H, Paint p) {
//		int text_ascent = -(int) p.ascent();
//		int text_descent = (int) p.descent();
//		
//		return (H + text_ascent - text_descent) / 2;
//	}
	
	/**
	 * 
	 * @return suitable width of layout
	 */
	private int getSuitableWidth() {
    	int ret = 10; // set minimal width to 10
    	
    	if (null != text_paint && null != unit_paint) {
    		String tmp = String.valueOf(current_val);
    		if (tmp.length() > 4) {
    			text_w = (int) text_paint.measureText(tmp);
    		} else {
    			text_w = (int) text_paint.measureText("88");
    		}
    		
    		ret = text_w + x_split + (int) unit_paint.measureText(m_speed_unit);
    		unit_string_x = ret - (int) unit_paint.measureText(m_speed_unit);
    	}
    	
    	return ret;
    }
	
	/**
	 * 
	 * @return suitable height of layout
	 */
    private int getSuitableHeight() {
    	int ret = 10; // set minimal height to 10
    	
    	if (null != text_paint && null != unit_paint) {
    		ret  = -(int) text_paint.ascent() + (int) text_paint.descent();
    		base_line = -(int) text_paint.ascent();//getCenterAlignBaseline(ret, text_paint);
    	}
    	
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
	
	private	int		x_split	= 15;
	@Override
	protected void onDraw (Canvas canvas) {
		super.onDraw(canvas);

		String val_str = String.valueOf(current_val);
		int cur_text_w = (int) text_paint.measureText(val_str);
		int x = (text_w - cur_text_w) / 2;
		
		canvas.drawText(val_str, x, base_line, text_paint); //draw value
		canvas.drawText(m_speed_unit, unit_string_x, base_line, unit_paint); // draw unit
	}
	
}
