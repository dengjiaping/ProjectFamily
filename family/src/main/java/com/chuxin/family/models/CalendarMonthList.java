package com.chuxin.family.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

/***
 * 记账明细月数据存储
 * @author shichao
 *
 */
public class CalendarMonthList extends CalendarDataObj{
    
	
	private static final String TABLE_NAME = "calendar_month_list";
	private static final String TAG_DATA="data";
	
	public CalendarMonthList(){
		super();
		//new CalendarDataObj();
		mTable = TABLE_NAME;
	}
	
	public CalendarMonthList(JSONObject data, String id,Context ctx){
		super();
		//new CalendarDataObj(data, id, ctx);
		mTable = TABLE_NAME;
		mContext = ctx;
        if (data != null) {
            mData = data;
            mId = id;
        } else {
            init();
        }
	}
	
	  public JSONArray getMonthRemindArray() {
	        try {
	            if (mData.has(TAG_DATA)) {
	                return mData.getJSONArray(TAG_DATA);
	            }
	        } catch (JSONException e) {
	            CxLog.e("getRemindArray", "" + e.getMessage());
	            assert (false);
	        }
	        return null;
	    }
	
	
}
