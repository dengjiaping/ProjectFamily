package com.chuxin.family.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chuxin.family.mate.CxMateProfileCacheData;
import com.chuxin.family.parse.CxUserInfoParse;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.utils.CxLog;


public class CxMateProfileApi extends ConnectionManager {

	private final String MATE_PROFILE_LIST = HttpApi.HTTP_SERVER_PREFIX + "User/partner"; //
	private final String MATE_PROFILE_POST = HttpApi.HTTP_SERVER_PREFIX + "User/update/partner"; //

	private static CxMateProfileApi mateprofileApi;
	
	private CxMateProfileApi(){};
	
	public static CxMateProfileApi getInstance(){
		if (null == mateprofileApi) {
			mateprofileApi = new CxMateProfileApi();
		}
		return mateprofileApi;
	}
	
	/**
	 * 备忘资料获取
	 */
	public void requestMateProfileInfo(final JSONCaller callback, final Context ctx){
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
//				JSONObject jObj = null;
//				try {
//					jObj = (JSONObject)result;
//				} catch (Exception e) {
//				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				
				//访问成功的情况需要存入数据库
				try {
					CxMateProfileCacheData cache = new CxMateProfileCacheData(ctx);
					cache.insertMateProfile(result.toString());
				} catch (Exception e) {
					CxLog.e(CxMateProfile.class.getName(),  "从服务器取回个人资料数据后，放到本地数据缓存出错!" + e.getMessage());
				}				
				
				CxMateProfile mateProfile = null;
				try {
					mateProfile = CxUserInfoParse.parseForUserPartnerProfile(result);
				} catch (Exception e) {
					CxLog.e(CxMateProfile.class.getName(),  "从服务器取回个人资料数据后，转换成RkMateProfile对象出错!" + e.getMessage());
				}
				callback.call(mateProfile);
				return 0;
			}
		};

		this.doHttpGet(MATE_PROFILE_LIST, netCallback);
		
	}
	
	/**
	 * 更新备忘资料
     * @param       name    - 昵称
     * @param       birth   - 生日 (型如 19800731)
     * @param       mobile  - 手机号
     * @param       note    - 备注
     * @param       data    - 其他数据
     * @param       avata   - 图片文件	 
	 */
	public void postMateProfileInfo(String name, String birth, String mobile, String note, String data, String avata, 
			final JSONCaller callback) throws Exception{
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

		int i = 0;
		if(name!=null){
			paramsList.add(new BasicNameValuePair("name", name)); 
			i++;
		}
		
		
		if (!TextUtils.isEmpty(birth) && !birth.equalsIgnoreCase("0")) {
			paramsList.add(new BasicNameValuePair("birth", birth));  
			i++;
		}

		if(mobile!=null){
			paramsList.add(new BasicNameValuePair("mobile", mobile));  
			i++;
		}
		
		if(note!=null){
			paramsList.add(new BasicNameValuePair("note", note)); 
			i++;
		}
		

		//注意，此处不能用:  "!TextUtils.isEmpty(data)".  原因: 后端会判断，如果为null，则不会更改此字段
		if (null!=data) {
			paramsList.add(new BasicNameValuePair("data", data)); 
			i++;
		}	
		
		if (0 == i) {
			throw new Exception("all params can not be null");
		}
		
		final String url = HttpApi.HTTP_SERVER_PREFIX+"User/update/partner";

		int size = paramsList.size();
		NameValuePair[] params = (NameValuePair[])paramsList.toArray(new NameValuePair[size]);		
		
		doHttpPost(url, new ConnectionManager.JSONCaller() {
			
			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject)data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}
					CxLog.i("RkMateProfileApi_men", result.toString());
					if (callback != null) {
						callback.call(result);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				return 0;
			}
		}, params);
		
	}	
}
