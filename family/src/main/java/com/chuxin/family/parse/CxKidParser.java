package com.chuxin.family.parse;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.kids.CxKidListCacheData;
import com.chuxin.family.parse.been.CxKidAddReply;
import com.chuxin.family.parse.been.CxKidFeed;
import com.chuxin.family.parse.been.CxKidFeedList;
import com.chuxin.family.parse.been.data.KidFeedChildrenData;
import com.chuxin.family.parse.been.data.KidFeedData;
import com.chuxin.family.parse.been.data.KidFeedPhoto;
import com.chuxin.family.parse.been.data.KidFeedPost;
import com.chuxin.family.parse.been.data.KidFeedReply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.ArrayList;

public class CxKidParser {

	
	/**
	 * kid帖子列表解析
	 * 
	 * @param offset
	 * @param obj
	 * @param ctx
	 * @return
	 */
	public synchronized CxKidFeedList getKidHomeList(int offset, JSONObject obj, Context ctx) {
		
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
		
		CxKidFeedList list = new CxKidFeedList();
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

		JSONObject data = null;
		try {
			data = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == data) {			
			return list;
		}
		// 访问成功的情况需要存入数据库
		if (0 == offset) { // 只保存第一屏
			try {
				CxKidListCacheData cache = new CxKidListCacheData(ctx);
				cache.insertData(CxGlobalParams.getInstance().getUserId(),obj.toString());
			} catch (Exception e) {
			}
		}
		
		
		JSONArray infosArray = null;
		try {
			infosArray = data.getJSONArray("children");
		} catch (JSONException e) {
		}
		
		
		if(infosArray!=null){
			ArrayList<KidFeedChildrenData> kidInfos=new ArrayList<KidFeedChildrenData>();
			for(int k=0;k<infosArray.length();k++){
				JSONObject infoObj = null;
				try {
					infoObj = infosArray.getJSONObject(k);
				} catch (JSONException e) {
				}
				if (null == infoObj) {
					continue;
				}
				KidFeedChildrenData kidInfo = new KidFeedChildrenData();
				
				try {
					if (!infoObj.isNull("id")) {
						kidInfo.setId(infoObj.getString("id"));
					}
					if (!infoObj.isNull("avata")) {
						kidInfo.setAvata(infoObj.getString("avata"));
					}
					if (!infoObj.isNull("name")) {
						kidInfo.setName(infoObj.getString("name"));
					}
					if (!infoObj.isNull("nickname")) {
						kidInfo.setNickname(infoObj.getString("nickname"));
					}
					if (!infoObj.isNull("gender")) {
						kidInfo.setGender(infoObj.getInt("gender"));
					}
					if (!infoObj.isNull("birth")) {
						kidInfo.setBirth(infoObj.getString("birth"));
					}
					if (!infoObj.isNull("note")) {
						kidInfo.setNote(infoObj.getString("note"));
					}
					if (!infoObj.isNull("data")) {
						kidInfo.setData(infoObj.getString("data"));
					}
				} catch (JSONException e) {
				}
				kidInfos.add(kidInfo);
			}
			
			list.setKids(kidInfos);
		}
		
		

		JSONArray feedArray = null;
		try {
			feedArray = data.getJSONArray("feeds");
		} catch (JSONException e) {
		}

		if ((null == feedArray) || (feedArray.length() < 1)) {
			return list;
		}
		ArrayList<KidFeedData> feeds = new ArrayList<KidFeedData>();
		for (int i = 0; i < feedArray.length(); i++) {
			JSONObject tempContentObject = null;
			try {
				tempContentObject = feedArray.getJSONObject(i);
			} catch (JSONException e) {
			}
			if (null == tempContentObject) {
				continue;
			}
			KidFeedData feedContent = new KidFeedData();

			try {			
				
				if (!tempContentObject.isNull("author")) {
					feedContent.setAuthor(tempContentObject.getString("author"));
				}
				if (!tempContentObject.isNull("create")) {
					feedContent.setCreate(tempContentObject.getLong("create") + "");
				}	
				if (!tempContentObject.isNull("id")) {
					feedContent.setId(tempContentObject.getString("id"));
				}
				if (!tempContentObject.isNull("pair_id")) {
					feedContent.setPair_id(tempContentObject.getString("pair_id"));
				}
				if (!tempContentObject.isNull("type")) {
					feedContent.setType(tempContentObject.getString("type"));
				}
				
				if (!tempContentObject.isNull("is_new")) {
					feedContent.setIsNew(tempContentObject.getInt("is_new"));
				}				
				if (!tempContentObject.isNull("open_url")) {
					feedContent.setOpen_url(tempContentObject.getString("open_url"));
				}				
			} catch (JSONException e) {
			}

			// post部分
			JSONObject postObject = null;
			try {
				postObject = tempContentObject.getJSONObject("post");
			} catch (JSONException e) {
			}
			if (null == postObject) {
				continue;
			}

			KidFeedPost feedPost = new KidFeedPost();
			// post的replay部分
			JSONArray replayArray = null;
			try {
				replayArray = postObject.getJSONArray("reply");
			} catch (JSONException e) {
			}
			if ((null != replayArray) && (replayArray.length() > 0)) {
				ArrayList<KidFeedReply> feedReplays = new ArrayList<KidFeedReply>();
				for (int j = 0; j < replayArray.length(); j++) {
					JSONObject replayObj = null;
					try {
						replayObj = replayArray.getJSONObject(j);
					} catch (JSONException e) {
					}
					if (null == replayObj) {
						continue;
					}
				
					KidFeedReply tempReplay = new KidFeedReply();

					try {
						if (!replayObj.isNull("name")) {
							tempReplay.setName(replayObj.getString("name"));
						}

						if (!replayObj.isNull("audio")) {
							tempReplay.setAudio(replayObj.getString("audio"));
						}				
						if (!replayObj.isNull("audio_len")) {
							tempReplay.setAudio_len(replayObj.getInt("audio_len"));
						}
						if (!replayObj.isNull("author")) {
							tempReplay.setAuthor(replayObj.getString("author"));
						}
						if (!replayObj.isNull("feed_id")) {
							tempReplay.setFeed_id(replayObj.getString("feed_id"));
						}
						if (!replayObj.isNull("reply_id")) {
							tempReplay.setReply_id(replayObj.getString("reply_id"));
						}

						if (!replayObj.isNull("reply_to")) {
							tempReplay.setReply_to(replayObj.getString("reply_to"));
						}

						if (!replayObj.isNull("text")) {
							tempReplay.setText(replayObj.getString("text"));
						}

						if (!replayObj.isNull("ts")) {
							tempReplay.setTs(replayObj.getInt("ts"));
						}
						
						if (!replayObj.isNull("type")) {
							tempReplay.setType(replayObj.getString("type"));
						}
						if (!replayObj.isNull("is_new")) {
							tempReplay.setIsNew(replayObj.getInt("is_new"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					feedReplays.add(tempReplay);
				} // end for(j)
				feedPost.setReplays(feedReplays);
			}

			// post的photo部分
			JSONArray photoArray = null;
			try {
				photoArray = postObject.getJSONArray("photos");
			} catch (JSONException e) {
			}
			if ((null != photoArray) && (photoArray.length() > 0)) {
				ArrayList<KidFeedPhoto> photos = new ArrayList<KidFeedPhoto>();
				for (int k = 0; k < photoArray.length(); k++) {
					JSONObject photoObj = null;
					try {
						photoObj = photoArray.getJSONObject(k);
					} catch (JSONException e) {
					}
					if (null == photoObj) {
						continue;
					}
					KidFeedPhoto photo = new KidFeedPhoto();
					try {
						if (!photoObj.isNull("big")) {
							photo.setBig(photoObj.getString("big"));
						}
						if (!photoObj.isNull("small")) {
							photo.setSmall(photoObj.getString("small"));
						}
						if (!photoObj.isNull("thumb")) {
							photo.setThumb(photoObj.getString("thumb"));
						}
					} catch (JSONException e) {
					}

					photos.add(photo);
				}
				feedPost.setPhotos(photos);
			}

			// post的text部分
			try {
				if (!postObject.isNull("text")) {
					feedPost.setText(postObject.getString("text"));
				}
			} catch (JSONException e) {
			}
			feedContent.setPost(feedPost);

			feeds.add(feedContent);

		} // end for(i)
		list.setDatas(feeds);
		return list;
	}

	
	
	/**
	 * 发帖子的解析
	 * 
	 * @param obj
	 * @return
	 */
	public CxKidFeed getAddFeedResult(JSONObject obj) {
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
		CxKidFeed sendFeed = new CxKidFeed();
		sendFeed.setRc(rc);

		try {
			if (!obj.isNull("msg")) {
				sendFeed.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
		}

		try {
			sendFeed.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return sendFeed;
		}

		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e1) {
		}
		if (null == dataObj) {
			return sendFeed;
		}

		KidFeedData feedContent = new KidFeedData();
		try {			
			if (!dataObj.isNull("author")) {
				feedContent.setAuthor(dataObj.getString("author"));
			}
			if (!dataObj.isNull("create")) {
				feedContent.setCreate(dataObj.getLong("create") + "");
			}	
			if (!dataObj.isNull("id")) {
				feedContent.setId(dataObj.getString("id"));
			}
			if (!dataObj.isNull("pair_id")) {
				feedContent.setPair_id(dataObj.getString("pair_id"));
			}
			if (!dataObj.isNull("type")) {
				feedContent.setType(dataObj.getString("type"));
			}
			
			if (!dataObj.isNull("is_new")) {
				feedContent.setIsNew(dataObj.getInt("is_new"));
			}
			if (!dataObj.isNull("open_url")) {
				feedContent.setOpen_url(dataObj.getString("open_url"));
			}	
		} catch (JSONException e) {
		}

		// post部分
		JSONObject postObject = null;
		try {
			postObject = dataObj.getJSONObject("post");
		} catch (JSONException e) {
		}
		if (null == postObject) {
			sendFeed.setData(feedContent);
			return sendFeed;
		}

		KidFeedPost feedPost = new KidFeedPost();
		

		// post的photo部分
		JSONArray photoArray = null;
		try {
			photoArray = postObject.getJSONArray("photos");
		} catch (JSONException e) {
		}
		if ((null != photoArray) && (photoArray.length() > 0)) {
			ArrayList<KidFeedPhoto> photos = new ArrayList<KidFeedPhoto>();
			for (int k = 0; k < photoArray.length(); k++) {
				JSONObject photoObj = null;
				try {
					photoObj = photoArray.getJSONObject(k);
				} catch (JSONException e) {
				}
				if (null == photoObj) {
					continue;
				}
				KidFeedPhoto photo = new KidFeedPhoto();
				try {
					if (!photoObj.isNull("big")) {
						photo.setBig(photoObj.getString("big"));
					}
					if (!photoObj.isNull("small")) {
						photo.setSmall(photoObj.getString("small"));
					}
					if (!photoObj.isNull("thumb")) {
						photo.setThumb(photoObj.getString("thumb"));
					}
				} catch (JSONException e) {
				}

				photos.add(photo);
			}
			feedPost.setPhotos(photos);
		}

		// post的text部分
		try {
			if (!postObject.isNull("text")) {
				feedPost.setText(postObject.getString("text"));
			}
		} catch (JSONException e) {
		}
		feedContent.setPost(feedPost);

		sendFeed.setData(feedContent);
		return sendFeed;
	}
	
	
	/**
	 * 发送帖子回复结果解析
	 * 
	 * @param replayObj
	 * @return
	 */
	public CxKidAddReply getAddReplyResult(JSONObject obj) {

		CxKidAddReply reply = new CxKidAddReply();
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
		reply.setRc(rc);

		try {
			if (!obj.isNull("msg")) {
				reply.setMsg(obj.getString("msg"));
			}
			reply.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return reply;
		}

		JSONObject replayObj = null;
		try {
			replayObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if ((null == replayObj)) {
			return reply;
		}

		KidFeedReply tempReplay = new KidFeedReply();

		try {
			if (!replayObj.isNull("name")) {
				tempReplay.setName(replayObj.getString("name"));
			}

			if (!replayObj.isNull("audio")) {
				tempReplay.setAudio(replayObj.getString("audio"));
			}
			
			if (!replayObj.isNull("audio_len")) {
				tempReplay.setAudio_len(replayObj.getInt("audio_len"));
			}

			if (!replayObj.isNull("author")) {
				tempReplay.setAuthor(replayObj.getString("author"));
			}

			if (!replayObj.isNull("feed_id")) {
				tempReplay.setFeed_id(replayObj.getString("feed_id"));
			}

			if (!replayObj.isNull("reply_id")) {
				tempReplay.setReply_id(replayObj.getString("reply_id"));
			}

			if (!replayObj.isNull("reply_to")) {
				tempReplay.setReply_to(replayObj.getString("reply_to"));
			}

			if (!replayObj.isNull("text")) {
				tempReplay.setText(replayObj.getString("text"));
			}
			if (!replayObj.isNull("ts")) {
				tempReplay.setTs(replayObj.getInt("ts"));
			}
			if (!replayObj.isNull("type")) {
				tempReplay.setType(replayObj.getString("type"));
			}
			if (!replayObj.isNull("type")) {
				tempReplay.setType(replayObj.getString("type"));
			}
			if (!replayObj.isNull("is_new")) {
				tempReplay.setIsNew(replayObj.getInt("is_new"));
			}
		} catch (JSONException e) {
		}

		reply.setReply(tempReplay);

		return reply;
	}
	
	
	
}
