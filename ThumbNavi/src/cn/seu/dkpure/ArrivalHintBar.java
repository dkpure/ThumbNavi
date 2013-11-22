package cn.seu.dkpure;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArrivalHintBar extends LinearLayout {
//	private static final String TAG = "ArrivalHintBar";
	private TextView m_static_text = null;
	private TextView m_dynamic_text = null;
	private boolean m_show_arrival_time = false;
	
	private Handler 	m_tick_handler = null;
	private Runnable 	m_tick_runnable = null;
	
	public ArrivalHintBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ArrivalHintBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Parse and Instantiate ArrivalHintBar
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dk_arrival_hint, this);
		
		init();
	}
	
	private String getRemainingTime(int rs) {
		String ret = "";
		int h = 0;
		int m = 0;
		int s = 0;
		
		while (rs >= 3600) {
			++h;
			rs -= 3600;
		}
		while (rs >= 60) {
			++m;
			rs -= 60;
		}
		s = rs;
		
		if (h > 0)
			ret = h + "h";
		if (m > 0)
			ret += m + "m";
		if (s > 0)
			ret += s + "s";
		
		return ret;
	}
	
	private String m_arrival_time = "";
	private int m_view_switch_cnt = 0;
	private int m_speed_calc_cnt = 0;
	private int m_tick_cnt = 0;
	private void init() {
		m_static_text = (TextView) findViewById(R.id.dkarrivalhint_static);
		m_dynamic_text = (TextView) findViewById(R.id.dkarrivalhint_dynamic);
		
		if (GlobalParams.RUN_720P) {
			m_static_text.setTextSize(50);
			m_dynamic_text.setTextSize(100);
		} else {
			m_static_text.setTextSize(40);
			m_dynamic_text.setTextSize(60);
		}
			
		
		m_tick_handler = new Handler();	
		m_tick_runnable = new Runnable() {
			@Override
			public void run() {
				m_tick_cnt++;
				if (m_speed_calc_cnt < 10)
					++m_speed_calc_cnt;
				else {
					float aver_speed = (float)m_cur_distance / (float)m_tick_cnt;
					int remain_secs = (int)((float)(m_total_distance - m_cur_distance) / aver_speed);
//					m_arrival_time =  addRemainSecondToTime(remain_secs);
					m_arrival_time = getRemainingTime(remain_secs);
//					Log.v(TAG, "remain_secs: " + remain_secs);
//					Log.v(TAG, "m_cur_distance = " + m_cur_distance + ", m_total_distance = " + m_total_distance);
//					Log.v(TAG, "aver_speed: " + aver_speed);
//					Log.v(TAG, "m_arrival_time: " + m_arrival_time);
					if (m_show_arrival_time) {
						m_dynamic_text.setText(m_arrival_time);
					}
					m_speed_calc_cnt = 0;
				}
				
				if (m_view_switch_cnt < 15 * 2 + 4)
					m_view_switch_cnt++;
				else {
					if (!m_show_arrival_time && (m_total_distance - m_cur_distance) < 500)
						return;
					m_show_arrival_time = !m_show_arrival_time;
					if (m_show_arrival_time)
						showArrivalTime(m_arrival_time);
					else
						showRemainDistance();
					
					m_view_switch_cnt = 0;
				}
				
				m_tick_handler.postDelayed(this, 1000); // loop it
			}
		};
		m_tick_handler.postDelayed(m_tick_runnable, 1000); // trick the runnable after 1 second
	}
	
	private void showArrivalTime(String t) {
		m_static_text.setText("สฃำเ: ");
		m_dynamic_text.setText(t);
	}
	
	private void showRemainDistance() {
		m_static_text.setText("สฃำเ: ");
		m_dynamic_text.setText(formatDistanceToShow(m_total_distance - m_cur_distance));
	}
	
	private String formatDistanceToShow(int distance_reminded) {
		String ret = "0";
		if (distance_reminded < 0)
			return ret;
		
		if (distance_reminded > 1000) {
			float td = (float)distance_reminded / 1000.0f;
			int int_part = (int)td;
			int float_part = (int)((td - int_part) * 100);
			ret = int_part + "." + float_part + "Km";
		} else {
			ret = distance_reminded + "m";
		}
		
		return ret;			
	}
	
	private int m_total_distance = 0;
	public final void setTotalDistance(int d) {
		m_total_distance = d;
		m_static_text.setText("สฃำเ: ");
		m_dynamic_text.setText(formatDistanceToShow(d));
		this.setVisibility(View.VISIBLE);
	}
	
	private int m_cur_distance = 0;
	private int m_distance_update_cnt = 0;
	public void updateCurrentDistance(int d) {
		if (0 == d)
			return;
		
		m_cur_distance = d;
		if (m_distance_update_cnt < 4) {
			m_distance_update_cnt++;
			return;
		} else
			m_distance_update_cnt = 0;
		
		if (!m_show_arrival_time) {
			m_dynamic_text.setText(formatDistanceToShow(m_total_distance - m_cur_distance));
		}
	}
	
	public void stop() {
		m_tick_handler.removeCallbacks(m_tick_runnable);
		m_cur_distance = 0;
		m_dynamic_text.setText("0m");
	}

}
