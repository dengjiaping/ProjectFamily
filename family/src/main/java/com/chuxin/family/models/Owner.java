package com.chuxin.family.models;

import org.json.JSONException;

public class Owner extends Model {
	
	private static final String TAG_CHAT_BG = "chat_bg";
	private static final String TAG_SPACE_BG = "bg_big";
	private static final String TAG_ICON = "icon_big";
	private static final String TAG_NAME = "name";
	private static final String TAG_PAIR_ID = "pair_id";
	private static final String TAG_PARTNER_ID = "partner_id";
	private static final String TAG_ICON_SMALL = "icon_small";
	
	private static final Owner sample = new Owner();
	public static Model getInstance() {
		return sample;
	}

	public Owner() {
		super();
		mTable = "owner";
		init();
	}
	
	public String getChatBg() {
		try {
			return mData.getString(TAG_CHAT_BG);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getSpaceBg() {
		try {
			return mData.getString(TAG_SPACE_BG);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
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

	public String getPairId() {
		try {
			return mData.getString(TAG_PAIR_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getPartnerId() {
		try {
			return mData.getString(TAG_PARTNER_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	public String getIconSmall() {
		try {
			return mData.getString(TAG_ICON_SMALL);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	

}
