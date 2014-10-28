package com.chuxin.family.views.reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.res.Resources;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.Reminder;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

public class ReminderDisplayUtility {
    private static final String TAG = "ReminderDisplayUtility";
	private Resources mResources;

	public ReminderDisplayUtility(Resources resouces) {
		mResources = resouces;
	}

	private String[] createNLSReminderPeriodTimeAndMorningTag(Calendar calendar) {
		String format = mResources
				.getString(R.string.cx_fa_nls_reminder_period_time_format);
		String timeTag = String.format(format, calendar);
		String morningTag = null;

		if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
			morningTag = mResources.getString(R.string.cx_fa_nls_pm);
		} else {
			morningTag = mResources.getString(R.string.cx_fa_nls_am);
		}

		return new String[] { timeTag, "" };
	}

	public String createNLSReminderPeriodForOncePeriod(Reminder reminder) {
		String format = null;
		String dateTag = null;
		String timeTag = null;
		String morningTag = null;
		String[] temp = null;

		long current = new Date().getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int thisYear = calendar.get(Calendar.YEAR);

		calendar.setTime(new Date((long) (reminder.getBaseTimestamp()) * 1000));
		int realYear = calendar.get(Calendar.YEAR);

		if (thisYear == realYear) {
			format = mResources
					.getString(R.string.cx_fa_nls_reminder_period_this_year_date_format);
			dateTag = String.format(format, calendar);
		} else {
			format = mResources
					.getString(R.string.cx_fa_nls_reminder_period_date_format);
			dateTag = String.format(format, calendar);
		}

		temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
		timeTag = temp[0];
		morningTag = temp[1];

		return dateTag + " " + morningTag + " " + timeTag;
	}

	public String createNLSReminderPeriodForDaily(Reminder reminder) {
		String format = null;
		String dateTag = null;
		String timeTag = null;
		String morningTag = null;
		String[] temp = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((long) (reminder.getBaseTimestamp()) * 1000));

		dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everyday);

		temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
		timeTag = temp[0];
		morningTag = temp[1];

		return dateTag + " " + morningTag + " " + timeTag;
	}

	public String createNLSReminderPeriodForWeekly(Reminder reminder) {
		try {
			String format = null;
			String dateTag = null;
			String timeTag = null;
			String morningTag = null;
			String[] temp = null;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date((long) (reminder.getBaseTimestamp()) * 1000));

			int week = calendar.get(Calendar.DAY_OF_WEEK);
			switch (week) {
			
			case 1:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everysun);
				break;
			case 2:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everymon);
				break;
			case 3:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everytue);
				break;
			case 4:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everywed);
				break;
			case 5:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everythu);
				break;
			case 6:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everyfri);
				break;
			case 7:
				dateTag = mResources
						.getString(R.string.cx_fa_nls_reminder_everysat);
				break;
			}
			dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everyweek) + dateTag;
			temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
			timeTag = temp[0];
			morningTag = temp[1];

			return dateTag + " " + morningTag + " " + timeTag;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String createNLSReminderPeriodForMonthly(Reminder reminder) {
		String format = null;
		String dateTag = null;
		String timeTag = null;
		String morningTag = null;
		String[] temp = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((long) (reminder.getBaseTimestamp()) * 1000));

		format = mResources
				.getString(R.string.cx_fa_nls_reminder_everymonth_format);
		dateTag = String.format(format, calendar.get(Calendar.DAY_OF_MONTH));

		temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
		timeTag = temp[0];
		morningTag = temp[1];

		return dateTag + " " + morningTag + " " + timeTag;
	}

	public String createNLSReminderPeriodForAnnually(Reminder reminder) {
		String format = null;
		String dateTag = null;
		String timeTag = null;
		String morningTag = null;
		String[] temp = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((long) (reminder.getBaseTimestamp()) * 1000));

		format = mResources
				.getString(R.string.cx_fa_nls_reminder_everyyear_format);
		dateTag = String.format(format, calendar);

		temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
		timeTag = temp[0];
		morningTag = temp[1];

		return dateTag + " " + morningTag + " " + timeTag;
	}

	public String createNLSReminderPeriodForCustomize(Reminder reminder, boolean flag) {
		String format = null;
		String dateTag = null;
		String timeTag = null;
		String morningTag = null;
		String[] temp = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date((long) (reminder.getBaseTimestamp()) * 1000));
		int customize = reminder.getCustomize();

		if (ReminderController.doesCustomizeMeanEveryDay(customize)) {
			dateTag = mResources
					.getString(R.string.cx_fa_nls_reminder_everyday);
		} else if (ReminderController
				.doesCustomizeMeanAllWorkingDays(customize)) {
			dateTag = mResources
					.getString(R.string.cx_fa_nls_reminder_workingdays);
		} else {
			List<String> days = new ArrayList<String>();
			if (ReminderController.doesCustomizeContainsMonday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everymon));
			if (ReminderController.doesCustomizeContainsTuesday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everytue));
			if (ReminderController.doesCustomizeContainsWednesday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everywed));
			if (ReminderController.doesCustomizeContainsThursday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everythu));
			if (ReminderController.doesCustomizeContainsFriday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everyfri));
			if (ReminderController.doesCustomizeContainsSaturday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everysat));
			if (ReminderController.doesCustomizeContainsSunday(customize))
				days.add(mResources
						.getString(R.string.cx_fa_nls_reminder_everysun));

			dateTag = "";
			String prefix = mResources
					.getString(R.string.cx_fa_nls_reminder_customize_perfix);
			String seperator = mResources
					.getString(R.string.cx_fa_nls_reminder_customize_seperator);
			for (int i = 0; i < days.size(); i++) {
				if (i == 0) {
					dateTag = days.get(i);
				} else {
					dateTag = dateTag + seperator + days.get(i);
				}
			}
			dateTag = prefix + dateTag;
		}

		temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
		timeTag = temp[0];
		morningTag = temp[1];
		
		if(flag){
		    return dateTag + " " + morningTag + " " + timeTag;
		} else {
		    return morningTag + " " + timeTag;
		}
	}

	public String getNLSReminderTarget(int target) {
		switch (target) {
		case ReminderController.sReminderTargetMyself:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_target_myself);
		case ReminderController.sReminderTargetRelatie:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_target_relatie);
		case ReminderController.sReminderTargetBoth:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_target_both);
		}
		return null;
	}

	public String getNLSReminderAdvance(int advance) {
		switch (advance) {
		case ReminderController.sReminderAdvanceNone:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_advance_none);
		case ReminderController.sReminderAdvance15Minute:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_advance_15minutes);
		case ReminderController.sReminderAdvance1Hour:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_advance_1hour);
		case ReminderController.sReminderAdvance1Day:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_advance_1day);
		case ReminderController.sReminderAdvance3Day:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_advance_3day);
		case ReminderController.sReminderAdvance5Day:
			return mResources
					.getString(R.string.cx_fa_nls_reminder_advance_5day);
		}
		return null;
	}

	public String createNLSReminderPeriodLabel(Reminder reminder) {
		String dateTag = null;
		String advanceTag = null;
		int period = reminder.getPeriodType();

		switch (period) {
		case ReminderController.sReminderPeriodOnce:
			dateTag = createNLSReminderPeriodForOncePeriod(reminder);
			break;
		case ReminderController.sReminderPeriodDaily:
			dateTag = createNLSReminderPeriodForDaily(reminder);
			break;

		case ReminderController.sReminderPeriodWeekly:
			dateTag = createNLSReminderPeriodForWeekly(reminder);
			break;

		case ReminderController.sReminderPeriodMonthly:
			dateTag = createNLSReminderPeriodForMonthly(reminder);
			break;

		case ReminderController.sReminderPeriodAnnually:
			dateTag = createNLSReminderPeriodForAnnually(reminder);
			break;

		case ReminderController.sReminderPeriodCustomize:
			dateTag = createNLSReminderPeriodForCustomize(reminder, true);
			break;
		}

		int advance = reminder.getAdvance();
		if (advance == ReminderController.sReminderAdvanceNone) {
			return dateTag;
		} else {
			advanceTag = getNLSReminderAdvance(advance);
			String format = mResources
					.getString(R.string.cx_fa_nls_reminder_label_date_advance);
			return String.format(format, dateTag, advanceTag);
		}
	}

    public String createNLSReminderPeriodLabelForPopDialog(Reminder reminder) {
        String dateTag = null;
        String advanceTag = null;
        int period = reminder.getPeriodType();

        switch (period) {
            case ReminderController.sReminderPeriodOnce:
                dateTag = createNLSReminderPeriodForOncePeriod(reminder);
                break;
            case ReminderController.sReminderPeriodDaily:
                dateTag = createNLSReminderPeriodForDaily(reminder);
                break;

            case ReminderController.sReminderPeriodWeekly:
                dateTag = createNLSReminderPeriodForWeekly(reminder);
                break;

            case ReminderController.sReminderPeriodMonthly:
                dateTag = createNLSReminderPeriodForMonthly(reminder);
                break;

            case ReminderController.sReminderPeriodAnnually:
                dateTag = createNLSReminderPeriodForAnnually(reminder);
                break;

            case ReminderController.sReminderPeriodCustomize:
                dateTag = createNLSReminderPeriodForCustomize(reminder, false);
                break;
        }

        int advance = reminder.getAdvance();
        if (advance == ReminderController.sReminderAdvanceNone) {
            return dateTag;
        } else {
            advanceTag = getNLSReminderAdvance(advance);
            String format = mResources.getString(R.string.cx_fa_nls_reminder_label_date_advance);
            return String.format(format, dateTag, advanceTag);
        }
    }
	
	public String createNLSReminderTipLabel(Reminder reminder) {

		boolean amITheOwner = (reminder.getAuthor().equals(CxGlobalParams.getInstance()
                .getUserId()));
		boolean notifyBoth = (reminder.getTarget() == ReminderController.sReminderTargetBoth);
//		boolean notifyMe = notifyBoth;
		boolean notifyMySelf = (reminder.getTarget() == ReminderController.sReminderTargetMyself);
		

/*		if (!notifyBoth) {
			String[] receivers = reminder.getReceivers();
			if (receivers == null)
				return null;

			for (int i = 0; i < receivers.length; i++) {
				if (receivers[i].equals(Globals.getInstance().getUid())) {
					notifyMe = true;
				}
			}
		}*/

		String me = null;
		String goal = null;
		
		if(amITheOwner){
		    me = mResources.getString(R.string.cx_fa_nls_me);
		    if(notifyMySelf){
		        goal = mResources.getString(R.string.cx_fa_nls_myself);
		    } else if(notifyBoth){
		        goal = mResources.getString(R.string.cx_fa_nls_us);
		    } else {
		        goal = mResources.getString(CxResourceString.getInstance().str_reminder_name);
//		        if (RkGlobalParams.getInstance().amIHusband()){
//		            goal = mResources.getString(R.string.cx_fa_nls_wife);
//		        } else {
//		            goal = mResources.getString(R.string.cx_fa_nls_husband);
//		        }
		    }
		} else {
		    me = mResources.getString(CxResourceString.getInstance().str_reminder_name);
//		    if(RkGlobalParams.getInstance().amIHusband()){
//		        me = mResources.getString(R.string.cx_fa_nls_wife);
//		    } else {
//		        me = mResources.getString(R.string.cx_fa_nls_husband);
//		    }
		    if(notifyBoth){
                goal = mResources.getString(R.string.cx_fa_nls_us);
            } else {
                goal = mResources.getString(R.string.cx_fa_nls_me);
            }
		}
		

		/*if (notifyMySelf) {
			me = mResources.getString(R.string.cx_fa_nls_me);
		} else {
			if (amITheOwner) {

				if (Globals.getInstance().amIHusband()) {
					me = mResources.getString(R.string.cx_fa_nls_me);
				} else {
					me = mResources.getString(R.string.cx_fa_nls_wife);
				}
			} else {

				if (Globals.getInstance().amIHusband()) {
					me = mResources.getString(R.string.cx_fa_nls_wife);
				} else {
					me = mResources.getString(R.string.cx_fa_nls_me);
				}
			}
		}
		if (notifyMySelf) {
			goal = mResources.getString(R.string.cx_fa_nls_myself);
		} else {
			if (notifyBoth) {
				goal = mResources.getString(R.string.cx_fa_nls_us);
			} else if (notifyMe) {
				goal = mResources.getString(R.string.cx_fa_nls_me);
			} else {
				if (Globals.getInstance().amIHusband()) {
					goal = mResources.getString(R.string.cx_fa_nls_wife);
				} else {
					goal = mResources.getString(R.string.cx_fa_nls_husband);
				}
			}
		}*/
		String format = mResources
				.getString(R.string.cx_fa_nls_reminder_tip_format);
		return String.format(format, me, goal);
	}
	
	public static String getDate(long time){
		Date date = new Date(time);
		return date.toString();
	}

	public String createNLSReminderHappenLabel(Reminder reminder)
			throws Exception {
		long baseTime = (long)(reminder.getRealTimestamp()) * 1000;
		int advance = reminder.getAdvance();

		long currentMs = new Date().getTime();
//		long real = ReminderController.getRealTime(baseTime, advance);
		long real = baseTime;

		if (real < currentMs)
			return null;

        Date curDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(real);
        long diffMs = endDate.getTime() - curDate.getTime();
        long days = diffMs/(1000*3600*24);
        long remainderMs = diffMs%(1000*3600*24);
        
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(curDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(currentMs + remainderMs));
        
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        
        days = (day1 == day2) ? days: (days+1);
		
		long hours =  (diffMs / (60 * 60 * 1000) - days * 24);
		long mins =  ((diffMs / (60 * 1000)) - days * 24 * 60 - hours * 60);
		long s =  (diffMs / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60);
        CxLog.v(TAG, "days= " + days );
		if (days < 1) {
			if (hours > 0) {
				String format = mResources
						.getString(R.string.cx_fa_nls_reminder_tip_future_hour_format);
				return String.format(format, hours);
			} else if (mins > 0) {
				String format = mResources
						.getString(R.string.cx_fa_nls_reminder_tip_future_minute_format);
				return String.format(format, mins);
			} else if (s > 0) {
				String format = mResources
						.getString(R.string.cx_fa_nls_reminder_tip_future_second_format);
				return String.format(format, s);
			}
		} else if (days == 1) {
			return mResources
					.getString(R.string.cx_fa_nls_reminder_tip_tomorrow);
		} 
		 else if (days == 2) {
            return mResources
                    .getString(R.string.cx_fa_nls_reminder_tip_after_tomorrow);
	    } else if (days > 2) {
			String format = mResources
					.getString(R.string.cx_fa_nls_reminder_tip_future_format);
			return String.format(format, days);
		} else {
			return mResources
					.getString(R.string.cx_fa_nls_reminder_tip_future_years);
		}
		return null;
		// if (differ <= Constants.SECONDS_OF_ONE_DAY) {
		// return mResources.getString(R.string.cx_fa_nls_reminder_tip_today);
		// } else if (differ <= Constants.SECONDS_OF_TWO_DAYS) {
		// return
		// mResources.getString(R.string.cx_fa_nls_reminder_tip_tomorrow);
		// } else if (differ <= Constants.SECONDS_OF_ONE_YEAR) {
		// int days = (int)((differ + (Constants.SECONDS_OF_ONE_DAY - 1)) /
		// Constants.SECONDS_OF_ONE_DAY);
		// String format =
		// mResources.getString(R.string.cx_fa_nls_reminder_tip_future_format);
		// return String.format(format, days);
		// } else if (differ <= Constants.SECONDS_OF_TWO_YEARS) {
		// return
		// mResources.getString(R.string.cx_fa_nls_reminder_tip_future_this_year);
		// } else {
		// return
		// mResources.getString(R.string.cx_fa_nls_reminder_tip_future_years);
		// }
	}
	
	   public String createNLSReminderPeriodForCustomizeDisplays(int customize) {
	        String format = null;
	        String dateTag = null;
	        String timeTag = null;
	        String morningTag = null;
	        String[] temp = null;

	        if (ReminderController.doesCustomizeMeanEveryDay(customize)) {
	            dateTag = mResources
	                    .getString(R.string.cx_fa_nls_reminder_everyday);
	        } else if (ReminderController
	                .doesCustomizeMeanAllWorkingDays(customize)) {
	            dateTag = mResources
	                    .getString(R.string.cx_fa_nls_reminder_workingdays);
	        } else {
	            List<String> days = new ArrayList<String>();
	            if (ReminderController.doesCustomizeContainsMonday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everymon));
	            if (ReminderController.doesCustomizeContainsTuesday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everytue));
	            if (ReminderController.doesCustomizeContainsWednesday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everywed));
	            if (ReminderController.doesCustomizeContainsThursday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everythu));
	            if (ReminderController.doesCustomizeContainsFriday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everyfri));
	            if (ReminderController.doesCustomizeContainsSaturday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everysat));
	            if (ReminderController.doesCustomizeContainsSunday(customize))
	                days.add(mResources
	                        .getString(R.string.cx_fa_nls_reminder_everysun));

	            dateTag = "";
	            String prefix = mResources
	                    .getString(R.string.cx_fa_nls_reminder_customize_perfix);
	            String seperator = mResources
	                    .getString(R.string.cx_fa_nls_reminder_customize_seperator);
	            for (int i = 0; i < days.size(); i++) {
	                if (i == 0) {
	                    dateTag = days.get(i);
	                } else {
	                    dateTag = dateTag + seperator + days.get(i);
	                }
	            }
	            dateTag = prefix + dateTag;
	        }

	        return dateTag;
	    }
	   public long getSecond(Reminder reminder){
	        long baseTime = (long)(reminder.getRealTimestamp()) * 1000;
	        int advance = reminder.getAdvance();

	        long currentMs = new Date().getTime();
//	      long real = ReminderController.getRealTime(baseTime, advance);
	        long real = baseTime;

	        if (real < currentMs)
	            return 0;

	        Date curDate = new Date(System.currentTimeMillis());
	        Date endDate = new Date(real);
	        long diffMs = endDate.getTime() - curDate.getTime();
	        long days = diffMs/(1000*3600*24);
	        long remainderMs = diffMs%(1000*3600*24);
	        
	        Calendar cal1 = Calendar.getInstance();
	        cal1.setTime(curDate);
	        Calendar cal2 = Calendar.getInstance();
	        cal2.setTime(new Date(currentMs + remainderMs));
	        
	        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
	        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
	        
	        days = (day1 == day2) ? days: (days+1);
	        
	        long hours =  (diffMs / (60 * 60 * 1000) - days * 24);
	        long mins =  ((diffMs / (60 * 1000)) - days * 24 * 60 - hours * 60);
	        long s =  (diffMs / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60);
	        if (days < 1) {
	            if (hours > 0) {
	               
	                return hours * 60 * 60;
	            } else if (mins > 0) {
	                return mins * 60;
	            } else if (s > 0) {
	                return s;
	            }
	        } else {
	            return days * 24 * 60 * 60;
	        }
	       return 0;
	   }
}
