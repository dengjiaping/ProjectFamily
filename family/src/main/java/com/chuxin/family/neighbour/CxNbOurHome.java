package com.chuxin.family.neighbour;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import org.fmod.effects.RkSoundEffects;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
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

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxNeighbourParser;
import com.chuxin.family.parse.been.CxNbReply;
import com.chuxin.family.parse.been.CxNeighbourInvitationList;
import com.chuxin.family.parse.been.CxNeighbourSendInvitation;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.parse.been.data.InvitationData;
import com.chuxin.family.parse.been.data.InvitationList;
import com.chuxin.family.parse.been.data.InvitationPhoto;
import com.chuxin.family.parse.been.data.InvitationPost;
import com.chuxin.family.parse.been.data.InvitationReply;
import com.chuxin.family.parse.been.data.InvitationUserInfo;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxAudioFileResourceManager;
import com.chuxin.family.utils.CxBaseDiskCache;
import com.chuxin.family.utils.CxLoadingUtil;
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

public class CxNbOurHome extends CxRootActivity {

	protected static final int ADD_INVITATION = 0;

	private static final int UPDATE_RECORD_TIME = 1;

	private static final int SEND_RECORD = 2;

	public static final int READ_RECORD = 3;
	public static final int STOP_READ_RECORD = 4;

	protected static final int MAX_VU_SIZE = 14; // set phone volume level 14

	protected static final int MODIFY_NB_BG_REQUEST = 5;

	protected static final int MODIFY_GIRL_HEAD_REQUEST = 6;

	protected static final int MODIFY_BOY_HEAD_REQUEST = 7;

	private InputMethodManager input;

	private List<InvitationData> mHomeFeedsData; // 帖子的数据

	private int mReplyIndex; // 需要删除的回复
	private int mFeedIndex; // 需要删除的回复所在的帖子在数据源中的位置
	private String mReplyFeedId; // 评论的帖子ID
	private String mReply_to; // 回复给的对象（评论时为null,回复时为对方的ID）
	public static boolean mRecordStart;

	private boolean isFirstComplete = false;;
	private boolean isDownComplete = true; // 向下拉完成的标识位。默认为true表示完成
	private boolean isUpComplete = true; // 向上推完成的标识位。默认为true表示完成

	private CurrentObserver mAddFeedObserver;

