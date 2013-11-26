package cn.seu.dkpure;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * A custom map widget based on BaiduMap SDK
 * @author dkpure
 *
 */
public class DkMapWidget extends FrameLayout {
	// global context
	private Context 		mcontext = null;
	
	private MapView 		mMapView = null;
	private MapController 	mMapController = null;
	
	// location related
	private GeoPoint				cur_location_point = null;
	private LocationData 			locData = null;
	private LocationClient 			mLocClient = null;
	private MyLocationOverlay 		myLocationOverlay = null;
	
	public DkMapWidget(Context context) {
		super(context);
	}
	
	public DkMapWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Parse and Instantiate dk_mapview
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dk_mapview, this);
		
//		if (GlobalParams.RUN_720P) {
////			this.getLayoutParams().width = 500;
////			this.getLayoutParams().height = 360;
//			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(500, 360);
////			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(500, 360);
//			this.setLayoutParams(lp);
//			this.requestLayout();
//		}
		
		init(context);
	}
	
	/**
	 * Do some initialization work
	 * 1. initialize app.mBMapManager, mMapView and mMapController
	 * 2. initialize mLocClient and start
	 * 3. initialize myLocationOverlay and add to mMapView
	 * @param context
	 */
//	@SuppressWarnings("deprecation")
	private void init(Context context) {
		mcontext = context;// record the context for later usage
		
		// get a MapApplication instance from context
		MainApplication app = (MainApplication) ((Activity) context).getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(context);
            app.mBMapManager.init(MainApplication.strKey, new MainApplication.MyGeneralListener());
        }
        
        cur_location_point = new GeoPoint((int)(32.06*1e6), (int)(118.8*1e6));
        
        // initialize mapView and mapController
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.setTraffic(true);
//        mMapView.displayZoomControls(true);
//        mMapView.getController().setCenter(cur_location_point);
        
//		if (GlobalParams.RUN_720P) {
//			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(500, 360);
//			this.setLayoutParams(lp);
//			mMapView.setLayoutParams(lp);
//			this.requestLayout();
//		}
        
        mMapController = mMapView.getController();
        mMapController.setCenter(cur_location_point);
        mMapController.enableClick(true);
        mMapController.setZoom(15);//16
        mMapController.setOverlooking(30);
        
        // setup locationListener and start locationClient
        mLocClient = new LocationClient(context);
//        mLocClient.registerLocationListener(location_listener);
        mLocClient.registerLocationListener(new MyLocationListenner());
        
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开gps
        option.setCoorType("bd09ll"); //返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setProdName("ThumbNavi");
        option.disableCache(true);//禁止启用缓存定位
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        // add locationOverlay
        myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
	    myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
//		myLocationOverlay.enableCompass();
		mMapView.refresh();
	}
	
	/**
	 * called in super's onPause
	 */
	protected void onPause() {
		if (mMapView != null)
			mMapView.onPause();
    }

	/**
	 * called in super's onResume
	 */
    protected void onResume() {
    	if (mMapView != null)
			mMapView.onResume();
    }

	/**
	 * called in super's onDestroy
	 */
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        
        if (mMapView != null) {
			 mMapView.destroy();
			 mMapView = null;
        }
        
        MainApplication app = (MainApplication)((Activity)mcontext).getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
    }

	/**
	 * called in super's onSaveInstanceState
	 */
    protected void onSaveInstanceState(Bundle outState) {
    	if (mMapView != null)
			mMapView.onSaveInstanceState(outState);
    }

	/**
	 * called in super's onRestoreInstanceState
	 */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	if (mMapView != null)
			mMapView.onRestoreInstanceState(savedInstanceState);
    }
	
    /**
     * Implements BDLocationListener to refresh our location periodically
     * @author dkpure
     *
     */
    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || locData == null || cur_location_point == null ||
            		mMapView == null || mMapController == null || myLocationOverlay == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            myLocationOverlay.setData(locData);
//            Log.v("MyLocationListenner", "latitude: " + locData.latitude + ", longitude: " + locData.longitude);
            mMapView.refresh();
            
            cur_location_point.setLatitudeE6((int)(locData.latitude* 1e6));
            cur_location_point.setLongitudeE6((int)(locData.longitude *  1e6));
            mMapController.animateTo(cur_location_point);
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
            
            //...
        }
    }
}
