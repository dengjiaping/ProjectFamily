
package com.chuxin.family.calendar;

import com.chuxin.family.app.events.ReminderReceiver;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.models.Reminder;
import com.chuxin.family.net.CalendarApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.reminder.ReminderDisplayUtility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
/**
 * 
 * @author shichao.wang
 *
 */
public class CalendarController {

    private static final String TAG = "CalendarController";

    public static final int sOneDayInSeconds = 3600 * 24;

    public static final int sReminderPeriodOnce = 0;

    public static final int sReminderPeriodDaily = 1;

    public static final int sReminderPeriodWeekly = 2;

    public static final int sReminderPeriodMonthly = 3;

    public static final int sReminderPeriodAnnually = 4;

    public static final int sReminderPeriodCustomize = 5;

    public static final int sReminderTargetMyself = 0;

    public static final int sReminderTargetRelatie = 1;

    public static final int sReminderTargetBoth = 2;

    public static final int sReminderAdvanceNone = 0;

    public static final int sReminderAdvance15Minute = 1;

    public static final int sReminderAdvance1Hour = 2;

    public static final int sReminderAdvance1Day = 3;

    public static final int sReminderAdvance3Day = 4;

    public static final int sReminderAdvance5Day = 5;

    public static final String REMINDER_ID = "remind_id";

    public static final String REMINDER_TITLE = "remind_title";

    public Reminder mReminder;
    
    private CalendarDataObj mCalendarDataObj;

    private class CalendarReminderObj {
        public String mId;

        public int mType;

        public int mTarget;

        public String mContent;

        public int mDayType;

        public int mIsLunar;

        public int mIsRemind;

        public long mBaseTime;

        public int mPeriod;

        public int mAdvance;

        public long mRealTime;
        private int mFlag;

        public CalendarReminderObj() {
        }

        public CalendarReminderObj(String id, int type, int target, String content, int day_type,
                int is_lunar, int is_remind, long base_ts, int cycle, int advance, int flag) {
            mId = id;
            mType = type;
            mTarget = target;
            mContent = content;
            mDayType = day_type;
            mIsLunar = is_lunar;
            mIsRemind = is_remind;
            mBaseTime = base_ts;
            mPeriod = cycle;
            mAdvance = advance;
            mFlag = flag;
        }

        public long getRealTime() throws Exception {
            return CalendarController.getRealTime(mBaseTime, mAdvance);
        }
    };

    public static long getRealTime(long baseTime, int advance) throws Exception {
        switch (advance) {
            case 0:
                return baseTime;
            case sReminderAdvance15Minute:
                return baseTime - (15 * 60 * 1000);
            case sReminderAdvance1Hour:
                return baseTime - (60 * 60 * 1000);
            case sReminderAdvance1Day:
                return baseTime - (24 * 60 * 60 * 1000);
            case sReminderAdvance3Day:
                return baseTime - (3 * 24 * 60 * 60 * 1000);
            case sReminderAdvance5Day:
                return baseTime - (5 * 24 * 60 * 60 * 1000);
            default:
                throw new Exception("Error: invalid advance type " + advance);
        }
    }

    public void submitCalendarChanges(
            Context context, JSONCaller callback) throws Exception {
        if (mData.mId.length() == 0) {
            // create one new reminder
        	
        	CxLog.i("CalendarController_men", "mCalendarId:"+""+",type:"+mData.mType+",itemTarget:"+mData.mTarget+
        			",memorialType:"+mData.mDayType+",memorialLunar:"+mData.mIsLunar+",itemReminderFlag:"+mData.mIsRemind
        			+",itemCycle:"+mData.mPeriod+",itemAdvance:"+mData.mAdvance);
        	
            CalendarApi.getInstance().doCreateCalendar(mData.mType, mData.mTarget, mData.mContent, mData.mDayType, mData.mIsLunar,
                    mData.mIsRemind, (int)(mData.mBaseTime / 1000), mData.mPeriod, mData.mAdvance, callback);
        } else {
        	CxLog.i("CalendarController_men", "mCalendarId:"+mData.mId+",type:"+mData.mType+",itemTarget:"+mData.mTarget+
        			",memorialType:"+mData.mDayType+",memorialLunar:"+mData.mIsLunar+",itemReminderFlag:"+mData.mIsRemind
        			+",itemCycle:"+mData.mPeriod+",itemAdvance:"+mData.mAdvance);
            // update one existing reminder
            CalendarApi.getInstance().doUpdateCalendar(mData.mId, mData.mType, mData.mTarget, mData.mContent, mData.mDayType, mData.mIsLunar,
                    mData.mIsRemind, (int)(mData.mBaseTime / 1000), mData.mPeriod, mData.mAdvance, callback);
        }
    }

