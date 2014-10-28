package com.chuxin.family.net;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

import com.chuxin.family.parse.CxAnswerParser;
import com.chuxin.family.parse.been.CxAnswerHomeList;
import com.chuxin.family.parse.been.CxAnswerQuestionData;
import com.chuxin.family.parse.been.CxAnswerResultData;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.utils.CxLog;

public class CxAnswerApi extends ConnectionManager {

	
	private final String ANSWER_HOME = HttpApi.HTTP_SERVER_PREFIX + "Qa/home"; //answer首页
	private final String ANSWER_GET_QUESTION = HttpApi.HTTP_SERVER_PREFIX + "Qa/get_question"; //answer获取问题
	private final String ANSWER_SUBMIT_RESULT = HttpApi.HTTP_SERVER_PREFIX + "Qa/submit_answer"; //answer提交答案
	private final String ANSWER_SHARE = HttpApi.HTTP_SERVER_PREFIX + "Qa/tell"; //answer炫耀一下
	private final String ANSWER_REMIND = HttpApi.HTTP_SERVER_PREFIX + "Qa/remind"; //提醒对方答题
	
	
	private CxAnswerApi(){};
	
	private static CxAnswerApi api;
	
	public static CxAnswerApi getInstance(){
		if (null == api) {
			api = new CxAnswerApi();
		}
		return api;
	}
	
	
	public void requestAnswerHome(final int offset, final int limit, final JSONCaller callback,
			final Context ctx){
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAnswerApi_men", jObj.toString());
				CxAnswerParser parser = new CxAnswerParser();
				CxAnswerHomeList feedList = null;
				try {
					feedList = parser.getAnswerHomeList(offset,jObj,ctx);					
				} catch (Exception e) {
				}
				callback.call(feedList);
				return 0;
			}
		};
		

		this.doHttpGet(ANSWER_HOME, netCallback, new BasicNameValuePair("offset", ""+offset), 
				new BasicNameValuePair("limit", ""+limit));
	}
	
	
	public void requestAnswerQuestion(final JSONCaller callback){

		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAnswerApi_men", jObj.toString());
				CxAnswerParser parser = new CxAnswerParser();
				CxAnswerQuestionData feedList = null;
				try {
					feedList = parser.getQuestion(jObj);					
				} catch (Exception e) {
				}
				callback.call(feedList);
				return 0;
			}
		};
		

		this.doHttpGet(ANSWER_GET_QUESTION, netCallback);
	}
	
	
	
	public void requestAnswerResult(final String id, final String result, final JSONCaller callback
			){
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAnswerApi_men", jObj.toString());
				CxAnswerParser parser = new CxAnswerParser();
				CxAnswerResultData feedList = null;
				try {
					feedList = parser.getResult(jObj);					
				} catch (Exception e) {
				}
				callback.call(feedList);
				return 0;
			}
		};
		

		this.doHttpPost(ANSWER_SUBMIT_RESULT, netCallback, new BasicNameValuePair("id", id), 
				new BasicNameValuePair("result", result));
	}
	
	
	
	
	
	
	public void requestNeighbourShare(final String id,final JSONCaller callback){
	JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAnswerApi_men", jObj.toString());
				try {					
					int rc = -1;
					rc = jObj.getInt("rc");
					CxParseBasic basic = new CxParseBasic();
					basic.setRc(rc);
					basic.setMsg(jObj.getString("msg"));
					basic.setTs(jObj.getInt("ts"));
					callback.call(basic);
								
				} catch (Exception e) {
					callback.call(null);
				}
				
				return 0;
			}
		};
		

		this.doHttpGet(ANSWER_SHARE, netCallback,new BasicNameValuePair("id", id));
		
	}
	
	
	
	
	
	public void requestRemind(final JSONCaller callback){

		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAnswerApi_men", jObj.toString());
				try {					
					int rc = -1;
					rc = jObj.getInt("rc");
					CxParseBasic basic = new CxParseBasic();
					basic.setRc(rc);
					basic.setMsg(jObj.getString("msg"));
					basic.setTs(jObj.getInt("ts"));
					callback.call(basic);
								
				} catch (Exception e) {
					callback.call(null);
				}
				
				return 0;
			}
		};
		

		this.doHttpGet(ANSWER_REMIND, netCallback);
	}
	
	
	
	
	
	
	
	
	
	
	
}
