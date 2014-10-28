package com.chuxin.family.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.BuildConfig;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxLogin;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.resource.CxResource;
import com.chuxin.family.utils.Push;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxUserProfileKeeper;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.login.Oauth2AccessToken;
import com.chuxin.family.views.login.CxThirdAccessToken;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;
import com.chuxin.family.R;
import com.chuxin.androidpush.sdk.push.RKPush;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.tencent.stat.StatService;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.utils.HttpUtils.HttpStatusException;
import com.tencent.utils.HttpUtils.NetworkUnavailableException;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.HttpManager;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.util.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 应用首界面和帮助界面，注意界面的复用
 *
 * @author shichao.wang
 *         <p/>
 *         <p/>
 *         <p/>
 *         这次改动较大  如果本地有token和自己及对方的信息则直接跳入主页面 再登录   否则 （第一次登录及清除了数据）在本类中完成注册登录流程再跳转。
 *         <p/>
 *         2014.3.21  by wentong.men
 */
@SuppressLint("HandlerLeak")
public class CxAuthenNew extends FragmentActivity {

    ArrayList<ImageView> mIndicators = new ArrayList<ImageView>();

    public static Tencent mTencent;
    private final String TENCENT_APP_ID = "100360393";
    //	private final String TENCENT_APP_ID = "101001735";
    public final static String TENCENT_APP_SCOPE = "get_simple_userinfo,get_user_info";
    public static Weibo mWeibo;
    public final String WEIBO_APP_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public final String WEIBO_APP_KEY = "3126932333";
    public final String WEIBO_GET_USER_PROFILE = "https://api.weibo.com/2/users/show.json";


    //	public static final String WECHAT_APP_ID = "wx34c2b06b3b1b1ea2"; //微信平台的应用id
    public static final String WECHAT_APP_ID = "wx3bcb2451a7939474"; //微信平台的应用id
    public static IWXAPI api;

    //	private RKPush mRKPush = null;
    public static Handler mAuthenHandler;
    public static final int AutoLogin = 1;
    public static final int FetchMyInfo = 2;

    private boolean isInitial = false;

    private FrameLayout /*hasAuthenLayout, */mUnusedLoginlayout;
//            mUsedLoginLayout;
    private LoadingState mLoadingManager;

    private LinearLayout loginLayout;

    private static boolean mStatus = false; //此界面是否开启


    public static void setStatus(boolean status) {
        CxAuthenNew.mStatus = status;
    }

    public static boolean isShown() {
        return mStatus;
    }

    @Override
    protected void onResume() {
        StatService.onResume(CxAuthenNew.this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatService.onPause(CxAuthenNew.this);
        super.onPause();
    }

    private void initMat() {
        StatConfig.setDebugEnable(false);//关闭logcat
        StatConfig.setAutoExceptionCaught(true);
        StatConfig.setStatSendStrategy(StatReportStrategy.INSTANT);
        StatConfig.setSessionTimoutMillis(1800000); //30*60*1000
        StatConfig.setEnableStatService(true);
        StatConfig.setMaxSendRetryCount(3);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStatus = true;
        super.onCreate(savedInstanceState);

        if (CxGlobalParams.getInstance().isLogin()
                && CxGlobalParams.getInstance().isAppNormal()) {
            turnToMainPage(true, true, 1);
            return;
        }

        mAuthenHandler = new Handler(getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AutoLogin:
                        autoLogin();
                        break;
                    case FetchMyInfo:
                        CxUserProfileDataField profile = (CxUserProfileDataField) msg.obj;
                        fetchMyInfo(profile, "登录失败");
                        break;

                    default:
                        break;
                }
            }
        };


        setContentView(R.layout.cx_fa_activity_authen_new);

        mTencent = Tencent.createInstance(TENCENT_APP_ID, getApplicationContext());
        mWeibo = Weibo.getInstance(WEIBO_APP_KEY, WEIBO_APP_REDIRECT_URL);

        mUnusedLoginlayout = (FrameLayout) findViewById(R.id.cx_fa_unused_login);
//        mUsedLoginLayout = (FrameLayout) findViewById(R.id.cx_fa_used_login);
        loginLayout = (LinearLayout) findViewById(R.id.rk_login_layer);
        loginLayout.setVisibility(View.GONE);

        channelLogo = (ImageView) findViewById(R.id.cx_fa_main_channel_logo_iv);
        channelLogo.setVisibility(View.GONE);
//        channelLogo.setVisibility(View.VISIBLE);
        LinearLayout mQQButton = (LinearLayout) findViewById(R.id.rk_login_qq);
        LinearLayout mWeiboButton = (LinearLayout) findViewById(R.id.rk_login_weibo);
        LinearLayout mXiaojiaButton = (LinearLayout) findViewById(R.id.rk_login_by_xiaojia);
        mQQButton.setOnClickListener(mButtonListener);
        mWeiboButton.setOnClickListener(mButtonListener);
        mXiaojiaButton.setOnClickListener(mButtonListener);

        ImageView mIndicator1 = (ImageView) findViewById(R.id.first_help_indicator);
        ImageView mIndicator2 = (ImageView) findViewById(R.id.second_help_indicator);
        ImageView mIndicator3 = (ImageView) findViewById(R.id.third_help_indicator);
        ImageView mIndicator4 = (ImageView) findViewById(R.id.forth_help_indicator);
        ImageView mIndicator5 = (ImageView) findViewById(R.id.fifth_help_indicator);
        ImageView mIndicator6 = (ImageView) findViewById(R.id.sixth_help_indicator);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.help_indicator_dots);
