package com.chuxin.family.net;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.CxSettingsParser;
import com.chuxin.family.parse.CxZoneParser;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxNeighbourInvitationList;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxReply;
import com.chuxin.family.parse.been.CxZoneFeedList;
import com.chuxin.family.parse.been.data.InvitationList;
import com.chuxin.family.parse.been.data.InvitationReply;
import com.chuxin.family.settings.CxFile;
import com.chuxin.family.utils.CxLog;

public class CxNeighbourApi extends ConnectionManager {
	
	
	private final String INVITATION_LIST = HttpApi.HTTP_SERVER_PREFIX + "Group/feed/list"; //帖子列表
	private final String INVITATION_HOME_LIST = HttpApi.HTTP_SERVER_PREFIX + "Group/home"; //帖子列表
//	private final String INVITATION_POST = HttpApi.HTTP_SERVER_PREFIX + "Space/feed/post"; //发帖子
	private final String INVITATION_DELETE = HttpApi.HTTP_SERVER_PREFIX + "Group/feed/delete"; //删除帖子
	private final String INVITATION_REPLAY = HttpApi.HTTP_SERVER_PREFIX + "Group/reply/post"; //回复帖子
	private final String INVITATION_DELETE_REPLAY = HttpApi.HTTP_SERVER_PREFIX + "Group/reply/delete"; //删除回复
//	private final String INVITATION_REPLAY_LIST = HttpApi.HTTP_SERVER_PREFIX+"Space/reply/list"; //回复列表
	private final String NEIGHBOUR_CHANGE_NAME = HttpApi.HTTP_SERVER_PREFIX+"Group/update_info"; //修改备注名
	private final String NEIGHBOUR_CHANGE_IMAGE = HttpApi.HTTP_SERVER_PREFIX+"Group/update"; //修改备注名
	
	private final String NEIGHBOUR_REMOVE = HttpApi.HTTP_SERVER_PREFIX+"Group/dismiss"; //解除密邻
	
	private final String NEIGHBOR_LIST = HttpApi.HTTP_SERVER_PREFIX + "Group/list"; //密邻列表
	private final String NEIGHBOR_QUERY = HttpApi.HTTP_SERVER_PREFIX + "Group/query"; //密邻列表
	private final String NEIGHBOR_INVITE = HttpApi.HTTP_SERVER_PREFIX + "Group/invite"; //添加密邻
	private final String NEIGHBOR_INVITE_TYPE = HttpApi.HTTP_SERVER_PREFIX + "User/statistic"; //邀请页面统计
	
	
	private CxNeighbourApi(){};
	
	private static CxNeighbourApi api;
	
