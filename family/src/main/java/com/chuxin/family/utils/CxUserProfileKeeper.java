package com.chuxin.family.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.androidpush.sdk.push.RKPush;

public class CxUserProfileKeeper {
	//*********************用户自己的资料**************************

	private final String PROFILE_FIELD_ICON_BIG = "icon_big";//头像
	private final String PROFILE_FIELD_ICON_MID = "icon_mid";
	private final String PROFILE_FIELD_ICON_SMALL = "icon_small";
	private final String PROFILE_FIELD_CHAT_BG_BIG = "chat_bg_big";//聊天背景
	private final String PROFILE_FIELD_CHAT_BG_SMALL = "chat_bg_small";
	private final String PROFILE_FIELD_ZONE_BG = "zone_bg";//空间背景
	private final String PROFILE_FIELD_FAMILY_BIG = "family_big";//家庭背景
	
	private final String PROFILE_FIELD_MATE_ID = "mate_id";
	private final String PROFILE_FIELD_PAIR = "pair";
	private final String PROFILE_FIELD_PAIR_ID = "pair_id";
	private final String PROFILE_FIELD_USER_ID = "user_id";
	private final String PROFILE_FILE_NAME = "profile_name";
	private final String PROFILE_FIELD_GENDER = "gender";
	private final String PROFILE_FIELD_GROUP_SHOW_ID = "group_show_id";

	private final String PROFILE_FIELD_TOGETHER_DAY = "together_day";
	private final String PROFILE_FIELD_PUSH_SOUND = "push_sound";

	private final String PROFILE_FIELD_SINGLE_MODE = "single_mode";
	private final String PROFILE_FIELD_VERSION_TYPE = "version_type";
	
	//*********************对方的资料**************************
	private final String PROFILE_FIELD_MATE_ICON = "mate_icon";
	private final String PROFILE_FIELD_MATE_NAME = "mate_name";
	

