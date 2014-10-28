package com.chuxin.family.resource;

import com.chuxin.family.R;

/**
 * 字符串角色资源管理类
 * @author wentong.men
 *
 */
public class CxResourceString {

	
	private CxResourceString() {}
	
	private static CxResourceString string;
	
	/**
	 * 
	 * @param flag  true  男 ，  false  女
	 * @return
	 */
	public static CxResourceString getInstance(){
		
		if(string==null){
			string=new CxResourceString();
		}
		
		return string;
	}
	
	
	/**
	 * <!-- zh-rCN   strings 
	<string name="cx_fa_role_app_name">老公</string>
    <string name="cx_fa_role_app_name_s">老婆</string>
    <string name="cx_fa_role_reminder">老公</string>
    <string name="cx_fa_role_reminder_s">老婆</string>
    <string name="cx_fa_role_reminder_birthday_tip">老公生日提醒</string>
    <string name="cx_fa_role_reminder_birthday_tip_s">老婆生日提醒</string>
    
    <string name="cx_fa_role_congratulation_s">恭喜你安装了“老婆”，邀请她也安装一个“老公”吧，两人互装互通！你的验证码是%1$s</string>
    <string name="cx_fa_role_congratulation">恭喜你安装了“老公”，邀请他也安装一个“老婆”吧，两人互装互通！你的验证码是%1$s</string>
    
    <string name="cx_fa_role_phnoe_num">老婆的手机号</string>
    <string name="cx_fa_role_phnoe_num_s">老公的手机号</string>
    
    
     <string name="cx_fa_role_invite_text">邀请老公</string>
    <string name="cx_fa_role_invite_text_s">邀请老婆</string>
    <string name="cx_fa_role_invite_partner_text">赶快邀请老公安装</string>
    <string name="cx_fa_role_invite_partner_text_s">赶快邀请老婆安装</string>
    
    <string name="cx_fa_role_dimesionbar_show_text">让老公扫描二维码安装“老婆”应用</string>
    <string name="cx_fa_role_dimesionbar_show_text_s">让老婆扫描二维码安装“老公”应用</string>
    
    <string name="cx_fa_role_pair">老公</string>
    <string name="cx_fa_role_pair_s">老婆</string>
    
    <string name="cx_fa_role_pairsetup_versionname">“老婆”</string>
    <string name="cx_fa_role_pairsetup_versionname_s">“老公”</string>
    
    <string name="cx_fa_role_pair_gender">他</string>
    <string name="cx_fa_role_pair_gender_s">她</string>
    
    <string name="cx_fa_role_comingtext">老公即将驾到...</string>
    <string name="cx_fa_role_comingtext_s">老婆即将驾到...</string>
    
    <string name="cx_fa_role_about_version">关于老公</string>
    <string name="cx_fa_role_about_version_s">关于老婆</string>
    
    <string name="cx_fa_role_user_protocol_title">恭喜你安装了“老公”，请认真阅读用户协议。</string>
    <string name="cx_fa_role_user_protocol_title_s">恭喜你安装了“老婆”，请认真阅读用户协议。</string>
    
    <string name="cx_fa_role_mate_with_me">老公和我</string>
    <string name="cx_fa_role_mate_with_me_s">老婆和我</string>
    
    <string name="cx_fa_role_zone_mate">老公</string>
    <string name="cx_fa_role_zone_mate_s">老婆</string>
    
    <string name="cx_fa_role_taboloid_forward_s">转发给老婆</string>
    <string name="cx_fa_role_taboloid_forward">转发给老公</string>
    <string name="cx_fa_role_taboloid_forward_pre_s">老婆,转个有意思的给你:\n</string>
    <string name="cx_fa_role_taboloid_forward_pre">老公,转个有意思的给你:\n</string>
    
    


<string name="cx_fa_role_mateprofile_name_hint">老公的昵称</string>
    <string name="cx_fa_role_mateprofile_name_hint_s">老婆的昵称</string>
    <string name="cx_fa_role_mateprofile_birth_hint">老公的生日</string>
    <string name="cx_fa_role_mateprofile_birth_hint_s">老婆的生日</string>    
    <string name="cx_fa_role_mateprofile_mobile_hint">老公的手机号</string>
    <string name="cx_fa_role_mateprofile_mobile_hint_s">老婆的手机号</string>
    
     <string name="cx_fa_role_chat_welcome_first_msg">老公，我等你很久啦！</string>
    <string name="cx_fa_role_chat_welcome_first_msg_s">老婆，我等你很久啦！</string>
    <string name="cx_fa_role_chat_welcome_second_msg">哈哈，老婆我来啦！</string>
    <string name="cx_fa_role_chat_welcome_second_msg_s">哈哈，老公我来啦！</string>
    
    
    <string name="cx_fa_role_mate_name">老公%1$s</string>
    <string name="cx_fa_role_mate_name_s">老婆%1$s</string>
    
    <string name="cx_fa_role_invite_word">老公，快安装一个叫“老婆”的应用，是个家庭微信，比微信好玩又私密，很酷！我的公婆号是 "%1$s" ，这是安装地址：http://m.family.chuxin.net/dl/wife</string>
     <string name="cx_fa_role_invite_word_s">老婆，快安装一个叫“老公”的应用，是个家庭微信，比微信好玩又私密，很酷！我的公婆号是 "%1$s"，这是安装地址：http://m.family.chuxin.net/dl/husband</string>
     
     <string name="cx_fa_role_send_img_hint">关于照片想跟老公说些什么呢？</string>
    <string name="cx_fa_role_send_img_hint_s">关于照片想跟老公说些什么呢？</string>
    <string name="cx_fa_role_send_text_hint">想写些什么跟老公分享？</string>
    <string name="cx_fa_role_send_text_hint_s">想写些什么跟老婆分享？</string>
    
    <string name="cx_fa_role_version_name">family_husband/</string>
    <string name="cx_fa_role_version_name_s">family_wife/</string>
    
    <string name="cx_fa_role_mateprofile_title_s">老婆的资料</string>
    <string name="cx_fa_role_mateprofile_title">老公的资料</string>
   
    
    
    <string name="cx_fa_role_invite_by_qq">通过QQ邀请老公</string>
    <string name="cx_fa_role_invite_by_qq_s">通过QQ邀请老婆</string>
    <string name="cx_fa_role_invite_by_dx_s">通过短信邀请老婆</string>
    <string name="cx_fa_role_invite_by_dx">通过短信邀请老公</string>
    <string name="cx_fa_role_invite_by_wx_s">通过微信邀请老婆</string>
    <string name="cx_fa_role_invite_by_wx">通过微信邀请老公</string>
    <string name="cx_fa_role_invite_by_qr">让老公扫描二维码安装“老婆”</string>
    <string name="cx_fa_role_invite_by_qr_s">让老婆扫描二维码安装“老公”</string>
    
    
    <string name="cx_fa_role_push_first_sound">调皮</string>
    <string name="cx_fa_role_push_first_sound_s">三弦音</string>
    <string name="cx_fa_role_push_second_sound">老婆</string>
    <string name="cx_fa_role_push_second_sound_s">老公</string>
    
    <string name="cx_fa_role_pair_invite_code_dialog_text">让他扫描二维码</string>
    <string name="cx_fa_role_pair_invite_code_dialog_text2">马上安装“老婆”应用</string>
    <string name="cx_fa_role_pair_invite_code_dialog_text_s">让她扫描二维码</string>
    <string name="cx_fa_role_pair_invite_code_dialog_text2_s">马上安装“老公”应用</string>
    
    <string name="cx_fa_role_neighbour_pair">老婆</string>
    <string name="cx_fa_role_neighbour_pair_s">老公</string>
    
    <string name="cx_fa_role_send_chart_to_partner">把图转发给老公</string>
    <string name="cx_fa_role_send_chart_to_partner_s">把图转发给老婆</string>
    
    <string name="cx_fa_role_accounting_account_title_opposite_account">老公的记账</string> 
    <string name="cx_fa_role_accounting_account_title_opposite_account_s">老婆的记账</string> 
    
    <string name="cx_fa_role_accounting_account_tip" formatted="false" >老公, 这是咱们%s的%s结构</string>
    <string name="cx_fa_role_accounting_account_tip_s" formatted="false">老婆, 这是咱们%s的%s结构</string> 
    
    
    <string name="cx_fa_role_inputpannel_sound_text1">按住说话 (装幼)</string>
    <string name="cx_fa_role_inputpannel_sound_text1_s">按住说话(装憨)</string>
    <string name="cx_fa_role_inputpannel_sound_text2">按住说话(装傻)</string>
    <string name="cx_fa_role_inputpannel_sound_text2_s">按住说话(装坏)</string>
    
    
    
    <string name="cx_fa_role_answer_question_half_dialog_content">你已经答完今天的一半题目，给老公留几道吧？</string>
    <string name="cx_fa_role_answer_question_half_dialog_content_s">你已经答完今天的一半题目，给老婆留几道吧？</string>
    
    
    <string name="cx_fa_role_push_first">cx_fa_role_push_first</string>
    <string name="cx_fa_role_push_first_s">cx_fa_role_push_first_s</string>
    <string name="cx_fa_role_push">cx_fa_role_push</string>
    <string name="cx_fa_role_push_s">cx_fa_role_push_s</string>
    
    
    <string name="cx_fa_role_emotion_version_en">husband</string>
    <string name="cx_fa_role_emotion_version_en_s">wife</string>
    
-->
	 * 
	 * 
	 * 
	 */
	
