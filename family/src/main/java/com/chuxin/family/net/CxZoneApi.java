package com.chuxin.family.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.chuxin.family.parse.CxZoneParser;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxReply;
import com.chuxin.family.parse.been.CxReplyList;
import com.chuxin.family.parse.been.CxShareThdRes;
import com.chuxin.family.parse.been.CxZoneFeedList;
import com.chuxin.family.settings.CxFile;
import com.chuxin.family.utils.CxLog;

public class CxZoneApi extends ConnectionManager {

	private final String FEED_LIST = HttpApi.HTTP_SERVER_PREFIX + "Space/feed/list"; //帖子列表
//	private final String FEED_POST = HttpApi.HTTP_SERVER_PREFIX + "Space/feed/post"; //发帖子
	private final String FEED_DELETE = HttpApi.HTTP_SERVER_PREFIX + "Space/feed/delete"; //删除帖子
	private final String FEED_REPLAY = HttpApi.HTTP_SERVER_PREFIX + "Space/reply/post"; //回复帖子
	private final String FEED_DELETE_REPLAY = HttpApi.HTTP_SERVER_PREFIX + "Space/reply/delete"; //删除回复
	private final String FEED_REPLAY_LIST = HttpApi.HTTP_SERVER_PREFIX+"Space/reply/list"; //回复列表
	private final String FEED_LIST_NEW = HttpApi.HTTP_SERVER_PREFIX + "Space/feed/fetch"; //帖子列表的新接口（加了在一起xx天）
	private final String SHARE_TO_THIRD = HttpApi.HTTP_SERVER_PREFIX + "Share/feed"; //分享到第三方
	
	private static CxZoneApi zoneApi;
	
	private CxZoneApi(){};
	
	public static CxZoneApi getInstance(){
		if (null == zoneApi) {
			zoneApi = new CxZoneApi();
		}
		return zoneApi;
	}
	