//		mIndicator5 = (ImageView)findViewById(R.id.fifth_help_indicator);
        mIndicator1.setImageResource(R.drawable.cx_fa_main_guide_dot_focused);
        mIndicators.add(mIndicator1);
        mIndicators.add(mIndicator2);
        mIndicators.add(mIndicator3);
        mIndicators.add(mIndicator4);
        mIndicators.add(mIndicator5);
        mIndicators.add(mIndicator6);

        ViewPager mFlyView = (ViewPager) findViewById(R.id.guider_viewpager);
        mFlyView.setAdapter(new GuiderPagerAdapter(CxAuthenNew.this.getSupportFragmentManager()));
        mFlyView.setOnPageChangeListener(mPageChange);

        mLoadingManager = new LoadingState();

        //发送激活报告chuxin
        new ReportActiveTask().execute();

        //加入腾讯云统计
        initMat();
        wechatRegist();


        firstUseLoad();


    }

    private void wechatRegist() {
        api = WXAPIFactory.createWXAPI(CxAuthenNew.this, WECHAT_APP_ID, true);
        //注册应用app—id到微信
        api.registerApp(WECHAT_APP_ID);
    }

    /**
     * 是否第一次登录  是则出现引导页 不是则停顿1秒后进入loading页
     */
    private void firstUseLoad() {
        SharedPreferences sp = getSharedPreferences("updateflag", Context.MODE_PRIVATE);

        CxLog.i("CxAuthenNew_men", sp.getBoolean("guide", false) + "");
        //是否需要开启新手引导页
        if (!sp.getBoolean("guide", false)) {
            channelLogo.setVisibility(View.GONE);
            mUnusedLoginlayout.setVisibility(View.VISIBLE); //新手引导可见
            loginLayout.setVisibility(View.VISIBLE); //登录按钮栏可见
            //与此同时生成设置界面“收到聊天消息”的各项默认值
            SharedPreferences chatSp = getSharedPreferences(
                    CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
            Editor chatEdit = chatSp.edit();
            chatEdit.putBoolean(CxGlobalConst.S_CHAT_SOUND, true);
            chatEdit.putBoolean(CxGlobalConst.S_CHAT_SHOCK, false);
            chatEdit.putBoolean(CxGlobalConst.S_CHAT_EARPHONE, false);
            chatEdit.apply();
        } else { //不是第一次

            new Handler() {
                public void handleMessage(Message msg) {
                    initView();
                }
            }.sendEmptyMessageDelayed(1, 1000);
        }
    }

    /**
     * 判断本地是否有localToken  没有则出现登录按钮  有则直接进入主页面  一般来说 有token 也必然有个人信息  但为防万一可在turnToMainPage再次判断。
     */
    private void initView() {

        //以下是未登录状况，可能出现2种情况：a、本地无账号；b、本地有账号
        CxThirdAccessToken localToken =
                CxThirdAccessTokenKeeper.readAccessToken(CxAuthenNew.this);

        CxLog.i("CxAuthenNew_men", "localToken" + CxThirdAccessTokenKeeper.isEmpty(localToken) + "");
        //对于情况a，处理如下:
        if (CxThirdAccessTokenKeeper.isEmpty(localToken)) { //对于本地无账号的情况，显示登录按钮栏即可
            channelLogo.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            return;
        }

        SharedPreferences pref = CxAuthenNew.this.getSharedPreferences("profile_name", Context.MODE_APPEND);
        String userId = pref.getString("user_id", null);
        if (TextUtils.isEmpty(userId)) {
            channelLogo.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            return;
        }

        int gender = pref.getInt("gender", -1);
        if (gender == -1) {
            autoLogin();
            return;
        }

        int version_type = pref.getInt("version_type", 0);
        if (version_type == 0) {
            autoLogin();
            return;
        }


        turnToMainPage(false, false, 1);

    }

    //点击登录
    OnClickListener mButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rk_login_qq: //QQ登录
                    CxLog.i(BuildConfig.PACKAGE_NAME,"QQLogin-Start");
                    isInitial = false;
                    mTencent.login(CxAuthenNew.this, TENCENT_APP_SCOPE, loginQQListener);
                    mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN);
                    break;
                case R.id.rk_login_weibo: //微博登录
                    isInitial = false;
                    mWeibo.authorize(CxAuthenNew.this, new AuthDialogListener());
                    break;
                case R.id.rk_login_by_xiaojia: //小家登录
                    Intent it = new Intent(CxAuthenNew.this, CxLoginByFamily.class);
                    startActivity(it);
                    overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                    break;
                default:
                    break;
            }

        }
    };

    OnPageChangeListener mPageChange = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            setSelectedFocus(arg0);

        }

        private void setSelectedFocus(int position) {
            for (int i = 0; i < mIndicators.size(); i++) {
                if (i == position) {
                    mIndicators.get(i).setImageResource(R.drawable.cx_fa_main_guide_dot_focused);
                    continue;
                }
                mIndicators.get(i).setImageResource(R.drawable.cx_fa_main_guide_dot_normal);
            }

            if (position == 6) {
                mIndicatorLayout.setVisibility(View.GONE);
                new Handler(getMainLooper()) {
                    public void handleMessage(android.os.Message msg) {
                        completeGuider();
                    }
                }.sendEmptyMessageDelayed(1, 1000);
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    //------------------------------------------------------
    //本地有第三方授权信息时候自动登录chuxin
    private void autoLogin() {
        CxThirdAccessToken localToken =
                CxThirdAccessTokenKeeper.readAccessToken(CxAuthenNew.this);
        if (!CxThirdAccessTokenKeeper.isEmpty(localToken)) { //本地有记录
            //不管是本地有账号自动登录还是授权回来登录，都显示首次加载的界面
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN);

            if (CxGlobalConst.S_SINA_PLATFORM.equals(localToken.getPlatName())) {
                CxLog.i("", "use weibo login ");

                loginWeibo();
                return;
            }
            if (CxGlobalConst.S_TENCET_PLATFORM.equals(localToken.getPlatName())) {
                CxLog.i("", "use qq login ");
                loginQQ();
                return;
            }

            if ("email".equals(localToken.getPlatName())) {
                CxLog.i("", "use xiaojia login ");
                loginFamily();
            }

        } else { //基本不会出现：（本机未登录过）因为前面已经处理了本地无账号的情况
            CxLog.i("no automatic login", "---------------");
//			mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
            //本地没有账号的情况,不作处理
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
        }

    }

    private void loginFamily() {
        CxThirdAccessToken localToken =
                CxThirdAccessTokenKeeper.readAccessToken(CxAuthenNew.this);
        doLoginChuxin(localToken.getPlatName(),
                localToken.getUid(), localToken.getToken(), loginCaller);
    }

    JSONCaller loginCaller = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) { // 属于异常情况  基本不可能
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);

