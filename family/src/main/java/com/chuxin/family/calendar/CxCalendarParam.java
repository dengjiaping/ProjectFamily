package com.chuxin.family.calendar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuxin.family.model.CxSubjectInterface;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.parse.been.data.CalendarDayData;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxCalendarParam extends CxSubjectInterface {

	
	
	private CxCalendarParam(){};
	
	private static CxCalendarParam param;
	
	public static CxCalendarParam getInstance(){
		if (null == param) {
			param = new CxCalendarParam();
		}
		return param;
	}
	
	public static final String CALENDAR_EDIT_TYPE="calendar_edit_type";//跳转到edit页面的参数 区分是事项还是纪念日。
	public static final int CALENDAR_TYPE_ITEM=1;
	public static final int CALENDAR_TYPE_MEMORIAL=2;
	
	public static final String CALENDAR_MEMORIAL_TYPE="calendar_memorial_type";//跳转到edit页面的参数 区分是一般纪念日还是生日
	public static final int CALENDAR_MEMORIAL_NORMAL=1;
	public static final int CALENDAR_MEMORIAL_BIRTHDAY=2;
	
	public static final String CALENDAR_EDIT_MODE="calendar_edit_mode";//跳转到edit页面的参数 区分是添加还是修改
	public static final int CALENDAR_EDIT_ADD=1;
	public static final int CALENDAR_EDIT_UPDATE=2;
	
	public static final String CALENDAR_EDIT_DATE="calendar_edit_date"; //跳转到edit页面的参数  默认时间
	
	
	public static final String CALENDAR_COMMON_MEMORIAL="calendar_common_memorial";//常用纪念日参数
	
	
	private HashMap<Integer, CalendarDayData>  currentMonthMap=new HashMap<Integer, CalendarDayData>();

	public HashMap<Integer, CalendarDayData> getCurrentMonthMap() {
		return currentMonthMap;
	}


	public void setCurrentMonthMap(HashMap<Integer, CalendarDayData> currentMonthMap) {
		this.currentMonthMap = currentMonthMap;
	}
	
	private int fragment_type=1;

	public int getFragment_type() {
		return fragment_type;
	}


	public void setFragment_type(int fragment_type) {
		this.fragment_type = fragment_type;
	}
	
	
	
	
	
	
	
	
	
	
	
}
