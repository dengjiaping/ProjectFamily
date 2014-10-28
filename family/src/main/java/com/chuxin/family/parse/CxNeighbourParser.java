package com.chuxin.family.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.text.TextUtils;

import com.chuxin.family.neighbour.CxNbCacheData;
import com.chuxin.family.parse.been.CxNbReply;
import com.chuxin.family.parse.been.CxNeighbourInvitationList;
import com.chuxin.family.parse.been.CxNeighbourSendInvitation;
import com.chuxin.family.parse.been.CxZoneFeedList;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.parse.been.data.FeedPost;
import com.chuxin.family.parse.been.data.FeedReply;
import com.chuxin.family.parse.been.data.InvitationData;
import com.chuxin.family.parse.been.data.InvitationList;
import com.chuxin.family.parse.been.data.InvitationPhoto;
import com.chuxin.family.parse.been.data.InvitationPost;
import com.chuxin.family.parse.been.data.InvitationReply;
import com.chuxin.family.parse.been.data.InvitationUserInfo;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.zone.CxZoneCacheData;

public class CxNeighbourParser {

	/**
	 * 发帖子的解析
	 * 
	 * @param obj
	 * @return
	 */
	public CxNeighbourSendInvitation getSendInvitationResult(JSONObject obj) {
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
		CxNeighbourSendInvitation sendFeed = new CxNeighbourSendInvitation();
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

		InvitationData feedContent = new InvitationData();
		
		
		// userinfo部分
		JSONObject userObject = null;
		try {
			userObject = dataObj.getJSONObject("user_info");
		} catch (JSONException e) {
		}
		if(userObject==null){
			return sendFeed;
		}

		InvitationUserInfo feedInfo = new InvitationUserInfo();

		try {
			if (!userObject.isNull("husband_name")) {
				feedInfo.setHusbandName(userObject
						.getString("husband_name"));
			}
			if (!userObject.isNull("wife_name")) {
				feedInfo.setWifeName(userObject.getString("wife_name"));
			}
			if (!userObject.isNull("wife_avatar")) {
				feedInfo.setWifeUrl(userObject.getString("wife_avatar"));
			}
			if (!userObject.isNull("husband_avatar")) {
				feedInfo.setHusbandUrl(userObject
						.getString("husband_avatar"));
			}
		} catch (JSONException e) {
		}
		feedContent.setUserInfo(feedInfo);
		
		try {
			if (!dataObj.isNull("message_group_id")) {
				feedContent.setMessage_group_id(dataObj.getString("message_group_id"));
			}
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("author")) {
				feedContent.setAuthor(dataObj.getString("author"));
			}
		} catch (JSONException e) {
		}
		
