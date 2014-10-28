package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class EmotionMessage extends Message {
	private static final String EMOTION_ID = "emotion";
	private static final String CATEGORY_ID = "category_id";
	private static final String IMAGE_ID = "image_id";
	public EmotionMessage(JSONObject data, Context context) {
		super(data, context);
		mContext = context;
	}

	@Override
	public int getType() {
		return MESSAGE_TYPE_EMOTION;
	}
	
	public int getCategoryId(){
		try {
			return mData.getInt(CATEGORY_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public int getImageId(){
		try {
			return mData.getInt(IMAGE_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	

	public String getEmotion() {
		try {
			return mData.getString(EMOTION_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
}
