package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A custom widget to show notification message.
 * @author dkpure
 *
 */
public class NotifBar extends View {
	private int			x_text_margin = 0;
	private int			y_text_margin = 0;
	private String		m_text;
	private	Paint		text_paint;
	
	public NotifBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		initNotifBar();
	}
	
	public NotifBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initNotifBar();
	}
	
	/**
	 * Do some initialization work.
	 */
	private void initNotifBar() {
		m_text = "上午要去机场接Tim Cook";
		x_text_margin = 60;
		y_text_margin = 16;
		text_paint = new Paint();
		text_paint.setColor(Color.WHITE);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(35);
		
		this.setBackgroundColor(Color.argb(200, 46, 167, 244));
	}
	
	private int getTextWidth() {
		if (m_text != null)
			return (int) text_paint.measureText(m_text);
		else
			return 0;
	}
	
	private int getTextHeight() {
		if (m_text != null)
			return - ((int) text_paint.ascent()) + (int)text_paint.descent();
		else
			return 0;
	}
	
	private int getRectWidth() {
    	int ret = 10;
    	
    	if (m_text != null)
    		ret = (int) text_paint.measureText(m_text) + x_text_margin;

    	return ret;
    }
    
    private int getRectHeight() {
    	int ret = 10;
    	
    	if (m_text != null)
    		ret = - ((int) text_paint.ascent()) + (int)text_paint.descent() + y_text_margin;
    	
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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int x, y;
				
		x = (getWidth() - getTextWidth()) / 2;
		y = getTextHeight();		
		
		if (m_text != null)
			canvas.drawText(m_text, x, y, text_paint);
	}
}
