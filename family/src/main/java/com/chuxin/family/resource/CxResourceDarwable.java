package com.chuxin.family.resource;

import com.chuxin.family.R;
/**
 * 图片角色资源管理类
 * @author wentong.men
 *
 */
public class CxResourceDarwable {

	
	private CxResourceDarwable() {}
	
	private static CxResourceDarwable drawable;
	
	/**
	 * 
	 * @param flag  true  男 ，  false  女
	 * @return
	 */
	public static CxResourceDarwable getInstance(){
		
		if(drawable==null){
			drawable=new CxResourceDarwable();
		}
		
		return drawable;
	}
	
    /*<!-- drawable
    cx_fa_role_biaoqing_btnluobo_h_s.png
    cx_fa_role_biaoqing_btnluobo_h.png
    cx_fa_role_biaoqing_btnluobo_s.png
    cx_fa_role_biaoqing_btnluobo.png
    
    cx_fa_role_biaoqing_btnlvdou_h_s.png
    cx_fa_role_biaoqing_btnlvdou_h.png   
    cx_fa_role_biaoqing_btnlvdou_s.png
    cx_fa_role_biaoqing_btnlvdou.png
    
    cx_fa_role_biaoqing_btnwenzi_h_s.png
    cx_fa_role_biaoqing_btnwenzi_h.png
    cx_fa_role_biaoqing_btnwenzi_s.png
    cx_fa_role_biaoqing_btnwenzi.png
    
    cx_fa_role_biaoqing_btnzanlia_h_s.png
    cx_fa_role_biaoqing_btnzanlia_h.png
    cx_fa_role_biaoqing_btnzanlia_s.png
    cx_fa_role_biaoqing_btnzanlia.png
    
    cx_fa_role_biaoqing_info_fuhao_s.png
    cx_fa_role_biaoqing_info_fuhao.png
    
    cx_fa_role_biaoqing_info_luobo_s.png
    cx_fa_role_biaoqing_info_luobo.png
    
    cx_fa_role_biaoqing_info_lvdou_s.png
    cx_fa_role_biaoqing_info_lvdou.png
    
    cx_fa_role_biaoqing_info_word_s.png
    cx_fa_role_biaoqing_info_word.png
    
    cx_fa_role_biaoqing_info_zanlia_s.png
    cx_fa_role_biaoqing_info_zanlia.png
    
    cx_fa_role_inputpanel_whip_btn_s.xml
    cx_fa_role_inputpanel_whip_btn.xml
    
    cx_fa_role_qrcode_s.png
    cx_fa_role_qrcode.png   
    
     
     
     
     <!-- drawable-hdpi
cx_fa_role_app_icon_s.png
cx_fa_role_app_icon.png

cx_fa_role_login_introduction1_s.jpg
cx_fa_role_login_introduction1.jpg
cx_fa_role_login_introduction2_s.jpg
cx_fa_role_login_introduction2.jpg
cx_fa_role_login_introduction3_s.jpg
cx_fa_role_login_introduction3.jpg
cx_fa_role_login_introduction4_s.jpg
cx_fa_role_login_introduction4.jpg
cx_fa_role_login_introduction5_s.jpg
cx_fa_role_login_introduction5.jpg

cx_fa_role_memo_imagedefault_s.png
cx_fa_role_memo_imagedefault.png

cx_fa_role_remind_image_s.png
cx_fa_role_remind_image.png

cx_fa_role_versiontip_s.png
cx_fa_role_versiontip.png

cx_fa_role_wait_logo_s.png
cx_fa_role_wait_logo.png

cx_fa_role_wezone_defaultimage_s.png
cx_fa_role_wezone_defaultimage.png  
-->
     
     
<!-- drawable-mdpi
cx_fa_role_app_icon_s.png
cx_fa_role_app_icon.png
cx_fa_role_chatbg_default_s.jpg
cx_fa_role_chatbg_default.jpg
cx_fa_role_login_introduction1_s.jpg
cx_fa_role_login_introduction1.jpg
cx_fa_role_login_introduction2_s.jpg
cx_fa_role_login_introduction2.jpg
cx_fa_role_login_introduction3_s.jpg
cx_fa_role_login_introduction3.jpg
cx_fa_role_login_introduction4_s.jpg
cx_fa_role_login_introduction4.jpg
cx_fa_role_login_introduction5_s.jpg
cx_fa_role_login_introduction5.jpg
cx_fa_role_memo_imagedefault_s.png
cx_fa_role_memo_imagedefault.png
cx_fa_role_versiontip_s.png
cx_fa_role_versiontip.png
cx_fa_role_wait_logo_s.png
cx_fa_role_wait_logo.png
-->
     	
<!-- drawable-xhdpi
cx_fa_role_app_icon_s.png
cx_fa_role_app_icon.png
cx_fa_role_chatbg_default_s.jpg
cx_fa_role_chatbg_default.jpg
cx_fa_role_chatbg_thumbnail_default_s.png
cx_fa_role_chatbg_thumbnail_default.png
cx_fa_role_login_introduction5_s.jpg
cx_fa_role_login_introduction5.jpg
cx_fa_role_memo_imagedefault_s.png
cx_fa_role_memo_imagedefault.png
cx_fa_role_versiontip_s.png
cx_fa_role_versiontip.png
cx_fa_role_wait_logo_s.png
cx_fa_role_wait_logo.png
-->

     
    -->*/
	
