package com.chuxin.family.models;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.reminder.ReminderDisplayUtility;

public class Reminder extends Model {
	public static final int sReminderAdvanceNone = 0;
	public static final int sReminderAdvance15Minute = 1;
	public static final int sReminderAdvance1Hour = 2;
	public static final int sReminderAdvance1Day = 3;
	public static final int sReminderAdvance3Day = 4;
	public static final int sReminderAdvance5Day = 5;

	public static final int sReminderCustomizeFlagMonday = 1 << 1;
	public static final int sReminderCustomizeFlagTuesday = 1 << 2;
	public static final int sReminderCustomizeFlagWednesday = 1 << 3;
	public static final int sReminderCustomizeFlagThursday = 1 << 4;
	public static final int sReminderCustomizeFlagFriday = 1 << 5;
	public static final int sReminderCustomizeFlagSaturday = 1 << 6;
	public static final int sReminderCustomizeFlagSunday = 1 << 7;

	public static final int sReminderPeriodOnce = 0;
	public static final int sReminderPeriodDaily = 1;
	public static final int sReminderPeriodWeekly = 2;
	public static final int sReminderPeriodMonthly = 3;
	public static final int sReminderPeriodAnnually = 4;
	public static final int sReminderPeriodCustomize = 5;

	public static final int sReminderDelay10Minute = 0;
	public static final int sReminderDelayOneHour = 1;
	public static final int sReminderDelayOneDay = 2;

	private static final String TAG = "Reminder";
	private static final String TAG_REMINDER_ID = "reminder_id";
	public static final String TAG_BASE_TS = "base_ts";
	// private static final String TAG_REAL_TS = "real_ts"; // unused, never
	// trust its value
	public static final String TAG_PERIOD_TYPE = "cycle_type";
	public static final String TAG_CUSTOMIZE = "data";
	public static final String TAG_TITLE = "comment";
	public static final String TAG_ADVANCE = "advance";
	public static final String TAG_TARGET = "target";
	public static final String TAG_AUTHOR = "author";
	public static final String TAG_UPDATE_TS = "ts";
	public static final String TAG_USER_UPDATE_TS = "update_ts";
	public static final String TAG_STATUS = "status";
	public static final String TAG_RECEIVERS = "receivers";
	public static final String TAG_DELAY = "is_delay";

	public Reminder() {
		super();
		mTable = "reminders";
	}

	public Reminder(JSONObject data, Context context) {
		super();
		mTable = "reminders";
		mContext = context;
		if (data != null) {
			mData = data;
			mId = getReminderId();
		} else {
			init();
		}
	}

