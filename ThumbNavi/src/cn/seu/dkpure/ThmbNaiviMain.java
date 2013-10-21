package cn.seu.dkpure;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
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
	private DigitalClock	wg_clock;
	
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
    	wg_clock = (DigitalClock) findViewById(R.id.main_DigitalClock);
//    	wg_weather = (WeatherView) findViewById(R.id.weatherView1);
    	wg_dkmap = (DkMapWidget) findViewById(R.id.main_DkMapWidget);
    	
    	wg_obdinfo = (LinearLayout) findViewById(R.id.linearLayout1);
    	
    	if (wg_clock != null) {
	    	wg_clock.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startDrivingActivity();
					}
					
				}
			);
    	}
    	if (wg_obdinfo != null) {
    		wg_obdinfo.bringToFront(); //bring to the top layer

//    		wg_obdinfo.setOnClickListener(
//	    				new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								startDrivingActivity();
//							}
//	    					
//	    				}
//    				);
    	}
    	
    	wg_sign_seatbelt = (ImageView) findViewById(R.id.imgV_sign_seatbelt);
    	wg_sign_fontlight = (ImageView) findViewById(R.id.imgV_sign_frontlight);
    	wg_sign_turnleft = (ImageView) findViewById(R.id.imgV_sign_turnleft);
    	wg_sign_gas = (ImageView) findViewById(R.id.imgV_sign_gas);
    	if (wg_sign_seatbelt != null)
    		wg_sign_seatbelt.setVisibility(View.INVISIBLE);
    	if (wg_sign_fontlight != null)
    		wg_sign_fontlight.setVisibility(View.INVISIBLE);
    	if (wg_sign_turnleft != null)
    		wg_sign_turnleft.setVisibility(View.INVISIBLE);
    	if (wg_sign_gas != null)
    		wg_sign_gas.setVisibility(View.INVISIBLE);
    }
    
    private void dashboardSignsAnimShow() {
    	final Animation a = new AlphaAnimation(0, 1.0f);
    	a.setDuration(1500); // 1.5s
    	a.setRepeatCount(0);
    	a.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.linear_interpolator));
    	a.setAnimationListener(new AnimationListener() {
	    	int anim_which = 0;
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				++anim_which;
				switch(anim_which) {
				case 1:
					//clear the animation of previous view or it will repeat again
					wg_sign_seatbelt.clearAnimation();
					if (wg_sign_fontlight != null)
			        	wg_sign_fontlight.startAnimation(a);
					break;
				case 2:
					wg_sign_fontlight.clearAnimation();
					if (wg_sign_turnleft != null)
			        	wg_sign_turnleft.startAnimation(a);
					break;
				case 3:
					wg_sign_turnleft.clearAnimation();
					if (wg_sign_gas != null)
			        	wg_sign_gas.startAnimation(a);
					break;
				default: 
					break;
				}
			}
	
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				switch(anim_which) {
				case 0: if (wg_sign_seatbelt != null) wg_sign_seatbelt.setVisibility(View.VISIBLE); break;
				case 1: if (wg_sign_fontlight != null) wg_sign_fontlight.setVisibility(View.VISIBLE); break;
				case 2: if (wg_sign_turnleft != null) wg_sign_turnleft.setVisibility(View.VISIBLE); break;
				case 3:  if (wg_sign_gas != null) wg_sign_gas.setVisibility(View.VISIBLE); break;
				default:
					break;
				}
			}
	    });
    	
    	//trigger the alpha animation chain
        if (wg_sign_seatbelt != null)
        	wg_sign_seatbelt.startAnimation(a);
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