		try {
			if (!dataObj.isNull("open_url")) {
				feedContent.setOpen_url(dataObj.getString("open_url"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (!dataObj.isNull("create")) {
				feedContent.setCreate(dataObj.getString("create"));
			}
		} catch (JSONException e) {
		}

		try {
			if (!dataObj.isNull("id")) {
				feedContent.setId(dataObj.getString("id"));
			}
		} catch (JSONException e) {
		}

		try {
			if (!dataObj.isNull("pair_id")) {
				feedContent.setPair_id(dataObj.getString("pair_id"));
			}
		} catch (JSONException e) {
		}

		try {
			feedContent.setStatus(dataObj.getInt("status"));
		} catch (JSONException e) {
		}

		try {
			if (!dataObj.isNull("type")) {
				feedContent.setType(dataObj.getString("type"));
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

		InvitationPost feedPost = new InvitationPost();
		// post的replay部分
		// JSONArray replayArray = null;
		// try {
		// replayArray = postObject.getJSONArray("reply");
		// } catch (JSONException e) {
		// }
		// if ((null != replayArray) && (replayArray.length() > 0)) {
		// List<InvitationReply> feedReplays = new ArrayList<InvitationReply>();
		// for (int j = 0; j < replayArray.length(); j++) {
		// JSONObject replayObj = null;
		// try {
		// replayObj = replayArray.getJSONObject(j);
		// } catch (JSONException e) {
		// }
		// if (null == replayObj) {
		// continue;
		// }
		// FeedReply tempReplay = new FeedReply();
		// try {
		// if (!replayObj.isNull("author")) {
		// tempReplay.setAuthor(replayObj.getString("author"));
		// }
		// } catch (JSONException e) {
		// }
		// try {
		// if (!replayObj.isNull("extra")) {
		// tempReplay.setExtra(replayObj.getString("extra"));
		// }
		// } catch (JSONException e) {
		// }
		// try {
		// if (!replayObj.isNull("feed_id")) {
		// tempReplay.setFeed_id(replayObj.getString("feed_id"));
		// }
		// } catch (JSONException e) {
		// }
		// try {
		// if (!replayObj.isNull("reply_id")) {
		// tempReplay.setReply_id(replayObj.getString("reply_id"));
		// }
		// } catch (JSONException e) {
		// }
		// try {
		// if (!replayObj.isNull("reply_to")) {
		// tempReplay.setReply_to(replayObj.getString("reply_to"));
		// }
		// } catch (JSONException e) {
		// }
		// try {
		// tempReplay.setStatus(replayObj.getInt("status"));
		// } catch (JSONException e) {
		// }
		// try {
		// if (!replayObj.isNull("text")) {
		// tempReplay.setText(replayObj.getString("text"));
		// }
		// } catch (JSONException e) {
		// }
		// try {
		// tempReplay.setTs(replayObj.getInt("ts"));
		// } catch (JSONException e) {
		// }
		// try {
		// if (!replayObj.isNull("type")) {
		// tempReplay.setType(replayObj.getString("type"));
		// }
		// } catch (JSONException e) {
		// }
		//
		// feedReplays.add(tempReplay);
		// } // end for(j)
		// feedPost.setReplays(feedReplays);
		// }

		// post的photo部分
		JSONArray photoArray = null;
		try {
			photoArray = postObject.getJSONArray("photos");
		} catch (JSONException e) {
		}
		if ((null != photoArray) && (photoArray.length() > 0)) {
			ArrayList<InvitationPhoto> photos = new ArrayList<InvitationPhoto>();
			for (int k = 0; k < photoArray.length(); k++) {
				JSONObject photoObj = null;
				try {
					photoObj = photoArray.getJSONObject(k);
				} catch (JSONException e) {
				}
				if (null == photoObj) {
					continue;
				}
				InvitationPhoto photo = new InvitationPhoto();
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

		sendFeed.setData(feedContent);
		return sendFeed;
	}

	/**
	 * 发送帖子回复结果解析
	 * 
	 * @param replayObj
	 * @return
	 */
	public CxNbReply getSendNbReplyResult(JSONObject obj) {

		CxNbReply nbReply = new CxNbReply();
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
		nbReply.setRc(rc);

		try {
			if (!obj.isNull("msg")) {
				nbReply.setMsg(obj.getString("msg"));
			}
			nbReply.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return nbReply;
		}

		JSONObject replayObj = null;
		try {
			replayObj = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if ((null == replayObj)) {
			return nbReply;
		}

		InvitationReply tempReplay = new InvitationReply();

		try {
			if (!replayObj.isNull("name")) {
				tempReplay.setName(replayObj.getString("name"));
			}

			if (!replayObj.isNull("audio")) {
				tempReplay.setAudio(replayObj.getString("audio"));
			}

			if (!replayObj.isNull("reply_name")) {
				tempReplay.setReply_name(replayObj.getString("reply_name"));
			}
			
			if (!replayObj.isNull("audio_len")) {
				tempReplay.setAudio_len(replayObj.getInt("audio_len"));
			}

			if (!replayObj.isNull("author")) {
				tempReplay.setAuthor(replayObj.getString("author"));
			}

			if (!replayObj.isNull("extra")) {
				tempReplay.setExtra(replayObj.getString("extra"));
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

			tempReplay.setStatus(replayObj.getInt("status"));

			if (!replayObj.isNull("text")) {
				tempReplay.setText(replayObj.getString("text"));
			}

			tempReplay.setTs(replayObj.getInt("ts"));

			if (!replayObj.isNull("type")) {
				tempReplay.setType(replayObj.getString("type"));
			}
		} catch (JSONException e) {
		}

		nbReply.setData(tempReplay);

		return nbReply;
	}

	/**
	 * 密邻帖子列表解析
	 * 
	 * @param offset
	 * @param obj
	 * @param ctx
	 * @return
	 */
	public synchronized CxNeighbourInvitationList getNbInvitationList(
			int offset, JSONObject obj, Context ctx) {
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
		CxNeighbourInvitationList nbInvitations = new CxNeighbourInvitationList();
		nbInvitations.setRc(rc);

		try {
			if (!obj.isNull("msg")) {
				nbInvitations.setMsg(obj.getString("msg"));
			}
			nbInvitations.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return nbInvitations;
		}

		JSONObject data = null;
		try {
			data = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if (null == data) {			
			return nbInvitations;
		}
		// 访问成功的情况需要存入数据库
		if (0 == offset) { // 只保存第一屏
			try {
				CxNbCacheData cache = new CxNbCacheData(ctx);
				cache.insertNbData("0",obj.toString());
			} catch (Exception e) {
			}
		}

		InvitationList list = new InvitationList();

//		try {
//			if (!data.isNull("bg_url"))
//				list.setBgUrl(data.getString("bg_url"));
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
		
		JSONObject userObj = null;
		try {
			userObj = data.getJSONObject("user_info");
		} catch (JSONException e) {
		}
		if (null == userObj) {
			return nbInvitations;
		}
		
		
		InvitationUserInfo userInfo = new InvitationUserInfo();

		try {
			if (!userObj.isNull("bg_url"))
				userInfo.setBgUrl(userObj.getString("bg_url"));

			if (!userObj.isNull("husband_name")) {
				userInfo.setHusbandName(userObj.getString("husband_name"));
			}
			if (!userObj.isNull("wife_name")) {
				userInfo.setWifeName(userObj.getString("wife_name"));
			}
			if (!userObj.isNull("wife_avatar")) {
				userInfo.setWifeUrl(userObj.getString("wife_avatar"));
			}
			if (!userObj.isNull("husband_avatar")) {
				userInfo.setHusbandUrl(userObj.getString("husband_avatar"));
			}
		} catch (JSONException e1) {
		}
		list.setUserInfo(userInfo);
			

		JSONArray feedArray = null;
		try {
			feedArray = data.getJSONArray("feeds");
		} catch (JSONException e) {
		}

		if ((null == feedArray) || (feedArray.length() < 1)) {
			nbInvitations.setData(list);
			return nbInvitations;
		}
		ArrayList<InvitationData> feeds = new ArrayList<InvitationData>();
		for (int i = 0; i < feedArray.length(); i++) {
			JSONObject tempContentObject = null;
			try {
				tempContentObject = feedArray.getJSONObject(i);
			} catch (JSONException e) {
			}
			if (null == tempContentObject) {
				continue;
			}
			InvitationData feedContent = new InvitationData();

			try {
			
				if (!tempContentObject.isNull("message_group_id")) {
					feedContent.setMessage_group_id(tempContentObject.getString("message_group_id"));
				}
				
				
				if (!tempContentObject.isNull("author")) {
					feedContent.setAuthor(tempContentObject.getString("author"));
				}
				feedContent.setFlag(tempContentObject.getInt("flag"));
			
				feedContent.setCreate(tempContentObject.getLong("create") + "");
		
				if (!tempContentObject.isNull("id")) {
					feedContent.setId(tempContentObject.getString("id"));
				}

				if (!tempContentObject.isNull("pair_id")) {
					feedContent.setPair_id(tempContentObject.getString("pair_id"));
				}

				if (!tempContentObject.isNull("status")) {
					feedContent.setStatus(tempContentObject.getInt("status"));
				}
	
				if (!tempContentObject.isNull("type")) {
					feedContent.setType(tempContentObject.getString("type"));
				}
				if (!tempContentObject.isNull("name")) {
					feedContent.setName(tempContentObject.getString("name"));
				}
				if (!tempContentObject.isNull("is_new")) {
					feedContent.setIsNew(tempContentObject.getInt("is_new"));
				}
				if (!tempContentObject.isNull("update_time")) {
					feedContent.setUpdate_time(tempContentObject.getString("update_time"));
				}
			} catch (JSONException e) {
			}

			// userinfo部分
			JSONObject userObject = null;
			try {
				userObject = tempContentObject.getJSONObject("user_info");
			} catch (JSONException e) {
			}
			// if (null == userObject) {
			// continue;
			// }

			InvitationUserInfo feedInfo = new InvitationUserInfo();

			try {
				if (!userObject.isNull("husband_name")) {
					feedInfo.setHusbandName(userObject
							.getString("husband_name"));
				}
				if (!userObject.isNull("wife_name")) {
					feedInfo.setWifeName(userObject.getString("wife_name"));
				}
				if (!userObject.isNull("wife_avatar")) {
					feedInfo.setWifeUrl(userObject.getString("wife_avatar"));
				}
				if (!userObject.isNull("husband_avatar")) {
					feedInfo.setHusbandUrl(userObject
							.getString("husband_avatar"));
				}
			} catch (JSONException e) {
			}

			feedContent.setUserInfo(feedInfo);

			// post部分
			JSONObject postObject = null;
			try {
				postObject = tempContentObject.getJSONObject("post");
			} catch (JSONException e) {
			}
			if (null == postObject) {
				continue;
			}

			InvitationPost feedPost = new InvitationPost();
			// post的replay部分
			JSONArray replayArray = null;
			try {
				replayArray = postObject.getJSONArray("reply");
			} catch (JSONException e) {
			}
			if ((null != replayArray) && (replayArray.length() > 0)) {
				ArrayList<InvitationReply> feedReplays = new ArrayList<InvitationReply>();
				for (int j = 0; j < replayArray.length(); j++) {
					JSONObject replayObj = null;
					try {
						replayObj = replayArray.getJSONObject(j);
					} catch (JSONException e) {
					}
					if (null == replayObj) {
						continue;
					}
				
					InvitationReply tempReplay = new InvitationReply();

					try {
						if (!replayObj.isNull("name")) {
							tempReplay.setName(replayObj.getString("name"));
						}

						if (!replayObj.isNull("audio")) {
							tempReplay.setAudio(replayObj.getString("audio"));
						}

						if (!replayObj.isNull("reply_name")) {
							tempReplay.setReply_name(replayObj.getString("reply_name"));
						}
						if (!replayObj.isNull("audio_len")) {
							tempReplay.setAudio_len(replayObj.getInt("audio_len"));
						}
						

						if (!replayObj.isNull("author")) {
							tempReplay.setAuthor(replayObj.getString("author"));
						}

						if (!replayObj.isNull("extra")) {
							tempReplay.setExtra(replayObj.getString("extra"));
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

						if (!replayObj.isNull("status")) {
							tempReplay.setStatus(replayObj.getInt("status"));
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
				ArrayList<InvitationPhoto> photos = new ArrayList<InvitationPhoto>();
				for (int k = 0; k < photoArray.length(); k++) {
					JSONObject photoObj = null;
					try {
						photoObj = photoArray.getJSONObject(k);
					} catch (JSONException e) {
					}
					if (null == photoObj) {
						continue;
					}
					InvitationPhoto photo = new InvitationPhoto();
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

		nbInvitations.setData(list);

		return nbInvitations;
	}

	/**
	 * 密邻HOME帖子列表解析
	 * 
	 * @param offset
	 * @param obj
	 * @param ctx
	 * @return
	 */
	public synchronized CxNeighbourInvitationList getNbHomeInvitationList(
			int offset, JSONObject obj, Context ctx,String groupId) {
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
		CxNeighbourInvitationList nbInvitations = new CxNeighbourInvitationList();
		nbInvitations.setRc(rc);

		try {
			if (!obj.isNull("msg")) {
				nbInvitations.setMsg(obj.getString("msg"));
			}
		} catch (JSONException e) {
		}

		try {
			nbInvitations.setTs(obj.getInt("ts"));
		} catch (JSONException e) {
		}

		if (0 != rc) {
			return nbInvitations;
		}

		JSONObject data = null;
		try {
			data = obj.getJSONObject("data");
		} catch (JSONException e) {
		}
		if ((null == data) || (data.length() < 1)) {
			return nbInvitations;
		}
//		 //访问成功的情况需要存入数据库
//		if (0 == offset) { // 只保存第一屏
//			try {
//				RkNbCacheData cache = new RkNbCacheData(ctx);
//				cache.insertNbData(data.toString());
//			} catch (Exception e) {
//			}
//		}

		InvitationList list = new InvitationList();

		JSONObject userObj = null;
		try {
			userObj = data.getJSONObject("user_info");
		} catch (JSONException e) {
		}
		if (null == userObj) {
			return nbInvitations;
		}

		InvitationUserInfo userInfo = new InvitationUserInfo();

		try {
			if (!userObj.isNull("bg_url"))
				userInfo.setBgUrl(userObj.getString("bg_url"));

			if (!userObj.isNull("husband_name")) {
				userInfo.setHusbandName(userObj.getString("husband_name"));
			}
			if (!userObj.isNull("wife_name")) {
				userInfo.setWifeName(userObj.getString("wife_name"));
			}

			if (!userObj.isNull("wife_avatar")) {
				userInfo.setWifeUrl(userObj.getString("wife_avatar"));
			}
			if (!userObj.isNull("husband_avatar")) {
				userInfo.setHusbandUrl(userObj.getString("husband_avatar"));
			}
		} catch (JSONException e1) {
		}
		list.setUserInfo(userInfo);

		JSONArray feedArray = null;
		try {
			feedArray = data.getJSONArray("feeds");
		} catch (JSONException e) {
		}

		if ((null == feedArray) || (feedArray.length() < 1)) {
			nbInvitations.setData(list);
			return nbInvitations;
		}
		
		if (0 == offset) { // 只保存第一屏
			try {				
				 //访问成功的情况需要存入数据库	
				CxNbCacheData cache = new CxNbCacheData(ctx);
				cache.insertNbData(groupId,obj.toString());				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<InvitationData> feeds = new ArrayList<InvitationData>();
		for (int i = 0; i < feedArray.length(); i++) {
			JSONObject tempContentObject = null;
			try {
				tempContentObject = feedArray.getJSONObject(i);
			} catch (JSONException e) {
			}
			if (null == tempContentObject) {
				continue;
			}
			InvitationData feedContent = new InvitationData();

			try {
				if (!tempContentObject.isNull("author")) {
					feedContent
							.setAuthor(tempContentObject.getString("author"));
				}
			} catch (JSONException e) {
			}
			
			try {
				feedContent.setFlag(tempContentObject.getInt("flag"));
			} catch (JSONException e) {
			}

			try {

				feedContent.setCreate(tempContentObject.getLong("create") + "");

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
				if (!tempContentObject.isNull("status")) {
					feedContent.setStatus(tempContentObject.getInt("status"));
				}
			} catch (JSONException e) {
			}

			try {
				if (!tempContentObject.isNull("type")) {
					feedContent.setType(tempContentObject.getString("type"));
				}
			} catch (JSONException e) {
			}

			try {
				if (!tempContentObject.isNull("name")) {
					feedContent.setName(tempContentObject.getString("name"));
				}
			} catch (JSONException e) {
			}
			
			try {
				if (!tempContentObject.isNull("message_group_id")) {
					feedContent.setMessage_group_id(tempContentObject.getString("message_group_id"));
				}
			} catch (JSONException e) {
			}
			
			try {
				if (!tempContentObject.isNull("update_time")) {
					feedContent.setUpdate_time(tempContentObject.getString("update_time"));
				}
			} catch (JSONException e) {
			}

			
			 // userinfo部分
			 JSONObject userObject = null;
			 try {
				 if(!tempContentObject.isNull("user_info")){
					 userObject = tempContentObject.getJSONObject("user_info");
				 }
			 
			 } catch (JSONException e) {
			 }

			 InvitationUserInfo feedInfo = new InvitationUserInfo();
			 if (null != userObject) {	
				try {
					if (!userObject.isNull("husband_name")) {					
					  feedInfo.setHusbandName(userObject.getString("husband_name"));
					}
					if (!userObject.isNull("wife_name")) {
						feedInfo.setWifeName(userObject.getString("wife_name"));
					}
					if (!userObject.isNull("wife_avatar")) {
						 feedInfo.setWifeUrl(userObject.getString("wife_avatar"));
					}
					if (!userObject.isNull("husband_avatar")) {
						 feedInfo.setHusbandUrl(userObject.getString("husband_avatar"));
					}
				} catch (JSONException e) {
				}
				 
			}
			feedContent.setUserInfo(feedInfo);
			CxLog.i("RkNeighbourParser_men", feedContent.getUserInfo().getHusbandUrl());
		

			// post部分
			JSONObject postObject = null;
			try {
				postObject = tempContentObject.getJSONObject("post");
			} catch (JSONException e) {
			}
			if (null == postObject) {
				continue;
			}

			InvitationPost feedPost = new InvitationPost();
			// post的replay部分
			JSONArray replayArray = null;
			try {
				replayArray = postObject.getJSONArray("reply");
			} catch (JSONException e) {
			}
			if ((null != replayArray) && (replayArray.length() > 0)) {
				ArrayList<InvitationReply> feedReplays = new ArrayList<InvitationReply>();
				for (int j = 0; j < replayArray.length(); j++) {
					JSONObject replayObj = null;
					try {
						replayObj = replayArray.getJSONObject(j);
					} catch (JSONException e) {
					}
					if (null == replayObj) {
						continue;
					}
					InvitationReply tempReplay = new InvitationReply();

					try {
						if (!replayObj.isNull("name")) {
							tempReplay.setName(replayObj.getString("name"));
						}

						if (!replayObj.isNull("audio")) {
							tempReplay.setAudio(replayObj.getString("audio"));
						}

						if (!replayObj.isNull("reply_name")) {
							tempReplay.setReply_name(replayObj
									.getString("reply_name"));
						}

						if (!replayObj.isNull("audio_len")) {
							tempReplay.setAudio_len(replayObj.getInt("audio_len"));
						}

						if (!replayObj.isNull("author")) {
							tempReplay.setAuthor(replayObj.getString("author"));
						}

						if (!replayObj.isNull("extra")) {
							tempReplay.setExtra(replayObj.getString("extra"));
						}

						if (!replayObj.isNull("feed_id")) {
							tempReplay.setFeed_id(replayObj
									.getString("feed_id"));
						}

						if (!replayObj.isNull("reply_id")) {
							tempReplay.setReply_id(replayObj
									.getString("reply_id"));
						}

						if (!replayObj.isNull("reply_to")) {
							tempReplay.setReply_to(replayObj
									.getString("reply_to"));
						}

						if (!replayObj.isNull("status")) {
							tempReplay.setStatus(replayObj.getInt("status"));
						}

						if (!replayObj.isNull("text")) {
							tempReplay.setText(replayObj.getString("text"));
						}

						tempReplay.setTs(replayObj.getInt("ts"));

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
				ArrayList<InvitationPhoto> photos = new ArrayList<InvitationPhoto>();
				for (int k = 0; k < photoArray.length(); k++) {
					JSONObject photoObj = null;
					try {
						photoObj = photoArray.getJSONObject(k);
					} catch (JSONException e) {
					}
					if (null == photoObj) {
						continue;
					}
					InvitationPhoto photo = new InvitationPhoto();
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

		nbInvitations.setData(list);

		return nbInvitations;
	}

}
