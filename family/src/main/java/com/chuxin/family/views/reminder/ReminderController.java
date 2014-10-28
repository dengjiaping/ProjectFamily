package com.chuxin.family.views.reminder;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chuxin.family.app.events.ReminderReceiver;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.Reminder;
import com.chuxin.family.net.ReminderApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.utils.Globals;

public class ReminderController {
	
	private static final String TAG = "ReminderController";

	public static final int sOneDayInSeconds = 3600 * 24;
	public static final int sReminderCustomizeFlagMonday   = 1 << 1;
	public static final int sReminderCustomizeFlagTuesday  = 1 << 2;
	public static final int sReminderCustomizeFlagWednesday= 1 << 3;
	public static final int sReminderCustomizeFlagThursday = 1 << 4;
	public static final int sReminderCustomizeFlagFriday   = 1 << 5;
	public static final int sReminderCustomizeFlagSaturday = 1 << 6;
	public static final int sReminderCustomizeFlagSunday   = 1 << 7;

	public static final int sReminderPeriodOnce 	= 0;
	public static final int sReminderPeriodDaily	= 1;
	public static final int sReminderPeriodWeekly = 2;
	public static final int sReminderPeriodMonthly= 3;
	public static final int sReminderPeriodAnnually= 4;
	public static final int sReminderPeriodCustomize = 5;	    
	
	public static final int sReminderTargetMyself = 0;
	public static final int sReminderTargetRelatie= 1;
	public static final int sReminderTargetBoth   = 2;

	public static final int sReminderAdvanceNone		= 0;
	public static final int sReminderAdvance15Minute	= 1;
	public static final int sReminderAdvance1Hour 		= 2;
	public static final int sReminderAdvance1Day		= 3;
	public static final int sReminderAdvance3Day		= 4;
	public static final int sReminderAdvance5Day		= 5;
	
   public static final String REMINDER_ID = "remind_id";
   public static final String REMINDER_TITLE = "remind_title";
   
   public Reminder mReminder;
   
	private class ReminderObj {
		public String mId;
		public String mTitle;
		public int mTarget;
		public int mPeriod;
		public long mBaseTime;
		public long mRealTime;
		public int mAdvance;
		public int mCustomize;
		private int mFlag;
		private boolean mIsDelay;

		public ReminderObj() {}
		public ReminderObj(String id, String title, int target,
						int period, long time,
						int advance, int customize, int flag, boolean isDelay) {
	
			mId = id;
			mTitle = title;
			mTarget = target;
			mPeriod = period;
			mBaseTime = time;
			mAdvance = advance;
			mCustomize = customize;
			mFlag = flag;
			mIsDelay = isDelay;
		}
		
