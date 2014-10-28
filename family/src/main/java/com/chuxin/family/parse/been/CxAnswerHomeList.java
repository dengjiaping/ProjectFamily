package com.chuxin.family.parse.been;

import java.util.ArrayList;

import com.chuxin.family.parse.been.data.AnswerHomeRateItem;
import com.chuxin.family.parse.been.data.AnswerHomeTotalItem;
import com.chuxin.family.parse.been.data.AnswerHomeUserInfo;
import com.chuxin.family.parse.been.data.AnswerHomeWeekItem;

public class CxAnswerHomeList extends CxParseBasic {

	
	private int total_score;
	private int wife_score;
	private int husband_score;
	private int right_rate;
	private int today_remain;
	
	private int weekRank;
	
	private ArrayList<AnswerHomeWeekItem> weekItems;
	private ArrayList<AnswerHomeTotalItem> totalItems;
	private ArrayList<AnswerHomeRateItem> rateItems;
	
	private AnswerHomeUserInfo userInfo;
	
	
	public int getWeekRank() {
		return weekRank;
	}


	public void setWeekRank(int weekRank) {
		this.weekRank = weekRank;
	}


	public AnswerHomeUserInfo getUserInfo() {
		return userInfo;
	}


	public void setUserInfo(AnswerHomeUserInfo userInfo) {
		this.userInfo = userInfo;
	}


	public ArrayList<AnswerHomeWeekItem> getWeekItems() {
		return weekItems;
	}


	public void setWeekItems(ArrayList<AnswerHomeWeekItem> weekItems) {
		this.weekItems = weekItems;
	}


	public ArrayList<AnswerHomeTotalItem> getTotalItems() {
		return totalItems;
	}


	public void setTotalItems(ArrayList<AnswerHomeTotalItem> totalItems) {
		this.totalItems = totalItems;
	}


	public ArrayList<AnswerHomeRateItem> getRateItems() {
		return rateItems;
	}


	public void setRateItems(ArrayList<AnswerHomeRateItem> rateItems) {
		this.rateItems = rateItems;
	}


	public int getTotal_score() {
		return total_score;
	}


	public void setTotal_score(int total_score) {
		this.total_score = total_score;
	}


	public int getWife_score() {
		return wife_score;
	}


	public void setWife_score(int wife_score) {
		this.wife_score = wife_score;
	}


	public int getHusband_score() {
		return husband_score;
	}


	public void setHusband_score(int husband_score) {
		this.husband_score = husband_score;
	}


	public int getRight_rate() {
		return right_rate;
	}


	public void setRight_rate(int right_rate) {
		this.right_rate = right_rate;
	}


	public int getToday_remain() {
		return today_remain;
	}


	public void setToday_remain(int today_remain) {
		this.today_remain = today_remain;
	}



	
	
	
	
	
	
	
}
