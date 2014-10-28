package com.chuxin.androidpush.sdk.push.utils;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

public class InternalStorage {
	private SharedPreferences mStorage;
	private static final String TAG = "InternalStorage";
	private static final String STORAGE_FILE = Constant.SDK_INTERNAL_STORAGE_FILE;
	private static final String FIELD_ALARM_TICK = Constant.SDK_INTERNAL_STORAGE_FIELD_ALARM_TICK;
	private static final String FIELD_LAST_MSGID = Constant.SDK_INTERNAL_STORAGE_FIELD_LAST_MSGID;
	private static final String FIELD_NOTIFICATION_TYPE = Constant.SDK_INTERNAL_STORAGE_FIELD_NOTIFICATION_TYPE;
	private static final String FIELD_LAST_BMSGIDS = Constant.SDK_INTERNAL_STORAGE_FIELD_LAST_BMSGIDS;
	
	public InternalStorage(Context context) {
		context = context.getApplicationContext();
		mStorage = context.getSharedPreferences(STORAGE_FILE, Context.MODE_PRIVATE);
	}

	public void setNotificationType(String type) {
		if (type == null) {
			// set to display (default-mode)
			mStorage.edit().remove(FIELD_NOTIFICATION_TYPE).commit();
		} else {
			mStorage.edit().putString(FIELD_NOTIFICATION_TYPE, type).commit();
		}
	}
	
	public String[] getNotificationType() {
		String type = mStorage.getString(FIELD_NOTIFICATION_TYPE, "DEFAULT");
		return type.split(",");
	}
	
	public void updateAlarmTick() {
		mStorage.edit()
			.putLong(FIELD_ALARM_TICK, Calendar.getInstance().getTimeInMillis())
			.commit();
	}
	
	public long getAlarmTick() {
		return mStorage.getLong(FIELD_ALARM_TICK, 0l);
	}

	public void setLastMsgId(int msgid) {
		TeeLog.d(TAG, "Store msgid " + msgid + " to internal storage.");
		mStorage.edit()
			.putInt(FIELD_LAST_MSGID, msgid)
			.commit();
	}
	
	public int getLastMsgId() {
		return mStorage.getInt(FIELD_LAST_MSGID, 0);
	}
	
	public void receivedTheBroadcastMessage(int messageIdInt) {
		if (messageIdInt >= 0)
			assert(false);
		
		String messageId = String.valueOf(-1 * messageIdInt);
		String broadcastMsgIds = mStorage.getString(FIELD_LAST_BMSGIDS, null);
		StringBuffer sb = new StringBuffer();

		if (broadcastMsgIds != null) {
			String[] receives = broadcastMsgIds.split(",");
			int receivesBegin = 0;
			int receivesEnd = receives.length;
			if (receivesEnd > 2)
				receivesBegin = receivesEnd - 2;
			
			for (int i = receivesBegin; i < receivesEnd; i++) {
				sb.append(receives[i]);
				sb.append(",");
			}
		}
		
		sb.append(messageId);
		mStorage.edit().putString(FIELD_LAST_BMSGIDS, sb.toString()).commit();
	}
	
	
	public boolean hasReceivedThisBroadcastMessage(int messageIdInt) {

		if (messageIdInt >= 0)
			assert(false);
		
		String broadcastMsgIds = mStorage.getString(FIELD_LAST_BMSGIDS, null);
		if (broadcastMsgIds == null)
			return false;
		String messageId = String.valueOf(-1 * messageIdInt);
		
		String[] receives = broadcastMsgIds.split(",");
		int receivesBegin = 0;
		int receivesEnd = receives.length;
		if (receivesEnd > 3)
			receivesBegin = receivesEnd - 3;
		
		for (int i = receivesBegin; i < receivesEnd; i++) {
			if (receives[i].equals(messageId))
				return true;
		}
		
		return false;
	}
}
