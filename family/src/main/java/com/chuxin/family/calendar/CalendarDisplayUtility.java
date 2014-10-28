
package com.chuxin.family.calendar;

import com.chuxin.family.R;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.Globals;

import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;
/**
 * 
 * @author shichao.wang
 *
 */
public class CalendarDisplayUtility {
    private static final String TAG = "ReminderDisplayUtility";

    private Resources mResources;

    public CalendarDisplayUtility(Resources resouces) {
        mResources = resouces;
    }

    private String[] createNLSReminderPeriodTimeAndMorningTag(Calendar calendar) {
        String format = mResources.getString(R.string.cx_fa_nls_reminder_period_time_format);
        String timeTag = String.format(format, calendar);
        String morningTag = null;

        if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
            morningTag = mResources.getString(R.string.cx_fa_nls_pm);
        } else {
            morningTag = mResources.getString(R.string.cx_fa_nls_am);
        }

        return new String[] {
                timeTag, ""
        };
    }

    public String createNLSReminderPeriodTimeAndMorningTag(CalendarDataObj calendarObj) {
        String[] temp = null;
        String timeTag = null;
        String morningTag = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(calendarObj.getBaseTimestamp()) * 1000));
        temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
        timeTag = temp[0];
        morningTag = temp[1];

        return morningTag + " " + timeTag;
    }

    public String createNLSReminderPeriodForOncePeriod(CalendarDataObj reminder) {
        String format = null;
        String dateTag = null;
        String timeTag = null;
        String morningTag = null;
        String[] temp = null;

        long current = new Date().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int thisYear = calendar.get(Calendar.YEAR);

        calendar.setTime(new Date((long)(reminder.getBaseTimestamp()) * 1000));
        int realYear = calendar.get(Calendar.YEAR);

        if (thisYear == realYear) {
            format = mResources.getString(R.string.cx_fa_nls_reminder_period_this_year_date_format);
            dateTag = String.format(format, calendar);
        } else {
            format = mResources.getString(R.string.cx_fa_nls_reminder_period_date_format);
            dateTag = String.format(format, calendar);
        }

        temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
        timeTag = temp[0];
        morningTag = temp[1];

        return dateTag + " " + morningTag + " " + timeTag;
    }

    public String createNLSReminderPeriodForDaily(CalendarDataObj reminder) {
        String format = null;
        String dateTag = null;
        String timeTag = null;
        String morningTag = null;
        String[] temp = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(reminder.getBaseTimestamp()) * 1000));

        dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everyday);

        temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
        timeTag = temp[0];
        morningTag = temp[1];

        return dateTag + " " + morningTag + " " + timeTag;
    }

    public String createNLSReminderPeriodForWeekly(CalendarDataObj reminder) {
        try {
            String format = null;
            String dateTag = null;
            String timeTag = null;
            String morningTag = null;
            String[] temp = null;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date((long)(reminder.getBaseTimestamp()) * 1000));

            int week = calendar.get(Calendar.DAY_OF_WEEK);
            switch (week) {

                case 1:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everysun);
                    break;
                case 2:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everymon);
                    break;
                case 3:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everytue);
                    break;
                case 4:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everywed);
                    break;
                case 5:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everythu);
                    break;
                case 6:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everyfri);
                    break;
                case 7:
                    dateTag = mResources.getString(R.string.cx_fa_nls_reminder_everysat);
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

    public String createNLSReminderPeriodForMonthly(CalendarDataObj reminder) {
        String format = null;
        String dateTag = null;
        String timeTag = null;
        String morningTag = null;
        String[] temp = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(reminder.getBaseTimestamp()) * 1000));

        format = mResources.getString(R.string.cx_fa_nls_reminder_everymonth_format);
        dateTag = String.format(format, calendar.get(Calendar.DAY_OF_MONTH));

        temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
        timeTag = temp[0];
        morningTag = temp[1];

        return dateTag + " " + morningTag + " " + timeTag;
    }

    public String createNLSReminderPeriodForAnnually(CalendarDataObj reminder) {
        String format = null;
        String dateTag = null;
        String timeTag = null;
        String morningTag = null;
        String[] temp = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date((long)(reminder.getBaseTimestamp()) * 1000));

        format = mResources.getString(R.string.cx_fa_nls_reminder_everyyear_format);
        dateTag = String.format(format, calendar);

        temp = createNLSReminderPeriodTimeAndMorningTag(calendar);
        timeTag = temp[0];
        morningTag = temp[1];

        return dateTag + " " + morningTag + " " + timeTag;
    }

    public String getNLSReminderTarget(int target) {
        switch (target) {
            case CalendarController.sReminderTargetMyself:
                return mResources.getString(R.string.cx_fa_nls_reminder_target_myself);
            case CalendarController.sReminderTargetRelatie:
                return mResources.getString(R.string.cx_fa_nls_reminder_target_relatie);
            case CalendarController.sReminderTargetBoth:
                return mResources.getString(R.string.cx_fa_nls_reminder_target_both);
        }
        return null;
    }

    public String getNLSReminderAdvance(int advance) {
        switch (advance) {
            case CalendarController.sReminderAdvanceNone:
                return mResources.getString(R.string.cx_fa_nls_reminder_advance_none);
            case CalendarController.sReminderAdvance15Minute:
                return mResources.getString(R.string.cx_fa_nls_reminder_advance_15minutes);
            case CalendarController.sReminderAdvance1Hour:
                return mResources.getString(R.string.cx_fa_nls_reminder_advance_1hour);
            case CalendarController.sReminderAdvance1Day:
                return mResources.getString(R.string.cx_fa_nls_reminder_advance_1day);
            case CalendarController.sReminderAdvance3Day:
                return mResources.getString(R.string.cx_fa_nls_reminder_advance_3day);
            case CalendarController.sReminderAdvance5Day:
                return mResources.getString(R.string.cx_fa_nls_reminder_advance_5day);
        }
        return null;
    }

    public String createNLSReminderPeriodLabel(CalendarDataObj reminder) {
        String dateTag = null;
        String advanceTag = null;
        int period = reminder.getCycle();

        switch (period) {
            case CalendarController.sReminderPeriodOnce:
                dateTag = createNLSReminderPeriodForOncePeriod(reminder);
                break;
            case CalendarController.sReminderPeriodDaily:
                dateTag = createNLSReminderPeriodForDaily(reminder);
                break;

            case CalendarController.sReminderPeriodWeekly:
                dateTag = createNLSReminderPeriodForWeekly(reminder);
                break;

            case CalendarController.sReminderPeriodMonthly:
                dateTag = createNLSReminderPeriodForMonthly(reminder);
                break;

            case CalendarController.sReminderPeriodAnnually:
                dateTag = createNLSReminderPeriodForAnnually(reminder);
                break;
        }

        int advance = reminder.getAdvance();
        if (advance == CalendarController.sReminderAdvanceNone) {
            return dateTag;
        } else {
            advanceTag = getNLSReminderAdvance(advance);
            String format = mResources.getString(R.string.cx_fa_nls_reminder_label_date_advance);
            return String.format(format, dateTag, advanceTag);
        }
    }

    public String createNLSReminderPeriodLabelForPopDialog(CalendarDataObj reminder) {
        String dateTag = null;
        String advanceTag = null;
        int period = reminder.getCycle();

        switch (period) {
            case CalendarController.sReminderPeriodOnce:
                dateTag = createNLSReminderPeriodForOncePeriod(reminder);
                break;
            case CalendarController.sReminderPeriodDaily:
                dateTag = createNLSReminderPeriodForDaily(reminder);
                break;

            case CalendarController.sReminderPeriodWeekly:
                dateTag = createNLSReminderPeriodForWeekly(reminder);
                break;

            case CalendarController.sReminderPeriodMonthly:
                dateTag = createNLSReminderPeriodForMonthly(reminder);
                break;

            case CalendarController.sReminderPeriodAnnually:
                dateTag = createNLSReminderPeriodForAnnually(reminder);
                break;
        }

        int advance = reminder.getAdvance();
        if (advance == CalendarController.sReminderAdvanceNone) {
            return dateTag;
        } else {
            advanceTag = getNLSReminderAdvance(advance);
            String format = mResources.getString(R.string.cx_fa_nls_reminder_label_date_advance);
            return String.format(format, dateTag, advanceTag);
        }
    }

    public String createNLSReminderTipLabel(CalendarDataObj reminder) {

        boolean amITheOwner = (reminder.getAuthor()
                .equals(CxGlobalParams.getInstance().getUserId()));
        boolean notifyBoth = (reminder.getTarget() == CalendarController.sReminderTargetBoth);
        boolean notifyMySelf = (reminder.getTarget() == CalendarController.sReminderTargetMyself);

        String me = null;
        String goal = null;

        if (amITheOwner) {
            me = mResources.getString(R.string.cx_fa_nls_me);
            if (notifyMySelf) {
                goal = mResources.getString(R.string.cx_fa_nls_myself);
            } else if (notifyBoth) {
                goal = mResources.getString(R.string.cx_fa_nls_us);
            } else {
                goal = mResources.getString(CxResourceString.getInstance().str_reminder_name);
            }
        } else {
            if(TextUtils.isEmpty(CxGlobalParams.getInstance().getPartnerName())){
                me = mResources.getString(CxResourceString.getInstance().str_reminder_name);
            } else {
                me = mResources.getString(CxResourceString.getInstance().str_reminder_name)+CxGlobalParams.getInstance().getPartnerName();
            }
            if (notifyBoth) {
                goal = mResources.getString(R.string.cx_fa_nls_us);
            } else {
                goal = mResources.getString(R.string.cx_fa_nls_me);
            }
        }
        String format = mResources.getString(R.string.cx_fa_nls_reminder_tip_format);
        return String.format(format, me, goal);
    }

    public static String getDateStr(long time) {
        Date date = new Date(time);
        return date.toString();
    }
    
    public static Date getDate(long time) {
        Date date = new Date(time);
        return date;
    }

    public String createNLSReminderHappenLabel(CalendarDataObj reminder) throws Exception {
        long baseTime = (long)(reminder.getRealTimestamp()) * 1000;
        int advance = reminder.getAdvance();

        long currentMs = new Date().getTime();
        // long real = ReminderController.getRealTime(baseTime, advance);
        long real = baseTime;

        if (real < currentMs)
            return null;

        Date curDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(real);
        long diffMs = endDate.getTime() - curDate.getTime();
        long days = diffMs / (1000 * 3600 * 24);
        long remainderMs = diffMs % (1000 * 3600 * 24);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(curDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(currentMs + remainderMs));

        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        days = (day1 == day2) ? days : (days + 1);

        long hours = (diffMs / (60 * 60 * 1000) - days * 24);
        long mins = ((diffMs / (60 * 1000)) - days * 24 * 60 - hours * 60);
        long s = (diffMs / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60);
        CxLog.v(TAG, "days= " + days);
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
            return mResources.getString(R.string.cx_fa_nls_reminder_tip_tomorrow);
        } else if (days == 2) {
            return mResources.getString(R.string.cx_fa_nls_reminder_tip_after_tomorrow);
        } else if (days > 2) {
            String format = mResources.getString(R.string.cx_fa_nls_reminder_tip_future_format);
            return String.format(format, days);
        } else {
            return mResources.getString(R.string.cx_fa_nls_reminder_tip_future_years);
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

    public long getSecond(CalendarDataObj reminder) {
        long baseTime = (long)(reminder.getRealTimestamp()) * 1000;
        int advance = reminder.getAdvance();

        long currentMs = new Date().getTime();
        // long real = ReminderController.getRealTime(baseTime, advance);
        long real = baseTime;

        if (real < currentMs)
            return 0;

        Date curDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(real);
        long diffMs = endDate.getTime() - curDate.getTime();
        long days = diffMs / (1000 * 3600 * 24);
        long remainderMs = diffMs % (1000 * 3600 * 24);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(curDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(currentMs + remainderMs));

        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        days = (day1 == day2) ? days : (days + 1);

        long hours = (diffMs / (60 * 60 * 1000) - days * 24);
        long mins = ((diffMs / (60 * 1000)) - days * 24 * 60 - hours * 60);
        long s = (diffMs / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60);
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