	/**
	 * 
	 * 引用命名规则： 资源分类名_模块名_次模块名_类名_类中层次名_资源名
	 * 注：
	 *    1  没有次模块名  可不写     
	 *    2  通常 类名和类中层次名可不写  如在统一模块出现冲突命名则需要写  做为区分
	 *    3  如该资源是全局使用  可不写  模块名和类名及 类中层析名
	 * 				模块通用   可不写类名及 类中层析名
	 * 				类中唯一   可不写 类中层次名
	 * 
	 * 
	 * 
	 * 资源分类名如下：(现在需合并的角色资源分以下四类     如后续需要  再商议命名)
	 * 				co - 颜色
                   	dr - drawable
					raw - raw
                   	str - 字符串

                   
                   
        	模块名     和包名相同
        	
        	  次模块名   可根据该模块功能定义  如  聊天下面的表情可以叫做  emotion
        	  
        	  资源名  沿用原来的资源名  如是新资源  尽量见名知义即可
   
	 * 
	 */
		
	
	public  int dr_chat_emotion_biaoqing_btnluobo_h=R.drawable.cx_fa_role_biaoqing_btnluobo_h;	
	public  int dr_chat_emotion_biaoqing_btnluobo=R.drawable.cx_fa_role_biaoqing_btnluobo;
	
	public  int dr_chat_emotion_biaoqing_btnlvdou_h=R.drawable.cx_fa_role_biaoqing_btnlvdou_h;
	public  int dr_chat_emotion_biaoqing_btnlvdou=R.drawable.cx_fa_role_biaoqing_btnlvdou;
	
	public  int dr_chat_emotion_biaoqing_btnwenzi_h=R.drawable.cx_fa_role_biaoqing_btnwenzi_h;
	public  int dr_chat_emotion_biaoqing_btnwenzi=R.drawable.cx_fa_role_biaoqing_btnwenzi;
	
	public  int dr_chat_emotion_biaoqing_btnzanlia_h=R.drawable.cx_fa_role_biaoqing_btnzanlia_h;
	public  int dr_chat_emotion_biaoqing_btnzanlia=R.drawable.cx_fa_role_biaoqing_btnzanlia;
	
	public  int dr_chat_emotion_biaoqing_info_fuhao=R.drawable.cx_fa_role_biaoqing_info_fuhao;
	public  int dr_chat_emotion_biaoqing_info_luobo=R.drawable.cx_fa_role_biaoqing_info_luobo;
	public  int dr_chat_emotion_biaoqing_info_lvdou=R.drawable.cx_fa_role_biaoqing_info_lvdou;
	public  int dr_chat_emotion_biaoqing_info_word=R.drawable.cx_fa_role_biaoqing_info_word;
	public  int dr_chat_emotion_biaoqing_info_zanlia=R.drawable.cx_fa_role_biaoqing_info_zanlia;
	
	
	public  int dr_chat_inputpanel_whip_btn=R.drawable.cx_fa_role_inputpanel_whip_btn;
	
//	public  int dr_pair_invite_qrcode=R.drawable.cx_fa_role_qrcode;
	
	//drawable-hdpi
//	public  int dr_app_icon=R.drawable.cx_fa_role_app_icon; //应用图标已改
	
//	public  int dr_app_icon_oppo=R.drawable.cx_fa_role_app_icon_s;//修改设置里自己的默认头像
	
//	public  int dr_main_login_introduction1=R.drawable.cx_fa_role_login_introduction1;
//	public  int dr_main_login_introduction2=R.drawable.cx_fa_role_login_introduction2;
//	public  int dr_main_login_introduction3=R.drawable.cx_fa_role_login_introduction3;
//	public  int dr_main_login_introduction4=R.drawable.cx_fa_role_login_introduction4;
//	public  int dr_main_login_introduction5=R.drawable.cx_fa_role_login_introduction5;
	
