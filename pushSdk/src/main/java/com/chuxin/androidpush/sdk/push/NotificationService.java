package com.chuxin.androidpush.sdk.push;

import java.io.File;
import java.util.Calendar;

import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.InternalStorage;
import com.chuxin.androidpush.sdk.push.utils.RkPushLog;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;
import com.chuxin.androidpush.sdk.push.utils.Utilities;



import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class NotificationService extends IntentService {

	private static final String TAG = "Push";

	public NotificationService() {
		super(String.valueOf(Calendar.getInstance().getTimeInMillis()));
	}
		
	private Intent fillData(Intent intent, long timestamp, String message, int badge, String sound, String extras) {
		String sign = Utilities.sign(message, timestamp, badge);
		intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_TIMESTAMP, timestamp)
			  .putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_MESSAGE, message)
			  .putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_BADGE, badge)
			  .putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_HASH, sign);
		if (sound != null)
			intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_SOUND, sound);
		if (extras != null)
			intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_EXTRAS, extras);
		return intent;
	}
	
	private void runNotification(long timestamp, String body, int badge, String sound, String extras, boolean broadcast) {
		String[] notificationTypes = new InternalStorage(this).getNotificationType();
		Intent intent = null;
		for (String type : notificationTypes) {
			if (type.equals("DEFAULT")) {
				runDefaultNotification(timestamp, body, badge, sound, extras, broadcast);
//				runStandardNotificaton(timestamp, body, badge, sound, extras);
			}
//            else if (type.equals("QUIET")) {
//
//			}
            else if (type.startsWith(":")) {
				intent = new Intent().setComponent(new ComponentName(getPackageName(), type.substring(1)));
		        fillData(intent, timestamp, body, badge, sound, extras);
		        startService(intent);
			} else {
				intent = new Intent(type);
			    fillData(intent, timestamp, body, badge, sound, extras);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);			
	        }
		}
	}
	
