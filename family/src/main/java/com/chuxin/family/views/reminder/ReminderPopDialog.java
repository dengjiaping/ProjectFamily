//
//package com.chuxin.family.views.reminder;
//
//import com.chuxin.family.app.RkRootActivity;
//import com.chuxin.family.global.RkGlobalParams;
//import com.chuxin.family.models.Reminder;
//import com.chuxin.family.utils.RkLog;
//import com.chuxin.family.widgets.RkImageView;
//import com.chuxin.family.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ApplicationInfo;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Message;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
//import android.provider.MediaStore.Audio;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import java.util.Calendar;
//
///**
// * reminder pop dialog
// * 
// * @author wangshichao
// */
//public class ReminderPopDialog extends RkRootActivity implements OnClickListener {
//
//    private static final String TAG = "ReminderPopDialog";
//
//    public static final int NOTIFICATION_ID = 1;
//
//    private MediaPlayer mMusic = null;
//
//    private ImageButton mRemindTurnOffBtn;
//
//    private ImageButton mRemindTenMinBtn;
//
//    private ImageButton mRemindOneHourBtn;
//
//    private ImageButton mRemindTomorrowBtn;
//
//    private String mRemindId;
//
//    private TextView mRemindTitle;
//
//    private TextView mRemindDateAndTime;
//
//    private String mReminderTitle;
//
//    private String mReminderDateAndTime;
//
//    private ReminderDisplayUtility mDisplayUtility = null;
//
//    private JSONObject mReminderDataObj;
//
//    private NotificationManager nm;
//
//    private RkImageView mRoleOwnerImageIcon;
//
//    @SuppressWarnings("deprecation")
//    private void runTextNotify() {
//
//        // getRemindData();
//
//        Context context = getBaseContext();
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        int iconRes = appInfo.icon;
//        int labelRes = appInfo.labelRes;
//        ComponentName launchComponent = getPackageManager().getLaunchIntentForPackage(
//                getPackageName()).getComponent();
//
//        String title = getResources().getString(labelRes);
//        long timestamp = Calendar.getInstance().getTimeInMillis();
//        String body = mReminderTitle;
//        RkLog.i(TAG, "mReminderTitle=" + mReminderTitle);
//        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/rkclock_strike");
//
//        NotificationManager notifyManager = (NotificationManager)context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Notification notify = new Notification(iconRes, body, timestamp);
//
//        notify.defaults = (Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
//        notify.flags = Notification.FLAG_AUTO_CANCEL;
//        notify.sound = soundUri;
//        // notify.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI,
//        // "20");
//
//        Intent intent = new Intent().setComponent(launchComponent);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        notify.setLatestEventInfo(context, title, body, pendingIntent);
//
//        notifyManager.notify(0, notify);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.cx_fa_dialog_reminder_pop);
//        mRemindId = this.getIntent().getExtras().getString(ReminderController.REMINDER_ID);
//        mReminderTitle = this.getIntent().getExtras().getString(ReminderController.REMINDER_TITLE);
//        RkLog.i(TAG, "mRemindId=" + mRemindId);
//        PowerManager pm = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
//        WakeLock wakelock = pm
//                .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
//                        | PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
//        initMusic();
//        playMusic();
//        if (!wakelock.isHeld()) {
//            wakelock.acquire();
//        }
//        if (null == RkReminderList.mReminderListAllHandler) {
//            RkLog.i(TAG, "rk reminderlist come in");
//            runTextNotify();
//            finish();
//            return;
//        }
//        mRemindTurnOffBtn = (ImageButton)findViewById(R.id.remindTurnOffBtn);
//        mRemindTenMinBtn = (ImageButton)findViewById(R.id.remindTenMinBtn);
//        mRemindOneHourBtn = (ImageButton)findViewById(R.id.remindOneHourBtn);
//        mRemindTomorrowBtn = (ImageButton)findViewById(R.id.remindTomorrowBtn);
//        mRemindTitle = (TextView)findViewById(R.id.remindTitle);
//        mRemindDateAndTime = (TextView)findViewById(R.id.remindDateAndTime);
//        mRemindTurnOffBtn.setOnClickListener(this);
//        mRemindTenMinBtn.setOnClickListener(this);
//        mRemindOneHourBtn.setOnClickListener(this);
//        mRemindTomorrowBtn.setOnClickListener(this);
//        mDisplayUtility = new ReminderDisplayUtility(getResources());
//        mRoleOwnerImageIcon = (RkImageView)findViewById(R.id.remindHeadImage);
//
//        getRemindData();
//        fillData();
//    }
//
//    /**
//     * 填充数据
//     */
//    private void fillData() {
//        mRemindTitle.setText(mReminderTitle);
//        mRemindDateAndTime.setText(mReminderDateAndTime);
//    }
//
//    /**
//     * 获取当前提醒数据
//     */
//    private void getRemindData() {
//        try {
//            Reminder reminder = (Reminder)new Reminder(null, ReminderPopDialog.this).get(mRemindId);
//            mReminderDataObj = reminder.mData;
//            if (TextUtils.equals(reminder.getAuthor(), RkGlobalParams.getInstance().getUserId())) { // 属于自己创建
//                                                                                                    // cx_fa_hb_icon_small
//            	/*mRoleOwnerImageIcon.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//                mRoleOwnerImageIcon.setImage(RkGlobalParams.getInstance().getIconSmall(), true, 74,
//                        ReminderPopDialog.this, "head", ReminderPopDialog.this);*/
//                mRoleOwnerImageIcon.displayImage(imageLoader, 
//                		RkGlobalParams.getInstance().getIconSmall(), 
//                		R.drawable.cx_fa_hb_icon_small, true, 
//                		RkGlobalParams.getInstance().getSmallImgConner());
//            } else { // 对方创建
//                /*mRoleOwnerImageIcon.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//                mRoleOwnerImageIcon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(),
//                        true, 74, ReminderPopDialog.this, "head", ReminderPopDialog.this);*/
//            	mRoleOwnerImageIcon.displayImage(imageLoader, 
//            			RkGlobalParams.getInstance().getPartnerIconBig(), 
//            			R.drawable.cx_fa_wf_icon_small, true, 
//            			RkGlobalParams.getInstance().getMateSmallImgConner());
//            }
//
//            mReminderTitle = mReminderDataObj.getString(Reminder.TAG_TITLE);
//            mReminderDateAndTime = mDisplayUtility
//                    .createNLSReminderPeriodLabelForPopDialog(reminder);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 创建本地新提醒
//     * 
//     * @param type
//     */
//    private void createNewReminder(int type) {
//        RkLog.d(TAG, "remindDataObj.toString()= " + mReminderDataObj.toString());
//        try {
//            mReminderDataObj.put(Reminder.TAG_DELAY, true);
//            mReminderDataObj.put(Reminder.TAG_BASE_TS,
//                    getDelayRealTimestamp((int)(System.currentTimeMillis() / 1000), type));
//            mReminderDataObj.put(Reminder.TAG_TARGET, ReminderController.sReminderTargetMyself);
//            mReminderDataObj.put(Reminder.TAG_PERIOD_TYPE, ReminderController.sReminderPeriodOnce);
//
//            Reminder reminder = new Reminder(mReminderDataObj, ReminderPopDialog.this);
//            reminder.setFlag(reminder.getBaseTimestamp());
//            reminder.mId = reminder.mId + "_1"; // 延迟提醒,本地reminder
//                                                // id,默认后面加上_1,以免被网络的覆盖
//            reminder.put();
//            ReminderController controller = ReminderController.getInstance();
//            controller.setAlarmReminder(this, reminder.getBaseTimestamp(), reminder.mId,
//                    reminder.getPeriodType(), reminder.getFlag(), reminder.getTitle());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void delReminder() {
//        // Reminder reminder = new Reminder();
//        Reminder reminder = (Reminder)new Reminder(null, ReminderPopDialog.this).get(mRemindId);
//        ReminderController controller = ReminderController.getInstance();
//        if (reminder != null) {
//            controller.cancelAlarmReminder(this, reminder.getFlag());
//            reminder.drop(mRemindId);
//
//            Log.d(TAG, "delReminder" + mRemindId);
//        }
//    }
//
//    private void initMusic() {
//        try {
//            mMusic = MediaPlayer.create(this, R.raw.rkclock_strike);
//            mMusic.setLooping(false);
//            mMusic.setWakeMode(getBaseContext(), NOTIFICATION_ID);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void playMusic() {
//        if (null == mMusic)
//            return;
//
//        try {
//            if (!mMusic.isPlaying()) {
//                mMusic.start();
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void pauseMusic() {
//        if (null == mMusic)
//            return;
//
//        try {
//            if (mMusic.isPlaying()) {
//                mMusic.pause();
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mMusic != null) {
//            mMusic.stop();
//            mMusic.release();
//        }
//    }
//
//    // @Override
//    // protected void onPause() {
//    // super.onPause();
//    // pauseMusic();
//    // }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        playMusic();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    public void onClick(View v) {
//        Message reminderMessage;
//        switch (v.getId()) {
//            case R.id.remindTurnOffBtn:
//                // del this remind in database
//                delReminder();
//                reminderMessage = RkReminderList.mReminderListAllHandler
//                        .obtainMessage(RkReminderList.RELOAD_REMINDER_DATA);
//                reminderMessage.sendToTarget();
//                finish();
//                break;
//            case R.id.remindTenMinBtn:
//                createNewReminder(Reminder.sReminderDelay10Minute);
//                delReminder();
//                reminderMessage = RkReminderList.mReminderListAllHandler
//                        .obtainMessage(RkReminderList.RELOAD_REMINDER_DATA);
//                reminderMessage.sendToTarget();
//                finish();
//                break;
//            case R.id.remindOneHourBtn:
//                createNewReminder(Reminder.sReminderDelayOneHour);
//                delReminder();
//                reminderMessage = RkReminderList.mReminderListAllHandler
//                        .obtainMessage(RkReminderList.RELOAD_REMINDER_DATA);
//                reminderMessage.sendToTarget();
//                finish();
//                break;
//            case R.id.remindTomorrowBtn:
//                createNewReminder(Reminder.sReminderDelayOneDay);
//                delReminder();
//                reminderMessage = RkReminderList.mReminderListAllHandler
//                        .obtainMessage(RkReminderList.RELOAD_REMINDER_DATA);
//                reminderMessage.sendToTarget();
//                finish();
//                break;
//        }
//    }
//
//    public int getDelayRealTimestamp(int now, int type) {
//        switch (type) {
//            case Reminder.sReminderDelay10Minute:
//                return now + (10 * 60);
//            case Reminder.sReminderDelayOneHour:
//                return now + (60 * 60);
//            case Reminder.sReminderDelayOneDay:
//                return now + (24 * 60 * 60);
//            default:
//                return now;
//        }
//    }
//
//}
