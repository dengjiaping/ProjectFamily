package com.chuxin.family.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

/***
 * 记账明细月数据存储
 * 
 * @author shichao
 * 
 */
public class CalendarMemoryDay extends Model {

	private static final String TAG_TIME_DOWM = "time_down"; // #倒计时
	private static final String TAG_TIME_UP = "time_up"; // #正计时

	private static final String TABLE_NAME = "calendar_memorial_list";

	private JSONArray mTimeDownArray;
	private JSONArray mTimeUpArray;

	public CalendarMemoryDay() {
		super();
		// new CalendarDataObj();
		mTable = TABLE_NAME;
	}

	public CalendarMemoryDay(JSONObject data, String id, Context ctx) {
		super();
		// new CalendarDataObj(data, ctx);
		mTable = TABLE_NAME;
		mContext = ctx;
		mId = "10000";
		if (data != null) {
			mData = data;
		} else {
			init();
		}
	}

	public JSONArray getTimeDownArray() {
		try {
			if (mData.has(TAG_TIME_DOWM)) {
				return mData.getJSONArray(TAG_TIME_DOWM);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getTimeUpArray() {
		try {
			if (mData.has(TAG_TIME_UP)) {
				return mData.getJSONArray(TAG_TIME_UP);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