//	private void runStandardNotificaton(long timestamp, String body, int badge, String sound, String extras) {
//		Context context = getBaseContext();
//		ApplicationInfo appInfo = context.getApplicationInfo();
//		int iconRes = appInfo.icon;
//		int labelRes = appInfo.labelRes;
//		ComponentName launchComponent = getPackageManager()
//        		.getLaunchIntentForPackage(getPackageName()).getComponent();
//
//		String title = getResources().getString(labelRes);
//		Uri soundUri = null;
//		
//		if ((sound == null) || (sound.equals("default"))) {
//			soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		} else {
//			soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + sound);
//		}
//
//		NotificationCompat.Builder builder =
//		        new NotificationCompat.Builder(this)
//		        .setSmallIcon(iconRes)
//		        .setContentTitle(title)
//		        .setContentText(body)
//		        .setWhen(timestamp)
//		        .setSound(soundUri)
//		        .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
//		        .setAutoCancel(true)
//		        .setNumber(badge);
//		
//		Intent resultIntent = new Intent().setComponent(launchComponent);
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//		stackBuilder.addParentStack(launchComponent);
//		stackBuilder.addNextIntent(resultIntent);
//		PendingIntent resultPendingIntent =
//		        stackBuilder.getPendingIntent(
//		            0,
//		            PendingIntent.FLAG_UPDATE_CURRENT
//		        );
//		builder.setContentIntent(resultPendingIntent);
//		NotificationManager mNotificationManager =
//		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		mNotificationManager.notify(0, builder.build());
//	}
	
	@SuppressWarnings("deprecation")
	private void runDefaultNotification(long timestamp, String body, int badge, String sound, String extras, boolean broadcast) {
		if (!RKPush.S_SEND_FLAG) { //应用在前台不需要发notification
			RkPushLog.i("push", "push close----------------");
			return;
		}
		RkPushLog.i("push", "push-----------open-----");
		Context context = getBaseContext();
		ApplicationInfo appInfo = context.getApplicationInfo();
		int iconRes = appInfo.icon;
		int labelRes = appInfo.labelRes;
		ComponentName launchComponent = getPackageManager()
        		.getLaunchIntentForPackage(getPackageName()).getComponent();

		String title = getResources().getString(labelRes);
		Uri soundUri = null;
		
		RkPushLog.e("", "push sound:"+sound);
		//add by niechao(reason:产品决定push声音采用系统默认)
		if ((sound == null) || (sound.equals("default"))) {
			soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		} else {
			
			if (sound.startsWith("/")) {
				soundUri = Uri.parse("android.resource://" + getPackageName() + sound);
			} else if (sound.startsWith("raw/")) {
				soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + sound);
			} else {				
				int end = sound.indexOf('.');
				if (end != -1){
					try {
						
						//针对弹脑壳、抽鞭子、和其他信息把声音区别出来
						if (sound.contains("rk_fa_pumpingwhip_push") 
								|| (sound.contains("rk_fa_bulletheads_push"))) {
							soundUri = Uri.parse("android.resource://" 
									+ getPackageName() + "/raw/" + sound.substring(0, end));
						}else{ //除了抽鞭子或弹脑壳以外的信息							
							if(RKPush.S_NOTIFY_SOUND_URI==null){
								
								if (sound.equalsIgnoreCase("w_rk_naughty_push.caf")  ||  sound.equalsIgnoreCase("h_rk_naughty_push.caf")) {
									if(sound.equalsIgnoreCase("w_rk_naughty_push.caf")){
										RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
												+ getPackageName() + "/raw/" + "rk_fa_role_push_first_s");
									}else{
										RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
												+ getPackageName() + "/raw/" + "rk_fa_role_push_first");
									}
									
								}else if(sound.equalsIgnoreCase("w_rk_fa_role_push.caf") || sound.equalsIgnoreCase("h_rk_fa_role_push.caf")){
									
									if(sound.equalsIgnoreCase("w_rk_fa_role_push.caf")){
										RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
												+ getPackageName() + "/raw/" + "rk_fa_role_push_s");
									}else{
										RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
												+ getPackageName() + "/raw/" + "rk_fa_role_push");
									}
								}else{
									RKPush.S_NOTIFY_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
								}
							}

							soundUri = RKPush.S_NOTIFY_SOUND_URI;
							RkPushLog.e("", "push sound:"+soundUri.toString());
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					try {
						soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		RkPushLog.e("", "push sound Uri : "+soundUri);
		if (null == soundUri) {
			soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}
		//soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
        NotificationManager notifyManager = (NotificationManager)context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        
        String tempBbStrSmall = "drawable"+File.separator+"cx_fa_push_icon_small";
		int resIdSmall = getResources().getIdentifier(tempBbStrSmall, null, context.getPackageName());
        
		String tempBbStrBig = "drawable"+File.separator+"cx_fa_push_icon_big";
		int resIdBig = getResources().getIdentifier(tempBbStrBig, null, context.getPackageName());
		
		Intent intent = new Intent().setComponent(launchComponent);
        
        fillData(intent, timestamp, body, badge, sound, extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification notify=null;
		
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB){
			notify = new Notification(resIdSmall,body,timestamp);
			notify.defaults = (Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
	        notify.flags = Notification.FLAG_AUTO_CANCEL;
	        notify.number = badge;
	        notify.sound = soundUri;

	        notify.setLatestEventInfo(context, title,body,pendingIntent);
	        
	        RkPushLog.e("", "push sound Uri : "+notify.sound.toString());
		}else{
			Notification.Builder builder=new Builder(context);
			builder.setTicker(body)
			.setContentText(body)
			.setContentTitle(title)
			.setContentIntent(pendingIntent)
			.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
			.setNumber(badge)
			.setSound(soundUri)
			.setAutoCancel(true)
	        .setWhen(timestamp)
	        .setSmallIcon(resIdSmall)
	        .setLargeIcon(BitmapFactory.decodeResource(getResources(), resIdBig));
			notify=builder.getNotification();
		}
		
//		AudioManager  systemService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

//        int STREAM_MUSIC = systemService.getStreamVolume(AudioManager.STREAM_MUSIC);
//        RkPushLog.i("NotificationService_men", "men>>>>>>>STREAM_MUSIC"+STREAM_MUSIC);
//        int STREAM_SYSTEM = systemService.getStreamVolume(AudioManager.STREAM_SYSTEM);
//        RkPushLog.i("NotificationService_men", "men>>>>>>>STREAM_SYSTEM"+STREAM_SYSTEM);
//        int STREAM_NOTIFICATION = systemService.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//        RkPushLog.i("NotificationService_men", "men>>>>>>>STREAM_NOTIFICATION"+STREAM_NOTIFICATION);
//        int STREAM_DTMF = systemService.getStreamVolume(AudioManager.STREAM_DTMF);
//        RkPushLog.i("NotificationService_men", "men>>>>>>>STREAM_DTMF"+STREAM_DTMF);
//		
//        int volume = systemService.getStreamVolume(AudioManager.STREAM_SYSTEM);
//        systemService.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
//
//		int STREAM_NOTIFICATION2 = systemService.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//		RkPushLog.i("NotificationService_men", "men>>>>>>>STREAM_NOTIFICATION2"+STREAM_NOTIFICATION2);
        
        if (broadcast) {
        	notifyManager.notify(1, notify);
        } else {
        	notifyManager.notify(0, notify);
        }
	}
	
	private boolean checkNotify(Intent intent) {
		
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			TeeLog.w(TAG, "Invalid Notify intent.");
			return false;
		}

		int msgid = bundle.getInt(Constant.NOTIFY_INTENT_ARGS_FIELD_MSGID);
		int timestamp = bundle.getInt(Constant.NOTIFY_INTENT_ARGS_FIELD_TIMESTAMP);
		String message = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_MESSAGE);
		int badge = bundle.getInt(Constant.NOTIFY_INTENT_ARGS_FIELD_BADGE);
		String sound = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_SOUND);
		String extras = bundle.getString(Constant.NOTIFY_INTENT_ARGS_FIELD_EXTRAS);

		TeeLog.d(TAG, "Bundle data: msgid = " + msgid);
		TeeLog.d(TAG, "Bundle data: timestamp = " + timestamp);
		TeeLog.d(TAG, "Bundle data: message = " + message);
		TeeLog.d(TAG, "Bundle data: badge = " + badge);
		TeeLog.d(TAG, "Bundle data: sound = " + sound);
		TeeLog.d(TAG, "Bundle data: extras = " + extras);

		if ((timestamp == 0) || (message == null))
			return false;
		
		InternalStorage is = new InternalStorage(this);
		if (msgid >= 0) {
			if (is.getLastMsgId() >= msgid) {
				TeeLog.w(TAG, "Warn: the msg (" + msgid + ")" + message + " has been received before (). The current max msg id =" + is.getLastMsgId());
				return false;
			}
			is.setLastMsgId(msgid);
			runNotification(((long)timestamp) * 1000, message, badge, sound, extras, false);
		} else {
			if (is.hasReceivedThisBroadcastMessage(msgid)) {
				TeeLog.w(TAG, "Warn: the msg (" + msgid + ")" + message + " has been received before ().");
				return false;
			}
			
			is.receivedTheBroadcastMessage(msgid);
			runNotification(((long)timestamp) * 1000, message, badge, sound, extras, true);
		}
		
		
		return true;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// capture the carsh backtrace;
		TeeLog.d(TAG, "onHandleIntent()");
		TeeLog.init(this);
		checkNotify(intent);		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
