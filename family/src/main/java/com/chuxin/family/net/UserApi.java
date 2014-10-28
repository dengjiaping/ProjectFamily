package com.chuxin.family.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxUserInfoParse;
import com.chuxin.family.parse.been.CxCheckVersion;
import com.chuxin.family.parse.been.CxFamilyInfoData;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.settings.CxFile;
import com.chuxin.family.utils.CxLog;
/**
 * 用户信息
 * @author           //commet add by shichao
 *
 */
public class UserApi extends ConnectionManager {
	private static UserApi instance = new UserApi();
//	private static String HTTP_SERVER_PREFIX = "https://192.168.1.46:441/";
	private static String PATH_USER_GET = HttpApi.HTTP_SERVER_PREFIX + "User/get";
	private static String PATH_USER_PARTNER_PROFILE = HttpApi.HTTP_SERVER_PREFIX + "User/partner";
	private static String PATH_USER_UPDATE_DEVICE_TOKEN = 
		HttpApi.HTTP_SERVER_PREFIX + "User/update/devicetoken";	
	private static final String CHECK_VERSION = HttpApi.HTTP_SERVER_PREFIX+"Stat/version_check_new";
	private static final String PATH_USER_UPDATE = HttpApi.HTTP_SERVER_PREFIX + "User/update";
	
	private static final String PATH_FAMILY_ICON = HttpApi.HTTP_SERVER_PREFIX + "User/family/update_background";
	private static final String PATH_FAMILY_INFO = HttpApi.HTTP_SERVER_PREFIX + "User/family/get_info";
	
	
	private UserApi() {
	}
	
	public static UserApi getInstance() {
		return instance;
	}
	
	/**
	 * 获取指定uid用户的UserProfile信息
	 * @param uid 用户ID
	 * @param callback
	 */
	public void getUserProfile(String uid, final ConnectionManager.JSONCaller callback) {

		NameValuePair[] params = { 
			new BasicNameValuePair("uid", uid),
		};
		
		JSONCaller userProfileCallback =  new ConnectionManager.JSONCaller() {
			
			@Override
			public int call(Object data) {
				
				if(null == data){
					callback.call(null);
					return -1;
				}
				CxLog.i("AccountApi_men", "获取用户信息："+data.toString());
				CxUserProfile userProfile = CxUserInfoParse.parseForUserInfo(data);
				callback.call(userProfile);
				
				return 0;
			}
		};
		this.doHttpGet(PATH_USER_GET, userProfileCallback, params);
	}
	
