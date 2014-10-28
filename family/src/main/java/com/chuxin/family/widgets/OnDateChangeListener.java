package com.chuxin.family.widgets;

public interface OnDateChangeListener {
	public void onDateChange(DatePicker view, int year,
			int monthOfYear, int dayOfMonth);
	
	public void onTimeChange(TimePicker view, int hourOfDay, int minute);
}
