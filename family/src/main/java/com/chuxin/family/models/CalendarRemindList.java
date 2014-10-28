package com.chuxin.family.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

/***
 * 
 * @author shichao
 *
 */
public class CalendarRemindList extends CalendarDataObj{
    
    private static final String TAG_UPDATE_TIME = "update_time"; // #日历项更新时间
    private static final String TAG_REMIND = "remind"; // remind array list
	private static final String TABLE_NAME = "calendar_remind_list";
	
	public CalendarRemindList(){
		super();
		//new CalendarDataObj();
		mTable = TABLE_NAME;
	}
	
	public CalendarRemindList(JSONObject data, Context ctx){
		super();
		//new CalendarDataObj(data, ctx);
		mTable = TABLE_NAME;
		mContext = ctx;
		mId = "10001";
        if (data != null) {
            mData = data;
        } else {
            init();
        }
	}
	
   public int getUpdateTime() {
        try {
            if(mData.has(TAG_UPDATE_TIME)){
                return mData.getInt(TAG_UPDATE_TIME);
            }
        } catch (JSONException e) {
            CxLog.e("getChargeMonthDetailDay", ""+e.getMessage());
            assert (false);
        }
        return 0;
    }
   public JSONArray getRemindArray(){
       try {
           if(mData.has(TAG_REMIND)){
               return mData.getJSONArray(TAG_REMIND);
           }
       } catch (JSONException e) {
           CxLog.e("getRemindArray", ""+e.getMessage());
           assert (false);
       }
       return null;
   }
}
