package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FastPhraseMessage extends Message {
	private static final String PHRASE_ID = "phrase";
	
	public FastPhraseMessage(JSONObject data, Context context) {
		super(data, context);
		mContext = context;
	}
	
	@Override
	public int getType() {
		return MESSAGE_TYPE_PHRASE;
	}

	public String getPhrase() {
		try {
			return mData.getString(PHRASE_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public int getPhraseId() {
		return 0;
	}
	
	public String getPhraseDesc() {
		return "";
	}
}