package com.chuxin.androidpush.sdk.push;

import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.InternalStorage;
import com.chuxin.androidpush.sdk.push.utils.PushMajor;
import com.chuxin.androidpush.sdk.push.utils.RkPushLog;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;
import com.chuxin.androidpush.sdk.push.utils.UUID;
import com.chuxin.androidpush.sdk.push.utils.Utilities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class RKPush {
	public static boolean S_SEND_FLAG = true; //默认发送push
	public static Uri S_NOTIFY_SOUND_URI;
	
	static public class NotificationData {
		public String mMessage;
		public int mBadge;
		public long mTimestamp;
		public String mSound;
		public String mExtras;

		public static NotificationData parse(Bundle bundle) {
			if (bundle == null)
				return null;
			
			String hash = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_HASH);
			if (hash == null)
				return null;

			String message = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_MESSAGE);
			if (message == null)
				return null;
			
			long timestamp = bundle.getLong(Constant.NOTIFY_INTENT_ARGS_FIELD_TIMESTAMP);
			int badge = bundle.getInt(Constant.NOTIFY_INTENT_ARGS_FIELD_BADGE);
			String sound = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_SOUND);
			String extras = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_EXTRAS);
			
			String sign = Utilities.sign(message, timestamp, badge);
			
	    	if ((sign != null) && !hash.equals(sign)) {
	    		return null;
	    	}

			NotificationData result = new NotificationData();
			result.mMessage = message;
			result.mExtras = extras;
			result.mBadge = badge;
			result.mSound = sound;
			result.mTimestamp = timestamp;
			return result;
		}
	};
	
	private static final String TAG = "Push";
	private Context mPackageContext;
	private String mName;
	private String mSecret;
	private String mDeviceToken;
	private PushMajor mMajor;
	
	private static RKPush sInstance = null;
	public static String DEVICE_ID = null;
	
	public RKPush(Context context, String secret) {
		mSecret = secret;
		mName = context.getPackageName();
		
		mPackageContext = context.getApplicationContext();
		
		DEVICE_ID = UUID.devUUID(mPackageContext);
		if (null == DEVICE_ID) {
			DEVICE_ID = "DEVICE_ID"+System.currentTimeMillis();
		}
		mDeviceToken = UUID.appUUID(mPackageContext, mName, secret);
		if (null == mDeviceToken) {
			mDeviceToken = "DeviceToken"+System.currentTimeMillis();
		}
		
		sInstance = this;
		
		// capture the carsh backtrace;
		TeeLog.init(context);
	}

	public static RKPush getInstance(Context context, String secret) {
		if (sInstance == null) {
			sInstance = new RKPush(context, secret);
		}
		return sInstance;
	}
	
	public void setNotificationType(String type) {
		try {
			// remove current all notifications
			((NotificationManager)mPackageContext
					.getSystemService(Context.NOTIFICATION_SERVICE))
						.cancelAll();
		} catch (Exception e) {
			;
		}
		
		new InternalStorage(mPackageContext).setNotificationType(type);
	}
	
	public String[] getNotificationType() {
		try {
			// remove current all notifications
			((NotificationManager)mPackageContext
					.getSystemService(Context.NOTIFICATION_SERVICE))
						.cancelAll();
		} catch (Exception e) {
			;
		}
		
		return new InternalStorage(mPackageContext).getNotificationType();
	}
	
	private boolean verify() {
		int constVersion = Constant.PUSH_VERSION;
		int metaVersion = 0;

		ApplicationInfo appInfo = null;
		try {
			appInfo = mPackageContext.getPackageManager()
						.getApplicationInfo(mPackageContext.getPackageName(),
							PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			RkPushLog.i(Constant.ERROR_CODE_NO_THIS_PACKAGE_INFO, "Android error!");
			return false;
		}
		
		Bundle metaData = appInfo.metaData;
		
		if (metaData == null) {
			RkPushLog.i(Constant.ERROR_CODE_NO_VALID_META_ENTRY, "Please check your \"meta-data\" in the \"AndroidManifest.xml\".");
			return false;
		}
		
		metaVersion = metaData.getInt(Constant.PUSH_VERSION_META, 0);
		
		if (constVersion != metaVersion) {
			RkPushLog.i(Constant.ERROR_CODE_NO_VALID_META_VALUE, "Please check your \"meta-data\" in the \"AndroidManifest.xml\".");
			return false;
		} else {
			RkPushLog.i(TAG, "SDK(" + constVersion + ") has been verified");
			return true;			
		}
	}
	
	//对外调用注册push
	public String registerForRemoteNotification() {
		if (!verify()) {
			return null;
		}
		
		mMajor = new PushMajor(mPackageContext);

		ComponentName major = mMajor.getPreferPush();
		if (major == null)
			return null;

		SystemEventsHandler.triggerNextAlarm(mPackageContext);
		
		RkPushLog.i(TAG, "SregisterForRemoteNotification"+major.toString());
		
		PushService.launch(mPackageContext, major, false, 0);
		if (null == mDeviceToken) {
			mDeviceToken = "mDeviceToken"+System.currentTimeMillis();
		}
		return mDeviceToken;
	}
	
}
