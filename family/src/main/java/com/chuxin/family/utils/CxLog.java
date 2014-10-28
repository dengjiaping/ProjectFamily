package com.chuxin.family.utils;

import android.util.Log;

import com.chuxin.family.BuildConfig;

public class CxLog {

    private final static boolean openState = BuildConfig.DEBUG; //发布版本的时候，请置为false
    private final static String RK_TAG = BuildConfig.PACKAGE_NAME;

    public static void d(String subTag, String info) {
        if (!openState) {
            return;
        }
        Log.d(RK_TAG, subTag + ":" + info);
    }

    public static void i(String subTag, String info) {
        if (!openState) {
            return;
        }
        Log.i(RK_TAG, subTag + ":" + info);
    }

    public static void w(String subTag, String info) {
        if (!openState) {
            return;
        }
        Log.w(RK_TAG, subTag + ":" + info);
    }

    public static void e(String subTag, String info) {
        if (!openState) {
            return;
        }
        Log.e(RK_TAG, subTag + ":" + info);
    }

    public static void v(String subTag, String info) {
        if (!openState) {
            return;
        }
        Log.v(RK_TAG, subTag + ":" + info);
    }

}
