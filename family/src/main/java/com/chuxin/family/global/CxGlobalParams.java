package com.chuxin.family.global;

import android.text.TextUtils;

import com.chuxin.family.model.CxSubjectInterface;
import com.chuxin.family.parse.been.data.ChatBgData;
import com.chuxin.family.parse.been.data.CxPairInitData;

import java.util.List;
/**
 * 全局变量
 * @author shichao.wang
 *
 */
public class CxGlobalParams extends CxSubjectInterface{
	private static CxGlobalParams sGlobalParam;
	private String mVersionName = "family_husband";
	
	private String mClientVersion = "1.0.0";
	
	private float mScale = 2.0f;
	
	private String mCid = "chuxin";
	
//	public static JSONArray array;//表情配置文件的所有表情
	

//	public static void setArray(JSONArray array) {
//		RkGlobalParams.array = array;
//	}
	
//	private boolean genderFlag=true;
//	
//	
//	public boolean isGenderFlag() {
//		return genderFlag;
//	}
//
//	public void setGenderFlag(boolean genderFlag) {
//		this.genderFlag = genderFlag;
//	}

	private ChatBgData chatbgData;
	

	public ChatBgData getChatbgData() {
		return chatbgData;
	}

	public void setChatbgData(ChatBgData chatbgData) {
		this.chatbgData = chatbgData;
	}

	private String kAppNormal = null;

	private boolean kChatSound; //声音
	private boolean kChatShock; //振动
	private boolean kChatEarphone; //听筒模式

	public static final String IS_LOGIN = "isLogin";
	private boolean kIsLogin; //是否登录,true表示登录，false表示退出(2013.10.25修改了，要脱网进入）
	
	public static final String USER_ID = "userId";
	private String kUserId; //用户ID
	
	public static final String GENDER = "gender";
	private int kVersion = -1; //版本的性别。  男是1，女是0，开发版是-1
	
	public static final String PUSH_FLAG = "pushFlg";
	private boolean kPushFlag; //push 开关，true是开启，false关闭
	
	public static final String PAIR = "pair";
	private int kPair = 0; //结对标记（已结对为1，未结对为0）
	
	public static final String LOCK_FLAG = "lockFlag";
	private boolean kLockFlg; //锁屏标识。true是锁屏，false不锁屏
	
	public static final String ICON_MIDDLE = "iconMiddle";
	private String kIconMiddle; //中等头像的地址
	
	public static final String ICON_SMALL = "iconSmall";
	private String kIconSmall; //小头像的地址
	
	public static final String ICON_BIG = "iconBig";
	private String kIconBig; //大头像的地址
	
	public static final String PHONE = "phone";
	private String kPhone; //绑定的手机号
	
	public static final String APP_STATUS = "appStatus";
	private boolean kAppStatus = false; //应用的状态（前台还是后台），true为前台， false为后台
	
	public static final String PARTNER_ICON_BIG = "partnerIconBig";
	private String kPartnerIconBig; //与之结对者大头像
	
	public static final String PARTNER_GENDER = "partnerGender";
	private int kPartnerGender; //版本性别 （即：与当前用户结对的人的性别 ）。 男是0，女是1

	public static final String PARTNER_PHONE = "partnerPhoneNumber";
	private String kPartnerPhoneNumber; //对方的电话号码
	
	public static final String PARTNER_ID = "partnerId";
	private String kPartnerId; //与之结对者ID
	
	public static final String PARTNER_NAME = "partnerName";
	private String kPartnerName="";
	
	public static final String PAIR_ID = "pairId";
	private String kPairId; //本次两人结对的对号
	
	private List<CxPairInitData> kInviteMePair; //邀请当前用户结对的信息（不需要观察者模式）
	
	public static final String CHAT_BIG= "bigChatBackground";
	private String kChatBackgroundBig; //聊天背景大图
	
	public static final String CHAT_SMALL= "smallChatBackground";
	private String kChatBackgroundSmall; //聊天背景小图
	
	private boolean sDismissPair; //主动解除结对(不需要监听者模式）
	
	public static final String ZONE_BACKGROUND = "zoneBackground";
	private String kZoneBackground; //二人空间背景图
	
	private boolean kIsCallGpuimage = false; //默认没有调用gpuimage
	
	private CxGlobalParams(){}
	
	private boolean kRecorderFlag = false; //录音
	
	public static final String HAVE_TABLOID_MSG = "tabloidMsg";
	private boolean kHaveTabloidMsg = false;		// 是否有小报信息(小报信息是不走服务器的，直接由本地发送)
	
