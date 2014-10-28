package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabloidMessage extends Message{
	
	private static final String TAG_TEXT = "content";
	
	public TabloidMessage(JSONObject data, Context context){
		super(data, context);
		mData = data;
		mContext = context;
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
	
	
	@Override
	public int getType() {
		return MESSAGE_TYPE_TABLOID;
	}
	
	
	
}