	private Handler mNbHandler;
	
	
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
		input = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

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
							CxNbOurHome.this.getPackageName());
					mRecordImageView.setImageResource(resId);
					if (mRecordRemainTimeTextView.getVisibility() != View.VISIBLE) {
						mRecordRemainTimeTextView.setVisibility(View.VISIBLE);
					}
					mRecordRemainTimeTextView.setText(String.format(
							getResources().getString(
									R.string.cx_fa_chat_record_time_remianing),
							(91 - mReocrdCount)));
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
							DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
							CxNeighbourApi.getInstance().requestReply(mReplyFeedId, "audio", null,
									mSoundFilePath, audioLength, mReply_to,
									null, mSendCommentCallback);
							mInputPanel.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						ToastUtil.getSimpleToast(CxNbOurHome.this,-1,getString(R.string.cx_fa_chat_record_time_short_msg),
										1).show();
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
		wifeUrl = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL);
		husbandUrl = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL);
		pairId = intent.getStringExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID);

		init();

	}

	public void init() {

		Button mTitleBack = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mTitleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
		Button mTitleShare = (Button) findViewById(R.id.cx_fa_activity_title_more);

		mTitleShare.setVisibility(View.VISIBLE);
		mTitleShare.setBackgroundResource(R.drawable.cx_fa_zone_shareimage_btn);
		mTitleShare.setOnClickListener(titleClick);
		mTitleShare.setOnLongClickListener(titleLongClick);
		mTitleBack.setOnClickListener(titleClick);
		mTitleBack.setText(getString(R.string.cx_fa_navi_back));
		mTitleText.setText(getString(R.string.cx_fa_neighbour_family_name));

		mInputPanel = (CxInputPanel) findViewById(R.id.cx_fa_widget_input_layer);
		mVoicePanel = (LinearLayout)mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout2);
		CxInputPanel.sInputPanelUse = CxNeighbourFragment.RK_CURRENT_VIEW;
		ImageButton mPlusButton1 = (ImageButton) mInputPanel
				.findViewById(R.id.cx_fa_widget_input_panel__layout1_button1);
		mPlusButton1.setVisibility(View.GONE);

		ImageButton mPlusButton2 = (ImageButton) mInputPanel
				.findViewById(R.id.cx_fa_widget_input_panel__layout2_button3);
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

		// 设置观察者
		// RkServiceParams mNbParam = RkServiceParams.getInstance(); //
		// 获取model的subject实例
		// mServiceObserver = new CurrentObserver(); // 生成观察者实例
		// // 设置观察目标
		// List<String> tags = new ArrayList<String>();
		// tags.add(RkServiceParams.NEIGHBOUR_TS); // 新的分享资料
		// mServiceObserver.setListenTag(tags); // 设置观察目标
		// mServiceObserver.setMainThread(true); // 设置在UI线程执行
		// mNbParam.registerObserver(mServiceObserver); // 注册观察者


		// 自己发帖子成功的监听
		mAddFeedObserver = new CurrentObserver();
		List<String> feedTags = new ArrayList<String>();
		feedTags.add(CxNeighbourParam.NEIGHBOUR_DATA);
		mAddFeedObserver.setListenTag(feedTags);
		mAddFeedObserver.setMainThread(true);
		CxNeighbourParam.getInstance().registerObserver(mAddFeedObserver);

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
					CxNeighbourApi.getInstance().requestHomeInvitationList(
							null, 0, 15, new InvitationResponse(true, false),
							CxNbOurHome.this,pairId);
				} else {
					new Handler(CxNbOurHome.this.getMainLooper()) {
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
					int offest = (null == mHomeFeedsData ? 0 : mHomeFeedsData.size());
					CxNeighbourApi.getInstance().requestHomeInvitationList(
							null, offest, 15,
							new InvitationResponse(false, false),
							CxNbOurHome.this,pairId);
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
						ToastUtil.getSimpleToast(CxNbOurHome.this, -1,
								getString(R.string.cx_fa_zone_no_content), 1)
								.show();
						return 0;
					}
					try {
						DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
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
				mChatRecordRelativeLayout.setBackgroundResource(R.drawable.pub_recorder);
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
		CxNbCacheData cacheData = new CxNbCacheData(CxNbOurHome.this);
		InvitationList list = cacheData.queryCacheData(pairId);
		if( null != list){
			InvitationUserInfo userInfo = list.getUserInfo();
			if(userInfo!=null){
				bgUrl=userInfo.getBgUrl();
			}
			ArrayList<InvitationData> feeds = list.getDatas();
			if ((null != feeds) && (feeds.size() > 0)) {
				 mAdapter.updataView(feeds);
			}
		}

		// 加载本地数据后首先到网络获取第一屏数据
		isFirstComplete = false;
		CxNeighbourApi.getInstance().requestHomeInvitationList(null, 0, 15,
				new InvitationResponse(true, true), CxNbOurHome.this,pairId);
		
		
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
	
	
	@Override
	protected void onDestroy() {
		if(mAddFeedObserver!=null){
			CxNeighbourParam.getInstance().unRegisterObsercer(mAddFeedObserver);
		}
		
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxNbOurHome.this, "neighbour_bg", CxNbOurHome.this);
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//			
//			CxResourceManager resourceManager2 = CxResourceManager.getInstance(
//					CxNbOurHome.this, "head", CxNbOurHome.this);
//			if (null != resourceManager2) {
//				resourceManager2.clearMemory();
//				resourceManager2 = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		super.onDestroy();
	}

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

			new Handler(CxNbOurHome.this.getMainLooper()) {
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
					// 此处husbandName 和wifeName没用
					bgUrl = userInfo.getBgUrl();
					husbandName = userInfo.getHusbandName();
					husbandUrl = userInfo.getHusbandUrl();
					wifeName = userInfo.getWifeName();
					wifeUrl = userInfo.getWifeUrl();
				}

				if (null != mHomeFeedsData) {
					mHomeFeedsData.clear();
					mHomeFeedsData = null;
				}

				mHomeFeedsData = feedList.getData().getDatas();
				updateListview.sendEmptyMessage(1);
				return 0;
			}

			/*
			 * if (null != mFeedsData) { mFeedsData.clear(); mFeedsData = null;
			 * }
			 */

			if (isPushDown) { // 往下拉(或者push)

				mHomeFeedsData = feedList.getData().getDatas(); // 直接换成最新数据即可

			} else { // 往上翻
				if ((null == feedList.getData().getDatas())
						|| (feedList.getData().getDatas().size() < 1)) {
					// 没有数据
					return 0;
				}
				if (null == mHomeFeedsData) {
					mHomeFeedsData = new ArrayList<InvitationData>();
				}
				mHomeFeedsData.addAll(feedList.getData().getDatas());
			}
			
			InvitationUserInfo userInfo = feedList.getData().getUserInfo();
			if (userInfo != null) {
				// 此处husbandName 和wifeName没用
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
			mAdapter.updataView(mHomeFeedsData);
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CxLog.i("Adapter getView", "" + position);

			if (0 == position) {
				if (null == convertView) {
					convertView = CxNbOurHome.this.getLayoutInflater().inflate(
							R.layout.cx_fa_fragment_neighbour_header, null);
				}
				// 加载二人空间的背景图、对方头像，自己的头像
				CxImageView nbBackground = (CxImageView) convertView
						.findViewById(R.id.cx_fa_neighbour_bg);
				ViewGroup.LayoutParams param = nbBackground.getLayoutParams();
				param.height = (int) (getResources().getDisplayMetrics().widthPixels * 0.66f + 0.5f);
				nbBackground.setLayoutParams(param);
//				if (bgUrl != null  && !bgUrl.equals(""))
//					nbBackground.setImage(bgUrl, false, 260, RkNbOurHome.this,"neighbour_bg", RkNbOurHome.this);
				
				nbBackground.displayImage(imageLoader, bgUrl,R.drawable.neighbor_image_ourhome, false, 0);

				CxImageView girlHead = (CxImageView) convertView
						.findViewById(R.id.nb_girlHeadView);
				CxImageView boyHead = (CxImageView) convertView
						.findViewById(R.id.nb_boyHeadView);
				ImageView changeName = (ImageView) convertView
						.findViewById(R.id.nb_head_changenickname_or_addpost);
				TextView familyText = (TextView) convertView
						.findViewById(R.id.nb_familyText);
				// RelativeLayout addInvitationLayout = (RelativeLayout)
				// convertView
				// .findViewById(R.id.nb_head_addinvitation);
				// ImageButton addImage = (ImageButton) convertView
				// .findViewById(R.id.nb_head_addinvitation_image);
				// addInvitationLayout.setVisibility(View.VISIBLE);
				//
				// addImage.setOnClickListener(imageBtnClick);
				// addImage.setOnLongClickListener(btnLongClick);
				nbBackground.setOnClickListener(imageBtnClick);
				girlHead.setOnClickListener(imageBtnClick);
				boyHead.setOnClickListener(imageBtnClick);

//				girlHead.setImage(wifeUrl, false, 100, RkNbOurHome.this, "head",
//						RkNbOurHome.this);
				//neighbor_icon_wife
				girlHead.displayImage(imageLoader, wifeUrl, 
						R.drawable.neighbor_icon_wife, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
//				boyHead.setImage(husbandUrl, false, 100, RkNbOurHome.this,
//						"head", RkNbOurHome.this);
				boyHead.displayImage(imageLoader, husbandUrl, 
						R.drawable.neighbor_icon_husband, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
				String familyName = CxNbOurHome.this.getResources().getString(R.string.cx_fa_neighbour_family_name);
				familyText.setText(familyName);
				changeName.setVisibility(View.GONE);

				return convertView;
			}

			final int itemLocation = position - 1;
			NbItemViewHolder holder = null;
			if (null == convertView) {
				convertView = CxNbOurHome.this.getLayoutInflater().inflate(
						R.layout.cx_fa_fragment_neighbour_list_item2, null);
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
				holder.familyId=(TextView)convertView.findViewById(R.id.nb_invitation_group_id2);
				
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
			TextView familyId=holder.familyId;
			
			String hintCommentName="";
			
			int flag2 = tempFeed.getFlag();
			if(flag2==0){
				addNbNow.setVisibility(View.GONE);
				familyId.setVisibility(View.GONE);
			}else{
				addNbNow.setVisibility(View.VISIBLE);
				familyId.setVisibility(View.VISIBLE);
			}
			addNbNow.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(CxNbOurHome.this, CxNeighbourList.class);
					startActivity(intent);	
					CxNbOurHome.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);			
				}
			});
			
			String numberFormat = CxNbOurHome.this.getResources().getString(R.string.cx_fa_my_neighbour_number_tip_formatted);
			String groupId=numberFormat.format(numberFormat,CxGlobalParams.getInstance().getGroup_show_id());		
			familyId.setText(TextUtil.getNewSpanStr(groupId, 13, Color.argb(80, 0, 0, 0)));
			
			// 头像、名称和删除按钮
			LinearLayout headLayout = holder.headLayout;
			CxImageView girlHeadView = holder.girlHead;
			CxImageView boyHeadView = holder.boyHead;
			TextView speakerName = holder.speakerName;
			ImageButton deleteBtn = holder.deleteBtn; // 删除按钮
			TextView textMore=holder.textMore;
			
			LinearLayout messageLayout = holder.messageLayout;
			TextView msgFamilyName = holder.msgFamilyName;
			CxImageView msgGirlHead = holder.msgGirlHead;
			CxImageView msgBoyHead = holder.msgBoyHead;
			LinearLayout msgSpeakLayout = holder.msgSpeakLayout;
			TextView msgSpeakerName = holder.msgSpeakerName;
			TextView msgSpeakTime = holder.msgSpeakTime;
			
			
			
			deleteBtn.setVisibility(View.GONE);
			

			String type=tempFeed.getType();
			if("post".equals(type)){
				messageLayout.setVisibility(View.GONE);
				headLayout.setVisibility(View.VISIBLE);
				msgSpeakLayout.setVisibility(View.GONE);
			}else if("message".equals(type)){
				messageLayout.setVisibility(View.VISIBLE);
				headLayout.setVisibility(View.GONE);
				msgSpeakLayout.setVisibility(View.VISIBLE);
			}
			String version = getString(CxResourceString.getInstance().str_pair);
			
			
			if("post".equals(type)){
				
//				girlHeadView.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//				girlHeadView.setImage(wifeUrl, false, 74, RkNbOurHome.this, "head",
//						RkNbOurHome.this);
				girlHeadView.displayImage(imageLoader, wifeUrl, 
						R.drawable.cx_fa_wf_icon_small, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
//				boyHeadView.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//				boyHeadView.setImage(husbandUrl, false, 74, RkNbOurHome.this,
//						"head", RkNbOurHome.this);
				boyHeadView.displayImage(imageLoader, husbandUrl, 
						R.drawable.cx_fa_hb_icon_small, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
				
			}else if("message".equals(type)){
				
				if("老公".equals(version)){
//					msgGirlHead.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
					CxLog.i("RkNbOurHome_men", (tempFeed.getUserInfo()==null)+"");
//					msgGirlHead.setImage(tempFeed.getUserInfo().getWifeUrl(), false, 74, RkNbOurHome.this,"head", RkNbOurHome.this);
					msgGirlHead.displayImage(imageLoader, 
							tempFeed.getUserInfo().getWifeUrl(), 
							R.drawable.cx_fa_wf_icon_small, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
//					msgBoyHead.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					msgBoyHead.setImage(tempFeed.getUserInfo().getHusbandUrl(), false, 74,RkNbOurHome.this, "head", RkNbOurHome.this);
					msgBoyHead.displayImage(imageLoader, tempFeed.getUserInfo().getHusbandUrl(), 
							R.drawable.cx_fa_hb_icon_small, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					if (TextUtils.equals(CxGlobalParams.getInstance().getPairId(),
							tempFeed.getPair_id())) {
						msgFamilyName.setText("我们和"+tempFeed.getUserInfo().getWifeName()+"一家私聊");
					}else{
						msgFamilyName.setText(tempFeed.getUserInfo().getWifeName()+"一家和我们私聊");
					}
					
				}else{
//					msgGirlHead.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//					msgGirlHead.setImage(tempFeed.getUserInfo().getHusbandUrl(), false, 74, RkNbOurHome.this,"head", RkNbOurHome.this);
					msgGirlHead.displayImage(imageLoader, 
							tempFeed.getUserInfo().getHusbandUrl(), 
							R.drawable.cx_fa_hb_icon_small, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
//					msgBoyHead.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//					msgBoyHead.setImage(tempFeed.getUserInfo().getWifeUrl(), false, 74,RkNbOurHome.this, "head", RkNbOurHome.this);
					msgBoyHead.displayImage(imageLoader, 
							tempFeed.getUserInfo().getWifeUrl(), 
							R.drawable.cx_fa_wf_icon_small, true, 
							CxGlobalParams.getInstance().getSmallImgConner());
					
					if (TextUtils.equals(CxGlobalParams.getInstance().getPairId(),
							tempFeed.getPair_id())) {
						msgFamilyName.setText("我们和"+tempFeed.getUserInfo().getHusbandName()+"一家私聊");
					}else{
						msgFamilyName.setText(tempFeed.getUserInfo().getHusbandName()+"一家和我们私聊");
					}
				}
			}

			if (TextUtils.equals(CxGlobalParams.getInstance().getPairId(),
					tempFeed.getPair_id())) { // 创建者是自己一家

				if (TextUtils.equals(CxGlobalParams.getInstance().getUserId(),
						tempFeed.getAuthor())) {
					speakerName.setText(CxNbOurHome.this.getString(R.string.cx_fa_nls_me));
					msgSpeakerName.setText(CxNbOurHome.this.getString(R.string.cx_fa_nls_me));
					deleteBtn.setVisibility(View.VISIBLE);
				} else {
					hintCommentName=getString(CxResourceString.getInstance().str_pair);
					speakerName.setText(hintCommentName); 
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
								DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
								CxNeighbourApi.getInstance().requestDeleteFeed(tempFeed.getId(),new DeleteFeedBack(itemLocation));
							}
						});
						Dialog dialog = du.getSimpleDialog(CxNbOurHome.this, null,getString(R.string.cx_fa_delete_comfirm_text),null, null);
						dialog.setCancelable(true);
						dialog.show();

					}
				});
			} else { // 创建者是密邻
				msgSpeakerName.setText(tempFeed.getName());
			}

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
					int row_w = screen_w- ScreenUtil.dip2px(CxNbOurHome.this, 12 * 2 + 15*2+14*2- 2 * 6); 
					int w = (Integer) (row_w / 3); // 自动算图的宽度(有一个风险，如果屏幕的宽大于高，这个就会特别难看)
					int h = w;
					LinearLayout.LayoutParams layoutParaForMorePic = new LinearLayout.LayoutParams(
							w, h);
					int margin = ScreenUtil.dip2px(CxNbOurHome.this, 2);
					layoutParaForMorePic.setMargins(margin, margin, margin,
							margin);

					// 只有一个图片的布局参数
					w = ScreenUtil.dip2px(CxNbOurHome.this, 170); // dp转换为pix
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
//								RkNbOurHome.this, "head", RkNbOurHome.this);
						tempImage.displayImage(ImageLoader.getInstance(), 
								tempPhoto.getThumb(), R.drawable.chatview_imageloading, false, 0);
						
						final int clickItem = i;
						tempImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								CxNeighbourParam.getInstance().setPhotos(photos);
