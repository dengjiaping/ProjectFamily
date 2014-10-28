package com.chuxin.family.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.HttpApi;
import com.chuxin.family.net.CxPairApi;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.CxSettingsCommonApi;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxSettingsParser.SendHeadImageType;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxChatBgList;
import com.chuxin.family.parse.been.CxEmotionConfigList;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.data.ChatBgData;
import com.chuxin.family.parse.been.data.EmotionList;
import com.chuxin.family.parse.been.data.CxChangeHeadDataField;
import com.chuxin.family.parse.been.data.CxUserProfileDataField;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.views.chat.EmotionParam;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;
import com.chuxin.family.R.drawable;

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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * setting需要从fragment该为activity  所以重写了一个类  RkSettingActivity  这个类先不用了
 * @author Administrator
 *
 */
//public class RkSettingFragment extends Fragment {
//	
//	LinearLayout modifyChatBgLayer, changeHeadLayer;
//	LinearLayout  aboutVersion, unbindPair; //关于老公/老婆,解绑
//	LinearLayout clearCacheLayer; //包括清楚图片缓存和清空聊天记录user_logined_operator_view
//	LinearLayout clearImageCache, clearChatRecord; //清楚图片缓存，清空聊天记录
//	private CurrentObserver mGlobalObserver;
//	private Button exitButton;
//	
//	private CheckBox mLockScreen, mSoundForChat, mShockBtnForChat,mEarphoneForChat;
//	
//	private RkImageView mHeadImage, mChatBgImage;
//	
//	private Button mMenuBtn;
//	
//	private LinearLayout mEditPushSound;
//	private View aboutVersionBottomLine, modifyMyHeadBottomLine;			// 关于分割线
//	
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		RkChatBgCacheData cacheData=new RkChatBgCacheData(getActivity());
//		RkChatBgList list = cacheData.queryCacheData(RkGlobalParams.getInstance().getUserId());
//		if(list!=null && list.getData()!=null){
//			RkGlobalParams.getInstance().setChatbgData(list.getData());		
//		}
//		int version = cacheData.queryCacheVersion(RkGlobalParams.getInstance().getUserId());
//		RkSettingsCommonApi.getInstance().requestBackgroundConfig(getActivity(), version, configCaller);
//	}
//	
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View tempView = inflater.inflate(R.layout.cx_fa_fragment_setting, null);
//		
//		mMenuBtn = (Button)tempView.findViewById(R.id.cx_fa_setting_menu);
//		mMenuBtn.setOnClickListener(itemClickListener);
//		mMenuBtn.setText(getString(R.string.cx_fa_navi_back));
//		
//		TextView mVersionName = (TextView) tempView.findViewById(R.id.about_version_name);
//		mVersionName.setText(RkResourceString.getInstance().str_setting_about_version);
//		
//		modifyChatBgLayer = (LinearLayout)tempView.findViewById(R.id.modify_chat_bg);
//		changeHeadLayer = (LinearLayout)tempView.findViewById(R.id.modify_my_head);
//		
//		mEditPushSound = (LinearLayout)tempView.findViewById(R.id.edit_push_sound);
//		mEditPushSound.setOnClickListener(itemClickListener);
//		
//		clearCacheLayer = (LinearLayout)tempView.findViewById(R.id.user_clear_cache_view);
//		clearImageCache = (LinearLayout)tempView.findViewById(R.id.clear_image_cache);
//		clearChatRecord = (LinearLayout)tempView.findViewById(R.id.clear_chat_cache);
//		clearImageCache.setOnClickListener(itemClickListener);
//		clearChatRecord.setOnClickListener(itemClickListener);
//		
//		//navigateAndSuggestLayer = (LinearLayout)tempView.findViewById(R.id.help_suggest_layer);
//		aboutVersion = (LinearLayout)tempView.findViewById(R.id.about_version);
//		aboutVersionBottomLine = (View)tempView.findViewById(R.id.about_version_bottom_line);
//		unbindPair = (LinearLayout)tempView.findViewById(R.id.unbindPair);
//		
//		exitButton = (Button)tempView.findViewById(R.id.cx_fa_exit_img_btn);
//		
//		mHeadImage = (RkImageView)tempView.findViewById(R.id.cx_fa_head_img);
//		modifyMyHeadBottomLine = (View)tempView.findViewById(R.id.modify_my_head_bottom_line);
//		mChatBgImage = (RkImageView)tempView.findViewById(R.id.cx_fa_chat_bg_img);
//		/*mHeadImage.setImage(RkGlobalParams.getInstance().getIconSmall(), 
//				false, 44, RkSettingFragment.this, "head", 
//				RkSettingFragment.this.getActivity());*/
//		mHeadImage.displayImage(ImageLoader.getInstance(), 
//				RkGlobalParams.getInstance().getIconSmall(), 
//				R.drawable.pair_logo, true, 
//				RkGlobalParams.getInstance().getSmallImgConner());
////		mChatBgImage.setChatbgImage(RkGlobalParams.getInstance().getChatBackgroundSmall(), 
////				false, 44, RkSettingFragment.this, "head", 
////				RkSettingFragment.this.getActivity());
//		RkLog.i("men", ">>>>>>>>>>>>>>1");
//		
//		
//		mLockScreen = (CheckBox)tempView.findViewById(R.id.cx_fa_lockscreen);
//		mSoundForChat = (CheckBox)tempView.findViewById(R.id.chat_sound_btn);
//		mShockBtnForChat = (CheckBox)tempView.findViewById(R.id.chat_shock_btn);
//		mEarphoneForChat = (CheckBox)tempView.findViewById(R.id.chat_earphone_btn);
//		
//		SharedPreferences sp = getActivity().getSharedPreferences(
//				RkGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
//		String lockPassword = sp.getString(RkGlobalConst.S_LOCKSCREEN_FIELD, null);
//		mLockScreen.setChecked(null != lockPassword);
//		SharedPreferences chatSp = getActivity().getSharedPreferences(
//				RkGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
//		boolean chatSound = chatSp.getBoolean(RkGlobalConst.S_CHAT_SOUND, false);
//		boolean chatShock = chatSp.getBoolean(RkGlobalConst.S_CHAT_SHOCK, false);
//		boolean chatEarphone = chatSp.getBoolean(RkGlobalConst.S_CHAT_EARPHONE, false);
//		mSoundForChat.setChecked(chatSound);
//		mShockBtnForChat.setChecked(chatShock);
//		mEarphoneForChat.setChecked(chatEarphone);
//		
//		mLockScreen.setOnCheckedChangeListener(checkChangeListener);
//		mSoundForChat.setOnCheckedChangeListener(checkChangeListener);
//		mShockBtnForChat.setOnCheckedChangeListener(checkChangeListener);
//		mEarphoneForChat.setOnCheckedChangeListener(checkChangeListener);
//		
//		//navigateAndSuggestLayer.setOnClickListener(itemClickListener);
//		aboutVersion.setOnClickListener(itemClickListener);
//		unbindPair.setOnClickListener(itemClickListener);
//		modifyChatBgLayer.setOnClickListener(itemClickListener);
//		changeHeadLayer.setOnClickListener(itemClickListener);
//		
//		exitButton.setOnClickListener(itemClickListener);
//		fetchMyInfo(RkGlobalParams.getInstance().getUserId());
//		//分登录状态进行UI的变化加载
//		changeViewByLogin();
//		
//		((RkMain)getActivity()).closeMenu();	
//		
//		return tempView;
//	}
//	
//	
//	
//	OnCheckedChangeListener checkChangeListener = new OnCheckedChangeListener() {
//		
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//			if (buttonView.getId() == R.id.cx_fa_lockscreen) {
//				Intent toLock = new Intent(getActivity(), RkLockScreen.class);
//				toLock.putExtra(RkGlobalConst.S_LOCKSCREEN_TYPE, 1); //set password
//				startActivity(toLock);
//				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
//				return;
//			}
//			SharedPreferences sp = getActivity().getSharedPreferences(
//					RkGlobalConst.S_CHAT_NAME, Context.MODE_PRIVATE);
//			SharedPreferences.Editor editor = sp.edit();
//			switch (buttonView.getId()) {
//			case R.id.chat_sound_btn:
//				editor.putBoolean(RkGlobalConst.S_CHAT_SOUND, isChecked);
//				RkGlobalParams.getInstance().setChatSound(isChecked);
//				break;
//			case R.id.chat_shock_btn:
//				editor.putBoolean(RkGlobalConst.S_CHAT_SHOCK, isChecked);
//				RkGlobalParams.getInstance().setChatShock(isChecked);
//				break;
//			case R.id.chat_earphone_btn:
//				editor.putBoolean(RkGlobalConst.S_CHAT_EARPHONE, isChecked);
//				RkGlobalParams.getInstance().setChatEarphone(isChecked);
//				break;
//			default:
//				break;
//			}
//			editor.commit();
//			
//		}
//	};
//	
//	//根据是否结对来切换设置界面的UI
//	private synchronized void changeViewByLogin(){
//		int pairStatus = RkGlobalParams.getInstance().getPair();
//		if (0 == pairStatus) { //非结对状态
//			
//			int paddingBottom = changeHeadLayer.getPaddingBottom();
//			int paddingTop = changeHeadLayer.getPaddingTop();
//			int paddingRight = changeHeadLayer.getPaddingRight();
//			int paddingLeft = changeHeadLayer.getPaddingLeft();
//			//changeHeadLayer.setBackgroundResource(R.drawable.cx_fa_round_corner_white_bg);
//			changeHeadLayer.setBackgroundDrawable(null);
//			changeHeadLayer.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
//			modifyMyHeadBottomLine.setVisibility(View.GONE);
//			
//			
//			modifyChatBgLayer.setVisibility(View.GONE);
//			clearCacheLayer.setVisibility(View.GONE);
//			
//			// "关于老公/老婆" 那一块少了一个“解除绑定关系"，要把关于的背景重设。否则就会出现两个边框的情况
//			int aboutVersionPadBottom = aboutVersion.getPaddingBottom();
//			int aboutVersionPadTop = aboutVersion.getPaddingTop();
//			int aboutVersionPadRight = aboutVersion.getPaddingRight();
//			int aboutVersionPadLeft = aboutVersion.getPaddingLeft();
//			aboutVersion.setBackgroundDrawable(null);
//			//Resource(R.drawable.cx_fa_ellipse_white_bg_border);
//			//aboutVersion.setBackgroundResource(R.drawable.cx_fa_bottum_ellipse_white_bg);
//			aboutVersion.setPadding(aboutVersionPadLeft, aboutVersionPadTop, aboutVersionPadRight, aboutVersionPadBottom);
//			aboutVersionBottomLine.setVisibility(View.GONE);
//			
//			unbindPair.setVisibility(View.GONE);
//			return;
//		}
//		//以下是结对状态
//		int paddingBottom = changeHeadLayer.getPaddingBottom();
//		int paddingTop = changeHeadLayer.getPaddingTop();
//		int paddingRight = changeHeadLayer.getPaddingRight();
//		int paddingLeft = changeHeadLayer.getPaddingLeft();
//		changeHeadLayer.setBackgroundResource(R.drawable.cx_fa_top_ellipse_white_bg);
//		changeHeadLayer.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
//		modifyChatBgLayer.setVisibility(View.VISIBLE);
//		clearCacheLayer.setVisibility(View.GONE); //这个版本先隐藏清除声音和图片的缓存功能
//		int aboutVersionPadBottom = aboutVersion.getPaddingBottom();
//		int aboutVersionPadTop = aboutVersion.getPaddingTop();
//		int aboutVersionPadRight = aboutVersion.getPaddingRight();
//		int aboutVersionPadLeft = aboutVersion.getPaddingLeft();
////		aboutVersion.setBackgroundResource(R.drawable.cx_fa_rect_white_bg);
////		aboutVersion.setPadding(aboutVersionPadLeft, aboutVersionPadTop, 
////				aboutVersionPadRight, aboutVersionPadBottom);
//		unbindPair.setVisibility(View.VISIBLE);
//		
//	}
//	
//	public final static int MODIFY_HEAD_REQUEST = 1; //修改头像
//	private final int MODIFY_BACKGROUND_REQUEST = 2; //修改聊天背景
//	OnClickListener itemClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.edit_push_sound:
//				Intent editPush = new Intent(getActivity(), RkSetPushSound.class);
//				startActivity(editPush);
//				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
//				break;
//			case R.id.cx_fa_setting_menu:
//				//((RkMain)getActivity()).toggleMenu();
//				((RkMain)getActivity()).changeFragment(RkMain.MORE);		// 让它退到"更多"页.(现在"设置"从"更多"页进入)  dujianyin 2013.10.16
//				break;
//			case R.id.modify_chat_bg: //修改聊天背景
//				
//				if(RkGlobalParams.getInstance().getChatbgData()==null){
//					ToastUtil.getSimpleToast(getActivity(), R.drawable.chatbg_update_error, "配置文件下载失败了，点我会崩的！\n还是不要了。。。", 1).show();
//					break;
//				}
//				Intent changeChatBackground = new Intent(getActivity(), RkChatBackgroundSelecter.class);
//				startActivityForResult(changeChatBackground, MODIFY_BACKGROUND_REQUEST);
//				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
//				break;
//			case R.id.modify_my_head: //修改头像
//				Intent selectImageForHead = new Intent(getActivity(), ActivitySelectPhoto.class);
//				ActivitySelectPhoto.kIsCallPhotoZoom =true;
//				ActivitySelectPhoto.kIsCallFilter = false;
//				ActivitySelectPhoto.kIsCallSysCamera = true;
//				ActivitySelectPhoto.kChoseSingle = true;
//				startActivityForResult(selectImageForHead, MODIFY_HEAD_REQUEST);
//				break;
////			case R.id.help_suggest_layer: //帮助与反馈
////				//Intent toHelpOrSuggest = new Intent(getActivity(), RkHelpSuggest.class);
////				//startActivity(toHelpOrSuggest);
////				
////				Intent toSuggest = new Intent(getActivity(), RkUserSuggest.class);
////				startActivity(toSuggest);
////				break;
//			case R.id.about_version: //关于老公/老婆
//				Intent toAboutVersion = new Intent(getActivity(), RkAboutVersion.class);
//				startActivity(toAboutVersion);
//				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
//				break;
//			case R.id.unbindPair: //解绑
//				
//				DialogUtil du = DialogUtil.getInstance();
//				
//				du.setOnSureClickListener(new OnSureClickListener() {
//					
//					@Override
//					public void surePress() {
//						RkLoadingUtil.getInstance().showLoading(getActivity(), true);
//						RkPairApi.getInstance().dismissInvite(dismissPairCallback);	
//						//RkNeighbourList.getInstance().clearAllData();// 解绑后清除密邻列表本地数据
//					}
//				});
//				
//				du.getSimpleDialog(getActivity(), getString(R.string.cx_fa_unbind_relationship_text), 
//						getString(R.string.cx_fa_unbind_tip), null, null).show();
//				
//				
////				AlertDialog unBindPairDialog = new AlertDialog.Builder(getActivity())
////				.setNegativeButton(getActivity().getString(R.string.cx_fa_cancel_button_text), null)
////				.setPositiveButton(getActivity().getString(R.string.cx_fa_confirm_text), 
////						new DialogInterface.OnClickListener() {
////					
////					@Override
////					public void onClick(DialogInterface dialog, int which) {
////						//
////						RkLoadingUtil.getInstance().showLoading(getActivity(), true);
////						RkPairApi.getInstance().dismissInvite(dismissPairCallback);
////						//RkNeighbourList.getInstance().clearAllData();// 解绑后清除密邻列表本地数据
////					}
////				}).create();
////				unBindPairDialog.setTitle(getActivity().getString(R.string.cx_fa_unbind_relationship_text));
////				unBindPairDialog.setIcon(getActivity().getResources().getDrawable(android.R.color.transparent));
////				unBindPairDialog.setMessage(getActivity().getString(R.string.cx_fa_unbind_tip));
////				unBindPairDialog.show();
//				
//				break;
//				
//			case R.id.cx_fa_exit_img_btn: //退出账号
//				
//				DialogUtil dul = DialogUtil.getInstance();
//				
//				dul.setOnSureClickListener(new OnSureClickListener() {
//					
//					@Override
//					public void surePress() {
//						try {
//							RkLoadingUtil.getInstance().showLoading(getActivity(), false);
//							AccountApi.getInstance().requestLogout(logoutCallback);
//						} catch (Exception e) {
//							e.printStackTrace();
//							RkLog.i("exit product error", ""+e.getMessage());
//							RkLoadingUtil.getInstance().dismissLoading();
//						}
//					}
//				});
//				
//				dul.getSimpleDialog(getActivity(), getString(R.string.cx_fa_logout_current_account), 
//						getString(R.string.cx_fa_exit_account_tip), null, null).show();
//				
//				
//				
//				
////				AlertDialog exitAccountDialog = new AlertDialog.Builder(getActivity())
////				.setNegativeButton(getActivity().getString(R.string.cx_fa_confirm_text), 
////						new DialogInterface.OnClickListener() {
////					
////					@Override
////					public void onClick(DialogInterface dialog, int which) {
////						try {
////							RkLoadingUtil.getInstance().showLoading(getActivity(), false);
////							AccountApi.getInstance().requestLogout(logoutCallback);
////						} catch (Exception e) {
////							e.printStackTrace();
////							RkLog.i("exit product error", ""+e.getMessage());
////							RkLoadingUtil.getInstance().dismissLoading();
////						}
////					}
////				})
////				.setPositiveButton(getActivity().getString(R.string.cx_fa_cancel_button_text), 
////						null).create();
////				exitAccountDialog.setTitle(getActivity().getString(R.string.cx_fa_logout_current_account));
////				exitAccountDialog.setIcon(getActivity().getResources().getDrawable(android.R.color.transparent));
////				exitAccountDialog.setMessage(getActivity().getString(R.string.cx_fa_exit_account_tip));
////				exitAccountDialog.show();
//				break;
//				
//			case R.id.clear_image_cache: //清除图片缓存
//				new ClearCache(true).execute();
//				break;
//				
//			case R.id.clear_chat_cache: //清除聊天记录
//				new ClearCache(false).execute();
//				break;
//
//			default:
//				break;
//			}
//			
//			
//			
//		}
//	};
//	
//	/*账号登出的回调*/
//	JSONCaller logoutCallback = new JSONCaller() {
//		
//		@Override
//		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
//			if (null == result) {
//				return -1;
//			}
//			RkLogoutResponce logout = (RkLogoutResponce)result;
//			if (0 != logout.getRc()) { //退出账号失败
//				//退出失败
//				new Handler(Looper.getMainLooper()){
//					public void handleMessage(Message msg) {
//						Toast tempToast = Toast.makeText(getActivity(), getActivity().getString(
//								R.string.cx_fa_exit_fail), Toast.LENGTH_SHORT);
//						tempToast.setGravity(Gravity.CENTER, 0, 0);
//						tempToast.show();
//					};
//				}.sendEmptyMessageDelayed(0, 10);
//				return logout.getRc();
//			}
//			//以下是正常退出账号（关闭所有界面、清除全局、局部变量和service，并弹出登录界面）
//			//清空账号信息
//			RkThirdAccessTokenKeeper.clear(getActivity());
//			//清除结对邀请码
//			try {
//				SharedPreferences spf = getActivity().getSharedPreferences(
//						RkGlobalConst.S_PAIR_CH_NAME, Context.MODE_PRIVATE);
//				Editor edt = spf.edit();
//				edt.clear();
//				edt.commit();
//			} catch (Exception e) {
//			}
//			//重置isLogin
//			RkGlobalParams.getInstance().setIsLogin(false); //退出
//			return 0;
//		}
//	};
//	
//	//解除绑定回调
//	JSONCaller dismissPairCallback = new JSONCaller() {
//		
//		@Override
//		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
//			RkLog.i("", "dismiss pair callback----------------"+System.currentTimeMillis());
//			if (null == result) {
//				unBindPair.sendEmptyMessage(-1);
//				return -1;
//			}
//			RkParseBasic unbindResult = (RkParseBasic)result;
//			if (0 != unbindResult.getRc()) {
//				Message msg = new Message();
//				msg.obj = unbindResult.getMsg();
//				msg.what = 1;
//				unBindPair.sendMessage(msg);
//				return 1;
//			}
//			RkLog.i("", "dismiss pair successfully----------------"+System.currentTimeMillis());
////			RkGlobalParams.getInstance().setUnBindSelf(true);
//			//这里应该要给long polling 
//			RkGlobalParams.getInstance().setDismissPair(true); //主动解除结对
//			RkGlobalParams.getInstance().setPair(0);
//			unBindPair.sendEmptyMessage(0);
//			return 0;
//		}
//	};
//	
//	//解除绑定回调
//	Handler unBindPair = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			if (null == getActivity()) {
//				return;
//			}
//			switch (msg.what) {
//			case -1: //网络等原因导致解除结对失败
//				Toast tempToast = Toast.makeText(getActivity(), getActivity().getString(
//						R.string.cx_fa_net_err), Toast.LENGTH_LONG);
//				tempToast.setGravity(Gravity.CENTER, 0, 0);
//				tempToast.show();
//				break;
//			case 1: //服务器告知解除失败
//				
//				String toastStr = "解绑失败!";
//				if(msg!=null && msg.obj!=null){
//					toastStr = msg.obj.toString();
//				}
//				
//				Toast serverToast = Toast.makeText(getActivity(), toastStr, Toast.LENGTH_LONG);
//				serverToast.setGravity(Gravity.CENTER, 0, 0);
//				serverToast.show();
//				break;
//			case 0: //成功解除
//				//告知数据模型
////				RkGlobalParams.getInstance().setPair(0); //0表示未结对
//				
//				//告知用户
//				Toast successToast = Toast.makeText(getActivity(), getActivity().getString(
//						R.string.cx_fa_unbind_success), Toast.LENGTH_LONG);
//				successToast.setGravity(Gravity.CENTER, 0, 0);
//				successToast.show();
//				
//				break;
//
//			default:
//				break;
//			}
//		};
//	};
//	
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode != Activity.RESULT_OK) {
//			return;
//		}
//		
//		if (null == data) {
//			return;
//		}
//		if (MODIFY_HEAD_REQUEST == requestCode) { //头像调用
//			RkLog.i("cpuimage back ", data.getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI));
//			if (!TextUtils.isEmpty(data.getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI))) {
//				try {
//					String imagePath = data.getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI);
//					RkLog.i("get image again", imagePath);
//					imagePath = imagePath.replace("file://", "");
//					RkLoadingUtil.getInstance().showLoading(getActivity(), true);
//					
//					RkSendImageApi.getInstance().sendHeadImage(imagePath, 
//							SendHeadImageType.HEAD_ME, modifyHeadImag);
//				} catch (Exception e) {
//					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
//					displayModifyHead(getString(R.string.cx_fa_modify_headimg_fail));
//				}
//			}else{
//				displayModifyHead(getString(R.string.cx_fa_modify_headimg_fail));
//			}
//			
//			return;
//		}
//		
//		if (MODIFY_BACKGROUND_REQUEST == requestCode) { //聊天背景调用
//			RkLog.i("", data.getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI));
//			return ;
//		}
//		
//	};
//	
//	
//	JSONCaller modifyHeadImag = new JSONCaller() {
//		
//		@Override
//		public int call(Object result) {
//			new Handler(getActivity().getMainLooper()){
//				public void handleMessage(Message msg) {
//					RkLoadingUtil.getInstance().dismissLoading();
//				};
//			}.sendEmptyMessage(1);
//			if (null == result) {
//				displayModifyHead(getString(R.string.cx_fa_modify_headimg_fail));
//				return -1;
//			}
//			RkChangeHead changeHeadResult = null;
//			try {
//				changeHeadResult = (RkChangeHead)result;
//			} catch (Exception e) {
//			}
//			if (null == changeHeadResult) {
//				displayModifyHead(getString(R.string.cx_fa_modify_headimg_fail));
//				return -2;
//			}
//			if(0 != changeHeadResult.getRc()){
//				displayModifyHead(changeHeadResult.getMsg());
//				return changeHeadResult.getRc();
//			}
//			//修改头像成功
//			RkChangeHeadDataField headData = changeHeadResult.getData();
//			RkGlobalParams.getInstance().setIconSmall(headData.getIcon_small());
//			RkGlobalParams.getInstance().setIconBig(headData.getIcon_big());
//			RkGlobalParams.getInstance().setIconMid(headData.getIcon_mid());
//			
//			return 0;
//		}
//	};
//	
//	private void displayModifyHead(String info){
//		Message msg = new Message();
//		msg.obj = info;
//		new Handler(getActivity().getMainLooper()){
//			public void handleMessage(Message msg) {
//				if ( (null == msg) || (null == msg.obj) ){
//					return;
//				}
//				Toast ts = Toast.makeText(getActivity(), msg.obj.toString(), 1000);
//				ts.setGravity(Gravity.CENTER, 0, 0);
//				ts.show();
//			};
//		}.sendMessage(msg);
//	}
//	
//	//全局监听
//	class CurrentObserver extends RkObserverInterface{
//
//		@Override
//		public void receiveUpdate(String actionTag) {
//			if (null == actionTag) {
//				return;
//			}
//			
//			if (RkGlobalParams.ICON_SMALL.equals(actionTag)) { //头像
//				RkLog.i("notify update head", actionTag);
//				if (null == getActivity()) {
//					return;
//				}
//				/*mHeadImage.setImage(RkGlobalParams.getInstance().getIconSmall(), 
//						false, 44, RkSettingFragment.this, "head", getActivity());*/
//				mHeadImage.displayImage(ImageLoader.getInstance(), 
//						RkGlobalParams.getInstance().getIconSmall(), 
//						R.drawable.cx_fa_wf_icon_small, true, 
//						RkGlobalParams.getInstance().getSmallImgConner());
//				return;
//			}
//			
//			if (RkGlobalParams.CHAT_SMALL.equals(actionTag)) { //聊天背景
////				mChatBgImage.setChatbgImage(RkGlobalParams.getInstance().getChatBackgroundSmall(), 
////						false, 44, RkSettingFragment.this, "head", 
////						RkSettingFragment.this.getActivity());
////				RkLog.i("men", ">>>>>>>>>>>>>>2");
////				setChatBgSmall();
//				return;
//			}
//			
//			if (RkGlobalParams.IS_LOGIN.equals(actionTag)) { //登出
//				//此界面在主界面，不考虑登录状态
//				return;
//			}
//			changeViewByLogin(); //绑定状态需要切换UI
//			
//		}
//		
//	}
//	
//	//清除图片缓存和聊天记录
//	class ClearCache extends AsyncTask<Object, Integer, Integer>{
//
//		private boolean mClearImageFlag = true;
//		public ClearCache(boolean isClearImage){
//			super();
//			mClearImageFlag = isClearImage;
//		}
//		
//		@Override
//		protected Integer doInBackground(Object... params) {
//			if (mClearImageFlag) { //清除图片缓存
//				clearSpecDir(Environment.getExternalStorageDirectory()
//						+File.separator+"chuxin"+File.separator);
//			}else{ //清除聊天记录
//				ChatFragment.getInstance().clearAllMessages();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			RkLoadingUtil.getInstance().dismissLoading();
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected void onPreExecute() {
//			RkLoadingUtil.getInstance().showLoading(getActivity(), false);
//			super.onPreExecute();
//		}
//		
//	}
//	
//	private void clearSpecDir(String dirName){
//		
//		File rootFile = new File(dirName);
//		if (!rootFile.exists()) {
//			return;
//		}
//		
//		if (!rootFile.isDirectory()) {
//			return;
//		}
//		
//		File []subFiles = rootFile.listFiles();
//		if ( (null == subFiles) || (rootFile.length() < 1) ) {
//			return;
//		}
//		
//		int i = 0;
//		int size = subFiles.length;
//		while (i < size) {
//			File tempFile = subFiles[i];
//			if (tempFile.isDirectory()) {
//				clearSpecDir(tempFile.getAbsolutePath());
//				tempFile.delete();
//			}else{
//				tempFile.delete();
//			}
//			i++;
//		} //end while
//		
//		rootFile.delete();
//	}
//	
//	@Override
//	public void onDestroy() {
//		try {
//			RkResourceManager resourceManager = RkResourceManager.getInstance(
//					RkSettingFragment.this, "head", RkSettingFragment.this.getActivity());
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//		} catch (Exception e) {
//			RkLog.e("destroy", ""+e.getMessage());
//		}
//		super.onDestroy();
//	}
//	
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		//添加对登录状态的监听
//		mGlobalObserver = new CurrentObserver(); //生成观察者实例
//		//设置观察目标
//		List<String> tags = new ArrayList<String>();
//		tags.add(RkGlobalParams.PAIR); //结对状态
//		tags.add(RkGlobalParams.IS_LOGIN);
//		tags.add(RkGlobalParams.ICON_SMALL); //头像
//		tags.add(RkGlobalParams.CHAT_SMALL); //聊天背景小图
//		//此界面只针对结对状态和登录状态进行监听
//		mGlobalObserver.setListenTag(tags); //设置观察目标
//		mGlobalObserver.setMainThread(true); //设置在UI线程执行update
//		RkGlobalParams.getInstance().registerObserver(mGlobalObserver); //注册观察者
//		super.onActivityCreated(savedInstanceState);
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		
//		setChatBgSmall();
//		SharedPreferences sp = getActivity().getSharedPreferences(
//				RkGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
//		
//		String lockPassword = sp.getString(RkGlobalConst.S_LOCKSCREEN_FIELD, null);
//		if(null!=lockPassword){
//			mLockScreen.setSelected(true);
//			mLockScreen.setButtonDrawable(R.drawable.set_checkon);
//		}else{
//			mLockScreen.setSelected(false);
//			mLockScreen.setButtonDrawable(R.drawable.set_checkoff);
//		}
//		
//		
//	}
//	
//	/**********************************************************************************************************/
//	//背景配置文件的检查更新 和下载
//	
//	@Override
//	public void onStart() {
//		super.onStart();
//		
////		setChatBgSmall();
//		
//
////		mChatBgImage.setChatbgImage(RkGlobalParams.getInstance().getChatBackgroundSmall(), 
////				false, 44, RkSettingFragment.this, "head", 
////				RkSettingFragment.this.getActivity());
//	}
//
//	
//	private void setChatBgSmall(){
//		String small = RkGlobalParams.getInstance().getChatBackgroundSmall();
//		if(small==null){
//			small="";
//		}
//		if(small.contains("@@")){
//			String imageUrl=small.replace("@@", "").toLowerCase();
//			String tempBbStr = "drawable"+File.separator+(imageUrl);
//			RkLog.i("men", (getActivity()==null)+"");
//			int resId = getResources().getIdentifier(tempBbStr, null, getActivity().getPackageName());
//			if(resId>0){
//				mChatBgImage.setImageResource(resId);
//			}else{
//				String url=RkGlobalParams.getInstance().getChatbgData().getResourceUrl()+File.separator+"a_"+small.replace("@@", "")+"_xhd.png";
//				mChatBgImage.displayImage(ImageLoader.getInstance(), url, RkResourceDarwable.getInstance().dr_chat_chatbg_thumbnail_default, false, 0);
//			}
//		}else{
//			mChatBgImage.displayImage(ImageLoader.getInstance(), small, RkResourceDarwable.getInstance().dr_chat_chatbg_thumbnail_default, false, 0);
//		}
//	}
//	
//	
//	JSONCaller configCaller=new JSONCaller() {
//		
//		@Override
//		public int call(Object result) {
//			if (null == result) {
//				
//				return -1;
//			}
//			RkChatBgList  list= null;
//			try {
//				list = (RkChatBgList) result;
//			} catch (Exception e) {
//			}
//			if (null == list) {
//				return -2;
//			}
//			int rc = list.getRc();
//			if (0 != rc) {
//
//				return rc;
//			}
//		
//			ChatBgData data = list.getData();
//			if(data.getItems()==null || data.getItems().size()<1){
//				return -3;
//			}
//			RkGlobalParams.getInstance().setChatbgData(data);		
//			return 0;	
//		}
//	};
//	
//	
//	
//	public void fetchMyInfo(String uid) {
//        UserApi userApi = UserApi.getInstance();
//
//        userApi.getUserProfile(uid, new ConnectionManager.JSONCaller() {
//
//            @Override
//            public int call(Object data) {
//                if (null == data) {
//            
//                    return -1;
//                }
//                
//                RkUserProfile userInitInfo = null;
//                try {
//                    userInitInfo = (RkUserProfile)data;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (null == userInitInfo) {
//             
//                    return -1;
//                }
//                if (0 != userInitInfo.getRc()) {
//                    return -2;
//                }
//                //获取成功,初始化用户信息，之后就进入主界面
//                RkUserProfileDataField profile = userInitInfo.getData();
//                if (null == profile) {
//                    return -3;
//                }
//                
//                String tempMateId = profile.getPartner_id();
//                
//                if (TextUtils.isEmpty(tempMateId)) { //如果这种情况取到对方的UID为null，认为是未结对
//                    return 0;
//                }
//                
//                RkGlobalParams global = RkGlobalParams.getInstance();
//                global.setVersion(profile.getGender());
//                global.setIconBig(profile.getIcon_big());
//                global.setIconMid(profile.getIcon_mid());
//                global.setIconSmall(profile.getIcon_small());
////              global.setPartnerName(profile.getName());   
//                global.setZoneBackground(profile.getBg_big()); //二人空间的背景图
//                global.setChatBackgroundBig(profile.getChat_big());
////                global.setChatBackgroundSmall(profile.getChat_small());
//
//                return 0;
//            }
//
//        });
//    }	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//}
