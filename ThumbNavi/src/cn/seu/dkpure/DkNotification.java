package cn.seu.dkpure;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Banner widget to show reminders with switch animation
 * @author dkpure
 *
 */
public class DkNotification extends RelativeLayout {
//	private static final String TAG = "DkNotification";
	private TextView 	m_contact_name = null;
	private ImageView 	m_msg_bar = null;
	
	public DkNotification(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	public DkNotification(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Parse and Instantiate DkBanner
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dk_tel_message, this);
		emergeViews();
		setVisibility(View.INVISIBLE);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				slideIn();
				delayedHide();
			}
			
		}, 25 * 1000);
	}
	
	void emergeViews() {
		m_contact_name = (TextView) findViewById(R.id.dk_tel_contact_name);
		m_msg_bar = (ImageView) findViewById(R.id.dk_tel_msg_bar);
		
		if (GlobalParams.RUN_720P) {
			m_contact_name.setTextSize(60);
			m_msg_bar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tel_msg_phonecall_720p));
		} else {
			m_contact_name.setTextSize(30);
			m_msg_bar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tel_msg_phonecall));
		}
	}
	
	public void slideIn() {
		TranslateAnimation anim_in = new TranslateAnimation(
											Animation.RELATIVE_TO_SELF, 1f, 
											Animation.RELATIVE_TO_SELF, 0f,
											Animation.RELATIVE_TO_SELF, 0f,
											Animation.RELATIVE_TO_SELF, 0f);
		anim_in.setDuration(1500);
		anim_in.setRepeatCount(0);
//		anim1.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
		setVisibility(View.VISIBLE);
		startAnimation(anim_in);
	}
	
	private void delayedHide() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				slideOut();
			}
			
		}, 10000);
	}
	
	public void slideOut() {
		TranslateAnimation anim_out = new TranslateAnimation(
											Animation.RELATIVE_TO_SELF, 0f, 
											Animation.RELATIVE_TO_SELF, 1f,
											Animation.RELATIVE_TO_SELF, 0f,
											Animation.RELATIVE_TO_SELF, 0f);
		anim_out.setDuration(1500);
		anim_out.setRepeatCount(0);
//		anim2.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
		anim_out.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		startAnimation(anim_out);
	}
	
}
