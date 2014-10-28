package com.hmammon.photointerface;

import android.util.Log;


/**
 * 打应日志类，可自动判断打包类型
 * Created by Xcfh on 2014/10/20.
 */
public class ZedLog {
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void i(Object obj, String log) {
        if (DEBUG) {
            if (obj instanceof String) Log.i(String.valueOf(obj), log);
            else i(obj.getClass().getSimpleName(), log);
        }
    }


    public static void d(Object obj, String log) {
        if (DEBUG) {
            if (obj instanceof String) Log.d(String.valueOf(obj), log);
            else d(obj.getClass().getSimpleName(), log);
        }
    }

    public static void e(Object obj, String log) {
        if (DEBUG) {
            if (obj instanceof String) Log.e(String.valueOf(obj), log);
            else e(obj.getClass().getSimpleName(), log);
        }
    }

    public static void v(Object obj, String log) {
        if (DEBUG) {
            if (obj instanceof String) Log.v(String.valueOf(obj), log);
            else v(obj.getClass().getSimpleName(), log);
        }
    }

    public static void SysoutAnyTime(String log) {
        System.out.println(log);
    }

}
