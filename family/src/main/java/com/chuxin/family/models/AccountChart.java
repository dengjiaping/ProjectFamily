package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

public class AccountChart extends Model{
	public static final String TAG_TYPE = "type";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_MONEY = "money";
	private static final String TABLE_NAME = "accountchart";
	
	public AccountChart(){
		super();
		mTable = TABLE_NAME;
	}
	
	public AccountChart(JSONObject data, Context ctx){
		super();
		mTable = TABLE_NAME;
		mContext = ctx;
		if(null != data){
			mData = data;
		} else {
			init();
		}
	}
	
	public int getAccountType() {
		try {
		    if(mData.has(TAG_TYPE)){
		        return mData.getInt(TAG_TYPE);
		    }
		} catch (JSONException e) {
			CxLog.e("getaccounttype", ""+e.getMessage());
			assert (false);
		}
		return 0;
	}
	public int getAccountCategory(){
		try {
		    if(mData.has(TAG_CATEGORY)){
		        return mData.getInt(TAG_CATEGORY);
		    }
		} catch (JSONException e) {
			CxLog.e("getaccountcategory", "" + e.getMessage());
			assert(false);
		}
		return 0;
	}
	
	public String getAccountMoney(){
		try {
		    if(mData.has(TAG_MONEY)){
		        return mData.getString(TAG_MONEY);
		    }
		} catch (JSONException e) {
			CxLog.e("getWifeAvatar", "" + e.getMessage());
			assert(false);
		}
		return null;
	}
}