	//获取用户自己的资料成功后调用此代码
	public void saveProfile(final CxUserProfileDataField profile, Context ctx){
		if (null == profile) {
			return;
		}
		
		SharedPreferences sp = ctx.getSharedPreferences(PROFILE_FILE_NAME, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		CxGlobalParams global = CxGlobalParams.getInstance();

		global.setIconBig(profile.getIcon_big());
		edit.putString(PROFILE_FIELD_ICON_BIG, profile.getIcon_big());
		global.setIconMid(profile.getIcon_mid());
		edit.putString(PROFILE_FIELD_ICON_MID, profile.getIcon_mid());
		global.setIconSmall(profile.getIcon_small());
		edit.putString(PROFILE_FIELD_ICON_SMALL, profile.getIcon_small());
		global.setChatBackgroundBig(profile.getChat_big());
		edit.putString(PROFILE_FIELD_CHAT_BG_BIG, profile.getChat_big());
		global.setChatBackgroundSmall(profile.getChat_small());
		edit.putString(PROFILE_FIELD_CHAT_BG_SMALL, profile.getChat_small());
		global.setTogetherDayStr(profile.getTogetherDay());	
		global.setFamily_big(profile.getFamily_big());
		edit.putString(PROFILE_FIELD_FAMILY_BIG, profile.getFamily_big());
		global.setUserId(profile.getUid());
		edit.putString(PROFILE_FIELD_USER_ID, profile.getUid());
		global.setVersion(profile.getGender());
		edit.putInt(PROFILE_FIELD_GENDER, profile.getGender());
		global.setTogetherDayStr(profile.getTogetherDay());
		edit.putString(PROFILE_FIELD_TOGETHER_DAY, profile.getTogetherDay());
		String pushSoundStr = profile.getPush_sound();
		edit.putString(PROFILE_FIELD_PUSH_SOUND, pushSoundStr);
		
		global.setSingle_mode(profile.getSingle_mode());
		edit.putInt(PROFILE_FIELD_SINGLE_MODE, profile.getSingle_mode());
		
		global.setVersion_type(profile.getVersion_type());
		edit.putInt(PROFILE_FIELD_VERSION_TYPE, profile.getVersion_type());
		
//		global.setPartnerName(profile.getName());
		global.setIsLogin(true);
		CxLog.i("getPartner_id", profile.getPartner_id());
		
		String tempMateId = profile.getPartner_id();
		CxLog.i("tempMateId", tempMateId+", boolean:"+TextUtils.isEmpty(tempMateId)
				+", equal:"+(null == tempMateId)+",null String:"+("null".equals(tempMateId)));
		edit.putString(PROFILE_FIELD_MATE_ID, tempMateId);
		
		if (TextUtils.isEmpty(tempMateId/*profile.getPartner_id()*/)) { //未结对
			global.setPair(0); //0表示未结对(1表示结对)
			edit.putInt(PROFILE_FIELD_PAIR, 0);
		}else{
			global.setPair(1); //1表示结对(0表示未结对)
			edit.putInt(PROFILE_FIELD_PAIR, 1);
			global.setPartnerId(profile.getPartner_id()); //对方UID
			global.setPairId(profile.getPair_id()); //结对号
			edit.putString(PROFILE_FIELD_PAIR_ID, profile.getPair_id());
			global.setZoneBackground(profile.getBg_big()); //二人空间的背景图
			edit.putString(PROFILE_FIELD_ZONE_BG, profile.getBg_big());
			//只有结对的情况才获取对方的资料
			
			//如果结对且伴侣UID不为空，就要同时开启线程去获取伴侣资料
//			UserApi.getInstance().getUserPartnerProfile(userMateProfileCallback);
			global.setGroup_show_id(profile.getGroup_show_id());
			edit.putString(PROFILE_FIELD_GROUP_SHOW_ID, profile.getGroup_show_id());
			
		}
		
		edit.commit();
		String tempSound = pushSoundStr;
		if (null != tempSound) {
			if (tempSound.equalsIgnoreCase("w_rk_naughty_push.caf")  ||  tempSound.equalsIgnoreCase("h_rk_naughty_push.caf")) {
				global.setPushSoundType(0);
				if(tempSound.equalsIgnoreCase("w_rk_naughty_push.caf")){
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_first_s");
				}else{
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_first");
				}
				
			}else if(tempSound.equalsIgnoreCase("w_rk_fa_role_push.caf") || tempSound.equalsIgnoreCase("h_rk_fa_role_push.caf")){
				global.setPushSoundType(1);
				
				if(tempSound.equalsIgnoreCase("w_rk_fa_role_push.caf")){
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_s");
				}else{
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push");
				}
			}else{
				global.setPushSoundType(2);
				RKPush.S_NOTIFY_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
		}else{
			global.setPushSoundType(1);
			if(profile.getGender()==0){
				RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
						+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_s");
			}else{
				RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
						+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push");
			}
			
		}
		
//		global.setAppStatus(true);
		global.setAppNormal(profile.getUid());
		
	}
	
	//脱网的时候调此段代码(个人资料和对方的名称、头像）
	public void readProfile(Context ctx){
		SharedPreferences sp = ctx.getSharedPreferences(PROFILE_FILE_NAME, Context.MODE_PRIVATE);
		
		CxGlobalParams global = CxGlobalParams.getInstance();
		
		
		global.setIconBig(sp.getString(PROFILE_FIELD_ICON_BIG, null));
		
		global.setIconMid(sp.getString(PROFILE_FIELD_ICON_MID, null));
		
		global.setIconSmall(sp.getString(PROFILE_FIELD_ICON_SMALL, null));
		
		global.setChatBackgroundBig(sp.getString(PROFILE_FIELD_CHAT_BG_BIG, null));
		
		global.setChatBackgroundSmall(sp.getString(PROFILE_FIELD_CHAT_BG_SMALL, null));
		
		global.setFamily_big(sp.getString(PROFILE_FIELD_FAMILY_BIG, null));
		
		global.setUserId(sp.getString(PROFILE_FIELD_USER_ID, null));
		
		global.setVersion(sp.getInt(PROFILE_FIELD_GENDER, -1));
		
		String tempMateId = sp.getString(PROFILE_FIELD_MATE_ID, null);
		
		global.setPartnerName(sp.getString(PROFILE_FIELD_MATE_NAME, null));

		global.setPartnerIconBig(sp.getString(PROFILE_FIELD_MATE_ICON, null));	
		
		global.setTogetherDayStr(sp.getString(PROFILE_FIELD_TOGETHER_DAY, null));
		
		global.setAppNormal(sp.getString(PROFILE_FIELD_USER_ID, null));
		
		global.setGroup_show_id(sp.getString(PROFILE_FIELD_GROUP_SHOW_ID, null));
		
		global.setSingle_mode(sp.getInt(PROFILE_FIELD_SINGLE_MODE, 0));
		
		global.setVersion_type(sp.getInt(PROFILE_FIELD_VERSION_TYPE, 0));
		
		global.setIsLogin(false); //这种情况是走脱网，用户未登录
		
		
		if (TextUtils.isEmpty(tempMateId/*profile.getPartner_id()*/)) { //未结对
			global.setPair(0); //0表示未结对(1表示结对)
		}else{
			global.setPair(1); //1表示结对(0表示未结对)
			global.setPartnerId(tempMateId); //对方UID
			global.setPairId(sp.getString(PROFILE_FIELD_PAIR_ID, null)); //结对号
			
			global.setZoneBackground(sp.getString(PROFILE_FIELD_ZONE_BG, null)); //二人空间的背景图
			
		}
		
		
		String tempSound = sp.getString(PROFILE_FIELD_PUSH_SOUND, null);
		if (null != tempSound) {
			if (tempSound.equalsIgnoreCase("w_rk_naughty_push.caf")  ||  tempSound.equalsIgnoreCase("h_rk_naughty_push.caf")) {
				global.setPushSoundType(0);
				if(tempSound.equalsIgnoreCase("w_rk_naughty_push.caf")){
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_first_s");
				}else{
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_first");
				}
				
			}else if(tempSound.equalsIgnoreCase("w_rk_fa_role_push.caf") || tempSound.equalsIgnoreCase("h_rk_fa_role_push.caf")){
				global.setPushSoundType(1);
				
				if(tempSound.equalsIgnoreCase("w_rk_fa_role_push.caf")){
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_s");
				}else{
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push");
				}
			}else{
				global.setPushSoundType(2);
				RKPush.S_NOTIFY_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
		}else{
			global.setPushSoundType(1);
			if(sp.getInt(PROFILE_FIELD_GENDER, -1)==0){
				RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
						+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push_s");
			}else{
				RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
						+ ctx.getPackageName() + "/raw/" + "rk_fa_role_push");
			}
		}

	}
	
	//对方的资料职存储头像和名称
	public void saveMateProfile(String mateIcon, String mateName, Context ctx){
		SharedPreferences sp = ctx.getSharedPreferences(PROFILE_FILE_NAME, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		CxGlobalParams.getInstance().setPartnerIconBig(mateIcon);
		edit.putString(PROFILE_FIELD_MATE_ICON, mateIcon);
		CxGlobalParams.getInstance().setPartnerName(mateName);
		edit.putString(PROFILE_FIELD_MATE_NAME, mateName);
		edit.commit();
		
	}
	
	
}
