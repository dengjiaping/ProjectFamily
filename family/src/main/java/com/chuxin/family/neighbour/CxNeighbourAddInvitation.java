package com.chuxin.family.neighbour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
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

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.been.CxNeighbourSendInvitation;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxShareUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.widgets.CxInputPanel.OnEventListener;
import com.chuxin.family.widgets.CxInputPanel.OnMoodEmotionListener;
import com.chuxin.family.zone.CxZoneAddFeed;
import com.chuxin.family.R;

public class CxNeighbourAddInvitation extends CxRootActivity implements Callback{

	private int mSharedType;
	private TextView mTitleView;
	private EditText mText;
//	private TextView mTextCounter;
	private ImageView mEmotion;
	private ArrayList<String> mImagesPath = new ArrayList<String>();
	private Button mTitleShareBtn;
	private int position; //被点击的图片按钮
	private List<CxImageView> mImages = new ArrayList<CxImageView>();
	
	private CxNeighbourParam mNbParam;
	private PhotosObserver mPhotosObserver;
	
	private ImageView mShareQzoneBtn, mShareWxMomentsBtn;
	private boolean mQzoneFlag = false, mWxMomentFlag = false, showEmotion=false;
	
	private int mSyncZ=0;
	
	public static String RK_CURRENT_VIEW="RkNeighbourAddInvitation";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_neighbour_add_invitation);
		
		ShareSDK.initSDK(CxNeighbourAddInvitation.this);
		mShareQzoneBtn = (ImageView)findViewById(R.id.ml_share_to_qzone);
		mShareWxMomentsBtn = (ImageView)findViewById(R.id.ml_share_to_wechatmoments);
		mShareQzoneBtn.setOnClickListener(mShareListener);
		mShareWxMomentsBtn.setOnClickListener(mShareListener);
		
		mNbParam = CxNeighbourParam.getInstance();
		mPhotosObserver = new PhotosObserver();
		//设置观察目标
		List<String> tags = new ArrayList<String>();
		tags.add(CxNeighbourParam.S_ADD_PHOTOS_PATH); //删除或者添加图片
		mPhotosObserver.setListenTag(tags); //设置观察目标
		mPhotosObserver.setMainThread(true); //设置在UI线程执行update
		mNbParam.registerObserver(mPhotosObserver); //注册观察者		
		
		//默认是发送文字界面
		try {
			mSharedType = this.getIntent().getIntExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_TYPE, 0);
		} catch (Exception e) {
		}
		
		mText = (EditText) findViewById(R.id.nb_invitation_text_content);
		
		//发帖加表情 至 165行
		mEmotion = (ImageView) findViewById(R.id.nb_invitation_emotion);
		LinearLayout mEmotionLayout = (LinearLayout) findViewById(R.id.nb_invitation_emotion_layout);
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.nb_invitation_text_and_img_layout);
		mEmotionLayout.setOnClickListener(mShareListener);
//		mLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(showEmotion){
//           	   		showEmotion=false;
//					mEmotion.setImageResource(R.drawable.wezone_face);
//					mInput.setVisibility(View.GONE);
//				}
//				input.hideSoftInputFromWindow(mText.getWindowToken(), 0);    
//			}
//		});
		
		
		mInput = (CxInputPanel) findViewById(R.id.cx_fa_widget_input_layer);
		CxInputPanel.sInputPanelUse = RK_CURRENT_VIEW;
		mInput.setOnlyFaceMode();
