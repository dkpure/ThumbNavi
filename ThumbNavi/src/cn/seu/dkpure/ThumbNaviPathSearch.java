package cn.seu.dkpure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

public class ThumbNaviPathSearch extends Activity {
	private static final String TAG = "ThumbNaviPathSearch";
	private PathSearchDialog m_start_dialog = null;
	private PathSearchDialog m_stop_dialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		        					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.dk_pathsearch_view);
		emergeViews();
		slideDown();
		
		final Handler tick_handler = new Handler();	
		Runnable tick_runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startDrivingActivity();
			}			
		};
		
		tick_handler.postDelayed(tick_runnable, 5000);
	}
	
	void emergeViews() {
		m_start_dialog = (PathSearchDialog) findViewById(R.id.pathsearch_start);
		m_stop_dialog = (PathSearchDialog) findViewById(R.id.pathsearch_stop);
		
		if (m_start_dialog != null) {
			m_start_dialog.setVisibility(View.INVISIBLE);
			m_start_dialog.setAllParams(true, m_start_city, m_start_location);
		} else
			DkDebuger.e(TAG, "null m_start_dialog");
		
		if (m_stop_dialog != null) {
			m_stop_dialog.setVisibility(View.INVISIBLE);
			m_stop_dialog.setAllParams(false, m_stop_city, m_stop_location);
		} else
			DkDebuger.e(TAG, "null m_stop_dialog");
	}
	
	private String m_start_city 	= "南京";
	private String m_stop_city 		= "南京";
	private String m_start_location = "总统府";//"新街口";//
	private String m_stop_location 	= "栖霞山";//"禄口机场";//
	private void startDrivingActivity() {
		Bundle extra_msg = new Bundle();
		extra_msg.putString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STARTCITY, m_start_city);
		extra_msg.putString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STARTLOCATION, m_start_location);
		extra_msg.putString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STOPCITY, m_stop_city);
		extra_msg.putString(GlobalParams.PATH_SEARCH_BUNDLE_KEY_STOPLOCATION, m_stop_location);
    	Intent intent = new Intent(this, ThumbNaviDriving.class);
    	intent.putExtras(extra_msg);
		startActivity(intent);
    }
	
	private void slideDown() {
		final TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, 
																Animation.RELATIVE_TO_SELF, 0f,
																Animation.RELATIVE_TO_SELF, -1f,
																Animation.RELATIVE_TO_SELF, 0f);
		anim.setDuration(500);
		anim.setRepeatCount(0);
		anim.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
		m_start_dialog.setVisibility(View.VISIBLE);
		m_stop_dialog.setVisibility(View.VISIBLE);
		m_start_dialog.startAnimation(anim);
		m_stop_dialog.startAnimation(anim);
	}
	
	@Override
	protected void onPause() {
	 	//...
	    super.onPause();
	}
	
	@Override
	protected void onResume() {
		//...
	    super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		//...
	    super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//...
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//...
	}
}
