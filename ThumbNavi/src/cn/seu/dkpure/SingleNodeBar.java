package cn.seu.dkpure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleNodeBar extends LinearLayout {
	private static final String TAG = "SingleNodeBar";
	private TextView m_distance_text = null;
	private TextView m_roadname_text = null;
	private ImageView m_dir_symbol = null;
	
	private Handler 	m_delay_handler = null;
	private Runnable 	m_delay_runnable = null;
	
	public SingleNodeBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SingleNodeBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Parse and Instantiate SingleNodeBar
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dk_singlenode_bar, this);
		
		init();
	}
	
	private void init() {
		is_arrival = false;
		m_dir_symbol = (ImageView) findViewById(R.id.dksinglenode_dirsymbol);
		m_roadname_text = (TextView) findViewById(R.id.dksinglenode_roadname);
		m_distance_text = (TextView) findViewById(R.id.dksinglenode_distance);
		
		m_delay_handler = new Handler();	
		m_delay_runnable = new Runnable() {
			@Override
			public void run() {
				if (m_dir_symbol != null) {
					m_dir_symbol.setImageBitmap(getBitmapFromIndex(RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT));
					m_msg_pending = false;
				}
			}
		};
	}
	
	public void updateDistance(String d) {
		if (m_distance_text != null && !is_arrival)
			m_distance_text.setText(d + "m");
	}
	
	private boolean m_msg_pending = false;
	private boolean m_first_node = true;
	public void switchNode(String node_content) {
		if (m_roadname_text != null) {
			m_roadname_text.setText(RouteParser.getDestination(node_content));
			m_dir_symbol.setImageBitmap(getBitmapFromIndex(RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT));
			
			if (m_first_node) {
				m_first_node = false;
				this.setVisibility(View.VISIBLE);
			}
		}
		Log.v(TAG, node_content);
	}
	
	public void preSwitchNode(String node_content) {
		if (m_dir_symbol != null) {
			String dir =  RouteParser.getDirection(node_content);
			int dir_id = RouteParser.getDirIndexFromDir(dir);
			m_dir_symbol.setImageBitmap(getBitmapFromIndex(dir_id));
		}
	}
	
	private boolean is_arrival = false;
	public void arrival() {
		is_arrival = true;
		if (m_roadname_text != null) {
			if (m_msg_pending)
				m_delay_handler.removeCallbacks(m_delay_runnable);
			m_roadname_text.setText("µ½´ï");
			m_distance_text.setText("");
			m_dir_symbol.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.arrival_flag));
		}
	}
	
	private final int 
	drawable_256x256_id_tab[] = 
	{
		R.drawable.dir_go_straight, R.drawable.dir_turn_left0, 
		R.drawable.dir_turn_left1, 	R.drawable.dir_turn_left2,
		R.drawable.dir_turn_back, 	R.drawable.dir_turn_right0, 
		R.drawable.dir_turn_right1, R.drawable.dir_turn_right2
	 };
	private final int 
	drawable_98x98_id_tab[] = 
	{
		R.drawable.dir_go_straight_98x98, 	R.drawable.dir_turn_left0_98x98,
		R.drawable.dir_turn_left1_98x98, 	R.drawable.dir_turn_left2_98x98,
		R.drawable.dir_turn_back_98x98, 	R.drawable.dir_turn_right0_98x98, 
		R.drawable.dir_turn_right1_98x98, 	R.drawable.dir_turn_right2_98x98
	 };
	
	/**
	 * Decode a bitmap from resource
	 * @param direction_index varies from RouteDirectionConstants
	 * @return bitmap of navigation direction
	 */
	private Bitmap getBitmapFromIndex(int direction_index) {
		int resId = R.drawable.dir_unknown;
		
		if (direction_index < drawable_256x256_id_tab.length) {
			if (GlobalParams.RUN_720P)
				resId = drawable_256x256_id_tab[direction_index];
			else
				resId = drawable_98x98_id_tab[direction_index];
		}
		
		return BitmapFactory.decodeResource(getResources(), resId);
	}

}
