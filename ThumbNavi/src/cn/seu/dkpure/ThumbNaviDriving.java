package cn.seu.dkpure;

import cn.seu.dkpure.RouteNaviWidget.UpdateNaviInfoListener;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Driving activity, launched during driving mode
 * @author dkpure
 *
 */
public class ThumbNaviDriving extends Activity {
	private final String TAG = "ThumbNaviDriving";
	private final String CITY = "南京";
	private RouteNaviWidget 	wg_routenavi = null;
	private SingleNodeBar		wg_singlenodebar = null;
//	private ObdInfoOnDriving	wg_obdinfo = null;
	private GearInfo 			wg_gearinfo = null;
	private RpmInfo				wg_rpminfo = null;
	private	SpeedInfo			wg_speedinfo = null;
	private ArrivalHintBar  	wg_arrivalhintbar = null;
	private DkNotification		wg_dknotification = null;
	
	private MKSearch 			mSearch = null;
	private String				m_start_city = "";
	private String				m_start_location = "";
	private	String				m_stop_city = "";
	private String				m_stop_location = "";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// set no title & fullscreen before setContentView
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
			        					WindowManager.LayoutParams.FLAG_FULLSCREEN);
     		
        setContentView(R.layout.layout_driving);
        
        MainApplication app = (MainApplication) getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(MainApplication.strKey, new MainApplication.MyGeneralListener());
        }
        
        Bundle extra_msg = this.getIntent().getExtras();
        if (!extra_msg.isEmpty()) {
        	m_start_city = extra_msg.getString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STARTCITY);
        	m_start_location = extra_msg.getString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STARTLOCATION);
        	m_stop_city = extra_msg.getString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STOPCITY);
        	m_stop_location = extra_msg.getString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STOPLOCATION);
        	DkDebuger.v(TAG, "start city: " + m_start_city + ", start location: " + m_start_location);
        	DkDebuger.v(TAG, "stop city: " + m_stop_city + ", stop location: " + m_stop_location);
        }
        
        emergeViews();
        
     // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
//					Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "抱歉，未找到结果!error code is: " + error);
					return;
				}
				
				if (wg_routenavi != null) {
					Log.v(TAG, "Got " + res.getNumPlan() + " plans!");
					if (wg_arrivalhintbar != null)
						wg_arrivalhintbar.setTotalDistance(res.getPlan(0).getRoute(0).getDistance());
					wg_routenavi.setRoute(res.getPlan(0).getRoute(0));
					wg_routenavi.startNavigation(RouteNaviWidget.AUTO_NAVI_SPEED_LOW);
				}
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
//					Toast.makeText(PureRouteDisplay.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "抱歉，未找到结果!");
					return;
				}
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
//					Toast.makeText(PureRouteDisplay.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "抱歉，未找到结果!");
					return;
				}			    
			}
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
 			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

        });
        
        doSearch();
	}
	
	/**
	 * search route according to the given address. Try another address once it thrown
	 * a error
	 */
	private void doSearch() {
		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		MKPlanNode stNode = new MKPlanNode();
		MKPlanNode enNode = new MKPlanNode();
		
		stNode.name = (m_start_location != "") ? m_start_location: "总统府";
		enNode.name = (m_stop_location != "") ? m_stop_location: "栖霞山";
		//"大行宫";//"安德门";//"河定桥";//"珍珠泉";
		//"莫愁湖";//"淳化";//"鼓楼区";//"夫子庙";//"草场门";
		
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);//MKSearch.ECAR_DIS_FIRST
		
		if (m_start_city != "" && m_stop_city != "")
			mSearch.drivingSearch(m_start_city, stNode, m_stop_city, enNode);
		else
			mSearch.drivingSearch(CITY, stNode, CITY, enNode);
	}
	
	private void emergeViews() {
		wg_singlenodebar = (SingleNodeBar) findViewById(R.id.driving_SingleNodeBar);
    	wg_gearinfo = (GearInfo) findViewById(R.id.driving_GearInfo);
    	wg_rpminfo = (RpmInfo) findViewById(R.id.driving_RpmInfo);
    	wg_speedinfo = (SpeedInfo) findViewById(R.id.driving_SpeedInfo);
		wg_routenavi = (RouteNaviWidget) findViewById(R.id.driving_RouteNaviWidget);
		wg_arrivalhintbar = (ArrivalHintBar) findViewById(R.id.driving_ArrivalHintBar);
		wg_dknotification = (DkNotification) findViewById(R.id.driving_Notification);
    	
		if (wg_dknotification != null) {
			wg_dknotification.bringToFront();
		}
		
		if (wg_arrivalhintbar != null) {
			wg_arrivalhintbar.setVisibility(View.INVISIBLE);
			wg_arrivalhintbar.bringToFront();
		}
    	
    	if (wg_routenavi != null) {
    		wg_routenavi.setEventListener(new UpdateNaviInfoListener() {

				@Override
				public void onEventAccured(int id, String ex_arg) {
					// TODO Auto-generated method stub
					switch(id) {
					case RouteNaviWidget.NAVI_START:
						Log.e(TAG, "start navigation!");
						m_vObd_engine.start();
						break;
					case RouteNaviWidget.SWITCH_NODE:
						Log.e(TAG, "switch node!");
						wg_singlenodebar.switchNode(ex_arg);
						break;
					case RouteNaviWidget.PRE_SWITCH_NODE:
						Log.e(TAG, "pre switch node!");
						wg_singlenodebar.preSwitchNode(ex_arg);
						break;
					case RouteNaviWidget.UPDATE_STEP_DISTANCE:
						wg_singlenodebar.updateDistance(ex_arg);
						break;
					case RouteNaviWidget.UPDATE_DISTANCE:
						wg_arrivalhintbar.updateCurrentDistance(Integer.parseInt(ex_arg));
						break;
					case RouteNaviWidget.ARRIVE_DESTINATION:
						m_vObd_engine.stop();
						DkDebuger.v(TAG, "arrived!");
						break;
					case RouteNaviWidget.PRE_ARRIVE:
						m_vObd_engine.preArrive();
						DkDebuger.v(TAG, "pre arrive!");
						break;
					}
				}
    		});
    	}
    	
    	if (wg_rpminfo != null) {
    		wg_rpminfo.setValue(0);
    		wg_rpminfo.bringToFront();
    	}
    	
    	if (wg_speedinfo != null) {
    		wg_speedinfo.setValue(0);
    		wg_speedinfo.bringToFront();
    	}
    	
    	if (wg_gearinfo != null) {
//    		wg_gearinfo.bringToFront();
    		wg_gearinfo.setGear(GearInfo.GAER_ENUMS.GEAR_N);
    	}
	    
    	if (wg_singlenodebar != null) {
    		wg_singlenodebar.setVisibility(View.INVISIBLE);
    		wg_singlenodebar.bringToFront();
    	}
	}
	
	private VirtualObdEngine m_vObd_engine = new VirtualObdEngine();
	class VirtualObdEngine {
		void start() {
			wg_speedinfo.start();
			wg_rpminfo.start();
			wg_gearinfo.start();
		}
		
		void stop() {
			wg_singlenodebar.arrival();
			wg_arrivalhintbar.stop();
		}
		
		void preArrive() {
			wg_speedinfo.stop();
			wg_rpminfo.stop();
			wg_gearinfo.stop();
		}
	}
	
	@Override
    protected void onPause() {
		wg_routenavi.stopAutoNavi();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    }
}