	public static final String GROUP = "group";
	private int kGroup; // 密邻未读消息数
	
	public static final String SPACE_TIPS = "space_tips";		// 二人空间未读的消息数
	private int kSpaceTips;
	
//	private boolean kUnBindSelf = false; //是否是自己主动接触绑定关系
	
	public static final String TOGETHER_DAY = "togetherDay";
	private String kTogetherDayStr; //在一起的天数
	
	private int kHeight = 0; //手机屏高

	private int kWidth = 0; //手机屏宽
	
	private int kPushSoundType = 0; //默认是调皮（老婆版）或三弦音（老公版）。共3种值，0（调皮或三弦音）、1（老公或老婆）、2（系统通知音）
	
	private boolean kLoginNetSuccess; //是否是登录成功后进入主界面（2013-11-13）
	
	private int kSmallImgConner; //自己的小头像圆角大小

	private int kMateSmallImgConner; //对方的小头像圆角大小
	
	private String kGroup_show_id; //用来显示的密邻ID
	
	public static final String FAMILY_BIG = "family_big"; //家庭头像
	private String kFamily_big;
	
	public static final String SINGLE_MODE = "single_mode"; //家庭头像
	private int kSingle_mode;
	
	public static final String VERSION_TYPE="version_type";//版本 二人版 或亲子版
	private int kVersion_type;
	
	public static final String KID_TIPS="kid_tips";//版本 二人版 或亲子版
	private int kid_tips;
	
	public int loginType=0; // 0 ，第一次登录模式; 1 先进入模式。
	
	public static final String CHANGE_MENU="change_menu";
	private int kChangeMenu;
	
	
	
	
	public int getChangeMenu() {
		return kChangeMenu;
	}

	public void setChangeMenu(int changeMenu) {
		this.kChangeMenu = changeMenu;
		notifyObserver(CHANGE_MENU);
	}

	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	public int getKid_tips() {
		return kid_tips;
	}

	public void setKid_tips(int kid_tips) {
		this.kid_tips = kid_tips;
		notifyObserver(KID_TIPS);
	}

	public int getVersion_type() {
		return kVersion_type;
	}

	public void setVersion_type(int version_type) {
		if(this.kVersion_type == version_type){
			return;
		}
		this.kVersion_type = version_type;
		notifyObserver(VERSION_TYPE);
	}

	public int getSingle_mode() {
		return kSingle_mode;
	}

	public void setSingle_mode(int single_mode) {
		if(this.kSingle_mode==single_mode){
			return ;
		}
		this.kSingle_mode = single_mode;
		notifyObserver(SINGLE_MODE);
	}

	public String getFamily_big() {
		return kFamily_big;
	}

	public void setFamily_big(String family_big) {
		this.kFamily_big = family_big;
		notifyObserver(FAMILY_BIG);
	}

	public String getGroup_show_id() {
		return kGroup_show_id;
	}

	public void setGroup_show_id(String group_show_id) {
		this.kGroup_show_id = group_show_id;
	}

	public int getSmallImgConner() {
		return kSmallImgConner;
	}

	public void setSmallImgConner(int smallImgConner) {
		this.kSmallImgConner = smallImgConner;
	}
	
	public int getMateSmallImgConner() {
		return kMateSmallImgConner;
	}

	public void setMateSmallImgConner(int mateSmallImgConner) {
		this.kMateSmallImgConner = mateSmallImgConner;
	}
	
	public boolean isLoginNetSuccess() {
		return kLoginNetSuccess;
	}

	public void setLoginNetSuccess(boolean loginNetSuccess) {
		this.kLoginNetSuccess = loginNetSuccess;
	}

	public int getPushSoundType() {
		return kPushSoundType;
	}

	public void setPushSoundType(int pushSoundType) {
		this.kPushSoundType = pushSoundType;
	}

	public int getHeight() {
		return kHeight;
	}

	public void setHeight(int kHeight) {
		this.kHeight = kHeight;
	}

	public int getWidth() {
		return kWidth;
	}

	public void setWidth(int kWidth) {
		this.kWidth = kWidth;
	}

//	public boolean isUnBindSelf() {
//		return kUnBindSelf;
//	}
//
//	public void setUnBindSelf(boolean unBindSelf) {
//		this.kUnBindSelf = unBindSelf;
//	}

	public String getTogetherDayStr() {
		return kTogetherDayStr;
	}

