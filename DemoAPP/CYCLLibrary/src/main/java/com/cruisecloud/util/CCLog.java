/*
 * Copyright 2017 CruiseCloud. All Rights Reserved.
 */
package com.cruisecloud.util;

public final class CCLog {

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
	private final static String DEFAULT_TAG = "+++CCLog+++";
	/** always tag */
	private final static String ALWAYS_TAG  = "+++Always+++";

	private CCLog() { }

	/**
	 * info Log print, default print tag
	 *
	 * @param msg
	 *            :print message
	 */
	public static void i(String msg) {
		if (all && i) {
			android.util.Log.i(DEFAULT_TAG, msg);
		}
	}

	/**
	 * info Log print
	 *
	 * @param tag
	 *            :print tag
	 * @param msg
	 *            :print message
	 */
	public static void i(String tag, String msg) {
		if (all && i) {
			android.util.Log.i(tag, msg);
		}
	}

	/**
	 * debug Log print, default print tag
	 *
	 * @param msg
	 *            :print message
	 */
	public static void d(String msg) {
		if (all && d) {
			android.util.Log.d(DEFAULT_TAG, msg);
		}
	}

	/**
	 * debug Log print
	 *
	 * @param tag
	 *            :print tag
	 * @param msg
	 *            :print message
	 */
	public static void d(String tag, String msg) {
		if (all && d) {
			android.util.Log.d(tag, msg);
		}
	}

	/**
	 * err Log print, default print tag
	 *
	 * @param msg
	 *            :print message
	 */
	public static void e(String msg) {
		if (all && e) {
			try {
				android.util.Log.e(DEFAULT_TAG, msg);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * err Log print
	 *
	 * @param tag
	 *            :print tag
	 * @param msg
	 *            :print message
	 */
	public static void e(String tag, String msg) {
		if (all && e) {
			android.util.Log.e(tag, msg);
		}
	}

	/**
	 * verbose Log print, default print tag
	 *
	 * @param msg
	 *            :print message
	 */
	public static void v(String msg) {
		if (all && v) {
			android.util.Log.v(DEFAULT_TAG, msg);
		}
	}

	/**
	 * verbose Log print
	 *
	 * @param tag
	 *            :print tag
	 * @param msg
	 *            :print message
	 */
	public static void v(String tag, String msg) {
		if (all && v) {
			android.util.Log.v(tag, msg);
		}
	}

	/**
	 * warn Log print, default print tag
	 *
	 * @param msg
	 *            :print message
	 */
	public static void w(String msg) {
		if (all && w) {
			android.util.Log.w(DEFAULT_TAG, msg);
		}
	}

	/**
	 * warn Log print
	 *
	 * @param tag
	 *            :print tag
	 * @param msg
	 *            :print message
	 */
	public static void w(String tag, String msg) {
		if (all && w) {
			android.util.Log.w(tag, msg);
		}
	}

	/**
	 * always Log print, default print tag
	 *
	 * @param msg
	 *            :print message
	 */
	public static void always(String msg) {
        android.util.Log.w(ALWAYS_TAG, msg);
    }

	/**
	 * always Log print
	 *
	 * @param tag
	 *            :print tag
	 * @param msg
	 *            :print message
	 */
	public static void always(String tag, String msg) {
        android.util.Log.w(tag, msg);
    }

    public static void setDebug(boolean value){
		all = value;
	}
}
