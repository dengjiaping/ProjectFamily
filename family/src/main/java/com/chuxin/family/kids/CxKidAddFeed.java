package com.chuxin.family.kids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.neighbour.CxNeighbourAddInvitation;
import com.chuxin.family.neighbour.CxNeighbourParam;
import com.chuxin.family.net.CxKidApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxKidParser;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.been.CxKidFeed;
import com.chuxin.family.parse.been.CxNeighbourSendInvitation;
import com.chuxin.family.parse.been.CxZoneSendFeed;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxShareUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.widgets.CxInputPanel.OnMoodEmotionListener;

public class CxKidAddFeed extends CxRootActivity implements Callback{

	private int mSharedType = 0; //默认是发送文字界面
	
	private Button mBackBtn, mShareBtn;
	private TextView mTitleView; //
	
	private EditText mShareText;
//	private TextView mShareTextCounter;
	private ImageView mEmotion;
	private CxInputPanel mInput;
	private InputMethodManager input;
	public static String RK_CURRENT_VIEW="CxKidAddFeed";
	private boolean showEmotion=false;
	
	
	private LinearLayout mFeedImagesLayer; //cx_fa_shared_photos
	private CxImageView firstImage, secondImage, thirdImage, 
	forthImage, fifthImage, sixthImage, seventhImage, eighthImage, ninethImage;
	private List<CxImageView> mImages = new ArrayList<CxImageView>();
	
	private int  imageCounter; //图片的张数
	private List<String> mImagesPath = new ArrayList<String>(); //存储图片路径
	private int position; //被点击的图片按钮
	
	private CxKidParam mKidParam;
	private PhotosObserver mPhotosObserver;
	private int mSyncN = 0,mSyncZ=0; // 0  默认不同步到密邻, 1 默认同步到密邻
	private LinearLayout mSharedSyncNeighbour;
	private TextView mIsSyncNText;
	private ImageView mIsSyncNView;
	
	private ImageView mShareQzoneBtn, mShareWxMomentsBtn;
	private boolean mQzoneFlag = false, mWxMomentFlag = false;
	
