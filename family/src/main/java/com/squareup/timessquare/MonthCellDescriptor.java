// Copyright 2012 Square, Inc.

package com.squareup.timessquare;

import com.chuxin.family.models.CalendarDataObj;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Describes the state of a particular date cell in a {@link CalendarView}. */
class MonthCellDescriptor {
    private final Date date;  //

    private final int value;  //日   

    private final boolean isCurrentMonth;  //是不是当前月

    private boolean isSelected; //是不是被选中了

    private final boolean isToday; //是不是今天

    private final boolean isSelectable; //是不是可选中

    private boolean isShowItem; //有没有事项

    private boolean isShowMemorial; //有没有纪念日

    private boolean isRead; //是否已读
    
    private String lunarStrDay;
    
    private List<CalendarDataObj> mListCalendarDatas = new ArrayList<CalendarDataObj>();

    MonthCellDescriptor(Date date,String lunarStr, boolean currentMonth, boolean selectable, boolean selected,
            boolean today, boolean isshowitem, boolean isshowmemorial, boolean isread, int value) {
        this.date = date;
        isCurrentMonth = currentMonth;
        isSelectable = selectable;
        isSelected = selected;
        isToday = today;
        this.isShowItem = isshowitem;
        this.isShowMemorial = isshowmemorial;
        this.value = value;
        this.isRead = isread;
        this.lunarStrDay = lunarStr;
    }

    public String getLunarDateStr(){
        return lunarStrDay;
    }
    
    public Date getDate() {
        return date;
    }

    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isToday() {
        return isToday;
    }

    public int getValue() {
        return value;
    }

    public boolean isShowItem() {
        return isShowItem;
    }

    public boolean isHasRead() {
        return isRead;
    }

    public void setHasRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setShowItem(boolean isshowitem) {
        this.isShowItem = isshowitem;
    }

    public boolean isShowMemorial() {
        return isShowMemorial;
    }

    public void setShowMemorial(boolean isshowmemorial) {
        this.isShowMemorial = isshowmemorial;
    }

    public List<CalendarDataObj> getListCalendarDatas() {
        return mListCalendarDatas;
    }

    public void setListCalendarDatas(List<CalendarDataObj> lisCalendarDatas) {
        this.mListCalendarDatas = lisCalendarDatas;
    }

    @Override
    public String toString() {
        return "MonthCellDescriptor{" + "date=" + date + ", value=" + value + ", isCurrentMonth="
                + isCurrentMonth + ", isSelected=" + isSelected + ", isToday=" + isToday
                + ", isSelectable=" + isSelectable + ", isShowItem=" + isShowItem
                + ", isShowMemorial=" + isShowMemorial +'}';
    }
}
