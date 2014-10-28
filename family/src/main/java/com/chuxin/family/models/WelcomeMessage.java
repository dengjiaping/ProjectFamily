package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class WelcomeMessage extends Message {
	
	private static final String TAG_TEXT = "text";
	
	public WelcomeMessage(JSONObject data, Context context) {
		super(data, context);
		mData = data;
		mContext = context;
	}
	
	@Override
	public int getType() {
		return MESSAGE_TYPE_WELCOME;
	}

	public String getText() {
		try {
			return mData.getString(TAG_TEXT);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
}
