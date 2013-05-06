package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A custom widget to show RPM message.
 * @author dkpure
 *
 */
public class RpmInfo extends View {
	final static String RPM_UNIT_STRING = "ת";
	final static int	DEFAULT_TEXT_SIZE = 40;
	final static int	DEFAULT_NUMBER_COLOR = Color.rgb(177, 241, 24);
	final static int	DEFAULT_UNIT_COLOR = Color.WHITE;
	
	// attributes
	private	int		current_val;
	private	int		text_size;
	private int		number_color;
	private	int		unit_color;
	
	// graphics related
	private	Paint	text_paint;
	private	int		y_margin = 10;
	private	int		x_split	= 8;
	private int		base_line = 0;
	private int		text_w = 0;
	private int		unit_string_x = 0;
	
	public RpmInfo(Context context) {
		super(context);
		
		init();
	}
	
	public RpmInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		init();
	}
	
	/**
	 * Set current RPM value
	 * @param val RPM value to be set, must be positive
	 */
	public void setValue(int val) {
		if (val > 0) {
			if (needResize(val))
				requestLayout(); // Invoke OnMeasure()
			
			current_val = val;
			invalidate(); // Invoke OnDraw()
		}
	}
	
	private boolean needResize(int v) {
		int len1 = String.valueOf(v).length();
		int len2 = String.valueOf(current_val).length();
		
		return (len1 != len2 && (len1 > 4 || len2 > 4)) ? true: false;
	}
	
	/**
	 * Do some initialization work.
	 */
	private void init() {
		current_val = 0;
		text_size = DEFAULT_TEXT_SIZE;
		number_color = DEFAULT_NUMBER_COLOR;
		unit_color = DEFAULT_UNIT_COLOR;
		
		text_paint = new Paint();
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(text_size);
		text_paint.setColor(number_color);
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
	 * 
	 * @return suitable width of layout
	 */
	private int getSuitableWidth() {
    	int ret = 10; // set minimal width to 10
    	
    	if (null != text_paint) {
    		String tmp = String.valueOf(current_val);
    		
    		if (tmp.length() > 4) {
    			text_w = (int) text_paint.measureText(tmp);
    		} else {
    			text_w = (int) text_paint.measureText("8888");
    		}
    		
    		ret = text_w + x_split + (int) text_paint.measureText(RPM_UNIT_STRING);
    		unit_string_x = ret - (int) text_paint.measureText(RPM_UNIT_STRING);
    	}
    	
    	return ret;
    }
	
	/**
	 * 
	 * @return suitable height of layout
	 */
    private int getSuitableHeight() {
    	int ret = 10; // set minimal height to 10
    	
    	if (null != text_paint) {
    		ret  = -(int) text_paint.ascent() + (int) text_paint.descent() + y_margin;
    		base_line = getCenterAlignBaseline(ret, text_paint);
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
	
	@Override
	protected void onDraw (Canvas canvas) {
		super.onDraw(canvas);
		
		String val_str = String.valueOf(current_val);
		int cur_text_w = (int) text_paint.measureText(val_str);
		int x = (text_w - cur_text_w) / 2;
		
		text_paint.setColor(number_color);
		canvas.drawText(val_str, x, base_line, text_paint); //draw value
		text_paint.setColor(unit_color);
		canvas.drawText(RPM_UNIT_STRING, unit_string_x, base_line, text_paint); // draw unit
	}
	
}