	public  int dr_mate_memo_imagedefault=R.drawable.cx_fa_role_memo_imagedefault;
	public  int dr_reminder_image=R.drawable.cx_fa_role_remind_image;
//	public  int dr_pair_versiontip=R.drawable.cx_fa_role_versiontip;
//	public  int dr_main_login_wait_logo=R.drawable.cx_fa_role_wait_logo;
	public  int dr_zone_defaultimage=R.drawable.cx_fa_role_wezone_defaultimage;
	
	//drawable-mdpi
	public  int dr_chat_chatbg_default=R.drawable.cx_fa_role_chatbg_default;
	
	//drawable-xhdpi
	public  int dr_chat_chatbg_thumbnail_default=R.drawable.cx_fa_role_chatbg_thumbnail_default;
	
	public  int dr_chat_icon_small_me=R.drawable.cx_fa_wf_icon_small;
	public  int dr_chat_icon_small_oppo=R.drawable.cx_fa_hb_icon_small;
	
	public  int dr_zone_icon_small_me=R.drawable.cx_fa_wf_small_icon;
	public  int dr_zone_icon_small_oppo=R.drawable.cx_fa_hb_small_icon;
	
	
	public  int dr_calendar_clock_img_me=R.drawable.calendar_icon_clockpink;
	public  int dr_calendar_clock_img_oppo=R.drawable.calendar_icon_clockblue;
	
	
	public  void setDrawableType(boolean flag){
		if(flag){
			//drawable
			dr_chat_emotion_biaoqing_btnluobo_h=R.drawable.cx_fa_role_biaoqing_btnluobo_h;
			dr_chat_emotion_biaoqing_btnluobo=R.drawable.cx_fa_role_biaoqing_btnluobo;

			dr_chat_emotion_biaoqing_btnlvdou_h=R.drawable.cx_fa_role_biaoqing_btnlvdou_h;
			dr_chat_emotion_biaoqing_btnlvdou=R.drawable.cx_fa_role_biaoqing_btnlvdou;
			
			dr_chat_emotion_biaoqing_btnwenzi_h=R.drawable.cx_fa_role_biaoqing_btnwenzi_h;
			dr_chat_emotion_biaoqing_btnwenzi=R.drawable.cx_fa_role_biaoqing_btnwenzi;
			
			dr_chat_emotion_biaoqing_btnzanlia_h=R.drawable.cx_fa_role_biaoqing_btnzanlia_h;
			dr_chat_emotion_biaoqing_btnzanlia=R.drawable.cx_fa_role_biaoqing_btnzanlia;
			
			dr_chat_emotion_biaoqing_info_fuhao=R.drawable.cx_fa_role_biaoqing_info_fuhao;
			dr_chat_emotion_biaoqing_info_luobo=R.drawable.cx_fa_role_biaoqing_info_luobo;
			dr_chat_emotion_biaoqing_info_lvdou=R.drawable.cx_fa_role_biaoqing_info_lvdou;
			dr_chat_emotion_biaoqing_info_word=R.drawable.cx_fa_role_biaoqing_info_word;
			dr_chat_emotion_biaoqing_info_zanlia=R.drawable.cx_fa_role_biaoqing_info_zanlia;
			
			
			dr_chat_inputpanel_whip_btn=R.drawable.cx_fa_role_inputpanel_whip_btn;
			
//			dr_pair_invite_qrcode=R.drawable.cx_fa_role_qrcode;
			
			//drawable-hdpi
//			dr_app_icon=R.drawable.cx_fa_role_app_icon;
			
//			dr_main_login_introduction1=R.drawable.cx_fa_role_login_introduction1;
//			dr_main_login_introduction2=R.drawable.cx_fa_role_login_introduction2;
//			dr_main_login_introduction3=R.drawable.cx_fa_role_login_introduction3;
//			dr_main_login_introduction4=R.drawable.cx_fa_role_login_introduction4;
//			dr_main_login_introduction5=R.drawable.cx_fa_role_login_introduction5;
			
			dr_mate_memo_imagedefault=R.drawable.cx_fa_role_memo_imagedefault;
			dr_reminder_image=R.drawable.cx_fa_role_remind_image;
//			dr_pair_versiontip=R.drawable.cx_fa_role_versiontip;
//			dr_main_login_wait_logo=R.drawable.cx_fa_role_wait_logo;
			dr_zone_defaultimage=R.drawable.cx_fa_role_wezone_defaultimage;
			
			//drawable-mdpi
			dr_chat_chatbg_default=R.drawable.cx_fa_role_chatbg_default;
			
			//drawable-xhdpi
			dr_chat_chatbg_thumbnail_default=R.drawable.cx_fa_role_chatbg_thumbnail_default;
			
//			dr_app_icon_oppo=R.drawable.cx_fa_role_app_icon_s;
			
			dr_chat_icon_small_me=R.drawable.cx_fa_wf_icon_small;
			dr_chat_icon_small_oppo=R.drawable.cx_fa_hb_icon_small;
			
			dr_zone_icon_small_me=R.drawable.cx_fa_wf_small_icon;
			dr_zone_icon_small_oppo=R.drawable.cx_fa_hb_small_icon;
			
			dr_calendar_clock_img_me=R.drawable.calendar_icon_clockpink;
			dr_calendar_clock_img_oppo=R.drawable.calendar_icon_clockblue;
			
		}else{
			//drawable
			dr_chat_emotion_biaoqing_btnluobo_h=R.drawable.cx_fa_role_biaoqing_btnluobo_h_s;
			dr_chat_emotion_biaoqing_btnluobo=R.drawable.cx_fa_role_biaoqing_btnluobo_s;

			dr_chat_emotion_biaoqing_btnlvdou_h=R.drawable.cx_fa_role_biaoqing_btnlvdou_h_s;
			dr_chat_emotion_biaoqing_btnlvdou=R.drawable.cx_fa_role_biaoqing_btnlvdou_s;
			
			dr_chat_emotion_biaoqing_btnwenzi_h=R.drawable.cx_fa_role_biaoqing_btnwenzi_h_s;
			dr_chat_emotion_biaoqing_btnwenzi=R.drawable.cx_fa_role_biaoqing_btnwenzi_s;
			
			dr_chat_emotion_biaoqing_btnzanlia_h=R.drawable.cx_fa_role_biaoqing_btnzanlia_h_s;
			dr_chat_emotion_biaoqing_btnzanlia=R.drawable.cx_fa_role_biaoqing_btnzanlia_s;
			
			dr_chat_emotion_biaoqing_info_fuhao=R.drawable.cx_fa_role_biaoqing_info_fuhao_s;
			dr_chat_emotion_biaoqing_info_luobo=R.drawable.cx_fa_role_biaoqing_info_luobo_s;
			dr_chat_emotion_biaoqing_info_lvdou=R.drawable.cx_fa_role_biaoqing_info_lvdou_s;
			dr_chat_emotion_biaoqing_info_word=R.drawable.cx_fa_role_biaoqing_info_word_s;
			dr_chat_emotion_biaoqing_info_zanlia=R.drawable.cx_fa_role_biaoqing_info_zanlia_s;
			
			
			dr_chat_inputpanel_whip_btn=R.drawable.cx_fa_role_inputpanel_whip_btn_s;
			
//			dr_pair_invite_qrcode=R.drawable.cx_fa_role_qrcode_s;
			
			//drawable-hdpi
//			dr_app_icon=R.drawable.cx_fa_role_app_icon_s;
			
//			dr_main_login_introduction1=R.drawable.cx_fa_role_login_introduction1_s;
//			dr_main_login_introduction2=R.drawable.cx_fa_role_login_introduction2_s;
//			dr_main_login_introduction3=R.drawable.cx_fa_role_login_introduction3_s;
//			dr_main_login_introduction4=R.drawable.cx_fa_role_login_introduction4_s;
//			dr_main_login_introduction5=R.drawable.cx_fa_role_login_introduction5_s;
			
			dr_mate_memo_imagedefault=R.drawable.cx_fa_role_memo_imagedefault_s;
			dr_reminder_image=R.drawable.cx_fa_role_remind_image_s;
//			dr_pair_versiontip=R.drawable.cx_fa_role_versiontip_s;
//			dr_main_login_wait_logo=R.drawable.cx_fa_role_wait_logo_s;
			dr_zone_defaultimage=R.drawable.cx_fa_role_wezone_defaultimage_s;
			
			//drawable-mdpi
			dr_chat_chatbg_default=R.drawable.cx_fa_role_chatbg_default_s;
			
			//drawable-xhdpi
			dr_chat_chatbg_thumbnail_default=R.drawable.cx_fa_role_chatbg_thumbnail_default_s;
			
			
			dr_chat_icon_small_me=R.drawable.cx_fa_hb_icon_small;
			dr_chat_icon_small_oppo=R.drawable.cx_fa_wf_icon_small;
			
			dr_zone_icon_small_me=R.drawable.cx_fa_hb_small_icon;
			dr_zone_icon_small_oppo=R.drawable.cx_fa_wf_small_icon;
			
			dr_calendar_clock_img_me=R.drawable.calendar_icon_clockblue;
			dr_calendar_clock_img_oppo=R.drawable.calendar_icon_clockpink;
//			dr_app_icon_oppo=R.drawable.cx_fa_role_app_icon;
			
		}	
	}
	
	
	
	
	
}