	private ArrayList<String> mFeedImages = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_kids_add_feed);
		
		//设置观察者
		mKidParam = CxKidParam.getInstance(); //获取model的subject实例
		mPhotosObserver = new PhotosObserver(); //生成观察者实例
		//设置观察目标
		List<String> tags = new ArrayList<String>();
		tags.add(CxKidParam.S_ADD_PHOTOS_PATH); //删除或者添加图片
		mPhotosObserver.setListenTag(tags); //设置观察目标
		mPhotosObserver.setMainThread(true); //设置在UI线程执行update
		mKidParam.registerObserver(mPhotosObserver); //注册观察者
		
		//默认是发送文字界面
		try {
			mSharedType = this.getIntent().getIntExtra(CxGlobalConst.S_KID_SHARED_TYPE, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			mImagesPath = getIntent().getStringArrayListExtra(CxGlobalConst.S_KID_SHARED_IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mSharedType==1 && ((null == mImagesPath) || (mImagesPath.size() < 1))) {
			CxKidAddFeed.this.finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
			return;
		}
		
		input = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initTitle();
		
		init();
		
		fillData();
		
		
	}
	
	private void fillData() {
		
		//分享
		ShareSDK.initSDK(CxKidAddFeed.this);
		
		if (0 == mSharedType) { //发送文字
			mShareText.setHint(getString(R.string.cx_fa_kids_add_hint));
			mTitleView.setText(getString(R.string.cx_fa_zone_sendtext));
			mFeedImagesLayer.setVisibility(View.GONE);
			mPromptText.setVisibility(View.GONE);
		}else{ //发送文字+图片
			mShareText.setHint(getString(R.string.cx_fa_kids_add_hint));
			mTitleView.setText(getString(R.string.cx_fa_zone_sendimage));
			mFeedImagesLayer.setVisibility(View.VISIBLE);
			mPromptText.setVisibility(View.VISIBLE);
			initImageVisible();
		}
	}

	private void init() {
		mShareText = (EditText)findViewById(R.id.cx_fa_kids_add_text_et);
		mEmotion = (ImageView) findViewById(R.id.cx_fa_kids_add_emotion);
		LinearLayout mEmotionLayout = (LinearLayout) findViewById(R.id.cx_fa_kids_add_emotion_layout);
		mFeedImagesLayer = (LinearLayout)findViewById(R.id.cx_fa_kids_add_shared_photos);
		//初始化9张图片
		initImages();
		
		mSharedSyncNeighbour = (LinearLayout)findViewById(R.id.cx_fa_kids_add_is_sync_neighbour_layout);
		mIsSyncNText = (TextView)findViewById(R.id.cx_fa_kids_add_is_sync_neighbour_tv);
		mIsSyncNView = (ImageView)findViewById(R.id.cx_fa_kids_add_is_sync_neighbour_iv);
		mIsSyncNText.setText(getResources().getString(R.string.cx_fa_sync_closing));
		mIsSyncNView.setBackgroundResource(R.drawable.set_checkoff);
		
		mSharedSyncZone = (LinearLayout)findViewById(R.id.cx_fa_kids_add_is_sync_zone_layout);
		mIsSyncZText = (TextView)findViewById(R.id.cx_fa_kids_add_is_sync_zone_tv);
		mIsSyncZView = (ImageView)findViewById(R.id.cx_fa_kids_add_is_sync_zone_iv);
		mIsSyncZText.setText(getResources().getString(R.string.cx_fa_sync_closing));
		mIsSyncZView.setBackgroundResource(R.drawable.set_checkoff);
		
		mShareQzoneBtn = (ImageView)findViewById(R.id.cx_fa_kids_add_share_to_qzone);
		mShareWxMomentsBtn = (ImageView)findViewById(R.id.cx_fa_kids_add_share_to_wechatmoments);
		
		mPromptText = (TextView) findViewById(R.id.cx_fa_kids_add_prompt);
		
		mInput = (CxInputPanel) findViewById(R.id.cx_fa_widget_input_layer);
		CxInputPanel.sInputPanelUse = RK_CURRENT_VIEW;
		mInput.setFaceMode();
		View linearlayout1 = mInput.findViewById(R.id.cx_fa_widget_input_panel__linearlayout1);
		View linearlayout2 = mInput.findViewById(R.id.cx_fa_widget_input_panel__layout2);
		linearlayout1.setVisibility(View.GONE);
		linearlayout2.setVisibility(View.GONE);
		mInput.setVisibility(View.GONE);

		mShareQzoneBtn.setOnClickListener(contentListener);
		mShareWxMomentsBtn.setOnClickListener(contentListener);
		mShareText.setOnClickListener(contentListener);
		mSharedSyncZone.setOnClickListener(contentListener);
		mSharedSyncNeighbour.setOnClickListener(contentListener);
		mEmotionLayout.setOnClickListener(contentListener);
		
		mShareText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	CxLog.i("men", "focuschange");
                if (!hasFocus) {
                	input.hideSoftInputFromWindow(mShareText.getWindowToken(), 0);    
                }
            }

        });
		mInput.setOnMoodEmotionListener(new OnMoodEmotionListener() {
			
			@Override
			public void onMoodOnClick(String msg) {
				if(!TextUtils.isEmpty(msg)){
					CxLog.i("men", msg);
					mShareText.append(msg);
				}		
			}
		});
		
		
		
	}

	private void initTitle() {
		mTitleView = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mShareBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mShareBtn.setVisibility(View.VISIBLE);
		
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		mShareBtn.setText(getString(R.string.cx_fa_share_feed_text));
		
		mBackBtn.setOnClickListener(titleListener);
		mShareBtn.setOnClickListener(titleListener);
	}
	
	OnClickListener  contentListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_kids_add_text_et:
				if(showEmotion){
           	   		showEmotion=false;
					mEmotion.setImageResource(R.drawable.wezone_face);
					mInput.setVisibility(View.GONE);
				}
				break;
			case R.id.cx_fa_kids_add_emotion_layout:
				if(showEmotion){
					showEmotion=false;
					mEmotion.setImageResource(R.drawable.wezone_face);
					mInput.setVisibility(View.GONE);
					
					
					mShareText.setFocusable(true);
					mShareText.requestFocus();		
					mShareText.requestFocusFromTouch();	
					input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);//弹出键盘
				}else{
					input.hideSoftInputFromWindow(mShareText.getWindowToken(), 0);
					SystemClock.sleep(300);
					
					showEmotion=true;
					mEmotion.setImageResource(R.drawable.wezone_face_h);
					mInput.setVisibility(View.VISIBLE);	
				}
				break;
				
			case R.id.cx_fa_kids_add_is_sync_neighbour_layout:
				if(mSyncN==0){
					mSyncN = 1;
					mIsSyncNText.setText(getResources().getString(R.string.cx_fa_sync_opening));
					mIsSyncNView.setBackgroundResource(R.drawable.set_checkon);
				} else {
					mSyncN = 0;
					mIsSyncNText.setText(getResources().getString(R.string.cx_fa_sync_closing));
					mIsSyncNView.setBackgroundResource(R.drawable.set_checkoff);
				}
				break;
				
			case R.id.cx_fa_kids_add_is_sync_zone_layout:
				if(mSyncZ==0){
					mSyncZ = 1;
					mIsSyncZText.setText(getResources().getString(R.string.cx_fa_sync_opening));
					mIsSyncZView.setBackgroundResource(R.drawable.set_checkon);
				} else {
					mSyncZ = 0;
					mIsSyncZText.setText(getResources().getString(R.string.cx_fa_sync_closing));
					mIsSyncZView.setBackgroundResource(R.drawable.set_checkoff);
				}
				break;
				
			case R.id.cx_fa_kids_add_share_to_qzone:
				Platform qzonePlat = ShareSDK.getPlatform(CxKidAddFeed.this, QZone.NAME);
				if (mQzoneFlag) { //已经授权过
					mShareQzoneBtn.setImageResource(R.drawable.logo_qzone_disable);
					mQzoneFlag = !mQzoneFlag;
					break;
				}
				if (!qzonePlat.isValid()) {
					qzonePlat.setPlatformActionListener(qzoneListener);
					qzonePlat.authorize();
				}else{
					mShareQzoneBtn.setImageResource(R.drawable.logo_qzone_enable);
					mQzoneFlag = !mQzoneFlag;
				}
				
				break;
			case R.id.cx_fa_kids_add_share_to_wechatmoments: //微信不需要授权，是通过client端之间发送
				Platform wxPlat = ShareSDK.getPlatform(CxKidAddFeed.this, WechatMoments.NAME);
				if (!mWxMomentFlag) {
					if (!wxPlat.isValid()) { //提示版本不兼容分享
						ToastUtil.getSimpleToast(CxKidAddFeed.this, -3, getString(R.string.wechat_client_inavailable), 1).show();
//						Toast.makeText(CxKidAddFeed.this, getString(R.string.wechat_client_inavailable), 
//								Toast.LENGTH_LONG).show();
						break;
					}
					mShareWxMomentsBtn.setImageResource(R.drawable.logo_wechatmoments_enable);
				}else{
					mShareWxMomentsBtn.setImageResource(R.drawable.logo_wechatmoments_disable);
				}
				mWxMomentFlag = !mWxMomentFlag;
				break;

			default:
				break;
			}
			
		}
	};
	
	
	OnClickListener  titleListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_more:
				if ((TextUtils.isEmpty(mShareText.getText().toString())) && (mImagesPath==null || (mImagesPath.size() < 1)) ){
					ToastUtil.getSimpleToast(CxKidAddFeed.this, -3,getString(R.string.cx_fa_zone_no_feed), 1).show();
					return;
				}
				if(mSyncN==0){
					showSendWithNDialog();
				}else{
					sendFeed();
				}
				
				break;
			case R.id.cx_fa_activity_title_back:
				back();
				break;
			default:
				break;
			}
			
		}
	};
	
	private void sendFeed(){
		try {
			mShareBtn.setEnabled(false);
			DialogUtil.getInstance().getLoadingDialogShow(CxKidAddFeed.this, -1);
			if (mQzoneFlag || mWxMomentFlag) {
				CxKidApi.getInstance().requestAddFeed(mShareText.getText().toString(), 
						mImagesPath, mSyncZ, mSyncN, 1, sendCallback);
			}else{
				CxKidApi.getInstance().requestAddFeed(mShareText.getText().toString(), 
						mImagesPath, mSyncZ, mSyncN, 0, sendCallback);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			mShareBtn.setEnabled(true);
		}
	}
	
	private void showSendWithNDialog(){
		View inflate = View.inflate(CxKidAddFeed.this, R.layout.cx_fa_widget_kids_add_send_dialog, null);
		Button withN = (Button) inflate.findViewById(R.id.cx_fa_kids_add_send_with_neighbour_btn);
		Button direct = (Button) inflate.findViewById(R.id.cx_fa_kids_add_send_direct_btn);
		
		final Dialog dialog=new Dialog(this, R.style.simple_dialog);		
		dialog.setContentView(inflate);	
		dialog.setCancelable(true);
		withN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				mSyncN=1;
				sendFeed();
				
			}
		});
		direct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				sendFeed();
			}
		});
		dialog.show();
		
	}
	
	
	
	//qzone 授权回调
	Handler mAuthenNotice = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mQzoneFlag = !mQzoneFlag;
				mShareQzoneBtn.setImageResource(R.drawable.logo_qzone_enable);
				break;
				
			case 2:
				ToastUtil.getSimpleToast(CxKidAddFeed.this, -3, getString(R.string.cx_fa_authen_fail), 1).show();
				break;
				
			case 3:
				ToastUtil.getSimpleToast(CxKidAddFeed.this, -3, getString(R.string.cancel), 1).show();
				break;

			default:
				break;
			}
		};
	};
	
	//qzone 授权
	PlatformActionListener qzoneListener = new PlatformActionListener() {
		
		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			CxLog.i("share third ", " arg1="+arg1+",error:"+arg2.toString());
			mAuthenNotice.sendEmptyMessage(2);
		}
		
		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			CxLog.i("share third ", " authen success");
			mAuthenNotice.sendEmptyMessage(1);
		}
		
		@Override
		public void onCancel(Platform arg0, int arg1) {
			CxLog.i("share third ", " cancel authen");
			mAuthenNotice.sendEmptyMessage(3);
		}
	};
	
	//发帖回调
	JSONCaller sendCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			new Handler(getMainLooper()){
				public void handleMessage(Message msg) {									
					mShareBtn.setEnabled(true);
				};
			}.sendEmptyMessage(1);

			if (null == result) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			JSONObject jObj=null;
			try {
				jObj=(JSONObject) result;
			} catch (Exception e) {
			}
			
			if(jObj==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -3;
			}
			
			CxKidParser sendParser = new CxKidParser();
			CxKidFeed sendResult = null;
			try {
				sendResult = sendParser.getAddFeedResult(jObj);
			} catch (Exception e) {
			}
			if (null == sendResult || sendResult.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = sendResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(sendResult.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(sendResult.getMsg(),0);
				}
				return rc;
			}
			//清楚图片和文字数据
			mImagesPath = null;
			
			//把数据告知密邻界面
			if (null != sendResult.getData()) {
				//同步到qq zone 和 微信朋友圈
				try {
					String commentStr = null;
					String firstImgUrl = null;
					String chuxinUrl = null;
					try {
						commentStr = sendResult.getData().getPost().getText();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						firstImgUrl = sendResult.getData().getPost().getPhotos().get(0).getBig();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						chuxinUrl = sendResult.getData().getOpen_url();
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					List<Platform> plats = new ArrayList<Platform>();
					if (mQzoneFlag) {
						Platform qzonePlat = ShareSDK.getPlatform(CxKidAddFeed.this, "QZone");
						plats.add(qzonePlat);
					}
					if (mWxMomentFlag) {
						Platform wxPlat = ShareSDK.getPlatform(CxKidAddFeed.this, "WechatMoments");
						plats.add(wxPlat);
					}
					
					if ( (null != plats) && (plats.size() > 0) ){
						CxShareUtil shareUtil = new CxShareUtil(CxKidAddFeed.this, CxKidAddFeed.this);
						shareUtil.shareToThird(commentStr, chuxinUrl, firstImgUrl, plats);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				CxKidParam.getInstance().setFeedsData(jObj.toString());
			}
			CxKidAddFeed.this.finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
			return 0;
		}
	};
	
	private void initImages(){
		CxImageView firstImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_first_9image);
		firstImage.setOnClickListener(mImageListener);
		mImages.add(firstImage);
		CxImageView secondImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_second_9image);
		secondImage.setOnClickListener(mImageListener);
		mImages.add(secondImage);
		CxImageView thirdImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_third_9image);
		thirdImage.setOnClickListener(mImageListener);
		mImages.add(thirdImage);
		CxImageView forthImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_forth_9image);
		forthImage.setOnClickListener(mImageListener);
		mImages.add(forthImage);
		CxImageView fifthImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_fifth_9image);
		fifthImage.setOnClickListener(mImageListener);
		mImages.add(fifthImage);
		CxImageView sixthImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_sixth_9image);
		sixthImage.setOnClickListener(mImageListener);
		mImages.add(sixthImage);
		CxImageView seventhImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_seventh_9image);
		seventhImage.setOnClickListener(mImageListener);
		mImages.add(seventhImage);
		CxImageView eighthImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_eighth_9image);
		eighthImage.setOnClickListener(mImageListener);
		mImages.add(eighthImage);
		CxImageView ninethImage = (CxImageView)findViewById(R.id.cx_fa_kids_add_nineth_9image);
		ninethImage.setOnClickListener(mImageListener);
		mImages.add(ninethImage);
	}
	
	private void initImageVisible(){ 
		if ( (null == mImagesPath) || (mImagesPath.size() < 1) ) { //仅仅第一张显示添加图片，
			//其余不显示(删除分享图片时有此情况）
			mImages.get(0).setVisibility(View.VISIBLE);
			mImages.get(0).setImageResource(R.drawable.wezone_ninegrid_bg);
			
			int inVisibleCount = 1;
			for(int k = inVisibleCount; k < mImages.size(); k++){
				mImages.get(k).setVisibility(View.GONE);
				mImages.get(k).setImageResource(R.drawable.wezone_ninegrid_bg);
			}
			return;
		}
		
		//
		int visibleCount = 0; //可见项
		if (mImagesPath.size() >= mImages.size()) { //容错
			visibleCount = mImages.size();
		}else{
			visibleCount = mImagesPath.size();
		}
		CxLog.i("initImageVisible method", "visibleCount is// "+visibleCount);
		for(int i = 0; i< visibleCount; i++){
			CxImageView tempView = mImages.get(i);
			tempView.setVisibility(View.VISIBLE);
			tempView.setImageResource(R.drawable.wezone_ninegrid_bg);
			CxLog.i("333 ", ""+mImagesPath.get(i));
			tempView.displayImage(imageLoader, mImagesPath.get(i), 
					R.drawable.chatview_imageloading, false, 0);
		}
		
		//没有满9张图片需要留一个可以点击添加图片的按钮
		if (visibleCount < mImages.size()) {
			mImages.get(visibleCount).setVisibility(View.VISIBLE);
			mImages.get(visibleCount).setImageResource(R.drawable.wezone_ninegrid_bg);
		}
		int inVisibleCount = visibleCount+1;
		for(int k = inVisibleCount; k < mImages.size(); k++){
			mImages.get(k).setVisibility(View.GONE);
		}
		
	}
	
	OnClickListener mImageListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//设置添加的图片为图片详情提供数据
			CxLog.i("CxKidAddFeed_men", "mImageListener");
			switch (v.getId()) {
			case R.id.cx_fa_kids_add_first_9image:
				processImageClick(0);
				break;
			case R.id.cx_fa_kids_add_second_9image:
				processImageClick(1);
				break;
			case R.id.cx_fa_kids_add_third_9image:
				processImageClick(2);
				break;
			case R.id.cx_fa_kids_add_forth_9image:
				processImageClick(3);
				break;
			case R.id.cx_fa_kids_add_fifth_9image:
				processImageClick(4);
				break;
			case R.id.cx_fa_kids_add_sixth_9image:
				processImageClick(5);
				break;
			case R.id.cx_fa_kids_add_seventh_9image:
				processImageClick(6);
				break;
			case R.id.cx_fa_kids_add_eighth_9image:
				processImageClick(7);
				break;
			case R.id.cx_fa_kids_add_nineth_9image:
				processImageClick(8);
				break;
			default:
				break;
			}
			
		}
	};
	
	private void processImageClick(int postion){
		CxLog.i("CxKidAddFeed_men", postion+"");
		this.position = postion;
		if (postion < mImagesPath.size() ) { //看大图
			CxKidParam.getInstance().setAddPhotosPath(mImagesPath);//这种情况才需要把数据源保存一下
			Intent imageDetail = new Intent(CxKidAddFeed.this, CxKidImagePager.class);
			imageDetail.putExtra(CxGlobalConst.S_KID_SELECTED_ORDER, postion);
			startActivity(imageDetail);
			overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		}else{ //添加图片
			Intent changeChatBackground = new Intent(CxKidAddFeed.this, ActivitySelectPhoto.class);
			ActivitySelectPhoto.kIsCallPhotoZoom =false;
			ActivitySelectPhoto.kIsCallFilter = true;
			ActivitySelectPhoto.kIsCallSysCamera = false;
			ActivitySelectPhoto.kChoseSingle = false;
			ActivitySelectPhoto.kFrom = "CxKidAddFeed";
			startActivityForResult(changeChatBackground, 33);
			overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		ArrayList<String> tempData = data.getStringArrayListExtra(
				CxGlobalConst.MULT_SELECT_IMAGE);
		if ((null == tempData) || (tempData.size() < 1)){
			return;
		}
		
		mImagesPath.addAll(tempData);
		
		initImageVisible();
		
	}

	class PhotosObserver extends CxObserverInterface{

		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) { //对此事件不处理
				return;
			}
			if (CxKidParam.S_ADD_PHOTOS_PATH.equals(actionTag)) { //照片有被删除情况
				mImagesPath = CxKidParam.getInstance().getAddPhotosPath();
				initImageVisible();
				return;
			}
			
		}
		
	}
	
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	private void back() {
		if ( (!TextUtils.isEmpty(mShareText.getText().toString())) 
				|| ( (null != mImagesPath) && (mImagesPath.size() > 0)) ) { //文字或者图片有其一都应该弹出确认对话框

			DialogUtil du = DialogUtil.getInstance();
			du.setOnSureClickListener(new OnSureClickListener() {
				
				@Override
				public void surePress() {
					mShareText.setText("");
                	CxKidParam.getInstance().setAddPhotosPath(null);
                    CxKidAddFeed.this.finish();
                    overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
				}
			});
			du.getSimpleDialog(CxKidAddFeed.this, null, getString(R.string.cx_fa_dispose_feed), null, null).show();
			return;
		}
		//没有任何内容的时候直接关闭就行
		CxKidAddFeed.this.finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
		
	}

	@Override
	protected void onDestroy() {
		
		mInput.sInputPanelUse="";
		
		CxKidParam.getInstance().setAddPhotosPath(null); //在此清楚图片数据
		
		//释放分享
		ShareSDK.stopSDK(CxKidAddFeed.this);
		
		super.onDestroy();
	}
	

	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;

	private TextView mPromptText;

	private LinearLayout mSharedSyncZone;

	private TextView mIsSyncZText;

	private ImageView mIsSyncZView;
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(CxKidAddFeed.this, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: {
				// 成功
				showNotification(2000,CxKidAddFeed.this.getString(R.string.cx_fa_third_share_success));
			}
				break;
			case 2: {
				// 失败
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)
						|| "WechatTimelineNotSupportedException".equals(expName)) {
					showNotification(2000, CxKidAddFeed.this.getString(R.string.wechat_client_inavailable));
				} else {
					showNotification(2000,CxKidAddFeed.this.getString(R.string.cx_fa_third_share_fail));
				}
			}
				break;
			case 3: {
				// 取消
				showNotification(2000,CxKidAddFeed.this.getString(R.string.cx_fa_third_share_cancel));
			}
				break;
			}
		}
			break;
		case MSG_CANCEL_NOTIFY: {
			NotificationManager nm = (NotificationManager) msg.obj;
			if (nm != null) {
				nm.cancel(msg.arg1);
			}
		}
			break;
		}

		return false;
	}
	
	// 在状态栏提示分享操作
	@SuppressWarnings("deprecation")
	private void showNotification(long cancelTime, String text) {
		try {
			Context app = CxKidAddFeed.this.getApplicationContext();
			NotificationManager nm = (NotificationManager) app
					.getSystemService(Context.NOTIFICATION_SERVICE);
			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);

			long when = System.currentTimeMillis();
			Notification notification = new Notification(R.drawable.cx_fa_app_icon, text, when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(), 0);
			notification.setLatestEventInfo(app, getString(R.string.share), text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);

			if (cancelTime > 0) {
				Message msg = new Message();
				msg.what = MSG_CANCEL_NOTIFY;
				msg.obj = nm;
				msg.arg1 = id;
				UIHandler.sendMessageDelayed(msg, cancelTime, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void showResponseToast(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(getMainLooper()) {
			public void handleMessage(Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				int id=-1;
				if(msg.arg1==0){
					id= R.drawable.chatbg_update_error;
				}else if(msg.arg1==1){
					id=R.drawable.chatbg_update_success;
				}
				ToastUtil.getSimpleToast(CxKidAddFeed.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	
	
}