		public Reminder toReminder() {
			String jsonData = "{\"status\": 0" +
			                    ",\"cycle_type\": " + mPeriod +
			                    ",\"base_ts\": " + (mBaseTime / 1000) +
			                    ",\"real_ts\": " + (mRealTime / 1000) +
			                    ",\"target\": " + mTarget +
			                    ",\"comment\": " + mTitle +
			                    ",\"advance\": " + mAdvance +
			                    ",\"data\": " + mCustomize +
			                   "}";
			JSONObject json;
			try {
				json = new JSONObject(jsonData);
				return new Reminder(json, null);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public long getRealTime() throws Exception {
			return ReminderController.getRealTime(mBaseTime, mAdvance);
		}
		
		@Override
		public String toString() {
			return "Reminder: mId=" + mId + ", mTitle=" + mTitle + ", mTarget=" + mTarget +
					", mPeriod=" + mPeriod + ", mBaseTime=" + mBaseTime +
					", mAdvance=" + mAdvance + ", mCustom=" + mCustomize +", mFlag=" + mFlag + ", mIsDelay=" + mIsDelay;
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

	
	public void submitReminderChanges(Context context, JSONCaller callback) throws Exception {
		if(mData.mIsDelay){
			
		} else {
			if (mData.mId.length() == 0) {
				// create one new reminder

				Reminder reminder = mData.toReminder();
				String dateString = "";
				if (reminder != null) {
					dateString = new ReminderDisplayUtility(context.getResources()).createNLSReminderPeriodLabel(reminder);
				}
				
				ReminderApi.getInstance().doCreateReminder(mData.mTitle,
						mData.mTarget, mData.mPeriod,
						(int) (mData.mBaseTime / 1000), mData.mAdvance,
						mData.mCustomize, dateString, callback);
			} else {
				// update one existing reminder
				ReminderApi.getInstance().doUpdateReminder(mData.mId,
						mData.mTitle, mData.mTarget, mData.mPeriod,
						(int) (mData.mBaseTime / 1000), mData.mAdvance,
						mData.mCustomize, callback);
			}
		}
	}
	
	public static boolean doesCustomizeMeanEveryDay(int customize) {
		int count = 0;
		
		if ((customize & sReminderCustomizeFlagMonday) == sReminderCustomizeFlagMonday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagTuesday) == sReminderCustomizeFlagTuesday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagWednesday) == sReminderCustomizeFlagWednesday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagThursday) == sReminderCustomizeFlagThursday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagFriday) == sReminderCustomizeFlagFriday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagSaturday) == sReminderCustomizeFlagSaturday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagSunday) == sReminderCustomizeFlagSunday)
			count += 1;

		return (count == 7);
	}
	
	public static boolean doesCustomizeMeanAllWorkingDays(int customize) {
		int count = 0;
		
		if ((customize & sReminderCustomizeFlagSunday) == sReminderCustomizeFlagSunday)
			return false;

		if ((customize & sReminderCustomizeFlagSaturday) == sReminderCustomizeFlagSaturday)
			return false;

		if ((customize & sReminderCustomizeFlagMonday) == sReminderCustomizeFlagMonday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagTuesday) == sReminderCustomizeFlagTuesday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagWednesday) == sReminderCustomizeFlagWednesday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagThursday) == sReminderCustomizeFlagThursday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagFriday) == sReminderCustomizeFlagFriday)
			count += 1;
		
		return (count == 5);
	}

	public static boolean doesCustomizeContainsMonday(int customize) {
		return ((customize & sReminderCustomizeFlagMonday) == sReminderCustomizeFlagMonday);
	}

	public static boolean doesCustomizeContainsTuesday(int customize) {
		return ((customize & sReminderCustomizeFlagTuesday) == sReminderCustomizeFlagTuesday);
	}

	public static boolean doesCustomizeContainsWednesday(int customize) {
		return ((customize & sReminderCustomizeFlagWednesday) == sReminderCustomizeFlagWednesday);
	}

	public static boolean doesCustomizeContainsThursday(int customize) {
		return ((customize & sReminderCustomizeFlagThursday) == sReminderCustomizeFlagThursday);
	}
	
	public static boolean doesCustomizeContainsFriday(int customize) {
		return ((customize & sReminderCustomizeFlagFriday) == sReminderCustomizeFlagFriday);
	}
	
	public static boolean doesCustomizeContainsSaturday(int customize) {
		return ((customize & sReminderCustomizeFlagSaturday) == sReminderCustomizeFlagSaturday);
	}
	
	public static boolean doesCustomizeContainsSunday(int customize) {
		return ((customize & sReminderCustomizeFlagSunday) == sReminderCustomizeFlagSunday);
	}

	public static int[] convertCustomizeToArray(int customize) {
		int count = 0;
		
		if ((customize & sReminderCustomizeFlagMonday) == sReminderCustomizeFlagMonday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagTuesday) == sReminderCustomizeFlagTuesday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagWednesday) == sReminderCustomizeFlagWednesday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagThursday) == sReminderCustomizeFlagThursday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagFriday) == sReminderCustomizeFlagFriday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagSaturday) == sReminderCustomizeFlagSaturday)
			count += 1;
		
		if ((customize & sReminderCustomizeFlagSunday) == sReminderCustomizeFlagSunday)
			count += 1;
		
		int[] result = new int[count];
		
		count = 0;
		if ((customize & sReminderCustomizeFlagMonday) == sReminderCustomizeFlagMonday) {
			result[count] = sReminderCustomizeFlagMonday;
			count += 1;
		}
		
		if ((customize & sReminderCustomizeFlagTuesday) == sReminderCustomizeFlagTuesday) {
			result[count] = sReminderCustomizeFlagTuesday;
			count += 1;
		}
		
		if ((customize & sReminderCustomizeFlagWednesday) == sReminderCustomizeFlagWednesday) {
			result[count] = sReminderCustomizeFlagWednesday;
			count += 1;
		}
		
		if ((customize & sReminderCustomizeFlagThursday) == sReminderCustomizeFlagThursday) {
			result[count] = sReminderCustomizeFlagThursday;
			count += 1;
		}
		
		if ((customize & sReminderCustomizeFlagFriday) == sReminderCustomizeFlagFriday) {
			result[count] = sReminderCustomizeFlagFriday;
			count += 1;
		}
		
		if ((customize & sReminderCustomizeFlagSaturday) == sReminderCustomizeFlagSaturday) {
			result[count] = sReminderCustomizeFlagSaturday;
			count += 1;
		}
	
		if ((customize & sReminderCustomizeFlagSunday) == sReminderCustomizeFlagSunday) {
			result[count] = sReminderCustomizeFlagSunday;
			count += 1;
		}

		return result;
	}

	private String mName;
	private String mSerilizeFilePath;
	private ReminderObj mData = null;