	public int str_app_name=R.string.cx_fa_role_app_name;
	public int str_reminder_name=R.string.cx_fa_role_reminder;//没有在用  可以注掉  暂保留  
	public int str_reminder_birthday_tip=R.string.cx_fa_role_reminder_birthday_tip;
	public int str_pair_congratulation=R.string.cx_fa_role_congratulation;
	
	public int str_main_login_phnoe_num=R.string.cx_fa_role_phnoe_num;   //没有在用  可以注掉  暂保留  
	public int str_pair_invite_text=R.string.cx_fa_role_invite_text;
	public int str_pair_invite_partner_text=R.string.cx_fa_role_invite_partner_text;
	public int str_main_login_dimesionbar_show_text=R.string.cx_fa_role_dimesionbar_show_text;//没有在用  可以注掉  暂保留  
	
	public int str_pair=R.string.cx_fa_role_pair;
	public int str_pair_gender=R.string.cx_fa_role_pair_gender;//没有在用  可以注掉  暂保留
	
	public int str_main_login_pairsetup_versionname=R.string.cx_fa_role_pairsetup_versionname;//没有在用  可以注掉  暂保留
	public int str_main_login_comingtext=R.string.cx_fa_role_comingtext;
	
//	public int str_setting_about_version=R.string.cx_fa_role_about_version;//不用了 
//	public int str_setting_about_user_protocol_title=R.string.cx_fa_role_user_protocol_title;  不用了
	
