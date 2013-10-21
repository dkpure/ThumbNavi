package cn.seu.dkpure;

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
import android.view.View.OnClickListener;
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
//	private DkMapWidget 	wg_dkmap = null;
	private RouteNaviWidget wg_routenavi = null;
	private RoadInfoBar		wg_roadinfo = null;
	private GearInfo 		wg_gearinfo = null;
	private RpmInfo			wg_rpminfo = null;
	private	SpeedInfo		wg_speedinfo = null;
	private MKSearch 		mSearch = null;
	
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
					wg_routenavi.setRoute(res.getPlan(0).getRoute(0));
					wg_routenavi.startNavigation();
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
		stNode.name = "总统府";//"东南大学";//
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = "河定桥";//"鼓楼区";//
		
		// 实际使用中请对起点终点城市进行正确的设定
		mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);//MKSearch.ECAR_DIS_FIRST
		mSearch.drivingSearch(CITY, stNode, CITY, enNode);
	}
	
	private void emergeViews() {
    	wg_roadinfo = (RoadInfoBar) findViewById(R.id.driving_RoadInfoBar);
    	wg_gearinfo = (GearInfo) findViewById(R.id.driving_GearInfo);
    	wg_rpminfo = (RpmInfo) findViewById(R.id.driving_RpmInfo);
    	wg_speedinfo = (SpeedInfo) findViewById(R.id.driving_SpeedInfo);
//    	wg_dkmap 	= (DkMapWidget) findViewById(R.id.driving_DkMapWidget);
		wg_routenavi = (RouteNaviWidget) findViewById(R.id.driving_RouteNaviWidget);
    	
    	if (wg_rpminfo != null) {
    		wg_rpminfo.setValue(1356);
    		wg_rpminfo.bringToFront();
    	}
    	
    	if (wg_speedinfo != null) {
    		wg_speedinfo.setValue(76);
    		wg_speedinfo.bringToFront();
    	}
    	
    	if (wg_gearinfo != null) {
    		wg_gearinfo.bringToFront();
    		wg_gearinfo.setGear(GearInfo.GAER_ENUMS.GEAR_R);
	    	wg_gearinfo.setOnClickListener(new OnClickListener () {
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					int g_index = (int)(Math.random() * 7);
					GearInfo.GAER_ENUMS g = GearInfo.GAER_ENUMS.GEAR_N;
					switch (g_index) {
					case 0: g = GearInfo.GAER_ENUMS.GEAR_N; break;
					case 1: g = GearInfo.GAER_ENUMS.GEAR_1; break;
					case 2: g = GearInfo.GAER_ENUMS.GEAR_2; break;
					case 3: g = GearInfo.GAER_ENUMS.GEAR_3; break;
					case 4: g = GearInfo.GAER_ENUMS.GEAR_4; break;
					case 5: g = GearInfo.GAER_ENUMS.GEAR_5; break;
					case 6: g = GearInfo.GAER_ENUMS.GEAR_R; break;
					}
					wg_gearinfo.animateSwitchGear(g);
				}
	    		
	    	});
    	}
	    
    	if (wg_roadinfo != null) {
    		wg_roadinfo.bringToFront();
	    	wg_roadinfo.addNode(0, "卡子门高架 - 300m");
	    	wg_roadinfo.addNode(7, "龙蟠中路 - 2.6Km");
	    	wg_roadinfo.addNode(2, "机场连接线 - 90m");
	    	wg_roadinfo.addNode(6, "机场连接线 - 30m");
    	}
	}
	
	@Override
    protected void onPause() {
//    	if (wg_dkmap != null)
//    		wg_dkmap.onPause();
		wg_routenavi.stopAutoNavi();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
//    	if (wg_dkmap != null)
//    		wg_dkmap.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
//    	if (wg_dkmap != null)
//    		wg_dkmap.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
//    	if (wg_dkmap != null)
//    		wg_dkmap.onRestoreInstanceState(savedInstanceState);
    }
}