//	private ReminderController() {
//		mName = ".reminder";
//		mSerilizeFilePath = Globals.getInstance().createUserDir() + mName;
//	}
	
	private static ReminderController sInstance = new ReminderController();
	public static ReminderController getInstance() {
		return sInstance;
	}
	
	public void reset() {
		mData = new ReminderObj("", "提醒/纪念日", sReminderTargetBoth,
							sReminderPeriodOnce,
							new Date().getTime(), sReminderAdvanceNone, 0, (int)(System.currentTimeMillis()/1000), false);
	}

	public void reset(String id, String title, int target, int period,
					  long time, int advance, int custorm, int flag, boolean isDelay) {
		mData = new ReminderObj(id, title, target, period, time, advance, custorm, flag, isDelay);
	}
	
	public void reset(Reminder reminder){
		mReminder = reminder; 
		reset(reminder.getId(),
				reminder.getTitle(), reminder.getTarget(),
				reminder.getPeriodType(),
				(long) (reminder.getBaseTimestamp()) * 1000,
				reminder.getAdvance(), reminder.getCustomize(),
				reminder.getFlag(), reminder.getIsDelay());
	}
	
	public int getFlag(){
		return mData.mFlag;
	}
	
	public String getId() {
		return mData.mId;
	}

	public String getTitle() {
		return mData.mTitle;
	}
	
	public void setTitle(String title) {
		mData.mTitle = title;
	}
	
	public int getTarget() {
		return mData.mTarget;
	}
	
	public void setTarget(int target) {
		mData.mTarget = target;
	}
	
	public int getPeriod() {
		return mData.mPeriod;
	}
	
	public boolean getIsDelay(){
		return mData.mIsDelay;
	}
	
	public Reminder getReminder(){
		return mReminder;
	}
	
	public void setPeriod(int period) {
		if (mData.mPeriod != period) {
			mData.mPeriod = period;
			
			if (period == sReminderPeriodCustomize) {
				int customize = 0;
				customize |= sReminderCustomizeFlagMonday;
				customize |= sReminderCustomizeFlagThursday;
				customize |= sReminderCustomizeFlagWednesday;
				customize |= sReminderCustomizeFlagTuesday;
				customize |= sReminderCustomizeFlagFriday;
				mData.mCustomize = customize;
			}
		}
	}
	
	public long getTime() {
		return mData.mBaseTime;
	}
	
	public void setTime(long time) {
		mData.mBaseTime = time;
	}
	
	public long getRealTime() throws Exception {
		return mData.getRealTime();
	}
	
	public int getAdvance() {
		return mData.mAdvance;
	}
	
	public void setAdvance(int advance) {
		mData.mAdvance = advance;
	}
	
	public int getCustomize() {
		return mData.mCustomize;
	}
	
	public void setCustomize(int customize) {
		mData.mCustomize = customize;
	}

	   /**
     * set alarm reminder
     * @param realtime 
     */
	public void setAlarmReminder(Context context, int realtime, String remindId, int periodType, int flag, String title){
		Intent intent = new Intent(context, ReminderReceiver.class );
		intent.putExtra(REMINDER_ID, remindId);
		intent.putExtra(REMINDER_TITLE, title);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, flag);