	public int str_zone_mate_with_me=R.string.cx_fa_role_mate_with_me;
	public int str_zone_mate=R.string.cx_fa_role_zone_mate;
	
	public int str_taboloid_forward=R.string.cx_fa_role_taboloid_forward;
	public int str_taboloid_forward_pre=R.string.cx_fa_role_taboloid_forward_pre;
	
	
	public int str_mate_mateprofile_name_hint=R.string.cx_fa_role_mateprofile_name_hint;
	public int str_mate_mateprofile_birth_hint=R.string.cx_fa_role_mateprofile_birth_hint;
	public int str_mate_mateprofile_mobile_hint=R.string.cx_fa_role_mateprofile_mobile_hint;
	public int str_mate_mateprofile_title=R.string.cx_fa_role_mateprofile_title;
	
	public int str_mate_name=R.string.cx_fa_role_mate_name;
	
	public int str_chat_welcome_first_msg=R.string.cx_fa_role_chat_welcome_first_msg;
	public int str_chat_welcome_second_msg=R.string.cx_fa_role_chat_welcome_second_msg;
	
	public int str_chat_welcome_single_mode_default=R.string.cx_fa_chat_welcome_single_mode_default;
		
	
	public int str_zone_feed_send_img_hint=R.string.cx_fa_role_send_img_hint;
	public int str_zone_feed_send_text_hint=R.string.cx_fa_role_send_text_hint;
	
	public int str_version_name=R.string.cx_fa_role_version_name;//没有在用  可以注掉  暂保留
	
	public int str_pair_invite_word=R.string.cx_fa_role_invite_word;
	public int str_pair_invite_by_qq=R.string.cx_fa_role_invite_by_qq;
	public int str_pair_invite_by_dx=R.string.cx_fa_role_invite_by_dx;
	public int str_pair_invite_by_wx=R.string.cx_fa_role_invite_by_wx;
	public int str_pair_invite_by_qr=R.string.cx_fa_role_invite_by_qr;
	
