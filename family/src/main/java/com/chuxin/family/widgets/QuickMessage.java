package com.chuxin.family.widgets;

import android.content.Context;
import android.widget.Toast;

public class QuickMessage {
	public static void error(Context context, int resId) {
		Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
	}
	
	public static void info(Context context, int resId) {
		Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
	}
}