//		realtime -= realtime % 60;// 秒位归零。
		long time = (long)realtime*1000;
		
		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!setAlarmReminder:" + remindId);
		
		String temprealstrx = ReminderDisplayUtility.getDate((long)time);
		int now = (int) (new Date().getTime() / 1000);
		String tempnowstrx = ReminderDisplayUtility.getDate((long)now*1000);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		switch(periodType){
			case sReminderPeriodOnce:
				alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
				break;
			case sReminderPeriodDaily:
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, (24*60*60*1000), pi); 
				break;
			case sReminderPeriodMonthly:
			    alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
				break;
			case sReminderPeriodWeekly:
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, (7*24*60*60*1000), pi); 
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
	public void cancelAlarmReminder(Context context, int flag){
	    if(null != context){
    		Intent intent = new Intent(context, ReminderReceiver.class);
    		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, flag);
    		AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
    		alarmManager.cancel(pi);
	    }
	}
	
    public int adjustTime() {
        try {
            int now = (int) (new Date().getTime() / 1000);
            now -= (now % 60);

            int advance = getAdvance();
            int periodType = getPeriod();

            int realTimestamp = (int) (getRealTime(getTime(), advance)/1000);
            Log.d(TAG, "realTimestamp=" + realTimestamp + "|now=" + now);
            if (realTimestamp < now) {
                if (periodType == sReminderPeriodOnce) {
                    return -1; // invalid
                } else {
                    Calendar nowCalendar = Calendar.getInstance();
                    Calendar realCalendar = Calendar.getInstance();
                    realCalendar.setTimeInMillis(((long) realTimestamp) * 1000);
                    switch (periodType) {
                    case sReminderPeriodDaily:
                        while (!realCalendar.after(nowCalendar))
                            realCalendar.set(Calendar.DATE,
                                    realCalendar.get(Calendar.DATE) + 1);
                        break;
                    case sReminderPeriodWeekly:
                        while (!realCalendar.after(nowCalendar))
                            realCalendar.set(Calendar.DATE,
                                    realCalendar.get(Calendar.DATE) + 7);
                        break;
                    case sReminderPeriodMonthly:
                        while (!realCalendar.after(nowCalendar))
                            realCalendar.set(Calendar.MONTH,
                                    realCalendar.get(Calendar.MONTH) + 1);
                        break;
                    case sReminderPeriodAnnually:
                        while (!realCalendar.after(nowCalendar))
                            realCalendar.set(Calendar.YEAR,
                                    realCalendar.get(Calendar.YEAR) + 1);
                        break;
                    default:
                        int customize = getCustomize();
                        while (!realCalendar.after(nowCalendar)) {
                            while (true) {
                                realCalendar.set(Calendar.DATE,
                                        realCalendar.get(Calendar.DATE) + 1);
                                int dayOfWeek = realCalendar
                                        .get(Calendar.DAY_OF_WEEK);
                                if (isValidCustomize(dayOfWeek, customize))
                                    break;
                            }
                        }
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
    
    private boolean isValidCustomize(int dayOfWeek, int customize) {
        int flag = 0;
        try {
            flag = CalendarUtil.getInstance().importDayOfWeek(dayOfWeek);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid customize: " + customize);
            assert (false);
        }
        return ((flag & customize) == flag);
    }
    
    /**
     * check settime is legal
     * @return true:legal， false:illegal
     */
    public boolean checkTimeIsLegal() {
        try {
            int now = (int) (new Date().getTime() / 1000);
            now -= (now % 60);

            int advance = getAdvance();
            int periodType = getPeriod();

            int realTimestamp = (int) (getRealTime(getTime(), advance)/1000);
            Log.d(TAG, "realTimestamp=" + realTimestamp + "|now=" + now);
//            if (realTimestamp < now) {
//                if (periodType == sReminderPeriodOnce) {
//                    return -1; // invalid
//                } else {
            Calendar nowCalendar = Calendar.getInstance();
            Calendar realCalendar = Calendar.getInstance();
            realCalendar.setTimeInMillis(((long) realTimestamp) * 1000);
            switch (periodType) {
            case sReminderPeriodDaily:
                while (!realCalendar.after(nowCalendar))
                    realCalendar.set(Calendar.DATE,
                            realCalendar.get(Calendar.DATE) + 1);
                break;
            case sReminderPeriodWeekly:
                while (!realCalendar.after(nowCalendar))
                    realCalendar.set(Calendar.DATE,
                            realCalendar.get(Calendar.DATE) + 7);
                break;
            case sReminderPeriodMonthly:
                while (!realCalendar.after(nowCalendar))
                    realCalendar.set(Calendar.MONTH,
                            realCalendar.get(Calendar.MONTH) + 1);
                break;
            case sReminderPeriodAnnually:
                while (!realCalendar.after(nowCalendar))
                    realCalendar.set(Calendar.YEAR,
                            realCalendar.get(Calendar.YEAR) + 1);
                break;
            case sReminderPeriodCustomize:
                int customize = getCustomize();
                while (!realCalendar.after(nowCalendar)) {
                    while (true) {
                        realCalendar.set(Calendar.DATE,
                                realCalendar.get(Calendar.DATE) + 1);
                        int dayOfWeek = realCalendar
                                .get(Calendar.DAY_OF_WEEK);
                        if (isValidCustomize(dayOfWeek, customize))
                            break;
                    }
                }
                break;
             default:
            	 break;
            }

            //setTime(realCalendar.getTimeInMillis());
            int realTime = (int)(realCalendar.getTimeInMillis()/1000);
            int nowTime = (int) (System.currentTimeMillis()/1000);
            if( realTime > nowTime ){
                return true;
            }
//                    return realCalendar.getTimeInMillis(); // adjusted
//                }
//            } 
//            else {
//                return 0; // no need to do adjust;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
