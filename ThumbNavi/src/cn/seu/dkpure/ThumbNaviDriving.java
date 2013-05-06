package cn.seu.dkpure;

import android.app.Activity;
import android.os.Bundle;
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
	private DkMapWidget 	wg_dkmap = null;
	private RoadInfoBar		wg_roadinfo = null;
	private GearInfo 		wg_gearinfo = null;
	private RpmInfo			wg_rpminfo = null;
	private	SpeedInfo		wg_speedinfo = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// set no title & fullscreen before setContentView
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
			        					WindowManager.LayoutParams.FLAG_FULLSCREEN);
     		
        setContentView(R.layout.layout_driving);
        
        emergeViews();
	}
	
	private void emergeViews() {
    	wg_roadinfo = (RoadInfoBar) findViewById(R.id.driving_RoadInfoBar);
    	wg_gearinfo = (GearInfo) findViewById(R.id.driving_GearInfo);
    	wg_rpminfo = (RpmInfo) findViewById(R.id.driving_RpmInfo);
    	wg_speedinfo = (SpeedInfo) findViewById(R.id.driving_SpeedInfo);
    	wg_dkmap 	= (DkMapWidget) findViewById(R.id.driving_DkMapWidget);
    	   	
    	if (wg_rpminfo != null)
    		wg_rpminfo.setValue(1356);
    	
    	if (wg_speedinfo != null)
    		wg_speedinfo.setValue(76);
    	
    	if (wg_gearinfo != null) {
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
	    	wg_roadinfo.addNode(0, "卡子门高架 - 300m");
	    	wg_roadinfo.addNode(7, "龙蟠中路 - 2.6Km");
	    	wg_roadinfo.addNode(2, "机场连接线 - 90m");
	    	wg_roadinfo.addNode(6, "机场连接线 - 30m");
    	}
	}
	
	@Override
    protected void onPause() {
    	if (wg_dkmap != null)
    		wg_dkmap.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	if (wg_dkmap != null)
    		wg_dkmap.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
//    	if (wg_dkmap != null)
//    		wg_dkmap.onDestroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	if (wg_dkmap != null)
    		wg_dkmap.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	if (wg_dkmap != null)
    		wg_dkmap.onRestoreInstanceState(savedInstanceState);
    }
}
