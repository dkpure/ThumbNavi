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
	
	private	Paint	time_paint = null;
	private	Paint	week_paint = null;
	private	String	str_time = "15:32";//null;
	private	String	str_weekday = "Mon";//null;
	private	String	str_am = "PM";//null;
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
		if (GlobalParams.RUN_720P)
			i_text_size = 140;
		else
			i_text_size = 70;
		time_paint = new Paint();
		time_paint.setAntiAlias(true);
		time_paint.setColor(Color.WHITE);
		time_paint.setTextSize(i_text_size);
		
		week_paint = new Paint();
		week_paint.setAntiAlias(true);
		week_paint.setColor(Color.WHITE);
		week_paint.setTextSize(i_text_size / 2);
		
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
    	
    	ret += time_paint.measureText("00:00");
    	ret += week_paint.measureText("Mon");
    	
    	return ret;
    }
    
    private int getRectHeight() {
    	int ret = 0;
    	
    	time_paint.setTextSize(i_text_size);
    	ret = - ((int) time_paint.ascent()) + (int)time_paint.descent();
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
		
		if (time_paint == null || week_paint == null || str_time == null)
			return;
		
		int x = (int) time_paint.measureText(str_time) + SPLITX;
		int y = getPaddingTop();
		int yy = y - ((int) time_paint.ascent());
//		int text_w = (int) week_paint.measureText(str_weekday) + x;
		int text_w = Math.max(	((int) week_paint.measureText(str_weekday)), 
								((int) week_paint.measureText("PM"))) 
								+ x;
		int x_offset = getWidth() - text_w;
		
		x += x_offset + getPaddingLeft();
		canvas.drawText(str_time, getPaddingLeft() + x_offset, yy, time_paint); // draw time
		canvas.drawText(str_weekday, x, y - ((int) week_paint.ascent()), week_paint); // draw weekday
		canvas.drawText(str_am, x, yy, week_paint); // draw am or pm
		
	}
}
