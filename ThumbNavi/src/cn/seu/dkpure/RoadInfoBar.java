package cn.seu.dkpure;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
//import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * A custom widget to show navigation message
 * @author dkpure
 *
 */
public class RoadInfoBar extends View{
	private int	n_expand_strip = 3;
	private List<String> info_list;
	private List<Bitmap> dir_bitmap_list;
	private	String	text_cur_info;
	private	String	text_cur_distance;
	
	private enum DISPLAY_MODE {SINGLE_STRIP, MULTI_STRIP, IN_ANIMATION};
	private DISPLAY_MODE disp_mode = DISPLAY_MODE.SINGLE_STRIP;
	
	// graphics related
	private Paint 	large_text_paint;
	private Paint 	medium_text_paint;
	private Paint	i_rect_paint;
	private Paint	sep_line_paint;
	private	Paint	progress_paint;
//	private	Bitmap	img_shadow = null;
	
	private int		screen_width = 854;
	private int 	default_barW = screen_width * 9 / 20;
	private	int		text_size_large = 45;
	private	int		text_size_medium = 35;
	private	int		sign_image_width = 50;
	private int 	x_margin = SPLIT_X1 + SPLIT_X2 + 4;
	private int		y_margin = 8;
	final static int	SPLIT_X1 = 5; 	// reserved width between road info and direction sign
	final static int	SPLIT_X2 = 20; 	// reserved width between direction sign and distance string
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (disp_mode == DISPLAY_MODE.IN_ANIMATION)
			disp_mode = DISPLAY_MODE.SINGLE_STRIP;
		else
			disp_mode = DISPLAY_MODE.IN_ANIMATION;
		
		updateCurrentDistance("200m");
		
