package com.chuxin.family.net;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.parse.CxChatBgParser;
import com.chuxin.family.parse.CxSettingsParser;
import com.chuxin.family.parse.been.CxChatBgList;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.utils.CxLog;

/**
 * 设置模块的网络请求API
 * @author shichao.wang
 *
 */
public class CxSettingsCommonApi extends ConnectionManager{

	private static final String RESPONSE_PATH = HttpApi.HTTP_SERVER_PREFIX + "Stat/complain";
	private static final String BACKGROUND_CONFIG = HttpApi.HTTP_SERVER_PREFIX + "User/get_config";
	
	private static CxSettingsCommonApi mSettingsApi;
	
	private CxSettingsCommonApi(){}
	
	public static CxSettingsCommonApi getInstance(){
		if (null == mSettingsApi) {
			mSettingsApi = new CxSettingsCommonApi();
		}
		return mSettingsApi;
	}
	
	/**
	 * 意见反馈
	 * @param msg，反馈内容
	 * @param tag，反馈者联系方式
	 * @param callback
	 */
	public void requestSuggestion(String msg, String tag, 
			final JSONCaller callback) throws Exception{
		if (null == msg) {
			throw new Exception("param msg can not null");
		}
		JSONCaller jsonParse = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLogoutResponce suggestResult = CxSettingsParser
				.getInstance().parseForUserSuggest(result);
				callback.call(suggestResult);
				
				return 0;
			}
		};
		if (null == tag) {
			NameValuePair []params = {new BasicNameValuePair("msg", msg)};
			this.doHttpPost(RESPONSE_PATH, jsonParse, params);
		}else{
			NameValuePair []params = {new BasicNameValuePair("msg", msg), 
					new BasicNameValuePair("tag", tag)};
			this.doHttpPost(RESPONSE_PATH, jsonParse, params);
		}
		
	}
	
	
	public void requestBackgroundConfig(final Context context,int version,final JSONCaller callback){
		if (version<0) {
			try {
				throw new Exception("param msg can not null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == jObj) {
					callback.call(null);
					return -2;
				}
				CxLog.i("RkSettiongsConmmonApi_men", jObj.toString());
												
				try {
					int rc = -1;
					rc = jObj.getInt("rc");
					if(rc!=0){
						callback.call(null);
						return -3;
					}
					
					CxChatBgParser parser=new CxChatBgParser();
					
					callback.call(parser.getChatBgConfig(jObj, context, false));
				} catch (JSONException e) {					
					e.printStackTrace();
				}
				
				return 0;
			}
		};
			
		this.doHttpGet(BACKGROUND_CONFIG, netCallback, new BasicNameValuePair("chat_bgs_version", ""+version));
		
	}
	
	
	
	
	
}
