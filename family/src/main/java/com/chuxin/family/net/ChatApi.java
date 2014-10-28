package com.chuxin.family.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.utils.CxLog;

import android.os.Environment;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;

public class ChatApi extends ConnectionManager {
	private static ChatApi instance = new ChatApi();
	private static String PATH_GET_MSGS = HttpApi.HTTP_SERVER_PREFIX
			+ "Chat/get_messages";
	private static String PATH_SEND_MSG = HttpApi.HTTP_SERVER_PREFIX
			+ "Chat/send_message";
	private static String PATH_READ_MSG = HttpApi.HTTP_SERVER_PREFIX
			+ "Chat/read_message";
	private static String PATH_UPDATE_EDIT_STATUS = HttpApi.HTTP_SERVER_PREFIX
			+ "Chat/update_edit_status";
	private static String PROCESS_SYSTEM_MESSAGE = HttpApi.HTTP_SERVER_PREFIX + "Chat/process_system_message";

	private ChatApi() {
	}

	public static ChatApi getInstance() {
		return instance;
	}

	public void doSendTextMessage(String msg,
			final ConnectionManager.JSONCaller callback) {
		NameValuePair[] params = { new BasicNameValuePair("type", "text"),
				new BasicNameValuePair("text", msg) };

		doSendMessage(params, callback);
	}

	public void doSendPhraseMessage(String msg,
			final ConnectionManager.JSONCaller callback) {
		NameValuePair[] params = { new BasicNameValuePair("type", "phrase"),
				new BasicNameValuePair("phrase_id", msg) };

		doSendMessage(params, callback);
	}

	public void doSendFaceMessage(String msg,
			final ConnectionManager.JSONCaller callback) {
		NameValuePair[] params = { new BasicNameValuePair("type", "face"),
				new BasicNameValuePair("face_id", msg) };

		doSendMessage(params, callback);
	}
	
	public void doSendEmotionMessage(String msg,
			final ConnectionManager.JSONCaller callback) {
		
		String[] split = msg.split("\\.");//分割字符串
		NameValuePair[] params = { new BasicNameValuePair("type", "emotion"),
				new BasicNameValuePair("category_id", split[1]),new BasicNameValuePair("image_id", split[2])};

		doSendMessage(params, callback);
	}
	
	

	public void doSendLoctionMessage(String msg, float lat, float lon,
			final ConnectionManager.JSONCaller callback) {
		NameValuePair[] params = { new BasicNameValuePair("type", "geo"),
				new BasicNameValuePair("lat", String.valueOf(lat)),
				new BasicNameValuePair("lon", String.valueOf(lon)),
				new BasicNameValuePair("text", msg),};

		doSendMessage(params, callback);
	}

