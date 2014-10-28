
package com.chuxin.family.utils;

import com.chuxin.family.calendar.ChineseCalendar;
import com.chuxin.family.parse.been.data.DateData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期相关的工具类
 * 
 * @author dujy
 */
public class DateUtil {

    /**
     * 该方法用于获取一个long型时间的数字时间值
     * 
     * @param time 如<0则为当前时间
     * @return DateData 为数字时间的封住类
     */
    public static DateData getNumberTime(long time) {
        if (time < 0) {
            time = System.currentTimeMillis();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time);
        DateData date = new DateData();
        date.setYear(c1.get(Calendar.YEAR));
        date.setMonth(c1.get(Calendar.MONTH));
        date.setDay(c1.get(Calendar.DAY_OF_MONTH));
        date.setHour(c1.get(Calendar.HOUR_OF_DAY));
        date.setMinute(c1.get(Calendar.MINUTE));
        date.setSecond(c1.get(Calendar.SECOND));
        date.setWeekday(c1.get(Calendar.DAY_OF_WEEK));
        date.setDateStr(format.format(c1.getTime()));

        return date;

    }

    public static DateData getNumberTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(date.getTime());
        DateData data = new DateData();
        data.setYear(c1.get(Calendar.YEAR));
        data.setMonth(c1.get(Calendar.MONTH));
        data.setDay(c1.get(Calendar.DAY_OF_MONTH));
        data.setHour(c1.get(Calendar.HOUR_OF_DAY));
        data.setMinute(c1.get(Calendar.MINUTE));
        data.setSecond(c1.get(Calendar.SECOND));
        data.setWeekday(c1.get(Calendar.DAY_OF_WEEK));
        data.setDateStr(format.format(c1.getTime()));

