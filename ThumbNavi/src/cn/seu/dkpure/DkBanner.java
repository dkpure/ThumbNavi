package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;

/**
 * Banner widget to show reminders with switch animation
 * @author dkpure
 *
 */
public class DkBanner extends FrameLayout {

	static final long	BANNER_SWITCH_DURATION = 8000;
	static final int	BANNER_TEXT_DEFAULT_SIZE = 26;
	private TextView	m_text_banner1;
	private TextView 	m_text_banner2;
	private Animation	m_in_anim;
	private Animation	m_out_anim;
	private boolean		m_default_banner_on = false;
	
	public DkBanner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	public DkBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Parse and Instantiate DkBanner
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dk_banner, this);
		
		init(context);
	}
	
	private void init(Context context) {
		m_text_banner1 = (TextView) findViewById(R.id.dkbanner_text1);
		m_text_banner2 = (TextView) findViewById(R.id.dkbanner_text2);
		
//		m_text_banner1.setTextColor(Color.WHITE);
		m_text_banner1.setTextColor(Color.rgb(0, 162, 232));
		m_text_banner1.setTextSize(BANNER_TEXT_DEFAULT_SIZE);
		
		m_text_banner2.setTextColor(Color.rgb(0, 162, 232));
		m_text_banner2.setTextSize(BANNER_TEXT_DEFAULT_SIZE);
		m_text_banner2.setVisibility(View.INVISIBLE);
		
		m_in_anim = AnimationUtils.loadAnimation(context, R.anim.push_up_in);
		m_out_anim = AnimationUtils.loadAnimation(context, R.anim.push_up_out);
		
		m_in_anim.setAnimationListener( new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (!m_default_banner_on)
					m_text_banner1.setVisibility(View.VISIBLE);
				else
					m_text_banner1.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				m_text_banner1.setVisibility(View.VISIBLE);
				m_default_banner_on = !m_default_banner_on;
			}
			
		});
		
		m_out_anim.setAnimationListener( new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (!m_default_banner_on)
					m_text_banner2.setVisibility(View.INVISIBLE);
				else
					m_text_banner2.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				m_text_banner2.setVisibility(View.VISIBLE);
			}
			
		});
		
		/* Use a Runnable to trick the switch animation every 5 seconds */
		final Handler tick_handler = new Handler();	
		Runnable tick_runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (m_default_banner_on) {
					m_text_banner1.startAnimation(m_in_anim);
					m_text_banner2.startAnimation(m_out_anim);
				} else {
					m_text_banner1.startAnimation(m_out_anim);
					m_text_banner2.startAnimation(m_in_anim);
				}
				tick_handler.postDelayed(this, BANNER_SWITCH_DURATION); // loop it
			}			
		};
		
		// trick the runnable after 5 seconds
		tick_handler.postDelayed(tick_runnable, BANNER_SWITCH_DURATION); 
	}
}
