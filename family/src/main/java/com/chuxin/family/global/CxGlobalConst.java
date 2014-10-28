
package com.chuxin.family.global;

import android.os.Environment;

import com.chuxin.family.BuildConfig;
import com.chuxin.family.app.CxApplication;

import java.io.File;

/**
 * 全局常量
 * 
 * @author shichao.wang
 */
public class CxGlobalConst {

    public static final String ACTION_TABLOID_RECIVER = "com.hmammon.family.tabloid.reciver";

    // 登录时需要检测的sharedpreference
    public static final String S_USER_LONGIN_PLATFORM = "platform";

    public static final String S_USER_FILE_NAME = "user_profile";

    public static final String S_SINA_PLATFORM = "weibo";

    public static final String S_TENCET_PLATFORM = "qq";

    /* 提醒模块常量 */
    public static final String S_REMINDER_PREFS_NAME = "lastReminder";

    public static final String S_REMINDER_RTS_KEY = "rts";

    public static final String S_REMINDER_FIRST_RTS_KEY = "firstRts";

    // 生日提醒
    public static final String S_BIRTHDAY_REMINDER_PREFS_NAME = "birthdayReminder";

    public static final String S_BIRTHDAY_REMINDER_ID = "reminderId";

    public static final String S_BIRTHDAY_REMINDER_FLAG = "reminderFlag";

//    public static final String S_INDEX_OR_SUGGEST_TAG = "isIndex";

    // 对话框
    public static final String S_DIALOG_INTENT = "dialogIntentType"; // 对话框用途

    // 图片缓存的根目录
    public static final String S_CHUXIN_IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "Android/data" + File.separator +CxApplication.getInstance().getPackageName()+ File.separator+ "images";

    // 音频文件缓存的根目录
    public static final String S_CHUXIN_AUDIO_CACHE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "Android/data" + File.separator +CxApplication.getInstance().getPackageName()+ File.separator+ "audios";

    public static final String S_CHUXIN_AUDIO_CACHE_NAME = "local";

    // 二人空间分享类型参数
    public static final String S_ZONE_SHARED_TYPE = "rkFeedType";

    public static final String S_ZONE_SHARED_IMAGE = "shareImage";

    public static final String S_ZONE_SELECTED_ORDER = "selectedOrder";

    public static final String S_ZONE_TITLE_MORE_BUTTTON = "titleMore";

    // 密邻添加帖子类型参数
    public static final String S_NEIGHBOUR_SHARED_TYPE = "nbInvitationType";

    public static final String S_NEIGHBOUR_SHARED_IMAGE = "nbInvitationImage";

    public static final String S_NEIGHBOUR_SELECTED_ORDER = "nbSelectedOrder";
    
    // kid添加帖子类型参数
    public static final String S_KID_SHARED_TYPE = "kidAddType";
    
    public static final String S_KID_SHARED_IMAGE = "kidAddImage";
    
    public static final String S_KID_SELECTED_ORDER = "kidSelectedOrder";

    // 密邻首页到HOME跳转的参数
    public static final String S_NEIGHBOUR_ID = "nbID";

    public static final String S_NEIGHBOUR_WIFE_NAME = "nbWifeName";

    public static final String S_NEIGHBOUR_HUSBAND_NAME = "nbHusbandName";

    public static final String S_NEIGHBOUR_HUSBAND_URL = "nbHusbandUrl";

    public static final String S_NEIGHBOUR_WIFE_URL = "nbWifeUrl";

    public static final String S_NEIGHBOUR_PAIR_ID = "nbPairId";

    // 锁屏
    public static final String S_LOCKSCREEN_NAME = "lockName";

    public static final String S_LOCKSCREEN_FIELD = "lockField";

    public static final String S_LOCKSCREEN_TYPE = "lockscreenType";

    // 地图
    public static final String S_LOCATION_LAT = "lat";

    public static final String S_LOCATION_LON = "lon";

    public static final String S_LOCATION_TEXT = "location";

    public static final String S_LOCATION_TYPE = "rkLocationType";

    // 聊天的声音，震动，听筒模式
    public static final String S_CHAT_NAME = "tableName";

    public static final String S_CHAT_SOUND = "chatSound";

    public static final String S_CHAT_SHOCK = "chatShock";

    public static final String S_CHAT_EARPHONE = "chatEarphone";

    // 结对账号
    public static final String S_PAIR_CH_NAME = "pairCharactorName";

    public static final String S_PAIR_CH_FIELD = "pairCharactorField";

    // 聊天界面LOG测试
//    public static boolean openFlg = false;

    // 表情文件存放地址
    public static final String S_CHAT_EMOTION = "chuxin" + File.separator + "emotion"
            + File.separator + "emotions";

    // 每天的毫秒时间
    public static final int S_DAY_TIME = 24 * 60 * 60 * 1000;

    public static final boolean DEVELOPER_MODE = BuildConfig.DEBUG;

    // 大图功能键
    public static final String S_STATE = "imagePagerState";

    //
    public static final String MULT_SELECT_IMAGE = "mult_images";

    // 日历
    public static final String S_CALENDAR_PREFS_NAME = "calendar";

    public static final String S_CALENDAR_REMIND_UPDATE_TIME = "remind_update_time";

    public static final String S_CALENDAR_CYCLE_UPDATE_TIME = "cycle_update_time";

}
