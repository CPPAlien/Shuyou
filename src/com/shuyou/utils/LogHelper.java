package com.shuyou.utils;

import android.util.Log;

/**
 * 自定义Log
 * @author jiabin
 *
 */
public class LogHelper {

	private static boolean enableDefaultLog = true;//true:开启log，发版前需要关闭log，置为false
	
	private static final int RETURN_NOLOG = 99;
	
	private static final String RETURN_NOLOG_STRING = "RETURN_NOLOG";
	
	public static int d(String tag, String msg, Throwable tr) {

        if (msg == null)
            msg = "";

        return enableDefaultLog ? Log.d(tag, msg, tr) : RETURN_NOLOG;
    }
	
	public static int d(String tag, String msg) {

        if (msg == null)
            msg = "";

        return enableDefaultLog ? Log.d(tag, msg) : RETURN_NOLOG;
    }
	
	public static int e(String tag, String msg) {
        if (msg == null)
            msg = "";

        return enableDefaultLog ? Log.e(tag, msg) : RETURN_NOLOG;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (msg == null)
            msg = "";

        return enableDefaultLog ? Log.e(tag, msg, tr) : RETURN_NOLOG;
    }
    
    public static String getStackTraceString(Throwable tr) {
        return enableDefaultLog ? Log.getStackTraceString(tr) : RETURN_NOLOG_STRING;
    }
    
    public static int i(String tag, String msg, Throwable tr) {
        if (msg == null)
            msg = "";

        return enableDefaultLog ? Log.i(tag, msg, tr) : RETURN_NOLOG;
    }

    public static int i(String tag, String msg) {
        if (msg == null)
            msg = "";
        return enableDefaultLog ? Log.i(tag, msg) : RETURN_NOLOG;
    }
    
    public static boolean isLoggable(String tag, int level) {
        return enableDefaultLog ? Log.isLoggable(tag, level) : false;
    }

    public static int println(int priority, String tag, String msg) {
        return enableDefaultLog ? Log.println(priority, tag, msg) : RETURN_NOLOG;
    }
    
    public static int v(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.v(tag, msg, tr) : RETURN_NOLOG;
    }

    public static int v(String tag, String msg) {
        return enableDefaultLog ? Log.v(tag, msg) : RETURN_NOLOG;
    }
    
    public static int w(String tag, String msg) {
        return enableDefaultLog ? Log.w(tag, msg) : RETURN_NOLOG;
    }

    public static int w(String tag, Throwable tr) {
        return enableDefaultLog ? Log.w(tag, tr) : RETURN_NOLOG;
    }

    public static int w(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.w(tag, msg, tr) : RETURN_NOLOG;
    }
}