	public void setTogetherDayStr(String togetherDayStr) {
		if (TextUtils.equals(togetherDayStr, kTogetherDayStr)) {
			return;
		}
		this.kTogetherDayStr = togetherDayStr;
		notifyObserver(TOGETHER_DAY);
	}

	private long kUpdateTaskID = -1; //版本更新时的任务ID
	
	public void setHaveTabloidMsg(boolean tabloidMsgTag){
		this.kHaveTabloidMsg = tabloidMsgTag;

		notifyObserver(HAVE_TABLOID_MSG);
	}
	
	public boolean getHaveTabloidMsg(){
		return this.kHaveTabloidMsg;
	}
	
	public long getUpdateTaskID() {
		return kUpdateTaskID;
	}

	public void setUpdateTaskID(long updateTaskID) {
		this.kUpdateTaskID = updateTaskID;
	}

	public boolean isRecorderFlag() {
		return kRecorderFlag;
	}

	public void setRecorderFlag(boolean recorderFlag) {
		this.kRecorderFlag = recorderFlag;
	}

	//	private int mProjectCode = 0; // 0 for wife, 1 for husband
	/*实例化*/
	public static CxGlobalParams getInstance(){
		if (null == sGlobalParam) {
			sGlobalParam = new CxGlobalParams();
		}
		return sGlobalParam;
	}

	public boolean isLogin() {
		return kIsLogin;
	}

	public synchronized void setIsLogin(boolean isLogin) {
//		if (isLogin == kIsLogin) { //避免单个界面因此标识重复刷新
//			return;
//		}
		this.kIsLogin = isLogin;
		notifyObserver(CxGlobalParams.IS_LOGIN);
	}

	public String getUserId() {
		return kUserId;
	}

	public void setUserId(String usrId) {
		this.kUserId = usrId;
		notifyObserver(CxGlobalParams.USER_ID);
	}

	public int getVersion() {
		return kVersion;
	}

	public void setVersion(int version) {
		if(this.kVersion == version){
			return ;
		}
		this.kVersion = version;
		notifyObserver(CxGlobalParams.GENDER);
	}

	public boolean isPushFlag() {
		return kPushFlag;
	}

	public void setPushFlag(boolean pushFlag) {
		this.kPushFlag = pushFlag;
		notifyObserver(CxGlobalParams.PUSH_FLAG);
	}

	public int getPair() {
		return kPair;
	}

	public synchronized void setPair(int pair) {
		if (kPair == pair) {
			return;
		}
		this.kPair = pair;
		notifyObserver(CxGlobalParams.PAIR);
	}

	public boolean isLockFlg() {
		return kLockFlg;
	}

	public void setLockFlg(boolean lockFlg) {
		this.kLockFlg = lockFlg;
//		notifyObserver(RkGlobalParams.LOCK_FLAG); //锁屏暂时不通知
	}

	public String getIconMid() {
		return kIconMiddle;
	}

	public void setIconMid(String iconMiddle) {
		this.kIconMiddle = iconMiddle;
		notifyObserver(CxGlobalParams.ICON_MIDDLE);
	}

	public String getIconSmall() {
		if(kIconSmall != null && kIconSmall.length() > 0){
			return kIconSmall;
		} else {
			return null; //
		}
	}

	public void setIconSmall(String iconSmall) {
		if (TextUtils.equals(kIconSmall, iconSmall)) {
			return;
		}
		this.kIconSmall = iconSmall;
		notifyObserver(CxGlobalParams.ICON_SMALL);
	}

	public String getIconBig() {
		return kIconBig;
	}

	public void setIconBig(String iconBig) {
		if (TextUtils.equals(kIconBig, iconBig)) {
			return;
		}
		this.kIconBig = iconBig;
		notifyObserver(CxGlobalParams.ICON_BIG);
	}

	public String getPhone() {
		return kPhone;
	}

	public void setPhone(String phone) {
		this.kPhone = phone;
		notifyObserver(CxGlobalParams.PHONE);
	}

	public boolean isAppStatus() {
		return kAppStatus;
	}

	public void setAppStatus(boolean appStatus) {
		this.kAppStatus = appStatus;
		notifyObserver(CxGlobalParams.APP_STATUS);
	}

	public String getPartnerIconBig() {
		return kPartnerIconBig;
	}

	public void setPartnerIconBig(String partnerIconBig) {
		if (TextUtils.equals(partnerIconBig, kPartnerIconBig)) {
			return;
		}
		this.kPartnerIconBig = partnerIconBig;
		notifyObserver(CxGlobalParams.PARTNER_ICON_BIG);
	}
	
