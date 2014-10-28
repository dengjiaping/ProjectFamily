package com.chuxin.family.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;

import com.chuxin.family.R;

public class CxLoadingUtil {
	private static CxLoadingUtil instance;
	
	private AlertDialog dlg;
	
	private Activity mCtx;
	
	private boolean isClose = false;
	
	private CxLoadingUtil(){}
	
	public static CxLoadingUtil getInstance(){
		if (null == instance) {
			instance = new CxLoadingUtil();
		}
		return instance;
	}
	
	//interface for extral
	public synchronized void showLoading(Activity ctx, final boolean isCloseable){
//		RkLog.i("***", "showLoading call");
		if (null == ctx) {
			return;
		}
		
		if (!isClose) {
			return;
		}
		
		try {
			dismissLoading();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		isClose = false;
		mCtx = ctx;
		new Handler(ctx.getMainLooper()){
			public void handleMessage(android.os.Message msg) {
				if ( ((null == dlg) || (!dlg.isShowing())) 
						&& (null != mCtx) && (!mCtx.isFinishing())) {
					dlg = new AlertDialog.Builder(mCtx).create();
					if (isClose) {
						isClose = false;
						dlg = null;
						return;
					}
					dlg.show();
//					RkLog.i("***", "showLoading start");
					dlg.setContentView(R.layout.cx_fa_loading);
					dlg.setCancelable(isCloseable);
				}
				
			};
		}.sendEmptyMessage(1);
	}
	
	public synchronized void dismissLoading(){
//		RkLog.i("***", "dismissLoading");
		isClose = true;
		if (null == mCtx) {
			return;
		}
		new Handler(mCtx.getMainLooper()){
			public void handleMessage(android.os.Message msg) {
				if ( (null != dlg) && (dlg.isShowing()) ){
					dlg.dismiss();
					dlg = null;
				}
				
			};
		}.sendEmptyMessage(2);
	}
	
}
