package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class SystemMessage extends TextMessage {

	private static final String TAG = "SystemMessage";
	private static final String REDIRECT = "redirect";
	private static final String BTN1_NAME = "btn1_name";
	private static final String BTN2_NAME = "btn2_name";
	private static final String TITLE = "title";
	private static final String ICON_TYPE = "icon_type";
	private static final String METHOD = "method";
	private static final String MODE = "mode";
	private static final String TEMPLATE = "template";
	private static final String ICON_URL = "icon_url";
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";
	private static final String BTN_NAME = "btn_name";
	private static final String BTN1_CONFIRM = "btn1_confirm";
	private static final String BTN2_CONFIRM = "btn2_confirm";

	public SystemMessage(JSONObject data, Context context) {
		super(data, context);
		mContext = context;
	}

	@Override
	public int getType() {
		return MESSAGE_TYPE_SYSTEM;
	}

	public int getSystemRedirect() {
		try {
			if(mData.has(REDIRECT)){
				return mData.getInt(REDIRECT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return 0;
	}
	
	public int getSystemIconType() {
		try {
			if(mData.has(ICON_TYPE)){
				return mData.getInt(ICON_TYPE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return 0;
	}
	
	public int getSystemMode() {
		try {
			if(mData.has(MODE)){
				return mData.getInt(MODE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return 0;
	}

	public String getSystemBtn1Name() {
		try {
			if(mData.has(BTN1_NAME)){
				return mData.getString(BTN1_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getSystemBtn2Name() {
		try {
			if(mData.has(BTN2_NAME)){
				return mData.getString(BTN2_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemTitle() {
		try {
			if(mData.has(TITLE)){
				return mData.getString(TITLE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemMethod() {
		try {
			if(mData.has(METHOD)){
				return mData.getString(METHOD);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemTemplate() {
		try {
			if(mData.has(TEMPLATE)){
				return mData.getString(TEMPLATE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemIconUrl() {
		try {
			if(mData.has(ICON_URL)){
				return mData.getString(ICON_URL);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemValue1() {
		try {
			if(mData.has(VALUE1)){
				return mData.getString(VALUE1);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemValue2() {
		try {
			if(mData.has(VALUE2)){
				return mData.getString(VALUE2);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getSystemBtnName() {
		try {
			if(mData.has(BTN_NAME)){
				return mData.getString(BTN_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
   public String getSystemBtn1Confirm() {
        try {
            if(mData.has(BTN1_CONFIRM)){
                return mData.getString(BTN1_CONFIRM);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            assert(false);
        }
        return null;
    }
   
    public String getSystemBtn2Confirm() {
        try {
            if(mData.has(BTN2_CONFIRM)){
                return mData.getString(BTN2_CONFIRM);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            assert(false);
        }
        return null;
    }
}
