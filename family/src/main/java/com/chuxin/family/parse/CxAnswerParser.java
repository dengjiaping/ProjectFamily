package com.chuxin.family.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.been.CxAnswerHomeList;
import com.chuxin.family.parse.been.CxAnswerQuestionData;
import com.chuxin.family.parse.been.CxAnswerResultData;
import com.chuxin.family.parse.been.data.AnswerHomeRateItem;
import com.chuxin.family.parse.been.data.AnswerHomeTotalItem;
import com.chuxin.family.parse.been.data.AnswerHomeUserInfo;
import com.chuxin.family.parse.been.data.AnswerHomeWeekItem;
import com.chuxin.family.parse.been.data.AnswerQuestionItem;
/**
 * 谁家最聪明解析类
 * @author wentong.men
 *
 */
public class CxAnswerParser {

	
	/**
	 * 首页数据解析
	 * @param offset
	 * @param obj
	 * @param context
	 * @return
	 */
	public CxAnswerHomeList getAnswerHomeList(int offset,JSONObject obj,Context context){
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAnswerHomeList list=new CxAnswerHomeList();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}
		
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {			
			return list;
		}
		
		try {
			if (!dataObj.isNull("total_score")) {
				list.setTotal_score(dataObj.getInt("total_score"));
			}
			if (!dataObj.isNull("wife_score")) {
				list.setWife_score(dataObj.getInt("wife_score"));
			}
			if (!dataObj.isNull("husband_score")) {
				list.setHusband_score(dataObj.getInt("husband_score"));
			}
			if (!dataObj.isNull("right_rate")) {
				list.setRight_rate(dataObj.getInt("right_rate"));
			}
			if (!dataObj.isNull("today_remain")) {
				list.setToday_remain(dataObj.getInt("today_remain"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONObject userObj=null;
		try {
			userObj=dataObj.getJSONObject("groups");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(userObj==null){
			return list;
		}
		
		JSONObject userInfoObj=null;
		try {
			userInfoObj=userObj.getJSONObject(CxGlobalParams.getInstance().getPairId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(userInfoObj==null){
			return list;
		}
		
		AnswerHomeUserInfo userInfo=new AnswerHomeUserInfo();
		try {
			if(!userInfoObj.isNull("name0")){
				userInfo.setWifeName(userInfoObj.getString("name0"));					
			}
			if(!userInfoObj.isNull("name1")){
				userInfo.setHusbandName(userInfoObj.getString("name1"));					
			}
			if(!userInfoObj.isNull("avatar0")){
				userInfo.setWifeUrl(userInfoObj.getString("avatar0"));					
			}
			if(!userInfoObj.isNull("avatar1")){
				userInfo.setHusbandUrl(userInfoObj.getString("avatar1"));					
			}		
		} catch (JSONException e) {				
			e.printStackTrace();
		}
		list.setUserInfo(userInfo);
		
		
		JSONArray weekArr=null;
		try {
			weekArr=dataObj.getJSONArray("week_rank");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(weekArr==null || weekArr.length()<1){
			return list;
		}
		
		
		ArrayList<AnswerHomeWeekItem> weekItems=new ArrayList<AnswerHomeWeekItem>();
		
		for(int i=0;i<weekArr.length();i++){
			JSONObject weekObj=null;
			try {
				weekObj=weekArr.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			if(weekObj==null){
				continue;
			}

			AnswerHomeWeekItem weekItem=new AnswerHomeWeekItem();
			JSONObject userItemObj=null;
			try {
				if(!weekObj.isNull("group_id")){
					String weekRank = weekObj.getInt("group_id")+"";
					if(CxGlobalParams.getInstance().getPairId().equals(weekRank)){
						list.setWeekRank(i);
					}
					weekItem.setGroupId(weekRank);
					userItemObj=userObj.getJSONObject(weekRank);
				}
				if(!weekObj.isNull("value")){
					weekItem.setWeekScore(weekObj.getInt("value")+"");
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			
			if(userItemObj==null){
				continue;
			}
			
			try {
				if(!userItemObj.isNull("name0")){
					weekItem.setWifeName(userItemObj.getString("name0"));					
				}
				if(!userItemObj.isNull("name1")){
					weekItem.setHusbandName(userItemObj.getString("name1"));					
				}
				if(!userItemObj.isNull("avatar0")){
					weekItem.setWifeUrl(userItemObj.getString("avatar0"));					
				}
				if(!userItemObj.isNull("avatar1")){
					weekItem.setHusbandUrl(userItemObj.getString("avatar1"));					
				}
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			weekItems.add(weekItem);	
		}
		list.setWeekItems(weekItems);
		
		JSONArray totalArr=null;
		try {
			totalArr=dataObj.getJSONArray("top_rank");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(totalArr==null || totalArr.length()<1){
			return list;
		}
		
		ArrayList<AnswerHomeTotalItem> totalItems=new ArrayList<AnswerHomeTotalItem>();
		
		for(int i=0;i<totalArr.length();i++){
			JSONObject totalObj=null;
			try {
				totalObj=totalArr.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			if(totalObj==null){
				continue;
			}

			AnswerHomeTotalItem totalItem=new AnswerHomeTotalItem();
			JSONObject userItemObj=null;
			try {
				if(!totalObj.isNull("group_id")){
					totalItem.setGroupId(totalObj.getInt("group_id")+"");
					userItemObj=userObj.getJSONObject(totalObj.getInt("group_id")+"");
				}
				if(!totalObj.isNull("value")){
					totalItem.setTotalScore(totalObj.getInt("value")+"");
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			
			if(userItemObj==null){
				continue;
			}
			
			try {
				if(!userItemObj.isNull("name0")){
					totalItem.setWifeName(userItemObj.getString("name0"));					
				}
				if(!userItemObj.isNull("name1")){
					totalItem.setHusbandName(userItemObj.getString("name1"));					
				}
				if(!userItemObj.isNull("avatar0")){
					totalItem.setWifeUrl(userItemObj.getString("avatar0"));					
				}
				if(!userItemObj.isNull("avatar1")){
					totalItem.setHusbandUrl(userItemObj.getString("avatar1"));					
				}
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			totalItems.add(totalItem);	
		}
		list.setTotalItems(totalItems);
		
		
		
		JSONArray rateArr=null;
		try {
			rateArr=dataObj.getJSONArray("rate_rank");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(rateArr==null || rateArr.length()<1){
			return list;
		}
		
		
		ArrayList<AnswerHomeRateItem> rateItems=new ArrayList<AnswerHomeRateItem>();
		
		for(int i=0;i<rateArr.length();i++){
			JSONObject rateObj=null;
			try {
				rateObj=rateArr.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			if(rateObj==null){
				continue;
			}

			AnswerHomeRateItem rateItem=new AnswerHomeRateItem();
			JSONObject userItemObj=null;
			try {
				if(!rateObj.isNull("group_id")){
					rateItem.setGroupId(rateObj.getInt("group_id")+"");
					userItemObj=userObj.getJSONObject(rateObj.getInt("group_id")+"");
				}
				if(!rateObj.isNull("value")){
					rateItem.setRate(rateObj.getInt("value")+"");
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			
			if(userItemObj==null){
				continue;
			}
			
			try {
				if(!userItemObj.isNull("name0")){
					rateItem.setWifeName(userItemObj.getString("name0"));					
				}
				if(!userItemObj.isNull("name1")){
					rateItem.setHusbandName(userItemObj.getString("name1"));					
				}
				if(!userItemObj.isNull("avatar0")){
					rateItem.setWifeUrl(userItemObj.getString("avatar0"));					
				}
				if(!userItemObj.isNull("avatar1")){
					rateItem.setHusbandUrl(userItemObj.getString("avatar1"));					
				}
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			rateItems.add(rateItem);	
		}
		list.setRateItems(rateItems);
	
		return list;
	}
	
	
	
	/**
	 * 答题界面返回问题解析
	 * @param obj
	 * @return
	 */
	public CxAnswerQuestionData getQuestion(JSONObject obj){
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAnswerQuestionData list=new CxAnswerQuestionData();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}
		
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {			
			return list;
		}
		
		try {
			if (!dataObj.isNull("id")) {
				list.setId(dataObj.getString("id"));
			}
			if (!dataObj.isNull("question")) {
				list.setQuestion(dataObj.getString("question"));
			}
			if (!dataObj.isNull("result")) {
				list.setResult(dataObj.getString("result"));
			}
			if (!dataObj.isNull("today_remain")) {
				list.setToday_remain(dataObj.getInt("today_remain"));
			}
			if (!dataObj.isNull("score")) {
				list.setScore(dataObj.getInt("score"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray answerArr=null;
		try {
			answerArr=dataObj.getJSONArray("answer");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(answerArr==null || answerArr.length()<1){
			return list;
		}
		
		ArrayList<AnswerQuestionItem> items=new ArrayList<AnswerQuestionItem>();
		for (int i = 0; i < answerArr.length(); i++) {
			JSONObject answerObj=null;
			try {
				answerObj=answerArr.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			if(answerObj==null){
				continue;
			}			
			AnswerQuestionItem item=new AnswerQuestionItem();			
			try {
				if (!answerObj.isNull("id")) {
					item.setId(answerObj.getString("id"));
				}
				if (!answerObj.isNull("text")) {
					item.setText(answerObj.getString("text"));
				}		
			} catch (JSONException e) {
				e.printStackTrace();
			}
			items.add(item);
		}
		
		list.setItems(items);
		
		
		
		return list;
	}
	
	
	/**
	 * 答题界面提交答案后返回值解析
	 * @param obj
	 * @return
	 */
	public CxAnswerResultData getResult(JSONObject obj){
		
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
		}
		if (-1 == rc) {
			return null;
		}
		
		CxAnswerResultData list=new CxAnswerResultData();
		list.setRc(rc);
		
		try {
			if (!obj.isNull("msg")) {
				list.setMsg(obj.getString("msg"));
			}
			if (!obj.isNull("ts")) {
				list.setTs(obj.getInt("ts"));
			}			
		} catch (JSONException e) {
		}
		
		if (0 != rc) {
			return list;
		}
		
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {			
			return list;
		}
		
		try {
			if(!dataObj.isNull("tips")){
				list.setTips(dataObj.getInt("tips"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
