package com.chuxin.family.parse;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.parse.been.CxLogin;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxUserAccount;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.utils.CxLog;

/**
 * 用户帐号管理
 * @author shichao.wang
 *
 */
public class CxManageAccountParser {
	
	/*注册解析*/
	public static CxParseBasic perseForRegister(Object obj){
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
		if (-1 == rc) {
			return null;
		}
		CxParseBasic register = new CxParseBasic();
		register.setRc(rc);
		if(0 != rc) {
			return register;
		}
		
		try {
			JSONObject dataObj = jObj.getJSONObject("data");
			register.setMsg(dataObj.getString("uid")); //msg字段存储uid
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return register;
	}
	
	/*登录解析*/
	public static CxLogin parseForLogin(Object obj){
		try {
			if (null == obj) {
				return null;
			}
			JSONObject jsonObj = (JSONObject)obj;
			int rc = -1;
			if (!jsonObj.has("rc")) {
				return null;
			}
			CxLogin login = new CxLogin();
			rc = jsonObj.getInt("rc");
			login.setRc(rc);
			try {
				if (!jsonObj.isNull("msg")) {
					login.setMsg(jsonObj.getString("msg"));
				}
				if (!jsonObj.isNull("ts")) {
					login.setTs(jsonObj.getInt("ts"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			
			
			if (0 != rc) {
				return login;
			}
			if (!jsonObj.has("data")) {
				return login;
			}
			JSONObject dataObj = jsonObj.getJSONObject("data");
			CxUserProfileDataField profile = new CxUserProfileDataField();
			try {
				if (!dataObj.isNull("together_date")) {
					profile.setTogetherDay(dataObj.getString("together_date"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			
			try {
				if (!dataObj.isNull("bg_big")) {
					profile.setBg_big(dataObj.getString("bg_big"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("bg_small")) {
					profile.setBg_small(dataObj.getString("bg_small"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("birth")) {
					profile.setBirth(dataObj.getString("birth"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("chat_big")) {
					profile.setChat_big(dataObj.getString("chat_big"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("chat_small")) {
					profile.setChat_small(dataObj.getString("chat_small"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("gender")) {
					profile.setGender(dataObj.getInt("gender"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("icon_big")) {
					profile.setIcon_big(dataObj.getString("icon_big"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("icon_mid")) {
					profile.setIcon_mid(dataObj.getString("icon_mid"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("icon_small")) {
					profile.setIcon_small(dataObj.getString("icon_small"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("identifie")) {
					profile.setIdentifie(dataObj.getString("identifie"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("name")) {
					profile.setName(dataObj.getString("name"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if (!dataObj.isNull("pair_id")) {
					profile.setPair_id(dataObj.getString("pair_id"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if(!dataObj.isNull("partner_id")){
					profile.setPartner_id(dataObj.getString("partner_id"));
				}
				
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if(!dataObj.isNull("reg_time")){
					profile.setReg_time(dataObj.getString("reg_time"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if(!dataObj.isNull("uid")){
					profile.setUid(dataObj.getString("uid"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if(!dataObj.isNull("push_sound")){
					profile.setPush_sound(dataObj.getString("push_sound"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			
			try {
				if(!dataObj.isNull("group_show_id")){
					profile.setGroup_show_id(dataObj.getString("group_show_id"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			
			try {
				if(!dataObj.isNull("mobile")){
					profile.setMobile(dataObj.getString("mobile"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			try {
				if(!dataObj.isNull("data")){
					profile.setData(dataObj.getString("data"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			
			try {
				if(!dataObj.isNull("family_big")){
					profile.setFamily_big(dataObj.getString("family_big"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			
			try {
				if(!dataObj.isNull("single_mode")){
					profile.setSingle_mode(dataObj.getInt("single_mode"));
				}
				if(!dataObj.isNull("version_type")){
					profile.setVersion_type(dataObj.getInt("version_type"));
				}
				if(!dataObj.isNull("tutorial")){
					profile.setTutorial(dataObj.getInt("tutorial"));
				}
				if(!dataObj.isNull("group_id")){
					profile.setGroup_id(dataObj.getString("group_id"));
				}
			} catch (Exception e) {
				CxLog.w("", ""+e.toString());
			}
			login.setData(profile);
			return login;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
