package com.chuxin.family.settings;

import com.chuxin.family.R;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.main.CxAuthenChildrenSelectorActivity;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.net.CxPairApi;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.CxSettingsCommonApi;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.parse.CxSettingsParser.SendHeadImageType;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxChatBgList;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.data.ChatBgData;
import com.chuxin.family.parse.been.data.CxChangeHeadDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author shichao.wang 设置页面
 *
 */
public class CxSettingActivity extends CxRootActivity {

	
	LinearLayout modifyChatBgLayer, changeHeadLayer;
	LinearLayout  aboutVersion, unbindPair; //关于老公/老婆,解绑
	LinearLayout clearCacheLayer; //包括清楚图片缓存和清空聊天记录user_logined_operator_view
	LinearLayout clearImageCache, clearChatRecord; //清楚图片缓存，清空聊天记录
	private CurrentObserver mGlobalObserver;
	private Button exitButton;
	
	private CheckBox mLockScreen, mSoundForChat, mShockBtnForChat,mEarphoneForChat;
	
	private CxImageView mHeadImage, mChatBgImage;
	
	private Button mMenuBtn;
	
	private final String PROFILE_FILE_NAME = "profile_name";
	
	private LinearLayout mEditPushSound;
	@SuppressWarnings("unused")
	private View aboutVersionBottomLine, modifyMyHeadBottomLine;			// 关于分割线
	
	private final String PROFILE_FIELD_PAIR = "pair";
	private final String PROFILE_FIELD_MATE_ID = "mate_id";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_fragment_setting);
		
		version_type = CxGlobalParams.getInstance().getVersion_type();
		
		init();
		
		
		
		//添加对登录状态的监听
		mGlobalObserver = new CurrentObserver(); //生成观察者实例
		//设置观察目标
		List<String> tags = new ArrayList<String>();
		tags.add(CxGlobalParams.PAIR); //结对状态
		tags.add(CxGlobalParams.IS_LOGIN);