//		View linearlayout1 = mInput.findViewById(R.id.cx_fa_widget_input_panel__linearlayout1);
//		View linearlayout2 = mInput.findViewById(R.id.cx_fa_widget_input_panel__layout2);
//		linearlayout1.setVisibility(View.GONE);
//		linearlayout2.setVisibility(View.GONE);
		
		mInput.setVisibility(View.GONE);
		mInput.setOnMoodEmotionListener(new OnMoodEmotionListener() {
			
			@Override
			public void onMoodOnClick(String msg) {
				if(!TextUtils.isEmpty(msg)){
					CxLog.i("men", msg);
					mText.append(msg);
				}		
			}
		});
		input = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		mText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(showEmotion){
           	   		showEmotion=false;
					mEmotion.setImageResource(R.drawable.wezone_face);
					mInput.setVisibility(View.GONE);
				}
				
			}
		});
		
		mText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	CxLog.i("men", "focuschange");
                if (!hasFocus) {
                	input.hideSoftInputFromWindow(mText.getWindowToken(), 0);    
                }
            }

        });
		
		
		
		mTitleView = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		Button mTitleBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mTitleBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( (!TextUtils.isEmpty(mText.getText().toString())) 
						|| ( (null != mImagesPath) && (mImagesPath.size() > 0)) ) { //文字或者图片有其一都应该弹出确认对话框				        
			        DialogUtil du=DialogUtil.getInstance();
			        du.setOnSureClickListener(new OnSureClickListener() {						
						@Override
						public void surePress() {
							mText.setText("");
                        	CxNeighbourParam.getInstance().setAddPhotosPath(null);
                        	CxNeighbourAddInvitation.this.finish();  
                        	overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
						}
					});
			        Dialog dialog=du.getSimpleDialog(CxNeighbourAddInvitation.this, null, getString(R.string.cx_fa_dispose_feed), null, null);
			        dialog.show();
					return;
				}
				//没有任何内容的时候直接关闭就行
				CxNeighbourAddInvitation.this.finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
			}
		});
		mTitleShareBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mTitleShareBtn.setVisibility(View.VISIBLE);
		//mShareBtn.setBackgroundResource(R.drawable.cx_fa_zone_addfeed_btn);
		
		mTitleBackBtn.setText(getString(R.string.cx_fa_navi_back));
		mTitleShareBtn.setText(getString(R.string.cx_fa_share_feed_text));
		
		
		mTitleShareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((TextUtils.isEmpty(mText.getText().toString())) 
						&& (mImagesPath.size() < 1) ){
					ToastUtil.getSimpleToast(CxNeighbourAddInvitation.this, -1,
							getResources().getString(R.string.cx_fa_zone_no_feed),1).show();
					return;
				}
				try {
					mTitleShareBtn.setEnabled(false);
//					RkLoadingUtil.getInstance().showLoading(RkZoneAddFeed.this, true);
//					String string = mImagesPath.get(0);
//					System.out.println(string);
					
//					dialog = DialogUtil.getInstance().getLoadingDialog(RkNeighbourAddInvitation.this);
//					dialog.show();
//					startTime = System.currentTimeMillis();
					DialogUtil.getInstance().getLoadingDialogShow(CxNeighbourAddInvitation.this, -1);
					if (mQzoneFlag || mWxMomentFlag) {
						CxNeighbourApi.getInstance().requestSendInvitation(mText.getText().toString().trim(), 
								mImagesPath,"post",null,mSyncZ, 1, sendCallback);
					}else{
						CxNeighbourApi.getInstance().requestSendInvitation(mText.getText().toString().trim(), 
								mImagesPath,"post",null,mSyncZ, 0, sendCallback); 
					}
					
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					mTitleShareBtn.setEnabled(true);
				}
				
			}
		});
	
		LinearLayout mImagesLayer = (LinearLayout)findViewById(R.id.cx_fa_neighbour_invitation_shared_photos);
		
		mSharedSyncZone = (LinearLayout)findViewById(R.id.cx_fa_shared_is_sync_zone);
		mIsSyncZText = (TextView)findViewById(R.id.cx_fa_is_sync);
		mIsSyncZView = (ImageView)findViewById(R.id.cx_fa_is_sync_imageview);
		mIsSyncZText.setText(getResources().getString(R.string.cx_fa_sync_closing));
		mIsSyncZView.setBackgroundResource(R.drawable.set_checkoff);
		mSharedSyncZone.setOnClickListener(mShareListener);
		
		
		if (0 == mSharedType) { //发送文字
			mTitleView.setText(getString(R.string.cx_fa_zone_sendtext));
			mImagesLayer.setVisibility(View.GONE);
		}else{ //发送文字+图片
			mTitleView.setText(getString(R.string.cx_fa_zone_sendimage));
			mImagesLayer.setVisibility(View.VISIBLE);
		}
		

//		mText.addTextChangedListener(textCountListener);
		
		if (0 == mSharedType) {
			mText.setHint(getString(R.string.cx_fa_neighbour_send_text_hint));
			return;
		}
		//------以下是分享图片的处理----------
		TextView prompt = (TextView) findViewById(R.id.cx_fa_neighbour_invitation_prompt);
		prompt.setVisibility(View.VISIBLE);
		mText.setHint(getString(R.string.cx_fa_neighbour_send_img_hint));