    private String mName;

    private String mSerilizeFilePath;

    private CalendarReminderObj mData = null;

    public void reset() {
        
    }

    private static CalendarController sInstance = new CalendarController();

    public static CalendarController getInstance() {
        return sInstance;
    }
    
    public void setData(String id, int type, int target, String content,
            int day_type, int is_lunar, int is_remind, long base_ts, int cycle, int advance){
        mData = new CalendarReminderObj(id, type, target, content, day_type, is_lunar, is_remind, base_ts, cycle, advance,(int)(base_ts/1000));
        setId(id);
        setType(type);
        setTarget(target);
        setContent(content);
        setDayType(day_type);
        setIsLunar(is_lunar);
        setIsRemind(is_remind);
        setTime(base_ts);
        setPeriod(cycle);
        setAdvance(advance);
        mData.mFlag = (int)(base_ts/1000);
    }
    public void setData(CalendarDataObj obj){
        mCalendarDataObj = obj;
    }
    
    public CalendarDataObj getData(){
        return mCalendarDataObj;
    }

    public String getId(){
        return mData.mId;
    }
    
    public void setId(String id){
        mData.mId = id;
    }
    
    public int getType(){
        return mData.mType;
    }
    
    public void setType(int type){
        mData.mType = type;
    }
    
    public int getTarget(){
        return mData.mTarget;
    }
    
    public void setTarget(int target){
        mData.mTarget = target;
    }
    
    public String getContent(){
        return mData.mContent;
    }
    
    public void setContent(String content){
        mData.mContent = content;
    }
    
    public int getDayType(){
        return mData.mDayType;
    }
    
    public void setDayType(int day_type){
        mData.mDayType = day_type;
    }
    
    public int getIsLunar(){
        return mData.mIsLunar;
    }
    
    public void setIsLunar(int is_lunar){
        mData.mIsLunar = is_lunar;
    }
    
    public int getIsRemind(){
        return mData.mIsRemind;
    }
    
    public void setIsRemind(int is_remind){
        mData.mIsRemind = is_remind;
    }
    
    
    public int getPeriod() {
        return mData.mPeriod;
    }

    public void setPeriod(int period) {
        if (mData.mPeriod != period) {
            mData.mPeriod = period;
        }
    }

    public long getTime() {
        return mData.mBaseTime;
    }

    public void setTime(long time) {
        mData.mBaseTime = time;
    }

    public int getAdvance() {
        return mData.mAdvance;
    }

    public void setAdvance(int advance) {
        mData.mAdvance = advance;
    }
    
    public long getRealTime() throws Exception{
        return mData.getRealTime();
    }
    public int getFlag(){
        return mData.mFlag;
    }

