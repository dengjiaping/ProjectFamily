package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

/***
 * 记账首页数据存储
 * @author shichao
 *
 */
public class ChargeHome extends Model{
    
    private static final String TAG_MONTH_OUT = "month_out"; // 本月支出
    private static final String TAG_MONTH_IN = "month_in"; // 本月收入
    private static final String TAG_MONTH_SURPLUS = "month_surplus"; // 本月结余
    private static final String TAG_YEAR_SURPLUS = "year_surplus"; // 本年结余
    private static final String TAG_LAST_RECORD = "last_record"; // 最新记录
    
    private static final String TAG_DATE = "date";   // 日期
	private static final String TAG_TYPE = "type";  // 类型 1支出 2收入
	private static final String TAG_CATEGORY = "category"; // 类别 100 居家物业
	private static final String TAG_MONEY = "money"; // 金额
	
	private static final String TABLE_NAME = "chargehome";
	private JSONObject mLastRecord;
	
	public ChargeHome(){
		super();
		mLastRecord = null;
		mTable = TABLE_NAME;
	}
	
	public ChargeHome(JSONObject data, Context ctx){
		super();
		mLastRecord = null;
		mTable = TABLE_NAME;
		mContext = ctx;
		if(null != data){
			mData = data;
		} else {
			init();
		}
	}
	
   public String getChargeHomeMonthOut() {
        try {
            if(mData.has(TAG_MONTH_OUT)){
                return mData.getString(TAG_MONTH_OUT);
            }
        } catch (JSONException e) {
            CxLog.e("getChargeHomeMonthOut", ""+e.getMessage());
            assert (false);
        }
        return null;
    }
   public String getChargeHomeMonthIn() {
       try {
           if(mData.has(TAG_MONTH_IN)){
               return mData.getString(TAG_MONTH_IN);
           }
       } catch (JSONException e) {
           CxLog.e("getChargeHomeMonthIn", ""+e.getMessage());
           assert (false);
       }
       return null;
   }
   public String getChargeHomeMonthSurplus() {
       try {
           if(mData.has(TAG_MONTH_SURPLUS)){
               return mData.getString(TAG_MONTH_SURPLUS);
           }
       } catch (JSONException e) {
           CxLog.e("getChargeHomeMonthSurplus", ""+e.getMessage());
           assert (false);
       }
       return null;
   }
   public String getChargeHomeYearSurplus() {
       try {
           if(mData.has(TAG_YEAR_SURPLUS)){
               return mData.getString(TAG_YEAR_SURPLUS);
           }
       } catch (JSONException e) {
           CxLog.e("getChargeHomeMonthSurplus", ""+e.getMessage());
           assert (false);
       }
       return null;
   }

   public String getChargeHomeLastRecordDate() {
       try {
           if(mData.has(TAG_LAST_RECORD)){
               if(null == mLastRecord){
                   mLastRecord = mData.getJSONObject(TAG_LAST_RECORD);
               }
               if(mLastRecord.has(TAG_DATE)){
                   return mLastRecord.getString(TAG_DATE);
               }
           }
       } catch (JSONException e) {
           CxLog.e("getChargeHomeLastRecordDate", ""+e.getMessage());
           assert (false);
       }
       return null;
   }
	
	
	public String getChargeHomeLastRecordType() {
	       try {
	           if(mData.has(TAG_LAST_RECORD)){
	               if(null == mLastRecord){
	                   mLastRecord = mData.getJSONObject(TAG_LAST_RECORD);
	               }
	               if(mLastRecord.has(TAG_TYPE)){
	                   return mLastRecord.getString(TAG_TYPE);
	               }
	           }
	       } catch (JSONException e) {
	           CxLog.e("getChargeHomeLastRecordType", ""+e.getMessage());
	           assert (false);
	       }
	       return null;
	}
	public String getChargeHomeLastRecordCategory(){
        try {
            if(mData.has(TAG_LAST_RECORD)){
                if(null == mLastRecord){
                    mLastRecord = mData.getJSONObject(TAG_LAST_RECORD);
                }
                if(mLastRecord.has(TAG_CATEGORY)){
                    return mLastRecord.getString(TAG_CATEGORY);
                }
            }
        } catch (JSONException e) {
            CxLog.e("getChargeHomeLastRecordCategory", ""+e.getMessage());
            assert (false);
        }
        return null;
	}
	
	public String getChargeHomeLastRecordMoney(){
	    try {
            if(mData.has(TAG_LAST_RECORD)){
                if(null == mLastRecord){
                    mLastRecord = mData.getJSONObject(TAG_LAST_RECORD);
                }
                if(mLastRecord.has(TAG_MONEY)){
                    return mLastRecord.getString(TAG_MONEY);
                }
            }
        } catch (JSONException e) {
            CxLog.e("getChargeHomeLastRecordMoney", ""+e.getMessage());
            assert (false);
        }
        return null;
	}
}