//		String firstImagePath = null;
		try {
//			firstImagePath = this.getIntent().getStringExtra(RkGlobalConst.S_NEIGHBOUR_SHARED_IMAGE);
			mImagesPath = getIntent().getStringArrayListExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null == mImagesPath) {
			CxNeighbourAddInvitation.this.finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
			return;
		}
		/*if (firstImagePath.contains("file://")) {
			firstImagePath = firstImagePath.replaceFirst("file://", "");
		}
		
		mImagesPath.add(firstImagePath);*/
		
		//初始化9张图片
		initImages();
		
		initImageVisible();
		
//		new ThumbImageProccessor(0, firstImagePath).execute();
		
	}
	
	OnClickListener mShareListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ShareSDK.initSDK(CxNeighbourAddInvitation.this);
			switch (v.getId()) {
			case R.id.ml_share_to_qzone:
				Platform qzonePlat = ShareSDK.getPlatform(CxNeighbourAddInvitation.this, "QZone");
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
			case R.id.ml_share_to_wechatmoments: //微信不需要授权，是通过client端之间发送
				Platform wxPlat = ShareSDK.getPlatform(
						CxNeighbourAddInvitation.this, "WechatMoments");
				if (!mWxMomentFlag) {
					if (!wxPlat.isValid()) { //提示版本不兼容分享
						Toast.makeText(CxNeighbourAddInvitation.this, getString(
								R.string.wechat_client_inavailable), 
								Toast.LENGTH_LONG).show();
						break;
					}
					mShareWxMomentsBtn.setImageResource(R.drawable.logo_wechatmoments_enable);
				}else{
					mShareWxMomentsBtn.setImageResource(R.drawable.logo_wechatmoments_disable);
				}
				mWxMomentFlag = !mWxMomentFlag;
				break;
			case R.id.nb_invitation_emotion_layout:
				if(showEmotion){
					showEmotion=false;
					mEmotion.setImageResource(R.drawable.wezone_face);
					mInput.setVisibility(View.GONE);
					
					mText.setFocusable(true);
					mText.requestFocus();		
					mText.requestFocusFromTouch();
					input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);//弹出键盘
//					input.showSoftInput(mText,InputMethodManager.SHOW_FORCED);	
				}else{
					input.hideSoftInputFromWindow(mText.getWindowToken(), 0);
					
					SystemClock.sleep(300);
					
					showEmotion=true;
					mEmotion.setImageResource(R.drawable.wezone_face_h);
					mInput.setVisibility(View.VISIBLE);	
				}
				break;
			case R.id.cx_fa_shared_is_sync_zone:
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
			default:
				break;
			}
			
		}
	};
	
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
	
	Handler mAuthenNotice = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mQzoneFlag = !mQzoneFlag;
				mShareQzoneBtn.setImageResource(R.drawable.logo_qzone_enable);
				break;
				
			case 2:
				Toast.makeText(CxNeighbourAddInvitation.this, getString(
						R.string.cx_fa_authen_fail), Toast.LENGTH_LONG).show();
				break;
				
			case 3:
				Toast.makeText(CxNeighbourAddInvitation.this, getString(
						R.string.cancel), Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		};
	};
	
	JSONCaller sendCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			new Handler(getMainLooper()){
				public void handleMessage(Message msg) {									
					mTitleShareBtn.setEnabled(true);
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
			
			CxNeighbourParser sendParser = new CxNeighbourParser();
			CxNeighbourSendInvitation sendResult = null;
			try {
				sendResult = sendParser.getSendInvitationResult(jObj);
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
						Platform qzonePlat = ShareSDK.getPlatform(
								CxNeighbourAddInvitation.this, "QZone");
						plats.add(qzonePlat);
					}
					if (mWxMomentFlag) {
						Platform wxPlat = ShareSDK.getPlatform(
								CxNeighbourAddInvitation.this, "WechatMoments");
						plats.add(wxPlat);
					}
					
					if ( (null != plats) && (plats.size() > 0) ){
						CxShareUtil shareUtil = new CxShareUtil(
								CxNeighbourAddInvitation.this, CxNeighbourAddInvitation.this);
						shareUtil.shareToThird(commentStr, chuxinUrl, firstImgUrl, plats);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				CxNeighbourParam.getInstance().setInvitationData(jObj.toString());
			}
			/*FeedListData feedData = sendResult.getData();
			RkZoneParam.getInstance().setFeedsData(feedData);*/
			CxNeighbourAddInvitation.this.finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
			return 0;
		}
	};
	
	
	
	
	private void initImages(){
		CxImageView firstImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_first_9image);
		firstImage.setOnClickListener(mImageListener);
		mImages.add(firstImage);
		CxImageView secondImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_second_9image);
		secondImage.setOnClickListener(mImageListener);
		mImages.add(secondImage);
		CxImageView thirdImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_third_9image);
		thirdImage.setOnClickListener(mImageListener);
		mImages.add(thirdImage);
		CxImageView forthImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_forth_9image);
		forthImage.setOnClickListener(mImageListener);
		mImages.add(forthImage);
		CxImageView fifthImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_fifth_9image);
		fifthImage.setOnClickListener(mImageListener);
		mImages.add(fifthImage);
		CxImageView sixthImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_sixth_9image);
		sixthImage.setOnClickListener(mImageListener);
		mImages.add(sixthImage);
		CxImageView seventhImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_seventh_9image);
		seventhImage.setOnClickListener(mImageListener);
		mImages.add(seventhImage);
		CxImageView eighthImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_eighth_9image);
		eighthImage.setOnClickListener(mImageListener);
		mImages.add(eighthImage);
		CxImageView ninethImage = (CxImageView)findViewById(R.id.cx_fa_neighbour_invitation_nineth_9image);
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
		
		for(int i = 0; i< visibleCount; i++){
			CxImageView tempView = mImages.get(i);
			tempView.setVisibility(View.VISIBLE);
			tempView.setImageResource(R.drawable.wezone_ninegrid_bg);
			new ThumbImageProccessor(i, mImagesPath.get(i)).execute();
//			tempView.setImage(mImagesPath.get(i));
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
			
			switch (v.getId()) {
			case R.id.cx_fa_neighbour_invitation_first_9image:
				processImageClick(0);
				break;
			case R.id.cx_fa_neighbour_invitation_second_9image:
				processImageClick(1);
				break;
			case R.id.cx_fa_neighbour_invitation_third_9image:
				processImageClick(2);
				break;
			case R.id.cx_fa_neighbour_invitation_forth_9image:
				processImageClick(3);
				break;
			case R.id.cx_fa_neighbour_invitation_fifth_9image:
				processImageClick(4);
				break;
			case R.id.cx_fa_neighbour_invitation_sixth_9image:
				processImageClick(5);
				break;
			case R.id.cx_fa_neighbour_invitation_seventh_9image:
				processImageClick(6);
				break;
			case R.id.cx_fa_neighbour_invitation_eighth_9image:
				processImageClick(7);
				break;
			case R.id.cx_fa_neighbour_invitation_nineth_9image:
				processImageClick(8);
				break;
			default:
				break;
			}
			
		}
	};
	private void processImageClick(int postion){
		this.position = postion;
		if (postion < mImagesPath.size() ) { //看大图
			CxNeighbourParam.getInstance().setAddPhotosPath(mImagesPath);//这种情况才需要把数据源保存一下
//			Intent ImageDetail = new Intent(RkNeighbourAddInvitation.this, RkNeighbourAddInvitationImageDetail.class);
			Intent ImageDetail = new Intent(CxNeighbourAddInvitation.this, CxNeighbourAddInvitationImagePager.class);
			ImageDetail.putExtra(CxGlobalConst.S_NEIGHBOUR_SELECTED_ORDER, postion);
			startActivity(ImageDetail);
			overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		}else{ //添加图片
			Intent changeChatBackground = new Intent(CxNeighbourAddInvitation.this, ActivitySelectPhoto.class);
			ActivitySelectPhoto.kIsCallPhotoZoom =false;
			ActivitySelectPhoto.kIsCallFilter = true;
			ActivitySelectPhoto.kIsCallSysCamera = false;
			ActivitySelectPhoto.kChoseSingle = false;
			ActivitySelectPhoto.kFrom = "RkNeighbourAddInvitation";
			startActivityForResult(changeChatBackground, 33);
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

	class ThumbImageProccessor extends AsyncTask<Object, Integer, Integer>{

		private String mImagePath;
		private int mLocation;
//		private AlertDialog dlg;
		private Bitmap mThumbBitmap;
		
		public ThumbImageProccessor(int location, String imagePath){
			this.mImagePath = imagePath;
			this.mLocation = location;
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			try {
			    DisplayMetrics metrics = getResources().getDisplayMetrics();
                
                switch (metrics.densityDpi) {
                    case DisplayMetrics.DENSITY_XXHIGH:
                    case DisplayMetrics.DENSITY_XHIGH:
                        mThumbBitmap = new PictureUtils(CxNeighbourAddInvitation.this).getImageThumbnail(mImagePath.replace("file://", ""), 320, 320);
                        break;
                    case DisplayMetrics.DENSITY_HIGH:
                    case DisplayMetrics.DENSITY_MEDIUM:
                        mThumbBitmap = new PictureUtils(CxNeighbourAddInvitation.this).getImageThumbnail(mImagePath.replace("file://", ""), 200, 200);
                        break;
                    case DisplayMetrics.DENSITY_LOW:
                        mThumbBitmap = new PictureUtils(CxNeighbourAddInvitation.this).getImageThumbnail(mImagePath);
                    default:
                        mThumbBitmap = new PictureUtils(CxNeighbourAddInvitation.this).getImageThumbnail(mImagePath);
                        break;
                }
//				mThumbBitmap = new PictureUtils(RkNeighbourAddInvitation.this).getImageThumbnail(mImagePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (null != mThumbBitmap) {
				//TODO 更新imageview
				mImages.get(mLocation).setImageBitmap(mThumbBitmap);
				mImages.get(mLocation).setVisibility(View.VISIBLE);
				if ((mLocation + 1) < mImages.size()) {
					mImages.get(mLocation+1).setVisibility(View.VISIBLE);
				}
			}else{
				mImages.get(mLocation).setImageResource(R.drawable.wezone_ninegrid_bg);
			}
			/*if ( (null != dlg) && (dlg.isShowing()) ){
				dlg.dismiss();
			}*/
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			/*dlg = new AlertDialog.Builder(RkZoneAddFeed.this).create();
			View tempView = getLayoutInflater().inflate(R.layout.cx_fa_progress, null);
			dlg.setContentView(tempView);
			dlg.show();*/
			super.onPreExecute();
		}
		
	}

	
	class PhotosObserver extends CxObserverInterface{

		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) { //对此事件不处理
				return;
			}
			if (CxNeighbourParam.S_ADD_PHOTOS_PATH.equals(actionTag)) { //照片有被删除情况
				mImagesPath = CxNeighbourParam.getInstance().getAddPhotosPath();
				initImageVisible();
				return;
			}			
		}	
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
			if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				finish();
				this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				return false;
			}
			return super.onKeyDown(keyCode, event);
	};

	@Override
	protected void onDestroy() {
		
		//mInput.sInputPanelUse="";
		
		if(mPhotosObserver!=null){
			CxNeighbourParam.getInstance().unRegisterObsercer(mPhotosObserver);
		}		
		CxNeighbourParam.getInstance().setAddPhotosPath(null); //在此清楚图片数据
		
		try {
			ShareSDK.stopSDK(CxNeighbourAddInvitation.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onDestroy();
		
	}

//	TextWatcher textCountListener = new TextWatcher() {
//		
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			mTextCounter.setText(""+(2000-s.length()));	
//		}
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {	
//		}
//		@Override
//		public void afterTextChanged(Editable s) {		
//		}
//	};
	
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;
	private CxInputPanel mInput;
	private InputMethodManager input;
	private LinearLayout mSharedSyncZone;
	private TextView mIsSyncZText;
	private ImageView mIsSyncZView;

	
	
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(CxNeighbourAddInvitation.this, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: {
				// 成功
				showNotification(2000,
						CxNeighbourAddInvitation.this.getString(R.string.cx_fa_third_share_success));
			}
				break;
			case 2: {
				// 失败
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)
						|| "WechatTimelineNotSupportedException".equals(expName)) {
					showNotification(2000, CxNeighbourAddInvitation.this.getString(
							R.string.wechat_client_inavailable));
				} else {
					showNotification(2000,
							CxNeighbourAddInvitation.this.getString(R.string.cx_fa_third_share_fail));
				}
			}
				break;
			case 3: {
				// 取消
				showNotification(2000,
						CxNeighbourAddInvitation.this.getString(R.string.cx_fa_third_share_cancel));
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
			Context app = CxNeighbourAddInvitation.this.getApplicationContext();
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
				ToastUtil.getSimpleToast(CxNeighbourAddInvitation.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	
}
