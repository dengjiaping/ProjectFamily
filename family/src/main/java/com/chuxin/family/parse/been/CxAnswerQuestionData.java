package com.chuxin.family.parse.been;

import java.util.ArrayList;

import com.chuxin.family.parse.been.data.AnswerHomeRateItem;
import com.chuxin.family.parse.been.data.AnswerHomeTotalItem;
import com.chuxin.family.parse.been.data.AnswerHomeUserInfo;
import com.chuxin.family.parse.been.data.AnswerHomeWeekItem;
import com.chuxin.family.parse.been.data.AnswerQuestionItem;

public class CxAnswerQuestionData extends CxParseBasic {

	private String id;
	private String question;
	private String result;
	private int score;
	private int today_remain;
	
	private ArrayList<AnswerQuestionItem> items;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getToday_remain() {
		return today_remain;
	}

	public void setToday_remain(int today_remain) {
		this.today_remain = today_remain;
	}

	public ArrayList<AnswerQuestionItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<AnswerQuestionItem> items) {
		this.items = items;
	}


	
}
