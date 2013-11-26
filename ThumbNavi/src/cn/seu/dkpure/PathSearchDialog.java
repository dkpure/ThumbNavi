package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A custom widget to location info.
 * @author dkpure
 *
 */
public class PathSearchDialog extends View {
//	private final static String TAG = "PathSearchDialog";
	private boolean	m_is_start_node = true;
	private boolean m_use_current_location = false;
	private String 	m_city = "南京";
	private String 	m_location_name = "";
	private int		m_text_size = 60;
	private Paint	m_text_paint = null;
	private Paint	m_splitline_paint = null;
	
	public PathSearchDialog(Context context) {
		super(context);
		
		init();
	}
	
	public PathSearchDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		init();
	}
	
	/**
	 * Do some initialization work.
	 */
	private void init() {
		m_text_size = GlobalParams.RUN_720P ? 60 : 40;
		m_text_paint = new Paint();
		m_text_paint.setAntiAlias(true);
		m_text_paint.setColor(Color.WHITE);
		
		m_splitline_paint = new Paint();
		m_splitline_paint.setAntiAlias(true);
		m_splitline_paint.setColor(Color.WHITE);
		m_splitline_paint.setStrokeWidth(GlobalParams.RUN_720P ? 4 : 2);
		
	}
	
	public boolean setAllParams(boolean m, String c, String n) {
		boolean ret = false;
		
		setDialogMode(m);
		ret = setCity(c) && setLocationName(n);
		this.requestLayout();
		this.invalidate();
		return ret;
	}
	
	public void setDialogMode(boolean is_start_node) {
		m_is_start_node = m_use_current_location = is_start_node;
	}
	
	public boolean setCity(String c) {
		if (c != null) {
			m_city = c;
			return true;
		}
		
		return false;
	}
	
	public boolean setLocationName(String la) {
		if (la != null) {
			m_location_name = la;
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return suitable width of layout
	 */
	private int getSuitableWidth() {
    	int ret = 10; // set minimal width to 10
    	
    	if (m_text_paint != null) {
    		m_text_paint.setTextSize(m_text_size);
    		if (m_location_name != "")
    			ret = (int)m_text_paint.measureText("南南南南南南南南");
    	}
    	
    	return ret;
    }
	
	/**
	 * 
	 * @return suitable height of layout
	 */
    private int getSuitableHeight() {
    	int ret; 
    	int h1 = 0;
    	
    	if (m_text_paint != null) {
    		m_text_paint.setTextSize(m_text_size);
    		int h = (int)(m_text_paint.descent() - m_text_paint.ascent());
    		int n_lines = m_is_start_node ? 4 : 3;
//    		if (m_location_name != "")
//    			n_lines += m_location_name.length() / 9;
    		
    		h1 = n_lines * h;
    	}
    	
    	ret = h1;
    	return ret == 0 ? 10 : ret; // set minimal height to 10
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
		
		int x = 0;
		int y = 0;
		
		if (m_city != "") {
			m_text_paint.setColor(Color.WHITE);
			m_text_paint.setTextSize(m_text_size - 10);
			y += -(int)m_text_paint.ascent();
			canvas.drawText(m_city, x, y, m_text_paint);
//			y += (int)m_text_paint.descent();
		}
		
		if (m_is_start_node) {
			m_text_paint.setColor(Color.rgb(185, 215, 20));
			int w = (int)m_text_paint.measureText("Start");
			canvas.drawText("Start", this.getWidth() - w, y, m_text_paint);
		} else {
			m_text_paint.setColor(Color.rgb(240, 143, 28));
			int w = (int)m_text_paint.measureText("Stop");
			canvas.drawText("Stop", this.getWidth() - w, y, m_text_paint);
		}
		
		y += (int)m_text_paint.descent();
		y +=  4;
		canvas.drawLine(x, y, this.getWidth(), y , m_splitline_paint);
		
		if (m_location_name != "") {
			m_text_paint.setColor(Color.WHITE);
			m_text_paint.setTextSize(m_text_size);
			y += 20 -(int)m_text_paint.ascent();
//			x = (this.getWidth() - (int)m_text_paint.measureText(m_location_name)) / 2;
			x = 0;
			canvas.drawText(m_location_name, x, y, m_text_paint);
			y += (int)m_text_paint.descent();
		}
		
		if (m_use_current_location) {
			m_text_paint.setColor(Color.rgb(51, 153, 255));
			m_text_paint.setTextSize(m_text_size - 10);
			y += 6 -(int)m_text_paint.ascent();
			x = (this.getWidth() - (int)m_text_paint.measureText("(当前位置)")) / 2;
			canvas.drawText("(当前位置)", x, y, m_text_paint);
		}		
	}
	
}
