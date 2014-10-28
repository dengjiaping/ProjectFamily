
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
 */
public class CalendarCycleList extends CalendarDataObj {

    private static final String TAG_UPDATE_TIME = "update_time"; // #日历项更新时间

    private static final String TAG_CYCLE_REMIND = "cycle"; // remind array list

    private static final String TABLE_NAME = "calendar_cycle_list";

    private JSONArray mRemindArray;

    public CalendarCycleList() {
        super();
        //new CalendarDataObj();
        mTable = TABLE_NAME;
    }

    public CalendarCycleList(JSONObject data, Context ctx) {
        super();
        CxLog.i("CalendarCycleList", "context="+ctx);
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
            if (mData.has(TAG_UPDATE_TIME)) {
                return mData.getInt(TAG_UPDATE_TIME);
            }
        } catch (JSONException e) {
            CxLog.e("getChargeMonthDetailDay", "" + e.getMessage());
            assert (false);
        }
        return 0;
    }

    public JSONArray getCycleRemindArray() {
        try {
            if (mData.has(TAG_CYCLE_REMIND)) {
                return mData.getJSONArray(TAG_CYCLE_REMIND);
            }
        } catch (JSONException e) {
            CxLog.e("getRemindArray", "" + e.getMessage());
            assert (false);
        }
        return null;
    }

}
