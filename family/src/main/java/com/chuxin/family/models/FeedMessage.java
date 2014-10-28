package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FeedMessage extends TextMessage {

	private static final String FEED_TYPE = "feed_type";
	private static final String FEED_ID = "feed_id";

	public FeedMessage(JSONObject data, Context context) {
		super(data, context);
		mContext = context;
	}

	@Override
	public int getType() {
		return MESSAGE_TYPE_FEED;
	}

	public String getFeedId() {
		try {
			return mData.getString(FEED_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getFeedType() {
		try {
			return mData.getString(FEED_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getFeedCategory() {
		String type = getFeedType();
		if ("reminder".equals(type)) {
			return "reminder";
		} if("me".equals(type)){
		    return "settings"; 
		} if("ta".equals(type)){
		    return "rkmate";
		} else {
			return "space";
		}
	}	
}
