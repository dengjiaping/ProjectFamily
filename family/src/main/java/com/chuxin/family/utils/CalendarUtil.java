package com.chuxin.family.utils;

import com.chuxin.family.calendar.CalendarDisplayUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {
	private static CalendarUtil sInstance = new CalendarUtil();
	private CalendarUtil() {}
	
	public static CalendarUtil getInstance() {
		return sInstance;
	}
	
	
	
	public boolean isSame(Calendar c1,Calendar c2){
		return c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH) 
			&& c1.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH);
		
	}
	
	public boolean isSameMonth(Calendar c1,Calendar c2){
		return c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH);
		
	}
	
	public boolean beforeMonth(Calendar c1, Calendar c2){
	    if(c1.get(Calendar.YEAR)<c2.get(Calendar.YEAR)){
	        return true;
	    } else if(c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR)){
	        if(c1.get(Calendar.MONTH)<c2.get(Calendar.MONTH)){
	            return true;
	        }
	    }
	    return false;
	    //return !((c1.get(Calendar.YEAR)>=c2.get(Calendar.YEAR)) && (c1.get(Calendar.MONTH)>=c2.get(Calendar.MONTH)));
	}
	
	public boolean beforeYear(Calendar c1, Calendar c2){
	    if(c1.get(Calendar.YEAR)<c2.get(Calendar.YEAR)){
            return true;
        }
	    return false;
	}
	
	
	public int importDayOfWeek(int dayOfWeek) throws Exception {
		switch (dayOfWeek) {
		case 1:
			return 7;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			return dayOfWeek - 1;
		default:
			throw new Exception("Error: importDayOfWeek failed with " + dayOfWeek);
		}
	}
	
	public int exportDayOfWeek(int flag) throws Exception {
		switch (flag) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			return flag + 1;
		case 7:
			return 1;
		default:
			throw new Exception("Error: exportDayOfWeek failed with" + flag);
		}
	}
	
	public List<Calendar> getCurMonthTheWeekCalendars(Calendar cal, int m){
	    List<Calendar> curMonthCals = new ArrayList<Calendar>();
	    int day = cal.get(Calendar.DAY_OF_WEEK);
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    Calendar c1 = Calendar.getInstance();
	    c1.set(Calendar.YEAR, year);
	    c1.set(Calendar.MONTH, (m-1));
	    c1.set(Calendar.DAY_OF_WEEK, day);
	    c1.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
	    //curMonthCals.add(c1);
	    
	    Calendar c3 = Calendar.getInstance();
	    c3.setTime(c1.getTime());
	    
	    for(int i=0; i<5; i++){
	        //RkLog.i("shichao.wang", "c1="+CalendarDisplayUtility.getDateStr(c1.getTimeInMillis()));
	           Calendar c2 = Calendar.getInstance();
	            c2.setTime(c1.getTime());
	            //RkLog.i("shichao.wang", "c3="+CalendarDisplayUtility.getDateStr(c3.getTimeInMillis()));
	            if(isSameMonth(c2, c3)){
	                //RkLog.i("shichao.wang", "c2="+CalendarDisplayUtility.getDateStr(c2.getTimeInMillis()));
	                if(!c2.before(cal)){
	                    curMonthCals.add(c2);
	                }
	            }
	        c1.add(Calendar.DAY_OF_MONTH, 7);

	       

	    }
//	    for(int j=0; j<curMonthCals.size(); j++){
//	        RkLog.i("shichao.wang", "curMonthCals="+CalendarDisplayUtility.getDateStr(curMonthCals.get(j).getTimeInMillis()));
//	    }
	    return curMonthCals;
	}
	
}
