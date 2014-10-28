package com.chuxin.family.parse;

import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxFamilyInfoCacheData;
import com.chuxin.family.parse.been.CxCheckVersion;
import com.chuxin.family.parse.been.CxFamilyInfoData;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.data.FamilyInfoUserInfo;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.utils.CxLog;

public class CxUserInfoParse {
	
	/*用户信息解析，对应"/User/get"接口*/
	public static CxUserProfile parseForUserInfo(Object obj){
		if (null == obj) {
			return null;
		}
		JSONObject jsonObj = (JSONObject)obj;
		int rc = -1;
		try {
			rc = jsonObj.getInt("rc");
		} catch (JSONException e) {
			CxLog.w("", ""+e.toString());
		}
		if (-1 == rc) {
			return null;
		}
		CxUserProfile userInfo = new CxUserProfile();
		userInfo.setRc(rc);
		
		try {
			if (!jsonObj.isNull("msg")) {
				userInfo.setMsg(jsonObj.getString("msg"));
			}
		} catch (JSONException e) {
			CxLog.w("", ""+e.toString());
		}
		
		try {
			userInfo.setTs(jsonObj.getInt("ts"));
		} catch (JSONException e) {
			CxLog.w("", ""+e.toString());
		}
		
		if (!jsonObj.has("data")) {
			return userInfo;
		}
		try {
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
			
			userInfo.setData(profile);
		} catch (JSONException e) {
			CxLog.w("", ""+e.toString());
		}
		
		return userInfo;
	}
	