//		tags.add(CxGlobalParams.ICON_SMALL); //头像
		tags.add(CxGlobalParams.SINGLE_MODE); 
		tags.add(CxGlobalParams.CHAT_SMALL); //聊天背景小图
		//此界面只针对结对状态和登录状态进行监听
		mGlobalObserver.setListenTag(tags); //设置观察目标
		mGlobalObserver.setMainThread(true); //设置在UI线程执行update
		CxGlobalParams.getInstance().registerObserver(mGlobalObserver); //注册观察者
		
		
		CxChatBgCacheData cacheData=new CxChatBgCacheData(this);
		CxChatBgList list = cacheData.queryCacheData(CxGlobalParams.getInstance().getUserId());
		if(list!=null && list.getData()!=null){
			CxGlobalParams.getInstance().setChatbgData(list.getData());	
			setChatBgSmall();
		}
		int version = cacheData.queryCacheVersion(CxGlobalParams.getInstance().getUserId());
		CxSettingsCommonApi.getInstance().requestBackgroundConfig(this, version, configCaller);
		
	}

	private void init() {
		
		mMenuBtn = (Button)this.findViewById(R.id.cx_fa_setting_menu);
		mMenuBtn.setOnClickListener(itemClickListener);
		mMenuBtn.setText(getString(R.string.cx_fa_navi_back));
		
//		TextView mVersionName = (TextView) this.findViewById(R.id.about_version_name);
//		mVersionName.setText(RkResourceString.getInstance().str_setting_about_version);
		
		modifyImgLayer = (LinearLayout)this.findViewById(R.id.modify_chat_icon_and_chatbg_layout);
		modifyChatBgLayer = (LinearLayout)this.findViewById(R.id.modify_chat_bg);
		changeHeadLayer = (LinearLayout)this.findViewById(R.id.modify_my_head);
		
		mEditPushSound = (LinearLayout)this.findViewById(R.id.edit_push_sound);
		mEditPushSound.setOnClickListener(itemClickListener);
		
		clearCacheLayer = (LinearLayout)this.findViewById(R.id.user_clear_cache_view);
		clearImageCache = (LinearLayout)this.findViewById(R.id.clear_image_cache);
		clearChatRecord = (LinearLayout)this.findViewById(R.id.clear_chat_cache);
		clearImageCache.setOnClickListener(itemClickListener);
		clearChatRecord.setOnClickListener(itemClickListener);
		
		//navigateAndSuggestLayer = (LinearLayout)this.findViewById(R.id.help_suggest_layer);
		aboutVersion = (LinearLayout)this.findViewById(R.id.about_version);
		aboutVersionBottomLine = (View)this.findViewById(R.id.about_version_bottom_line);
		unbindPair = (LinearLayout)this.findViewById(R.id.unbindPair);
		
		versionBtn = (Button)this.findViewById(R.id.cx_fa_change_version_img_btn);
		exitButton = (Button)this.findViewById(R.id.cx_fa_exit_img_btn);
		if(version_type==1){
			versionBtn.setText(R.string.cx_fa_setting_version_type_kid);
		}else{
			versionBtn.setText(R.string.cx_fa_setting_version_type_nokid);
		}
		
		mHeadImage = (CxImageView)this.findViewById(R.id.cx_fa_head_img);
		modifyMyHeadBottomLine = (View)this.findViewById(R.id.modify_my_head_bottom_line);
		mChatBgImage = (CxImageView)this.findViewById(R.id.cx_fa_chat_bg_img);

		mHeadImage.displayImage(ImageLoader.getInstance(), 
				CxGlobalParams.getInstance().getIconSmall(), 
				CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
				CxGlobalParams.getInstance().getSmallImgConner());

		CxLog.i("men", ">>>>>>>>>>>>>>1");
		
		
		mLockScreen = (CheckBox)this.findViewById(R.id.cx_fa_lockscreen);
		mSoundForChat = (CheckBox)this.findViewById(R.id.chat_sound_btn);
		mShockBtnForChat = (CheckBox)this.findViewById(R.id.chat_shock_btn);
		mEarphoneForChat = (CheckBox)this.findViewById(R.id.chat_earphone_btn);
		
		SharedPreferences sp = CxSettingActivity.this.getSharedPreferences(
				CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
		String lockPassword = sp.getString(CxGlobalConst.S_LOCKSCREEN_FIELD, null);
		mLockScreen.setChecked(null != lockPassword);
		SharedPreferences chatSp = CxSettingActivity.this.getSharedPreferences(
				CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
		boolean chatSound = chatSp.getBoolean(CxGlobalConst.S_CHAT_SOUND, false);
		boolean chatShock = chatSp.getBoolean(CxGlobalConst.S_CHAT_SHOCK, false);
		boolean chatEarphone = chatSp.getBoolean(CxGlobalConst.S_CHAT_EARPHONE, false);
		mSoundForChat.setChecked(chatSound);
		mShockBtnForChat.setChecked(chatShock);
		mEarphoneForChat.setChecked(chatEarphone);
		
		mLockScreen.setOnCheckedChangeListener(checkChangeListener);
		mSoundForChat.setOnCheckedChangeListener(checkChangeListener);
		mShockBtnForChat.setOnCheckedChangeListener(checkChangeListener);
		mEarphoneForChat.setOnCheckedChangeListener(checkChangeListener);
		
		//navigateAndSuggestLayer.setOnClickListener(itemClickListener);
		aboutVersion.setOnClickListener(itemClickListener);
		unbindPair.setOnClickListener(itemClickListener);
		modifyChatBgLayer.setOnClickListener(itemClickListener);
		changeHeadLayer.setOnClickListener(itemClickListener);
		
		versionBtn.setOnClickListener(itemClickListener);
		exitButton.setOnClickListener(itemClickListener);
//		fetchMyInfo(RkGlobalParams.getInstance().getUserId());
		//分登录状态进行UI的变化加载
		changeViewByLogin();
		
//		((RkMain)RkSettingActivity.this).closeMenu();	
		
	}
	
	
	OnCheckedChangeListener checkChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (buttonView.getId() == R.id.cx_fa_lockscreen) {
				Intent toLock = new Intent(CxSettingActivity.this, CxLockScreen.class);
				toLock.putExtra(CxGlobalConst.S_LOCKSCREEN_TYPE, 1); //set password
				startActivity(toLock);
				CxSettingActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				return;
			}
			SharedPreferences sp = CxSettingActivity.this.getSharedPreferences(
					CxGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			switch (buttonView.getId()) {
			case R.id.chat_sound_btn:
				editor.putBoolean(CxGlobalConst.S_CHAT_SOUND, isChecked);
				CxGlobalParams.getInstance().setChatSound(isChecked);
				break;
			case R.id.chat_shock_btn:
				editor.putBoolean(CxGlobalConst.S_CHAT_SHOCK, isChecked);
				CxGlobalParams.getInstance().setChatShock(isChecked);
				break;
			case R.id.chat_earphone_btn:
				editor.putBoolean(CxGlobalConst.S_CHAT_EARPHONE, isChecked);
				CxGlobalParams.getInstance().setChatEarphone(isChecked);
				break;
			default:
				break;
			}
			editor.commit();
			
		}
	};
	
	
	//根据是否结对来切换设置界面的UI
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private synchronized void changeViewByLogin(){
		int pairStatus = CxGlobalParams.getInstance().getPair();
		if (0 == pairStatus) { //非结对状态
			
			// "关于老公/老婆" 那一块少了一个“解除绑定关系"，要把关于的背景重设。否则就会出现两个边框的情况

			aboutVersion.setBackgroundResource(R.drawable.cx_fa_setting_bg_blue_focused_all);
			aboutVersionBottomLine.setVisibility(View.GONE);			
			unbindPair.setVisibility(View.GONE);
			return;
		}
		//以下是结对状态
//		modifyImgLayer.setVisibility(View.VISIBLE);
//		modifyChatBgLayer.setVisibility(View.VISIBLE);
//		clearCacheLayer.setVisibility(View.GONE); //这个版本先隐藏清除声音和图片的缓存功能
		
		int single_mode = CxGlobalParams.getInstance().getSingle_mode();
		if(single_mode==1){
			aboutVersion.setBackgroundResource(R.drawable.cx_fa_setting_bg_blue_focused_all);
			aboutVersionBottomLine.setVisibility(View.GONE);			
			unbindPair.setVisibility(View.GONE);
		}
	}
	
	public final static int MODIFY_HEAD_REQUEST = 1; //修改头像
	private final int MODIFY_BACKGROUND_REQUEST = 2; //修改聊天背景
	OnClickListener itemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.edit_push_sound:
				Intent editPush = new Intent(CxSettingActivity.this, CxSetPushSound.class);
				startActivity(editPush);
				CxSettingActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			case R.id.cx_fa_setting_menu:
				
				back();
				
				
				//((RkMain)RkSettingActivity.this).toggleMenu();
//				((RkMain)RkSettingActivity.this).changeFragment(RkMain.MORE);		// 让它退到"更多"页.(现在"设置"从"更多"页进入)  dujianyin 2013.10.16
				break;
			case R.id.modify_chat_bg: //修改聊天背景
				
				if(CxGlobalParams.getInstance().getChatbgData()==null){
					ToastUtil.getSimpleToast(CxSettingActivity.this, R.drawable.chatbg_update_error, "配置文件下载失败了，点我会崩的！\n还是不要了。。。", 1).show();
					break;
				}
				Intent changeChatBackground = new Intent(CxSettingActivity.this, CxChatBackgroundSelecter.class);
				startActivityForResult(changeChatBackground, MODIFY_BACKGROUND_REQUEST);
				CxSettingActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			case R.id.modify_my_head: //修改头像
				Intent selectImageForHead = new Intent(CxSettingActivity.this, ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom =true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForHead, MODIFY_HEAD_REQUEST);
				break;
//			case R.id.help_suggest_layer: //帮助与反馈
//				//Intent toHelpOrSuggest = new Intent(RkSettingActivity.this, RkHelpSuggest.class);
//				//startActivity(toHelpOrSuggest);
//				
//				Intent toSuggest = new Intent(RkSettingActivity.this, RkUserSuggest.class);
//				startActivity(toSuggest);
//				break;
			case R.id.about_version: //关于老公/老婆
				Intent toAboutVersion = new Intent(CxSettingActivity.this, CxAboutVersion.class);
				startActivity(toAboutVersion);
				CxSettingActivity.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			case R.id.unbindPair: //解绑
				
				DialogUtil du = DialogUtil.getInstance();
				
				du.setOnSureClickListener(new OnSureClickListener() {
					
					@Override
					public void surePress() {
//						RkLoadingUtil.getInstance().showLoading(RkSettingActivity.this, true);
						DialogUtil.getInstance().getLoadingDialogShow(CxSettingActivity.this, -1);
						CxPairApi.getInstance().dismissInvite(dismissPairCallback);	
						//RkNeighbourList.getInstance().clearAllData();// 解绑后清除密邻列表本地数据
					}
				});
				
				du.getSimpleDialog(CxSettingActivity.this, getString(R.string.cx_fa_unbind_relationship_text), 
						getString(R.string.cx_fa_unbind_tip), null, null).show();
				
				
//				AlertDialog unBindPairDialog = new AlertDialog.Builder(RkSettingActivity.this)
//				.setNegativeButton(RkSettingActivity.this.getString(R.string.cx_fa_cancel_button_text), null)
//				.setPositiveButton(RkSettingActivity.this.getString(R.string.cx_fa_confirm_text), 
//						new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//
//						RkLoadingUtil.getInstance().showLoading(RkSettingActivity.this, true);
//						RkPairApi.getInstance().dismissInvite(dismissPairCallback);
//						//RkNeighbourList.getInstance().clearAllData();// 解绑后清除密邻列表本地数据
//					}
//				}).create();
//				unBindPairDialog.setTitle(RkSettingActivity.this.getString(R.string.cx_fa_unbind_relationship_text));
//				unBindPairDialog.setIcon(RkSettingActivity.this.getResources().getDrawable(android.R.color.transparent));
//				unBindPairDialog.setMessage(RkSettingActivity.this.getString(R.string.cx_fa_unbind_tip));
//				unBindPairDialog.show();
				
				break;
				
			case R.id.cx_fa_exit_img_btn: //退出账号
				
				DialogUtil dul = DialogUtil.getInstance();
				
				dul.setOnSureClickListener(new OnSureClickListener() {
					
					@Override
					public void surePress() {
						try {
//							RkLoadingUtil.getInstance().showLoading(RkSettingActivity.this, false);
							DialogUtil.getInstance().getLoadingDialogShow(CxSettingActivity.this, -1);
							AccountApi.getInstance().requestLogout(logoutCallback);
						} catch (Exception e) {
							e.printStackTrace();
							CxLog.i("exit product error", ""+e.getMessage());
//							RkLoadingUtil.getInstance().dismissLoading();
							DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
						}
					}
				});
				
				dul.getSimpleDialog(CxSettingActivity.this, getString(R.string.cx_fa_logout_current_account), 
						getString(R.string.cx_fa_exit_account_tip), null, null).show();
				

				break;
			case R.id.cx_fa_change_version_img_btn: //切换版本
				
				DialogUtil dul1 = DialogUtil.getInstance();
				String str="";
				String title="";
				int toVersion=1;
				if(version_type==1){
					str=getString(R.string.cx_fa_setting_version_type_kid_text);
					title=getString(R.string.cx_fa_setting_version_type_kid);
					toVersion=2;
				}else{
					str=getString(R.string.cx_fa_setting_version_type_nokid_text);
					title=getString(R.string.cx_fa_setting_version_type_nokid);	
					toVersion=1;
				}
				final int version=toVersion;
				dul1.setOnSureClickListener(new OnSureClickListener() {
					
					@Override
					public void surePress() {
						try {
							DialogUtil.getInstance().getLoadingDialogShow(CxSettingActivity.this, -1);
							UserApi.getInstance().updateUserProfile(null, null,null, null, null,null,null,version+"", versionCaller);
						} catch (Exception e) {
							e.printStackTrace();
							CxLog.i("exit product error", ""+e.getMessage());
							DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
						}
					}
				});
				
				dul1.getSimpleDialog(CxSettingActivity.this, title, str, "切换", null).show();
				
				
				break;
				
			case R.id.clear_image_cache: //清除图片缓存
                DialogUtil clearImageCacheDialog = DialogUtil.getInstance();
                clearImageCacheDialog.setOnSureClickListener(new OnSureClickListener() {

                    @Override
                    public void surePress() {
                        new ClearCache(true).execute();
                    }
                });
                clearImageCacheDialog.getSimpleDialog(CxSettingActivity.this, null, getString(R.string.cx_fa_setting_clear_imgcache_message),
                        null, null).show();
				break;
				
			case R.id.clear_chat_cache: //清除聊天记录
                DialogUtil clearChatCacheDialog = DialogUtil.getInstance();
                clearChatCacheDialog.setOnSureClickListener(new OnSureClickListener() {

                    @Override
                    public void surePress() {
                        new ClearCache(false).execute();
                    }
                });
                clearChatCacheDialog.getSimpleDialog(CxSettingActivity.this, null, getString(R.string.cx_fa_setting_clear_chatrecord_message),
                        null, null).show();
				
				break;

			default:
				break;
			}
			
			
			
		}
	};
	
	
	
	/*账号登出的回调*/
	JSONCaller logoutCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			if (null == result) {
				return -1;
			}
			CxLogoutResponce logout = (CxLogoutResponce)result;
			if (0 != logout.getRc()) { //退出账号失败
				//退出失败
				new Handler(Looper.getMainLooper()){
					public void handleMessage(Message msg) {
						Toast tempToast = Toast.makeText(CxSettingActivity.this, CxSettingActivity.this.getString(
								R.string.cx_fa_exit_fail), Toast.LENGTH_SHORT);
						tempToast.setGravity(Gravity.CENTER, 0, 0);
						tempToast.show();
					};
				}.sendEmptyMessageDelayed(0, 10);
				return logout.getRc();
			}
			//以下是正常退出账号（关闭所有界面、清除全局、局部变量和service，并弹出登录界面）
			//清空账号信息
			CxThirdAccessTokenKeeper.clear(CxSettingActivity.this);
			//清除结对邀请码
			try {
				SharedPreferences spf = CxSettingActivity.this.getSharedPreferences(
						CxGlobalConst.S_PAIR_CH_NAME, Context.MODE_PRIVATE);
				Editor edt = spf.edit();
				edt.clear();
				edt.commit();
			} catch (Exception e) {
			}
			//重置isLogin
			CxLog.i("RkMain_men", CxGlobalParams.getInstance().isLogin()+">>>>>>>>>>>3"+CxAuthenNew.isShown());
			CxGlobalParams.getInstance().setIsLogin(false); //退出
			back();
			return 0;
		}
	};
	
	//解除绑定回调
	JSONCaller dismissPairCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			CxLog.i("", "dismiss pair callback----------------"+System.currentTimeMillis());
			if (null == result) {
				unBindPair.sendEmptyMessage(-1);
				return -1;
			}
			CxParseBasic unbindResult = (CxParseBasic)result;
			if (0 != unbindResult.getRc()) {
				Message msg = new Message();
				msg.obj = unbindResult.getMsg();
				msg.what = 1;
				unBindPair.sendMessage(msg);
				return 1;
			}
			CxLog.i("", "dismiss pair successfully----------------"+System.currentTimeMillis());
