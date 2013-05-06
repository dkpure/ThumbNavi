package cn.seu.dkpure;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

/**
 * Main Application
 * - submit BaiduMap SDK key at start time
 * @author dkpure
 *
 */
public class MainApplication extends Application {
	
    private static MainApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    // API Key of ThumbNavi
    public static final String strKey = "B0D87068D6CEDADA393974913FA83FBC3A2A99D0";
	
	@Override
    public void onCreate() {
	    super.onCreate();
	    
		mInstance = this;
		initEngineManager(this);
	}
	
	/**
	 * Call mBMapManager.destroy() before app terminated to
	 * avoid additional time consumption of duplicated initialization
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
	    if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
	    
		super.onTerminate();
	}
	
	/**
	 * Initialize mBMapManager once the application created
	 * @param context
	 */
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(MainApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	/**
	 * Return the instance of MapApplication
	 * @return MapApplication
	 */
	public static MainApplication getInstance() {
		return mInstance;
	}
	
	/**
	 * Implements MKGeneralListener to handle general errors
	 * such as network error and permission error
	 * @author dkpure
	 *
	 */
    static class MyGeneralListener implements MKGeneralListener {
        @Override
        public void onGetNetworkState(int iError) {
//            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
//                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
//                    Toast.LENGTH_LONG).show();
//            }
            
        	if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MainApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(MainApplication.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                MainApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}