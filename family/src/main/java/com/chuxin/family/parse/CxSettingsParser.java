package com.chuxin.family.parse;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.parse.been.CxChangeChatBackground;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.parse.been.data.CxChangeHeadDataField;

/**
 * 设置的网络请求解析
 * @author shichao.wang
 *
 */



public class CxSettingsParser {
	private static CxSettingsParser mParser;
	
	//
	public enum SendHeadImageType {
	    HEAD_ME, HEAD_PARTNER  
	}  
	
	private CxSettingsParser(){}
	
	public static CxSettingsParser getInstance(){
		if (null == mParser) {
			mParser = new CxSettingsParser();
		}
		return mParser;
	}
	
	/*修改头像*/
	public CxChangeHead parseChangeHead(Object obj, SendHeadImageType headType) {  
		if (null == obj) {
			return null;
		}
		JSONObject jObj = (JSONObject)obj;
		int rc = -1;
		try {
			rc = jObj.getInt("rc");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (-1 == rc) { //视为非法
			return null;
		}
		CxChangeHead changeHead = new CxChangeHead();
		changeHead.setRc(rc);
		try {
			changeHead.setMsg(jObj.getString("msg"));
		} catch (Exception e) {
		}
		try {
			changeHead.setTs(jObj.getInt("ts"));
		} catch (Exception e) {
		}
		
		if (0 != rc) { //如果rc==0,才有data字段
			return changeHead;
		}
		CxChangeHeadDataField dataField = new CxChangeHeadDataField();
		try {
			JSONObject dataObj = jObj.getJSONObject("data"); //这种情况一定存在data字段
			
			if (headType == SendHeadImageType.HEAD_ME) {
				if (!dataObj.isNull("icon_mid")) {
					dataField.setIcon_mid(dataObj.getString("icon_mid"));
				}
				if (!dataObj.isNull("icon_small")) {
					dataField.setIcon_small(dataObj.getString("icon_small"));
				}
				if (!dataObj.isNull("icon_big")) {
					dataField.setIcon_big(dataObj.getString("icon_big"));
				}
			} else {
				if (!dataObj.isNull("icon")) {
					dataField.setIcon_mid(dataObj.getString("icon"));
					dataField.setIcon_small(dataObj.getString("icon"));
					dataField.setIcon_big(dataObj.getString("icon"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		changeHead.setData(dataField);
		return changeHead;
	}

	/**
	 * 解析用户反馈
	 * @param obj
	 * @return 与登出返回的数据形式一样，都用RkLogoutResponce
	 */
	public CxLogoutResponce parseForUserSuggest(Object obj){
		if (null == obj) {
			return null;
		}
		JSONObject jsonObj = (JSONObject)obj;
		int rc = -1;
		String msg=null;
		try {
			rc = jsonObj.getInt("rc");
			msg = jsonObj.getString("msg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (-1 == rc) {
			return null;
		}
		CxLogoutResponce suggestResult = new CxLogoutResponce();
		suggestResult.setRc(rc);
		suggestResult.setMsg(msg);
		return suggestResult;
	}
	
	//修改聊天背景
	public CxChangeChatBackground parseForChangeChatBg(JSONObject obj){
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
		CxChangeChatBackground chatBackground = new CxChangeChatBackground();
		
		chatBackground.setRc(rc);
		
		try {
			String msg = obj.getString("msg");
			chatBackground.setMsg(msg);
		} catch (JSONException e) {
		}
		try {
			chatBackground.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == dataObj) {
			return chatBackground;
		}
		
		try {
			if (!dataObj.isNull("uid")) {
				chatBackground.setUid(dataObj.getString("uid"));
			}
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("bg_small")) {
				chatBackground.setBg_small(dataObj.getString("bg_small"));
			}
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("icon_small")){
				chatBackground.setIcon_small(dataObj.getString("icon_small"));
			}
		} catch (JSONException e) {
		}
		
		try {
			chatBackground.setBirth(dataObj.getInt("birth"));
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("bg_big")) {
				chatBackground.setBg_big(dataObj.getString("bg_big"));
			}
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("icon_mid")) {
				chatBackground.setIcon_mid(dataObj.getString("icon_mid"));
			}
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("name")) {
				chatBackground.setName(dataObj.getString("name"));
			}
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("icon_big")) {
				chatBackground.setIcon_big(dataObj.getString("icon_big"));
			}
		} catch (JSONException e) {
		}
		
		try {
			chatBackground.setReg_time(dataObj.getInt("reg_time"));
		} catch (JSONException e) {
		}
		
		try {
			chatBackground.setGender(dataObj.getInt("gender"));
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("chat_big")) {
				chatBackground.setChat_big(dataObj.getString("chat_big"));
			}
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("chat_small")) {
				chatBackground.setChat_small(dataObj.getString("chat_small"));
			}
		} catch (JSONException e) {
		}
		
		return chatBackground;
	}
	
}
