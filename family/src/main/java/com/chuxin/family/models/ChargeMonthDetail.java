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
public class ChargeMonthDetail extends Model{
    
    private static final String TAG_DAY = "day"; // 日期
    private static final String TAG_RECORD = "record"; // 月记录

    
    private static final String TAG_ID = "id";   // 月份
	private static final String TAG_TYPE = "type";  // 月收入
	private static final String TAG_MONEY = "money"; // 月支出
	private static final String TAG_CATEGORY = "category"; // 月结余
	private static final String TAG_FROM = "from"; // 月结余
	private static final String TAG_DESC = "desc"; // 月结余
	
	private static final String TABLE_NAME = "chargemonthdetail";
	private JSONArray mMothDetailArray;
	private JSONObject mMonthDetailJsonObj;
	
	public ChargeMonthDetail(){
		super();
		mMonthDetailJsonObj = null;
		mTable = TABLE_NAME;
	}
	
	public ChargeMonthDetail(JSONObject data, String id,Context ctx){
		super();
		mMonthDetailJsonObj = null;
		mId = id;
		mTable = TABLE_NAME;
		mContext = ctx;
		if(null != data){
			mData = data;
		} else {
			init();
		}
	}
	
   public String getChargeMonthDetailDay() {
        try {
            if(mData.has(TAG_DAY)){
                return mData.getString(TAG_DAY);
            }
        } catch (JSONException e) {
            CxLog.e("getChargeMonthDetailDay", ""+e.getMessage());
            assert (false);
        }
        return null;
    }
}
