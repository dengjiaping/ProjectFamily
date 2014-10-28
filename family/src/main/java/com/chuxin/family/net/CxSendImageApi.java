package com.chuxin.family.net;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxSettingsParser;
import com.chuxin.family.parse.CxZoneParser;
import com.chuxin.family.parse.been.CxChangeBgOfZone;
import com.chuxin.family.parse.been.CxChangeChatBackground;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.settings.CxFile;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxInputPanel;

/**
 * 图片上传
 * @author shichao.wang
 *
 */

public class CxSendImageApi extends CxFileUploadApi {

	private static CxSendImageApi mSendApi;
	private CxSendImageApi(){}
	
	public static CxSendImageApi getInstance(){
		if (null == mSendApi) {
			mSendApi = new CxSendImageApi();
		}
		return mSendApi;
	}
	
	/**
	 * 修改头像
	 * @param headImagePath
	 * @param headType
	 * @param callback
	 * @throws Exception
	 */
	public void sendHeadImage(String headImagePath, final 
			CxSettingsParser.SendHeadImageType headType, 
			final JSONCaller callback) throws Exception{
		if (TextUtils.isEmpty(headImagePath)) {
			throw new Exception("head image can not null");
		}
		if(!new File(headImagePath).exists()){
			throw new Exception("head image can not reach or exitst");
		}
		
		String sendPath;
		
		if (headType == CxSettingsParser.SendHeadImageType.HEAD_ME){
			sendPath = HttpApi.HTTP_SERVER_PREFIX+"User/post/avata";
		}else{
			sendPath = HttpApi.HTTP_SERVER_PREFIX+"User/update/partner";
		}
			
		
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				//解析
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkSendImageApi_men", result.toString());
				CxChangeHead changeResult = null;
				try {
					CxSettingsParser changeHeadParse = CxSettingsParser.getInstance();
					changeResult = changeHeadParse.parseChangeHead(result, headType);
				} catch (Exception e) {
					e.printStackTrace();
				}
				callback.call(changeResult);
				return 0;
			}
		};
		List<CxFile> images = new ArrayList<CxFile>();
		images.add(new CxFile(headImagePath, "avata", "image/jpg"));
		try {
			sendMultTypeData(sendPath, null, images, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
		
	}
	
	/**
	 * 修改聊天背景
	 * @param isCustom :true indicates that modify chat background 
	 * with custom image, false indicates that use system background 
	 * @param nameValues:system background (thunb and big image). just isCustom is false
	 * @param background :custom chat background by custom image. just isCustom is true
	 * @param callback
	 * @throws Exception
	 */
	public void modifyChatBackground(boolean isCustom, List<CxFile> customBackground,
			final JSONCaller callback, NameValuePair... nameValues) throws Exception{
		
		if (isCustom) { // custom chat background
			if ( (null == customBackground) || (customBackground.size() < 1)) {
				throw new Exception("there's no any image for changing chat background");
			}
			
		}else{ // sysytem background
			if (null == nameValues) {
				throw new Exception("any of chat_small or chat_big must not be null");
			}
			
		}
		
		String path = HttpApi.HTTP_SERVER_PREFIX + "User/post/chat_background";
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
//				RkLog.i("+++++++++", result.toString());
				if (null == result) {
					callback.call(null);
					return -1;
				}
				try {
					JSONObject modifyChatBgObj = (JSONObject)result;
					CxChangeChatBackground modifyResult = 
						CxSettingsParser.getInstance().parseForChangeChatBg(modifyChatBgObj);
					callback.call(modifyResult);
				} catch (Exception e) {
					callback.call(null);
					e.printStackTrace();
				}
				return 0;
			}
		};
		
		if (isCustom) {
			try {
				sendMultTypeData(path, null, customBackground, netCallback);
			} catch (Exception e) {
				callback.call(null);
			}
		}else{
			doHttpPost(path, netCallback, nameValues);
		}
		
	}
	
	
	
	
	
	
	
	/**
	 * 二人空间发帖子
	 * @param text
	 * @param images
	 * @param lon
	 * @param lat
	 */
	public void sendShareInZone(String text, List<CxFile> images, 
			String lon, String lat, int sync, int open, final JSONCaller call){
		Map<String, String> params = new HashMap<String, String>();
		if (!TextUtils.isEmpty(text)) {
			params.put("text", text);
		}
		if (!TextUtils.isEmpty(lon)) {
			params.put("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.put("lat", lat);
		}
		if (!TextUtils.isEmpty(""+sync)) {
			params.put("sync", ""+sync);
		}
		
		params.put("open", ""+open);
		
		String path = HttpApi.HTTP_SERVER_PREFIX+"Space/feed/post";
		
		try {
			sendMultTypeData(path, params, images, call);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * send chat image
 	 * @param headImagePath
	 * @param callback
	 * @throws Exception
	 */
	public void sendChatImage(String chatImagePath, final JSONCaller callback) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		if (TextUtils.isEmpty(chatImagePath)) {
			throw new Exception("head image can not null");
		}
		if(!new File(chatImagePath).exists()){
			throw new Exception("image can not reach or exitst");
		}
		
		final String sendPath = HttpApi.HTTP_SERVER_PREFIX+"Chat/send_message";
		
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object data) {
				//解析
				Log.d("HI", "THE RESULT of " + sendPath + ":" + data.toString());
				JSONObject result = (JSONObject)data;
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
					} catch (Exception e) {
						e.printStackTrace();
					}
				return 0;
			}
		};
		List<CxFile> images = new ArrayList<CxFile>();
		images.add(new CxFile(chatImagePath, "photo", "image/jpg"));
		params.put("type", "photo");
		try {
			sendMultTypeData(sendPath, params, images, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
		
	}
	
	/**
	 * send audio file
 	 * @param filePath
	 * @param callback
	 * @throws Exception
	 */
	public void sendAudioFile(String filePath, int fileLength, int audioType, final JSONCaller callback) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		if (TextUtils.isEmpty(filePath)) {
			throw new Exception("head image can not null");
		}
		if (fileLength > 0){
			params.put("audio_len", fileLength+"");
		}
		if(!new File(filePath).exists()){
			throw new Exception("file can not reach or exitst");
		}
		
		final String sendPath = HttpApi.HTTP_SERVER_PREFIX+"Chat/send_message";
		
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object data) {
				//解析
				Log.d("HI", "THE RESULT of " + sendPath + ":" + data.toString());
				JSONObject result = (JSONObject)data;
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
					} catch (Exception e) {
						e.printStackTrace();
					}
				return 0;
			}
		};
		List<CxFile> files = new ArrayList<CxFile>();
		files.add(new CxFile(filePath, "audio", "audio/amr"));
		params.put("type", "audio");
		params.put("audio_type", ""+audioType);
		try {
			sendMultTypeData(sendPath, params, files, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
		
	}
	
	/**
	 * send error log file
	 * 
	 * @param filePath
	 * @param tag
	 * @param callback
	 * @throws Exception
	 */
	public void sendClientResponce(String reponseText, String filePath,
			String tag, final JSONCaller callback) throws Exception {
		if (TextUtils.isEmpty(reponseText)) {
			throw new Exception("head image can not null");
		}

		final String sendPath = HttpApi.HTTP_SERVER_PREFIX + "Stat/complain";

		JSONCaller call = new JSONCaller() {

			@Override
			public int call(Object data) {
				// 解析
				Log.d("HI", "THE RESULT of " + sendPath + ":" + data.toString());
				JSONObject result = null;
				try {
					result = (JSONObject) data;
				} catch (Exception e1) {
					e1.printStackTrace();
					callback.call(null);
					return -1;
				}
				if (null == result) {
					callback.call(null);
					return -2;
				}
				try {
					int rc = result.getInt("rc");
					String msg = result.getString("msg");
					CxLogoutResponce resultParse = new CxLogoutResponce();
					resultParse.setMsg(msg);
					resultParse.setRc(rc);
					callback.call(resultParse);
					return 0;
				} catch (Exception e) {
					e.printStackTrace();
					callback.call(null);
				}
				return 0;
			}
		};

		Map<String, String> params = new HashMap<String, String>();
		params.put("msg", reponseText);
		params.put("tag", tag);
		params.put("brand",   android.os.Build.BRAND);
		params.put("model",  android.os.Build.MODEL);

		List<CxFile> files = null;
		if (new File(filePath).exists()) {
			files = new ArrayList<CxFile>();
			files.add(new CxFile(filePath, "_d", "application/octet-stream"));
			CxLog.i("client responce file", "exist");
		}else{
			CxLog.i("client responce file", "do not exist");
		}
		

		try {
			sendMultTypeData(sendPath, params, files, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}

	}
	
	/**
	 * 
	 * @param path
	 * @param params
	 * @param files
	 * @throws Exception
	 */
	public void sendMultTypeData(String path,Map<String, String> params, 
			List<CxFile> files, JSONCaller call) throws Exception{
		
		MultipartEntity mEntity = new MultipartEntity();
		if (null != params) {
			// 构建表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				StringBody par = new StringBody(entry.getValue());
				mEntity.addPart(entry.getKey(), par);
			}
		}
		
		if (null != files) {
			// 上传的文件
			for (CxFile file : files) {
				FileBody fileBody = new FileBody(new File(file.getFilname()));  
	            mEntity.addPart(file.getNameField(), fileBody);
			} // end for(files)
		} // end if(files)
		HttpPost post = new HttpPost(path); 
		post.setEntity(mEntity);

		
		this.doHttp(post, call);
	}
	
	public void changeBackgroundOfZone(String backgroundFile, final JSONCaller callback){
		
		final String CHANGE_BACKROUND = HttpApi.HTTP_SERVER_PREFIX+"User/post/background";
		JSONCaller netResponce = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				//解析修改二人空间背景
				if (null == result) {
					callback.call(null);
				}
				JSONObject obj = null;
				try {
					obj = (JSONObject)result;
					CxChangeBgOfZone changeBg = new CxZoneParser().parseForChangeBgOfZone(obj);
					callback.call(changeBg);
				} catch (Exception e) {
					callback.call(null);
				}
				return 0;
			}
		};
		
		List<CxFile> images = new ArrayList<CxFile>();
		images.add(new CxFile(backgroundFile, "background", "image/jpg"));
		
		try {
			sendMultTypeData(CHANGE_BACKROUND, null, images, netResponce);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
	}
	/**************************************************************************************************************************/
	//密邻网络接口
	
	
	/**
	 * 密邻发帖子
	 * @param text
	 * @param images
	 * @param lon
	 * @param lat
	 */
	public void sendShareInNeighbour(String text, List<CxFile> images,String type,String group_id, 
			String lon, String lat,int sync_space, int open, final JSONCaller call){
		Map<String, String> params = new HashMap<String, String>();
		if (!TextUtils.isEmpty(text)) {
			params.put("text", text);
		}
		if (!TextUtils.isEmpty(lon)) {
			params.put("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.put("lat", lat);
		}
		if (!TextUtils.isEmpty(type)) {
			params.put("type", type);
		}
		if (!TextUtils.isEmpty(group_id)) {
			params.put("group_id", group_id);
		}
		
		params.put("sync_space", ""+sync_space);
		params.put("open", ""+open);
		
		String path = HttpApi.HTTP_SERVER_PREFIX+"Group/feed/post";
		/*JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				RkLog.i("", result.toString());
				return 0;
			}
		};*/		
		try {
			sendMultTypeData(path, params, images, call);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void changeImageOfNbHome(String backgroundFile,String girlHeadFile,String boyHeadFile,final JSONCaller callback){
		
		final String CHANGE_IMAGE = HttpApi.HTTP_SERVER_PREFIX+"Group/update";
	
		if(backgroundFile==null && girlHeadFile==null && boyHeadFile==null){
			try {
				throw new Exception("can not be all null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		List<CxFile> images = new ArrayList<CxFile>();
		
		if(backgroundFile!=null){
			if(new File(backgroundFile).exists()){
				images.add(new CxFile(backgroundFile, "background", "image/jpg"));
			}
		}
		
		if(girlHeadFile!=null){
			if(new File(girlHeadFile).exists()){
				images.add(new CxFile(girlHeadFile, "avatar0", "image/jpg"));
			}
		}
		
		if(boyHeadFile!=null){
			if(new File(boyHeadFile).exists()){
				images.add(new CxFile(boyHeadFile, "avatar1", "image/jpg"));
			}
		}
		
		try {
			sendMultTypeData(CHANGE_IMAGE, null, images, callback);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
	}
	
	/**
	 * Kid发帖子
	 * @param text
	 * @param images
	 * @param lon
	 * @param lat
	 */
	public void sendShareInKid(String text, List<CxFile> images, 
			String lon, String lat, int sync_space,int sync_group, int open, final JSONCaller call){
		Map<String, String> params = new HashMap<String, String>();
		if (!TextUtils.isEmpty(text)) {
			params.put("text", text);
		}
		if (!TextUtils.isEmpty(lon)) {
			params.put("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.put("lat", lat);
		}
		params.put("sync_space", sync_space+"");
		params.put("sync_group", sync_group+"");
		params.put("open", open+"");

		String path = HttpApi.HTTP_SERVER_PREFIX+"Child/feed/post";		
		try {
			sendMultTypeData(path, params, images, call);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	/**
	 * 密邻回复(在RkNeighbourApi中处理了，该方法先用不到了)
	 * send audio file
 	 * @param filePath
	 * @param callback
	 * @throws Exception
	 */
	public void sendAudioOrTextFile(String feed_id,String type,String text,String audio,String audioLength, 
			String reply_to, String extra, final JSONCaller callback) throws Exception{
	/*	Map<String, String> params = new HashMap<String, String>();
		if (TextUtils.isEmpty(filePath)) {
			throw new Exception("head image can not null");
		}
		if (fileLength > 0){
			params.put("audio_len", fileLength+"");
		}
		if(!new File(filePath).exists()){
			throw new Exception("file can not reach or exitst");
		}
		
		final String sendPath = HttpApi.HTTP_SERVER_PREFIX+"Chat/send_message";
		
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object data) {
				//解析
				Log.d("HI", "THE RESULT of " + sendPath + ":" + data.toString());
				JSONObject result = (JSONObject)data;
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
					} catch (Exception e) {
						e.printStackTrace();
					}
				return 0;
			}
		};
		List<RkFile> files = new ArrayList<RkFile>();
		files.add(new RkFile(filePath, "audio", "audio/amr"));
		params.put("type", "audio");
		try {
			sendMultTypeData(sendPath, params, files, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
		*/
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
