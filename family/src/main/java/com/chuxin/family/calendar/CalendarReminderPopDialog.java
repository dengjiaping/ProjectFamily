
package com.chuxin.family.calendar;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.models.CalendarRemindList;
import com.chuxin.family.models.Model;
import com.chuxin.family.net.CalendarApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * reminder pop dialog
 * 
 * @author wangshichao
 */
public class CalendarReminderPopDialog extends CxRootActivity implements OnClickListener {

    private static final String TAG = "ReminderPopDialog";

    public static final int NOTIFICATION_ID = 1;

    private MediaPlayer mMusic = null;

    private ImageButton mRemindTurnOffBtn;

    private String mRemindId;

    private TextView mRemindTitle;

    private TextView mRemindDateAndTime;

    private String mReminderTitle;

    private String mReminderDateAndTime;

    private CalendarDisplayUtility mDisplayUtility = null;

    private CalendarDataObj mReminderDataObj;

    private NotificationManager nm;

    private CxImageView mRoleOwnerImageIcon;

    @SuppressWarnings("deprecation")
    private void runTextNotify() {

        // getRemindData();

        Context context = getBaseContext();
        ApplicationInfo appInfo = context.getApplicationInfo();
        int iconRes = appInfo.icon;
        int labelRes = appInfo.labelRes;
        ComponentName launchComponent = getPackageManager().getLaunchIntentForPackage(
                getPackageName()).getComponent();

        String title = getResources().getString(labelRes);
        long timestamp = Calendar.getInstance().getTimeInMillis();
        String body = mReminderTitle;
        CxLog.i(TAG, "mReminderTitle=" + mReminderTitle);
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/rkclock_strike");

        NotificationManager notifyManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notify = new Notification(iconRes, body, timestamp);

        notify.defaults = (Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        notify.flags = Notification.FLAG_AUTO_CANCEL;
        notify.sound = soundUri;
        // notify.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI,
        // "20");

        Intent intent = new Intent().setComponent(launchComponent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notify.setLatestEventInfo(context, title, body, pendingIntent);

        notifyManager.notify(0, notify);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_fa_dialog_reminder_pop);
        mRemindId = this.getIntent().getExtras().getString(CalendarController.REMINDER_ID);
        mReminderTitle = this.getIntent().getExtras().getString(CalendarController.REMINDER_TITLE);
        CxLog.i(TAG, "mRemindId=" + mRemindId);
        PowerManager pm = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        WakeLock wakelock = pm
                .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
        initMusic();
        playMusic();
        if (!wakelock.isHeld()) {
            wakelock.acquire();
        }
        if (null == CxCalendarFragment.calendarHandler) {
            CxLog.i(TAG, "rk reminderlist come in");
            runTextNotify();
            finish();
            return;
        }
        mRemindTurnOffBtn = (ImageButton)findViewById(R.id.remindTurnOffBtn);
        mRemindTitle = (TextView)findViewById(R.id.remindTitle);
        mRemindDateAndTime = (TextView)findViewById(R.id.remindDateAndTime);
        mRemindTurnOffBtn.setOnClickListener(this);
        mDisplayUtility = new CalendarDisplayUtility(getResources());
        mRoleOwnerImageIcon = (CxImageView)findViewById(R.id.remindHeadImage);

        getRemindData();
        fillData();
    }

    /**
     * 填充数据
     */
    private void fillData() {
        mRemindTitle.setText(mReminderTitle);
        mRemindDateAndTime.setText(mReminderDateAndTime);
    }

    /**
     * 获取当前提醒数据
     */
    private void getRemindData() {
        try {
            List<Model> remindlist = new CalendarRemindList(null, CalendarReminderPopDialog.this)
                    .gets("1=1", new String[] {}, null, 0, 0);
            if (null != remindlist) {
                CalendarRemindList crl = (CalendarRemindList)remindlist.get(0);
                JSONArray cja = crl.getRemindArray();
                if (null != cja && cja.length() > 0) {
                    for (int i = 0; i < cja.length(); i++) {
                        JSONObject cjo = cja.getJSONObject(i);
                        CalendarDataObj cdo = new CalendarDataObj(cjo,
                                CalendarReminderPopDialog.this);
                        if (cdo.getId().equals(mRemindId)) {
                            mReminderDataObj = cdo;
                            break;
                        }
                    }
                }
            }
            if (TextUtils.equals(mReminderDataObj.getAuthor(), CxGlobalParams.getInstance()
                    .getUserId())) { // 属于自己创建
                mRoleOwnerImageIcon.displayImage(imageLoader, CxGlobalParams.getInstance()
                        .getIconSmall(), R.drawable.cx_fa_hb_icon_small, true, CxGlobalParams
                        .getInstance().getSmallImgConner());
            } else { // 对方创建
                mRoleOwnerImageIcon.displayImage(imageLoader, CxGlobalParams.getInstance()
                        .getPartnerIconBig(), R.drawable.cx_fa_wf_icon_small, true, CxGlobalParams
                        .getInstance().getMateSmallImgConner());
            }

            mReminderTitle = mReminderDataObj.getContent();
            mReminderDateAndTime = mDisplayUtility
                    .createNLSReminderPeriodLabelForPopDialog(mReminderDataObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMusic() {
        try {
            mMusic = MediaPlayer.create(this, R.raw.rkclock_strike);
            mMusic.setLooping(false);
            mMusic.setWakeMode(getBaseContext(), NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        if (null == mMusic)
            return;

        try {
            if (!mMusic.isPlaying()) {
                mMusic.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        if (null == mMusic)
            return;

        try {
            if (mMusic.isPlaying()) {
                mMusic.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMusic != null) {
            mMusic.stop();
            mMusic.release();
        }
    }

    // @Override
    // protected void onPause() {
    // super.onPause();
    // pauseMusic();
    // }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remindTurnOffBtn:
                if (mReminderDataObj.getCycle() == CalendarController.sReminderPeriodOnce) {
                    CalendarApi.getInstance().doUpdateCalendar(mReminderDataObj.getId(),
                            2, new JSONCaller() { // status 2 提醒已过期

                                @Override
                                public int call(Object result) {
                                    if (null != CxCalendarFragment.calendarHandler) {
                                        Message calendarMessage = CxCalendarFragment.calendarHandler
                                                .obtainMessage(CxCalendarFragment.getInstance().REFRESH_CALENDAR_DATA);
                                        calendarMessage.sendToTarget();
                                    }
                                    return 0;
                                }
                            });
                } else {
                    if (null != CxCalendarFragment.calendarHandler) {
                        Message calendarMessage = CxCalendarFragment.calendarHandler
                                .obtainMessage(CxCalendarFragment.getInstance().LONGPOLLING_REFRESH_DATA);
                        calendarMessage.sendToTarget();
                    }
                }
                finish();
                break;
        }
    }

}