	/**
	 * 帖子列表获取
	 * @param offset，偏移量
	 * @param limit，返回数目上限
	 */
	public void requestFeedList(final int offset, final int limit, final JSONCaller callback,
			final Context ctx){
		/*if (0 >= offset) {
			offset = 0;
		}
		if (limit <= 0) {
			limit = 10;
		}*/
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				    CxLog.e("requestFeedList", "error="+e.getMessage());
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxZoneParser zoneParser = new CxZoneParser();
				CxZoneFeedList feedList = null;
				try {
					feedList = zoneParser.getZoneFeedList(offset, jObj, ctx);
				} catch (Exception e) {
				    CxLog.e("requestFeedList", "error="+e.getMessage());
				}
				callback.call(feedList);
				return 0;
			}
		};

		/*this.doHttpGet(FEED_LIST, netCallback, new BasicNameValuePair("offset", ""+offset), 
				new BasicNameValuePair("limit", ""+limit));*/
		this.doHttpGet(FEED_LIST_NEW, netCallback, new BasicNameValuePair("offset", ""+offset), 
				new BasicNameValuePair("limit", ""+limit));
		
	}
	
	/**
	 * 发帖子
	 * @param text, 帖子的文字信息
	 * @param photos，帖子的照片
	 * @param callback
	 * @param sync 0 不同步到密邻,1 同步
	 * @param open 是否开放给第三方访问 (optional)（0：不公开，1：公开）
	 */
	public void requestSendFeed(String text, List<String> photos, int sync,
			int open, final JSONCaller callback) throws Exception{
		if ( (null == text) && ( (null == photos) || (photos.size() < 1) ) ) {
			throw new Exception("parameters can not be both null");
		}
		
		List<CxFile> images = new ArrayList<CxFile>();
		for(int i = 0; i < photos.size(); i++){
			String fileStr = photos.get(i).replace("file://", "");
			CxFile tempFile = new CxFile(fileStr, "photo"+i, null);
			images.add(tempFile);
		}
		
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(-1);
					return -1;
				}
				try {
					JSONObject obj = (JSONObject)result;
					CxZoneParser sendParser = new CxZoneParser();
					callback.call(sendParser.getSendFeedResult(obj));
					CxLog.i("111", "ok");
				} catch (Exception e) {
					callback.call(null);
				}
				
				return 0;
			}
		};
		//经纬度现在暂时不传
		CxSendImageApi.getInstance().sendShareInZone(text, images, null, null, sync, open, call);
	}
	
	/**
	 * 删除帖子
	 * @param id
	 * @param callback
	 */
	public void requestDeleteFeed(String id, final JSONCaller callback){
		
		JSONCaller deleteCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				try {
					JSONObject obj = (JSONObject)result;
					int rc = -1;
					rc = obj.getInt("rc");
					CxParseBasic deleteResult = new CxParseBasic();
					deleteResult.setRc(rc);
					deleteResult.setMsg(obj.getString("msg"));
					deleteResult.setTs(obj.getInt("ts"));
					callback.call(deleteResult);
				} catch (Exception e) {
					callback.call(null);
				}
				
				return 0;
			}
		};
		
		this.doHttpPost(FEED_DELETE, deleteCallback, new BasicNameValuePair("id", id));
	}
	
	/**
	 * 回复帖子
	 * @param feed_id，帖子ID
	 * @param text，文字信息
	 * @param reply_to，回复用户的ID
	 * @param extra，表情
	 */
	public void requestReply(String feed_id, String text, 
			String reply_to, String extra, final JSONCaller callback) throws Exception{
		if (null == feed_id) {
			throw new Exception("feed id can not be null");
		}
		if ( (null == text) && (null == extra) ){
			throw new Exception("text and extra can not be both null");
		}
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("feed_id", feed_id));
		if (null != text) {
			params.add(new BasicNameValuePair("text", text));
		}
		if (null != reply_to) {
			params.add(new BasicNameValuePair("reply_to", reply_to));
		}
		if (null != extra) {
			params.add(new BasicNameValuePair("extra", extra));
		}
		
		BasicNameValuePair[] paramsArray = null;
		try {
			paramsArray = params.toArray(new BasicNameValuePair[params.size()]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		JSONCaller call = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				try {
					JSONObject dataObj = (JSONObject)result;
					CxZoneParser replyParser = new CxZoneParser();
					CxReply replyResult = replyParser.getReplyResult(dataObj);
//					RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback ready normal");
					callback.call(replyResult);
//					RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback normal");
				} catch (Exception e) {
//					RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback exception："+e.toString());
//					e.printStackTrace();
					callback.call(null);
				}
				return 0;
			}
		};
		
		this.doHttpPost(FEED_REPLAY, call, paramsArray);
	}
	
	/**
	 * 删除回复
	 * @param id,回复的ID
	 * @param callback
	 * @throws Exception
	 */
	public void requestDeleteReply(String id, final JSONCaller callback) throws Exception{
		if (TextUtils.isEmpty(id)) {
			throw new Exception("delete id can not be null");
		}
		JSONCaller deleteReplyBack = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				try {
					JSONObject obj = (JSONObject)result;
					int rc = -1;
					rc = obj.getInt("rc");
					CxParseBasic deleteResult = new CxParseBasic();
					deleteResult.setRc(rc);
					try {
						deleteResult.setMsg(obj.getString("msg"));
					} catch (Exception e) {
					}
					try {
						deleteResult.setTs(obj.getInt("ts"));
					} catch (Exception e) {
					}
					callback.call(deleteResult);
				} catch (Exception e) {
					callback.call(null);
				}
				
				return 0;
			}
		};
		
		this.doHttpPost(FEED_DELETE_REPLAY, deleteReplyBack, new BasicNameValuePair("id", id));
	}
	
	/**
	 * 回复列表获取
	 * @param id, 帖子的ID
	 * @param offset，偏移量
	 * @param limit，返回记录条数上限
	 * @param callback
	 * @throws Exception
	 */
	public void getReplyList(String id, int offset, int limit, 
			final JSONCaller callback) throws Exception {
		if (null == id) {
			throw new Exception("param id can not be null");
		}
		
		if (0 >= offset) {
			offset = 0;
		}
		if (limit <= 0) {
			limit = 10;
		}
		
		JSONCaller caller = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result){
					callback.call(null);
					return -1;
				}
				try {
					JSONObject obj = (JSONObject)result;
					CxZoneParser replyListParser = new CxZoneParser();
					CxReplyList replies = replyListParser.getReplyList(obj);
					callback.call(replies);
				} catch (Exception e) {
					callback.call(null);
				}
				return 0;
			}
		};
		this.doHttpGet(FEED_REPLAY_LIST, caller, new BasicNameValuePair("id", id),
				new BasicNameValuePair("offset", ""+offset), 
				new BasicNameValuePair("limit", ""+limit));
	}
	
	public synchronized void sendShareRequest(String type,
			String feed_id, final JSONCaller callback) throws Exception{
		if ((null == type) || (null == feed_id) || (null == callback)) {
			throw new Exception("any param can not be null");
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("feed_id", feed_id));
		JSONCaller caller = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject obj = null;
				obj = (JSONObject)result;
				if (null == obj) {
					callback.call(null);
				}
				CxLog.i("CxZoneApi_men", result.toString());
				try {
					CxZoneParser parser = new CxZoneParser();
					CxShareThdRes res = parser.parseForShare(obj);
					callback.call(res);
				} catch (Exception e) {
					e.printStackTrace();
					callback.call(null);
				}
				return 0;
			}
		};
		
		this.doHttpGet(SHARE_TO_THIRD, caller, params);
	}
	
}
