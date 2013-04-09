package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DashboardItem extends View {
	final static int SIZE_SPAN = 108;
	final static int BORDER_WIDTH = 8;
	final static int CIRCLE_DADIUS = (SIZE_SPAN - BORDER_WIDTH * 2) / 2;

	private Paint bg_paint;
	
	public DashboardItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		initDashboardItem();
	}
	
	public DashboardItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initDashboardItem();
	}
	
	private void initDashboardItem() {
		bg_paint = new Paint();
		bg_paint.setAntiAlias(true);
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
            result = SIZE_SPAN;
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
            result = SIZE_SPAN;
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
		
		canvas.translate(getWidth() / 2, getHeight() / 2);
		
		bg_paint.setColor(Color.argb(200, 255, 255, 255));
		bg_paint.setStyle(Style.STROKE);
		bg_paint.setStrokeWidth(BORDER_WIDTH);
		canvas.drawCircle(0, 0, CIRCLE_DADIUS, bg_paint);
		
		bg_paint.setColor(Color.argb(80, 40, 110, 255));
		bg_paint.setStyle(Style.FILL);
		bg_paint.setStrokeWidth(0);
		canvas.drawCircle(0, 0, CIRCLE_DADIUS-3, bg_paint);

		Log.v("DashboardItem", "w = " + getWidth() + ", h = " + getHeight());
	}
}
