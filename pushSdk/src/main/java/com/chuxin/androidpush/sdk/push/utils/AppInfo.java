package com.chuxin.androidpush.sdk.push.utils;

public class AppInfo {
	public String mName;
	public String mUUID;
	public String mPackageName;
	public String mNotifyClassName;
	
	public AppInfo(String name, String uuid, String packageName, String notifyClassName) {
		mName = name;
		mUUID = uuid;
		mPackageName = packageName;
		mNotifyClassName = notifyClassName;
	}
	
	public boolean equals(AppInfo right) {
		return (mName.equals(right.mUUID)
				&& mUUID.equals(right.mUUID) &&
				mPackageName.equals(right.mPackageName) &&
				mNotifyClassName.equals(mNotifyClassName));
	}
};