	//解析伴侣资料（对应User/partner接口返回的数据解析）
	public static CxMateProfile parseForUserPartnerProfile(Object obj){
		if (null == obj) {
			return null;
		}
		JSONObject profileObj = null;
		try {
			profileObj = (JSONObject)obj;
		} catch (Exception e) {
			try {
				profileObj = new JSONObject((String)obj);
			}catch (Exception e1) {
				e.printStackTrace();
			}
		}
		if (null == profileObj) {
			return null;
		}
		int rc = -1;
		try {
			rc = profileObj.getInt("rc");
		} catch (JSONException e) {
//			e.printStackTrace();
		}
		if (-1 == rc) {
			return null;
		}
		CxMateProfile profile = new CxMateProfile();
		profile.setRc(rc);
		try {
			profile.setMsg(profileObj.getString("msg"));
		} catch (JSONException e) {
//			e.printStackTrace();
		}
		try {
			profile.setTs(profileObj.getInt("ts"));
		} catch (JSONException e) {
//			e.printStackTrace();
		}
		if (0 != rc) {
			return profile;
		}
		
		JSONObject dataObj = null;
		try {
			dataObj = profileObj.getJSONObject("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (null == dataObj) {
			return profile;
		}
		CxMateProfileDataField data = new CxMateProfileDataField();
		try {
			data.setBirth(dataObj.getInt("birth"));
		} catch (JSONException e) {
		}
		
		//data部分
		//JSONObject dataObject = null;
		String strObject;
		LinkedHashMap<String,String> hmdata = new LinkedHashMap<String,String>();
		
		try {
			strObject = dataObj.getString("data");
			if (!strObject.equalsIgnoreCase("null")) {
				String[] strLine = strObject.split("\\n");
				
				for (int i = 0; i < strLine.length; i++) {
					String[] strKV = strLine[i].split(":");
					if (strKV.length == 1) {
						if (strKV[0].length() == 0) continue;
						hmdata.put(strKV[0],"");
					} else {
						if (strKV[0].length() == 0) continue;
						hmdata.put(strKV[0],strKV[1]);
					}
				}
			}
			data.setData(hmdata);
		} catch (JSONException e) {
		}
		
		try {
			data.setEmail(dataObj.getString("email"));
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.getString("icon").equalsIgnoreCase("null"))
				data.setIcon(dataObj.getString("icon"));
			else
				data.setIcon(dataObj.getString(""));
		} catch (JSONException e) {
		}
		try {
			data.setId(dataObj.getString("id"));
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.getString("mobile").equalsIgnoreCase("null"))
				data.setMobile(dataObj.getString("mobile"));
			else
				data.setMobile(dataObj.getString(""));
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.getString("name").equalsIgnoreCase("null"))
				data.setName(dataObj.getString("name"));
			else
				data.setName("");
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.getString("note").equalsIgnoreCase("null"))
				data.setNote(dataObj.getString("note"));
			else
				data.setNote("");
		} catch (JSONException e) {
		}
		try {
			data.setPartner_id(dataObj.getString("partner_id"));
		} catch (JSONException e) {
		}
		profile.setData(data);
		
		return profile;
	}
	
	//解析伴侣资料（对应User/partner接口返回的数据解析）
	public static String parseForUserPartnerHead(Object obj){
		if (null == obj) {
			return "";
		}
		JSONObject profileObj = null;
		try {
			profileObj = (JSONObject)obj;
		} catch (Exception e) {
			try {
				profileObj = new JSONObject((String)obj);
			}catch (Exception e1) {
				e.printStackTrace();
			}
		}
		if (null == profileObj) {
			return "";
		}
		int rc = -1;
		try {
			rc = profileObj.getInt("rc");
		} catch (JSONException e) {
//			e.printStackTrace();
		}
		if (-1 == rc) {
			return "";
		}

		String headicon = "";
		
		JSONObject dataObj = null;
		try {
			dataObj = profileObj.getJSONObject("data");
			headicon = dataObj.getString("icon");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		return headicon;
	}
	
	public static CxCheckVersion parseForCheckVersion(JSONObject obj){
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
		CxCheckVersion check = new CxCheckVersion();
		check.setRc(rc);
		
		if (!obj.isNull("msg")) {
			try {
				check.setMsg(obj.getString("msg"));
			} catch (JSONException e) {
			}
		}
		
		try {
			check.setTs(obj.getInt("ts"));
		} catch (Exception e) {
		}
		
		try {
			JSONObject dataObj = obj.getJSONObject("data");
			if (null == dataObj) {
				return check;
			}
			
			check.setFlag(dataObj.getInt("flag"));
			check.setVersion(dataObj.getString("version"));
			check.setUrl(dataObj.getString("url"));
			check.setMsg(dataObj.getString("msg"));
			
		} catch (JSONException e) {
		}
		
		return check;
	}
	
	
	
	
	public CxFamilyInfoData getFamilyInfo(JSONObject obj, Context ctx,boolean isNative){
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
		
		CxFamilyInfoData list=new CxFamilyInfoData();
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
		
		
//		// 访问成功的情况需要存入数据库
		if(!isNative){
			 //chargehomeJsonObject  网络返回的result， 记得要做一个转换
			CxLog.i("men", "obj存上了");
			CxFamilyInfoCacheData data=new CxFamilyInfoCacheData(ctx);
			data.insertData(CxGlobalParams.getInstance().getUserId(), obj.toString());
		}
		
		
		try {
			if (!dataObj.isNull("family_big")) {
				list.setFamily_icon(dataObj.getString("family_big"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		JSONObject oppoObj=null;
		try {
			oppoObj=dataObj.getJSONObject("partner");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(oppoObj==null){
			return list;
		}
		
		FamilyInfoUserInfo oppoInfo=new FamilyInfoUserInfo();
		try {

			if(!oppoObj.isNull("name")){
				oppoInfo.setName(oppoObj.getString("name"));
			}
			
			if(!oppoObj.isNull("mobile")){
				oppoInfo.setMobile(oppoObj.getString("mobile"));
			}
			if(!oppoObj.isNull("partner_id")){
				oppoInfo.setPartner_id(oppoObj.getString("partner_id"));
			}
			if(!oppoObj.isNull("email")){
				oppoInfo.setEmail(oppoObj.getString("email"));
			}
			if(!oppoObj.isNull("note")){
				oppoInfo.setNote(oppoObj.getString("note"));
			}			
			if(!oppoObj.isNull("id")){
				oppoInfo.setId(oppoObj.getString("id"));
			}
			if(!oppoObj.isNull("birth")){
				oppoInfo.setBirth(oppoObj.getInt("birth")+"");
			}
			if(!oppoObj.isNull("data")){
				oppoInfo.setData(oppoObj.getString("data"));
			}
			if(!oppoObj.isNull("icon")){
				oppoInfo.setIcon(oppoObj.getString("icon"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		list.setOppoInfo(oppoInfo);
		
		
		JSONObject meObj=null;
		try {
			meObj=dataObj.getJSONObject("own");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(meObj==null){
			return list;
		}
		
		FamilyInfoUserInfo meInfo=new FamilyInfoUserInfo();
		try {
			
			if(!meObj.isNull("name")){
				meInfo.setName(meObj.getString("name"));
			}
			
			if(!meObj.isNull("mobile")){
				meInfo.setMobile(meObj.getString("mobile"));
			}
			if(!meObj.isNull("partner_id")){
				meInfo.setPartner_id(meObj.getString("partner_id"));
			}
			if(!meObj.isNull("email")){
				meInfo.setEmail(meObj.getString("email"));
			}
			if(!meObj.isNull("note")){
				meInfo.setNote(meObj.getString("note"));
			}			
			if(!meObj.isNull("id")){
				meInfo.setId(meObj.getString("id"));
			}
			if(!meObj.isNull("birth")){
				meInfo.setBirth(meObj.getInt("birth")+"");
			}
			if(!meObj.isNull("data")){
				meInfo.setData(meObj.getString("data"));
			}
			if(!meObj.isNull("icon")){
				meInfo.setIcon(meObj.getString("icon"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		list.setMeInfo(meInfo);
		
		

		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
}
