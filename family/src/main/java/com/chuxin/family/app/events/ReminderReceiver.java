package com.chuxin.family.app.events;

import com.chuxin.family.calendar.CalendarReminderPopDialog;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.reminder.ReminderController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 
 * @author shichao.wang
 *
 */
public class ReminderReceiver extends BroadcastReceiver{

	private static final String TAG = "ReminderReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
	  CxLog.i("", "the time is up,start the alarm...");  
//      Toast.makeText(context, "闹钟时间到了！", Toast.LENGTH_SHORT).show();
      String remindId = intent.getExtras().getString(ReminderController.REMINDER_ID);
      String remindTitle = intent.getExtras().getString(ReminderController.REMINDER_TITLE);
      CxLog.i(TAG, "remindId=" + remindId);
      Intent reminderIntent = new Intent(context, CalendarReminderPopDialog.class);
      reminderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      reminderIntent.putExtra(ReminderController.REMINDER_ID, remindId);
      reminderIntent.putExtra(ReminderController.REMINDER_TITLE, remindTitle);
      context.startActivity(reminderIntent);
	}

}
