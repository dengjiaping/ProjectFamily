package com.chuxin.androidpush.sdk.push;

import java.util.Calendar;

import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.InternalStorage;
import com.chuxin.androidpush.sdk.push.utils.PushMajor;
import com.chuxin.androidpush.sdk.push.utils.RkPushLog;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SystemEventsHandler extends BroadcastReceiver {

//	static final String TAG = "SystemEventsHandler";
	static final String TAG ="Push";
	static final String BOOT_UP_ACTION = "android.intent.action.BOOT_COMPLETED";
	static final int ALARM_INTERVAL = 60 * 5 * 1000;
	static final int ALARM_VALIDATION_THRESHOLD = (ALARM_INTERVAL - 5000);
	
	private boolean amIPreferPackage(Context context) {
		// am I the preferred package ?
		PushMajor major = new PushMajor(context);
		ComponentName majorPush = major.getPreferPush();
		
		if (majorPush == null)
			return false;
		
		if (majorPush.getPackageName().equals(context.getPackageName())) {
			return true;
		}
		
		return false;
	}
	
    private int checkNetworkState(Context context) {
    	ConnectivityManager cm =
    	        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	 
    	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	if (activeNetwork == null) {
    		return 0;	// off
    	}
    	
    	boolean isConnected = activeNetwork.isConnectedOrConnecting();
    	
    	if (!isConnected) {
    		return 0; // off
    	} else {
    		if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
    			return 1;	// WIFI
    		} else {
    			return 2;   // CELL
    		}
    	}
    }

	public static void triggerNextAlarm(Context context) {
		long now = Calendar.getInstance().getTimeInMillis();
		InternalStorage is = new InternalStorage(context);
		if ((now - is.getAlarmTick()) < ALARM_VALIDATION_THRESHOLD)
			return;
		
		is.updateAlarmTick();
		
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND, ALARM_INTERVAL);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, SystemEventsHandler.class);
        intent.setAction(Constant.ALARM_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);        
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean alarmAction = false;
		
		TeeLog.init(context);

		if (intent == null) {
			TeeLog.d(TAG, context.getPackageName() + " intent = null");
			return;
		}

		String action = intent.getAction();
		TeeLog.d(TAG, context.getPackageName() + " Receiving action " + action);
		if (action == null) {
			return;
		}

		if (action.equals(Constant.ALARM_ACTION)) {
			alarmAction = true;
			triggerNextAlarm(context);	
		}

		if (0 == checkNetworkState(context)) {
			// the network is down, we can do nothing;
			return;
		}
		
		
		if (!amIPreferPackage(context))
			return;
		
		if (alarmAction) {
			// timer
			PushService.launch(context, null, false, 2);
		} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			// network changes
			PushService.launch(context, null, true, 3);
			detectNetworkStatus(context);
		} else if (action.equals(BOOT_UP_ACTION)) {
			// bootup action
			PushService.launch(context, null, true, 1);
		}
	}
	
	public void detectNetworkStatus(Context ctx) {
		try {
			ConnectivityManager connectivity = 
				(ConnectivityManager) ctx.getSystemService(
						Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null){
					for (int i = 0; i < info.length; i++)
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							RkPushLog.i(TAG, ""+info[i].getType()+":NetworkInfo.State.CONNECTED");
							return;
						}
				}
			}
			RkPushLog.i(TAG, "no connected network for my app");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			RkPushLog.i(TAG, "detect network status error:"+e.getMessage());
		}
	}

}
