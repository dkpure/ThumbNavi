package cn.seu.dkpure;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.mapapi.utils.DistanceUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RouteNaviWidget extends View {
	protected static final String TAG = "RouteNaviWidget";
//	public enum AUTO_NAVI_SPEED_LEVEL {LOW, NORMAL, FAST};
	public final int AUTO_NAVI_SPEED_LOW = 0;
	public final int AUTO_NAVI_SPEED_NORMAL = 1;
	public final int AUTO_NAVI_SPEED_FAST = 2;
//	private final static int MARGIN = 20;
	private final static float YX_RATIO = 0.8541606f;
	private final static double DEFAULT_PATH_STEP = 0.00005d;
	private AtomicBoolean drawing = new AtomicBoolean(false);
	private boolean	m_route_ok = false;
	private Float[] mlat_weight = null;
	private Float[] mlon_weight = null;
	private Float[] m_step_lat_weight = null;
	private Float[] m_step_lon_weight = null;
	private Point[] m_step_pts = null;
	private GeoPoint[] m_step_geopts = null;
	private int		m_lon_span = 0, m_lat_span = 0;
	private int		m_lon_min = 0, m_lat_min = 0;
	
//	private PaintFlagsDrawFilter mPfd = null;
//	private Bitmap	m_start_bitmap = null;
//	private Bitmap	m_end_bitmap = null;
//	private Bitmap	m_nod_bitmap = null;
	private Paint 	m_line_paint;
	private Path	m_driving_path;
	private float 	wh_ratio = 1f;
	private int		m_drawing_area_w, m_drawing_area_h;
	
	//indicator related
	private Path	m_indicator;
	private Paint	m_indicator_paint;
	private int 	m_offset_x = 0, m_offset_y = 0;
	private double 	m_current_step = 0f;
	private double	m_path_step = DEFAULT_PATH_STEP;
	private int 	m_angle = 0;
	
	private Handler 	m_tick_handler;
	private Runnable 	m_tick_runnable = null;
	private boolean		m_stop_flag = false;
	private int			m_locate_cnt = 0;
	private PathMeasure m_path_measure = null;
	private GeoPoint	m_prev_geopt = null;
	private GeoPoint	m_cur_geopt = null;
	private	double		m_cur_step_distance = 0;
	
	public RouteNaviWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		init();
	}
	
	public RouteNaviWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	private final static float ACCEPTABLE_ERROR = 0.00001f;
	final int compareFloat2Zero(float f) {
		if (Math.abs(f) > ACCEPTABLE_ERROR) {
			if (f > ACCEPTABLE_ERROR)
				return 1;
			else
				return -1;
		}
		
		return 0;
	}
	
	private double clacAngle(float deltaX, float deltaY) {
		double radian = .0f;
		int zeroX = compareFloat2Zero(deltaX);
		int zeroY = compareFloat2Zero(deltaY);
		
		if (0 == zeroX) {
			if (zeroY < 0)
				radian = Math.PI * 0.500000f;
			else
				radian = Math.PI * 1.500000f;
		} else {
			radian = Math.atan(Math.abs(deltaY) / Math.abs(deltaX));
			
			if (zeroY > 0) {
				if (zeroX < 0)
					radian += Math.PI;
				else
					radian = 2.000000f * Math.PI - radian;
			} else {
				if (zeroX < 0)
					radian = Math.PI - radian;
			}
		}
		
		return Math.toDegrees(radian);
	}
	
	private void init() {
		m_line_paint = new Paint();
		m_line_paint.setColor(Color.argb(200, 58, 163, 244));
		m_line_paint.setAntiAlias(true);
		m_line_paint.setStyle(Paint.Style.STROKE);
		m_line_paint.setStrokeWidth(8);//8
//		m_line_paint.setStrokeCap(Cap.ROUND); //round cap
		m_line_paint.setStrokeJoin(Paint.Join.ROUND); //round join
		m_line_paint.setPathEffect(new CornerPathEffect(50));
		
//		mPfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);			 
		m_driving_path = new Path();
		
//		m_start_bitmap 	= BitmapFactory.decodeResource(this.getResources(), R.drawable.step_start);
//		m_end_bitmap 	= BitmapFactory.decodeResource(this.getResources(), R.drawable.step_end);
//		m_nod_bitmap 	= BitmapFactory.decodeResource(this.getResources(), R.drawable.step_nod);
		
//		mZRotationMatrix = new Matrix();
//		Camera tmpCam = new Camera();
//		tmpCam.rotateX(45);			
//		tmpCam.getMatrix(mZRotationMatrix);
		
		m_indicator = new Path();
		float scale_factor = 8f;
		m_indicator.moveTo(3.732051f * scale_factor, 0);
		m_indicator.lineTo(-3.732051f * scale_factor, -2 * scale_factor);
		m_indicator.lineTo(-2.2f * scale_factor, 0);
		m_indicator.lineTo(-3.732051f * scale_factor, 2 * scale_factor);
		m_indicator.close();
		
		m_indicator_paint = new Paint();
		m_indicator_paint.setStyle(Paint.Style.FILL_AND_STROKE);
		m_indicator_paint.setAntiAlias(true);
		m_indicator_paint.setColor(Color.argb(200, 0, 255, 50));
		
		m_tick_handler = new Handler();	
		m_tick_runnable = new Runnable() {
			@Override
			public void run() {
				if (m_route_ok) {
				    float cur_pos[] = {0f, 0f};
				    float next_pos[] = {0f, 0f};
				    double next_step = m_current_step + 2 * m_path_step;
				    
				    m_path_measure.getPosTan(m_path_measure.getLength() * (float)m_current_step, cur_pos, null);
				    //calculate the rotate angle of indicator
			    	if (next_step < 1.00001f) {
			    		m_path_measure.getPosTan(m_path_measure.getLength() * (float)next_step, next_pos, null);
			    		int tmp_angle = (int) clacAngle(next_pos[0] - cur_pos[0], 
			    				cur_pos[1] - next_pos[1]);
			    		if (Math.abs(tmp_angle - m_angle) > 2) {
							m_angle = tmp_angle;
						}
			    	}
			    	
			    	if (m_locate_cnt++ > 19) {
			    		refreshLocation(cur_pos[0], cur_pos[1]);
			    		m_locate_cnt = 0;
			    	}

				    m_current_step += m_path_step;
				    m_offset_x = -(int)cur_pos[0];
				    m_offset_y = -(int)cur_pos[1];
				    invalidate();
				}
			    
				if (m_current_step < 1.000001d && !m_stop_flag)
					m_tick_handler.postDelayed(this, 50); // loop it
//				else {
//					Log.v(TAG, "m_current_step = " + m_current_step);
//				}
			}
		};
	}
	
	private void refreshLocation(float px, float py) {
    	Point2Geo(px, py, m_cur_geopt);
    	double distance = DistanceUtil.getDistance(m_cur_geopt, m_prev_geopt);
    	m_cur_step_distance += distance;
    	m_prev_geopt.setLatitudeE6(m_cur_geopt.getLatitudeE6());
    	m_prev_geopt.setLongitudeE6(m_cur_geopt.getLongitudeE6());
    	Log.v(TAG, "已经行驶 " + (int)m_cur_step_distance + " 米");
	}
	
	public void startNavigation() {
		if (m_route_ok) {
			m_tick_handler.postDelayed(m_tick_runnable, 100); // trick the runnable
		}
	}
	
	public void stopAutoNavi() {
		m_stop_flag = true;
		if (m_tick_handler != null) 
			m_tick_handler.removeCallbacks(m_tick_runnable);
	}
	
	public void setAutoNaviSpeedLevel(int level) {
		switch (level) {
		case AUTO_NAVI_SPEED_LOW:
			m_path_step = 0.0001d;
			break;
		case AUTO_NAVI_SPEED_NORMAL:
			m_path_step = 0.001d;
			break;
		case AUTO_NAVI_SPEED_FAST:
			m_path_step = 0.01d;
			break;
		}
	}
	
	public void setRouteWidth(int w) {
		if (m_line_paint != null) {
			m_line_paint.setStrokeWidth(w);
		}
	}
	
	private final void weight2Point(float lon_weight, float lat_weight, Point dst) {
		if (dst == null)
			dst = new Point();
		
		int x = (int)(lon_weight * m_drawing_area_w);
		int y = m_drawing_area_h - (int)(lat_weight * m_drawing_area_h);
		dst.set(x, y);
	}
	
	public void Point2Geo(float px, float py, GeoPoint geo) {
		int lat_e6, lon_e6;
		
		lon_e6 = (int) ((px * m_lon_span) / m_drawing_area_w + m_lon_min);
		lat_e6 = (int) ((m_drawing_area_h - py) / m_drawing_area_h * m_lat_span + m_lat_min);
		
		if (geo == null)
			geo = new GeoPoint(lat_e6, lon_e6);
		else {
			geo.setLatitudeE6(lat_e6);
			geo.setLongitudeE6(lon_e6);
		}		
	}
	
	/**
	 * Call after setRoute and in the very beginning of mapRoute2Path
	 * @param w Width of map to hold driving path
	 */
	private final void setMapWidth(int w) {
		m_drawing_area_w = w;
		m_drawing_area_h = (int)(w * wh_ratio);
	}
	
	private void mapRoute2Path(int w) {
		if (mlat_weight == null || mlon_weight == null || m_driving_path == null ||
				mlat_weight.length != mlon_weight.length) {
			Log.v("RouteShowWidget", "mapRoute2Path error!");
			return;
		}

		setMapWidth(w);
		
		Point tmp = new Point();
		weight2Point(mlon_weight[0], mlat_weight[0], tmp);
		m_driving_path.moveTo(tmp.x,  tmp.y);
		
		for (int i = 1; i < mlon_weight.length; ++i) {
			weight2Point(mlon_weight[i], mlat_weight[i], tmp);
			m_driving_path.lineTo(tmp.x,  tmp.y);
		}
		
		if (m_step_lon_weight == null || m_step_lat_weight == null || m_step_pts == null ||
				m_step_lon_weight.length != m_step_lat_weight.length) {
			Log.v("RouteShowWidget", "mapRoute2Path error!");
			return;
		}
		
		for (int i = 0; i < m_step_lon_weight.length; ++i) {
			m_step_pts[i] = new Point();
			weight2Point(m_step_lon_weight[i], m_step_lat_weight[i], m_step_pts[i]);	
		}
		
		m_path_measure = new PathMeasure(m_driving_path, false);
		m_route_ok = true;
	}
	
	private void resetVars() {
		m_angle = 0;
		m_current_step = 0;
		m_cur_step_distance = 0;
		m_route_ok = false;
		m_stop_flag = false;
		m_driving_path.reset();
	}
	
	public void setRoute(MKRoute route) {
		if (route == null) return;

		resetVars();
		
		ArrayList<GeoPoint> poi;
    	ArrayList<ArrayList<GeoPoint>> geo_arr = route.getArrayPoints();
    	int pts_num = 0;
    	
    	//get the number of all geo points
    	for (int i = 0; i < geo_arr.size(); ++i) {
    		pts_num += geo_arr.get(i).size();
    	}
    	
    	GeoPoint[] linePoints = new GeoPoint[pts_num];
    	int pt_index = 0;
    	
    	for (int i = 0; i < geo_arr.size(); ++i) {
    		poi = geo_arr.get(i);
    		for (int j = 0; j < poi.size(); ++j) {
    			linePoints[pt_index++] = poi.get(j);
    		}
    	}
    	
    	int min_lat, max_lat;
    	int min_lon, max_lon;
    	int tmp_lat, tmp_lon;
    	
    	
    	min_lat = max_lat = linePoints[0].getLatitudeE6();
    	min_lon = max_lon = linePoints[0].getLongitudeE6();
    	m_prev_geopt = new GeoPoint(min_lat, min_lon);
    	m_cur_geopt = new GeoPoint(min_lat, min_lon);//just allocate a GeoPoint
    	
    	for (int i = 1; i < linePoints.length; ++i) {
    		tmp_lat = linePoints[i].getLatitudeE6();
    		tmp_lon = linePoints[i].getLongitudeE6();
//    		Log.v("getCoordWeight", "tmp_lat = " + tmp_lat + ", tmp_lon = " + tmp_lon);
    		
    		if (tmp_lat < min_lat)
    			min_lat = tmp_lat;
    		
    		if (tmp_lat > max_lat)
    			max_lat = tmp_lat;
    		
    		if (tmp_lon < min_lon)
    			min_lon = tmp_lon;
    		
    		if (tmp_lon > max_lon)
    			max_lon = tmp_lon;
    	}
    	
    	m_lon_min = min_lon;
    	m_lat_min = min_lat;
    	m_lat_span = max_lat - min_lat;
    	m_lon_span = max_lon - min_lon;
    	wh_ratio = (float) m_lat_span / (float) m_lon_span;
    	wh_ratio *= YX_RATIO;
//    	Log.v("setRoute", "wh_ratio = " + wh_ratio);
//    	wh_ratio = 1.67f;

    	mlat_weight = new Float[linePoints.length];
    	mlon_weight = new Float[linePoints.length];
    	
    	for (int i = 0; i < linePoints.length; ++i) {
    		tmp_lat = linePoints[i].getLatitudeE6();
    		tmp_lon = linePoints[i].getLongitudeE6();
    		
    		mlat_weight[i] = (float)(tmp_lat - min_lat) / (float)m_lat_span;
    		mlon_weight[i] = (float)(tmp_lon - min_lon) / (float)m_lon_span;
    	}
    	
    	//generate the m_step_lat_weight, m_step_lon_weight array
    	m_step_lat_weight = new Float[route.getNumSteps()];
    	m_step_lon_weight = new Float[route.getNumSteps()];
    	m_step_pts = new Point[route.getNumSteps()];
    	m_step_geopts = new GeoPoint[route.getNumSteps()];
    	
    	GeoPoint tmp_geo;
    	for (int i = 0; i < route.getNumSteps(); ++i) {
    		tmp_geo = route.getStep(i).getPoint();
    		tmp_lat = tmp_geo.getLatitudeE6();
    		tmp_lon = tmp_geo.getLongitudeE6();
    		m_step_geopts[i] = tmp_geo;
    		Log.v(TAG, "distance = " + RouteParser.getPureDistance(route.getStep(i).getContent()));
    		
    		m_step_lat_weight[i] = (float)(tmp_lat - min_lat) / (float)m_lat_span;
    		m_step_lon_weight[i] = (float)(tmp_lon - min_lon) / (float)m_lon_span;
    	}
    	
    	Log.v("RouteShowWidget", "setRoute ok!");
    	setRouteWidth(50);
//    	setAutoNaviSpeedLevel(AUTO_NAVI_SPEED_LOW);
    	ZoomToLevel(15f);//10
	}
	
	public final void ZoomToLevel(float zl) {
		if (zl > 0f && zl < 50f) {
			int targetW = (int) (zl * this.getWidth());
			mapRoute2Path(targetW);
		}
	}
	
	private int m_window_width_div2;
	private int m_window_height_div2;
	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		m_window_width_div2 = w / 2;
		m_window_height_div2 = h / 2;
//		Log.v(TAG, "onSizeChanged");
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas == null) throw new NullPointerException();

        if (!drawing.compareAndSet(false, true)) return;
		
//		canvas.setDrawFilter(mPfd);
//        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);
        canvas.translate(m_window_width_div2, m_window_height_div2);
        m_driving_path.offset(m_offset_x, m_offset_y);
//		canvas.setMatrix(mZRotationMatrix);
		canvas.drawPath(m_driving_path, m_line_paint);
		m_driving_path.offset(-m_offset_x, -m_offset_y);
		
		canvas.rotate(m_angle);
		canvas.drawPath(m_indicator, m_indicator_paint);
				
		drawing.set(false);
	}
}