//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return -1;
            }
            // 检测rc,如果rc==2000表明授权过期，要求弹出相应的第三方授权界面
            CxLogin loginResult;
            loginResult = (CxLogin) result;

            if (0 != loginResult.getRc()) { // 异常
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return loginResult.getRc();
            }

            if ((null == loginResult.getData()) || (TextUtils.isEmpty(loginResult.getData().getUid()))) {
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return -100;
            }
            // 正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面

            SharedPreferences preference = getSharedPreferences(CxGlobalConst.S_USER_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preference.edit();
            edit.putString(CxGlobalConst.S_USER_LONGIN_PLATFORM, "email");
            edit.commit();

            fetchMyInfo(loginResult.getData(), loginResult.getMsg());
            return 0;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // must call mTencent.onActivityResult.
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    /* 登录chuxin服务器 */
    public static void doLoginChuxin(String via, String opendId, String token,
                                     JSONCaller platformloginRkCallback) {

        try {
            int gender = -1; //用户资料，性别 （可选） 0：男性 1：女性 -1：未设置(2013.09.18余志伟说文档和协议反了，android改）
            AccountApi.getInstance().doLogin(via, opendId, token,
                    gender, platformloginRkCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* QQ登录 */
    public void loginQQ() {
        if (mTencent == null) {
            CxLog.d("", "mTencent is null");
//			openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
            if (isInitial) {
                mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
            }

            return;
        }

        CxThirdAccessToken localToken =
                CxThirdAccessTokenKeeper.readAccessToken(CxAuthenNew.this);
        if (null == localToken) {
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_QQ);
            mLoadingManager.sendEmptyMessage(LOGIN_QQ);
            return;
        }
        if ((null == localToken.getToken()) //QQ不返回有效期，所以不用验证有效性
                || (null == localToken.getUid())) {
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_QQ);
            mLoadingManager.sendEmptyMessage(LOGIN_QQ);
            return;
        }

        mTencent.setAccessToken(localToken.getToken(), localToken.getExpiresTime());
        mTencent.setOpenId(localToken.getUid());
        if (mTencent.isSessionValid()) { //有效
            mLoadingManager.sendEmptyMessage(DISPLAY_LOADING);
            doLoginChuxin(CxGlobalConst.S_TENCET_PLATFORM,
                    localToken.getUid(), localToken.getToken(), tencentCallback);
        } else { //无效就登录QQ
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_QQ);
            mLoadingManager.sendEmptyMessage(LOGIN_QQ);
        }

    }

    //QQ授权回调
    IUiListener loginQQListener = new IUiListener() {
        //凡是这个授权回调的，都要经过chuxin的登录验证
        @Override
        public void onCancel() {
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
            CxLog.i("", "cancel qq login");
        }

        @Override
        public void onComplete(Object arg0) {
            CxLog.i("", "logint TENCET COME BACK :" + arg0);

            if (null == arg0) {
                //提示出错
                mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_qq_authen_fail);
                new DisplayAlertInfo().sendMessage(msg);
                return;
            }

            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN);

            JSONObject obj = null;
            try {
                obj = (JSONObject) arg0;
            } catch (Exception ignored) {

            }

            //本地缓存
            try {
                CxThirdAccessToken localToken = new CxThirdAccessToken(
                        obj.getString("access_token"), obj.getString("openid"),
                        obj.getString("expires_in"), CxGlobalConst.S_TENCET_PLATFORM);
                CxThirdAccessTokenKeeper.keepAccessToken(CxAuthenNew.this, localToken);
            } catch (JSONException e) {
                e.printStackTrace();
                CxLog.e("---------", "save third token fail");
            }
            try {
                CxLog.i("RkAuthenNew_men", obj.getString("access_token") + ">>>>>>>" + obj.getString("openid") + ">>>>>" + obj.getString("expires_in"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // QQ登录授权成功后登录chuxin服务器
            doLoginChuxin("qq", mTencent.getOpenId(),
                    mTencent.getAccessToken(), tencentCallback);
        }

        @Override
        public void onError(UiError e) {
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
            Message msg = new Message();
            msg.obj = e.errorMessage;
            new DisplayAlertInfo().sendMessage(msg);

        }

    };

    /* 获取QQ用户的资料 */
    public void getQQUserInfo() {
        Bundle params = new Bundle();
        params.putString(Constants.PARAM_ACCESS_TOKEN, mTencent.getAccessToken());
        params.putString(Constants.PARAM_CONSUMER_KEY, TENCENT_APP_ID);
        params.putString(Constants.PARAM_OPEN_ID, mTencent.getOpenId());
        CxLog.i("", "params for qq get user profile:" + Constants.PARAM_ACCESS_TOKEN + "="
                + mTencent.getAccessToken() + ", " + Constants.PARAM_CONSUMER_KEY + "="
                + TENCENT_APP_ID + "," + Constants.PARAM_OPEN_ID + "=" + mTencent.getOpenId());

        mTencent.requestAsync("user/get_user_info", params,
                Constants.HTTP_GET, new IRequestListener() {

                    @Override
                    public void onComplete(JSONObject response) {
                        CxLog.i("onComplete:", "");
                        //获取完成用户资料，就要去chuxin注册
                        if (null == response) {
                            // 提示用户资料获取失败
                            Message msg = new Message();
                            msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                            new DisplayAlertInfo().sendMessage(msg);

//							openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                            mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
                            return;
                        }
                        int ret = -1;
                        try {
                            ret = response.getInt("ret");
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                        if (-1 == ret) {
                            //QQ资料获取失败
                            Message msg = new Message();
                            msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                            new DisplayAlertInfo().sendMessage(msg);
//							openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                            mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
                            return;
                        }
                        String headStr = null;
                        try {
                            headStr = response.getString("figureurl_qq_1");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        String nickName = null;
                        try {
                            nickName = response.getString("nickname");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        int genderInt = -1; //QQ默认返回"男"

                        try {
                            AccountApi.getInstance().doRegister("qq", mTencent.getOpenId(),
                                    mTencent.getAccessToken(),
                                    nickName,
                                    "" + genderInt, //0：男性 1：女性 -1：未设置
                                    null/*sina微博没有生日这个字段返回*/,
                                    null/*暂时没有client_version*/,
                                    "zh_cn",
                                    headStr, regiseCallback);
                        } catch (Exception e) {
                            Message msg = new Message();
                            msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                            new DisplayAlertInfo().sendMessage(msg);
//							openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                            mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectTimeoutException(
                            ConnectTimeoutException arg0) {
                        Log.d("onConnectTimeoutException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onHttpStatusException(HttpStatusException arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onIOException(IOException arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onJSONException(JSONException arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onMalformedURLException(
                            MalformedURLException arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onNetworkUnavailableException(
                            NetworkUnavailableException arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onSocketTimeoutException(
                            SocketTimeoutException arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }

                    @Override
                    public void onUnknowException(Exception arg0) {
                        Log.d("onHttpStatusException:", arg0.toString());
                        Message msg = new Message();
                        msg.obj = getString(R.string.cx_fa_obtain_profile_fail);
                        new DisplayAlertInfo().sendMessage(msg);
//						openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

                    }


                }, null);
    }


    /**
     * @param hasToken         0  没有  1  有
     */
    public void turnToMainPage(boolean exist, boolean isNetworkSuccess, int hasToken) {
        CxLoadingUtil.getInstance().dismissLoading();
        if (!isNetworkSuccess) { //脱网进入之前要把所有用户数据读入内存
            new CxUserProfileKeeper().readProfile(CxAuthenNew.this);
            CxGlobalParams.getInstance().setLoginNetSuccess(false);
        } else {
            CxGlobalParams.getInstance().setLoginNetSuccess(true);
        }

        //加载聊天的三种模式设置
        SharedPreferences chatSp = getSharedPreferences(CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
        boolean chatSound = chatSp.getBoolean(CxGlobalConst.S_CHAT_SOUND, true);
        boolean chatShock = chatSp.getBoolean(CxGlobalConst.S_CHAT_SHOCK, false);
        boolean chatEarphone = chatSp.getBoolean(CxGlobalConst.S_CHAT_EARPHONE, false);
        CxGlobalParams.getInstance().setChatSound(chatSound);
        CxGlobalParams.getInstance().setChatShock(chatShock);
        CxGlobalParams.getInstance().setChatEarphone(chatEarphone);


        //屏蔽push
        RKPush.S_SEND_FLAG = false;
        CxLog.i("CxAuthenNew_men", ">>>>>>>>>>>>>>3" + CxGlobalParams.getInstance().getVersion());
        if (-1 == CxGlobalParams.getInstance().getVersion()) {
            Intent it = new Intent(CxAuthenNew.this, CxAuthenGenderSelectorActivity.class);
            it.putExtra("exist", exist);
            startActivity(it);
            overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
            finish();
            return;
        }

        int version = CxGlobalParams.getInstance().getVersion();
        if (version == 0) {
            CxResource.getInstance().setType(false);
        } else {
            CxResource.getInstance().setType(true);
        }

        if (0 == CxGlobalParams.getInstance().getVersion_type()) {
            Intent it = new Intent(CxAuthenNew.this, CxAuthenChildrenSelectorActivity.class);
            it.putExtra("exist", exist);
            startActivity(it);
            overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
            finish();
            return;
        }


        CxLog.i("CxAuthenNew_men", ">>>>>>>>>>>>>>4");
        Intent it = new Intent(CxAuthenNew.this, CxMain.class);
        it.putExtra("exist", exist);
        it.putExtra("hasToken", hasToken);
        startActivity(it);
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
        finish();
    }

    /*获取用户在chuxin上的资料*/
    public void fetchMyInfo(CxUserProfileDataField profile, String message) {
        UserApi userApi = UserApi.getInstance();
        try {
            RKPush push = Push.getInstance(getApplicationContext());
            String deviceToken = push.registerForRemoteNotification();

            userApi.updateDeviceToken("android", deviceToken, new ConnectionManager.JSONCaller() {

                @Override
                public int call(Object obj) {
                    return 0;
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
            CxLog.w("", "" + e1.toString());
        }

        //获取成功,初始化用户信息，之后就进入主界面

        if (null == profile) {
            Message msg = new Message();
            msg.obj = message;
            new DisplayAlertInfo().sendMessage(msg);

//			mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
            //失败也跳到主界面(2013-10-24 脱网进入)
            turnToMainPage(false, false, 0);
            return;
        }

        String tempMateId = profile.getPartner_id();
        if (!TextUtils.isEmpty(tempMateId)) { //结对了就要获取对方的资料
            //如果结对且伴侣UID不为空，就要同时开启线程去获取伴侣资料
            UserApi.getInstance().getUserPartnerProfile(userMateProfileCallback);
        }
        //存储自己的资料信息
        CxUserProfileKeeper profileKeeper = new CxUserProfileKeeper();
        profileKeeper.saveProfile(profile, CxAuthenNew.this);

        mLoadingManager.sendEmptyMessage(DISMISS_LOADING);

        turnToMainPage(false, true, 0); //转入主界面


//		userApi.getUserProfile(uid, new ConnectionManager.JSONCaller() {
//
//			@Override
//			public int call(Object data) {
//				if (null == data) {
//					Message msg = new Message();
//					msg.obj = getString(R.string.cx_fa_net_err);
//					new DisplayAlertInfo().sendMessage(msg);
////					mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
//					//失败也跳到主界面(2013-10-24 脱网进入)
//					turnToMainPage(false, false);
//					return -1;
//				}
//				
//				CxUserProfile userInitInfo = null;
//				try {
//					userInitInfo = (CxUserProfile)data;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (null == userInitInfo) {
//					Message msg = new Message();
//					msg.obj = getString(R.string.cx_fa_net_err);
//					new DisplayAlertInfo().sendMessage(msg);
//					
////					mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
//					//失败也跳到主界面(2013-10-24 脱网进入)
//					turnToMainPage(false, false);
//					return -1;
//				}
//				if (0 != userInitInfo.getRc()) {
//					Message msg = new Message();
//					msg.obj = userInitInfo.getMsg();
//					new DisplayAlertInfo().sendMessage(msg);
////					mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
//					//失败也跳到主界面(2013-10-24 脱网进入)
//					turnToMainPage(false, false);
//					return -2;
//				}
//				//获取成功,初始化用户信息，之后就进入主界面
//				CxUserProfileDataField profile = userInitInfo.getData();
//				if (null == profile) {
//					Message msg = new Message();
//					msg.obj = userInitInfo.getMsg();
//					new DisplayAlertInfo().sendMessage(msg);
//					
////					mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
//					//失败也跳到主界面(2013-10-24 脱网进入)
//					turnToMainPage(false, false);
//					return -3;
//				}
//				
//				String tempMateId = profile.getPartner_id();
//				if (!TextUtils.isEmpty(tempMateId)){ //结对了就要获取对方的资料
//					//如果结对且伴侣UID不为空，就要同时开启线程去获取伴侣资料
//					UserApi.getInstance().getUserPartnerProfile(userMateProfileCallback);
//				}
//				//存储自己的资料信息
//				CxUserProfileKeeper profileKeeper = new CxUserProfileKeeper();
//				profileKeeper.saveProfile(profile, CxAuthenNew.this);
//				
//				//加载聊天的三种模式设置
//				SharedPreferences chatSp = getSharedPreferences(CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
//				boolean chatSound = chatSp.getBoolean(CxGlobalConst.S_CHAT_SOUND, false);
//				boolean chatShock = chatSp.getBoolean(CxGlobalConst.S_CHAT_SHOCK, false);
//				boolean chatEarphone = chatSp.getBoolean(CxGlobalConst.S_CHAT_EARPHONE, false);
//				CxGlobalParams.getInstance().setChatSound(chatSound);
//				CxGlobalParams.getInstance().setChatShock(chatShock);
//				CxGlobalParams.getInstance().setChatEarphone(chatEarphone);
//				
//				mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
//				
//				turnToMainPage(false, true); //转入主界面
//				
//				return 0;
//			}
//
//		});
    }

    //注册回调
    private JSONCaller regiseCallback = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) { //注册失败给予提示
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);

//				openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
                return -1;
            }
            CxLog.i("", "ready to get user chuxin profile:" + result.toString());
            CxParseBasic loginResult = null;
            try {
                loginResult = (CxParseBasic) result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((TextUtils.isEmpty(loginResult.getMsg()))) { //注意此处把uid放在msg字段
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);

//				openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
                return -1;
            }
            CxLog.i("", "registed uid is:" + loginResult.getMsg());

            //2013.10.11增加对注册成功后的云统计
            //StatService.trackCustomEvent(RkAuthen.this, "rk_registe_count", "");

            //2013-4-28这里还是跟ios保持一致：注册成功需要再去登录
            isInitial = false;

            autoLogin();
//			//注册成功就去获取个人信息，对应接口（api/User/get）
//			fetchMyInfo(loginResult.getMsg());
            return 0;
        }
    };

    /* QQ授权方式登录chuxin */
    JSONCaller tencentCallback = new ConnectionManager.JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) { // 属于异常情况  基本不可能
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);

//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return -1;
            }
            // 检测rc,如果rc==2000表明授权过期，要求弹出相应的第三方授权界面
            CxLogin loginResult;
            loginResult = (CxLogin) result;

            if (999 == loginResult.getRc()) { // 授权过期(2013.09.14修改了，后端把token过期设置为999）
                //转入QQ授权
                //授权过期需要清除本地账号信息
                CxThirdAccessTokenKeeper.clear(CxAuthenNew.this);

                mLoadingManager.sendEmptyMessage(LOGIN_QQ_WITH_LOAD);
                return 999;
            }
            if (3000 == loginResult.getRc()) { // 未注册
                //先获取QQ用户在第三方的资料，再进入注册环节，如果注册成功就要获取用户信息，再进入主界面
                /**对于QQ，这里不同于sina微博，QQ需要重新授权*/
                CxThirdAccessToken localToken =
                        CxThirdAccessTokenKeeper.readAccessToken(CxAuthenNew.this);
                mTencent.setAccessToken(localToken.getToken(), localToken.getExpiresTime());
                mTencent.setOpenId(localToken.getUid());
                CxLog.i("", " ready params for qq get user profile:" + Constants.PARAM_ACCESS_TOKEN + "="
                        + mTencent.getAccessToken() + ", " + Constants.PARAM_CONSUMER_KEY + "="
                        + TENCENT_APP_ID + "," + Constants.PARAM_OPEN_ID + "=" + mTencent.getOpenId());
                if (mTencent.isSessionValid()) {
                    getQQUserInfo();
                } else {
//					openButtonOrQQLogin.sendEmptyMessage(LOGIN_QQ);
                    mLoadingManager.sendEmptyMessage(LOGIN_QQ_WITH_LOAD);
                }

                return 3000;
            }
            if (0 != loginResult.getRc()) { // 异常
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return loginResult.getRc();
            }

            if ((null == loginResult.getData()) || (TextUtils.isEmpty(loginResult.getData().getUid()))) {
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return -100;
            }
            // 正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面

            SharedPreferences preference = getSharedPreferences(CxGlobalConst.S_USER_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preference.edit();
            edit.putString(CxGlobalConst.S_USER_LONGIN_PLATFORM, CxGlobalConst.S_TENCET_PLATFORM);
            edit.commit();

            fetchMyInfo(loginResult.getData(), loginResult.getMsg());
            return 0;
        }

    };

    //-----提示--------------
    class DisplayAlertInfo extends Handler {

        public DisplayAlertInfo() {
            super(getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if ((null == msg) || (null == msg.obj)) {
                return;
            }
            ToastUtil.getSimpleToast(getApplicationContext(), -3, msg.obj.toString(), 1).show();
//			Toast.makeText(CxAuthenNew.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    }

    // ---------------------微博------------------------------
    private void loginWeibo() {
        CxLog.d("loginWeibo", "come here");
        //sina 微博登录
        CxThirdAccessToken localToken =
                CxThirdAccessTokenKeeper.readAccessToken(CxAuthenNew.this);
        if (null == localToken) {
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_WEIBO);
            mLoadingManager.sendEmptyMessage(LOGIN_WEIBO);
            return;
        }
        if ((null == localToken.getToken()) || (null == localToken.getExpiresTime())
                || (null == localToken.getUid())) {
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_WEIBO);
            mLoadingManager.sendEmptyMessage(LOGIN_WEIBO);
            return;
        }
        Oauth2AccessToken accessToken = new Oauth2AccessToken(
                localToken.getToken(), localToken.getExpiresTime());
        if (accessToken.isSessionValid()) { //直接登录chuxin
//			if (isInitial) {
//				mLoadingManager.sendEmptyMessage(VISIBLE_HAS_ACCOUNT);
//			}else{
//				mLoadingManager.sendEmptyMessage(DISPLAY_LOADING);
//			}
            doLoginChuxin("weibo", localToken.getUid(), localToken.getToken(), sinaCallback);
        } else { //过期需要第三方认证
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_WEIBO);
            mLoadingManager.sendEmptyMessage(LOGIN_WEIBO);
        }
    }

    /* sina微博回调 */
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            CxLog.w("AuthDialogListener", "onComplete come here");
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);

            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String uid = values.getString("uid");

            // 认证成功
            CxThirdAccessToken localToken = new CxThirdAccessToken(token, uid,
                    expires_in, CxGlobalConst.S_SINA_PLATFORM);
            CxThirdAccessTokenKeeper.keepAccessToken(CxAuthenNew.this,
                    localToken);
            //认证成功就登录chuxin
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN);
            doLoginChuxin("weibo", uid, token, sinaCallback);

        }

        @Override
        public void onError(WeiboDialogError e) {
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
            CxLog.w("AuthDialogListener", "onError *****");

            Message msg = new Message();
            msg.obj = e.getMessage();
            new DisplayAlertInfo().sendMessage(msg);
            /*if (isInitial) {
				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
			}else{
				mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
			}*/
        }

        @Override
        public void onCancel() {
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
            CxLog.w("AuthDialogListener", "onCancel() --------");
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mLoadingManager.sendEmptyMessage(LOGIN_CHUXIN_FAIL);
            Message msg = new Message();
            msg.obj = e.getMessage();
            new DisplayAlertInfo().sendMessage(msg);
            CxLog.w("AuthDialogListener", "onWeiboException() %%%%%%%%%%%%%%%%%");
        }

    }

    /* sina微博授权方式登录chuxin */
    JSONCaller sinaCallback = new ConnectionManager.JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) { // 属于异常情况
                //
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return -1;
            }
            // 检测rc,如果rc==2000表明授权过期，要求弹出相应的第三方授权界面
            CxLogin loginResult;
            loginResult = (CxLogin) result;

            if (999 == loginResult.getRc()) { // 授权过期(2013.09.14修改了，后端把token过期设置为999）
                // 转入sina授权
//				openButtonOrQQLogin.sendEmptyMessage(LOGIN_WEIBO);
                //授权过期需要清除本地账号信息
                CxThirdAccessTokenKeeper.clear(CxAuthenNew.this);
                mLoadingManager.sendEmptyMessage(LOGIN_WEIBO);
                return 999;
            }
            if (3000 == loginResult.getRc()) { // 未注册
                //先获取在sina微博的资料，成功后再获取用户资料，再成功就直接进入主界面
                try {
                    CxThirdAccessToken accessBeen = CxThirdAccessTokenKeeper
                            .readAccessToken(CxAuthenNew.this);

                    getWeiboUserProfile(accessBeen.getToken(), accessBeen.getUid());
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj = getString(R.string.cx_fa_login_fail);
                    new DisplayAlertInfo().sendMessage(msg);
//					openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                    mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                    e.printStackTrace();
                }
                return 3000;
            }
            if (0 != loginResult.getRc()) { // 异常
                //
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_login_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				mLoadingManager.sendEmptyMessage(INVISIBLE_HAS_ACCOUNT);
                //失败也跳到主界面(2013-10-24 脱网进入)
                turnToMainPage(false, false, 0);
                return loginResult.getRc();
            }
            //正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面
            SharedPreferences preference = getSharedPreferences(CxGlobalConst.S_USER_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preference.edit();
            edit.putString(CxGlobalConst.S_USER_LONGIN_PLATFORM, CxGlobalConst.S_SINA_PLATFORM);
            edit.commit();

            CxLog.i("sina login chuxin id", loginResult.getData().getUid());

            fetchMyInfo(loginResult.getData(), loginResult.getMsg());

            return 0;
        }

    };

    /*sina微博获取用户的资料*/
    private void getWeiboUserProfile(final String accessToken, final String uid) throws Exception {
        if (TextUtils.isEmpty(accessToken) || (TextUtils.isEmpty(uid))) {

            throw new Exception("any param of the two params can not null");
        }

        RequestListener listener = new RequestListener() {

            @Override
            public void onIOException(IOException arg0) {
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_sina_authorize_fail);
                new DisplayAlertInfo().sendMessage(msg);

//				openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
            }

            @Override
            public void onError(WeiboException arg0) {
                Message msg = new Message();
                msg.obj = getString(R.string.cx_fa_sina_authorize_fail);
                new DisplayAlertInfo().sendMessage(msg);
//				openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
            }

            @Override
            public void onComplete(String arg0) {
                CxLog.i("sina get user profile", arg0);
                //解析得到用户的昵称，性别，生日
                Bundle values = Utility.parseUrl(arg0);
//				String genderStr = values.getString("gender");
                int genderInt = -1; //0：男性 1：女性 -1：未设置
//				if (TextUtils.isEmpty(genderStr)) {
//					genderInt = -1;
//				}else if (genderStr.equalsIgnoreCase("m")) {
//					genderInt = 0;
//				}else if (genderStr.equalsIgnoreCase("f")) {
//					genderInt = 1;
//				}else{
//					genderInt = -1;
//				}

                //执行chuxin服务器的注册
                try {
                    AccountApi.getInstance().doRegister("weibo", uid, accessToken,
                            values.getString("screen_name"),
                            "" + genderInt, null/*sina微博没有这个字段返回*/,
                            null/*暂时没有client_version*/,
                            "zh_cn",
                            values.getString("profile_image_url"), regiseCallback);
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj = getString(R.string.cx_fa_sina_authorize_fail);
                    new DisplayAlertInfo().sendMessage(msg);
//					openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
                    mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
                    e.printStackTrace();
                }

            }
        };

        WeiboParameters params = new WeiboParameters();
        params.add("access_token", accessToken);
        params.add("uid", uid);
        AsyncWeiboRunner.request(WEIBO_GET_USER_PROFILE, params,
                HttpManager.HTTPMETHOD_GET, listener);
    }

    //-------------------------------------------------------

    //伴侣资料获取回调
    JSONCaller userMateProfileCallback = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) {
                return -1; //不做其他处理
            }
            try {
                CxMateProfile mateProfile = (CxMateProfile) result;
                if (0 != mateProfile.getRc() || (null == mateProfile.getData())) {
                    return -1; //不做其他处理
                }
                //正常获取成功就要设置伴侣资料到RkMateParams
                CxMateProfileDataField profileDataField = mateProfile.getData();
                CxMateParams myMateProfile = CxMateParams.getInstance();
                myMateProfile.setMateData(profileDataField.getData());
                myMateProfile.setMateIcon(profileDataField.getIcon());
                myMateProfile.setmMateBirth(profileDataField.getBirth());
                myMateProfile.setmMateEmail(profileDataField.getEmail());
                myMateProfile.setmMateMobile(profileDataField.getMobile());
                myMateProfile.setmMateName(profileDataField.getName());
                myMateProfile.setmMateNote(profileDataField.getNote());
                myMateProfile.setmMateUid(profileDataField.getPartner_id());
				
				/*//对方头像添加进全局
				RkGlobalParams.getInstance().setPartnerIconBig(profileDataField.getIcon());
				RkGlobalParams.getInstance().setPartnerName(profileDataField.getName());*/
                CxUserProfileKeeper mateProfileKeeper = new CxUserProfileKeeper();
                mateProfileKeeper.saveMateProfile(profileDataField.getIcon(),
                        profileDataField.getName(), CxAuthenNew.this);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    };

    @Override
    protected void onDestroy() {
//		isInitialed = false;
        CxLoadingUtil.getInstance().dismissLoading();
        mStatus = false;
        try {
            CxLoadingUtil.getInstance().dismissLoading();
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    private final int DISPLAY_LOADING = 1;
    private final int DISMISS_LOADING = 2;
    public final int VISIBLE_HAS_ACCOUNT = 3;
    private final int INVISIBLE_HAS_ACCOUNT = 4;
    private final static int LOGIN_QQ = 5;
    private final static int LOGIN_WEIBO = 6;
    private final static int LOGIN_QQ_WITH_LOAD = 7;
    private final static int LOGIN_WEIBO_WITH_LOAD = 8;
    private final static int LOGIN_CHUXIN = 9;
    private final static int LOGIN_CHUXIN_FAIL = 10;

    class LoadingState extends Handler {

        public LoadingState() {
            super(getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //
            CxLog.d("handleMessage", "come msg what>>>" + msg.what);
            switch (msg.what) {
                case DISPLAY_LOADING: //只显示loading状态
//				RkLoadingUtil.getInstance().showLoading(RkAuthen.this, true);
                    break;
                case DISMISS_LOADING: //只取消loading状态
                    CxLoadingUtil.getInstance().dismissLoading();
                    break;

                case VISIBLE_HAS_ACCOUNT: //自动登录
                    channelLogo.setVisibility(View.GONE);
                    loginLayout.setVisibility(View.VISIBLE);
//				RkLoadingUtil.getInstance().showLoading(RkAuthen.this, true);
                    break;
                case INVISIBLE_HAS_ACCOUNT: //自动登录失败
//				CxLoadingUtil.getInstance().dismissLoading();
//                    channelLogo.setVisibility(View.GONE);
                    loginLayout.setVisibility(View.VISIBLE);
                    break;
                case LOGIN_QQ: //QQ登录
                    mTencent.login(CxAuthenNew.this, TENCENT_APP_SCOPE, loginQQListener);
//				RkLoadingUtil.getInstance().showLoading(RkAuthen.this);
                    break;

                case LOGIN_WEIBO: //微博登录
                    mWeibo.authorize(CxAuthenNew.this, new AuthDialogListener());
//				RkLoadingUtil.getInstance().showLoading(RkAuthen.this);
                    break;
                case LOGIN_QQ_WITH_LOAD: //QQ登录
                    mTencent.login(CxAuthenNew.this, TENCENT_APP_SCOPE, loginQQListener);
//				RkLoadingUtil.getInstance().showLoading(RkAuthen.this, true);
                    break;

                case LOGIN_WEIBO_WITH_LOAD: //微博登录
                    mWeibo.authorize(CxAuthenNew.this, new AuthDialogListener());
//				RkLoadingUtil.getInstance().showLoading(RkAuthen.this, true);
                    break;
                case LOGIN_CHUXIN: //登录chuxin
                    loginLayout.setVisibility(View.INVISIBLE);
//                    channelLogo.setVisibility(View.VISIBLE);
                    break;
                case LOGIN_CHUXIN_FAIL: //登录chuxin失败
//                    channelLogo.setVisibility(View.GONE);
                    loginLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

        }

    }

    @SuppressLint("DefaultLocale")
            //汇报服务器已经激活app
    class ReportActiveTask extends AsyncTask<Object, Integer, Integer> {

        public String hexToString(byte[] hex, int offset, int length) {
            StringBuilder sb = new StringBuilder();

            if ((offset + length) > hex.length)
                length = hex.length - offset;

            for (int i = offset; i < length; i++) {
                sb.append(String.format("%02x", hex[i]));
            }

            return sb.toString();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected Integer doInBackground(Object... params) {

            SharedPreferences sp = getSharedPreferences("updateflag", Context.MODE_PRIVATE);
            if (sp.getBoolean("report", false)) {
                return 1;
            }

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId() + android.provider.Settings.Secure.getString(
                    getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            try {
                byte[] md5ByteArray = MessageDigest.getInstance("MD5").digest(deviceId.getBytes());
                String hexString = hexToString(md5ByteArray, 0, md5ByteArray.length).toUpperCase();
                AccountApi.getInstance().sendActiveAction(hexString, reportCaller);
                //腾讯统计
                //StatService.trackCustomEvent(RkAuthen.this, "active_key", ("A"+hexString));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    //激活app回调
    JSONCaller reportCaller = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) {
                return -1;
            }
            CxParseBasic reportResult;
            try {
                reportResult = (CxParseBasic) result;
            } catch (Exception e) {
                reportResult = null;
                e.printStackTrace();
            }
            if (0 != reportResult.getRc()) {
                return 1;
            }

            //置回标志，以后不再发送了
            SharedPreferences sp = getSharedPreferences("updateflag", Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.putBoolean("report", true);
            editor.commit();
            return 0;
        }
    };
    private LinearLayout mIndicatorLayout;
    private ImageView channelLogo;

    public void completeGuider() {
        mUnusedLoginlayout.setVisibility(View.GONE);
        initView();
//		loginLayout.setVisibility(View.VISIBLE);
        SharedPreferences sp = getSharedPreferences("updateflag", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("guide", true);
        editor.commit();
    }


    class GuiderPagerAdapter extends FragmentStatePagerAdapter {


        public GuiderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            arg0 = arg0 % 7;
            switch (arg0) {
                case 0:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction1, false);
                case 1:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction2, false);
                case 2:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction3, false);
                case 3:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction4, false);
                case 4:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction5, false);
                case 5:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction6, false);
                case 6:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_main_login_launchscreen, true);
                default:
                    return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_main_login_launchscreen, true);
            }

        }

        @Override
        public int getCount() {
            return 7;
        }

    }

}