	public void getUserPartnerProfile(final JSONCaller callback){
		JSONCaller partnerProfileCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				CxLog.i("UserApi_men", "老公/老婆："+result.toString());
				CxMateProfile profile = CxUserInfoParse.parseForUserPartnerProfile(result);
				callback.call(profile);
				return 0;
			}
		};
		this.doHttpGet(PATH_USER_PARTNER_PROFILE, partnerProfileCallback);
	}

	public void updateDeviceToken(String os, String token, final JSONCaller callback) {
		NameValuePair[] params = { 
				new BasicNameValuePair("os", os),
				new BasicNameValuePair("token", token),
		};

		doHttpPost(PATH_USER_UPDATE_DEVICE_TOKEN, callback, params);
	}
	
	public void checkVersion(final JSONCaller callback){ //注意每个版本修改agent中版本号码
		
		JSONCaller netBack = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("versioncheck", ""+result.toString());
				try {
					CxCheckVersion version = CxUserInfoParse.parseForCheckVersion((JSONObject)result);
					callback.call(version);
				} catch (Exception e) {
					e.printStackTrace();
					callback.call(null);
				}
				
				return 0;
			}
		};
		
		this.doHttpGet(CHECK_VERSION, netBack);
	}
	
	public void updateUserProfile(String together_data, String birth, String push_sound,
			String gender, String name, final JSONCaller callback) throws Exception{
		updateUserProfile(together_data,birth,push_sound,gender,name,null,null,null,callback);
	}
	
	public void updateUserProfile(String together_data, String birth, String push_sound,
			String gender, String name,String mobile,String data,final JSONCaller callback) throws Exception{
		updateUserProfile(together_data,birth,push_sound,gender,name,mobile,data,null,callback);
	}
	
	public void updateUserProfile(String together_data, String birth, String push_sound,
			String gender, String name,String mobile,String data, String version_type,final JSONCaller callback) throws Exception{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		int i = 0;
		if (null != together_data) {
			params.add(new BasicNameValuePair("together_date", together_data)); //更新了文档最初写的together_data
			i++;
		}
		
		if (null != birth) {
			params.add(new BasicNameValuePair("birth", birth));
			i++;
		}
		
		if (null != push_sound) {
			params.add(new BasicNameValuePair("push_sound", push_sound)); //push声音（除弹脑壳和抽鞭子）
			i++;
		}
		
		if (null != gender) {
			params.add(new BasicNameValuePair("gender", gender));
			i++;
		}
		if (null != name) {
			params.add(new BasicNameValuePair("name", name));
			i++;
		}
		
		if (null != mobile) {
			params.add(new BasicNameValuePair("mobile", mobile));
			i++;
		}
		
		if (null != data) {
			params.add(new BasicNameValuePair("data", data));
			i++;
		}
		
		if (null != version_type) {
			params.add(new BasicNameValuePair("version_type", version_type));
			i++;
		}
		
		if (0 == i) {
			throw new Exception("all params can not be null");
		}
		
		JSONCaller caller = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if(null == result){
					callback.call(null);
					return -1;
				}
				CxLog.i("UserApi_men", result.toString());
				CxUserProfile userProfile = CxUserInfoParse.parseForUserInfo(result);
				callback.call(userProfile);
				
				return 0;
			}
		};
		this.doHttpPost(PATH_USER_UPDATE, caller, params);
	}
	
	
	/**
	 * 修改家庭头像
	 * @param headImagePath
	 * @param headType
	 * @param callback
	 * @throws Exception
	 */
	public void sendFamilyImage(String headImagePath,String type,final JSONCaller callback) throws Exception{
		if (TextUtils.isEmpty(headImagePath)) {
			throw new Exception("head image can not null");
		}
		if(!new File(headImagePath).exists()){
			throw new Exception("head image can not reach or exitst");
		}
				
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				//解析
				if (null == result) {
					callback.call(null);
					return -1;
				}
				
				JSONObject jObj=null;
				try {
					
					jObj=(JSONObject) result;
				} catch (Exception e) {
					e.printStackTrace();
				}
//				System.out.println(jObj.toString());				
				if (null == jObj) {
					callback.call(null);
					return -1;
				}
				CxLog.i("UserApi_men", jObj.toString());
				callback.call(jObj);
				return 0;
			}
		};
		List<CxFile> images = new ArrayList<CxFile>();
		images.add(new CxFile(headImagePath, type, "image/jpg"));
		try {
			CxSendImageApi.getInstance().sendMultTypeData(PATH_FAMILY_ICON, null, images, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
		
	}
	
	public void requestFamilyInfo(final Context ctx,final JSONCaller callback){
		
		JSONCaller netBack = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				CxLog.i("versioncheck", ""+result.toString());
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("UserApi_men", result.toString());
				try {
					CxUserInfoParse infoParse=new CxUserInfoParse();
					CxFamilyInfoData familyInfo = infoParse.getFamilyInfo((JSONObject)result, ctx, false);
					CxLog.i("UserApi_men", (familyInfo==null)+"");
					callback.call(familyInfo);
				} catch (Exception e) {
					e.printStackTrace();
					callback.call(null);
				}
				
				return 0;
			}
		};
		
		this.doHttpGet(PATH_FAMILY_INFO, netBack);
	};
	
	
	
	
	
	
	
	
	
	
	
	
	

}