	public int getPartnerGender() {
		return kPartnerGender;
	}

	public void setPartnerGender(int partnerGender) {
		this.kPartnerGender = partnerGender;
		notifyObserver(CxGlobalParams.PARTNER_GENDER);
	}

	public String getPartnerPhoneNumber() {
		return kPartnerPhoneNumber;
	}

	public void setPartnerPhoneNumber(String partnerPhoneNumber) {
		this.kPartnerPhoneNumber = partnerPhoneNumber;
		notifyObserver(CxGlobalParams.PARTNER_PHONE);
	}

	public String getPartnerId() {
		return kPartnerId;
	}
	
	public void setPartnerId(String partnerId) {
		this.kPartnerId = partnerId;
		notifyObserver(CxGlobalParams.PARTNER_ID);
	}
	
	public String getPairId() {
		return kPairId;
	}
	
	public void setPairId(String pairId) {
		this.kPairId = pairId;
		notifyObserver(CxGlobalParams.PAIR_ID);
	}
	
	public List<CxPairInitData> getInviteMePair() {
		return kInviteMePair;
	}
	
	public void setInviteMePair(List<CxPairInitData> inviteMePair) {
		this.kInviteMePair = inviteMePair; //不设置观察者模式
	}
   
    public String getChatBackgroundBig() {
		return kChatBackgroundBig;
	}

	public void setChatBackgroundBig(String chatBackgroundBig) {
		this.kChatBackgroundBig = chatBackgroundBig;
		notifyObserver(CHAT_BIG);
	}
	
	public String getChatBackgroundSmall() {
		return kChatBackgroundSmall;
	}

	public void setChatBackgroundSmall(String chatBackgroundSmall) {
		this.kChatBackgroundSmall = chatBackgroundSmall;
		notifyObserver(CHAT_SMALL);
	}

	public boolean isDismissPair() {
		return sDismissPair;
	}

	public void setDismissPair(boolean dismissPair) {
		this.sDismissPair = dismissPair;
	}
	
	public String getZoneBackground() {
		return kZoneBackground;
	}

	public void setZoneBackground(String zoneBackground) {
		if (TextUtils.equals(zoneBackground, kZoneBackground)) {
			return;
		}
		this.kZoneBackground = zoneBackground;
		notifyObserver(ZONE_BACKGROUND);
	}

	public String getPartnerName() {
		return kPartnerName;
	}

	public void setPartnerName(String partnerName) {
		if (TextUtils.equals(kPartnerName, partnerName)) {
			return;
		}
		this.kPartnerName = partnerName;
		notifyObserver(PARTNER_NAME);
	}
	
	public boolean isCallGpuimage() {
		return kIsCallGpuimage;
	}

	public void setCallGpuimage(boolean isCallGpuimage) {
		this.kIsCallGpuimage = isCallGpuimage;
	}
	
	public boolean isAppNormal() {
        return !TextUtils.isEmpty(kAppNormal);
    }

	public void setAppNormal(String appNormal) {
		kAppNormal = appNormal;
	}
	
	public boolean isChatSound() {
		return kChatSound;
	}

	public void setChatSound(boolean chatSound) {
		this.kChatSound = chatSound;
	}

	public boolean isChatShock() {
		return kChatShock;
	}

	public void setChatShock(boolean chatShock) {
		this.kChatShock = chatShock;
	}

	public boolean isChatEarphone() {
		return kChatEarphone;
	}

	public void setChatEarphone(boolean chatEarphone) {
		this.kChatEarphone = chatEarphone;
	}
	
	public String getVersionName() {
		return mVersionName;
	}

	public void setVersionName(String versionName) {
		this.mVersionName = versionName;
	}
	
	public String getClientVersion() {
		return mClientVersion;
	}

	public void setClientVersion(String clientVersion) {
		mClientVersion = clientVersion;
	}
	
	public float getScale() {
		return mScale;
	}

	public void setScale(float scale) {
		this.mScale = scale;
	}
	
	public String getCid() {
		return mCid;
	}

	public void setCid(String cid) {
		this.mCid = cid;
	}
	
	public int getGroup() {
		return kGroup;
	}

	public void setGroup(int group) {
		this.kGroup = group;
		notifyObserver(CxGlobalParams.GROUP);
	}
	
	public int getSpaceTips() {
		return kSpaceTips;
	}

	public void setSpaceTips(int space_tips) {
		this.kSpaceTips = space_tips;
		notifyObserver(CxGlobalParams.SPACE_TIPS);
	}
}
