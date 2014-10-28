package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class LocationMessage extends TextMessage {

	private static final String TAG_LON = "lon";
	private static final String TAG_LAT = "lat";

	public LocationMessage(JSONObject data, Context context) {
		super(data, context);
		mContext = context;
	}

	@Override
	public int getType() {
		return MESSAGE_TYPE_LOCATION;
	}

	public float getLon() {
		try {
			return (float) mData.getDouble(TAG_LON);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return 0.0f;
	}

	public float getLat() {
		try {
			return (float) mData.getDouble(TAG_LAT);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return 0.0f;
	}


	public String getDescription() {
		return getText();
	}
	
}