	public int str_pair_invite_code_dialog_text=R.string.cx_fa_role_pair_invite_code_dialog_text;
	public int str_pair_invite_code_dialog_text2=R.string.cx_fa_role_pair_invite_code_dialog_text2;
	
	
	public int str_answer_question_half_dialog_content=R.string.cx_fa_role_answer_question_half_dialog_content;
	
	
	
	public int str_setting_sound_push_first_sound=R.string.cx_fa_role_push_first_sound;
	public int str_setting_sound_push_second_sound=R.string.cx_fa_role_push_second_sound;
	
	public int str_accounting_pie_send_chart_to_partner=R.string.cx_fa_role_send_chart_to_partner;
	public int str_accounting_account_title_opposite_account=R.string.cx_fa_role_accounting_account_title_opposite_account;
	public int str_accounting_account_tip=R.string.cx_fa_role_accounting_account_tip;
	
	
	public int str_chat_inputpannel_sound_text1=R.string.cx_fa_role_inputpannel_sound_text1;
	public int str_chat_inputpannel_sound_text2=R.string.cx_fa_role_inputpannel_sound_text2;
	
	
	public int str_chat_emotion_version_en=R.string.cx_fa_role_emotion_version_en;
	
	
	public int str_pair_top_word=R.string.cx_fa_role_pair_top_word;
	public int str_pair_edit_hint=R.string.cx_fa_role_pair_edit_hint;
	public int str_pair_invite_oppo_btn_text=R.string.cx_fa_pair_invite_oppo_btn_text;
	
	
	public int str_main_menu_chat_with=R.string.cx_fa_role_main_menu_chat_with;
	public int str_main_menu_invite=R.string.cx_fa_role_main_menu_invite;
	
	
	public int str_chat_input_popuimenu_whip_btn=R.string.cx_fa_role_chat_popupmenu_whip_btn;
	
	
	public int str_chat_input_soundeffect_text1=R.string.cx_fa_role_inputpannel_soundeffect_text1;
	public int str_chat_input_soundeffect_text2=R.string.cx_fa_role_inputpannel_soundeffect_text2;
	
	
	public int str_kids_home_no_feed_text=R.string.cx_fa_kids_home_no_feed_text;
	
	
	public int str_pair_role_clash_text=R.string.cx_fa_role_clash_text;
	
	

