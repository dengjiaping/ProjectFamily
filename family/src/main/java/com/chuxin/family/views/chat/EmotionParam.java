package com.chuxin.family.views.chat;

import java.util.ArrayList;
import java.util.List;

import com.chuxin.family.model.CxSubjectInterface;
import com.chuxin.family.parse.been.data.EmotionSet;

public class EmotionParam extends CxSubjectInterface {

	private static EmotionParam param;
	private EmotionParam(){};
	
	public static EmotionParam getInstance(){
		if (null == param) {
			param = new EmotionParam();
		}
		return param;
	}
	
	
	private ArrayList<EmotionSet> emotions; //表情配置文件
	public ArrayList<EmotionSet> getEmotions() {
		return emotions;
	}

	public void setEmotions(ArrayList<EmotionSet> emotions) {
		this.emotions = emotions;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
