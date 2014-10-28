package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 抽鞭子和弹脑壳
 * @author dujy
 *
 */
public class AnimationMessage extends Message{
	
	private static final String TAG_TEXT = "text";

	public AnimationMessage(JSONObject data, Context context) {
		super(data, context);
		mData = data;
		mContext = context;
	}
	
	@Override
	public int getType() {
		return MESSAGE_TYPE_ANIMATION;
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
