package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FaceMessage extends Message {
	private static final String FACE_ID = "face";
	public FaceMessage(JSONObject data, Context context) {
		super(data, context);
		mContext = context;
	}

	@Override
	public int getType() {
		return MESSAGE_TYPE_FACE;
	}

	public String getFace() {
		try {
			return mData.getString(FACE_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
}
