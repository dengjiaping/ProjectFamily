package com.chuxin.family.neighbour;

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.neighbour.CxNeighbourFragment.DeleteFeedBack;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxNbReply;
import com.chuxin.family.parse.been.CxNeighbourInvitationList;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.parse.been.data.InvitationData;
import com.chuxin.family.parse.been.data.InvitationList;
import com.chuxin.family.parse.been.data.InvitationPhoto;
import com.chuxin.family.parse.been.data.InvitationPost;
import com.chuxin.family.parse.been.data.InvitationReply;
import com.chuxin.family.parse.been.data.InvitationUserInfo;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxAudioFileResourceManager;
import com.chuxin.family.utils.CxBaseDiskCache;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.CustomTextView;
import com.chuxin.family.widgets.CxImagePager;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.widgets.ScrollableListView;
import com.chuxin.family.widgets.CxInputPanel.OnEventListener;
import com.chuxin.family.widgets.ScrollableListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;
import com.uraroji.garage.android.mp3recvoice.RecMicToMp3;

import org.fmod.effects.RkSoundEffects;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class CxNbNeighboursHome extends CxRootActivity {

	protected static final int ADD_INVITATION = 0;

	private static final int UPDATE_RECORD_TIME = 1;

	private static final int SEND_RECORD = 2;

	public static final int READ_RECORD = 3;
	public static final int STOP_READ_RECORD = 4;

	protected static final int MAX_VU_SIZE = 14; // set phone volume level 14

	private InputMethodManager input;
	
	private List<InvitationData> mFeedsData; // 帖子的数据

	private int mReplyIndex; // 需要删除的回复
	private int mFeedIndex; // 需要删除的回复所在的帖子在数据源中的位置
	private String mReplyFeedId; // 评论的帖子ID
	private String mReply_to; // 回复给的对象（评论时为null,回复时为对方的ID）
	public static boolean mRecordStart;

	private boolean isFirstComplete = false;;
	private boolean isDownComplete = true; // 向下拉完成的标识位。默认为true表示完成
	private boolean isUpComplete = true; // 向上推完成的标识位。默认为true表示完成

	private CurrentObserver mServiceObserver;
	private CurrentObserver mGlobalObserver;
	private CurrentObserver mAddFeedObserver;

	private Handler mNbHandler;

	private String neighbourId;
	
	private AudioManager mAudioManager;
	
	private SensorManager mSensorManager = null; // 传感器管理器  
    private Sensor mProximiny = null; // 传感器实例  
    private float mFproximiny; // 当前传感器距离
    
    private LinearLayout mVoicePanel;

	private static String[] sRecordingDrawables = new String[] {
			"pub_microphone_volume_1", "pub_microphone_volume_2",
			"pub_microphone_volume_3", "pub_microphone_volume_4",
			"pub_microphone_volume_5", "pub_microphone_volume_6",
			"pub_microphone_volume_7", "pub_microphone_volume_8",
			"pub_microphone_volume_9", "pub_microphone_volume_10",
			"pub_microphone_volume_11", "pub_microphone_volume_12",
			"pub_microphone_volume_13", "pub_microphone_volume_14", };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_neighbour_home);
		input = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		mNbHandler = new Handler() {

			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case UPDATE_RECORD_TIME:
					mChatRecordRelativeLayout
							.setBackgroundResource(R.drawable.pub_recorder);
//					int vuSize = MAX_VU_SIZE * mReocrdCount / 90;
					int vuSize = 0;
					if(mReocrdCount<7){
						vuSize=mReocrdCount;
					}else if(mReocrdCount<22){
						vuSize=(int)((mReocrdCount-7)/3)+6;
					}else if(mReocrdCount<70){
						vuSize=11;
					}else if(mReocrdCount<85){
						vuSize=12;
					}else{
						vuSize=13;
					}
					int resId = getResources().getIdentifier(
							sRecordingDrawables[vuSize], "drawable",
							CxNbNeighboursHome.this.getPackageName());
					mRecordImageView.setImageResource(resId);
					if (mRecordRemainTimeTextView.getVisibility() != View.VISIBLE) {
						mRecordRemainTimeTextView.setVisibility(View.VISIBLE);
					}
					mRecordRemainTimeTextView.setText(String.format(getResources().getString(R.string.cx_fa_chat_record_time_remianing)
							,(91 - mReocrdCount)));
					mRecordCancelTip.setText(R.string.cx_fa_chat_record_tip);
					if ((90 - ((System.currentTimeMillis() - mRecorderStartTime) / 1000)) <= 0) {
						mRecordView.setVisibility(View.GONE);
						mInputPanel.setBackgroundResource(R.drawable.chatview_voice);
						stopRecord();
					}
					break;
				case SEND_RECORD:
					// RkLog.v(TAG, "filePath=" + mSoundFilePath);
					int audioLength = (int) ((mRecorderStopTime - mRecorderStartTime) / 1000);
					if (audioLength > 1) {

						try {
							DialogUtil.getInstance().getLoadingDialogShow(CxNbNeighboursHome.this, -1);
							CxNeighbourApi.getInstance().requestReply(mReplyFeedId, "audio", null,
									mSoundFilePath, audioLength, mReply_to,null, mSendCommentCallback);
							mInputPanel.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {

						ToastUtil.getSimpleToast(CxNbNeighboursHome.this,-1,
							getString(R.string.cx_fa_chat_record_time_short_msg),1).show();
					}
					break;

				case STOP_READ_RECORD:
					ImageView image = (ImageView) msg.obj;
					stopVoice(image);
					break;
				case READ_RECORD:
					// updateChatView();
					break;

				default:
					break;
				}
			}

		};

		Intent intent = getIntent();
		neighbourId = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_ID);
		wifeName = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_NAME);
		husbandName = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_NAME);
		wifeUrl = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL);
		husbandUrl = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL);
		pairId = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID);

		init();

	}

	public void init() {

		Button mTitleBack = (Button) findViewById(R.id.cx_fa_activity_title_back);
		Button mTitleMessage = (Button) findViewById(R.id.cx_fa_activity_title_more);
		mTitleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
		
		mTitleMessage.setVisibility(View.VISIBLE);
		mTitleMessage.setBackgroundResource(R.drawable.cx_fa_nb_chat_btn);

		mTitleBack.setOnClickListener(titleBtnClick);
		mTitleMessage.setOnClickListener(titleBtnClick);
		mTitleBack.setText(getString(R.string.cx_fa_navi_back));
		mTitleText.setText(getString(R.string.cx_fa_neighbour_neighbours_title_name));

		mInputPanel = (CxInputPanel) findViewById(R.id.cx_fa_widget_input_layer);
		CxInputPanel.sInputPanelUse = CxNeighbourFragment.RK_CURRENT_VIEW;
		ImageButton mPlusButton1 = (ImageButton) mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout1_button1);
		mPlusButton1.setVisibility(View.GONE);

		ImageButton mPlusButton2 = (ImageButton) mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout2_button3);
		mPlusButton2.setVisibility(View.GONE);

		mInputText = (EditText) mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout1_textedit1);

		mSendReplyLayer = (LinearLayout) findViewById(R.id.cx_fa_neighbour_home_send_reply);
		mSendReplyLayer.setVisibility(View.GONE);

		mNbContent = (ScrollableListView) findViewById(R.id.nb_home_content_ListView);

		mNbRecordLayout = (RelativeLayout) findViewById(R.id.cx_fa_view_nb_home_record_relativelayout);
		mRecordView = (LinearLayout) findViewById(R.id.cx_fa_view_nb_home_record_include);

		mRecordImageView = (ImageView) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_imageview);
		mChatRecordRelativeLayout = (RelativeLayout) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_relativelayout);
		mRecordRemainTimeTextView = (TextView) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_recordtime_textview);
		mRecordCancelTip = (TextView) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_textview_tip);


		mAdapter = new NeighbourAdapter();
		mNbContent.setAdapter(mAdapter);
		mNbContent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputPanel.setDefaultMode();
				mInputPanel.setVisibility(View.GONE);
				return false;
			}
		});

		mNbContent.setOnHeaderRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (isFirstComplete && isDownComplete) {
					isDownComplete = false;
					CxNeighbourApi.getInstance().requestHomeInvitationList(neighbourId,0, 15,
							new InvitationResponse(true, false),
							CxNbNeighboursHome.this,neighbourId);
				} else {
					new Handler(CxNbNeighboursHome.this.getMainLooper()) {
						public void handleMessage(android.os.Message msg) {
							mNbContent.onRefreshComplete();
							// mZoneContent.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
						};
					}.sendEmptyMessageDelayed(1, 500);
				}

			}
		});

		mNbContent.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (isUpComplete) {
					isUpComplete = false;
					int offest = (null == mFeedsData ? 0 : mFeedsData.size());
					CxNeighbourApi.getInstance().requestHomeInvitationList(neighbourId,offest, 15,
							new InvitationResponse(false, false),
							CxNbNeighboursHome.this,neighbourId);
				} else {
					mNbContent.refreshComplete();
				}
			}
		});

		// 面板事件添加 add by shichao 20130708
		mInputPanel.setOnEventListener(new OnEventListener() {

			@Override
			public int onMessage(String msg, int flag) {
				if (flag == 0) {
					// 发普通文字
					if (TextUtils.isEmpty(msg)) {
						ToastUtil.getSimpleToast(CxNbNeighboursHome.this, -1,
								getString(R.string.cx_fa_zone_no_content), 1)
								.show();
						return 0;
					}
					try {
						DialogUtil.getInstance().getLoadingDialogShow(CxNbNeighboursHome.this, -1);
						CxNeighbourApi.getInstance().requestReply(mReplyFeedId,
								"text", msg, null, 0, mReply_to, null,
								mSendCommentCallback);
						// RkMateParams.getInstance().getMateUid()不传，这只是发表评论，不是回复
						// RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%call");
						// 关闭输入区
						mInputPanel.setDefaultMode();
						mInputPanel.setVisibility(View.GONE);
						return 0;
					} catch (Exception e) {
						e.printStackTrace();
						return 1;
					}
				} else {
					return 1;
				}
			}

			@Override
			public void onButton2Click(View button) {
			}

			@Override
			public void onButton1Click(View button) {
			}

			@Override
			public void onStopMoveEvent(View v, MotionEvent m) {
				stopRecordEvent(m);
			}

			@Override
			public void onStartRecordEvent(View v, MotionEvent m) {
				mInputPanel.setBackgroundResource(R.drawable.chatview_voice_h);
				CxGlobalParams.getInstance().setRecorderFlag(true);
				mNbRecordLayout.setVisibility(View.VISIBLE);
				mRecordView.setVisibility(View.VISIBLE);
				mChatRecordRelativeLayout
						.setBackgroundResource(R.drawable.pub_recorder);
				mRecordCancelTip.setText(R.string.cx_fa_chat_record_tip);
				mRecordStart = true;
				startRecord();
				startTimer();
				// android.os.Message msg0 = android.os.Message.obtain(
				// mNbHandler, UPDATE_RECORD_TIME);
			}

			@Override
			public void onAcionMoveEvent(View v, MotionEvent m) {
				moveRecordEvent(m);
			}

			@Override
			public void onOtherEvent(View v, MotionEvent m) {
				stopRecordEvent(m);
			}

            @Override
            public void onButton0Click(View button) {

            }
		});
		
		//加载本地数据(本地保存15条，不至于会超时）
		CxNbCacheData cacheData = new CxNbCacheData(CxNbNeighboursHome.this);

		InvitationList list = cacheData.queryCacheData(pairId);
		if( null != list){
			InvitationUserInfo userInfo = list.getUserInfo();
			if(userInfo!=null){
				bgUrl=userInfo.getBgUrl();
			}
			ArrayList<InvitationData> feeds = list.getDatas();
			if ((null != feeds) && (feeds.size() > 0)) {
//				 mFeedsData=feeds;
				 mAdapter.updataView(feeds);
			}
		}


		// 加载本地数据后首先到网络获取第一屏数据
		isFirstComplete = false;
		CxNeighbourApi.getInstance().requestHomeInvitationList(neighbourId, 0, 15,new InvitationResponse(true, true),
						CxNbNeighboursHome.this,neighbourId);
		
		mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null){
        	
	        if (CxGlobalParams.getInstance().isChatEarphone()) {
	            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
	        } else {
	            mAudioManager.setMode(AudioManager.MODE_NORMAL);
	        }
        }
		
        mSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);  
        mProximiny = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); 

	}
	
	protected void onDestroy() {
		super.onDestroy();
		
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxNbNeighboursHome.this, "neighbour_bg", CxNbNeighboursHome.this);
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//			
//			CxResourceManager resourceManager2 = CxResourceManager.getInstance(
//					CxNbNeighboursHome.this, "head", CxNbNeighboursHome.this);
//			if (null != resourceManager2) {
//				resourceManager2.clearMemory();
//				resourceManager2 = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	};
	
	

	private String bgUrl = null;
	private String husbandName = null;
	private String husbandUrl = null;
	private String wifeName = null;
	private String wifeUrl = null;
	private String pairId = null;

	// 空间列表请求的网络应答
	class InvitationResponse implements JSONCaller {

		private boolean isPushDown = true; // 默认向下拉
		private boolean isFirst = false; // 默认不是第一次获取空间资源

		public InvitationResponse(boolean pushDown, boolean first) {
			this.isFirst = first;
			this.isPushDown = pushDown;
		}

		@Override
		public int call(Object result) {
			if (isFirst) { // 将首次刷新界面结束的标识位置为true
				isFirstComplete = true;
			}

			if (isPushDown) { // 往下拉(或者push)
				isDownComplete = true;
			} else {
				isUpComplete = true;
			}

			new Handler(CxNbNeighboursHome.this.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					if (isPushDown) {
						mNbContent.onRefreshComplete();
					} else {
						mNbContent.refreshComplete();
					}

				};
			}.sendEmptyMessage(1);

			if (null == result) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxNeighbourInvitationList feedList = null;
			try {
				feedList = (CxNeighbourInvitationList) result;
			} catch (Exception e) {
			}
			if (null == feedList || feedList.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = feedList.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(feedList.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(feedList.getMsg(),0);
				}
				return 1;
			}

			if (isFirst) {

				InvitationUserInfo userInfo = feedList.getData().getUserInfo();
				if (userInfo != null) {
					bgUrl = userInfo.getBgUrl();
					husbandName = userInfo.getHusbandName();
					husbandUrl = userInfo.getHusbandUrl();
					wifeName = userInfo.getWifeName();
					wifeUrl = userInfo.getWifeUrl();			
				}
				
				if (null != mFeedsData) {
					mFeedsData.clear();
					mFeedsData = null;
				}

				mFeedsData = feedList.getData().getDatas();
				updateListview.sendEmptyMessage(1);
				return 0;
			}

			/*
			 * if (null != mFeedsData) { mFeedsData.clear(); mFeedsData = null;
			 * }
			 */

			if (isPushDown) { // 往下拉(或者push)

				mFeedsData = feedList.getData().getDatas(); // 直接换成最新数据即可

			} else { // 往上翻
				if ((null == feedList.getData().getDatas())
						|| (feedList.getData().getDatas().size() < 1)) {
					// 没有数据
					return 0;
				}
				if (null == mFeedsData) {
					mFeedsData = new ArrayList<InvitationData>();
				}
				mFeedsData.addAll(feedList.getData().getDatas());
			}
			
			
			InvitationUserInfo userInfo = feedList.getData().getUserInfo();
			if (userInfo != null) {
				bgUrl = userInfo.getBgUrl();
				husbandName = userInfo.getHusbandName();
				husbandUrl = userInfo.getHusbandUrl();
				wifeName = userInfo.getWifeName();
				wifeUrl = userInfo.getWifeUrl();			
			}
			
			mVoicePlayFlag = false;
			updateListview.sendEmptyMessage(1);
			return 0;
		}

	}

	Handler updateListview = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter.updataView(mFeedsData);
		};
	};

	// item的adapter
	class NeighbourAdapter extends BaseAdapter {
		private final int HEAD_VIEW = 0;
		private final int ITEM_VIEW = 1;

		private List<InvitationData> mAdapterData;

		public synchronized void updataView(List<InvitationData> adapterData) {

			mAdapterData = adapterData;

			NeighbourAdapter.this.notifyDataSetChanged();
		}

		private String filterStartZero(String stamp) {
			if (null == stamp) {
				return null;
			}
			if (stamp.startsWith("0")) {
				return stamp.replaceFirst("0", "");
			}
			return stamp;
		}

		/**
		 * 判断是否是同一日期
		 * 
		 * @param timeStampStr
		 *            , long类型的时间戳的字符串
		 * @param mdStr
		 *            ，形如“dd:MM月"的字符串
		 * @return
		 */
		private boolean equalDate(String timeStampStr, String mdStr) {
			if ((null == timeStampStr) || (null == mdStr)) {
				return false;
			}
			Date date = new Date(Long.parseLong(timeStampStr) * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("MM月:dd");
			String preTimeStr = sdf.format(date);

			if (TextUtils.equals(preTimeStr, mdStr)) {
				return true;
			}

			return false;
		}

		@Override
		public int getItemViewType(int position) {
			if (0 == position) {
				return HEAD_VIEW;
			}
			return ITEM_VIEW;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			// RkLog.i("getCount", ""+ (null == mAdapterData ? 1 :
			// (mAdapterData.size()+1) ) );

			if (null == mAdapterData) {
				return 1;
			}

			return mAdapterData.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		
		/**
		 * 显示头像的大图
		 * @param headImg	: 头像对象
		 * @param picUrl		: 头像地址
		 * @param defaultImg : 用户未设头像时，要显示的默认头像
		 */
		private void setShowBigHeadPic(ImageView headImg, final String picUrl, final int defaultImg){
			headImg.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					final Dialog dialog = new Dialog(CxNbNeighboursHome.this, R.style.simple_dialog);
					
					View inflate 	= View.inflate(CxNbNeighboursHome.this, R.layout.cx_fa_activity_neighbour_show_pic,null);
					CxImageView headImgView = (CxImageView) inflate.findViewById(R.id.cx_fa_activity_neighbour_show_pic_imageView);
//					if(picUrl!=null && !picUrl.equals("")){
//						// 用户设置的头像
//						headImgView.setImage(picUrl, false, 100, RkNbNeighboursHome.this, "head", RkNbNeighboursHome.this);
//					}else{
//						// 默认头像
//						headImgView.setImageResource(defaultImg);
//					}
//					
					
					headImgView.displayImage(imageLoader, picUrl, defaultImg, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					dialog.setContentView(inflate);
					dialog.show();
					
					// 点击图片关闭窗口
					headImgView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							 dialog.dismiss();
						}
					});
					
				}
			});
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CxLog.i("Adapter getView", "" + position);

			if (0 == position) {
				if (null == convertView) {
					convertView = CxNbNeighboursHome.this.getLayoutInflater()
							.inflate(R.layout.cx_fa_fragment_neighbour_header,
									null);
				}
				// 加载二人空间的背景图、对方头像，自己的头像
				CxImageView nbBackground = (CxImageView) convertView
						.findViewById(R.id.cx_fa_neighbour_bg);
				ViewGroup.LayoutParams param = nbBackground.getLayoutParams();
				param.height = (int) (getResources().getDisplayMetrics().widthPixels * 0.66f + 0.5f);
				nbBackground.setLayoutParams(param);
//				if (bgUrl != null  && !bgUrl.equals(""))
//					nbBackground.setImage(bgUrl, false, 260,RkNbNeighboursHome.this, "neighbour_bg",RkNbNeighboursHome.this);
				nbBackground.displayImage(imageLoader, bgUrl, 
						R.drawable.neighbor_image_ourhome, false, 0);
				
				LinearLayout familyNameLayout = (LinearLayout) convertView.findViewById(R.id.cx_fa_neighbour_family_text);
				CxImageView girlHead = (CxImageView) convertView.findViewById(R.id.nb_girlHeadView);
				CxImageView boyHead = (CxImageView) convertView.findViewById(R.id.nb_boyHeadView);
//				ImageView changeName = (ImageView) convertView.findViewById(R.id.nb_head_changenickname_or_addpost);
				TextView familyText = (TextView) convertView.findViewById(R.id.nb_familyText);
				
				String familyName="";
				String headUrl1 = "";					// 第一个头像地址
				String headUrl2 = "";					// 第二个头像地址
				int defaultHeadImg1 = 0;			// 第一个头像地址为空时，默认要显示的图片资源
				int defaultHeadImg2 = 0;			// 第二个头像地址为空时，默认要显示的图片资源
				String version = getString(CxResourceString.getInstance().str_pair);
				if("老公".equals(version)){
//					girlHead.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					girlHead.setImage(wifeUrl, false, 100, RkNbNeighboursHome.this,"head", RkNbNeighboursHome.this);
					
					girlHead.displayImage(imageLoader, wifeUrl, 
							CxResourceDarwable.getInstance().dr_zone_icon_small_me, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
//					boyHead.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					boyHead.setImage(husbandUrl, false, 100,RkNbNeighboursHome.this, "head",RkNbNeighboursHome.this);
					boyHead.displayImage(imageLoader, husbandUrl, 
							CxResourceDarwable.getInstance().dr_zone_icon_small_oppo, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					familyName = wifeName + "和" + husbandName + "一家";
					
					headUrl1 = wifeUrl;
					headUrl2 = husbandUrl;
					defaultHeadImg1 = R.drawable.cx_fa_wf_icon_small;
					defaultHeadImg2 = R.drawable.cx_fa_hb_icon_small;
				}else{
//					girlHead.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					girlHead.setImage(husbandUrl, false, 100, RkNbNeighboursHome.this,"head", RkNbNeighboursHome.this);

					girlHead.displayImage(imageLoader, husbandUrl, 
							CxResourceDarwable.getInstance().dr_zone_icon_small_me, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
//					boyHead.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					boyHead.setImage(wifeUrl, false, 100,RkNbNeighboursHome.this, "head",RkNbNeighboursHome.this);
					
					boyHead.displayImage(imageLoader, wifeUrl, 
							CxResourceDarwable.getInstance().dr_zone_icon_small_oppo, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					familyName = husbandName + "和" + wifeName + "一家";
					
					headUrl1 = husbandUrl;
					headUrl2 = wifeUrl;
					defaultHeadImg1 = R.drawable.cx_fa_hb_icon_small;
					defaultHeadImg2 = R.drawable.cx_fa_wf_icon_small;
				}
				
				// 设置头像可点击放大
				setShowBigHeadPic(girlHead, headUrl1, defaultHeadImg1);			
				setShowBigHeadPic(boyHead, headUrl2, defaultHeadImg2);
				
				
				familyText.setText(familyName);				
				familyNameLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						View view = View.inflate(CxNbNeighboursHome.this,R.layout.cx_fa_widget_neighbour_dialog, null);
						TextView grilText = (TextView) view.findViewById(R.id.nb_neighbours_home_change_griltext); 
						TextView boyText = (TextView) view.findViewById(R.id.nb_neighbours_home_change_boytext); 
						final EditText girlEdit = (EditText) view.findViewById(R.id.nb_change_grilname);
						final EditText boyEdit = (EditText) view.findViewById(R.id.nb_change_boyname);
						Button sureBtn = (Button) view.findViewById(R.id.nb_change_name_ok);
						Button cancleBtn = (Button) view.findViewById(R.id.nb_change_name_cancle);

						final Dialog dialog = new Dialog(CxNbNeighboursHome.this, R.style.simple_dialog);
						
						String version = getString(CxResourceString.getInstance().str_pair);
						if("老公".equals(version)){
							grilText.setText(getString(R.string.cx_fa_neighbour_home_change_griltext));
							boyText.setText(getString(R.string.cx_fa_neighbour_home_change_boytext));
							girlEdit.setText(wifeName);
							girlEdit.setHint(wifeName);
							boyEdit.setText(husbandName);
							boyEdit.setHint(husbandName);
							
							sureBtn.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (TextUtils.isEmpty(girlEdit.getText().toString())
											&& TextUtils.isEmpty(boyEdit.getText().toString())) {
										ToastUtil.getSimpleToast(CxNbNeighboursHome.this, -1,getString
												(R.string.cx_fa_neighbour_neighbours_home_un_changename), 1).show();
									} else {
										String girlName = null;
										String boyName = null;
										if (TextUtils.isEmpty(girlEdit.getText().toString())) {
											girlName = wifeName;
										} else {
											girlName = girlEdit.getText().toString().trim();
										}
										if (TextUtils.isEmpty(boyEdit.getText().toString())) {
											boyName = husbandName;
										} else {
											boyName = boyEdit.getText().toString().trim();
										}
										DialogUtil.getInstance().getLoadingDialogShow(CxNbNeighboursHome.this, -1);
										CxNeighbourApi.getInstance().requestChangeName(neighbourId,girlName, boyName,changeNameCaller);
										dialog.dismiss();
									}

								}
							});
							
						}else{
							grilText.setText(getString(R.string.cx_fa_neighbour_home_change_boytext));
							boyText.setText(getString(R.string.cx_fa_neighbour_home_change_griltext));
							girlEdit.setText(husbandName);
							girlEdit.setHint(husbandName);
							boyEdit.setText(wifeName);
							boyEdit.setHint(wifeName);
							
							sureBtn.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (TextUtils.isEmpty(girlEdit.getText().toString())
											&& TextUtils.isEmpty(boyEdit.getText().toString())) {
										ToastUtil.getSimpleToast(CxNbNeighboursHome.this, -1,getString
												(R.string.cx_fa_neighbour_neighbours_home_un_changename), 1).show();
									} else {
										String girlName = null;
										String boyName = null;
										if (TextUtils.isEmpty(girlEdit.getText().toString())) {
											boyName = husbandName;
										} else {
											boyName = girlEdit.getText().toString().trim();
										}
										if (TextUtils.isEmpty(boyEdit.getText().toString())) {
											girlName = wifeName;
										} else {
											girlName = boyEdit.getText().toString().trim();
										}
										CxNeighbourApi.getInstance().requestChangeName(neighbourId,girlName, boyName,changeNameCaller);
										dialog.dismiss();
									}

								}
							});
						}

						
						cancleBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
							
						dialog.setContentView(view);
						dialog.show();

					}
				});

				return convertView;
			}

			final int itemLocation = position - 1;
			NbItemViewHolder holder = null;
			if (null == convertView) {
				convertView = CxNbNeighboursHome.this.getLayoutInflater()
						.inflate(R.layout.cx_fa_fragment_neighbour_list_item2,
								null);
				holder = new NbItemViewHolder();
				holder.commentBtn = (ImageButton) convertView
						.findViewById(R.id.nb_invitation_comment2);
				holder.deleteBtn = (ImageButton) convertView
						.findViewById(R.id.nb_invitation_delete2);
				holder.speakDate = (TextView) convertView
						.findViewById(R.id.nb_invitation_speakdata2);
				holder.girlHead = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_girlhead2);
				holder.boyHead = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_boyhead2);
				holder.speakerName = (TextView) convertView
						.findViewById(R.id.nb_invitation_speakname2);
				holder.speakTime = (TextView) convertView
						.findViewById(R.id.nb_invitation_speaktime2);
				holder.speakWord = (CustomTextView) convertView
						.findViewById(R.id.nb_invitation_speakword2);
				holder.commentAndReplayOfRecord = (LinearLayout) convertView
						.findViewById(R.id.nb_invitation_commentAndReplayOfRecord2);
				holder.addNbNow = (Button) convertView
						.findViewById(R.id.nb_invitation_addneighbour2);	
				holder.textMore=(TextView)convertView.findViewById(R.id.nb_invitation_text_more2);
				
				holder.headLayout=(LinearLayout) convertView.findViewById(R.id.nb_invitation_headlayout2);
				
				holder.messageLayout=(LinearLayout) convertView.findViewById(R.id.nb_invitation_headlayout_message2);
				holder.msgFamilyName=(TextView) convertView.findViewById(R.id.nb_invitation_familyname_message2);
				holder.msgGirlHead=(CxImageView) convertView.findViewById(R.id.nb_invitation_girlhead_message2);
				holder.msgBoyHead=(CxImageView) convertView.findViewById(R.id.nb_invitation_boyhead_message2);
				
				holder.msgSpeakLayout=(LinearLayout) convertView.findViewById(R.id.nb_invitation_speakline_message2);
				holder.msgSpeakerName=(TextView) convertView.findViewById(R.id.nb_invitation_speakname_message2);
				holder.msgSpeakTime=(TextView) convertView.findViewById(R.id.nb_invitation_speaktime_message2);
				

				holder.sharedPhotos = new ArrayList<CxImageView>();
				CxImageView firstImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_first_9image2);
				holder.sharedPhotos.add(firstImage);
				CxImageView secondImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_second_9image2);
				holder.sharedPhotos.add(secondImage);
				CxImageView thirdImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_third_9image2);
				holder.sharedPhotos.add(thirdImage);
				CxImageView forthImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_forth_9image2);
				holder.sharedPhotos.add(forthImage);
				CxImageView fifthImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_fifth_9image2);
				holder.sharedPhotos.add(fifthImage);
				CxImageView sixthImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_sixth_9image2);
				holder.sharedPhotos.add(sixthImage);
				CxImageView seventhImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_seventh_9image2);
				holder.sharedPhotos.add(seventhImage);
				CxImageView eighthImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_eighth_9image2);
				holder.sharedPhotos.add(eighthImage);
				CxImageView ninethImage = (CxImageView) convertView
						.findViewById(R.id.nb_invitation_speakphoto_nineth_9image2);
				holder.sharedPhotos.add(ninethImage);

				convertView.setTag(holder);

			} else {
				holder = (NbItemViewHolder) convertView.getTag();
			}

			if ((null == mAdapterData) || (mAdapterData.size() < position)) { // 第一个item不需要数据
				return convertView;
			}
			final InvitationData tempFeed = mAdapterData.get(position - 1); // 第一个item不需要数据

			
			Button addNbNow=holder.addNbNow;
			addNbNow.setVisibility(View.GONE);
			
			String hintCommentName="";
			
			LinearLayout messageLayout = holder.messageLayout;
			TextView msgFamilyName = holder.msgFamilyName;
			CxImageView msgGirlHead = holder.msgGirlHead;
			CxImageView msgBoyHead = holder.msgBoyHead;
			LinearLayout msgSpeakLayout = holder.msgSpeakLayout;
			TextView msgSpeakerName = holder.msgSpeakerName;
			TextView msgSpeakTime = holder.msgSpeakTime;
			
			
			// 头像、名称和删除按钮
			LinearLayout headLayout = holder.headLayout;
			CxImageView girlHeadView = holder.girlHead;
			CxImageView boyHeadView = holder.boyHead;
			TextView speakerName = holder.speakerName;
			ImageButton deleteBtn = holder.deleteBtn; // 删除按钮
			TextView textMore=holder.textMore;

			deleteBtn.setVisibility(View.GONE);
			
			String type=tempFeed.getType();
			CxLog.i("RkNbNeighboursHome_men", type);
			if("post".equals(type)){
				messageLayout.setVisibility(View.GONE);
				headLayout.setVisibility(View.VISIBLE);
				msgSpeakLayout.setVisibility(View.GONE);
			}else if("message".equals(type)){
				messageLayout.setVisibility(View.VISIBLE);
				headLayout.setVisibility(View.GONE);
				msgSpeakLayout.setVisibility(View.VISIBLE);
			}
			

			
			String headUrl1 = "";					// 第一个头像地址
			String headUrl2 = "";					// 第二个头像地址
			int defaultHeadImg1 = 0;			// 第一个头像地址为空时，默认要显示的图片资源
			int defaultHeadImg2 = 0;			// 第二个头像地址为空时，默认要显示的图片资源
			String version = getString(CxResourceString.getInstance().str_pair);
			
			if("post".equals(type)){
				
				if("老公".equals(version)){
//					girlHeadView.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					girlHeadView.setImage(wifeUrl, false, 74, RkNbNeighboursHome.this,"head", RkNbNeighboursHome.this);
					
					girlHeadView.displayImage(imageLoader, wifeUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
//					boyHeadView.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					boyHeadView.setImage(husbandUrl, false, 74,RkNbNeighboursHome.this, "head", RkNbNeighboursHome.this);
					
					boyHeadView.displayImage(imageLoader, husbandUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					
					headUrl1 = wifeUrl;
					headUrl2 = husbandUrl;
					defaultHeadImg1 = R.drawable.cx_fa_wf_icon_small;
					defaultHeadImg2 = R.drawable.cx_fa_hb_icon_small;
				}else{
//					girlHeadView.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					girlHeadView.setImage(husbandUrl, false, 74, RkNbNeighboursHome.this,"head", RkNbNeighboursHome.this);
					
					girlHeadView.displayImage(imageLoader, husbandUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					boyHeadView.displayImage(imageLoader, wifeUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					
//					boyHeadView.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					boyHeadView.setImage(wifeUrl, false, 74,RkNbNeighboursHome.this, "head", RkNbNeighboursHome.this);
					

					
					
					headUrl1 = husbandUrl;
					headUrl2 = wifeUrl;
					defaultHeadImg1 = R.drawable.cx_fa_hb_icon_small;
					defaultHeadImg2 = R.drawable.cx_fa_wf_icon_small;
				}
				
				hintCommentName=tempFeed.getName();
				speakerName.setText(tempFeed.getName());
				
				
			}else if("message".equals(type)){
				
				if("老公".equals(version)){
//					msgGirlHead.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					msgGirlHead.setImage(wifeUrl, false, 74, RkNbNeighboursHome.this,"head", RkNbNeighboursHome.this);
//					msgBoyHead.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					msgBoyHead.setImage(husbandUrl, false, 74,RkNbNeighboursHome.this, "head", RkNbNeighboursHome.this);
			
					msgGirlHead.displayImage(imageLoader, wifeUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					msgBoyHead.displayImage(imageLoader, husbandUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					
					
					headUrl1 = wifeUrl;
					headUrl2 = husbandUrl;
					defaultHeadImg1 = R.drawable.cx_fa_wf_icon_small;
					defaultHeadImg2 = R.drawable.cx_fa_hb_icon_small;
				}else{
//					msgGirlHead.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					msgGirlHead.setImage(husbandUrl, false, 74, RkNbNeighboursHome.this,"head", RkNbNeighboursHome.this);
//					msgBoyHead.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					msgBoyHead.setImage(wifeUrl, false, 74,RkNbNeighboursHome.this, "head", RkNbNeighboursHome.this);
					
					msgBoyHead.displayImage(imageLoader, wifeUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					msgGirlHead.displayImage(imageLoader, husbandUrl, 
							CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					
					headUrl1 = husbandUrl;
					headUrl2 = wifeUrl;
					defaultHeadImg1 = R.drawable.cx_fa_hb_icon_small;
					defaultHeadImg2 = R.drawable.cx_fa_wf_icon_small;
				}
				
				if (TextUtils.equals(CxGlobalParams.getInstance().getPairId(),
						tempFeed.getPair_id())) { // 创建者是自己一家
					if (TextUtils.equals(CxGlobalParams.getInstance().getUserId(),tempFeed.getAuthor())) {
						msgSpeakerName.setText(getString(R.string.cx_fa_nls_me));		
						deleteBtn.setVisibility(View.VISIBLE);			
					}else{
						hintCommentName=getString(CxResourceString.getInstance().str_pair);
						msgSpeakerName.setText(hintCommentName);
						deleteBtn.setVisibility(View.GONE);
					}
					
					deleteBtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							DialogUtil du = DialogUtil.getInstance();
							du.setOnSureClickListener(new OnSureClickListener() {

								@Override
								public void surePress() {
									DialogUtil.getInstance().getLoadingDialogShow(CxNbNeighboursHome.this, -1);
									CxNeighbourApi.getInstance().requestDeleteFeed(tempFeed.getId(),
											new DeleteFeedBack(itemLocation));
								}
							});
							Dialog dialog = du.getSimpleDialog(CxNbNeighboursHome.this,null,getString(R.string.cx_fa_delete_comfirm_text),
									null, null);
							dialog.setCancelable(true);
							dialog.show();

						}
					});
					
					if("老公".equals(version)){
						msgFamilyName.setText("我们和"+wifeName+"一家私聊");
					}else{
						msgFamilyName.setText("我们和"+husbandName+"一家私聊");
					}
				
				}else{
					
					hintCommentName=tempFeed.getName();
					msgSpeakerName.setText(hintCommentName);
					deleteBtn.setVisibility(View.GONE);
					
					if("老公".equals(version)){
						msgFamilyName.setText(wifeName+ "一家和我们私聊");
					}else{
						msgFamilyName.setText(husbandName+ "一家和我们私聊");
					}
				}	
			}
			
			
			// 设置头像点击显示大图
			setShowBigHeadPic(girlHeadView, headUrl1, defaultHeadImg1);			
			setShowBigHeadPic(boyHeadView, headUrl2, defaultHeadImg2);		
			setShowBigHeadPic(msgGirlHead, headUrl1, defaultHeadImg1);			
			setShowBigHeadPic(msgBoyHead, headUrl2, defaultHeadImg2);		

			
			// 时间
			TextView speakTime = holder.speakTime;
			SimpleDateFormat sdf = new SimpleDateFormat("MM月:dd:HH:mm");
			CxLog.i("zone feed create time:", "" + tempFeed.getCreate());
			Date createStamp = new Date(Long.parseLong(tempFeed.getCreate()) * 1000L);
			
			String update_time = tempFeed.getUpdate_time();
			if("0".equalsIgnoreCase(update_time) || update_time==null){
				update_time=tempFeed.getCreate();
			}
			
			Date updateStamp = new Date(Long.parseLong(update_time) * 1000L);
			
			String stampStr = sdf.format(createStamp);
			String[] stampArray = stampStr.split(":");
			String dayStr = null, monthStr = null, timeStr = null;
			monthStr = filterStartZero(stampArray[0]);
			dayStr = stampArray[1]; // filterStartZero(stampArray[1]);
			timeStr = stampArray[2] + ":" + stampArray[3];

			speakTime.setText(timeStr);
			msgSpeakTime.setText(timeStr+"  "+monthStr+dayStr+"日");

			if("message".equals(type)){
				String stampStr2 = sdf.format(updateStamp);
				String[] stampArray2 = stampStr2.split(":");
				monthStr = filterStartZero(stampArray2[0]);
				dayStr = stampArray2[1];				
			}

			CustomTextView speakWord = holder.speakWord;
			LinearLayout photosLayout = (LinearLayout) convertView
					.findViewById(R.id.cx_fa_neighbour_shared_photos2);

			InvitationPost feedContent = tempFeed.getPost();
			if (null != feedContent) {
				// 文字内容
				if (!TextUtils.isEmpty(feedContent.getText())) {
					speakWord.setVisibility(View.VISIBLE);
					final String text=feedContent.getText();		
					if(text.length()>100){
						final String simpleText=text.substring(0, 100)+"...";
						textMore.setVisibility(View.VISIBLE);
						textMore.setText("显示更多>>");						
						speakWord.setText(simpleText);
						final TextView mSpeakWord=speakWord;
						final TextView mTextMore=textMore;
						textMore.setOnClickListener(new OnClickListener() {
							boolean unfold=false;
							@Override
							public void onClick(View v) {
								if(unfold){
									mTextMore.setText("显示更多>>");						
									mSpeakWord.setText(simpleText);
									unfold=false;
								}else{
									mTextMore.setText("收起");
									mSpeakWord.setText(text);			
									unfold=true;
								}
								
							}
						});
					}else{
						speakWord.setText(text);
						textMore.setVisibility(View.GONE);
					}
				} else {
					speakWord.setVisibility(View.GONE);
					textMore.setVisibility(View.GONE);
				}
				// 图片
				final List<InvitationPhoto> photos = feedContent.getPhotos();
				if ((null == photos) || (photos.size() < 1)) { // 没有照片
					photosLayout.setVisibility(View.GONE);
				} else { // 有照片
					photosLayout.setVisibility(View.VISIBLE);
					int len = photos.size();

					/* 分别得到有多张图时和只有一张图时，每个图片的显示布局。 目的:只有一张图时，显示大图 */
					// 有多张图片时每张图片的布局参数
					int screen_w = getResources().getDisplayMetrics().widthPixels; // 屏幕宽度
					// 三个图像减去父对象margin及padding后，可用的宽度
					int row_w = screen_w- ScreenUtil.dip2px(CxNbNeighboursHome.this, 12 * 2 + 15*2+14*2- 2 * 6); 
					int w = (Integer) (row_w / 3); // 自动算图的宽度(有一个风险，如果屏幕的宽大于高，这个就会特别难看)
					int h = w;
					LinearLayout.LayoutParams layoutParaForMorePic = new LinearLayout.LayoutParams(
							w, h);
					int margin = ScreenUtil.dip2px(CxNbNeighboursHome.this, 2);
					layoutParaForMorePic.setMargins(margin, margin, margin,
							margin);

					// 只有一个图片的布局参数
					w = ScreenUtil.dip2px(CxNbNeighboursHome.this, 170); // dp转换为pix
					h = w;
					LinearLayout.LayoutParams layoutParaForOnlyOnePic = new LinearLayout.LayoutParams(
							w, h);

					// 得到图片的所有路径，供看大图时使用 (
					ArrayList<String> imagepaths = new ArrayList<String>();
					for(int i=0; i<len; i++){
						InvitationPhoto tempPhoto = photos.get(i);
						if(tempPhoto.getBig()!=null){
							imagepaths.add( tempPhoto.getBig() );
						}
					}
					final ArrayList<String> imgs = imagepaths;
					
					for (int i = 0; i < len; i++) { //
						CxImageView tempImage = holder.sharedPhotos.get(i);
						tempImage.setVisibility(View.VISIBLE);
						InvitationPhoto tempPhoto = photos.get(i);
//						tempImage.setImage(tempPhoto.getThumb(), false, 74,
//								RkNbNeighboursHome.this, "head",RkNbNeighboursHome.this);
						
						tempImage.displayImage(ImageLoader.getInstance(), 
								tempPhoto.getThumb(), R.drawable.chatview_imageloading, false, 0);
						
						final int clickItem = i;
						tempImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								CxNeighbourParam.getInstance().setPhotos(photos);
//								Intent imageDetail = new Intent(RkNbNeighboursHome.this,RkNeighbourImageDetail.class);
								Intent imageDetail = new Intent(CxNbNeighboursHome.this,CxImagePager.class);
								imageDetail.putExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER,clickItem);
				                imageDetail.putExtra(CxGlobalConst.S_STATE, CxImagePager.STATE_ZONE_NEIGHBOR_PARTNER);
				                imageDetail.putStringArrayListExtra("imagespath", imgs);
								startActivity(imageDetail);
								overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
							}
						});

						/*
						 * 如果只有一张图，则把它变为大图
						 * 说明:为了防止多图情况下的第一张图变成大图(上一条是单图,下一条是多图时会出现)。
						 * 在此需要重新设多图情况的第一张图的布局)
						 */
						if (len == 1) {
							tempImage.setLayoutParams(layoutParaForOnlyOnePic);
						} else {
							tempImage.setLayoutParams(layoutParaForMorePic);
						}
					}

					int gapSize = 9 - len; // 一共9张
					for (int k = 0; k < gapSize; k++) {
						CxImageView tempImage = holder.sharedPhotos.get(8 - k);
						tempImage.setVisibility(View.GONE);
					}
				}
				// 评论的内容
				LinearLayout commentAndReplayOfRecord = holder.commentAndReplayOfRecord;
				commentAndReplayOfRecord.removeAllViews();
				List<InvitationReply> replies = feedContent.getReplays();
				if ((null == replies) || (replies.size() < 1)) {
					commentAndReplayOfRecord.setVisibility(View.GONE);
				} else {
					commentAndReplayOfRecord.setVisibility(View.VISIBLE);
					for (int k = 0; k < replies.size(); k++) {
						final int tempIndex = k;
						View replyView = CxNbNeighboursHome.this.getLayoutInflater().inflate(
								R.layout.cx_fa_fragment_neighbour_list_reply_item,null);
						TextView itemReply = (TextView) replyView.findViewById(R.id.nb_post_reply_text_content);
						TextView replyTime = (TextView) replyView.findViewById(R.id.nb_post_reply_time);
						
						EnhancedGifView gifView = (EnhancedGifView) replyView.findViewById(R.id.nb_post_reply_expression_content);
						LinearLayout recordLayout = (LinearLayout) replyView.findViewById(R.id.nb_post_reply_record_content_linearlayout);
						ProgressBar recordProgress = (ProgressBar) replyView.findViewById(R.id.nb_post_reply_record_content_circleProgressBar);
						ImageView recordImage = (ImageView) replyView.findViewById(R.id.nb_post_reply_record_content_image);
						TextView recordLength = (TextView) replyView.findViewById(R.id.nb_post_reply_record_content_audiolength);

						itemReply.setText("");
						replyTime.setText("");

						final InvitationReply reply = replies.get(k);

						
						String speakerStr = "";
						String hintReplyName="";
						// 谁评论

						if (TextUtils.equals(reply.getAuthor(), CxGlobalParams
								.getInstance().getUserId())) {
							speakerStr = getString(R.string.cx_fa_nls_me);
						} else if (TextUtils.equals(reply.getAuthor(),
								CxGlobalParams.getInstance().getPartnerId())) {
							// speakerStr =
							// getString(R.string.cx_fa_role_zone_mate)+ (null
							// == RkGlobalParams.getInstance()
							// .getPartnerName() ? "":
							// RkGlobalParams.getInstance().getPartnerName());
							speakerStr = getString(CxResourceString.getInstance().str_pair);
						} else {			
							speakerStr = reply.getName();
						}
						itemReply.append(TextUtil.getNewSpanStr(speakerStr, 14, Color.argb(170, 0, 0, 0)));
						
						
						speakerStr = "";
						// 是否是回复（有2中情况：评论和回复）
						if ((null == reply.getReply_to())|| ("null".equalsIgnoreCase(reply.getReply_to()))) {
							// 不是回复，仅仅发表评论// 不做任何处理
						} else { // 回复(其实这样要注意自己给自己回复的情况，这是不允许的，暂时先不考虑这样的bug出现)
							
							speakerStr = getString(R.string.cx_fa_reply_text);
							itemReply.append( TextUtil.getNewSpanStr(speakerStr, 14, Color.argb(117, 0, 0, 0)) );
							
							/* 回复了谁(被回复的人) */
							speakerStr = "";
							/* 回复了谁(被回复的人) */
							if (reply.getReply_to().equals(CxGlobalParams.getInstance().getUserId())) { // 回复我
								speakerStr = getString(R.string.cx_fa_nls_me);
							} else if (reply.getReply_to().equals(CxGlobalParams.getInstance().getPartnerId())) { // 回复对方
								speakerStr = getString(CxResourceString.getInstance().str_pair);
								hintReplyName="回复 "+speakerStr+":";
								// speakerStr += (null == RkGlobalParams
								// .getInstance().getPartnerName() ? ""
								// : RkGlobalParams.getInstance()
								// .getPartnerName());
							} else {
								speakerStr = reply.getReply_name();
								hintReplyName="回复 "+speakerStr+":";
							}
							if (!speakerStr.equals("")) {
								itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.argb(170, 0, 0, 0)) );
							}

						}
						
//						replyTime.setText(DateUtil.getTimeDiffWithNow(reply.getTs()));
						
						speakerStr = " :  ";
						itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.argb(170, 0, 0, 0)) );
						
						String str2 = "   "+DateUtil.getTimeDiffWithNow(reply.getTs());
						String replyType = reply.getType();
						if ("audio".equals(replyType)) {
							recordLayout.setVisibility(View.VISIBLE);
							recordLength.setText(reply.getAudio_len()+"''");
							final ImageView mRecordImage = recordImage;
							final ProgressBar mRecordProgress = recordProgress;
							recordLayout.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// mRecordImage为final 播放动画也许会报错 带测试
									getAudioFile(reply.getAudio(),reply.getAudio_len(),mRecordImage,mRecordProgress);

								}
							});
							replyTime.append(TextUtil.getNewSpanStr(str2, 8, Color.argb(112, 0, 0, 0)));
							str2="";
						} else if ("text".equals(replyType)) {
							if (null != reply.getText()) {
								String[] faceTexts = getResources().getStringArray(R.array.face_texts);
								TypedArray faceImageIds = getResources().obtainTypedArray(R.array.cx_fa_ids_input_panel_face_images);

								boolean isFace=false;
								boolean hasright=false;
								String text = reply.getText();
								for(int i=0;i<text.length();i++){
									if('['==text.charAt(i)){
										CxLog.i("men", ">>>>>>>>>1");
										for(int j=i+1;j<text.length();j++){
											if(']'==text.charAt(j)){
												hasright=true;
												
												String substring = text.substring(i, j+1);
												for(int m=0;m<faceTexts.length;m++){
													if(faceTexts[m].equals(substring)){													
														isFace=true;
														int resourceId = faceImageIds.getResourceId(m, 0);
														SpannableStringBuilder spanStr = TextUtil.getImageSpanStr(substring, resourceId, 
																getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_neighbour_reply_emotion_size), 
																getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_neighbour_reply_emotion_size), CxNbNeighboursHome.this);
														itemReply.append(spanStr);
														break;
													}
												}	
												if(!isFace){
													itemReply.append(TextUtil.getNewSpanStr(substring, 14, Color.argb(117, 0, 0, 0)));						
												}
												i=j;
												break;											
											}
										}
										if(!hasright){
											itemReply.append(TextUtil.getNewSpanStr("[", 14, Color.argb(117, 0, 0, 0)));						
										}
									}else{
										itemReply.append(TextUtil.getNewSpanStr(text.charAt(i)+"", 14, Color.argb(117, 0, 0, 0)));
									}
									
								}
								
								replyTime.setVisibility(View.GONE);
								itemReply.append(TextUtil.getNewSpanStr(str2, 8, Color.argb(112, 0, 0, 0)));
							}

						}
						if (TextUtils.equals(reply.getAuthor(), CxGlobalParams
								.getInstance().getUserId())) { // 是自己说的可以删除评论或回复，否则是回复
							replyView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// 弹窗确认是否删除
									DialogUtil du = DialogUtil.getInstance();
									du.setOnSureClickListener(new OnSureClickListener() {
										@Override
										public void surePress() {
											try {
												mFeedIndex = itemLocation;
												mReplyIndex = tempIndex;
												DialogUtil.getInstance().getLoadingDialogShow(CxNbNeighboursHome.this, -1);
												CxNeighbourApi.getInstance().requestDeleteReply(reply.getReply_id(),deleteReply);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
									du.getSimpleDialog(CxNbNeighboursHome.this,null,getString(R.string.cx_fa_delete_comfirm_text),
											getString(R.string.cx_fa_confirm_text),getString(R.string.cx_fa_cancel_button_text)).show();
								}
							});

						} else { // 对方说的就要回复
							final String hintReplyName2=hintReplyName;
							replyView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									mReplyFeedId = tempFeed.getId();
									mReply_to = reply.getAuthor();
									// mCommentLayout.setVisibility(View.VISIBLE);
									mInputPanel.setVisibility(View.VISIBLE);
									mSendReplyLayer.setVisibility(View.VISIBLE);
									input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
									mInputText.setFocusable(true);
									mInputText.requestFocus();
									mInputText.requestFocusFromTouch();
									mInputText.setSelection(0);
									mInputText.setCursorVisible(true);
									mInputText.setHint(hintReplyName2);
									mVoicePlayFlag = false;

								}
							});

						}

						
						
						
						commentAndReplayOfRecord.addView(replyView);

					} // end for(k)

				}
			} else { // 异常情况，帖子没有文字和图片
				speakWord.setVisibility(View.GONE);
				photosLayout.setVisibility(View.GONE);
			}

			// 评论按钮
			ImageButton commentOrReplayBtn = holder.commentBtn;
			
			String strName="";
			if(!TextUtils.isEmpty(hintCommentName)){
				strName="评论："+hintCommentName;
			}
			
