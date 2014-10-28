package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

public class Neighbour extends Model{
	private static final String TAG_NEIGHBOUR_ID = "id";
	private static final String TAG_NEIGHBOUR_SHOW_ID = "show_id";
	private static final String TAG_WIFE_NAME = "wife_name";
	private static final String TAG_WIFE_AVATAR = "wife_avatar";
	private static final String TAG_HUSBAND_NAME = "husband_name";
	private static final String TAG_HUSBAND_AVATAR = "husband_avatar";
	private static final String TAG_STATUS = "status";
	private static final String TABLE_NAME = "neighbours";
	
	public Neighbour(){
		super();
		mTable = TABLE_NAME;
	}
	
	public Neighbour(JSONObject data, Context ctx){
		super();
		mTable = TABLE_NAME;
		mContext = ctx;
		if(null != data){
			mData = data;
			mId = getNeighbourId(); 
		} else {
			init();
		}
	}
	
	public String getNeighbourId() {
		try {
			return mData.getString(TAG_NEIGHBOUR_ID);
		} catch (JSONException e) {
			CxLog.e("getNeighbourId", ""+e.getMessage());
			assert (false);
		}
		return null;
	}
	
	public String getNeighbourShowId() {
		try {
			return mData.getString(TAG_NEIGHBOUR_SHOW_ID);
		} catch (JSONException e) {
			CxLog.e("getNeighbourShowId", ""+e.getMessage());
			assert (false);
		}
		return null;
	}
	
	public String getWifeName(){
		try {
			return mData.getString(TAG_WIFE_NAME);
		} catch (JSONException e) {
			CxLog.e("getWifeName", "" + e.getMessage());
			assert(false);
		}
		return null;
	}
	
	public String getWifeAvatar(){
		try {
			return mData.getString(TAG_WIFE_AVATAR);
		} catch (JSONException e) {
			CxLog.e("getWifeAvatar", "" + e.getMessage());
			assert(false);
		}
		return null;
	}
	
	public String getHusbandName(){
		try {
			return mData.getString(TAG_HUSBAND_NAME);
		} catch (JSONException e) {
			CxLog.e("getHusbandName", "" + e.getMessage());
			assert(false);
		}
		return null;
	}
	
	public String getHusbandAvatar(){
		try {
			return mData.getString(TAG_HUSBAND_AVATAR);
		} catch (JSONException e) {
			CxLog.e("getHusbandAvatar", "" + e.getMessage());
			assert(false);
		}
		return null;
	}
	
	public int getStatus(){
		try {
			return mData.getInt(TAG_STATUS);
		} catch (JSONException e) {
			CxLog.e("getStatus", "" + e.getMessage());
			assert(false);
		}
		return -1;
	}
}
