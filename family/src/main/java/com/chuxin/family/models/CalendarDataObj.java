
package com.chuxin.family.models;

import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.reminder.ReminderDisplayUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class CalendarDataObj extends Model {
    public static final int sReminderAdvanceNone = 0;

    public static final int sReminderAdvance15Minute = 1;

    public static final int sReminderAdvance1Hour = 2;

    public static final int sReminderAdvance1Day = 3;

    public static final int sReminderAdvance3Day = 4;

    public static final int sReminderAdvance5Day = 5;

    public static final int sReminderPeriodOnce = 0;

    public static final int sReminderPeriodDaily = 1;

    public static final int sReminderPeriodWeekly = 2;

    public static final int sReminderPeriodMonthly = 3;

    public static final int sReminderPeriodAnnually = 4;

    private static final String TAG = "CalendarDataObj";

    private static final String TAG_ID = "id"; // 当前记录的id

    public static final String TAG_PAIR_ID = "pair_id";

    public static final String TAG_SET_TS = "set_ts";// 设置的时间

    public static final String TAG_TYPE = "type";// 分类0:事项 1:纪念日

    public static final String TAG_CONTENT = "content";// 日历内容

    public static final String TAG_ADVANCE = "advance";// 提前量

    public static final String TAG_TARGET = "target";// 显示对象0:自己 1:对方 2:双方

    public static final String TAG_AUTHOR = "author";// 创建者

    public static final String TAG_CYCLE = "cycle";// 循环周期类型，0：不循环，1：按天，2：按周，3：按月，4：按年

    public static final String TAG_IS_REMIND = "is_remind";// 是否提醒，0:不提醒 1:提醒

    public static final String TAG_STATUS = "status";// 日历状态，0：有效的，1：无效的，2：过期的

    public static final String TAG_IS_READ = "is_read";// 是否已读 0：未读，1：已读

    public static final String TAG_DAY_TYPE = "day_type";// 纪念日类别，0：无 1：生日 2：其它
    
    public static final String TAG_IS_LUNAR = "is_lunar";// 0：公历 1：农历

    public CalendarDataObj() {
        super();
        mTable = "caleandars";
    }

    public CalendarDataObj(JSONObject data, Context context) {
        super();
        mContext = context;
        mTable = "caleandars";
        if (data != null) {
            mData = data;
            mId = getId();
        } else {
            init();
        }
    }
    
    public CalendarDataObj(JSONObject data, String id, Context context){
        mContext = context;
        mTable = "caleandars";
        mId = id;
        if (data != null) {
            mData = data;
            //mId = getId();
        } else {
            init();
        }
    }


    public static int getBaseTimestamp(int realTime, int advance) {
        switch (advance) {
            case sReminderAdvance15Minute:
                return realTime + (15 * 60);
            case sReminderAdvance1Hour:
                return realTime + (60 * 60);
            case sReminderAdvance1Day:
                return realTime + (24 * 60 * 60);
            case sReminderAdvance3Day:
                return realTime + (3 * 24 * 60 * 60);
            case sReminderAdvance5Day:
                return realTime + (5 * 24 * 60 * 60);
            default:
                return realTime;
        }
    }

    public static int getRealTimestamp(int baseTime, int advance) {
        switch (advance) {
            case sReminderAdvance15Minute:
                return baseTime - (15 * 60);
            case sReminderAdvance1Hour:
                return baseTime - (60 * 60);
            case sReminderAdvance1Day:
                return baseTime - (24 * 60 * 60);
            case sReminderAdvance3Day:
                return baseTime - (3 * 24 * 60 * 60);
            case sReminderAdvance5Day:
                return baseTime - (5 * 24 * 60 * 60);
            default:
                return baseTime;
        }
    }

    public int getRealTimestamp() {
        int baseTimestamp = getBaseTimestamp();
        int advance = getAdvance();
        return getRealTimestamp(baseTimestamp, advance);
    }

    public int getBaseTimestamp() {
        try {
            return mData.getInt(TAG_SET_TS);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }
    
    public void setBaseTimestamp(int value) {
        try {
            mData.put(TAG_SET_TS, value);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public int getAdvance() {
        try {
            return mData.getInt(TAG_ADVANCE);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }

    public String getId() {
        try {
            return mData.getString(TAG_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return null;
    }

    public int getType() {
        try {
            return mData.getInt(TAG_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }

    public String getAuthor() {
        try {
            return mData.getString(TAG_AUTHOR);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return null;
    }

    public boolean isValid() {
        try {
            return (mData.getInt(TAG_STATUS) == 0 || mData.getInt(TAG_STATUS) == 2); // 服务生器只有0和1，返回的数据中status确为2，服务器正在查，这里暂时认定staus为2时也为true
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return false;
    }

    public boolean getIsRemind() {
        try {
            return mData.getInt(TAG_IS_REMIND) == 1;
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return false;
    }
    public int getIsRemindToInt() {
        try {
            return mData.getInt(TAG_IS_REMIND);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }
    public boolean getIsRead() {
        try {
            return mData.getInt(TAG_IS_READ) == 1;
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return false;
    }

    public int getStatus() {
        try {
            return mData.getInt(TAG_STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return 0;
    }

    public int getDayType() {
        try {
            return mData.getInt(TAG_DAY_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return 0;
    }

    public int getTarget() {
        try {
            return mData.getInt(TAG_TARGET);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }

    public int getCycle() {
        try {
            return mData.getInt(TAG_CYCLE);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }
    
    public int getIsLunar() {
        try {
            return mData.getInt(TAG_IS_LUNAR);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
    }

    public int adjust() {
        if (!isValid())
            return -1;
        if(!getIsRemind()){
            return 1;
        }
        int dayOfMonth, dayOfWeek, month;
        int now = (int)(new Date().getTime() / 1000);
        // 此操作为将当前时间点秒位置归零，由于服务器端拿下来的提醒时间点可能不是整秒，所以当过滤时，用归零的时间点
        // 去比较从服务器拿下来的时间点时，总是认为没到提醒时间，保存此提醒数据到数据库，后期刷新数据时，又弹出提醒
        // 1分钟内总会提醒的。考虑过向服务器提交数据时将时间点归零提交，但是iPhone版提交时也没有归零，为了统一，改此处的归零时间点。
        // now -= (now % 60);

        int advance = getAdvance();
        int periodType = getCycle();

        int realTimestamp = getRealTimestamp(getBaseTimestamp(), advance);

        String temprealstrx = ReminderDisplayUtility.getDate((long)realTimestamp * 1000);
        String tempnowstrx = ReminderDisplayUtility.getDate((long)now * 1000);

        // 用户设置的响铃时间
        Date userConfigAlarmDate = new Date((long)realTimestamp * 1000);
        Date nowDate = new Date((long)now * 1000);

        Calendar userConfigAlarmCalendar = Calendar.getInstance();
        userConfigAlarmCalendar.setTime(userConfigAlarmDate);
        // int nextPeriodMonth = nextPeriodCalendar.get(Calendar.MONTH);
        int alarmDay = userConfigAlarmCalendar.get(Calendar.DAY_OF_MONTH);
        int alarmHours = userConfigAlarmCalendar.get(Calendar.HOUR_OF_DAY);
        int alarmMinutes = userConfigAlarmCalendar.get(Calendar.MINUTE);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(nowDate);
        // int nextPeriodMonth = nextPeriodCalendar.get(Calendar.MONTH);
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
        int nowHours = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinutes = nowCalendar.get(Calendar.MINUTE);

        CxLog.d(TAG, "realTimestamp=" + ReminderDisplayUtility.getDate((long)realTimestamp * 1000)
                + "|now=" + ReminderDisplayUtility.getDate((long)now * 1000));
        // if (realTimestamp < now) {
        // 此处待优化
        if (periodType == sReminderPeriodOnce) {
            if (realTimestamp < now)
                return -1;
            else {
                return 0;
            }
        } else {
            nowCalendar = Calendar.getInstance();
            Calendar nextAlarmCalendar = Calendar.getInstance();
            nextAlarmCalendar.setTimeInMillis(((long)realTimestamp) * 1000);

            boolean valid;

            switch (periodType) {
                case sReminderPeriodDaily:
                    // 判断小时 分钟 ，如果已过 则天+1
                    Boolean curPeriod;
                    nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
                    ;
                    int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
                    if (nowDay != alarmDay)
                        curPeriod = false;
                    else {
                        if (nowHour < alarmHours) {
                            curPeriod = true;
                        } else if (alarmHours == nowCalendar.get(Calendar.HOUR_OF_DAY)
                                && nowCalendar.get(Calendar.MINUTE) < alarmMinutes) {
                            curPeriod = true;
                        } else
                            curPeriod = false;
                    }

                    int nextAlarmDay;

                    // 此处待优化
                    if (curPeriod) {
                        nextAlarmDay = nextAlarmCalendar.get(Calendar.DATE);
                    } else {
                        if (alarmDay <= nowDay)
                            nextAlarmDay = nowDay + 1;
                        else
                            nextAlarmDay = nextAlarmCalendar.get(Calendar.DATE);
                    }

                    nextAlarmCalendar.set(nextAlarmCalendar.get(Calendar.YEAR),
                            nextAlarmCalendar.get(Calendar.MONTH), nextAlarmDay, alarmHours,
                            alarmMinutes, 0);

                    break;
                case sReminderPeriodWeekly:
                    valid = nextAlarmCalendar.after(nowCalendar);
                    while (!valid) {
                        nextAlarmCalendar.set(Calendar.DATE,
                                nextAlarmCalendar.get(Calendar.DATE) + 7);
                        valid = nextAlarmCalendar.after(nowCalendar);
                    }
                    break;
                case sReminderPeriodMonthly:
                    dayOfMonth = nextAlarmCalendar.get(Calendar.DAY_OF_MONTH);
                    nextAlarmCalendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
                    valid = (nextAlarmCalendar.after(nowCalendar))
                            && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth);

                    while (!valid) {
                        nextAlarmCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        nextAlarmCalendar.set(Calendar.MONTH,
                                nextAlarmCalendar.get(Calendar.MONTH) + 1);
                        valid = (nextAlarmCalendar.after(nowCalendar))
                                && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth);
                    }
                    break;
                case sReminderPeriodAnnually:
                    dayOfMonth = nextAlarmCalendar.get(Calendar.DAY_OF_MONTH);
                    month = nextAlarmCalendar.get(Calendar.MONTH);
                    nextAlarmCalendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
                    valid = (nextAlarmCalendar.after(nowCalendar))
                            && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                            && (nextAlarmCalendar.get(Calendar.MONTH) == month);

                    while (!valid) {
                        nextAlarmCalendar.set(Calendar.YEAR,
                                nextAlarmCalendar.get(Calendar.YEAR) + 1);
                        valid = (nextAlarmCalendar.after(nowCalendar))
                                && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                                && (nextAlarmCalendar.get(Calendar.MONTH) == month);
                    }
                    break;

                default:
                    break;
            }

            CxLog.d(TAG,
                    "realTimestamp after adjust="
                            + ReminderDisplayUtility.getDate(nextAlarmCalendar.getTimeInMillis()));
            setBaseTimestamp(Reminder.getBaseTimestamp(
                    (int)(nextAlarmCalendar.getTimeInMillis() / 1000), advance));
            return 1; // adjusted
        }
        // } else {
        // return 0; // no need to do adjust;
        // }
    }

    public String getContent() {
        try {
            return mData.getString(TAG_CONTENT);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return null;
    }

    public boolean isShowMemorial() {
        return (getType() == 1);
    }

    public boolean isShowPrivate() {
        return (getTarget() == 0);
    }

    public static CalendarDataObj buildReminderObject(String data) {
        JSONObject object = null;
        try {
            object = new JSONObject(data);
            Log.d("ReminderModel", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new CalendarDataObj(object, mContext);
    }
    
    

}
