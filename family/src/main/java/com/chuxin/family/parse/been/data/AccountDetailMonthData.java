package com.chuxin.family.parse.been.data;

import java.util.ArrayList;

public class AccountDetailMonthData {

	
	private String in;
	private String out;
	private String surplus;
	private ArrayList<AccountDetailMonthItem> list;
	public String getIn() {
		return in;
	}
	public void setIn(String in) {
		this.in = in;
	}
	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}
	public String getSurplus() {
		return surplus;
	}
	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}
	public ArrayList<AccountDetailMonthItem> getList() {
		return list;
	}
	public void setList(ArrayList<AccountDetailMonthItem> list) {
		this.list = list;
	}

	
}
