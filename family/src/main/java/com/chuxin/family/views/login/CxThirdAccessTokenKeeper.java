package com.chuxin.family.views.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class CxThirdAccessTokenKeeper {
	private static final String PREFERENCES_NAME = "user_profile";
	private static final String PLATFORM_EXPIRES_TIME = "expiresTime";
	private static final String PLATFORM_TOKEN = "token";
	private static final String PLATFORM_UID = "userIdentify"; 
	private static final String PLATFORM = "platName";
	/**
	 * 保存accesstoken到SharedPreferences
	 * @param context Activity 上下文环境
	 * @param token RkThirdAccessToken
	 */
	public static void keepAccessToken(Context context, CxThirdAccessToken token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(PLATFORM_TOKEN, token.getToken());
		editor.putString(PLATFORM_EXPIRES_TIME, token.getExpiresTime());
		editor.putString(PLATFORM_UID, token.getUid());
		editor.putString(PLATFORM, token.getPlatName());
		editor.commit();
	}
	
	/**
	 * 清空sharepreference
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * @param context
	 * @return RkThirdAccessToken
	 */
	public static CxThirdAccessToken readAccessToken(Context context){
		CxThirdAccessToken token = new CxThirdAccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setToken(pref.getString(PLATFORM_TOKEN, null));
		token.setExpiresTime(pref.getString(PLATFORM_EXPIRES_TIME, null));
		token.setUid(pref.getString(PLATFORM_UID, null));
		token.setPlatName(pref.getString(PLATFORM, null));
		return token;
	}
	
	public static boolean isEmpty(CxThirdAccessToken checkToken){
		if ( (null == checkToken) || (TextUtils.isEmpty(checkToken.getPlatName())) 
				|| (TextUtils.isEmpty(checkToken.getToken()) 
						|| (TextUtils.isEmpty(checkToken.getUid())) ) ) {
			return true;
		}
		
		return false;
	}
	
}
