package com.chuxin.family.main;


import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import net.simonvt.menudrawer.CxBaseSlidingMenu;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.accounting.CxAccountFragment;
import com.chuxin.family.calendar.CxCalendarFragment;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.kids.CxKidFragment;
import com.chuxin.family.main.CxAuthenNew.AuthDialogListener;
import com.chuxin.family.main.CxAuthenNew.DisplayAlertInfo;
import com.chuxin.family.mate.CxFamilyInfoFragment;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.more.CxMoreFragment;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.pair.CxPairRequest;
import com.chuxin.family.parse.been.CxCheckVersion;
import com.chuxin.family.parse.been.CxLogin;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.service.CxBackgroundService;
import com.chuxin.family.tabloid.TabloidDataProcess;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.CxDailyEntryProcessor;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.CxUserProfileKeeper;
import com.chuxin.family.utils.Push;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.views.login.CxThirdAccessToken;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;
import com.chuxin.family.views.login.Oauth2AccessToken;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.zone.CxUsersPairZone;
import com.chuxin.androidpush.sdk.push.RKPush;
import com.chuxin.family.R;
import com.tencent.stat.StatService;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxMain extends CxBaseSlidingMenu {
	protected CxImageView mPartnerImageView;
	protected TextView mMateName;
	protected TextView mTitle, mUnPairTextTip;
	protected FrameLayout mMateLayer;

	private boolean isUpdateTip = false; 	// 版本更新
	
//	private static RkMain mSelf;
	
	private boolean mForceUpdate = false; //默认非强制更新
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		mSelf = this;
		if (1 == CxGlobalParams.getInstance().getPair()) {
			getSupportFragmentManager().beginTransaction().replace(
					mMenuDrawer.getContentContainer().getId(), 
					getFragment(FRAGMENT_CHAT)).commitAllowingStateLoss();
		}else{
			getSupportFragmentManager().beginTransaction().replace(
					mMenuDrawer.getContentContainer().getId(), 
					getFragment(FRAGMENT_INVITE)).commitAllowingStateLoss();
		}
	
		
		
		try {
			isUpdateTip = CxMain.this.getIntent().getBooleanExtra("exist",false);
		} catch (Exception e) {
			CxLog.w("rkmain", ""+e.toString());
		}

		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        mMenuDrawer.setOffsetMenuEnabled(false);
        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                /*if (newState == MenuDrawer.STATE_CLOSED) {
                    commitTransactions();
                }*/
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                // Do nothing
            }
        });
        
        int hasToken = CxMain.this.getIntent().getIntExtra("hasToken", 0);
		if(hasToken==1){
			autoLogin();
		}

		// 启动服务
        Intent rkService = new Intent(CxMain.this, CxBackgroundService.class);
		rkService.putExtra("source", 2);
		startService(rkService);

		// 每次杀死进程 或 每隔3天都要检查一次版本更新
		if( !isUpdateTip  || isNeedCheckVersion() ){
			new Handler() {
				public void handleMessage(Message msg) {
					checkHasOldVersion();
				};
			}.sendEmptyMessageDelayed(1, 50);
		}
		
		//2013.10.11增加云统计
		StatService.trackCustomEvent(CxMain.this, "rk_main_count", "");
		
	
		
		
		//拉小报数据
		CxDailyEntryProcessor.DailyEventInterface eve = new CxDailyEntryProcessor.DailyEventInterface() {
			
			@Override
			public boolean doDailyEvent() {
				//拉取小报数据
				try {
					return new TabloidDataProcess(getApplicationContext()).getDataFromServerAtFirstTimeEveryday();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		};
		try {
			new CxDailyEntryProcessor(CxMain.this.getApplicationContext(), 
					eve).execute();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
	
	private void checkHasOldVersion(){
		PackageManager pm = getApplicationContext().getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		boolean hasOld=false;
		for(PackageInfo pInfo : packages){

			String packagename = pInfo.packageName;
			if("com.rekoo.family.husband".equalsIgnoreCase(packagename) 
					|| "com.rekoo.family.wife".equalsIgnoreCase(packagename)){
				hasOld=true;
				showHasOldDialog();
			}
		}
		
		if(!hasOld){
			UserApi.getInstance().checkVersion(checkVersionCall);//版本检查
		}
		

	}
	
	protected void showHasOldDialog() {
		
		View inflate = View.inflate(this, R.layout.cx_fa_widget_neighbour_answer_question_dialog, null);
		TextView contentText = (TextView) inflate.findViewById(R.id.cx_fa_answer_question_dialog_text_tv);
		Button yesBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_yes_btn);
		Button noBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_no_btn);
		contentText.setText(getString(R.string.cx_fa_main_login_hasold_text));
		yesBtn.setText(getString(R.string.cx_fa_answer_question_zero_dialog_cancel));
		noBtn.setVisibility(View.GONE);
		
		final Dialog dialog = new Dialog(this, R.style.simple_dialog);		
		dialog.setContentView(inflate);	
		dialog.setCancelable(false);
		yesBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				UserApi.getInstance().checkVersion(checkVersionCall);//版本检查
			}
		});
		dialog.show();
	}
	
	
	
	/**
	 * 是否需要检查版本更新
	 * @return
	 */
	private boolean isNeedCheckVersion(){
		SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		if(sp==null){
			return true;
		}
		
		long lastCheckTime 		= sp.getLong("checkVersionTime", 0);		// 上次检查时间
		
		long cha = System.currentTimeMillis() - lastCheckTime;
		int days  	= 3;		// 相差多少天检查一次
		if( cha >=  days * 24 * 3600 * 1000 ){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 设置此次检查版本更新的时间
	 */
	private void updateCheckVersionTime(){
		SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		Editor sharedata          = sp.edit();
		sharedata.putLong("checkVersionTime", System.currentTimeMillis());
		sharedata.commit();
	}
	
	
	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_WINDOW;
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	/*//对外提供context对象接口（2.13.9.25提供给世超）
	public static RkMain getMainActivity(){
		return mSelf;
	}*/

	
	private Fragment getFragment(int order) {
		Fragment f = null;

			switch (order) {
			case FRAGMENT_MATE_PROFILE:
				f = new CxFamilyInfoFragment();
				break;
			case FRAGMENT_CHAT:
				f = new ChatFragment();
				break;
			case FRAGMENT_ZONE:
				f = new CxUsersPairZone();
				break;
			case FRAGMENT_NEIGHBOUR:
				f = new CxNeighbourFragment();
				break;
			case FRAGMENT_INVITE:
				f = new CxPairRequest();
				break;
//			case FRAGMENT_SETTINGS:
//				f = new RkSettingFragment();
//				break;
			case FRAGMENT_MORE:
				f = new CxMoreFragment();
				break;
			case FRAGMENT_ACCOUNT:
				f = new CxAccountFragment();
				break;
			case FRAGMENT_CALENDAR:
			    //f = new CxCalendarFragment();
				f=new CxCalendarFragment();
				break;
			case FRAGMENT_KID:
				f=new CxKidFragment();
				break;
			case FRAGMENT_INVITE_OPPO:
				f=new CxPairRequest();
				break;
			default:
				break;
			}

		return f;
	}

	
	
	//对外菜单开启与关闭
	public void toggleMenu(){
		mMenuDrawer.toggleMenu();
	}
	
	public void closeMenu(){
		mMenuDrawer.closeMenu();
	}
	
	private JSONCaller checkVersionCall = new JSONCaller() {

		@SuppressLint("NewApi")
		@Override
		public int call(Object result) {
			
			if (null == result) {
				return -1;
			}
			CxCheckVersion version = null;
			try {
				version = (CxCheckVersion) result;
			} catch (Exception e) {
			}
			if (null == version) {
				return -1;
			}
			if (0 != version.getRc()) {
				return version.getRc();
			}
			
			//成功的情况下才更新标记位
			updateCheckVersionTime();		// 将当前时间写入本地缓存中(下次用来判断，每隔3天执行一下次版本检查)
			
			final String downLoadUri = version.getUrl();
			if (TextUtils.isEmpty(downLoadUri)) {
				return 0;
			}
			
			final String tipMsg = version.getMsg(); 		// 提示语
			
			if (1 == version.getFlag()) { // 必须更新
				mForceUpdate = true;
				new Handler(getMainLooper()) {
					public void handleMessage(Message msg) {
						if (CxMain.this.isFinishing()) {
							return;
						}
						
						View inflate = View.inflate(CxMain.this, R.layout.cx_fa_widget_neighbour_answer_question_dialog, null);
						
						TextView titleText = (TextView) inflate.findViewById(R.id.cx_fa_simple_dialog_title);
						TextView contentText = (TextView) inflate.findViewById(R.id.cx_fa_answer_question_dialog_text_tv);
						Button yesBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_yes_btn);
						Button noBtn = (Button) inflate.findViewById(R.id.cx_fa_answer_question_dialog_no_btn);
						
						String textMsg="";
						
						if(tipMsg!=null && !tipMsg.equals("")){
							textMsg=tipMsg;
						}else{
							textMsg=getString(R.string.cx_fa_must_upgrade_text);
						}
						
						titleText.setText(getString(R.string.cx_fa_upgrade_tip_text));
						contentText.setText(textMsg);
						noBtn.setText(getString(R.string.cx_fa_upgrade_text));
						yesBtn.setVisibility(View.GONE);
						
						final Dialog dialog = new Dialog(CxMain.this, R.style.simple_dialog);		
						dialog.setContentView(inflate);	
						dialog.setCancelable(false);
						noBtn.setOnClickListener(new OnClickListener() {			
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								Uri content_url = Uri.parse(downLoadUri);
								intent.setData(content_url);
								startActivity(intent);
								CxMain.this.finish();
							}
						});
						dialog.show();

					};
				}.sendEmptyMessage(1);
				return 1;
			}
			if (2 == version.getFlag()) { // 能更新的话最好，不更新也可以
				mForceUpdate = false;
				final String versionName = version.getVersion();
				new Handler(getMainLooper()) {
					public void handleMessage(Message msg) {
						if (CxMain.this.isFinishing()) {
							return;
						}
						
						DialogUtil du = DialogUtil.getInstance();
						du.setOnSureClickListener(new OnSureClickListener() {
							
							@Override
							public void surePress() {
								/*new RkUpdateTask().execute(downLoadUri);
								dialog.dismiss();*/
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								Uri content_url = Uri.parse(downLoadUri);
								intent.setData(content_url);
								startActivity(intent);
								CxMain.this.finish();
								
							}
						});
						
						
						String st = "";
						try {
							st = String.format(getString(R.string.cx_fa_upgrade_version_text),
											versionName);
						} catch (Exception e) {
							st = getString(R.string.cx_fa_upgrade_version_text);
						}
						
						String textMsg="";
						
						if(tipMsg!=null && !tipMsg.equals("")){
							textMsg=tipMsg;
						}else{
							textMsg=st;
						}
						
						Dialog simpleDialog = du.getSimpleDialog(CxMain.this, getString(R.string.cx_fa_upgrade_tip_text), textMsg, 
								getString(R.string.cx_fa_upgrade_text), null);
						
						simpleDialog.setCancelable(false);
						simpleDialog.show();

							};
				}.sendEmptyMessage(1);
				return 2;
			}

			return 0;
		}
	};

	class RkUpdateTask extends AsyncTask<String, Integer, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
			if (mForceUpdate) {
				mForceUpdate = false;
				CxMain.this.finish();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Integer doInBackground(String... params) {
			if (null == params) {
				return -1;
			}
			String downLoadUri = params[0];
			
			CxLog.i("version download", ""+downLoadUri);
			File tempApk = new File(Environment.DIRECTORY_DOWNLOADS, "chuxin_family.apk");
        	if (tempApk.exists()) {
        		try {
					tempApk.delete();
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
			}
        	
        	File dirFile = new File(Environment.DIRECTORY_DOWNLOADS);
        	if (!dirFile.exists()) {
        		try {
					dirFile.mkdirs();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        	
			DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			
			Uri uri = Uri.parse(downLoadUri);
			CxLog.i("version send uri", ""+uri.toString());
			DownloadManager.Request request = new DownloadManager.Request(uri);
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
			try{
				request.setDestinationInExternalFilesDir(
						CxMain.this.getApplicationContext(),
						Environment.DIRECTORY_DOWNLOADS,
						"chuxin_family.apk");
			}catch(Exception e){
				e.printStackTrace();
				CxLog.e("DownloadManager setDestinationInExternalFilesDir error", " "+e.getMessage());
			}
			
			request.setMimeType("application/vnd.android.package-archive");
			
			long downloadTaskId = -1;
			try {
				downloadTaskId = downloadManager.enqueue(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (-1 == downloadTaskId) {
				return -2;
			}
			CxGlobalParams.getInstance().setUpdateTaskID(downloadTaskId);
			
			return 1;
		}
		
	}
	
	 
    
    @Override  
    protected void onResume() {  
        super.onResume();  
    }  
    
	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_CLOSED || drawerState == MenuDrawer.STATE_CLOSING) {
			mMenuDrawer.openMenu();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void switchFragment(int item) {
		Fragment fmt = getFragment(item);
		if (null == fmt) {
			return;
		}
		if ((null == CxMain.this)|| (CxMain.this.isFinishing())) {
			return;
		}
		
		try {
			int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
			for (int i = 0; i < backStackCount; i++) {
			    int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
			    getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getSupportFragmentManager().beginTransaction().replace(
				mMenuDrawer.getContentContainer().getId(), 
				fmt).commitAllowingStateLoss();
	}
	

	public static final int MATE_PROFILE = R.id.menuPage_imageView1;
	public static final int CHAT = R.id.chat_rl_layout;
//	public static final int REMINDER = R.id.remind_rl_layout; // delete reminder by  shichao 20131024
	public static final int NEIGHBOUR = R.id.neighbour_rl_layout; // add neighbour by shichao20131024 
	public static final int ZONE = R.id.zone_rl_layout;
	public static final int ZONE2 = R.id.zone_rl_layout2;
	public static final int INVITE = R.id.invite_rl_layout;
//	public static final int SETTINGS = R.id.settings_rl_layout;
	public static final int CALENDAR = R.id.calendar_rl_layout;
	public static final int CALENDAR2 = R.id.calendar_rl_layout2;
	public static final int MORE = R.id.more_rl_layout;
	public static final int MORE2 = R.id.more_rl_layout2;
	public static final int ACCOUNT = R.id.finance_recorder_layout;
	public static final int ACCOUNT2 = R.id.finance_recorder_layout2;
	public static final int KID = R.id.kids_rl_layout; 
	public static final int INVITE_OPPO = R.id.invite_oppo_rl_layout; 
	public void changeFragment(int order){
		menuEvent(order);
	}

	@Override
	protected void logout() {
		//停止push
		RKPush.S_SEND_FLAG = false;
		
		if (!CxGlobalParams.getInstance().isLogin() 
				&& (!CxAuthenNew.isShown())) { // 登出
			CxGlobalParams.getInstance().setAppStatus(false);
			CxAuthenNew.setStatus(true);
            Intent toLogin = new Intent(CxMain.this, CxAuthenNew.class);
            startActivity(toLogin);
            CxMain.this.finish();
        }
		
	}

	@Override
	protected void pairStatusChange(int item) {
		switchFragment(item);
	}
	
	@Override
	protected void onDestroy() {
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxMain.this, "head", CxMain.this);
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		super.onDestroy();
	}

	//子控件可以滑动
	public void availeChildSlide(){
		changeSlideStatus(true);
	}
	
	//控件不可滑动
	public void inavaileChildSlide(){
		changeSlideStatus(false);
	}
	
	/***************************************登录部分********************************************/
	
	//本地有第三方授权信息时候自动登录chuxin
	private void autoLogin() {
		CxThirdAccessToken localToken = CxThirdAccessTokenKeeper.readAccessToken(CxMain.this);
			
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
			return;
		}

	}
	
	
	private void loginWeibo() {
	    CxLog.d("loginWeibo", "come here");
		//sina 微博登录
		CxThirdAccessToken localToken = CxThirdAccessTokenKeeper.readAccessToken(CxMain.this);
	
		Oauth2AccessToken accessToken = new Oauth2AccessToken(
				localToken.getToken(), localToken.getExpiresTime());
		if (accessToken.isSessionValid()) { //直接登录chuxin
			changeTitle(true, 0);
			CxAuthenNew.doLoginChuxin("weibo", localToken.getUid(), localToken.getToken(), sinaCallback);
		}else{ //过期需要第三方认证
			CxAuthenNew.mWeibo.authorize(CxMain.this, new AuthDialogListener());
		}
		return;
	}
	
	/* sina微博回调 */
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
		    CxLog.w("AuthDialogListener", "onComplete come here");
		    
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String uid = values.getString("uid");

			// 认证成功
			CxThirdAccessToken localToken = new CxThirdAccessToken(token, uid,
					expires_in, CxGlobalConst.S_SINA_PLATFORM);
			CxThirdAccessTokenKeeper.keepAccessToken(CxMain.this,
					localToken);
			//认证成功就登录chuxin
			
			
			changeTitle(true, 0);
			CxAuthenNew.doLoginChuxin("weibo", localToken.getUid(), localToken.getToken(), sinaCallback);
		}

		@Override
		public void onError(WeiboDialogError e) {
			CxLog.w("AuthDialogListener", "onError *****");
			Message msg = new Message();
			msg.obj = e.getMessage();
			new DisplayAlertInfo().sendMessage(msg);
		}

		@Override
		public void onCancel() {		
			CxLog.w("AuthDialogListener", "onCancel() --------");
		}

		@Override
		public void onWeiboException(WeiboException e) {
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
				Message msg = new Message();
				msg.obj = getString(R.string.cx_fa_login_fail);
//				new DisplayAlertInfo().sendMessage(msg);
				changeTitle(false, 1);
				return -1;
			}
			// 检测rc,如果rc==2000表明授权过期，要求弹出相应的第三方授权界面
			CxLogin loginResult = null;
			loginResult = (CxLogin) result;

			if (999 == loginResult.getRc()) { // 授权过期(2013.09.14修改了，后端把token过期设置为999）
				// 转入sina授权
//				openButtonOrQQLogin.sendEmptyMessage(LOGIN_WEIBO);
				//授权过期需要清除本地账号信息
				changeTitle(false, 0);
				CxThirdAccessTokenKeeper.clear(CxMain.this);
				CxAuthenNew.mWeibo.authorize(CxMain.this, new AuthDialogListener());
				return 999;
			}
			
			if(408==loginResult.getRc()){
				changeTitle(false, 1);
				return 408;
			}
		
			if (0 != loginResult.getRc()) { // 异常
				Message msg = new Message();
				msg.obj = getString(R.string.cx_fa_login_fail);
//				new DisplayAlertInfo().sendMessage(msg);
				changeTitle(false, 2);
				return loginResult.getRc();
			}
			changeTitle(false, 0);
			//正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面
			SharedPreferences preference = getSharedPreferences(CxGlobalConst.S_USER_FILE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = preference.edit();
			edit.putString(CxGlobalConst.S_USER_LONGIN_PLATFORM, CxGlobalConst.S_SINA_PLATFORM);
			edit.commit();
			
			CxLog.i("sina login chuxin id", loginResult.getData().getUid());
			
			fetchMyInfo(loginResult.getData(),loginResult.getMsg());
			
			return 0;
		}

	};
	
	
	
	/* QQ登录 */
	public void loginQQ() {
		if (CxAuthenNew.mTencent == null) {
			CxLog.d("", "mTencent is null");
//			openButtonOrQQLogin.sendEmptyMessage(ENABLE_BUTTON_INT);
			
			return;
		}

		CxThirdAccessToken localToken = 
			CxThirdAccessTokenKeeper.readAccessToken(CxMain.this);
		
		
		CxAuthenNew.mTencent.setAccessToken(localToken.getToken(), localToken.getExpiresTime());
		CxAuthenNew.mTencent.setOpenId(localToken.getUid());
		if (CxAuthenNew.mTencent.isSessionValid()) { //有效
			changeTitle(true, 0);
			CxAuthenNew.doLoginChuxin(CxGlobalConst.S_TENCET_PLATFORM, localToken.getUid(), localToken.getToken(), tencentCallback);
		}else{ //无效就登录QQ
//			openButtonOrQQLogin.sendEmptyMessage(LOGIN_QQ);
			CxAuthenNew.mTencent.login(CxMain.this, CxAuthenNew.TENCENT_APP_SCOPE, loginQQListener);
		}
		
	}
	
	//QQ授权回调
	IUiListener loginQQListener = new IUiListener() {
		//凡是这个授权回调的，都要经过chuxin的登录验证
		@Override
		public void onCancel() {
			CxLog.i("", "cancel qq login");
		}

		@Override
		public void onComplete(Object arg0) {
			CxLog.i("", "logint TENCET COME BACK :"+arg0);
			
			if (null == arg0) {
				//提示出错
				Message msg = new Message();
				msg.obj = getString(R.string.cx_fa_qq_authen_fail);
				new DisplayAlertInfo().sendMessage(msg);
				return;
			}
			
			JSONObject obj=null;
			try {
				obj=(JSONObject)arg0;
			} catch (Exception e) {
				
			}
			
			//本地缓存
			try {
				CxThirdAccessToken localToken = new CxThirdAccessToken(
						obj.getString("access_token"), obj.getString("openid"), 
						obj.getString("expires_in"), CxGlobalConst.S_TENCET_PLATFORM);
				CxThirdAccessTokenKeeper.keepAccessToken(CxMain.this, localToken);
			} catch (JSONException e) {
				e.printStackTrace();
				CxLog.e("---------", "save third token fail");
			}
			try {
				CxLog.i("RkAuthenNew_men", obj.getString("access_token")+">>>>>>>"+obj.getString("openid")+">>>>>"+obj.getString("expires_in"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// QQ登录授权成功后登录chuxin服务器
			changeTitle(true, 0);
			CxAuthenNew.doLoginChuxin(CxGlobalConst.S_TENCET_PLATFORM, CxAuthenNew.mTencent.getOpenId(), 
					CxAuthenNew.mTencent.getAccessToken(), tencentCallback);
		}

		@Override
		public void onError(UiError e) {
			Message msg = new Message();
			msg.obj = e.errorMessage;
			new DisplayAlertInfo().sendMessage(msg);
		}

	};
	
	
	
	
	/* QQ授权方式登录chuxin */
	JSONCaller tencentCallback = new ConnectionManager.JSONCaller() {

		@Override
		public int call(Object result) {
			if (null == result) { // 属于异常情况  基本不可能

				changeTitle(false, 1);
				return -1;
			}
			// 检测rc,如果rc==2000表明授权过期，要求弹出相应的第三方授权界面
			CxLogin loginResult = null;
			loginResult = (CxLogin) result;

			if (999 == loginResult.getRc()) { // 授权过期(2013.09.14修改了，后端把token过期设置为999）
				//转入QQ授权
				//授权过期需要清除本地账号信息
				changeTitle(false, 0);
				CxThirdAccessTokenKeeper.clear(CxMain.this);
				CxAuthenNew.mTencent.login(CxMain.this, CxAuthenNew.TENCENT_APP_SCOPE, loginQQListener);
				return 999;
			}
			
			if(408==loginResult.getRc()){
				changeTitle(false, 1);
				return 408;
			}
	
			if (0 != loginResult.getRc()) { // 异常

				changeTitle(false, 2);
				return loginResult.getRc();
			}
			
			if ( (null == loginResult)  || (null == loginResult.getData()) 
					|| (TextUtils.isEmpty(loginResult.getData().getUid())) ){

				changeTitle(false, 2);
				return -100;
			}
			// 正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面
			changeTitle(false, 0);
			SharedPreferences preference = getSharedPreferences(CxGlobalConst.S_USER_FILE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = preference.edit();
			edit.putString(CxGlobalConst.S_USER_LONGIN_PLATFORM, CxGlobalConst.S_TENCET_PLATFORM);
			edit.commit();
	
			fetchMyInfo(loginResult.getData(),loginResult.getMsg());
			return 0;
		}

	};
	
	
	
	private void loginFamily() {
		CxThirdAccessToken localToken = 
			CxThirdAccessTokenKeeper.readAccessToken(CxMain.this);
		CxAuthenNew.doLoginChuxin(localToken.getPlatName(), 
				localToken.getUid(), localToken.getToken(), loginCaller);
	}
	
	
	JSONCaller loginCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) { // 属于异常情况  基本不可能
				changeTitle(false, 1);
				return -1;
			}
			// 检测rc,如果rc==2000表明授权过期，要求弹出相应的第三方授权界面
			CxLogin loginResult = null;
			loginResult = (CxLogin) result;
			
			if(408==loginResult.getRc()){
				changeTitle(false, 1);
				return 408;
			}


			if (0 != loginResult.getRc()) { // 异常
				changeTitle(false, 2);
				return loginResult.getRc();
			}
			
			if ( (null == loginResult)  || (null == loginResult.getData()) 
					|| (TextUtils.isEmpty(loginResult.getData().getUid())) ){
				changeTitle(false, 2);
				return -100;
			}
			// 正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面
			changeTitle(false, 0);
			SharedPreferences preference = getSharedPreferences(CxGlobalConst.S_USER_FILE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = preference.edit();
			edit.putString(CxGlobalConst.S_USER_LONGIN_PLATFORM, "email");
			edit.commit();
	
			fetchMyInfo(loginResult.getData(),loginResult.getMsg());
			return 0;
		}
	};
	
	
	
	/*获取用户在chuxin上的资料*/
	public void fetchMyInfo(CxUserProfileDataField  profile,String message) {
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
			CxLog.w("", ""+e1.toString());
		}

		//获取成功,初始化用户信息，之后就进入主界面
		
		if (null == profile) {
			Message msg = new Message();
			msg.obj = message;
			new DisplayAlertInfo().sendMessage(msg);
			return ;
		}
		
		CxGlobalParams.getInstance().setLoginNetSuccess(true);
		
		String tempMateId = profile.getPartner_id();
		if (!TextUtils.isEmpty(tempMateId)){ //结对了就要获取对方的资料
			//如果结对且伴侣UID不为空，就要同时开启线程去获取伴侣资料
			UserApi.getInstance().getUserPartnerProfile(userMateProfileCallback);
		}
		//存储自己的资料信息
		CxUserProfileKeeper profileKeeper = new CxUserProfileKeeper();
		profileKeeper.saveProfile(profile, CxMain.this);
		
		//加载聊天的三种模式设置
		SharedPreferences chatSp = getSharedPreferences(CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
		boolean chatSound = chatSp.getBoolean(CxGlobalConst.S_CHAT_SOUND, false);
		boolean chatShock = chatSp.getBoolean(CxGlobalConst.S_CHAT_SHOCK, false);
		boolean chatEarphone = chatSp.getBoolean(CxGlobalConst.S_CHAT_EARPHONE, false);
		CxGlobalParams.getInstance().setChatSound(chatSound);
		CxGlobalParams.getInstance().setChatShock(chatShock);
		CxGlobalParams.getInstance().setChatEarphone(chatEarphone);
		
//		mLoadingManager.sendEmptyMessage(DISMISS_LOADING);
		
//		turnToMainPage(false, true); //转入主界面
	}
	
	
	
	//伴侣资料获取回调
	JSONCaller userMateProfileCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {
				return -1; //不做其他处理
			}
			try {
				CxMateProfile mateProfile = (CxMateProfile)result;
				if (0 != mateProfile.getRc() || (null == mateProfile.getData()) ) {
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
						profileDataField.getName(), CxMain.this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return 0;
		}
	};
	
	class DisplayAlertInfo extends Handler{
		
		public DisplayAlertInfo(){
			super(getMainLooper());
		}
		
		@Override
		public void handleMessage(Message msg) {
			if ( (null == msg) || (null == msg.obj) ) {
				return;
			}
			ToastUtil.getSimpleToast(getApplicationContext(), -3, msg.obj.toString(), 1).show();
//			Toast.makeText(CxAuthenNew.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}
	}
	
	/**
	 * 
	 * @param showLoading  显示登录的loading
	 * @param i    0 老公说；1 网络未连接  ； 2 网络有点问题。
	 */
	private void  changeTitle(boolean showLoading,int i){
		ChatFragment.mShowLoading=showLoading;
		ChatFragment.mShowWhichTitle=i;
		CxLog.i("ChatFragment_men", (null != ChatFragment.mChatHandler)+">>>>>>>>changeTitle");
		if(null != ChatFragment.mChatHandler){
			Message chatMessage = ChatFragment.mChatHandler.obtainMessage(ChatFragment.UPDATE_CHAT_TITLE);
		 	chatMessage.sendToTarget();
		}
	}
}
