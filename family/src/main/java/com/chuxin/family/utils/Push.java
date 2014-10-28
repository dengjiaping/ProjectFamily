package com.chuxin.family.utils;

import android.content.Context;

import com.chuxin.androidpush.sdk.push.RKPush;

public class Push {
	public static RKPush getInstance(Context context) {
		return RKPush.getInstance(context, "FaMiLy@REKOO");
	}
}
