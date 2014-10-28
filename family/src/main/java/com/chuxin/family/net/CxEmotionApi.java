package com.chuxin.family.net;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.parse.CxEmotionParser;
import com.chuxin.family.utils.CxLog;

public class CxEmotionApi extends ConnectionManager {

//	private final String INVITATION_LIST = HttpApi.HTTP_SERVER_PREFIX + "Group/feed/list"; //帖子列表
	private final String EMOTION_CONFIG=HttpApi.HTTP_SERVER_PREFIX+"User/get_config";// 下载表情配置文件
	
	private CxEmotionApi(){};
	
	private static CxEmotionApi api;
	
	public static CxEmotionApi getInstance(){
		if (null == api) {
			api = new CxEmotionApi();
		}
		return api;
	}
	
	
	public void requestEmotionConfig(final Context context,final int version,final JSONCaller callback){
		
		
		JSONCaller caller = new JSONCaller() {
			
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
				CxLog.i("RkEmotionApi_men", jObj.toString());
				CxEmotionParser parser=new CxEmotionParser();
				if (null == jObj) {
					callback.call(null);
					return -1;
				}
				callback.call(parser.getEmotionConfigResult(context, jObj, version));
				return 0;
			}
		};
		
		
		doHttpGet(EMOTION_CONFIG, caller, new BasicNameValuePair("emotions_version", version+""));
		
	}
	
	
	
	
	
	
}