//			RkGlobalParams.getInstance().setUnBindSelf(true);
			//这里应该要给long polling 
			CxGlobalParams.getInstance().setDismissPair(true); //主动解除结对
			CxGlobalParams.getInstance().setPair(0);
			
			SharedPreferences sp = CxApplication.getInstance().getContext().getSharedPreferences(PROFILE_FILE_NAME, Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putInt(PROFILE_FIELD_PAIR, 0);
			edit.putString(PROFILE_FIELD_MATE_ID, "");
			edit.commit();
			
			unBindPair.sendEmptyMessage(0);
			
			return 0;
		}
	};
	
	//解除绑定回调
	Handler unBindPair = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (null == CxSettingActivity.this) {
				return;
			}
			switch (msg.what) {
			case -1: //网络等原因导致解除结对失败
				Toast tempToast = Toast.makeText(CxSettingActivity.this, CxSettingActivity.this.getString(
						R.string.cx_fa_net_err), Toast.LENGTH_LONG);
				tempToast.setGravity(Gravity.CENTER, 0, 0);
				tempToast.show();
				break;
			case 1: //服务器告知解除失败
				
				String toastStr = "解绑失败!";
				if(msg!=null && msg.obj!=null){
					toastStr = msg.obj.toString();
				}
				
				Toast serverToast = Toast.makeText(CxSettingActivity.this, toastStr, Toast.LENGTH_LONG);
				serverToast.setGravity(Gravity.CENTER, 0, 0);
				serverToast.show();
				break;
			case 0: //成功解除
				//告知数据模型
//				RkGlobalParams.getInstance().setPair(0); //0表示未结对
				
				//告知用户
				Toast successToast = Toast.makeText(CxSettingActivity.this, CxSettingActivity.this.getString(
						R.string.cx_fa_unbind_success), Toast.LENGTH_LONG);
				successToast.setGravity(Gravity.CENTER, 0, 0);
				successToast.show();
				back();
				break;

			default:
				break;
			}
		};
	};
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (null == data) {
			return;
		}
		if (MODIFY_HEAD_REQUEST == requestCode) { //头像调用
			CxLog.i("cpuimage back ", data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI));
			if (!TextUtils.isEmpty(data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI))) {
				try {
					String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
					CxLog.i("get image again", imagePath);
					imagePath = imagePath.replace("file://", "");
					CxLoadingUtil.getInstance().showLoading(CxSettingActivity.this, true);
					
					CxSendImageApi.getInstance().sendHeadImage(imagePath, 
							SendHeadImageType.HEAD_ME, modifyHeadImag);
				} catch (Exception e) {
					e.printStackTrace();
					CxLoadingUtil.getInstance().dismissLoading();
					displayResultInfo(getString(R.string.cx_fa_modify_headimg_fail),0);
				}
			}else{
				displayResultInfo(getString(R.string.cx_fa_modify_headimg_fail),0);
			}
			
			return;
		}
		
		if (MODIFY_BACKGROUND_REQUEST == requestCode) { //聊天背景调用
			CxLog.i("", data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI));
			return ;
		}
		
	};
	
	
	JSONCaller modifyHeadImag = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			new Handler(CxSettingActivity.this.getMainLooper()){
				public void handleMessage(Message msg) {
					CxLoadingUtil.getInstance().dismissLoading();
				};
			}.sendEmptyMessage(1);
			if (null == result) {
				displayResultInfo(getString(R.string.cx_fa_modify_headimg_fail),0);
				return -1;
			}
			CxChangeHead changeHeadResult = null;
			try {
				changeHeadResult = (CxChangeHead)result;
			} catch (Exception e) {
			}
			if (null == changeHeadResult) {
				displayResultInfo(getString(R.string.cx_fa_modify_headimg_fail),0);
				return -2;
			}
			if(0 != changeHeadResult.getRc()){
				displayResultInfo(changeHeadResult.getMsg(),0);
				return changeHeadResult.getRc();
			}
			//修改头像成功
			CxChangeHeadDataField headData = changeHeadResult.getData();
			CxGlobalParams.getInstance().setIconSmall(headData.getIcon_small());
			CxGlobalParams.getInstance().setIconBig(headData.getIcon_big());
			CxGlobalParams.getInstance().setIconMid(headData.getIcon_mid());
			
			return 0;
		}
	};
	
	/**
	 * 
	 * @param info
	 * @param number
	 *            0 失败；1 成功；2 不要图。
	 */
	private void displayResultInfo(String info, int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1 = number;
		new Handler(CxSettingActivity.this.getMainLooper()) {
			public void handleMessage(Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				int id = -1;
				if (msg.arg1 == 0) {
					id = R.drawable.chatbg_update_error;
				} else if (msg.arg1 == 1) {
					id = R.drawable.chatbg_update_success;
				}
				ToastUtil.getSimpleToast(CxSettingActivity.this,
						id, msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	//全局监听
	class CurrentObserver extends CxObserverInterface{

		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}
			
			if (CxGlobalParams.ICON_SMALL.equals(actionTag)) { //头像
				CxLog.i("notify update head", actionTag);
				if (null == CxSettingActivity.this) {
					return;
				}
				mHeadImage.displayImage(ImageLoader.getInstance(), 
						CxGlobalParams.getInstance().getIconSmall(), 
						R.drawable.cx_fa_wf_icon_small, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
				return;
			}
			
			if (CxGlobalParams.CHAT_SMALL.equals(actionTag)) { //聊天背景
				setChatBgSmall();
				return;
			}
			
			if (CxGlobalParams.IS_LOGIN.equals(actionTag)) { //登出
				//此界面在主界面，不考虑登录状态
				return;
			}
			
			if (CxGlobalParams.SINGLE_MODE.equals(actionTag)) { 
				int mode = CxGlobalParams.getInstance().getSingle_mode();	
				int pair = CxGlobalParams.getInstance().getPair();
				if(pair==1 && mode==0){
					new Handler(getMainLooper()){
						public void handleMessage(Message msg) {
							aboutVersionBottomLine.setVisibility(View.VISIBLE);
							unbindPair.setVisibility(View.VISIBLE);
						};
					}.sendEmptyMessage(0);
				}
				return;
			}
			changeViewByLogin(); //绑定状态需要切换UI
			
		}
		
	}
	
	//清除图片缓存和聊天记录
	class ClearCache extends AsyncTask<Object, Integer, Integer>{

		private boolean mClearImageFlag = true;
		public ClearCache(boolean isClearImage){
			super();
			mClearImageFlag = isClearImage;
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			if (mClearImageFlag) { //清除图片缓存

				ImageLoader.getInstance().clearDiscCache();
                clearSpecDir(CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH+File.separator+"audio");
			}else{ //清除聊天记录
				ChatFragment.getInstance().clearAllMessages();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			CxLoadingUtil.getInstance().dismissLoading();
			if(mClearImageFlag){
			    ToastUtil.getSimpleToast(CxSettingActivity.this, -2, getString(R.string.cx_fa_setting_clear_imgcache_message_sucess), 1).show();
			} else {
			    ToastUtil.getSimpleToast(CxSettingActivity.this, -2, getString(R.string.cx_fa_setting_clear_chatrecord_message_sucess), 1).show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			CxLoadingUtil.getInstance().showLoading(CxSettingActivity.this, false);
			super.onPreExecute();
		}
		
	}
	
	private void clearSpecDir(String dirName){
		
		File rootFile = new File(dirName);
		if (!rootFile.exists()) {
			return;
		}
		
		if (!rootFile.isDirectory()) {
			return;
		}
		
		File []subFiles = rootFile.listFiles();
		if ( (null == subFiles) || (rootFile.length() < 1) ) {
			return;
		}
		
		int i = 0;
		int size = subFiles.length;
		while (i < size) {
			File tempFile = subFiles[i];
			if (tempFile.isDirectory()) {
				clearSpecDir(tempFile.getAbsolutePath());
				tempFile.delete();
			}else{
				tempFile.delete();
			}
			i++;
		} //end while
		
		rootFile.delete();
	}
	
	@Override
	public void onDestroy() {

		super.onDestroy();
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	
		SharedPreferences sp = CxSettingActivity.this.getSharedPreferences(
				CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
		
		String lockPassword = sp.getString(CxGlobalConst.S_LOCKSCREEN_FIELD, null);
		if(null!=lockPassword){
			mLockScreen.setSelected(true);
			mLockScreen.setButtonDrawable(R.drawable.set_checkon);
		}else{
			mLockScreen.setSelected(false);
			mLockScreen.setButtonDrawable(R.drawable.set_checkoff);
		}
		
		
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
	}

	
	private void setChatBgSmall(){
		String small = CxGlobalParams.getInstance().getChatBackgroundSmall();
		if(small==null){
			small="";
		}
		if(small.contains("@@")){
			String imageUrl=small.replace("@@", "").toLowerCase();
			String tempBbStr = "drawable"+File.separator+(imageUrl);
			CxLog.i("men", (CxSettingActivity.this==null)+"");
			int resId = getResources().getIdentifier(tempBbStr, null, CxSettingActivity.this.getPackageName());
			if(resId>0){
				mChatBgImage.setImageResource(resId);
			}else{
				if(CxGlobalParams.getInstance().getChatbgData()!=null && CxGlobalParams.getInstance().getChatbgData().getResourceUrl()!=null){
					String url=CxGlobalParams.getInstance().getChatbgData().getResourceUrl()+File.separator+"a_"+small.replace("@@", "")+"_xhd.png";
					mChatBgImage.displayImage(ImageLoader.getInstance(), url, CxResourceDarwable.getInstance().dr_chat_chatbg_thumbnail_default, false, 0);
				}else{
					mChatBgImage.setImageResource(CxResourceDarwable.getInstance().dr_chat_chatbg_thumbnail_default);
				}
			}
		}else{
			mChatBgImage.displayImage(ImageLoader.getInstance(), small, CxResourceDarwable.getInstance().dr_chat_chatbg_thumbnail_default, false, 0);
		}
	}
	
	
	JSONCaller configCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {
				
				return -1;
			}
			CxChatBgList  list= null;
			try {
				list = (CxChatBgList) result;
			} catch (Exception e) {
			}
			if (null == list) {
				return -2;
			}
			int rc = list.getRc();
			if (0 != rc) {

				return rc;
			}
		
			ChatBgData data = list.getData();
			if(data.getItems()==null || data.getItems().size()<1){
				return -3;
			}
			CxGlobalParams.getInstance().setChatbgData(data);
			new Handler(getMainLooper()){
				public void handleMessage(Message msg) {
					setChatBgSmall();
				};
			}.sendEmptyMessage(0);
			
			return 0;	
		}
	};
	
	
	
	JSONCaller versionCaller = new JSONCaller() {

		@Override
		public int call(Object result) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1500);
			
			if (null == result) {

				displayResultInfo(getString(R.string.cx_fa_net_response_code_null), 0);
				return -1;
			}

			CxUserProfile userInitInfo = null;
			try {
				userInitInfo = (CxUserProfile) result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == userInitInfo || userInitInfo.getData() == null) {
				displayResultInfo(getString(R.string.cx_fa_net_response_code_null), 0);
				//
				return -1;
			}
			if (0 != userInitInfo.getRc()) {
				displayResultInfo(getString(R.string.cx_fa_net_response_code_fail), 0);
				return -2;
			}

			SharedPreferences sp = getSharedPreferences(PROFILE_FILE_NAME,Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putInt("version_type", userInitInfo.getData().getVersion_type());
			edit.commit();
			
			version_type=userInitInfo.getData().getVersion_type();
			
			new Handler(CxSettingActivity.this.getMainLooper()) {
				public void handleMessage(Message msg) {
					if(version_type==1){
						versionBtn.setText(R.string.cx_fa_setting_version_type_kid);
					}else{
						versionBtn.setText(R.string.cx_fa_setting_version_type_nokid);
					}
				};
			}.sendEmptyMessage(1);
			


			CxGlobalParams.getInstance().setVersion_type(userInitInfo.getData().getVersion_type());
			displayResultInfo("切换成功", 1);
			return 0;
		}
	};
	
	
	private LinearLayout modifyImgLayer;
	private Button versionBtn;
	private int version_type;
	
	
	
	public void fetchMyInfo(String uid) {
        UserApi userApi = UserApi.getInstance();

        userApi.getUserProfile(uid, new ConnectionManager.JSONCaller() {

            @Override
            public int call(Object data) {
                if (null == data) {
            
                    return -1;
                }
                
                CxUserProfile userInitInfo = null;
                try {
                    userInitInfo = (CxUserProfile)data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == userInitInfo) {
             
                    return -1;
                }
                if (0 != userInitInfo.getRc()) {
                    return -2;
                }
                //获取成功,初始化用户信息，之后就进入主界面
                CxUserProfileDataField profile = userInitInfo.getData();
                if (null == profile) {
                    return -3;
                }
                
                String tempMateId = profile.getPartner_id();
                
                if (TextUtils.isEmpty(tempMateId)) { //如果这种情况取到对方的UID为null，认为是未结对
                    return 0;
                }
                
                CxGlobalParams global = CxGlobalParams.getInstance();
                global.setVersion(profile.getGender());
                global.setIconBig(profile.getIcon_big());
                global.setIconMid(profile.getIcon_mid());
                global.setIconSmall(profile.getIcon_small());
//              global.setPartnerName(profile.getName());   
                global.setZoneBackground(profile.getBg_big()); //二人空间的背景图
                global.setChatBackgroundBig(profile.getChat_big());
//                global.setChatBackgroundSmall(profile.getChat_small());

                return 0;
            }

        });
    }	
	
	private void back(){
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
	
	
	
	
	
	
	
}
