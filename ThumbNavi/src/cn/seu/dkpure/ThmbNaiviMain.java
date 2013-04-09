package cn.seu.dkpure;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class ThmbNaiviMain extends Activity {
	DigitalClock 	wg_clock;
	WeatherView	 	wg_weather;
	RoadInfoBar		wg_roadinfo;
	GearInfo		wg_gearinfo;
	
	ImageView		wg_sign_seatbelt;
	ImageView		wg_sign_fontlight;
	ImageView		wg_sign_turnleft;
	ImageView		wg_sign_gas;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set no title & fullscreen before setContentView
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		        					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);
		
		getWindow().setFormat(PixelFormat.RGBA_8888);
		
		emergeViews();
		dashboardSignsAnimShow();
    }
    
    private void emergeViews() {
    	wg_clock = (DigitalClock) findViewById(R.id._DigitalClock);
    	wg_weather = (WeatherView) findViewById(R.id.weatherView1);
    	wg_roadinfo = (RoadInfoBar) findViewById(R.id.roadInfoBar);
    	wg_gearinfo = (GearInfo) findViewById(R.id._GearInfo);
    	
    	if (wg_gearinfo != null) {
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
	    	wg_gearinfo.setGear(GearInfo.GAER_ENUMS.GEAR_R);
	    	wg_gearinfo.animateSwitchGear(GearInfo.GAER_ENUMS.GEAR_N);
    	}
    	
    	
    	wg_roadinfo.addNode(0, "龙蟠中路 - 300m");
    	wg_roadinfo.addNode(7, "卡子门高架 - 2.6Km");
    	wg_roadinfo.addNode(2, "机场连接线 - 90m");
    	wg_roadinfo.addNode(6, "机场连接线 - 30m");
    	
    	wg_sign_seatbelt = (ImageView) findViewById(R.id.imgV_sign_seatbelt);
    	wg_sign_fontlight = (ImageView) findViewById(R.id.imgV_sign_frontlight);
    	wg_sign_turnleft = (ImageView) findViewById(R.id.imgV_sign_turnleft);
    	wg_sign_gas = (ImageView) findViewById(R.id.imgV_sign_gas);
    }
    
    private void dashboardSignsAnimShow() {
    	Animation a = new TranslateAnimation(0.0f, 
    										0.0f, 
    										87.0f, // magic number of imageView height
    										0.0f);
        a.setDuration(1500); // 1.5s
        a.setRepeatCount(0);
        a.setInterpolator(AnimationUtils.loadInterpolator(this,
                			android.R.anim.bounce_interpolator));
        
        if (wg_sign_seatbelt != null)
        	wg_sign_seatbelt.startAnimation(a);
        if (wg_sign_fontlight != null)
        	wg_sign_fontlight.startAnimation(a);
        if (wg_sign_turnleft != null)
        	wg_sign_turnleft.startAnimation(a);
        if (wg_sign_gas != null)
        	wg_sign_gas.startAnimation(a);
    }
    
}
