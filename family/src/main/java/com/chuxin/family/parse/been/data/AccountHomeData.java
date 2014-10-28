package com.chuxin.family.parse.been.data;

import java.util.ArrayList;

public class AccountHomeData {

	private String monthIn;
	private String monthOut;
	private String monthSurplus;
	private String yearSurplus;
	
	private ArrayList<AccountHomeItem> list;

	public String getMonthIn() {
		return monthIn;
	}

	public void setMonthIn(String monthIn) {
		this.monthIn = monthIn;
	}

	public String getMonthOut() {
		return monthOut;
	}

	public void setMonthOut(String monthOut) {
		this.monthOut = monthOut;
	}

	public String getMonthSurplus() {
		return monthSurplus;
	}

	public void setMonthSurplus(String monthSurplus) {
		this.monthSurplus = monthSurplus;
	}

	public String getYearSurplus() {
		return yearSurplus;
	}

	public void setYearSurplus(String yearSurplus) {
		this.yearSurplus = yearSurplus;
	}

	public ArrayList<AccountHomeItem> getList() {
		return list;
	}

	public void setList(ArrayList<AccountHomeItem> list) {
		this.list = list;
	}
	
	
	
	
}
