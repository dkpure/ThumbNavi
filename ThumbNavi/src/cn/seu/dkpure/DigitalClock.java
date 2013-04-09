package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

/**
 * A custom clock widget
 * @author dkpure
 *
 */
public class DigitalClock extends View {
	final static String Weekdays[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
	final static int SPLITX = 5;
	
	private	Paint	i_text_paint;
	private	String	str_time = "15:52";
	private	String	str_weekday = "Mon";
	private	String	str_am = "PM";
	private Time	i_time = null;
	private Time	old_time = null;
	private	int		i_text_size;
	
	public DigitalClock(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		initDigitalClock();
	}
	
	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initDigitalClock();
	}
	
	/**
	 * Update the UI with current time if there is a necessity
	 * @return bool value of necessity to update the UI
	 */
	private boolean updateTime() {
		if (null == i_time)
			i_time = new Time();
		
		if (null == old_time)
			old_time = new Time();
		
		old_time.set(i_time);
		i_time.setToNow();
		// check whether we need to update or not
		if (old_time.minute != i_time.minute) {
			str_time = i_time.hour + ":";
			str_time += i_time.minute > 9 ? "" + i_time.minute : "0" + i_time.minute;
			str_weekday = Weekdays[i_time.weekDay];
			str_am	= (i_time.hour < 12) ? "AM" : "PM";
			return true;
		}
		
		return false;
	}
	
	/**
	 * Do some initialization work
	 */
	private void initDigitalClock() {
		updateTime();
		i_text_size = 70;
		i_text_paint = new Paint();
		i_text_paint.setAntiAlias(true);
		i_text_paint.setColor(Color.WHITE);
		
		final Handler tick_handler = new Handler();	
		Runnable tick_runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if (updateTime())
					invalidate(); // we are in UI thread	
				tick_handler.postDelayed(this, 1000); // loop it
			}			
		};
		
		tick_handler.postDelayed(tick_runnable, 1000); // trick the runnable after 1 second
	}
	
	private int getRectWidth() {
    	int ret = SPLITX;
    	
    	i_text_paint.setTextSize(i_text_size);
    	ret += i_text_paint.measureText("00:00");
    	i_text_paint.setTextSize(i_text_size / 2);
    	ret += i_text_paint.measureText("Mon");
    	
    	return ret;
    }
    
    private int getRectHeight() {
    	int ret = 0;
    	
    	i_text_paint.setTextSize(i_text_size);
    	ret = - ((int) i_text_paint.ascent()) + (int)i_text_paint.descent();
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
            result = getRectWidth();
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
            result = getRectHeight();
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
		
		i_text_paint.setTextSize(i_text_size);// text size for time
		int x = getPaddingLeft() + (int) i_text_paint.measureText(str_time) + SPLITX;
		int y = getPaddingTop();
		int yy = y - ((int) i_text_paint.ascent());
		canvas.drawText(str_time, getPaddingLeft(), yy, i_text_paint); // draw time
		
		i_text_paint.setTextSize(i_text_size / 2);// text size for weekday and am_pm
		canvas.drawText(str_weekday, x, y - ((int) i_text_paint.ascent()), i_text_paint);// draw weekday
		canvas.drawText(str_am, x, yy, i_text_paint);// draw am or pm
	}
}
