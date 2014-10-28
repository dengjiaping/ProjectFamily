package com.chuxin.family.models;

import org.json.JSONException;

public class Partner extends Model {
	
	private static final String TAG_ICON = "icon_big";
	private static final String TAG_NAME = "name";
	
	private static final Partner sample = new Partner();
	public static Model getInstance() {
		return sample;
	}
	
	public Partner() {
		super();
		mTable = "partner";
		init();
	}

	public String getIcon() {
		try {
			return mData.getString(TAG_ICON);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getName() {
		try {
			return mData.getString(TAG_NAME);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
}