//			final String hintCommentName2=strName;
			final String hintCommentName2="评论:";
			
			commentOrReplayBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mReplyFeedId = tempFeed.getId();
					mReply_to = null;
					// mCommentLayout.setVisibility(View.VISIBLE);
					mInputPanel.setVisibility(View.VISIBLE);
					mSendReplyLayer.setVisibility(View.VISIBLE);
					input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

					mInputText.setFocusable(true);
					mInputText.requestFocus();
					mInputText.requestFocusFromTouch();
					mInputText.setSelection(0);
					mInputText.setCursorVisible(true);
					mInputText.setHint(hintCommentName2);
					mVoicePlayFlag=false;
					// mCommentInputEditer.requestFocus();
				}
			});

			// 日期
			int len = dayStr.length() + monthStr.length();
			TextView speakDate = holder.speakDate;
			SpannableString tempSpanStr = new SpannableString(dayStr + monthStr);
			tempSpanStr.setSpan(new RelativeSizeSpan(3.0f), 0, dayStr.length(),
					Spanned.SPAN_INCLUSIVE_INCLUSIVE); // 设置日的字号
			tempSpanStr.setSpan(
					new ForegroundColorSpan(Color.rgb(235, 161, 121)), 0,
					dayStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE); // 设置数字日的颜色

			tempSpanStr.setSpan(
					new ForegroundColorSpan(Color.rgb(156, 156, 163)), 2,
					len - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE); // 设置数字月的颜色

			tempSpanStr.setSpan(new RelativeSizeSpan(1.1f), 2, len - 1,
					Spanned.SPAN_INCLUSIVE_INCLUSIVE); // 设置数字月的字号(android中没有与ios对应的字体，将数字月与"月"字设为不同字号模拟一下ios的效果)
			// tempSpanStr.setSpan(new TypefaceSpan("serif"), 2, len-1,
			// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置数字月的字体
			tempSpanStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
					0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 全部设为设置粗体

			if (1 == position) { // 第一行无条件显示
				speakDate.setVisibility(View.VISIBLE);
				speakDate.setText(tempSpanStr);
			} else { // 非第一行
						//
						// 判断跟上一天是否是同一天
				if((position - 2)>=0 && (position - 2)<mAdapterData.size()){
					InvitationData preFeed = mAdapterData.get(position - 2);
					// Log.e("RkUsersPairZone", feedContent.getText()+"  time1:" +
					// preFeed.getCreate() + "  time2:" + tempFeed.getCreate() );
					boolean flag = DateUtil.isTheSameDay(
							Long.valueOf(preFeed.getCreate()) * 1000,
							Long.valueOf(tempFeed.getCreate()) * 1000);
	
					if (flag) {
						speakDate.setVisibility(View.GONE);
					} else {
						speakDate.setVisibility(View.VISIBLE);
						speakDate.setText(tempSpanStr);
					}
				}
			}
			//
			return convertView;
		}

	}

	// item的adapter的holder
	static class NbItemViewHolder {
		public TextView speakDate; // 日期
		public CxImageView girlHead; // 女生头像
		public CxImageView boyHead; // 男生头像
		public TextView familyName; // 家庭名称
		public TextView speakerName; // 昵称
		public TextView speakTime; // 时间
		public CustomTextView speakWord; // 文字内容
		public List<CxImageView> sharedPhotos; // 图片内容
		public ImageButton commentBtn; // 评论
		public ImageButton deleteBtn; // 删除本帖子
		public LinearLayout commentAndReplayOfRecord;
		public Button addNbNow;// 评论和回复的内容
		public TextView textMore;
		public LinearLayout headLayout;
		
		public LinearLayout messageLayout;
		public TextView msgFamilyName;
		public CxImageView msgGirlHead; // 女生头像
		public CxImageView msgBoyHead; // 男生头像
		public LinearLayout msgSpeakLayout; // 
		public TextView msgSpeakerName;
		public TextView msgSpeakTime;
	}

	// 评论帖子的回调
	JSONCaller mSendCommentCallback = new JSONCaller() {

		@Override
		public int call(Object result) {
			// RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback start");
//			new Handler(getMainLooper()) {
//				public void handleMessage(android.os.Message msg) {
//					mInputPanel.setVisibility(View.GONE);
//					
//				}
//			}.sendEmptyMessage(1);
//			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			
			
			if (null == result) {
				// 提示评论失败
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxNbReply replyResult = null;
			try {
				replyResult = (CxNbReply) result;
			} catch (Exception e) {
			}
			if (null == replyResult || replyResult.getRc()==408) {
				// 提示评论失败
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}

			int rc = -1;
			try {
				rc = replyResult.getRc();
			} catch (Exception e) {
			}
			if (-1 == rc) {
				// 提示评论失败
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -3;
			}

			if (0 != rc) {
				if(TextUtils.isEmpty(replyResult.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(replyResult.getMsg(),0);
				}
				return rc;
			}
			InvitationReply reply = replyResult.getData();
			if (null == reply) {
				return 0;
			}
			// 在帖子列表中插入数据，更新列表
			// FeedListData
			if ((null == mFeedsData) || (mFeedsData.size() < 1)) { // 属于异常(没有帖子数据就没有回复的动作）
				return -1;
			}
			int tempSize = mFeedsData.size();
			for (int i = 0; i < tempSize; i++) {
				InvitationData tempFeed = mFeedsData.get(i);

				if (TextUtils.equals(reply.getFeed_id(), tempFeed.getId())) { // 是这个帖子的回复
					InvitationPost tempPost = tempFeed.getPost();
					if (null == tempPost) { // 容错处理：帖子内存不存在 （严格来讲，这样的情况属于异常）
						tempPost = new InvitationPost();
						ArrayList<InvitationReply> targetReply = new ArrayList<InvitationReply>();
						targetReply.add(reply);
						
						CxNeighbourParam.getInstance().setNbAddReply(reply);
						// tempPost.setReplays(targetReply);
						mFeedsData.get(i).getPost().setReplays(targetReply);
					} else { // 正常情况
						ArrayList<InvitationReply> targetReply = new ArrayList<InvitationReply>();
						targetReply.add(reply);
						if (null != tempPost.getReplays()) {
							targetReply.addAll(tempPost.getReplays());
						}
						mFeedsData.get(i).getPost().setReplays(targetReply);
						CxNeighbourParam.getInstance().setNbAddReply(reply);
					}
					// RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback end");
					updateListview.sendEmptyMessage(1);
					break;
				}

			} // end for(i)

			return 0;
		}
	};

	// 修改备注名的回调
	JSONCaller changeNameCaller = new JSONCaller() {

		@Override
		public int call(Object result) {

			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			
			if (null == result) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			JSONObject changeObj = null;
			try {
				changeObj = (JSONObject) result;
			} catch (Exception e) {
			}
			if (null == changeObj) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = -1;
			try {
				rc = changeObj.getInt("rc");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			String text=getString(R.string.cx_fa_net_response_code_fail);
			
			if (0 != rc) {
				// 提示服务端返回的失败原因
				try {
					if(rc==408){
						showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
						
						return 408;
					}
					
					if(!changeObj.isNull("msg") && !TextUtils.isEmpty(changeObj.getString("msg"))){
						text=changeObj.getString("msg");
					}
					
					showResponseToast(text,0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return rc;
			}
			JSONObject data = null;
			try {
				data = changeObj.getJSONObject("data");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (data == null) {
				showResponseToast(text,0);
				return -9;
			}
			String girlName = null;
			String boyName = null;
			try {
				girlName = data.getString("remark0");
				boyName = data.getString("remark1");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (girlName == null || boyName == null) {
				showResponseToast(text,0);
				return -10;
			}
			
			wifeName = girlName;
			husbandName = boyName;
			
			new Handler(CxNbNeighboursHome.this.getMainLooper()) {
				public void handleMessage(Message msg) {
//					mTitleText.setText(wifeName + "和" + husbandName);
					ToastUtil.getSimpleToast(CxNbNeighboursHome.this, R.drawable.chatbg_update_success,CxNbNeighboursHome.this.getResources()
							.getString(R.string.cx_fa_neighbour_neighbours_home_changename_success), 1).show();
				};
			}.sendEmptyMessage(0);
			CxNeighbourApi.getInstance().requestHomeInvitationList(neighbourId,0,15,new InvitationResponse(true, false),
					CxNbNeighboursHome.this,neighbourId);
			
//			updateListview.sendEmptyMessage(1);
		
			
			InvitationUserInfo info=new InvitationUserInfo();
			info.setHusbandName(boyName);
			info.setWifeName(girlName);
			CxNeighbourParam.getInstance().setNbChangeName(info);
			
			// mDeleteReply mFeedIndex
			return 0;
		}
	};

	// 删除回复的回调
	JSONCaller deleteReply = new JSONCaller() {

		@Override
		public int call(Object result) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			if (null == result) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic deleteResult = null;
			try {
				deleteResult = (CxParseBasic) result;
			} catch (Exception e) {
			}
			if (null == deleteResult || deleteResult.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}

			int rc = deleteResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(deleteResult.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(deleteResult.getMsg(),0);
				}
				return rc;
			}
			// 删除成功
			if (mFeedIndex >= mFeedsData.size()) {
				// 提示删除失败
				return -9;
			}
			try {
				InvitationData tempFeed = mFeedsData.get(mFeedIndex);
				InvitationPost feedPost = tempFeed.getPost();
				List<InvitationReply> tempReplies = feedPost.getReplays();
				
				InvitationReply reply=tempReplies.get(mReplyIndex);
				CxNeighbourParam.getInstance().setNbDelReply(reply);

				tempReplies.remove(mReplyIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			updateListview.sendEmptyMessage(1);
			// mDeleteReply mFeedIndex
			return 0;
		}
	};

	
	// 删帖子的回调
	class DeleteFeedBack implements JSONCaller {
		private int mLocation;

		public DeleteFeedBack(int location) {
			this.mLocation = location;
		}

		@Override
		public int call(Object result) {
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);	
			
			if (null == result) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic deleteResult = null;
			try {
				deleteResult = (CxParseBasic) result;
			} catch (Exception e) {
			}
			if (null == deleteResult || deleteResult.getRc()==408) {
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = deleteResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(deleteResult.getMsg())){
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(deleteResult.getMsg(),0);
				}
				return rc;
			}
			// 以下是删除成功
			InvitationData data=mFeedsData.get(mLocation);
			CxNeighbourParam.getInstance().setNbDelInvitation(data);	
			mFeedsData.remove(mLocation);
			updateListview.sendEmptyMessage(1);

			return 0;
		}
	}


	OnClickListener titleBtnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				finish();
				CxNbNeighboursHome.this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				break;
			case R.id.cx_fa_activity_title_more:
				Intent intent=new Intent(CxNbNeighboursHome.this, CxNeighbourAddMessage.class);
				intent.putExtra(CxNeighbourParam.NB_ADD_MESSAGE_GROUP_ID, neighbourId);
				CxNbNeighboursHome.this.startActivity(intent);
				overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			default:
				break;
			}

		}
	};

	
	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void showResponseToast(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(CxNbNeighboursHome.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxNbNeighboursHome.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}


	private CxInputPanel mInputPanel;
	private ScrollableListView mNbContent;
	private LinearLayout mSendReplyLayer;
	private NeighbourAdapter mAdapter;
	private EditText mInputText;
	private ImageView mRecordImageView;
	private RelativeLayout mChatRecordRelativeLayout;
	private TextView mRecordRemainTimeTextView;
	private TextView mRecordCancelTip;
	private RelativeLayout mNbRecordLayout;
	private LinearLayout mRecordView;
	private RecMicToMp3 mRecMicToMp3;
	private long mRecorderStartTime;
	private String mSoundFilePath;

	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// if (Activity.RESULT_OK != resultCode) {
	// return;
	// }
	// if (ADD_INVITATION == requestCode) { // 二人空间调用返回图片
	// //
	// String imagePath = data
	// .getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI);
	// if (null == imagePath) {
	// return;
	// }
	// Intent addInvitation = new Intent(RkNbNeighboursHome.this,
	// RkNeighbourAddInvitation.class);
	// addInvitation.putExtra(RkGlobalConst.S_NEIGHBOUR_SHARED_TYPE, 1);
	// addInvitation.putExtra(RkGlobalConst.S_NEIGHBOUR_SHARED_IMAGE,
	// imagePath);
	// startActivity(addInvitation);
	// return;
	// }
	//
	// };

	class CurrentObserver extends CxObserverInterface {

		/* 主要事项有：1、新的分享 2、头像（中、小） */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}

			// if (actionTag.equalsIgnoreCase(RkServiceParams.NEIGHBOUR_TS)) {
			// // 对方发帖子成功时long
			// // polling告诉更新
			// if (isFirstComplete && isDownComplete) {
			// isDownComplete = false;
			// RkZoneApi.getInstance().requestFeedList(0, 15,
			// new InvitationResponse(true, false),
			// RkNbNeighboursHome.this);
			// }
			//
			// return;
			// }

			// if (RkGlobalParams.ICON_SMALL.equalsIgnoreCase(actionTag)
			// //自己的头像修改，要刷新列表
			// || (RkGlobalParams.PARTNER_ICON_BIG.equalsIgnoreCase(actionTag))
			// //对方的头像修改，要刷新列表
			// || (RkGlobalParams.ZONE_BACKGROUND.equalsIgnoreCase(actionTag)) )
			// { //空间背景修改
			// if (null != mZoneAdapter) {
			// mZoneAdapter.notifyDataSetChanged();
			// }
			// return;
			// }
			//
			// if (RkGlobalParams.PARTNER_NAME.equalsIgnoreCase(actionTag)) {
			// if (null != mZoneAdapter) {
			// mZoneAdapter.notifyDataSetChanged();
			// }
			// return;
			// }

			// if (RkNeighbourParam.NEIGHBOUR_DATA.equalsIgnoreCase(actionTag))
			// { // 自己发帖子成功
			// RkLog.i("999999", " has receive notify for new feed");
			// if (null == mFeedsData) {
			// mFeedsData = new ArrayList<InvitationData>();
			// mFeedsData.add(RkNeighbourParam.getInstance()
			// .getInvitationData());
			// mAdapter.updataView(mFeedsData);
			// RkLog.i("source data is null",
			// " has receive notify for new feed");
			// return;
			// } else {
			// List<InvitationData> targetData = new
			// ArrayList<InvitationData>();
			// targetData.add(RkNeighbourParam.getInstance()
			// .getInvitationData());
			// targetData.addAll(mFeedsData);
			// mFeedsData = targetData;
			// mAdapter.updataView(mFeedsData);
			// RkLog.i("source data is not null",
			// " has receive notify for new feed");
			// }
			// return;
			// }
		}
	}

	/*************************************** 语音回复 *******************************************************/

	private Timer mRecordTimer;
	private TimerTask mRecordTask;
	private static int mReocrdCount = 0;
	private long mRecorderStopTime;
	private Timer mTimer;
	private TimerTask mTask = null;

	// private ImageView mRecordImage;
	// private ProgressBar mRecordProgress;

	private boolean mVoicePlayFlag = false; // play voice flag

	private AnimationDrawable mVoiceAd;

	private void startRecord() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			DialogUtil du = DialogUtil.getInstance();
			du.setOnSureClickListener(new OnSureClickListener() {
				@Override
				public void surePress() {
					mRecordView.setVisibility(View.GONE);
					mInputPanel.setBackgroundResource(R.drawable.chatview_voice);
				}
			});
			du.getSimpleDialog(CxNbNeighboursHome.this, null,
					getResources().getString(R.string.cx_fa_confirm_text),
					null, null).show();
			return;
		}

		try {
			File storageDirectory = new File(
					CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH,
					CxGlobalConst.S_CHUXIN_AUDIO_CACHE_NAME);
			boolean cacheable = CxBaseDiskCache
					.createDirectory(storageDirectory);
			if (!cacheable) {
				try {
					throw new Exception("the sd card is not useable");
				} catch (Exception e) {
					CxLog.e("startRecord", "" + e.getMessage());
				}
			}

			String fileName = CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH
					+ File.separator + CxGlobalConst.S_CHUXIN_AUDIO_CACHE_NAME
					+ File.separator + System.currentTimeMillis() + ".mp3";
			mRecMicToMp3 = new RecMicToMp3(fileName, 8000);
			mRecMicToMp3.start();
			mRecorderStartTime = System.currentTimeMillis();
			mSoundFilePath = fileName;
		} catch (IllegalStateException e) {
			CxLog.e("startRecord", "" + e.getMessage());
		} catch (Exception e) {
			CxLog.e("startRecord", "" + e.getMessage());
		}
		mRecordRemainTimeTextView
				.setText(String
						.format(getResources().getString(
								R.string.cx_fa_chat_record_time_remianing),
								(90 - (System.currentTimeMillis() - mRecorderStartTime) / 1000)));
		// int vuSize = MAX_VU_SIZE * mRecMicToMp3.getVolume() / 100;
		// // RkLog.d(TAG, "vuSize>>>" + vuSize);
		// int resId = getResources().getIdentifier(sRecordingDrawables[vuSize],
		// "drawable",
		// getActivity().getPackageName());
		// mRecordImageView.setImageResource(resId);
	}

	private void startTimer() {
		if (null == mRecordTimer) {
			mRecordTimer = new Timer();
		}
		if (null == mRecordTask) {
			mRecordTask = new TimerTask() {

				@Override
				public void run() {
					android.os.Message message = android.os.Message.obtain(
							mNbHandler, UPDATE_RECORD_TIME);
					message.sendToTarget();
					if (mReocrdCount >= 90) {
						stopTimer();
					}
					mReocrdCount++;
				}
			};
		}
		mRecordTimer.schedule(mRecordTask, 0, 1 * 1000);
	}

	private void stopTimer() {
		if (null != mRecordTimer) {
			mRecordTimer.cancel();
			mRecordTimer = null;
		}
		if (null != mRecordTask) {
			mRecordTask.cancel();
			mRecordTask = null;
		}
		mReocrdCount = 0;
	}

	private void deleteFile2(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	// 录音滑动事件
	private void moveRecordEvent(MotionEvent m) {
		int layerLocation[] = new int[2];
		mVoicePanel.getLocationInWindow(layerLocation);
		int layerX = layerLocation[0];
		int layerY = layerLocation[1];

		// RkLog.i("onAcionMoveEvent", "layerX:layerY>>>" + layerX + " : " +
		// layerY);

		float x = m.getX();
		float y = m.getY();
		// RkLog.i("onAcionMoveEvent", "X:Y>>>" + x + " : " + y);

		mInputPanel.setBackgroundResource(R.drawable.chatview_voice_h);
		if (y < 0) {
			mChatRecordRelativeLayout
					.setBackgroundResource(R.drawable.pub_cancel);
			mRecordImageView.setImageResource(R.drawable.pub_cancel);
			if (mRecordRemainTimeTextView.getVisibility() == View.VISIBLE) {
				mRecordRemainTimeTextView.setVisibility(View.INVISIBLE);
			}
			mRecordCancelTip.setText(R.string.cx_fa_chat_record_cancle_tip);
		} else {
			android.os.Message message = android.os.Message.obtain(mNbHandler,
					UPDATE_RECORD_TIME);
			// message.sendToTarget();
		}
	}

	// 录音停止事件
	private void stopRecordEvent(MotionEvent m) {
		int layerLocation[] = new int[2];
		mVoicePanel.getLocationInWindow(layerLocation);
		int layerX = layerLocation[0];
		int layerY = layerLocation[1];

		float x = m.getX();
		float y = m.getY();
		mRecordView.setVisibility(View.GONE);
		mInputPanel.setBackgroundResource(R.drawable.chatview_voice);
		stopRecord();
		int mDeviceWidth = CxNbNeighboursHome.this.getWindowManager()
				.getDefaultDisplay().getWidth();
		if (y > 0 || x < 0 || x >= mDeviceWidth) {
			android.os.Message msg = android.os.Message.obtain(mNbHandler,
					SEND_RECORD);
			msg.sendToTarget();
		} else {
			// delete audio file
			deleteFile2(mSoundFilePath);
		}
		stopTimer();
		mRecordStart = false;
		// ((RkMain)getActivity()).inavaileChildSlide();
		CxGlobalParams.getInstance().setRecorderFlag(false);
	}

	private void stopRecord() {
		if (null != mRecMicToMp3) {
			mRecMicToMp3.stop();
			mRecorderStopTime = System.currentTimeMillis();
		}
	}

	// 播发录音。
	public void playVoice(String path, int msgVoiceLen, ImageView image) {
		// RkLog.v(TAG, "voice uri=" + path);
		if (RkSoundEffects.cFmodGetIsPlay()) {
			// RkSoundEffects.cFmodStop();
			// stopRecordAnimation();
			stopVoice(image);
		}
		RkSoundEffects.soundPlay(path, 0);
		startTimer(msgVoiceLen, image);
		startRecordAnimation(image);
		// ChatFragment.getInstance().mPlayRecordEntry = RecordEntry.this;
	}

	public void stopVoice(ImageView image) {
		CxLog.d("playVoice", "isplay1=" + RkSoundEffects.cFmodGetIsPlay());
		CxLog.d("playVoice", "ispause1=" + RkSoundEffects.cFmodGetIsPause());
		if (RkSoundEffects.cFmodGetIsPlay() || RkSoundEffects.cFmodGetIsPause()) {
			RkSoundEffects.cFmodStop();
		}
		if (null != mTimer) {
			mTimer.cancel();
			mTimer = null;
		}
		if (null != mTask) {
			mTask.cancel();
			mTask = null;
		}
		stopRecordAnimation(image);
	}

	private void startTimer(int msgVoiceLen, final ImageView image) {
		if (null == mTimer) {
			mTimer = new Timer();
		}
		if (null == mTask) {
			mTask = new TimerTask() {

				@Override
				public void run() {
					// android.os.Message message =
					// recordHandler.obtainMessage(0);
					// message.sendToTarget();
					android.os.Message msg = android.os.Message.obtain(
							mNbHandler, STOP_READ_RECORD);
					msg.obj = image;
					msg.sendToTarget();
				}
			};
		}
		mTimer.schedule(mTask, (msgVoiceLen + 1) * 1000);
	}

	private void startRecordAnimation(ImageView image) {

		image.setImageResource(R.anim.cx_fa_anim_chat_voice_for_partner);

		mVoiceAd = (AnimationDrawable) image.getDrawable();
		mVoiceAd.start();
		mVoicePlayFlag = true;
	}

	private void stopRecordAnimation(ImageView iamge) {
		if (null != mVoiceAd && mVoiceAd.isRunning()) {
			mVoiceAd.stop();
			mVoicePlayFlag = false;
			iamge.setImageResource(R.drawable.chat_voice3);
		}
	}

	private int mRetryCount = 3; // 重试3次下载

	private TextView mTitleText;

	public String getAudioFile(final String url, final int audioLength,
			final ImageView image, final ProgressBar progress) {
		if ((null == url) || (url.equals("null"))) { // 避免服务器返回"null"
			// RkLog.i(TAG, " param url is null");
			return "";
		}

		final CxAudioFileResourceManager resourceManager = CxAudioFileResourceManager
				.getAudioFileResourceManager(CxNbNeighboursHome.this);

		if (resourceManager.exists(Uri.parse(url))) {
			// RkLog.i(TAG, "file path local=");
			final File file = resourceManager.getFile(Uri.parse(url));
			CxLog.i("getAudioFile", "filepath0=" + file.getAbsolutePath());
			new Handler(CxNbNeighboursHome.this.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					preparePlayVoice(file.getAbsolutePath(), audioLength, image);
				}
			}.sendEmptyMessage(1);
			return file.getAbsolutePath();
		} else {
			// checkSdCardExist();
			new Handler(CxNbNeighboursHome.this.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					image.setVisibility(View.GONE);
					progress.setVisibility(View.VISIBLE);
				}
			}.sendEmptyMessage(1);

			// RkLog.i(TAG, "ready to net download");
			resourceManager.addObserver(new CxAudioFileResourceManager.ResourceRequestObserver(
							Uri.parse(url)) {
				@Override
				public void requestReceived(Observable observable,
						Uri uri, long len) {
					observable.deleteObserver(this);
					try {
						mRetryCount--;
						File file = resourceManager.getFile(uri);
						CxLog.i("getAudioFile", "contentlength>>>"
								+ len);
						CxLog.i("getAudioFile",
								"file.length>>>" + file.length());
						CxLog.i("getAudioFile", "audiolength>>>"
								+ audioLength);

						if (file.length() != len) {
							CxLog.i("getAudioFile",
									"not download file complete, need to reload");
							file.delete();
							// 重试3次，失败了给出提示
							if (mRetryCount == 0) {
								getAudioFile(url, audioLength, image,
										progress);
							} else {
								new Handler(CxNbNeighboursHome.this
										.getMainLooper()) {
									public void handleMessage(
											android.os.Message msg) {
										progress.setVisibility(View.GONE);
										image.setVisibility(View.VISIBLE);
									}
								}.sendEmptyMessage(1);

								ToastUtil
										.getSimpleToast(
												CxNbNeighboursHome.this,
												-1,
												getString(R.string.cx_fa_not_download_file_complete),
												1).show();

							}
						} else {
							CxLog.i("getAudioFile",
									"filepath1="
											+ file.getAbsolutePath());
							final String audioFilePath = file
									.getAbsolutePath();
							CxLog.i("getAudioFile", "audioFilePath="
									+ audioFilePath);
							new Handler(CxNbNeighboursHome.this
									.getMainLooper()) {
								public void handleMessage(
										android.os.Message msg) {
									progress.setVisibility(View.GONE);
									image.setVisibility(View.VISIBLE);
									preparePlayVoice(audioFilePath,
											audioLength, image);
								}
							}.sendEmptyMessage(1);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			resourceManager.request(Uri.parse(url));
			return "";
		}
	}

	private ImageView oldImage;
	private void preparePlayVoice(final String msgVoiceUrl,
			final int msgVoiceLen, ImageView image) {
		if (!mVoicePlayFlag) {
			mVoicePlayFlag = true;
			playVoice(msgVoiceUrl, msgVoiceLen, image);	
			oldImage=image;
		}else{
			if(image.equals(oldImage)){
				if (RkSoundEffects.cFmodGetIsPlay()) {
					stopVoice(oldImage);
				}
				mVoicePlayFlag=false;
			}else{
				if (RkSoundEffects.cFmodGetIsPlay()) {
					stopVoice(oldImage);
				}
				mVoicePlayFlag=false;
				preparePlayVoice(msgVoiceUrl,msgVoiceLen,image);
			}
		}
	}
	
	
	
	
	   @Override
	    public void onResume() {
	        super.onResume();
	        if(null != mSensorManager){
	            mSensorManager.registerListener(sensorListener, mProximiny,  
	                    SensorManager.SENSOR_DELAY_NORMAL);
	        }
	        
	    }
	   
	   @Override
	    public void onPause() {
	        super.onPause();
	        if( null != mSensorManager){
	            mSensorManager.unregisterListener(sensorListener);
	        }	        
	    }
		
	   SensorEventListener sensorListener=new SensorEventListener() {
		

		    @Override
		    public void onSensorChanged(SensorEvent event) {
		        mFproximiny = event.values[0];  
		        CxLog.i("tag",  
		                "-->  " + mFproximiny + "  |  " + mProximiny.getMaximumRange());  
		  
		        if (mFproximiny == mProximiny.getMaximumRange() && !CxGlobalParams.getInstance().isChatEarphone()) {  
		            mAudioManager.setMode(AudioManager.MODE_NORMAL);  
		        } else {  
		            mAudioManager.setMode(AudioManager.MODE_IN_CALL);  
		        }  
		    }
		
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
		};
	
		public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
			if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				return false;
			}
			return super.onKeyDown(keyCode, event);
		};


}