	public void doSendMessage(NameValuePair[] params,
			final ConnectionManager.JSONCaller callback) {
		/*if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(
				Environment.getExternalStorageState()) ) {
			String name = Environment.getExternalStorageDirectory().getAbsoluteFile()
			+File.separator+"chuxin"+File.separator+"chat_log.txt";
			try {
				FileOutputStream fos = new FileOutputStream(name, true);
				fos.write(("mobile send request time:"+java.lang.System.currentTimeMillis()/1000).getBytes());
				fos.write("\r\n".getBytes());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}*/
		
		final String url = PATH_SEND_MSG;
		this.doHttpPost(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {

				//write chat log result return by server add by shichao.wang 2013.8.21
				/*if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(
						Environment.getExternalStorageState()) ) {
					String name = Environment.getExternalStorageDirectory().getAbsoluteFile()
					+File.separator+"chuxin"+File.separator+"chat_log.txt";
					try {
						FileOutputStream fos = new FileOutputStream(name, true);
						fos.write(data.toString().getBytes());
						fos.write("-----".getBytes());
						fos.write(("mobile receive time:"+java.lang.System.currentTimeMillis()/1000).getBytes());
						fos.write("\r\n".getBytes());
						fos.flush();
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}*/
				
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}

					if (callback != null) {
						JSONObject obj = result.getJSONObject("data");
						callback.call(obj);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}
		}, params);
	}

	public void doReadMessage(final int msgid,
			final ConnectionManager.JSONCaller callback) {
		final String url = PATH_READ_MSG;
		NameValuePair[] params = { new BasicNameValuePair("read_max",
				String.valueOf(msgid)) };
		this.doHttpPost(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}
					callback.call(null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, params);
	}

	public void doGetMessages(final ConnectionManager.JSONCaller callback) {
		final String url = PATH_GET_MSGS;

		this.doHttpGet(url, new ConnectionManager.JSONCaller() {

			@Override
			public int call(Object data) {
				JSONObject result = (JSONObject) data;
				Log.d("HI", "THE RESULT of " + url + ":" + result.toString());
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}
					JSONArray array = result.getJSONArray("data");
					callback.call(array);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}
	
	/**
	 * 抽鞭子、弹脑壳
	 * @param msg
	 * @param callback
	 */
	public void doSendAnimationMessage(int animation_id, int effect,
			final ConnectionManager.JSONCaller callback) {
		
		NameValuePair[] params = { new BasicNameValuePair("type", "animation"),
				new BasicNameValuePair("animation_id", 	String.valueOf(animation_id) ),
				new BasicNameValuePair("effect", 				String.valueOf(effect))
		};

		doSendMessage(params, callback);
	}
	
	/**
	 * 猜拳--发起猜拳
	 * @param guess_id  ： 猜拳的ID（客户端生成）
	 * @param value1		：己方出拳(1, 2, 3)
	 * @param callback
	 */
	public void doSendGuessRequestMessage(String guess_id, int value1,
			final ConnectionManager.JSONCaller callback) {
		
		NameValuePair[] params = { 
				new BasicNameValuePair("type", "guess_request"),
				new BasicNameValuePair("guess_id", 		guess_id ),
				new BasicNameValuePair("value1", 			String.valueOf(value1))
		};

		doSendMessage(params, callback);
	}
	
	
	/**
	 * 猜拳--回拳
	 * @param guess_id		：猜拳的ID
	 * @param value1			：己方出拳(1, 2, 3)
	 * @param value2			：对方出拳(1, 2, 3)
	 * @param result			  : 比赛结果：-1, 0 , 1
	 * @param callback
	 */
	public void doSendGuessResponseMessage(String guess_id, int value1, int value2, int result, 
			final ConnectionManager.JSONCaller callback) {
		
		NameValuePair[] params = { 
				new BasicNameValuePair("type", 		"guess_response"),
				new BasicNameValuePair("guess_id", 	guess_id ),
				new BasicNameValuePair("value1", 		String.valueOf(value1)),
				new BasicNameValuePair("value2", 		String.valueOf(value2)),
				new BasicNameValuePair("result", 		String.valueOf(result))
		};

		doSendMessage(params, callback);
	}
	
	/**
	 * 密邻添加
	 * @param id 密邻号
	 * @param callback
	 * @throws Exception
	 */
	public void requestProcessSystemMessage(final String method, final String btn, final String value1, final String value2, final JSONCaller callback){
		
	    NameValuePair[] params = {new BasicNameValuePair("method", method), new BasicNameValuePair("btn", btn), 
	    		new BasicNameValuePair("value1", value1), new BasicNameValuePair("value2", value2)};
	    
	    JSONCaller netCallback = new JSONCaller(){

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                try {
                    int rc = result.getInt("rc");
                    if(rc != 0 ){
                        callback.call(null);
                        return 0;
                    }
                    callback.call(result);
                } catch (JSONException e) {
                    CxLog.e("requestProcessSystemMessage", e.getMessage());
                }
                return 0;
            }
	        
	    };
	    this.doHttpGet(PROCESS_SYSTEM_MESSAGE, netCallback, params);
	}
}
