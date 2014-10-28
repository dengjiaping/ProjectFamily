package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.utils.CxLog;

import android.content.Context;

/***
 * 记账明细年数据存储
 * @author shichao
 *
 */
public class ChargeYearDetail extends Model{
    
    private static final String TAG_OUT = "out"; // 本年支出
    private static final String TAG_IN = "in"; // 本年收入
    private static final String TAG_SURPLUS = "surplus"; // 本年结余
    private static final String TAG_MONTH = "month"; // 每月记录
    
    private static final String TAG_MONTH_MONTH = "month";   // 月份
	private static final String TAG_MONTH_IN = "in";  // 月收入
	private static final String TAG_MONTH_OUT = "out"; // 月支出
	private static final String TAG_MONTH_SURPLUS = "surplus"; // 月结余
	
	private static final String TABLE_NAME = "chargeyeardetail";
	private JSONObject mMothDetail;
	
	public ChargeYearDetail(){
		super();
		mMothDetail = null;
		mTable = TABLE_NAME;
	}
	
	public ChargeYearDetail(JSONObject data, String id, Context ctx){
		super();
		mMothDetail = null;
		mTable = TABLE_NAME;
		mId = id;
		mContext = ctx;
		if(null != data){
			mData = data;
		} else {
			init();
		}
	}
	
   public String getChargeYearDetailOut() {
        try {
            if(mData.has(TAG_OUT)){
                return mData.getString(TAG_OUT);
            }
        } catch (JSONException e) {
            CxLog.e("getChargeYearDetailOut", ""+e.getMessage());
            assert (false);
        }
        return null;
    }
   public String getChargeYearDetailIn() {
       try {
           if(mData.has(TAG_IN)){
               return mData.getString(TAG_IN);
           }
       } catch (JSONException e) {
           CxLog.e("getChargeYearDetailIn", ""+e.getMessage());
           assert (false);
       }
       return null;
   }
   public String getChargeYearDetailSurplus() {
       try {
           if(mData.has(TAG_SURPLUS)){
               return mData.getString(TAG_SURPLUS);
           }
       } catch (JSONException e) {
           CxLog.e("getChargeYearDetailSurplus", ""+e.getMessage());
           assert (false);
       }
       return null;
   }

   public String getChargeYearDetailMonthMonth() {
       try {
           if(mData.has(TAG_MONTH)){
               if(null == mMothDetail){
                   mMothDetail = mData.getJSONObject(TAG_MONTH);
               }
               if(mMothDetail.has(TAG_MONTH_MONTH)){
                   return mMothDetail.getString(TAG_MONTH_MONTH);
               }
           }
       } catch (JSONException e) {
           CxLog.e("getChargeYearDetailMonthMonth", ""+e.getMessage());
           assert (false);
       }
       return null;
   }
	
	
	public String getChargeYearDetailMonthIn() {
	       try {
	           if(mData.has(TAG_MONTH)){
	               if(null == mMothDetail){
	                   mMothDetail = mData.getJSONObject(TAG_MONTH);
	               }
	               if(mMothDetail.has(TAG_MONTH_IN)){
	                   return mMothDetail.getString(TAG_MONTH_IN);
	               }
	           }
	       } catch (JSONException e) {
	           CxLog.e("getChargeYearDetailMonthIn", ""+e.getMessage());
	           assert (false);
	       }
	       return null;
	}
	public String getChargeYearDetailMonthOut(){
        try {
            if(mData.has(TAG_MONTH)){
                if(null == mMothDetail){
                    mMothDetail = mData.getJSONObject(TAG_MONTH);
                }
                if(mMothDetail.has(TAG_MONTH_OUT)){
                    return mMothDetail.getString(TAG_MONTH_OUT);
                }
            }
        } catch (JSONException e) {
            CxLog.e("getChargeYearDetailMonthCategory", ""+e.getMessage());
            assert (false);
        }
        return null;
	}
	
	public String getChargeYearDetailMonthSurplus(){
	    try {
            if(mData.has(TAG_MONTH)){
                if(null == mMothDetail){
                    mMothDetail = mData.getJSONObject(TAG_MONTH);
                }
                if(mMothDetail.has(TAG_MONTH_SURPLUS)){
                    return mMothDetail.getString(TAG_MONTH_SURPLUS);
                }
            }
        } catch (JSONException e) {
            CxLog.e("getChargeYearDetailMonthSurplus", ""+e.getMessage());
            assert (false);
        }
        return null;
	}
}
