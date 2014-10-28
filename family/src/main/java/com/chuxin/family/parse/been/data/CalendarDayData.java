package com.chuxin.family.parse.been.data;

import java.util.ArrayList;

import com.chuxin.family.models.CalendarDataObj;

public class CalendarDayData {

	
	private boolean hasItem=false;
	private boolean hasMemorial=false;
	private boolean isRead=true;
	
	private ArrayList<CalendarDataObj> items; //包含事项和纪念日
	
	
	
	public boolean isHasItem() {
		return hasItem;
	}
	public void setHasItem(boolean hasItem) {
		this.hasItem = hasItem;
	}
	public boolean isHasMemorial() {
		return hasMemorial;
	}
	public void setHasMemorial(boolean hasMemorial) {
		this.hasMemorial = hasMemorial;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public ArrayList<CalendarDataObj> getItems() {
		return items;
	}
	public void setItems(ArrayList<CalendarDataObj> items) {
		this.items = items;
	}

	
	
}
