package cn.seu.dkpure;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Main activity to be launch
 * @author dkpure
 *
 */
public class ThmbNaiviMain extends Activity {
	private DkMapWidget 	wg_dkmap = null;
	private LinearLayout 	wg_obdinfo = null;
	
	private ImageView		wg_sign_seatbelt;
	private ImageView		wg_sign_fontlight;
	private ImageView		wg_sign_turnleft;
	private ImageView		wg_sign_gas;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set no title & fullscreen before setContentView
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		        					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		
		setContentView(R.layout.main);
		
		emergeViews();
		dashboardSignsAnimShow();
    }
    
    private void startDrivingActivity() {
    	Intent intent = new Intent(this, ThumbNaviDriving.class);
		startActivity(intent);
    }
    
    private void emergeViews() {
//    	wg_clock = (DigitalClock) findViewById(R.id._DigitalClock);
//    	wg_weather = (WeatherView) findViewById(R.id.weatherView1);
    	wg_dkmap = (DkMapWidget) findViewById(R.id.main_DkMapWidget);
    	
    	wg_obdinfo = (LinearLayout) findViewById(R.id.linearLayout1);
    	
    	if (wg_obdinfo != null) {
    		wg_obdinfo.bringToFront();

    		wg_obdinfo.setOnClickListener(
	    				new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								startDrivingActivity();
							}
	    					
	    				}
    				);
    	}
    	
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
    	if (wg_dkmap != null)
    		wg_dkmap.onDestroy();
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
