package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class VoiceMessage extends Message {

	private static final String TAG_VOICE = "audio";
	private static final String TAG_VOICE_URL = "url";
	private static final String TAG_VOICE_LEN = "len";
	private static final String TAG_VOICE_TYPE = "type";
	private static final String TAG_IS_READ = "is_read";

	private JSONObject mVoice;

	public VoiceMessage(JSONObject data, Context context) {
		super(data, context);
		mVoice = null;
		mContext = context;
	}

	@Override
	public int getType() {
		return MESSAGE_TYPE_VOICE;
	}

	public String getVoiceUrl() {
		try {
			if (mVoice == null) {
				mVoice = mData.getJSONObject(TAG_VOICE);
			}
			return mVoice.getString(TAG_VOICE_URL);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return null;
	}

	public int getVoiceLen() {
		try {
			if (mVoice == null) {
				mVoice = mData.getJSONObject(TAG_VOICE);
			}
			return mVoice.getInt(TAG_VOICE_LEN);
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return -1;
	}
	
	public boolean getIsRead(){
		try {
			if(mData!=null){
				return mData.getBoolean(TAG_IS_READ);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert (false);
		}
		return false;
	}
	
	public int getAudioType(){
	    try {
            if (mVoice == null) {
                mVoice = mData.getJSONObject(TAG_VOICE);
            }
            return mVoice.getInt(TAG_VOICE_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            assert (false);
        }
        return -1;
	}
}
