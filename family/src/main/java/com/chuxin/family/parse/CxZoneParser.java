package com.chuxin.family.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.parse.been.CxChangeBgOfZone;
import com.chuxin.family.parse.been.CxReply;
import com.chuxin.family.parse.been.CxReplyList;
import com.chuxin.family.parse.been.CxShareThdRes;
import com.chuxin.family.parse.been.CxZoneFeedList;
import com.chuxin.family.parse.been.CxZoneSendFeed;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.parse.been.data.FeedPost;
import com.chuxin.family.parse.been.data.FeedReply;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.zone.CxZoneCacheData;

/**
 * 二人空间的解析
 * 
 * @author shichao.wang
 * 
 */
public class CxZoneParser {

	/**
	 * 帖子列表解析
	 * 
	 * @param obj
	 * @return
	 */
	public synchronized CxZoneFeedList getZoneFeedList(int offset, JSONObject obj, Context ctx) {
		if (null == obj) {
			return null;
		}
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (JSONException e) {
			CxLog.i("", ""+e.toString());
		}
		if (-1 == rc) {
			return null;
		}
		CxZoneFeedList zoneFeeds = new CxZoneFeedList();
		zoneFeeds.setRc(rc);

		try {
			if(!obj.isNull("msg")){
				zoneFeeds.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
			CxLog.i("", ""+e.toString());
		}

		try {                                                                                                                                                                                                                                                                                                                                                                                                                  
			zoneFeeds.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
			CxLog.i("", ""+e.toString());
		}

		if (0 != rc) {
			return zoneFeeds;
		}

		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
			CxLog.i("", ""+e.toString());
		}
		if (null == dataObj) {
			return zoneFeeds;
		}
		
		try {
			if (!dataObj.isNull("together_date")) { //在一起xx天
				String togetherStr = dataObj.getString("together_date");
				zoneFeeds.setTogetherDay(togetherStr);
				CxGlobalParams.getInstance().setTogetherDayStr(togetherStr);
			}
		} catch (JSONException e2) {
			CxLog.i("", ""+e2.toString());
		}
		
		JSONArray feedsArr = null;
		try {
			feedsArr = dataObj.getJSONArray("feeds");
		} catch (JSONException e1) {
			CxLog.i("", ""+e1.toString());
		}
		if (null == feedsArr) {
			return zoneFeeds;
		}
		
		//访问成功的情况需要存入数据库
		if (0 == offset) { //只保存第一屏
			try {
				CxZoneCacheData cache = new CxZoneCacheData(ctx);
				cache.insertZoneData(feedsArr.toString());
			} catch (Exception e) {
				CxLog.i("", ""+e.toString());
			}
		}

		List<FeedListData> feedListdData = new ArrayList<FeedListData>();
		for (int i = 0; i < feedsArr.length(); i++) {
			JSONObject tempContentObject = null;
			try {
				tempContentObject = feedsArr.getJSONObject(i);
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}
			if (null == tempContentObject) {
				continue;
			}
			FeedListData feedContent = new FeedListData();

			
			try {
				if (!tempContentObject.isNull("is_new")) {
					feedContent.setIsNew(tempContentObject.getInt("is_new"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			try {
				if (!tempContentObject.isNull("open_url")) {
					feedContent.setOpenUrl(tempContentObject.getString("open_url"));
				}
			} catch (Exception e) {
				CxLog.i("", ""+e.toString());
			}
			
			try {
				if (!tempContentObject.isNull("author")) {
					feedContent.setAuthor(tempContentObject.getString("author"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			try {
				if (!tempContentObject.isNull("create")) {
					feedContent.setCreate(tempContentObject.getString("create"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			try {
				if (!tempContentObject.isNull("id")) {
					feedContent.setId(tempContentObject.getString("id"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			try {
				if (!tempContentObject.isNull("pair_id")) {
					feedContent.setPair_id(tempContentObject.getString("pair_id"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			try {
				if (!tempContentObject.isNull("status")) {
					feedContent.setStatus(tempContentObject.getInt("status"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			try {
				if (!tempContentObject.isNull("type")) {
					feedContent.setType(tempContentObject.getString("type"));
				}
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}

			// post部分
			JSONObject postObject = null;
			try {
				postObject = tempContentObject.getJSONObject("post");
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}
			if (null == postObject) {
				continue;
			}

			FeedPost feedPost = new FeedPost();
			// post的replay部分
			JSONArray replayArray = null;
			try {
				replayArray = postObject.getJSONArray("reply");
			} catch (JSONException e) {
				CxLog.i("", ""+e.toString());
			}
			if ((null != replayArray) && (replayArray.length() > 0)) {
				List<FeedReply> feedReplays = new ArrayList<FeedReply>();
				for (int j = 0; j < replayArray.length(); j++) {
					JSONObject replayObj = null;
					try {
						replayObj = replayArray.getJSONObject(j);
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					if (null == replayObj) {
						continue;
					}
					FeedReply tempReplay = new FeedReply();
					
					try {
						if (!replayObj.isNull("is_new")) {
							tempReplay.setIsNew(replayObj.getInt("is_new"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					
					try {
						if (!replayObj.isNull("author")) {
							tempReplay.setAuthor(replayObj.getString("author"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("extra")) {
							tempReplay.setExtra(replayObj.getString("extra"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("feed_id")) {
							tempReplay.setFeed_id(replayObj.getString("feed_id"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("reply_id")) {
							tempReplay.setReply_id(replayObj.getString("reply_id"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("reply_to")) {
							tempReplay.setReply_to(replayObj.getString("reply_to"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("status")) {
							tempReplay.setStatus(replayObj.getInt("status"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("text")) {
							tempReplay.setText(replayObj.getString("text"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						tempReplay.setTs(replayObj.getInt("ts"));
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!replayObj.isNull("type")) {
							tempReplay.setType(replayObj.getString("type"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
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
				CxLog.i("", ""+e.toString());
			}
			if ((null != photoArray) && (photoArray.length() > 0)) {
				List<FeedPhoto> photos = new ArrayList<FeedPhoto>();
				for (int k = 0; k < photoArray.length(); k++) {
					JSONObject photoObj = null;
					try {
						photoObj = photoArray.getJSONObject(k);
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					if (null == photoObj) {
						continue;
					}
					FeedPhoto photo = new FeedPhoto();
					try {
						if (!photoObj.isNull("big")) {
							photo.setBig(photoObj.getString("big"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!photoObj.isNull("small")) {
							photo.setSmall(photoObj.getString("small"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
					}
					try {
						if (!photoObj.isNull("thumb")) {
							photo.setThumb(photoObj.getString("thumb"));
						}
					} catch (JSONException e) {
						CxLog.i("", ""+e.toString());
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
				CxLog.i("", ""+e.toString());
			}
			feedContent.setPost(feedPost);

			feedListdData.add(feedContent);
		} // end for(i)
		zoneFeeds.setData(feedListdData);

		return zoneFeeds;
	}

	/**
	 * 发帖子的解析
	 * @param obj
	 * @return
	 */
	public CxZoneSendFeed getSendFeedResult(JSONObject obj) {
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
		CxZoneSendFeed sendFeed = new CxZoneSendFeed();
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
			e1.printStackTrace();
		}
		if (null == dataObj) {
			return sendFeed;
		}
		
		FeedListData feedContent = new FeedListData();
		try {
			if (!dataObj.isNull("author")) {
				feedContent.setAuthor(dataObj.getString("author"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			if (!dataObj.isNull("open_url")) {
				feedContent.setOpenUrl(dataObj.getString("open_url"));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			if (!dataObj.isNull("create")) {
				feedContent.setCreate(dataObj.getString("create"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (!dataObj.isNull("id")) {
				feedContent.setId(dataObj.getString("id"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (!dataObj.isNull("pair_id")) {
				feedContent.setPair_id(dataObj.getString("pair_id"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			feedContent.setStatus(dataObj.getInt("status"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (!dataObj.isNull("type")) {
				feedContent.setType(dataObj.getString("type"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// post部分
		JSONObject postObject = null;
		try {
			postObject = dataObj.getJSONObject("post");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (null == postObject) {
			sendFeed.setData(feedContent);
			return sendFeed;
		}

		FeedPost feedPost = new FeedPost();
		// post的replay部分
		JSONArray replayArray = null;
		try {
			replayArray = postObject.getJSONArray("reply");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ((null != replayArray) && (replayArray.length() > 0)) {
			List<FeedReply> feedReplays = new ArrayList<FeedReply>();
			for (int j = 0; j < replayArray.length(); j++) {
				JSONObject replayObj = null;
				try {
					replayObj = replayArray.getJSONObject(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (null == replayObj) {
					continue;
				}
				FeedReply tempReplay = new FeedReply();
				try {
					if (!replayObj.isNull("author")) {
						tempReplay.setAuthor(replayObj.getString("author"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!replayObj.isNull("extra")) {
						tempReplay.setExtra(replayObj.getString("extra"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!replayObj.isNull("feed_id")) {
						tempReplay.setFeed_id(replayObj.getString("feed_id"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!replayObj.isNull("reply_id")) {
						tempReplay.setReply_id(replayObj.getString("reply_id"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!replayObj.isNull("reply_to")) {
						tempReplay.setReply_to(replayObj.getString("reply_to"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					tempReplay.setStatus(replayObj.getInt("status"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!replayObj.isNull("text")) {
						tempReplay.setText(replayObj.getString("text"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					tempReplay.setTs(replayObj.getInt("ts"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!replayObj.isNull("type")) {
						tempReplay.setType(replayObj.getString("type"));
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
			List<FeedPhoto> photos = new ArrayList<FeedPhoto>();
			for (int k = 0; k < photoArray.length(); k++) {
				JSONObject photoObj = null;
				try {
					photoObj = photoArray.getJSONObject(k);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (null == photoObj) {
					continue;
				}
				FeedPhoto photo = new FeedPhoto();
				try {
					if (!photoObj.isNull("big")) {
						photo.setBig(photoObj.getString("big"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!photoObj.isNull("small")) {
						photo.setSmall(photoObj.getString("small"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					if (!photoObj.isNull("thumb")) {
						photo.setThumb(photoObj.getString("thumb"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
			e.printStackTrace();
		}
		feedContent.setPost(feedPost);

		sendFeed.setData(feedContent);
		return sendFeed;
	}

	/**
	 * 回复帖子的数据解析
	 * @param obj
	 * @return
	 */
	public CxReply getReplyResult(JSONObject obj){
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
		CxReply reply = new CxReply();
		reply.setRc(rc);
		try {
			if (!obj.isNull("msg")) {
				reply.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
		}
		try {
			reply.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (null == dataObj) {
			return reply;
		}
		
		FeedReply dataReply = new FeedReply();
		try {
			if (!dataObj.isNull("author")) {
				dataReply.setAuthor(dataObj.getString("author"));
			}
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("extra")) {
				dataReply.setExtra(dataObj.getString("extra"));
			}
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("feed_id")) {
				dataReply.setFeed_id(dataObj.getString("feed_id"));
			}
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("reply_id")) {
				dataReply.setReply_id(dataObj.getString("reply_id"));
			}
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("reply_to")) {
				dataReply.setReply_to(dataObj.getString("reply_to"));
			}
		} catch (JSONException e) {
		}
		try {
			dataReply.setStatus(dataObj.getInt("status"));
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("text")) {
				dataReply.setText(dataObj.getString("text"));
			}
		} catch (JSONException e) {
		}
		try {
			dataReply.setTs(dataObj.getInt("ts"));
		} catch (JSONException e) {
		}
		try {
			if (!dataObj.isNull("type")) {
				dataReply.setType(dataObj.getString("type"));
			}
		} catch (JSONException e) {
		}
		reply.setData(dataReply);
		
		return reply;
	}
	
	/**
	 * 回复列表
	 * @param obj
	 * @return
	 */
	public CxReplyList getReplyList(JSONObject obj){
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
		CxReplyList replyList = new CxReplyList();
		replyList.setRc(rc);
		try {
			if (!obj.isNull("msg")) {
				replyList.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
		}
		try {
			replyList.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}
		JSONArray dataArray = null;
		try {
			dataArray = obj.getJSONArray("data");
		} catch (JSONException e) {
		}
		if ( (null == dataArray) || (dataArray.length() < 1) ) {
			return replyList;
		}
		
		List<FeedReply> replies = new ArrayList<FeedReply>();
		for(int i = 0; i < dataArray.length(); i++){
			try {
				JSONObject replyObj = dataArray.getJSONObject(i);
				FeedReply singleReply = new FeedReply();
				try {
					if (!replyObj.isNull("author")) {
						singleReply.setAuthor(replyObj.getString("author"));
					}
				} catch (Exception e) {
				}
				try {
					if (!replyObj.isNull("extra")) {
						singleReply.setExtra(replyObj.getString("extra"));
					}
				} catch (Exception e) {
				}
				try {
					if (!replyObj.isNull("feed_id")) {
						singleReply.setFeed_id(replyObj.getString("feed_id"));
					}
				} catch (Exception e) {
				}
				try {
					if (!replyObj.isNull("reply_id")) {
						singleReply.setReply_id(replyObj.getString("reply_id"));
					}
				} catch (Exception e) {
				}
				try {
					if (!replyObj.isNull("reply_to")) {
						singleReply.setReply_to(replyObj.getString("reply_to"));
					}
					
				} catch (Exception e) {
				}
				try {
					singleReply.setStatus(replyObj.getInt("status"));
				} catch (Exception e) {
				}
				try {
					if (!replyObj.isNull("text")) {
						singleReply.setText(replyObj.getString("text"));
					}
				} catch (Exception e) {
				}
				try {
					singleReply.setTs(replyObj.getInt("ts"));
				} catch (Exception e) {
				}
				try {
					if (!replyObj.isNull("type")) {
						singleReply.setType(replyObj.getString("type"));
					}
				} catch (Exception e) {
				}
				if (null != singleReply) {
					replies.add(singleReply);
				}
			} catch (JSONException e) {
			}
			
		} //end for(i)
		
		replyList.setReplies(replies);
		return replyList;
	}
	
	//二人空间的本地数据解析
	public List<FeedListData> getFeedsContent(String feedsDataStr){
		if (TextUtils.isEmpty(feedsDataStr)) {
			return null;
		}
		
		JSONArray dataArray = null;
		try {
			dataArray = new JSONArray(feedsDataStr);
		} catch (JSONException e) {
		}
		
		if ( (null == dataArray) || (dataArray.length() < 1) ){
			return null;
		}
		
		List<FeedListData> feedListdData = new ArrayList<FeedListData>();
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject tempContentObject = null;
			try {
				tempContentObject = dataArray.getJSONObject(i);
			} catch (JSONException e) {
			}
			if (null == tempContentObject) {
				continue;
			}
			FeedListData feedContent = new FeedListData();

			try {
				if (!tempContentObject.isNull("author")) {
					feedContent.setAuthor(tempContentObject.getString("author"));
				}
			} catch (JSONException e) {
			}

			try {
				if (!tempContentObject.isNull("create")) {
					feedContent.setCreate(tempContentObject.getString("create"));
				}
			} catch (JSONException e) {
			}

			try {
				if (!tempContentObject.isNull("id")) {
					feedContent.setId(tempContentObject.getString("id"));
				}
			} catch (JSONException e) {
			}

			try {
				if (!tempContentObject.isNull("pair_id")) {
					feedContent.setPair_id(tempContentObject.getString("pair_id"));
				}
			} catch (JSONException e) {
			}

			try {
				feedContent.setStatus(tempContentObject.getInt("status"));
			} catch (JSONException e) {
			}

			try {
				if (!tempContentObject.isNull("type")) {
					feedContent.setType(tempContentObject.getString("type"));
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

			FeedPost feedPost = new FeedPost();
			// post的replay部分
			JSONArray replayArray = null;
			try {
				replayArray = postObject.getJSONArray("reply");
			} catch (JSONException e) {
			}
			if ((null != replayArray) && (replayArray.length() > 0)) {
				List<FeedReply> feedReplays = new ArrayList<FeedReply>();
				for (int j = 0; j < replayArray.length(); j++) {
					JSONObject replayObj = null;
					try {
						replayObj = replayArray.getJSONObject(j);
					} catch (JSONException e) {
					}
					if (null == replayObj) {
						continue;
					}
					FeedReply tempReplay = new FeedReply();
					try {
						if (!replayObj.isNull("author")) {
							tempReplay.setAuthor(replayObj.getString("author"));
						}
					} catch (JSONException e) {
					}
					try {
						if (!replayObj.isNull("extra")) {
							tempReplay.setExtra(replayObj.getString("extra"));
						}
					} catch (JSONException e) {
					}
					try {
						if (!replayObj.isNull("feed_id")) {
							tempReplay.setFeed_id(replayObj.getString("feed_id"));
						}
					} catch (JSONException e) {
					}
					try {
						if (!replayObj.isNull("reply_id")) {
							tempReplay.setReply_id(replayObj.getString("reply_id"));
						}
					} catch (JSONException e) {
					}
					try {
						if (!replayObj.isNull("reply_to")) {
							tempReplay.setReply_to(replayObj.getString("reply_to"));
						}
					} catch (JSONException e) {
					}
					try {
						tempReplay.setStatus(replayObj.getInt("status"));
					} catch (JSONException e) {
					}
					try {
						if (!replayObj.isNull("text")) {
							tempReplay.setText(replayObj.getString("text"));
						}
					} catch (JSONException e) {
					}
					try {
						tempReplay.setTs(replayObj.getInt("ts"));
					} catch (JSONException e) {
					}
					try {
						if (!replayObj.isNull("type")) {
							tempReplay.setType(replayObj.getString("type"));
						}
					} catch (JSONException e) {
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
				List<FeedPhoto> photos = new ArrayList<FeedPhoto>();
				for (int k = 0; k < photoArray.length(); k++) {
					JSONObject photoObj = null;
					try {
						photoObj = photoArray.getJSONObject(k);
					} catch (JSONException e) {
					}
					if (null == photoObj) {
						continue;
					}
					FeedPhoto photo = new FeedPhoto();
					try {
						if (!photoObj.isNull("big")) {
							photo.setBig(photoObj.getString("big"));
						}
					} catch (JSONException e) {
					}
					try {
						if (!photoObj.isNull("small")) {
							photo.setSmall(photoObj.getString("small"));
						}
					} catch (JSONException e) {
					}
					try {
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

			feedListdData.add(feedContent);
		} // end for(i)
		
		return feedListdData;
	}
	
	//修改二人空间背景
	public CxChangeBgOfZone parseForChangeBgOfZone(JSONObject obj){
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
		//----
		CxChangeBgOfZone changeBg = new CxChangeBgOfZone();
		changeBg.setRc(rc);
		try {
			if (!obj.isNull("msg")) {
				changeBg.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
		}

		try {
			changeBg.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return changeBg;
		}
		
		JSONObject dataObj = null;
		try {
			dataObj = obj.getJSONObject("data");
		} catch (JSONException e1) {
		}
		if (null == dataObj) {
			return changeBg;
		}
		
		if (!dataObj.isNull("bg_big")) {
			try {
				changeBg.setBg_big(dataObj.getString("bg_big"));
			} catch (JSONException e) {
			}
		}
		
		return changeBg;
	}
	
	public CxShareThdRes parseForShare(JSONObject obj){
		if (null == obj) {
			return null;
		}
		CxShareThdRes shareRes = new CxShareThdRes();
		int rc = -1;
		try {
			rc = obj.getInt("rc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (-1 == rc) {
			return null;
		}
		shareRes.setRc(rc);
		if (!obj.isNull("data")) {
			JSONObject dataObj = null;
			try {
				dataObj = obj.getJSONObject("data");
				shareRes.setData(dataObj.getString("open_url"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		try {
			if(!obj.isNull("msg")){
				shareRes.setMsg(obj.getString("msg"));
			}
			if(!obj.isNull("ts")){
				shareRes.setTs(obj.getInt("ts"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return shareRes;
	}
	
}
