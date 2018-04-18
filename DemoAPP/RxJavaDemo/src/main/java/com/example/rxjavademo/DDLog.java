package com.example.rxjavademo;

import android.util.Log;

public final class DDLog {

	/** all Log on-off */
	private static boolean all = false;
	/** info Log on-off */
	private final static boolean i = true;
	/** debug Log on-off */
	private final static boolean d = true;
	/** err Log on-off */
	private final static boolean e = true;
	/** verbose Log on-off */
	private final static boolean v = true;
	/** warn Log on-off */
	private final static boolean w = true;

	/** default tag */
	private final static String DEFAULT_TAG = "+++++DDLog+++++";

	public static void i(String msg) {
		if (all && i) {
			Log.i(DEFAULT_TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (all && i) {
			Log.i(DEFAULT_TAG+tag, msg);
		}
	}

	public static void d(String msg) {
		if (all && d) {
			Log.d(DEFAULT_TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (all && d) {
			Log.d(DEFAULT_TAG+tag, msg);
		}
	}

	public static void e(String msg) {
		if (all && e) {
				Log.e(DEFAULT_TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (all && e) {
			Log.e(DEFAULT_TAG+tag, msg);
		}
	}

	public static void v(String msg) {
		if (all && v) {
			Log.v(DEFAULT_TAG, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (all && v) {
			Log.v(DEFAULT_TAG+tag, msg);
		}
	}

	public static void w(String msg) {
		if (all && w) {
			Log.w(DEFAULT_TAG, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (all && w) {
			Log.w(DEFAULT_TAG+tag, msg);
		}
	}
	//log开关
    public static void setDebug(boolean value){
		all = value;
	}
}
