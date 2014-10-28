package com.chuxin.androidpush.sdk.push.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.chuxin.androidpush.sdk.push.PushService;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class PushMajor {
	private List<PushPackageInfo> mPushPackages = new ArrayList<PushPackageInfo>();
	private Context mBindingContext = null;
	private static final String TAG = "PushMajor";
			
	private class PushPackageInfo implements Comparable<PushPackageInfo> {
		public int mPushVersion;
		public String mPackageName;
		public String mClassName;
		
		public PushPackageInfo(int version, String packageName, String className) {
			mPackageName = packageName;
			mClassName = className;
			mPushVersion = version;
		}
		
		@Override
		public int compareTo(PushPackageInfo another) {
			int compare = mPushVersion - another.mPushVersion;
			if (compare > 0)
				return 1;
			if (compare == 0)
				return mPackageName.compareTo(another.mPackageName);
			return -1;
		} 
	};
	
	public PushMajor(Context context) {
		mBindingContext = context;
		harvestAllPushs();
	}

	public ComponentName getMajorPush() {
		TeeLog.e(TAG, "mPushPackages.size()："+mPushPackages.size());
		if (mPushPackages.size() > 0) {
			PushPackageInfo info  = mPushPackages.get(mPushPackages.size() - 1);
			return new ComponentName(info.mPackageName, info.mClassName);
		}
		return null;
	}
	
	public ComponentName getCandidatePush() {
		if (mPushPackages.size() > 1) {
			PushPackageInfo info  = mPushPackages.get(mPushPackages.size() - 2);
			return new ComponentName(info.mPackageName, info.mClassName);
		}
		return null;
	}
	
	private void harvestAllPushs() {
		Intent test = new Intent();
		test.setAction(Constant.PUSH_SERVICE_ACTION);
		
		List<ResolveInfo> list = mBindingContext.getPackageManager().queryIntentServices(test, 0);
		Iterator<ResolveInfo> itor = list.iterator();
		
		String packageName = mBindingContext.getPackageName(); 
		String className;
		int sdkVersion = Constant.PUSH_VERSION;
//		TeeLog.d(TAG,  packageName + "/" + Constant.PUSH_SERVICE_CLASS_NAME + ": sdkVersion = " + sdkVersion);
		if (sdkVersion <= 0)
			return;
		
		List<PushPackageInfo> packages = new ArrayList<PushPackageInfo>();
//		packages.add(new PushPackageInfo(sdkVersion, packageName, Constant.PUSH_SERVICE_CLASS_NAME));
		
		while (itor.hasNext()) {
			ResolveInfo info = itor.next();
			
			packageName = info.serviceInfo.packageName;
			className = info.serviceInfo.name;

			ApplicationInfo appInfo = null;
			try {
				appInfo = mBindingContext.getPackageManager()
								.getApplicationInfo(packageName,
										PackageManager.GET_META_DATA);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				TeeLog.w(TAG, "Cannot retrieve package info from system for the package " + packageName);
				continue;
			}
			
			Bundle metaData = appInfo.metaData;
			if (metaData == null) {
				TeeLog.w(TAG, "Cannot retrieve meta info from system for the package " + packageName);
				continue;
			}
			
			sdkVersion = metaData.getInt(Constant.PUSH_VERSION_META, 0);			
//			TeeLog.d(TAG, packageName + "/" + className + ": sdkVersion = " + sdkVersion);
			if (sdkVersion <= 0)
				continue;
			
			packages.add(new PushPackageInfo(sdkVersion, packageName, className));
		}

		if (packages.size() > 0) {
			Collections.sort(packages);
		}
		
		mPushPackages = packages;
	}
	
	public ComponentName getPreferPush() {
		ComponentName major = getMajorPush();
		ComponentName councillor = getCandidatePush();
		
		if (major == null) {
			TeeLog.e(TAG, "Error: no PUSH service can be used.");
			return null;
		}
		
		if (councillor != null) {
			TeeLog.i(TAG, "Stopping push service: " + councillor.getPackageName() + "/" + councillor.getClassName());
			PushService.stop(mBindingContext, councillor); 
		}
	
		return major;
	}
}
