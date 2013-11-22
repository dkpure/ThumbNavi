package cn.seu.dkpure;

import android.util.Log;

public class DkDebuger {

	private static final boolean debug_on = true;
	private static final boolean verbose_on = true;
	private static final boolean error_on = true;
	public static void d(String tag, String msg) {
		if (debug_on)
			Log.d(tag, msg);
	}
	
	public static void e(String tag, String msg) {
		if (error_on)
			Log.e(tag, msg);
	}
	
	public static void v(String tag, String msg) {
		if (verbose_on)
			Log.v(tag, msg);
	}
}