	public static CxNeighbourApi getInstance(){
		if (null == api) {
			api = new CxNeighbourApi();
		}
		return api;
	}
	
	
	/**
	 * 修改头像和背景
	 * @param headImagePath
	 * @param headType
	 * @param callback
	 * @throws Exception
	 */
	public void sendHeadImage(String headImagePath,String type,final JSONCaller callback) throws Exception{
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
				callback.call(jObj);
				return 0;
			}
		};
		List<CxFile> images = new ArrayList<CxFile>();
		images.add(new CxFile(headImagePath, type, "image/jpg"));
		try {
			CxSendImageApi.getInstance().sendMultTypeData(NEIGHBOUR_CHANGE_IMAGE, null, images, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}
		
	}
	
	
	
	
	/**
	 * 发帖子
	 * 
	 * @param text
	 *            , 帖子的文字信息
	 * @param photos
	 *            ，帖子的照片
	 * @param callback
	 */
	public void requestSendInvitation(String text, List<String> photos,String type,String group_id,int sync_space,
			int open, final JSONCaller callback) throws Exception {
		if ((null == text) && ((null == photos) || (photos.size() < 1))) {
			throw new Exception("nb parameters can not be both null");
		}

		List<CxFile> images = new ArrayList<CxFile>();
		if(photos!=null && photos.size()>0){		
			for (int i = 0; i < photos.size(); i++) {
				String fileStr = photos.get(i).replace("file://", "");
				CxFile tempFile = new CxFile(fileStr, "photo" + i, null);
				images.add(tempFile);
			}
		}
		JSONCaller call = new JSONCaller() {

			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(-1);
					return -1;
				}
				try {
					JSONObject obj = (JSONObject) result;
//					RkNeighbourParser sendParser = new RkNeighbourParser();
//					callback.call(sendParser.getSendInvitationResult(obj));
					CxLog.i("RkNeighbour_men", obj.toString());
					callback.call(obj);
					CxLog.i("111", "ok");
				} catch (Exception e) {
					callback.call(null);
				}

				return 0;
			}
		};
		
		if(TextUtils.isEmpty(type) || "post".equals(type)){
			// 经纬度现在暂时不传
			CxSendImageApi.getInstance().sendShareInNeighbour(text, images,null,null, null,
					null,sync_space, open, call);
		}else if("message".equals(type) && !TextUtils.isEmpty(group_id)){
			CxSendImageApi.getInstance().sendShareInNeighbour(text, images,type,group_id, null,
					null,sync_space, open, call);
		}
		
		
	}

	/**
	 * 回复帖子
	 * 
	 * @param feed_id
	 *            ，帖子ID
	 * @param text
	 *            ，文字信息
	 * @param reply_to
	 *            ，回复用户的ID
	 * @param extra
	 *            ，表情
	 */
	public void requestReply(String feed_id, String type, String text,
			String audio, int audioLength, String reply_to, String extra,
			final JSONCaller callback) throws Exception {
		if (null == feed_id) {
			throw new Exception("feed id can not be null");
		}
		if ((null == text) && (null == extra) && (null == audio)) {
			throw new Exception("text and extra can not be both null");
		}

		Map<String, String> params = new HashMap<String, String>();

		params.put("feed_id", feed_id);
		params.put("type", type);

		if (audioLength > 0) {
			params.put("audio_len", audioLength + "");
		}
		if (null != text) {
			params.put("text", text);
		}
		if (null != reply_to) {
			params.put("reply_to", reply_to);
		}
		if (null != extra) {
			params.put("extra", extra);
		}

//		final String sendPath = HttpApi.HTTP_SERVER_PREFIX + "Group/reply/post";

		JSONCaller call = new JSONCaller() {

			@Override
			public int call(Object data) {
				// 解析
//				Log.d("HI", "THE RESULT of " + sendPath + ":" + data.toString());
				JSONObject result = (JSONObject) data;
				try {
					int rc = result.getInt("rc");
					if (rc != 0) {
						callback.call(result);
						return 0;
					}
					if (callback != null) {
						
						CxNeighbourParser sendParser = new CxNeighbourParser();
						callback.call(sendParser.getSendNbReplyResult(result));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		};

		List<CxFile> files = new ArrayList<CxFile>();

		if (null != audio) {
			if (new File(audio).exists()) {
				files.add(new CxFile(audio, "audio", "audio/amr"));
			}
		}
		try {
			CxSendImageApi.getInstance().sendMultTypeData(INVITATION_REPLAY, params,files, call);
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null);
		}

	}
	
	
	/**
	 * 修改备注名
	 * @param id
	 * @param callback
	 */
	public void requestChangeName(String id,String remark0,String remark1, final JSONCaller callback){
		
		JSONCaller changeCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				if (null == result) {
					callback.call(null);
					return -1;
				}
				try {
					JSONObject obj = (JSONObject)result;
//					int rc = -1;
//					rc = obj.getInt("rc");
//					RkParseBasic deleteResult = new RkParseBasic();
//					deleteResult.setRc(rc);
//					deleteResult.setMsg(obj.getString("msg"));
//					deleteResult.setTs(obj.getInt("ts"));
					callback.call(obj);
				} catch (Exception e) {
					callback.call(null);
				}
				
				return 0;
			}
		};
		
		this.doHttpPost(NEIGHBOUR_CHANGE_NAME, changeCallback, new BasicNameValuePair("group_id", id),
				new BasicNameValuePair("remark0", remark0),new BasicNameValuePair("remark1", remark1));
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
		
		this.doHttpPost(INVITATION_DELETE, deleteCallback, new BasicNameValuePair("id", id));
	}
	
	
	
	
	
	
	/**
	 * 帖子列表获取
	 * @param offset，偏移量
	 * @param limit，返回数目上限
	 */
	public void requestInvitationList(final int offset, final int limit, final JSONCaller callback,
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
//					System.out.println(jObj.toString());
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
//				System.out.println(jObj.toString());
				CxLog.i("RkNeighbourApi_men",jObj.toString() );

				CxNeighbourParser parser = new CxNeighbourParser();
				CxNeighbourInvitationList feedList = null;
				try {
					feedList = parser.getNbInvitationList(offset,jObj,ctx);
					
				} catch (Exception e) {
				}
				callback.call(feedList);
				return 0;
			}
		};
		