	public void setStringType(boolean flag){
		if(flag){
			
			str_app_name=R.string.cx_fa_role_app_name;
			str_reminder_name=R.string.cx_fa_role_reminder;
			str_reminder_birthday_tip=R.string.cx_fa_role_reminder_birthday_tip;
			str_pair_congratulation=R.string.cx_fa_role_congratulation;
			
			str_main_login_phnoe_num=R.string.cx_fa_role_phnoe_num;
			str_pair_invite_text=R.string.cx_fa_role_invite_text;
			str_pair_invite_partner_text=R.string.cx_fa_role_invite_partner_text;
			str_main_login_dimesionbar_show_text=R.string.cx_fa_role_dimesionbar_show_text;
			
			str_pair=R.string.cx_fa_role_pair;
			str_pair_gender=R.string.cx_fa_role_pair_gender;
			
			str_main_login_pairsetup_versionname=R.string.cx_fa_role_pairsetup_versionname;
			str_main_login_comingtext=R.string.cx_fa_role_comingtext;
			
//			str_setting_about_version=R.string.cx_fa_role_about_version;
//			str_setting_about_user_protocol_title=R.string.cx_fa_role_user_protocol_title;
			
			str_zone_mate_with_me=R.string.cx_fa_role_mate_with_me;
			str_zone_mate=R.string.cx_fa_role_zone_mate;
			
			str_taboloid_forward=R.string.cx_fa_role_taboloid_forward;
			str_taboloid_forward_pre=R.string.cx_fa_role_taboloid_forward_pre;
			
			
			str_mate_mateprofile_name_hint=R.string.cx_fa_role_mateprofile_name_hint;
			str_mate_mateprofile_birth_hint=R.string.cx_fa_role_mateprofile_birth_hint;
			str_mate_mateprofile_mobile_hint=R.string.cx_fa_role_mateprofile_mobile_hint;
			str_mate_mateprofile_title=R.string.cx_fa_role_mateprofile_title;
			
			str_mate_name=R.string.cx_fa_role_mate_name;
			
			str_chat_welcome_first_msg=R.string.cx_fa_role_chat_welcome_first_msg;
			str_chat_welcome_second_msg=R.string.cx_fa_role_chat_welcome_second_msg;
			str_chat_welcome_single_mode_default=R.string.cx_fa_chat_welcome_single_mode_default;
				
			
			str_zone_feed_send_img_hint=R.string.cx_fa_role_send_img_hint;
			str_zone_feed_send_text_hint=R.string.cx_fa_role_send_text_hint;
			
			str_version_name=R.string.cx_fa_role_version_name;
			
			str_pair_invite_word=R.string.cx_fa_role_invite_word;
			str_pair_invite_by_qq=R.string.cx_fa_role_invite_by_qq;
			str_pair_invite_by_dx=R.string.cx_fa_role_invite_by_dx;
			str_pair_invite_by_wx=R.string.cx_fa_role_invite_by_wx;
			str_pair_invite_by_qr=R.string.cx_fa_role_invite_by_qr;
			
//			str_pair_invite_code_dialog_text=R.string.cx_fa_role_pair_invite_code_dialog_text;
//			str_pair_invite_code_dialog_text2=R.string.cx_fa_role_pair_invite_code_dialog_text2;
			
			
			
			str_setting_sound_push_first_sound=R.string.cx_fa_role_push_first_sound;
			str_setting_sound_push_second_sound=R.string.cx_fa_role_push_second_sound;
			
			str_accounting_pie_send_chart_to_partner=R.string.cx_fa_role_send_chart_to_partner;
			str_accounting_account_title_opposite_account=R.string.cx_fa_role_accounting_account_title_opposite_account;
			str_accounting_account_tip=R.string.cx_fa_role_accounting_account_tip;
			
			
			str_chat_inputpannel_sound_text1=R.string.cx_fa_role_inputpannel_sound_text1;
			str_chat_inputpannel_sound_text2=R.string.cx_fa_role_inputpannel_sound_text2;
			
			str_answer_question_half_dialog_content=R.string.cx_fa_role_answer_question_half_dialog_content;
			
			str_chat_emotion_version_en=R.string.cx_fa_role_emotion_version_en;
			
			str_pair_top_word=R.string.cx_fa_role_pair_top_word;
			str_pair_edit_hint=R.string.cx_fa_role_pair_edit_hint;
			str_pair_invite_oppo_btn_text=R.string.cx_fa_pair_invite_oppo_btn_text;
			
			str_main_menu_chat_with=R.string.cx_fa_role_main_menu_chat_with;
			str_main_menu_invite=R.string.cx_fa_role_main_menu_invite;
			
			str_chat_input_popuimenu_whip_btn=R.string.cx_fa_role_chat_popupmenu_whip_btn;
			
			str_chat_input_soundeffect_text1=R.string.cx_fa_role_inputpannel_soundeffect_text1;
			str_chat_input_soundeffect_text2=R.string.cx_fa_role_inputpannel_soundeffect_text2;
			
			str_kids_home_no_feed_text=R.string.cx_fa_kids_home_no_feed_text;
			
			str_pair_role_clash_text=R.string.cx_fa_role_clash_text;
			
		}else{
			str_app_name=R.string.cx_fa_role_app_name_s;
			str_reminder_name=R.string.cx_fa_role_reminder_s;
			str_reminder_birthday_tip=R.string.cx_fa_role_reminder_birthday_tip_s;
			str_pair_congratulation=R.string.cx_fa_role_congratulation_s;
			
			str_main_login_phnoe_num=R.string.cx_fa_role_phnoe_num_s;
			str_pair_invite_text=R.string.cx_fa_role_invite_text_s;
			str_pair_invite_partner_text=R.string.cx_fa_role_invite_partner_text_s;
			str_main_login_dimesionbar_show_text=R.string.cx_fa_role_dimesionbar_show_text_s;
			
			str_pair=R.string.cx_fa_role_pair_s;
			str_pair_gender=R.string.cx_fa_role_pair_gender_s;
			
			str_main_login_pairsetup_versionname=R.string.cx_fa_role_pairsetup_versionname_s;
			str_main_login_comingtext=R.string.cx_fa_role_comingtext_s;
			
//			str_setting_about_version=R.string.cx_fa_role_about_version_s;
//			str_setting_about_user_protocol_title=R.string.cx_fa_role_user_protocol_title_s;
			
			str_zone_mate_with_me=R.string.cx_fa_role_mate_with_me_s;
			str_zone_mate=R.string.cx_fa_role_zone_mate_s;
			
			str_taboloid_forward=R.string.cx_fa_role_taboloid_forward_s;
			str_taboloid_forward_pre=R.string.cx_fa_role_taboloid_forward_pre_s;
			
			
			str_mate_mateprofile_name_hint=R.string.cx_fa_role_mateprofile_name_hint_s;
			str_mate_mateprofile_birth_hint=R.string.cx_fa_role_mateprofile_birth_hint_s;
			str_mate_mateprofile_mobile_hint=R.string.cx_fa_role_mateprofile_mobile_hint_s;
			str_mate_mateprofile_title=R.string.cx_fa_role_mateprofile_title_s;
			
			str_mate_name=R.string.cx_fa_role_mate_name_s;
			
			str_chat_welcome_first_msg=R.string.cx_fa_role_chat_welcome_first_msg_s;
			str_chat_welcome_second_msg=R.string.cx_fa_role_chat_welcome_second_msg_s;
			str_chat_welcome_single_mode_default=R.string.cx_fa_chat_welcome_single_mode_default_s;
				
			
			str_zone_feed_send_img_hint=R.string.cx_fa_role_send_img_hint_s;
			str_zone_feed_send_text_hint=R.string.cx_fa_role_send_text_hint_s;
			
			str_version_name=R.string.cx_fa_role_version_name_s;
			
			str_pair_invite_word=R.string.cx_fa_role_invite_word_s;
			str_pair_invite_by_qq=R.string.cx_fa_role_invite_by_qq_s;
			str_pair_invite_by_dx=R.string.cx_fa_role_invite_by_dx_s;
			str_pair_invite_by_wx=R.string.cx_fa_role_invite_by_wx_s;
			str_pair_invite_by_qr=R.string.cx_fa_role_invite_by_qr_s;
			
//			str_pair_invite_code_dialog_text=R.string.cx_fa_role_pair_invite_code_dialog_text_s;
//			str_pair_invite_code_dialog_text2=R.string.cx_fa_role_pair_invite_code_dialog_text2_s;
			
			
			
			str_setting_sound_push_first_sound=R.string.cx_fa_role_push_first_sound_s;
			str_setting_sound_push_second_sound=R.string.cx_fa_role_push_second_sound_s;
			
			str_accounting_pie_send_chart_to_partner=R.string.cx_fa_role_send_chart_to_partner_s;
			str_accounting_account_title_opposite_account=R.string.cx_fa_role_accounting_account_title_opposite_account_s;
			str_accounting_account_tip=R.string.cx_fa_role_accounting_account_tip_s;
			
			
			str_chat_inputpannel_sound_text1=R.string.cx_fa_role_inputpannel_sound_text1_s;
			str_chat_inputpannel_sound_text2=R.string.cx_fa_role_inputpannel_sound_text2_s;
			
			
			str_answer_question_half_dialog_content=R.string.cx_fa_role_answer_question_half_dialog_content_s;
			
			str_chat_emotion_version_en=R.string.cx_fa_role_emotion_version_en_s;
			
			str_pair_top_word=R.string.cx_fa_role_pair_top_word_s;
			str_pair_edit_hint=R.string.cx_fa_role_pair_edit_hint_s;
			str_pair_invite_oppo_btn_text=R.string.cx_fa_pair_invite_oppo_btn_text_s;
			
			
			str_main_menu_chat_with=R.string.cx_fa_role_main_menu_chat_with_s;
			str_main_menu_invite=R.string.cx_fa_role_main_menu_invite_s;
			
			str_chat_input_popuimenu_whip_btn=R.string.cx_fa_role_chat_popupmenu_whip_btn_s;
			
			str_chat_input_soundeffect_text1=R.string.cx_fa_role_inputpannel_soundeffect_text1_s;
			str_chat_input_soundeffect_text2=R.string.cx_fa_role_inputpannel_soundeffect_text2_s;
			
			str_kids_home_no_feed_text=R.string.cx_fa_kids_home_no_feed_text_s;
			str_pair_role_clash_text=R.string.cx_fa_role_clash_text_s;
		}
	}
	
	
	public String getStringByFlag(String str,int flag){
		
		if(flag==0){
			return "cx_fa_role_"+str+"_s";
		}else{
			return "cx_fa_role_"+str;
		}
	}

	
	
	
	
	
	
	
	
	
	
}