    /**
     * set alarm reminder
     * 
     * @param realtime
     */
    public void setAlarmReminder(Context context, int realtime, String remindId, int periodType,
            int flag, String title) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(REMINDER_ID, remindId);
        intent.putExtra(REMINDER_TITLE, title);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, flag);
        // realtime -= realtime % 60;// 秒位归零。
        long time = (long)realtime * 1000;

        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!setAlarmReminder:" + remindId);

        String temprealstrx = ReminderDisplayUtility.getDate((long)time);
        int now = (int)(new Date().getTime() / 1000);
        String tempnowstrx = ReminderDisplayUtility.getDate((long)now * 1000);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        switch (periodType) {
            case sReminderPeriodOnce:
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
                break;
            case sReminderPeriodDaily:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, (24 * 60 * 60 * 1000), pi);
                break;
            case sReminderPeriodMonthly:
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
                break;
            case sReminderPeriodWeekly:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, (7 * 24 * 60 * 60 * 1000),
                        pi);
                break;
            case sReminderPeriodAnnually:
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
                break;
            case sReminderPeriodCustomize:
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
                break;
        }
    }

    /**
     * cancel alarm reminder
     */
    public void cancelAlarmReminder(Context context, int flag) {
        if (null != context) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, flag);
            AlarmManager alarmManager = (AlarmManager)context
                    .getSystemService(context.ALARM_SERVICE);
            alarmManager.cancel(pi);
        }
    }

    public int adjustTime() {
        try {
            int now = (int)(new Date().getTime() / 1000);
            now -= (now % 60);

            int advance = getAdvance();
            int periodType = getPeriod();

            int realTimestamp = (int)(getRealTime(getTime(), advance) / 1000);
            Log.d(TAG, "realTimestamp=" + realTimestamp + "|now=" + now);
            if (realTimestamp < now) {
                if (periodType == sReminderPeriodOnce) {
                    return -1; // invalid
                } else {
                    Calendar nowCalendar = Calendar.getInstance();
                    Calendar realCalendar = Calendar.getInstance();
                    realCalendar.setTimeInMillis(((long)realTimestamp) * 1000);
                    switch (periodType) {
                        case sReminderPeriodDaily:
                            while (!realCalendar.after(nowCalendar))
                                realCalendar
                                        .set(Calendar.DATE, realCalendar.get(Calendar.DATE) + 1);
                            break;
                        case sReminderPeriodWeekly:
                            while (!realCalendar.after(nowCalendar))
                                realCalendar
                                        .set(Calendar.DATE, realCalendar.get(Calendar.DATE) + 7);
                            break;
                        case sReminderPeriodMonthly:
                            while (!realCalendar.after(nowCalendar))
                                realCalendar.set(Calendar.MONTH,
                                        realCalendar.get(Calendar.MONTH) + 1);
                            break;
                        case sReminderPeriodAnnually:
                            while (!realCalendar.after(nowCalendar))
                                realCalendar
                                        .set(Calendar.YEAR, realCalendar.get(Calendar.YEAR) + 1);
                            break;
                        default:
                            break;
                    }

                    setTime(realCalendar.getTimeInMillis());
                    return 1; // adjusted
                }
            } else {
                return 0; // no need to do adjust;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * check settime is legal
     * 
     * @return true:legal， false:illegal
     */
    public boolean checkTimeIsLegal() {
        try {
            int now = (int)(new Date().getTime() / 1000);
            now -= (now % 60);

            int advance = getAdvance();
            int periodType = getPeriod();

            int realTimestamp = (int)(getRealTime(getTime(), advance) / 1000);
            Log.d(TAG, "realTimestamp=" + realTimestamp + "|now=" + now);
            // if (realTimestamp < now) {
            // if (periodType == sReminderPeriodOnce) {
            // return -1; // invalid
            // } else {
            Calendar nowCalendar = Calendar.getInstance();
            Calendar realCalendar = Calendar.getInstance();
            realCalendar.setTimeInMillis(((long)realTimestamp) * 1000);
            switch (periodType) {
                case sReminderPeriodDaily:
                    while (!realCalendar.after(nowCalendar))
                        realCalendar.set(Calendar.DATE, realCalendar.get(Calendar.DATE) + 1);
                    break;
                case sReminderPeriodWeekly:
                    while (!realCalendar.after(nowCalendar))
                        realCalendar.set(Calendar.DATE, realCalendar.get(Calendar.DATE) + 7);
                    break;
                case sReminderPeriodMonthly:
                    while (!realCalendar.after(nowCalendar))
                        realCalendar.set(Calendar.MONTH, realCalendar.get(Calendar.MONTH) + 1);
                    break;
                case sReminderPeriodAnnually:
                    while (!realCalendar.after(nowCalendar))
                        realCalendar.set(Calendar.YEAR, realCalendar.get(Calendar.YEAR) + 1);
                    break;
                default:
                    break;
            }

            // setTime(realCalendar.getTimeInMillis());
            int realTime = (int)(realCalendar.getTimeInMillis() / 1000);
            int nowTime = (int)(System.currentTimeMillis() / 1000);
            if (realTime > nowTime) {
                return true;
            }
            // return realCalendar.getTimeInMillis(); // adjusted
            // }
            // }
            // else {
            // return 0; // no need to do adjust;
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