	public static boolean isValidCustomize(int dayOfWeek, int customize) {
		CxLog.v(TAG, "dayofweek=" + dayOfWeek + " : customize=" + customize);
		int flag = 0;
		try {
			flag = CalendarUtil.getInstance().importDayOfWeek(dayOfWeek);
		} catch (Exception e) {
			e.printStackTrace();
			CxLog.e(TAG, "Invalid customize: " + customize);
			assert (false);
		}
		return (((1<<flag) & customize) == (1<<flag));
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

	public int getDelayRealTimestamp(int now, int type) {
		switch (type) {
		case sReminderDelay10Minute:
			return now + (10 * 60);
		case sReminderDelayOneHour:
			return now + (60 * 60);
		case sReminderDelayOneDay:
			return now + (24 * 60 * 60);
		default:
			return now;
		}
	}

	public int getBaseTimestamp() {
		try {
			return mData.getInt(TAG_BASE_TS);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return -1;
	}

	public void setBaseTimestamp(int value) {
		try {
			mData.put(TAG_BASE_TS, value);
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

	public String[] getReceivers() {
		JSONArray receivers = null;
		try {
			receivers = mData.getJSONArray(TAG_RECEIVERS);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		String[] result = new String[receivers.length()];
		for (int i = 0; i < receivers.length(); i++) {
			try {
				result[i] = receivers.getString(i);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return result;
	}

	public String getReminderId() {
		try {
			return mData.getString(TAG_REMINDER_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return null;
	}

	public boolean getReminderIsDelay() {
		try {
			return mData.getBoolean(TAG_DELAY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getCustomize() {
		try {
			return mData.getInt(TAG_CUSTOMIZE);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return -1;
	}

	public int getPeriodType() {
		try {
			return mData.getInt(TAG_PERIOD_TYPE);
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
			return (mData.getInt(TAG_STATUS) == 0 || mData.getInt(TAG_STATUS) == 2 ); // 服务生器只有0和1，返回的数据中status确为2，服务器正在查，这里暂时认定staus为2时也为true 
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return false;
	}
	
	public int getStatus(){
	    try {
            return mData.getInt(TAG_STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
	    return 0;
	}

	public int getUpdateTimestamp() {
		try {
			return mData.getInt(TAG_UPDATE_TS);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return -1;
	}

	public boolean isNewToMe() {

		int updateTimestamp = getUpdateTimestamp();
		try {
			JSONObject updateTimestamps = mData
					.getJSONObject(TAG_USER_UPDATE_TS);
			int myUpdateTimestamp = updateTimestamps.getInt(CxGlobalParams
					.getInstance().getUserId());
			return (myUpdateTimestamp == updateTimestamp);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return false;
	}

	public boolean isNewToMyPartner() {
		int updateTimestamp = getUpdateTimestamp();
		try {
			JSONObject updateTimestamps = mData
					.getJSONObject(TAG_USER_UPDATE_TS);
			int partnerUpdateTimestamp = updateTimestamps.getInt(CxGlobalParams
					.getInstance().getPartnerId());
			return (partnerUpdateTimestamp == updateTimestamp);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return false;
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

	public int adjust() {
		if (!isValid())
			return -1;

		int dayOfMonth, dayOfWeek, month;
		int now = (int) (new Date().getTime() / 1000);
		// 此操作为将当前时间点秒位置归零，由于服务器端拿下来的提醒时间点可能不是整秒，所以当过滤时，用归零的时间点
		// 去比较从服务器拿下来的时间点时，总是认为没到提醒时间，保存此提醒数据到数据库，后期刷新数据时，又弹出提醒
		// 1分钟内总会提醒的。考虑过向服务器提交数据时将时间点归零提交，但是iPhone版提交时也没有归零，为了统一，改此处的归零时间点。
//		now -= (now % 60);

		int advance = getAdvance();
		int periodType = getPeriodType();

		int realTimestamp = getRealTimestamp(getBaseTimestamp(), advance);

		String temprealstrx = ReminderDisplayUtility.getDate((long)realTimestamp*1000);
		String tempnowstrx = ReminderDisplayUtility.getDate((long)now*1000);
		
		//用户设置的响铃时间
		Date userConfigAlarmDate = new Date((long)realTimestamp*1000);
		Date nowDate = new Date((long)now*1000);
		
		Calendar userConfigAlarmCalendar = Calendar.getInstance();
		userConfigAlarmCalendar.setTime(userConfigAlarmDate);
//		int nextPeriodMonth = nextPeriodCalendar.get(Calendar.MONTH);
		int alarmDay = userConfigAlarmCalendar.get(Calendar.DAY_OF_MONTH);
		int alarmHours = userConfigAlarmCalendar.get(Calendar.HOUR_OF_DAY);
		int alarmMinutes = userConfigAlarmCalendar.get(Calendar.MINUTE);

		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(nowDate);
//		int nextPeriodMonth = nextPeriodCalendar.get(Calendar.MONTH);
		int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
		int nowHours = nowCalendar.get(Calendar.HOUR_OF_DAY);
		int nowMinutes = nowCalendar.get(Calendar.MINUTE);		
		
		CxLog.d(TAG, "realTimestamp=" + ReminderDisplayUtility.getDate((long)realTimestamp*1000) + "|now=" + ReminderDisplayUtility.getDate((long)now*1000));
		//if (realTimestamp < now) {
		//此处待优化
			if (periodType == sReminderPeriodOnce) {
				if (realTimestamp < now)
					return -1; 
				else {
					return 0;
				}
			} else {
				nowCalendar = Calendar.getInstance();
				Calendar nextAlarmCalendar = Calendar.getInstance();
				nextAlarmCalendar.setTimeInMillis(((long) realTimestamp) * 1000);
				
				boolean valid;

				switch (periodType) {
				case sReminderPeriodDaily:
					//判断小时 分钟 ，如果已过 则天+1
					Boolean curPeriod;
					nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);;
					int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
					if (nowDay != alarmDay)
						curPeriod = false;
					else {
						if ( nowHour < alarmHours) {
							curPeriod = true;
						} else if (alarmHours == nowCalendar.get(Calendar.HOUR_OF_DAY) &&
								nowCalendar.get(Calendar.MINUTE) < alarmMinutes) {
							curPeriod = true;
						} else
							curPeriod = false;
					}
					
					int nextAlarmDay;
					
					//此处待优化
					if (curPeriod) {
						nextAlarmDay = nextAlarmCalendar.get(Calendar.DATE);
					} else {
						if (alarmDay <= nowDay)
							nextAlarmDay = nowDay + 1;
						else
							nextAlarmDay = nextAlarmCalendar.get(Calendar.DATE);
					}

					nextAlarmCalendar.set(nextAlarmCalendar.get(Calendar.YEAR), 
							  nextAlarmCalendar.get(Calendar.MONTH), 
							  nextAlarmDay, 
							  alarmHours,
							  alarmMinutes, 
							  0);							
					
					break;
				case sReminderPeriodWeekly:
					valid = nextAlarmCalendar.after(nowCalendar);
					while (!valid) {
						nextAlarmCalendar.set(Calendar.DATE, nextAlarmCalendar.get(Calendar.DATE) + 7);
						valid = nextAlarmCalendar.after(nowCalendar);
					}
					break;
				case sReminderPeriodMonthly:
					dayOfMonth = nextAlarmCalendar.get(Calendar.DAY_OF_MONTH);
					nextAlarmCalendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
					valid = (nextAlarmCalendar.after(nowCalendar)) && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth);
					
					while (!valid) {
						nextAlarmCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);						
						nextAlarmCalendar.set(Calendar.MONTH, nextAlarmCalendar.get(Calendar.MONTH) + 1);
						valid = (nextAlarmCalendar.after(nowCalendar)) && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth);
					}
					break;
				case sReminderPeriodAnnually:
					dayOfMonth = nextAlarmCalendar.get(Calendar.DAY_OF_MONTH);
					month = nextAlarmCalendar.get(Calendar.MONTH);
					nextAlarmCalendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));					
					valid = (nextAlarmCalendar.after(nowCalendar)) && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) &&
							(nextAlarmCalendar.get(Calendar.MONTH) == month);
					
					while (!valid) {
						nextAlarmCalendar.set(Calendar.YEAR, nextAlarmCalendar.get(Calendar.YEAR) + 1);
						valid = (nextAlarmCalendar.after(nowCalendar)) && (nextAlarmCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) &&
								(nextAlarmCalendar.get(Calendar.MONTH) == month);
					}
					break;
				case sReminderPeriodCustomize:
					int customize = getCustomize();
					CxLog.d(TAG, "customize=" + customize);
					nextAlarmCalendar.set(Calendar.DATE, nowCalendar.get(Calendar.DATE));
					dayOfWeek = nextAlarmCalendar.get(Calendar.DAY_OF_WEEK);
					
					//valid = (nextAlarmCalendar.after(nowCalendar) && isValidCustomize(dayOfWeek, customize)); 
					//响铃时间 超过 当时时间  切换至下一个响铃
					while (!nextAlarmCalendar.after(nowCalendar)) {
						nextAlarmCalendar.set(Calendar.DATE, nextAlarmCalendar.get(Calendar.DATE) + 1);
						dayOfWeek = nextAlarmCalendar.get(Calendar.DAY_OF_WEEK);
						valid = (nextAlarmCalendar.after(nowCalendar) && isValidCustomize(dayOfWeek, customize)); 
						
						if (valid) break;
					}
					
					break; 
				default:
					break;
				}

				CxLog.d(TAG, "realTimestamp after adjust=" + ReminderDisplayUtility.getDate(nextAlarmCalendar.getTimeInMillis()) );
				setBaseTimestamp(Reminder.getBaseTimestamp(
						(int) (nextAlarmCalendar.getTimeInMillis() / 1000), advance));
				return 1; // adjusted
			}
//		} else {
//			return 0; // no need to do adjust;
//		}
	}

	public boolean getIsDelay() {
		try {
			return mData.getBoolean(TAG_DELAY);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return false;
	}

	public String getTitle() {
		try {
			return mData.getString(TAG_TITLE);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return null;
	}

	public static Reminder buildReminderObject(String data) {
		JSONObject object = null;
		try {
			object = new JSONObject(data);
			Log.d("ReminderModel", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new Reminder(object, mContext);
	}

}