		return true;
	}
	
	public RoadInfoBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		initRoadInfoBar(context);
	}
	
	public RoadInfoBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initRoadInfoBar(context);
	}
	
	/**
	 * Return the width of screen
	 * @param activity context
	 * @return screen's width
	 */
	private int getScreenWidth(Context activity_context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) activity_context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	/**
	 * Do some initialization work
	 * @param context
	 */
	private final void initRoadInfoBar(Context context) {
		text_cur_info = null;
		text_cur_distance = null;
		
		screen_width = getScreenWidth(context);
		default_barW = screen_width * 9 / 20;
		
		info_list = new ArrayList<String>();
		dir_bitmap_list = new ArrayList<Bitmap>();

		large_text_paint = new Paint();
		large_text_paint.setAntiAlias(true);
		large_text_paint.setTextSize(text_size_large);
		large_text_paint.setColor(Color.WHITE);
		
		medium_text_paint = new Paint();
		medium_text_paint.setAntiAlias(true);
		medium_text_paint.setTextSize(text_size_medium);
//		medium_text_paint.setColor(Color.BLACK);
		medium_text_paint.setColor(Color.WHITE);
		
		i_rect_paint = new Paint();
		i_rect_paint.setAntiAlias(true);
		i_rect_paint.setColor(Color.argb(150, 46, 167, 244));
		
		sep_line_paint = new Paint();
		sep_line_paint.setColor(Color.argb(200, 0, 0, 0));
		sep_line_paint.setStrokeWidth(1);
		
		progress_paint = new Paint();
		Shader shader = new LinearGradient(0, 0, 200, 0, 
							Color.argb(50, 195, 232, 22), 
							Color.argb(255, 195, 232, 22), 
							Shader.TileMode.CLAMP);
		progress_paint.setShader(shader);
		progress_paint.setAlpha(150);
		
//		img_shadow = BitmapFactory.decodeResource(getResources(), R.drawable.shadow0);
	}
	
	/**
	 * Get pure road info from mixed info.
	 * mixed_info is in format like "XXX - YYY", we get XXX and return
	 * @param mixed_info in format: "XXX - YYY", like "Dragon Road - 300m"
	 * @return pure road info
	 */
	private String getRoadInfo(String mixed_info) {
		String[] tmp = mixed_info.split("-");
		
		return tmp[0].trim();
	}
	
	/**
	 * Get distance info from mixed info.
	 * mixed_info is in format like "XXX - YYY", we get YYY and return
	 * @param mixed_info, in format: "XXX - YYY", like "Dragon Road - 300m"
	 * @return distance info (use ?? for invalid)
	 */
	private String getDistanceInfo(String mixed_info) {
		String[] tmp = mixed_info.split("-");
		
		if (tmp.length > 1)
			return tmp[1].trim();
		else
			return "??";
	}
	
	/**
	 * Update the interface with new distance info
	 * @param dist distance info in String
	 */
	public void updateCurrentDistance(String dist) {
		text_cur_distance = dist;
		requestLayout();// invoke OnMeasure()
		invalidate();	// invoke OnDraw()
	}
	
	/**
	 * Decode a bitmap from resource
	 * @param direction_index varies from RouteDirectionConstants
	 * @return bitmap of navigation direction
	 */
	private Bitmap getBitmapFromIndex(int direction_index) {
		int resId = R.drawable.dir_unknown;
		
		switch (direction_index) {
		case RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT:
			resId = R.drawable.dir_go_straight;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_LEFT0:
			resId = R.drawable.dir_turn_left0;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_LEFT1:
			resId = R.drawable.dir_turn_left1;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_LEFT2:
			resId = R.drawable.dir_turn_left2;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_LEFT3:
			resId = R.drawable.dir_turn_back;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT0:
			resId = R.drawable.dir_turn_right0;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT1:
			resId = R.drawable.dir_turn_right1;
			break;
		case RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT2:
			resId = R.drawable.dir_turn_right2;
			break;			
		default:
			;
		}
		
		return BitmapFactory.decodeResource(getResources(), resId);
	}
	
	/**
	 * Add a new node to the tail of current node list
	 * @param _index navigation direction index, see RouteDirectionConstants
	 * @param _str navigation message
	 */
	public void addNode(int _index, String _str) {
		if (info_list != null)
			info_list.add(_str);
		
		if (dir_bitmap_list != null && dir_bitmap_list.size() < n_expand_strip)
			dir_bitmap_list.add(getBitmapFromIndex(_index));
		
		if (null == text_cur_info)
			text_cur_info = getRoadInfo(info_list.get(0));
		
		if (null == text_cur_distance)
			text_cur_distance = getDistanceInfo(info_list.get(0));
	}
	
	/**
	 * Remove a node from current node list
	 * @param node_index index of the node to be remove
	 */
	public void removeNode(int node_index) {
		if (info_list != null && info_list.size() > node_index)
			info_list.remove(node_index);
		
		if (dir_bitmap_list != null)
			dir_bitmap_list.remove(node_index);
		
		if (0 == node_index) {
			text_cur_info = getRoadInfo(info_list.get(0));
			text_cur_distance = getDistanceInfo(info_list.get(0));
		}
	}
	
	/**
	 * Remove the first node of current node list
	 */
	public void removeFirst() {
		removeNode(0);
	}
    
	/**
	 * Get suitable width of layout.
	 * 1. In SINGLE_STRIP mode, just measure the first navigation message
	 * 2. In MULTI_STRIP mode, calculate the maximal width
	 * @return suitable width of layout
	 */
    private int getSuitableWidth() {
    	int ret = 10; // set minimal width 10
    	
    	if (info_list != null && large_text_paint != null) {
    		if (DISPLAY_MODE.SINGLE_STRIP == disp_mode) {
    			ret = sign_image_width + (int) large_text_paint.measureText(text_cur_info) +
		    			(int) large_text_paint.measureText(text_cur_distance) + x_margin +
		    			getPaddingLeft() + getPaddingRight();
    		} else {
    			int max_len = info_list.get(0).length();
    			int ii = 0;
    			for (int i = 1; i < n_expand_strip && i < info_list.size(); ++i) {
    				if (info_list.get(i).length() > max_len) {
    					max_len = info_list.get(i).length();
    					ii = i;
    				}
    			}
    			
    			String mixed_info = info_list.get(ii);
    			String tmp_road = getRoadInfo(mixed_info);
    			String tmp_distance = getDistanceInfo(mixed_info);
    			ret = sign_image_width + (int) large_text_paint.measureText(tmp_road) +
		    			(int) large_text_paint.measureText(tmp_distance) + x_margin +
		    			getPaddingLeft() + getPaddingRight();
    		}
    		
	    	if (ret < default_barW)
	    		ret = default_barW;
    	}
    	
    	return ret;
    }
	
    /**
     * Get suitable height of layout.
     * @return suitable height of layout
     */
    private int getSuitableHeight() {
    	int ret = 10; // set minimal height 10
    	
    	if (info_list != null && large_text_paint != null) {
	    	ret = getSingleStripHeight();
	    	
	    	if (DISPLAY_MODE.MULTI_STRIP ==  disp_mode || 
	    		DISPLAY_MODE.IN_ANIMATION ==  disp_mode )
	    		ret *= Math.min(n_expand_strip, info_list.size());	    	
    	}
    	
    	return ret;
    }
    
    /**
     * Calculate single strip height.
     * h = total text height + vertical margin
     * @return single strip height
     */
    private int getSingleStripHeight() {
    	return (int) (-(int) large_text_paint.ascent() + large_text_paint.descent()) + 
    			y_margin + getPaddingTop() + getPaddingBottom();
    }
    
	/**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
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

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
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
		
		if (info_list == null || info_list.isEmpty())
			return;
		
		int H = getSingleStripHeight();
		int x = sign_image_width;
		int y = getCenterAlignBaseline(H, large_text_paint);
		
//		canvas.drawRect(0, 0, getWidth(), getHeight(), i_rect_paint);// draw background
//		canvas.drawRect(0, 0, getWidth(), H, i_rect_paint);// draw the first strip
		canvas.drawRect(0, 0, getWidth() - 200, H, progress_paint);// draw progress 
		
		// draw road direction indicator
		if (dir_bitmap_list != null) {
			int y_delta = (H - dir_bitmap_list.get(0).getHeight()) / 2;
			canvas.drawBitmap(dir_bitmap_list.get(0), 0, y_delta, null);
		}
		
		// draw road info text
		canvas.drawText(text_cur_info, x, y, large_text_paint);
		
		// draw distance text align to the right
		x = getWidth() - getTextWidth(text_cur_distance, large_text_paint);
		canvas.drawText(text_cur_distance, x, y, large_text_paint);
		
		if (DISPLAY_MODE.MULTI_STRIP == disp_mode ||
				DISPLAY_MODE.IN_ANIMATION == disp_mode) {
			int tmp_baseline2 = getCenterAlignBaseline(H, medium_text_paint);
			
			// draw shadow
//			if (img_shadow != null) {
//				canvas.drawBitmap(
//						img_shadow, 
//						new Rect(0, 0, 1, img_shadow.getHeight()), 
//						new Rect(0, H, getWidth(), H + img_shadow.getHeight()), 
//						null);
//			}
			
			for (int i = 1; i < n_expand_strip && i < info_list.size(); ++i) {
				x = sign_image_width;
				y = H * i + tmp_baseline2;
				String mixed_info = info_list.get(i);
				String tmp_road = getRoadInfo(mixed_info);
    			String tmp_distance = getDistanceInfo(mixed_info);
    			
    			// draw separation line
//    			canvas.drawLine(0, (i + 1) * H, getWidth(), (i + 1) * H + 1, sep_line_paint);
    			
    			if (dir_bitmap_list != null && i < dir_bitmap_list.size()) {
    				int y_delta = (H - dir_bitmap_list.get(i).getHeight()) / 2;
    				// draw direction bitmaps
					canvas.drawBitmap(dir_bitmap_list.get(i), 0, H * i + y_delta, null);
    			}
    			
				canvas.drawText(tmp_road, x, y, medium_text_paint);// draw road info
				x = getWidth() - getTextWidth(tmp_distance, medium_text_paint);
				canvas.drawText(tmp_distance, x, y, medium_text_paint);// draw distance info
			}
		}
		
	}

	/**
	 * Return width of the String to be drawn
	 * @param txt String to be measured
	 * @param p paint to draw the text
	 * @return width(in pixels) of the String to be drawn
	 */
    private int getTextWidth(String txt, Paint p) {
    	if (txt != null && p != null)
    		return (int) p.measureText(txt);
    	
    	return 0;
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
		
//		return text_ascent + (H - text_ascent - text_descent) / 2;
		return (H + text_ascent - text_descent) / 2;
	}
}