//		final String sendPath = HttpApi.HTTP_SERVER_PREFIX + "Group/feed/list";

		this.doHttpGet(INVITATION_LIST, netCallback, new BasicNameValuePair("offset", ""+offset), 
				new BasicNameValuePair("limit", ""+limit));
		
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
		
		this.doHttpPost(INVITATION_DELETE_REPLAY, deleteReplyBack, new BasicNameValuePair("id", id));
	}
	
	
	
	
	
	/**
	 * Home帖子列表获取
	 * @param offset，偏移量
	 * @param limit，返回数目上限
	 */
	public void requestHomeInvitationList(final String id,final int offset, final int limit, final JSONCaller callback,
			final Context ctx,final String groupId){
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
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}

				CxLog.i("RkNeighbourApi_men", jObj.toString());
				
				CxNeighbourParser parser = new CxNeighbourParser();
				CxNeighbourInvitationList feedList = null;
				try {
					feedList = parser.getNbHomeInvitationList(offset,jObj,ctx,groupId);
				} catch (Exception e) {
				}
				callback.call(feedList);
				return 0;
			}
		};

		if(id!=null){	
			this.doHttpGet(INVITATION_HOME_LIST, netCallback,new BasicNameValuePair("id", id),
					new BasicNameValuePair("offset", ""+offset), 
					new BasicNameValuePair("limit", ""+limit));
		}else{
			this.doHttpGet(INVITATION_HOME_LIST, netCallback,
					new BasicNameValuePair("offset", ""+offset), 
					new BasicNameValuePair("limit", ""+limit));
		}
		
	}
	
	
	
	/**
	 * 密邻列表
	 * @param offset 偏移量 缺省0
	 * @param limit  返回数目上限值 缺省10
	 * @param callback 返回数据接口
	 * @param ctx
	 */
	
	public void requestNeighborList(final int offset, final int limit, final JSONCaller callback){
	    NameValuePair[] params = {
	            new BasicNameValuePair("offset", "" + offset),
	            new BasicNameValuePair("limit", "" + limit)
	    };
        JSONCaller netCallback = new JSONCaller() {
            
            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject) data;
                try {
                    int rc = result.getInt("rc");
                    if(rc != 0){
                        callback.call(null);
                        return -1;
                    }
                    CxLog.i("RkNeighbourApi_men", result.toString());
//                    System.out.println(result.toString());
                    JSONArray array = result.getJSONArray("data");
                    callback.call(array);
                } catch (JSONException e) {
                    CxLog.e("requestNeighborList", e.getMessage());
                }
                
                return 0;
            }
        };
        this.doHttpGet(NEIGHBOR_LIST, netCallback, params);
	}
	
	public void requestNeighbourQuery(final String id, final JSONCaller callback) throws Exception{
		 if (TextUtils.isEmpty(id)) {
	            throw new Exception("neighbour id can not be null");
	        }
		    NameValuePair[] params = {new BasicNameValuePair("id", id)};
		    
		    JSONCaller netCallback = new JSONCaller(){

	            @Override
	            public int call(Object data) {
	                JSONObject result = (JSONObject)data;
	                CxLog.i("RkNeighbourApi_men", result.toString());
	                try {
	                    int rc = result.getInt("rc");
	                    if(rc != 0 ){
	                        callback.call(result);
	                        return 0;
	                    }
	                    
	                    callback.call(result);
	                } catch (JSONException e) {
	                    CxLog.e("requestNeighborQuery", e.getMessage());
	                }
	                return 0;
	            }
		        
		    };
		    this.doHttpGet(NEIGHBOR_QUERY, netCallback, params);
	}
	
	/**
	 * 密邻添加
	 * @param id 密邻号
	 * @param callback
	 * @throws Exception
	 */
	public void requestNeighborInvite(final String id, final JSONCaller callback) throws Exception{
       if (TextUtils.isEmpty(id)) {
            throw new Exception("inivite id can not be null");
        }
	    NameValuePair[] params = {new BasicNameValuePair("id", id)};
	    
	    JSONCaller netCallback = new JSONCaller(){

            @Override
            public int call(Object data) {

            	CxLog.i("RkNeighbourApi_men", data.toString());
                callback.call(data);
//                JSONObject result = (JSONObject)data;
//                try {
//                    int rc = result.getInt("rc");
//                    if(rc != 0 ){
//                        callback.call(null);
//                        return 0;
//                    }
//                    callback.call(result);
//                } catch (JSONException e) {
//                    RkLog.e("requestNeighborInvite", e.getMessage());
//                }
                return 0;
            }
	        
	    };
	    this.doHttpGet(NEIGHBOR_INVITE, netCallback, params);
	}
	
	/**
	 * 密邻解除
	 * @param id 密邻号
	 * @param callback
	 * @throws Exception
	 */
	public void requestNeighborRemove(final String id, final JSONCaller callback) throws Exception{
		if (TextUtils.isEmpty(id)) {
			throw new Exception("inivite id can not be null");
		}
		NameValuePair[] params = {new BasicNameValuePair("group_id", id)};
		
		JSONCaller netCallback = new JSONCaller(){
			
			@Override
			public int call(Object data) {
				
				CxLog.i("RkNeighbourApi_men", data.toString());
				callback.call(data);
				return 0;
			}
			
		};
		this.doHttpGet(NEIGHBOUR_REMOVE, netCallback, params);
	}
	
	
	
	
	/**
	 * 统计各种邀请方式的使用频率
	 * @param type
	 * @param subtype
	 * @param callback
	 */
	public void requestInviteType(final String type, final int subtype, final JSONCaller callback){
		/*if (0 >= offset) {
			offset = 0;
		}
		if (limit <= 0) {
			limit = 10;
		}*/
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				
				if (null == result) {
//					callback.call(null);
					return -1;
				}
				
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == jObj) {
//					callback.call(null);
					return -2;
				}				
	
//				System.out.println(jObj.toString());
				CxLog.i("RkNeighbourApi_men",jObj.toString());
				
//				callback.call(jObj);
				return 0;
			}
		};

		this.doHttpGet(NEIGHBOR_INVITE_TYPE, netCallback, new BasicNameValuePair("type", type+""), 
				new BasicNameValuePair("subtype", ""+subtype));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