        return data;

    }

    public static String getStringMonth(Date date) {
        DateData data = getNumberTime(date);
        String monthStr = (data.getMonth()+1) > 9 ? (data.getMonth()+1) + "" : "0" + (data.getMonth()+1);
        String monthDate = String.valueOf(data.getYear()) + monthStr;
        return monthDate;

    }
    
    public static String getStringDay(Date date) {
        DateData data = getNumberTime(date);
        String dayStr = data.getDay() > 9 ? data.getDay() + "" : "0" + data.getDay();
        return dayStr;
    }
    
    public static String getStringDate(Date date){
        String dateStr = getStringMonth(date)+getStringDay(date)+"";
        return dateStr;
    }

    /**
     * @param time 必须为"20121221" 形式
     * @return
     */
    public static DateData getNumberTime(String time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, Integer.parseInt(time.substring(0, 4)));
        c1.set(Calendar.MONTH, Integer.parseInt(time.substring(4, 6)) - 1);
        c1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(time.substring(6, 8)));

        DateData date = new DateData();
        date.setYear(c1.get(Calendar.YEAR));
        date.setMonth(c1.get(Calendar.MONTH));
        date.setDay(c1.get(Calendar.DAY_OF_MONTH));
        date.setHour(c1.get(Calendar.HOUR_OF_DAY));
        date.setMinute(c1.get(Calendar.MINUTE));
        date.setSecond(c1.get(Calendar.SECOND));
        date.setWeekday(c1.get(Calendar.DAY_OF_WEEK));
        date.setDateStr(format.format(c1.getTime()));

        return date;

    }

    public static String getCatipalNumber(int number) {
        String str = "";
        switch (number) {
            case 7:
                str = "星期六";
                break;
            case 1:
                str = "星期日";
                break;
            case 2:
                str = "星期一";
                break;
            case 3:
                str = "星期二";
                break;
            case 4:
                str = "星期三";
                break;
            case 5:
                str = "星期四";
                break;
            case 6:
                str = "星期五";
                break;
            default:
                break;
        }

        return str;
    }

    /**
     * 某个时间距现在的距离 (主要用于二人空间的回复)
     * 
     * @param agoTime ： 以前的某个时间点，单位为秒
     * @return
     */
    public static String getTimeDiffWithNow(int agoTime) {
        long now = System.currentTimeMillis();
        long ago = agoTime * 1000L;

        Calendar c1 = Calendar.getInstance();

        c1.setTimeInMillis(now);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(ago);

        // 如果客户端的时间有问题，造成过去的时间大于当前时间的情况
        if (ago > now) {
            return "刚刚";
        }

        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);

        int mon1 = c1.get(Calendar.MONTH);
        int mon2 = c2.get(Calendar.MONTH);

        int day1 = c1.get(Calendar.DATE);
        int day2 = c2.get(Calendar.DATE);

        int hour1 = c1.get(Calendar.HOUR_OF_DAY);
        int hour2 = c2.get(Calendar.HOUR_OF_DAY);

        int min1 = c1.get(Calendar.MINUTE);
        int min2 = c2.get(Calendar.MINUTE);

        int sec1 = c1.get(Calendar.SECOND);
        int sec2 = c2.get(Calendar.SECOND);

        if (year1 != year2) {
            CxLog.e("DateUtil_men", year1 + ">>>>>" + year2);
            return (year1 - year2) + "年前";
        } else if (mon1 != mon2) {
            CxLog.e("DateUtil_men", mon1 + ">>>>>" + mon2);
            return (mon1 - mon2) + "月前";
        } else if (day1 != day2) {
            CxLog.e("DateUtil_men", day1 + ">>>>>" + day2);
            return (day1 - day2) + "天前";
        } else if (hour1 != hour2) {
            CxLog.e("DateUtil_men", hour1 + ">>>>>" + hour2);
            return (hour1 - hour2) + "小时前";
        } else if (min1 != min2) {
            CxLog.e("DateUtil_men", min1 + ">>>>>" + min2);
            return (min1 - min2) + "分钟前";
        } else if (sec1 != sec2) {
            CxLog.e("DateUtil_men", sec1 + ">>>>>" + sec2);
            return (sec1 - sec2) + "秒前";
        } else if (sec1 == sec2) {
            return "刚刚";
        }

        return "";
    }

    /**
     * 判断两个日期是否是同一天
     * 
     * @param t1
     * @param t2
     * @return
     */
    public static boolean isTheSameDay(long t1, long t2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(t1);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(t2);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);

        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);

        if (year1 == year2 && month1 == month2 && day1 == day2) {
            return true;
        } else {
            return false;
        }
    }
    
    public static int getDaysByNow(Date createDate,String date){
    	
    	Calendar oldC = Calendar.getInstance();
    	Calendar nowC = Calendar.getInstance();
    	oldC.setTime(createDate);
    	nowC.setTime(createDate);
    	oldC.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
    	oldC.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6))-1);
    	oldC.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6, 8)));
    	oldC.add(Calendar.HOUR, 1);
    	
    	int day = (int)((oldC.getTimeInMillis() - nowC.getTimeInMillis()) / (1000 * 3600 * 24));

    	return day;
    }
    public static String getChildAge(String date){
    	return  getChildAge(new Date(), date);
    }
    
    
    public static String getChildAge(Date createDate,String date){
    	int year=0,month=0,day=0;
    	
    	Calendar oldC = Calendar.getInstance();
    	Calendar newC = Calendar.getInstance();
    	oldC.setTime(createDate);
    	newC.setTime(createDate);
    	oldC.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
    	oldC.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6))-1);
    	oldC.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6, 8)));
    	oldC.add(Calendar.HOUR, -1); 
    	
    	if(newC.getTimeInMillis()<oldC.getTimeInMillis()){
    		return null;
    	}
    	
    	year=newC.get(Calendar.YEAR)-oldC.get(Calendar.YEAR);
    	if(newC.get(Calendar.MONTH)<oldC.get(Calendar.MONTH)){
    		year--;
    		month=newC.get(Calendar.MONTH)+12-oldC.get(Calendar.MONTH);
    	}else{
    		month=newC.get(Calendar.MONTH)-oldC.get(Calendar.MONTH);
    	}
    	
    	if(newC.get(Calendar.DAY_OF_MONTH)<oldC.get(Calendar.DAY_OF_MONTH)){
    		int i=ChineseCalendar.daysInGregorianMonth(oldC.get(Calendar.YEAR),oldC.get(Calendar.MONTH));
    		if(month==0){
    			year--;
    			month=11;
    			day=newC.get(Calendar.DAY_OF_MONTH)+i-oldC.get(Calendar.DAY_OF_MONTH);
    		}else{
    			month--;
    			day=newC.get(Calendar.DAY_OF_MONTH)+i-oldC.get(Calendar.DAY_OF_MONTH);
    		}
    	}else{
    		day=newC.get(Calendar.DAY_OF_MONTH)-oldC.get(Calendar.DAY_OF_MONTH);
    	}
    	
    	return year+":"+month+":"+day;
    }
    
    
    
}