//								Intent imageDetail = new Intent(RkNbOurHome.this,RkNeighbourImageDetail.class);
								Intent imageDetail = new Intent(CxNbOurHome.this,CxImagePager.class);
								imageDetail.putExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER,clickItem);
								imageDetail.putExtra(CxGlobalConst.S_STATE, CxImagePager.STATE_ZONE_NEIGHBOR);
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
						View replyView = CxNbOurHome.this.getLayoutInflater().inflate(R.layout.cx_fa_fragment_neighbour_list_reply_item,null);
						
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
							hintReplyName="回复 "+speakerStr+":";
						} else {			
							speakerStr = reply.getName();
							hintReplyName="回复 "+speakerStr+":";
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
								// speakerStr += (null == RkGlobalParams
								// .getInstance().getPartnerName() ? ""
								// : RkGlobalParams.getInstance()
								// .getPartnerName());
							} else {
								speakerStr = reply.getReply_name();
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
																getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_neighbour_reply_emotion_size), CxNbOurHome.this);
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
												DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
												CxNeighbourApi.getInstance().requestDeleteReply(reply.getReply_id(),deleteReply);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
									du.getSimpleDialog(CxNbOurHome.this,null,getString(R.string.cx_fa_delete_comfirm_text),
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
			
			String strName="";
			if(!TextUtils.isEmpty(hintCommentName)){
				strName="评论："+hintCommentName;
			}
			
//			final String hintCommentName2=strName;
			final String hintCommentName2="评论:";

			// 评论按钮
			ImageButton commentOrReplayBtn = holder.commentBtn;
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
		public TextView familyId;
		
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
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
//			new Handler(getMainLooper()) {
//				public void handleMessage(android.os.Message msg) {
//					mInputPanel.setVisibility(View.GONE);
//					
//				}
//			}.sendEmptyMessage(1);
			
			// RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback start");
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
			if ((null == mHomeFeedsData) || (mHomeFeedsData.size() < 1)) { // 属于异常(没有帖子数据就没有回复的动作）
				return -1;
			}
			int tempSize = mHomeFeedsData.size();
			for (int i = 0; i < tempSize; i++) {
				InvitationData tempFeed = mHomeFeedsData.get(i);
				
				if (TextUtils.equals(reply.getFeed_id(), tempFeed.getId())) { // 是这个帖子的回复
					InvitationPost tempPost = tempFeed.getPost();
					if (null == tempPost) { // 容错处理：帖子内存不存在 （严格来讲，这样的情况属于异常）
						tempPost = new InvitationPost();
						ArrayList<InvitationReply> targetReply = new ArrayList<InvitationReply>();
						targetReply.add(reply);
						
//						RkNeighbourParam.getInstance().setNbAddReply(reply);
						// tempPost.setReplays(targetReply);
						mHomeFeedsData.get(i).getPost().setReplays(targetReply);
					} else { // 正常情况
						ArrayList<InvitationReply> targetReply = new ArrayList<InvitationReply>();
						targetReply.add(reply);
						if (null != tempPost.getReplays()) {
							targetReply.addAll(tempPost.getReplays());
						}
						mHomeFeedsData.get(i).getPost().setReplays(targetReply);
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
			if (mFeedIndex >= mHomeFeedsData.size()) {
				return -9;
			}
			try {
				InvitationData tempFeed = mHomeFeedsData.get(mFeedIndex);
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
			InvitationData data=mHomeFeedsData.get(mLocation);
			CxNeighbourParam.getInstance().setNbDelInvitation(data);
			mHomeFeedsData.remove(mLocation);
			updateListview.sendEmptyMessage(1);

			return 0;
		}
	}

	JSONCaller modifyBgOfNb = new JSONCaller() {

		@Override
		public int call(Object result) {
//			new Handler(RkNbOurHome.this.getMainLooper()) {
//				public void handleMessage(Message msg) {
//					RkLoadingUtil.getInstance().dismissLoading();
//				};
//			}.sendEmptyMessage(1);

			String background = changeImageParser(result,CxNbOurHome.this.
					getString(R.string.cx_fa_net_response_code_fail),1);

			if (background == null) {
				return -1;
			}

			bgUrl = background;

			updateListview.sendEmptyMessage(1);
			showResponseToast(getString(R.string.cx_fa_modify_bg_of_neighbour_ourhome_success), 1);

			return 0;
		}
	};

	JSONCaller modifyGirlHeadImage = new JSONCaller() {

		@Override
		public int call(Object result) {
//			new Handler(RkNbOurHome.this.getMainLooper()) {
//				public void handleMessage(Message msg) {
//					RkLoadingUtil.getInstance().dismissLoading();
//				};
//			}.sendEmptyMessage(1);

			String wifeHead = changeImageParser(result,CxNbOurHome.this
					.getString(R.string.cx_fa_net_response_code_fail), 3);

			if (wifeHead == null) {
				return -1;
			}

			wifeUrl = wifeHead;
			CxNeighbourParam.getInstance().setNbWifeIcon(wifeHead);
			
			String version = getString(CxResourceString.getInstance().str_pair);
			
			if("老公".equals(version)){
				CxGlobalParams.getInstance().setIconSmall(wifeHead);
				CxGlobalParams.getInstance().setIconBig(wifeHead);
				CxGlobalParams.getInstance().setIconMid(wifeHead);
			}else{
				CxGlobalParams.getInstance().setPartnerIconBig(wifeHead);
			}
			

			updateListview.sendEmptyMessage(1);
			showResponseToast(getString(R.string.cx_fa_modify_headimg_success), 1);
			return 0;
		}
	};

	JSONCaller modifyBoyHeadImage = new JSONCaller() {

		@Override
		public int call(Object result) {
//			new Handler(RkNbOurHome.this.getMainLooper()) {
//				public void handleMessage(Message msg) {
//					RkLoadingUtil.getInstance().dismissLoading();
//				};
//			}.sendEmptyMessage(1);

			String husbandHead = changeImageParser(result,CxNbOurHome.this
							.getString(R.string.cx_fa_net_response_code_fail), 4);

			if (husbandHead == null) {
				return -1;
			}
			husbandUrl = husbandHead;
			CxNeighbourParam.getInstance().setNbHusbandIcon(husbandHead);
			
			String version = getString(CxResourceString.getInstance().str_pair);
			
			if("老公".equals(version)){
				CxGlobalParams.getInstance().setPartnerIconBig(husbandHead);
			}else{
				CxGlobalParams.getInstance().setIconSmall(husbandHead);
				CxGlobalParams.getInstance().setIconBig(husbandHead);
				CxGlobalParams.getInstance().setIconMid(husbandHead);
			}
			
			updateListview.sendEmptyMessage(1);
			showResponseToast(getString(R.string.cx_fa_modify_headimg_success), 1);
			return 0;
		}
	};

	private String changeImageParser(Object result, String text, int i) {
		
		DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
		if (null == result) {

			showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
			return null;
		}
		JSONObject changeBg = null;
		try {
			changeBg = (JSONObject) result;
		} catch (Exception e) {
		}
		if (null == changeBg) {
			showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
			return null;
		}

		try {
			int rc = changeBg.getInt("rc");
			if (0 != rc) {
				if(rc==408){
					showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
					return null;
				}
				
				if(!changeBg.isNull("msg") && !TextUtils.isEmpty(changeBg.getString("msg"))){
					showResponseToast(changeBg.getString("msg"),0);
				}else{
					showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				}
				
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject data = null;
		try {
			data = changeBg.getJSONObject("data");
		} catch (Exception e) {
		}
		if (null == data) {
			showResponseToast(text,0);
			return null;
		}
		String str = null;
		try {
			switch (i) {
			case 1:
				str = data.getString("bg_big");
				break;
			case 2:
				str = data.getString("bg_small");
				break;
			case 3:
				str = data.getString("avatar0");
				break;
			case 4:
				str = data.getString("avatar1");
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (str == null) {
			showResponseToast(text,0);
			return null;
		}
		return str;
	}

	OnLongClickListener titleLongClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			Intent addInvitation = new Intent(CxNbOurHome.this,
					CxNeighbourAddInvitation.class);
			addInvitation.putExtra(CxGlobalConst.S_NEIGHBOUR_SHARED_TYPE, 0);
			startActivity(addInvitation);
			return false;
		}
	};

	OnClickListener titleClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				if(mInputPanel.getVisibility()==View.VISIBLE){
					mSendReplyLayer.setVisibility(View.INVISIBLE);
					mInputPanel.setVisibility(View.GONE);
					mInputPanel.setDefaultMode();
				}else{
					finish();
					CxNbOurHome.this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				}
				break;
			case R.id.cx_fa_activity_title_more:
				Intent changeChatBackground = new Intent(CxNbOurHome.this,
						ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom = false;
				ActivitySelectPhoto.kIsCallFilter = true;
				ActivitySelectPhoto.kIsCallSysCamera = false;
				ActivitySelectPhoto.kChoseSingle = false;
				ActivitySelectPhoto.kFrom = "RkNbOurHome";
				startActivity(changeChatBackground);
//				startActivityForResult(changeChatBackground, ADD_INVITATION);
				break;
			default:
				break;
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			if(mInputPanel.getVisibility()==View.INVISIBLE){
				mSendReplyLayer.setVisibility(View.INVISIBLE);
				mInputPanel.setVisibility(View.GONE);
				mInputPanel.setDefaultMode();
				return false;
			}
			finish();
			CxNbOurHome.this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			return false;
		}
		
		
		return super.onKeyDown(keyCode, event);
	};

	OnClickListener imageBtnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_neighbour_bg:
				Intent selectImageForBg = new Intent(CxNbOurHome.this,
						ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom = true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForBg, MODIFY_NB_BG_REQUEST);
				break;
			case R.id.nb_girlHeadView:
				Intent selectImageForGirlHead = new Intent(CxNbOurHome.this,
						ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom = true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForGirlHead,
						MODIFY_GIRL_HEAD_REQUEST);
				break;
			case R.id.nb_boyHeadView:
				Intent selectImageForBoyHead = new Intent(CxNbOurHome.this,
						ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom = true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForBoyHead,
						MODIFY_BOY_HEAD_REQUEST);
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
		new Handler(CxNbOurHome.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxNbOurHome.this, id,
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Activity.RESULT_OK != resultCode) {
			return;
		}
//		if (ADD_INVITATION == requestCode) { 
//			//
//			String imagePath = data
//					.getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI);
//			if (null == imagePath) {
//				return;
//			}
//			Intent addInvitation = new Intent(RkNbOurHome.this,
//					RkNeighbourAddInvitation.class);
//			addInvitation.putExtra(RkGlobalConst.S_NEIGHBOUR_SHARED_TYPE, 1);
//			addInvitation.putExtra(RkGlobalConst.S_NEIGHBOUR_SHARED_IMAGE,
//					imagePath);
//			startActivity(addInvitation);
//			return;
//		}

		if (MODIFY_NB_BG_REQUEST == requestCode) {
			if (!TextUtils.isEmpty(data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI))) {
				try {
					String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
					imagePath = imagePath.replace("file://", "");
//					RkLoadingUtil.getInstance().showLoading(RkNbOurHome.this,true);
					DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
					CxNeighbourApi.getInstance().sendHeadImage(imagePath,"background",modifyBgOfNb);
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					showResponseToast(getString(R.string.cx_fa_modify_bg_of_neighbour_ourhome),0);
				}
			} else {
				showResponseToast(getString(R.string.cx_fa_modify_bg_of_neighbour_ourhome),0);
			}
			return;
		}

		if (MODIFY_GIRL_HEAD_REQUEST == requestCode) {
			if (!TextUtils.isEmpty(data
					.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI))) {
				try {
					String imagePath = data
							.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
					imagePath = imagePath.replace("file://", "");
//					RkLoadingUtil.getInstance().showLoading(RkNbOurHome.this,true);
					DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
					CxNeighbourApi.getInstance().sendHeadImage(imagePath,"avatar0",modifyGirlHeadImage);
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					showResponseToast(getString(R.string.cx_fa_modify_headimg_fail),0);
				}
			} else {
				showResponseToast(getString(R.string.cx_fa_modify_headimg_fail),0);
			}
			return;
		}

		if (MODIFY_BOY_HEAD_REQUEST == requestCode) {
			if (!TextUtils.isEmpty(data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI))) {
				try {
					String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
					imagePath = imagePath.replace("file://", "");
//					RkLoadingUtil.getInstance().showLoading(RkNbOurHome.this,true);
					DialogUtil.getInstance().getLoadingDialogShow(CxNbOurHome.this, -1);
					CxNeighbourApi.getInstance().sendHeadImage(imagePath,"avatar1",modifyBoyHeadImage);
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					showResponseToast(getString(R.string.cx_fa_modify_headimg_fail),0);
				}
			} else {
				showResponseToast(getString(R.string.cx_fa_modify_headimg_fail),0);
			}
			return;
		}

	};

	class CurrentObserver extends CxObserverInterface {

		/* 主要事项有：1、新的分享 2、头像（中、小） */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}

			if (CxNeighbourParam.NEIGHBOUR_DATA.equalsIgnoreCase(actionTag)) { // 自己发帖子成功
				CxLog.i("999999", " has receive notify for new feed");
				
				CxNeighbourParser sendParser = new CxNeighbourParser();
				CxNeighbourSendInvitation sendResult = null;
				JSONObject jObj=null;
				
				String result=CxNeighbourParam.getInstance().getInvitationData();
				if(result==null)
					return;
				
				try {
					jObj=new JSONObject(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(jObj==null){
					return ;
				}
				sendResult=sendParser.getSendInvitationResult(jObj);
				if (null == mHomeFeedsData) {
					mHomeFeedsData = new ArrayList<InvitationData>();
					mHomeFeedsData.add(sendResult.getData());
					mAdapter.updataView(mHomeFeedsData);
					sendResult=null;
//					RkNeighbourParam.getInstance().setInvitationData(RkNeighbourParam.getInstance()
//							.getHomeInvitationData());
					CxLog.i("source data is null",
							" has receive notify for new feed");
					return;
				} else {
					List<InvitationData> targetData = new ArrayList<InvitationData>();
					targetData.add(sendResult.getData());
					targetData.addAll(mHomeFeedsData);
					mHomeFeedsData = targetData;
					mAdapter.updataView(mHomeFeedsData);
					sendResult=null;
//					RkNeighbourParam.getInstance().setInvitationData(RkNeighbourParam.getInstance()
//							.getHomeInvitationData());
					CxLog.i("source data is not null",
							" has receive notify for new feed");
				}
				return;
			}
		}
	}


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
			du.getSimpleDialog(CxNbOurHome.this, null,
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
		int mDeviceWidth = CxNbOurHome.this.getWindowManager()
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
		try {
			if (RkSoundEffects.cFmodGetIsPlay()) {
				// RkSoundEffects.cFmodStop();
				// stopRecordAnimation();
				stopVoice(image);
			}
			RkSoundEffects.soundPlay(path, 0);
			startTimer(msgVoiceLen, image);
			startRecordAnimation(image);
		} catch (Exception e) {
			e.printStackTrace();
			mVoicePlayFlag = false;
		}
		// ChatFragment.getInstance().mPlayRecordEntry = RecordEntry.this;
	}

	public void stopVoice(ImageView image) {
		CxLog.d("playVoice", "isplay1=" + RkSoundEffects.cFmodGetIsPlay());
		CxLog.d("playVoice", "ispause1=" + RkSoundEffects.cFmodGetIsPause());
		try {
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
		} catch (Exception e) {
			mVoicePlayFlag = false;
			e.printStackTrace();
		}
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
			try {
				mVoiceAd.stop();
				mVoicePlayFlag = false;
				iamge.setImageResource(R.drawable.chat_voice3);
			} catch (Exception e) {
				mVoicePlayFlag = false;
				e.printStackTrace();
			}
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
				.getAudioFileResourceManager(CxNbOurHome.this);

		if (resourceManager.exists(Uri.parse(url))) {
			// RkLog.i(TAG, "file path local=");
			final File file = resourceManager.getFile(Uri.parse(url));
			CxLog.i("getAudioFile", "filepath0=" + file.getAbsolutePath());
			new Handler(CxNbOurHome.this.getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					preparePlayVoice(file.getAbsolutePath(), audioLength, image);
				}
			}.sendEmptyMessage(1);
			return file.getAbsolutePath();
		} else {
			// checkSdCardExist();
			new Handler(CxNbOurHome.this.getMainLooper()) {
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
						CxLog.i("getAudioFile", "contentlength>>>"+ len);
						CxLog.i("getAudioFile","file.length>>>" + file.length());
						CxLog.i("getAudioFile", "audiolength>>>"+ audioLength);

						if (file.length() != len) {
							CxLog.i("getAudioFile","not download file complete, need to reload");
							file.delete();
							// 重试3次，失败了给出提示
							if (mRetryCount == 0) {
								getAudioFile(url, audioLength, image,progress);
							} else {
								new Handler(CxNbOurHome.this.getMainLooper()) {
									public void handleMessage(android.os.Message msg) {
										progress.setVisibility(View.GONE);
										image.setVisibility(View.VISIBLE);
									}
								}.sendEmptyMessage(1);

								ToastUtil.getSimpleToast(CxNbOurHome.this,-1,
										getString(R.string.cx_fa_not_download_file_complete),1).show();
							}
						} else {
							CxLog.i("getAudioFile","filepath1="+ file.getAbsolutePath());
							final String audioFilePath = file.getAbsolutePath();
							CxLog.i("getAudioFile", "audioFilePath="+ audioFilePath);
							new Handler(CxNbOurHome.this.getMainLooper()) {
								public void handleMessage(android.os.Message msg) {
									progress.setVisibility(View.GONE);
									image.setVisibility(View.VISIBLE);
									preparePlayVoice(audioFilePath,audioLength, image);
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
	
	

}
