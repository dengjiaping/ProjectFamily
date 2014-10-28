package com.chuxin.family.kids;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OneKeyShareCallback;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.R;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.net.CxKidApi;
import com.chuxin.family.net.CxZoneApi;
import com.chuxin.family.parse.CxKidParser;
import com.chuxin.family.parse.been.CxKidAddReply;
import com.chuxin.family.parse.been.CxKidFeed;
import com.chuxin.family.parse.been.CxKidFeedList;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxShareThdRes;
import com.chuxin.family.parse.been.data.KidFeedChildrenData;
import com.chuxin.family.parse.been.data.KidFeedData;
import com.chuxin.family.parse.been.data.KidFeedPhoto;
import com.chuxin.family.parse.been.data.KidFeedPost;
import com.chuxin.family.parse.been.data.KidFeedReply;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.service.CxServiceParams;
import com.chuxin.family.utils.CxAudioFileResourceManager;
import com.chuxin.family.utils.CxBaseDiskCache;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CustomTextView;
import com.chuxin.family.widgets.CxImagePager;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.widgets.CxInputPanel.OnEventListener;
import com.chuxin.family.widgets.ScrollableListView;
import com.chuxin.family.widgets.ScrollableListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uraroji.garage.android.mp3recvoice.RecMicToMp3;

import net.simonvt.menudrawer.CxBaseSlidingMenu;

import org.fmod.effects.RkSoundEffects;
import org.json.JSONException;
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
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class CxKidFragment extends Fragment implements Callback{

	protected static final int ADD_INVITATION = 0;

	private static final int UPDATE_RECORD_TIME = 1;

	private static final int SEND_RECORD = 2;

	public static final int READ_RECORD = 3;
	public static final int STOP_READ_RECORD = 4;
	
	public static final int UPDATE_HOME_MENU = 5; // 更新home按钮未读消息状态
	public static final int UPDATE_KIDS_INFO = 6; // 

	protected static final int MAX_VU_SIZE = 14; // set phone volume level 14

	protected static final int MODIFY_NB_BG_REQUEST = 6;
	
	public static String RK_CURRENT_VIEW = "CxKidFragment";
	private boolean isFirstComplete = false;
	private boolean isDownComplete = true; // 向下拉完成的标识位。默认为true表示完成
	private boolean isUpComplete = true; // 向上推完成的标识位。默认为true表示完成
	
	public static boolean mRecordStart;
	
	private int mReplyIndex; // 需要删除的回复
	private int mFeedIndex; // 需要删除的回复所在的帖子在数据源中的位置
	private String mReplyFeedId; // 评论的帖子ID
	private String mReply_to; // 回复给的对象（评论时为null,回复时为对方的ID）
	
	private List<KidFeedData> mFeedsData; // 帖子的数据 
	private ArrayList<KidFeedChildrenData> mKidsData;
	
	private SensorManager mSensorManager = null; // 传感器管理器  
    private Sensor mProximiny = null; // 传感器实例  
    private float mFproximiny; // 当前传感器距离
    
    private int kidPos=1;//当前被选中的第几个孩子
    
    private String currentAge=""; // 1208   前两位为年  后两位为月
    
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
	public void onAttach(Activity activity) {
		ShareSDK.initSDK(activity);
		super.onAttach(activity);
	}
    
    @Override
    public void onStart() {    	
    	super.onStart();
    	CxInputPanel.sInputPanelUse = RK_CURRENT_VIEW;
    	mInputPanel.setDefaultMode();
    	mInputPanel.setVisibility(View.GONE);
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		input = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		
		// 设置观察者
		mServiceObserver = new CurrentObserver();
		// 设置观察目标
		List<String> tags = new ArrayList<String>();
		tags.add(CxGlobalParams.KID_TIPS); // 新的分享资料
		mServiceObserver.setListenTag(tags); // 设置观察目标
		mServiceObserver.setMainThread(true); // 设置在UI线程执行
		CxGlobalParams.getInstance().registerObserver(mServiceObserver); // 注册观察者
			
		
		mAddFeedObserver = new CurrentObserver();
		List<String> feedTags = new ArrayList<String>();
		feedTags.add(CxKidParam.KID_ADD_DATA);
		mAddFeedObserver.setListenTag(feedTags);
		mAddFeedObserver.setMainThread(true);
		CxKidParam.getInstance().registerObserver(mAddFeedObserver);
		
		mKidHandler = new Handler() {

			@Override
			public void handleMessage(final android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				
				case UPDATE_RECORD_TIME:
					mChatRecordRelativeLayout
							.setBackgroundResource(R.drawable.pub_recorder);
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
							getActivity().getPackageName());
					mRecordImageView.setImageResource(resId);
					if (mRecordRemainTimeTextView.getVisibility() != View.VISIBLE) {
						mRecordRemainTimeTextView.setVisibility(View.VISIBLE);
					}
					mRecordRemainTimeTextView.setText(String.format(
							getResources().getString(
									R.string.cx_fa_chat_record_time_remianing),
							(91 - mReocrdCount)));
					// mRecordRemainTimeTextView
					// .setText(String
					// .format(getResources().getString(
					// R.string.cx_fa_chat_record_time_remianing),
					// (90 - ((System.currentTimeMillis() - mRecorderStartTime)
					// / 1000))));
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
							DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
							CxKidApi.getInstance().requestAddReply(mReplyFeedId, "audio", null,
									mSoundFilePath, audioLength, mReply_to, mSendCommentCallback);	
							mInputPanel.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						ToastUtil.getSimpleToast(getActivity(),-1,
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
                case UPDATE_HOME_MENU:
                	if(CxKidFragment.this.isVisible()){
                		updateHomeMenu();
                	} 
                    break;
                case UPDATE_KIDS_INFO:
                    CxKidApi.getInstance().requestFeedList(0, 15,
                            new FeedListResponse(true, true), getActivity());
                    break;
				default:
					break;
				}
			}

		};

		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {		
		
		try {
			ShareSDK.stopSDK(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(mAddFeedObserver!=null){
			CxKidParam.getInstance().unRegisterObsercer(mAddFeedObserver);
		}
		if(mServiceObserver!=null){
			CxGlobalParams.getInstance().unRegisterObsercer(mServiceObserver);
		}
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View inflate = inflater.inflate(R.layout.cx_fa_fragment_kids, null);
		((CxMain) getActivity()).closeMenu();
		initTitle(inflate);
		init(inflate);
		fillData();
		
		return inflate;
	}



	private void fillData() {
		// 加载本地数据(本地保存15条，不至于会超时）
		CxKidListCacheData cacheData = new CxKidListCacheData(getActivity());
		CxKidFeedList list = cacheData.queryCacheData(CxGlobalParams.getInstance().getUserId());
		if( null != list){
			ArrayList<KidFeedChildrenData> kids = list.getKids();
			
			ArrayList<KidFeedData> feeds = list.getDatas();
			if ((null != feeds) && (feeds.size() > 0)) {
//				noFeedLayout.setVisibility(View.GONE);
				mKidsData=kids;
				mAdapter.updataView(feeds);
				CxLog.i("CxKidFragment_men", ">>>>>>>>>>2");
			}
		}
		// 加载本地数据后首先到网络获取第一屏数据
		CxKidApi.getInstance().requestFeedList(0, 15,
				new FeedListResponse(true, true), getActivity());
		
	}

	private void init(View inflate) {
		
		mInputPanel = (CxInputPanel) inflate.findViewById(R.id.cx_fa_widget_input_layer);
		mVoicePanel = (LinearLayout)mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout2);
		ImageButton mPlusButton1 = (ImageButton) inflate.findViewById(R.id.cx_fa_widget_input_panel__layout1_button1);	
		ImageButton mPlusButton2 = (ImageButton) inflate.findViewById(R.id.cx_fa_widget_input_panel__layout2_button3);
		mPlusButton1.setVisibility(View.GONE);
		mPlusButton2.setVisibility(View.GONE);
		mInputText = (EditText) mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout1_textedit1);
		
		mContentList = (ScrollableListView) inflate.findViewById(R.id.cx_fa_kids_fragment_slv);
		
		mRecordView = (LinearLayout) inflate.findViewById(R.id.cx_fa_kids_fragment_record_include);
		mRecordImageView = (ImageView) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_imageview);
		mChatRecordRelativeLayout = (RelativeLayout) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_relativelayout);
		mRecordRemainTimeTextView = (TextView) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_recordtime_textview);
		mRecordCancelTip = (TextView) mRecordView.findViewById(R.id.cx_fa_dialog_chat_record_textview_tip);
		mRecordView.setVisibility(View.GONE);
		
		mAdapter = new KidAdapter();
		mContentList.setAdapter(mAdapter);
		CxLog.i("CxKidFragment_men", ">>>>>>>>>>1");
		
//		noFeedLayout = (LinearLayout) inflate.findViewById(R.id.cx_fa_kids_home_no_feed_layout);
		TextView tv = (TextView) inflate.findViewById(R.id.cx_fa_kids_home_no_feed_tv);
		Button btn = (Button) inflate.findViewById(R.id.cx_fa_kids_home_no_feed_btn);
		tv.setText(CxResourceString.getInstance().str_kids_home_no_feed_text);
		btn.setOnClickListener(titleListener);
		
		mContentList.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputPanel.setDefaultMode();
				mInputPanel.setVisibility(View.GONE);
				return false;
			}
		});

		mContentList.setOnHeaderRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (isFirstComplete && isDownComplete) {
					isDownComplete = false;
					CxKidApi.getInstance().requestFeedList(0, 15, new FeedListResponse(true, true), getActivity());
				} else {
					new Handler(getActivity().getMainLooper()) {
						public void handleMessage(android.os.Message msg) {
							mContentList.onRefreshComplete();
						};
					}.sendEmptyMessageDelayed(1, 500);
				}
			}
		});

		mContentList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (isUpComplete) {
					isUpComplete = false;
					int offest = (null == mFeedsData ? 0 : mFeedsData.size());
					CxKidApi.getInstance().requestFeedList(offest, 15, new FeedListResponse(false, false), getActivity());
				} else {
					mContentList.refreshComplete();
				}
			}
		});
		
		mInputPanel.setOnEventListener(inputEventListener);
		
		isFirstComplete = false;

		mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null){	
	        if (CxGlobalParams.getInstance().isChatEarphone()) {
	            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
	        } else {
	            mAudioManager.setMode(AudioManager.MODE_NORMAL);
	        }
        }
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);  
        mProximiny = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); 
		
	}

	private void initTitle(View inflate) {
		menuBtn = (Button) inflate.findViewById(R.id.cx_fa_activity_title_back);
		Button feedBtn = (Button) inflate.findViewById(R.id.cx_fa_activity_title_more);
		TextView titleText = (TextView) inflate.findViewById(R.id.cx_fa_activity_title_info);
		
		titleText.setText(getString(R.string.cx_fa_kids_home_title_text));
		
		menuBtn.setBackgroundResource(R.drawable.cx_fa_menu_btn);
		feedBtn.setVisibility(View.VISIBLE);
		feedBtn.setBackgroundResource(R.drawable.cx_fa_zone_shareimage_btn);
		
		menuBtn.setOnClickListener(titleListener);
		feedBtn.setOnClickListener(titleListener);
		feedBtn.setOnLongClickListener(titleBtnLongClick);
		
	}
	
	// 空间列表请求的网络应答
	class FeedListResponse implements JSONCaller {

		private boolean isPushDown = true; // 默认向下拉
		private boolean isFirst = false; // 默认不是第一次获取空间资源

		public FeedListResponse(boolean pushDown, boolean first) {
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

			if (null == getActivity()) {
				return -1;
			}

			new Handler(getActivity().getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					if (isPushDown) {
						mContentList.onRefreshComplete();
					} else {
						mContentList.refreshComplete();
					}

				};
			}.sendEmptyMessage(1);

			if (null == result) {
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
				return -1;
			}
			CxKidFeedList feedList = null;
			try {
				feedList = (CxKidFeedList) result;
			} catch (Exception e) {
			}
			if (null == feedList || feedList.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
				return -2;
			}
			int rc = feedList.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(feedList.getMsg())){
					showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(feedList.getMsg(),0);
				}
				return 1;
			}

			if (isFirst) {
				
				if (null != mFeedsData) {
					mFeedsData.clear();
					mFeedsData = null;
				}
				
				if(null!=mKidsData){
					mKidsData.clear();
					mKidsData = null;
				}
				mKidsData=feedList.getKids();
				if(mKidsData==null || mKidsData.size()<2){
					kidPos=1;
				}else if(kidPos>mKidsData.size()){
					kidPos=mKidsData.size();
				}
				mFeedsData = feedList.getDatas();
//				if(mFeedsData!=null && mFeedsData.size()>0){
//					noFeedLayout.setVisibility(View.GONE);
//				}
				updateListview.sendEmptyMessage(1);
				CxLog.i("CxKidFragment_men", ">>>>>>>>>>3");
				
				CxGlobalParams.getInstance().setKid_tips(0);// 进入密邻获取数据后，未读消息数置空 by shichao.wang 20131029
				Message nbMsg = CxKidFragment.mKidHandler.obtainMessage(CxNeighbourFragment.UPDATE_HOME_MENU);
	            nbMsg.sendToTarget();
	            if(null != CxBaseSlidingMenu.mBaseSlidingMenuHandler){
	                Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler
	                .obtainMessage(CxBaseSlidingMenu.UPDATE_KID_UNREAD_MESSAGE);
	                baseMsg.sendToTarget();
	            }	
				return 0;
			}

			/*
			 * if (null != mFeedsData) { mFeedsData.clear(); mFeedsData = null;
			 * }
			 */
			ArrayList<KidFeedChildrenData> kids = feedList.getKids();
			if (isPushDown) { // 往下拉(或者push)
				mFeedsData = feedList.getDatas(); // 直接换成最新数据即可

			} else { // 往上翻
				if ((null == feedList.getDatas())
						|| (feedList.getDatas().size() < 1)) {
					// 没有数据
					return 0;
				}
				if (null == mFeedsData) {
					mFeedsData = new ArrayList<KidFeedData>();
				}
				mFeedsData.addAll(feedList.getDatas());
			}
//			if(mFeedsData!=null && mFeedsData.size()>0){
//				(View.GONE);
//			}
			
			mVoicePlayFlag = false;
			updateListview.sendEmptyMessage(1);
			CxGlobalParams.getInstance().setGroup(0);// 进入密邻获取数据后，未读消息数置空 by shichao.wang 20131029
			Message nbMsg = CxKidFragment.mKidHandler.obtainMessage(CxKidFragment.UPDATE_HOME_MENU);
            nbMsg.sendToTarget();
            if(null != CxBaseSlidingMenu.mBaseSlidingMenuHandler){
                Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler.obtainMessage(CxBaseSlidingMenu.UPDATE_UNREAD_MESSAGE);
                baseMsg.sendToTarget();
            }
			return 0;
		}

	}

	Handler updateListview = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter.updataView(mFeedsData);
		};
	};
	
	
	private LinearLayout nameLayout1;
	private LinearLayout nameLayout2;
	private LinearLayout nameLayout3;
	private LinearLayout nameLayout4;
	private LinearLayout nameLayout5;
	private TextView name1;
	private TextView name2;
	private TextView name3;
	private TextView name4;
	private TextView name5;
	private CxImageView icon;
	private TextView nickNameText;
	private TextView gendarText;
	private TextView ageText;
	private TextView birText;
	private TextView healthyText;
	private TextView eduText;
	
	
	// item的adapter
	class KidAdapter extends BaseAdapter {
		private final int HEAD_VIEW = 0;
		private final int ITEM_VIEW = 1;

		private List<KidFeedData> mAdapterData;

		public synchronized void updataView(List<KidFeedData> adapterData) {
			this.
			mAdapterData = adapterData;
			KidAdapter.this.notifyDataSetChanged();
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
			
			if (null == mAdapterData || mAdapterData.size()==0) {
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
				
				View view = getActivity().getLayoutInflater().inflate(R.layout.cx_fa_fragment_kids_header, null);
				
				LinearLayout nameLayout = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_name_all_layout);
				nameLayout1 = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_name_1_layout);
				nameLayout2 = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_name_2_layout);
				nameLayout3 = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_name_3_layout);
				nameLayout4 = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_name_4_layout);
				nameLayout5 = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_name_5_layout);
				
				name1 = (TextView) view.findViewById(R.id.cx_fa_kids_header_name_1_tv);
				name2 = (TextView) view.findViewById(R.id.cx_fa_kids_header_name_2_tv);
				name3 = (TextView) view.findViewById(R.id.cx_fa_kids_header_name_3_tv);
				name4 = (TextView) view.findViewById(R.id.cx_fa_kids_header_name_4_tv);
				name5 = (TextView) view.findViewById(R.id.cx_fa_kids_header_name_5_tv);
				
				icon = (CxImageView) view.findViewById(R.id.cx_fa_kids_header_icon_civ);
				nickNameText = (TextView) view.findViewById(R.id.cx_fa_kids_header_nickname_tv);
				gendarText = (TextView) view.findViewById(R.id.cx_fa_kids_header_gendar_tv);
				ageText = (TextView) view.findViewById(R.id.cx_fa_kids_header_age_tv);
				birText = (TextView) view.findViewById(R.id.cx_fa_kids_header_bir_tv);
				
				LinearLayout infoLayout = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_kidinfo_layout);
				LinearLayout growupLayout = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_growup_layout);
				LinearLayout healthyLayout = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_healthy_layout);
				LinearLayout eduLayout = (LinearLayout) view.findViewById(R.id.cx_fa_kids_header_education_layout);
				
				healthyText = (TextView) view.findViewById(R.id.cx_fa_kids_header_healthy_tv);
				eduText = (TextView) view.findViewById(R.id.cx_fa_kids_header_education_tv);
				
				LinearLayout noFeedLayout = (LinearLayout) view.findViewById(R.id.cx_fa_kids_home_no_feed_layout);
				TextView noFeedText = (TextView) view.findViewById(R.id.cx_fa_kids_home_no_feed_tv);
				Button noFeedBtn = (Button) view.findViewById(R.id.cx_fa_kids_home_no_feed_btn);
				noFeedText.setText(CxResourceString.getInstance().str_kids_home_no_feed_text);
				noFeedBtn.setOnClickListener(headerListener);
				if(getCount()==1){
					noFeedLayout.setVisibility(View.VISIBLE);
				}else{
					noFeedLayout.setVisibility(View.GONE);
				}
				
				ViewGroup.LayoutParams param = icon.getLayoutParams();
				param.height = (int) (getResources().getDisplayMetrics().widthPixels * 0.67f + 0.5f);
				icon.setLayoutParams(param);

				nameLayout1.setOnClickListener(headerListener);
				nameLayout2.setOnClickListener(headerListener);
				nameLayout3.setOnClickListener(headerListener);
				nameLayout4.setOnClickListener(headerListener);
				nameLayout5.setOnClickListener(headerListener);
				icon.setOnClickListener(headerListener);
				infoLayout.setOnClickListener(headerListener);
				healthyLayout.setOnClickListener(headerListener);
				eduLayout.setOnClickListener(headerListener);
				
				if(mKidsData==null || mKidsData.size()<1){
					nameLayout.setVisibility(View.GONE);
					gendarText.setVisibility(View.GONE);
					ageText.setVisibility(View.GONE);
					birText.setVisibility(View.GONE);
					nickNameText.setText("点这里设置孩子资料>");
					showKidTab(0, kidPos);
				}else if(mKidsData.size()==1){
					nameLayout.setVisibility(View.GONE);					
					showKidTab(1, kidPos);
				}else{
					nameLayout.setVisibility(View.VISIBLE);				
					showKidTab(mKidsData.size(), kidPos);
					switch (mKidsData.size()) {
					case 2:
						nameLayout3.setVisibility(View.GONE);
						nameLayout4.setVisibility(View.GONE);
						nameLayout5.setVisibility(View.GONE);
						break;
					case 3:
						nameLayout3.setVisibility(View.VISIBLE);
						nameLayout4.setVisibility(View.GONE);
						nameLayout5.setVisibility(View.GONE);
						break;
					case 4:
						nameLayout3.setVisibility(View.VISIBLE);
						nameLayout4.setVisibility(View.VISIBLE);
						nameLayout5.setVisibility(View.GONE);
						break;
					case 5:
						nameLayout3.setVisibility(View.VISIBLE);
						nameLayout4.setVisibility(View.VISIBLE);
						nameLayout5.setVisibility(View.VISIBLE);
						break;
					default:
						break;
					}
				}
				return view;
			}
			CxLog.i("CxKidFragment_men", (null == mAdapterData)+">>>>>>>>1");
			
//			if (position==1) {
//				View inflate = getActivity().getLayoutInflater().inflate(
//						R.layout.cx_fa_fragment_kids_list_item2, null);
//				
//				TextView tv = (TextView) inflate.findViewById(R.id.cx_fa_kids_home_item2_tv);
//				Button btn = (Button) inflate.findViewById(R.id.cx_fa_kids_home_item2_btn);
//				tv.setText(CxResourceString.getInstance().str_kids_home_no_feed_text);
//				btn.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						Intent changeChatBackground = new Intent(getActivity(),ActivitySelectPhoto.class);
//						ActivitySelectPhoto.kIsCallPhotoZoom = false;
//						ActivitySelectPhoto.kIsCallFilter = true;
//						ActivitySelectPhoto.kIsCallSysCamera = false;
//						ActivitySelectPhoto.kChoseSingle = false;
//						ActivitySelectPhoto.kFrom="RkNeighbourFrament";
////						startActivityForResult(changeChatBackground, ADD_INVITATION);
//						startActivity(changeChatBackground);
//					}
//				});
//				
//				if(null == mAdapterData || mAdapterData.size()==0){
//					return null;
//				}
//				
//				return inflate;
//			}
			CxLog.i("CxKidFragment_men", (null == mAdapterData)+">>>>>>>>2");

			final int itemLocation = position - 1;
			NbItemViewHolder holder = null;
			if (null == convertView) {
				
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.cx_fa_fragment_kids_list_item, null);
				CxLog.i("CxKidFragment_men", (null == convertView)+">>>>>>>>4");
				holder = new NbItemViewHolder();
				
				holder.titleLayout = (LinearLayout) convertView.findViewById(R.id.cx_fa_kids_item_title_layout);
				holder.speakDate = (TextView) convertView.findViewById(R.id.cx_fa_kids_item_title_date);
				holder.kidTime1 = (TextView) convertView.findViewById(R.id.cx_fa_kids_item_title_kid_time1);
				holder.kidTime2 =(TextView) convertView.findViewById(R.id.cx_fa_kids_item_title_kid_time2);
				holder.speakHead = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_content_icon);
				holder.speakerName = (TextView) convertView.findViewById(R.id.cx_fa_kids_item_content_name);
				holder.speakTime = (TextView) convertView.findViewById(R.id.cx_fa_kids_item_content_time);
				holder.speakWord = (CustomTextView) convertView.findViewById(R.id.cx_fa_kids_item_content_word);
				holder.textMore=(TextView)convertView.findViewById(R.id.cx_fa_kids_item_content_word_more);
				
				holder.commentBtn = (ImageButton) convertView.findViewById(R.id.cx_fa_kids_item_comment);
				holder.deleteBtn = (ImageButton) convertView.findViewById(R.id.cx_fa_kids_item_delete);
				holder.commentAndReplayOfRecord = (LinearLayout) convertView.findViewById(R.id.cx_fa_kids_item_commentAndReplayOfRecord);
				

				holder.sharedPhotos = new ArrayList<CxImageView>();
				CxImageView firstImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_first_9image);
				holder.sharedPhotos.add(firstImage);
				CxImageView secondImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_second_9image);
				holder.sharedPhotos.add(secondImage);
				CxImageView thirdImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_third_9image);
				holder.sharedPhotos.add(thirdImage);
				CxImageView forthImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_forth_9image);
				holder.sharedPhotos.add(forthImage);
				CxImageView fifthImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_fifth_9image);
				holder.sharedPhotos.add(fifthImage);
				CxImageView sixthImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_sixth_9image);
				holder.sharedPhotos.add(sixthImage);
				CxImageView seventhImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_seventh_9image);
				holder.sharedPhotos.add(seventhImage);
				CxImageView eighthImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_eighth_9image);
				holder.sharedPhotos.add(eighthImage);
				CxImageView ninethImage = (CxImageView) convertView.findViewById(R.id.cx_fa_kids_item_photos_nineth_9image);
				holder.sharedPhotos.add(ninethImage);

//				holder.titleLayout.setTag(position);
				
				convertView.setTag(holder);

			} else {
				CxLog.i("CxKidFragment_men", (null == convertView)+">>>>>>>>5");
				holder = (NbItemViewHolder) convertView.getTag();
			}

			if ((null == mAdapterData) || (mAdapterData.size() < position)) { // 第一个item不需要数据
				return convertView;
			}
			final KidFeedData tempFeed = mAdapterData.get(position - 1); // 第一个item不需要数据
			
			
//			int tag = (Integer) holder.titleLayout.getTag();
//			if(tag!=position){
//				return convertView;
//			}
//			
//			LinearLayout bgLayout=holder.bgLayout;
//			
//			if(1==tempFeed.getIsNew()){
//				bgLayout.setBackgroundResource(R.drawable.neighbor_backpink);
//			}else{
//				bgLayout.setBackgroundResource(R.drawable.neighbor_backwhite);
//			}
			
			String hintCommentName="";
				
			CxLog.i("CxKidFragment_men", (null == holder)+">>>>>>>>3");
			
			// 头像、名称和删除按钮
			CxImageView speakHeadView = holder.speakHead;
			TextView speakerName = holder.speakerName;
			TextView speakTime = holder.speakTime;
			ImageButton deleteBtn = holder.deleteBtn; // 删除按钮

			deleteBtn.setVisibility(View.GONE);
			
			if (TextUtils.equals(CxGlobalParams.getInstance().getUserId(),tempFeed.getAuthor())) {
				speakHeadView.displayImage(ImageLoader.getInstance(),CxGlobalParams.getInstance().getIconBig(), 
						CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, CxGlobalParams.getInstance().getSmallImgConner());
				speakerName.setText(getActivity().getString(R.string.cx_fa_nls_me));
				deleteBtn.setVisibility(View.VISIBLE);
				
			} else {
				speakHeadView.displayImage(ImageLoader.getInstance(),CxGlobalParams.getInstance().getPartnerIconBig(), 
						CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, CxGlobalParams.getInstance().getSmallImgConner());
				hintCommentName=getActivity().getString(CxResourceString.getInstance().str_pair);
				speakerName.setText(hintCommentName);
				deleteBtn.setVisibility(View.GONE);
			}

			deleteBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					//
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setItems(new String[]{"分享到社交网络",
							"删除此话题","取消"}, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0: //分享到社交网络 
								dlg = DialogUtil.getInstance().getLoadingDialog(getActivity());
								dlg.show();
								try {
									CxZoneApi.getInstance().sendShareRequest("child", tempFeed.getId(), 
											new ShareThirdSender(tempFeed, getActivity()));
								} catch (Exception e) {
									e.printStackTrace();
									dlg.dismiss();
								}
								break;
								
							case 1:
								// 删除帖子
								DialogUtil du = DialogUtil.getInstance();
								du.setOnSureClickListener(new OnSureClickListener() {

									@Override
									public void surePress() {
										DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
										CxKidApi.getInstance().requestDeleteFeed(tempFeed.getId(),
												new DeleteFeedBack(itemLocation));
									}
								});
								Dialog dialog2 = du.getSimpleDialog(getActivity(),null,getString(R.string.cx_fa_delete_comfirm_text),
										null, null);
								dialog2.setCancelable(true);
								dialog2.show();
								break;

							default:
								break;
							}
							
						}
					});
					builder.show();
				}
			});
			
			// 时间
			SimpleDateFormat sdf = new SimpleDateFormat("MM月:dd日:HH:mm");
			CxLog.i("zone feed create time:", "" + tempFeed.getCreate());
			Date createStamp = new Date(Long.parseLong(tempFeed.getCreate()) * 1000L);
			
				
			String stampStr = sdf.format(createStamp);
			String[] stampArray = stampStr.split(":");
			String dayStr = null, monthStr = null, timeStr = null;
			monthStr = filterStartZero(stampArray[0]);
			dayStr =  filterStartZero(stampArray[1]);
			timeStr = stampArray[2] + ":" + stampArray[3];
			speakTime.setText(timeStr);
			
			
			final CustomTextView speakWord = holder.speakWord;
			final TextView textMore = holder.textMore;
			LinearLayout photosLayout = (LinearLayout) convertView.findViewById(R.id.cx_fa_kids_item_content_shared_photos);

			KidFeedPost feedContent = tempFeed.getPost();
			if (null != feedContent) {
				// 文字内容
				if (!TextUtils.isEmpty(feedContent.getText())) {
					
					speakWord.setVisibility(View.VISIBLE);
					String text=feedContent.getText();		
					if(text.length()>100){
						final String simpleText=text.substring(0, 100)+"...";
						textMore.setVisibility(View.VISIBLE);
						textMore.setText("显示更多>>");						
						speakWord.setText(simpleText);
//						final TextView mSpeakWord=speakWord;
//						final TextView mTextMore=textMore;
						final String text2=text;
						textMore.setOnClickListener(new OnClickListener() {
							boolean unfold=false;
							@Override
							public void onClick(View v) {
								if(unfold){
									textMore.setText("显示更多>>");						
									speakWord.setText(simpleText);
									unfold=false;
								}else{
									textMore.setText("收起");
									speakWord.setText(text2);	
									unfold=true;
								}
								
							}
						});
					}else{
						speakWord.setText(text);
						textMore.setVisibility(View.GONE);
					}
					
					
				} else {
					textMore.setVisibility(View.GONE);
					speakWord.setVisibility(View.GONE);
				}
				// 图片
				final List<KidFeedPhoto> photos = feedContent.getPhotos();
				if ((null == photos) || (photos.size() < 1)) { // 没有照片
					photosLayout.setVisibility(View.GONE);
				} else { // 有照片
					photosLayout.setVisibility(View.VISIBLE);
					int len = photos.size();

					/* 分别得到有多张图时和只有一张图时，每个图片的显示布局。 目的:只有一张图时，显示大图 */
					// 有多张图片时每张图片的布局参数
					int screen_w = getResources().getDisplayMetrics().widthPixels; // 屏幕宽度
					int row_w = screen_w- ScreenUtil.dip2px(getActivity(), 24+3*6); // 三个图像减去父对象margin及padding后，可用的宽度
					
					int w = (Integer) (row_w / 3); // 自动算图的宽度(有一个风险，如果屏幕的宽大于高，这个就会特别难看)
					int h = w;
					LinearLayout.LayoutParams layoutParaForMorePic = new LinearLayout.LayoutParams(w, h);
					int margin = ScreenUtil.dip2px(getActivity(), 3);
					layoutParaForMorePic.setMargins(margin, margin, margin,margin);

					// 只有一个图片的布局参数
					w = ScreenUtil.dip2px(getActivity(), 170); // dp转换为pix
					h = w;
					LinearLayout.LayoutParams layoutParaForOnlyOnePic = new LinearLayout.LayoutParams(
							w, h);
					
					// 得到图片的所有路径，供看大图时使用 (
					ArrayList<String> imagepaths = new ArrayList<String>();
					for(int i=0; i<len; i++){
						KidFeedPhoto tempPhoto = photos.get(i);
						if(tempPhoto.getBig()!=null){
							imagepaths.add( tempPhoto.getBig() );
						}
					}
					final ArrayList<String> imgs = imagepaths;
					
					for (int i = 0; i < len; i++) { //
						CxImageView tempImage = holder.sharedPhotos.get(i);
						tempImage.setVisibility(View.VISIBLE);
						KidFeedPhoto tempPhoto = photos.get(i);
//						tempImage.setImage(tempPhoto.getThumb(), false, 74,RkNeighbourFragment.this, "head",
//								RkNeighbourFragment.this.getActivity());
						tempImage.displayImage(ImageLoader.getInstance(), tempPhoto.getThumb(),
								R.drawable.chatview_imageloading, false, 0);
						final int clickItem = i;
						tempImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
//								CxNeighbourParam.getInstance().setPhotos(photos);
//								Intent imageDetail = new Intent(getActivity(),RkNeighbourImageDetail.class);
								Intent imageDetail = new Intent(getActivity(),CxImagePager.class);
								imageDetail.putExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER,clickItem);
								imageDetail.putExtra(CxGlobalConst.S_STATE, CxImagePager.STATE_KID);
				                imageDetail.putStringArrayListExtra("imagespath", imgs);				            
								startActivity(imageDetail);
								getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
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
				List<KidFeedReply> replies = feedContent.getReplays();
				if ((null == replies) || (replies.size() < 1)) {
					commentAndReplayOfRecord.setVisibility(View.GONE);
				} else {
					commentAndReplayOfRecord.setVisibility(View.VISIBLE);
					for (int k = 0; k < replies.size(); k++) {
						final int tempIndex = k;
						View replyView = getActivity().getLayoutInflater().inflate(R.layout.cx_fa_fragment_neighbour_list_reply_item,null);
//						CustomTextView itemReply = (CustomTextView) replyView.findViewById(R.id.nb_post_reply_text_content);
						TextView itemReply = (TextView) replyView.findViewById(R.id.nb_post_reply_text_content);
						TextView replyTime = (TextView) replyView.findViewById(R.id.nb_post_reply_time);
						ImageView replyUnread = (ImageView) replyView.findViewById(R.id.nb_post_reply_unread);
						
						EnhancedGifView gifView = (EnhancedGifView) replyView.findViewById(R.id.nb_post_reply_expression_content);
						LinearLayout recordLayout = (LinearLayout) replyView.findViewById(R.id.nb_post_reply_record_content_linearlayout);
						ProgressBar recordProgress = (ProgressBar) replyView.findViewById(R.id.nb_post_reply_record_content_circleProgressBar);
						ImageView recordImage = (ImageView) replyView.findViewById(R.id.nb_post_reply_record_content_image);
						TextView recordLength = (TextView) replyView.findViewById(R.id.nb_post_reply_record_content_audiolength);

						itemReply.setText("");
						replyTime.setText("");

						final KidFeedReply reply = replies.get(k);
							
						if(1==reply.getIsNew()){
							replyUnread.setVisibility(View.VISIBLE);
						}
						
						String speakerStr = "";
						String hintReplyName="";
						// 谁评论

						if (TextUtils.equals(reply.getAuthor(), CxGlobalParams
								.getInstance().getUserId())) {
							speakerStr = getString(R.string.cx_fa_nls_me);
						} else if (TextUtils.equals(reply.getAuthor(),CxGlobalParams.getInstance().getPartnerId())) {

							speakerStr = getString(CxResourceString.getInstance().str_pair);
							hintReplyName="回复 "+speakerStr+":";
						} 
						itemReply.append(TextUtil.getNewSpanStr(speakerStr, 14, Color.rgb( 105, 105, 105)));
						
						
						speakerStr = "";
						// 是否是回复（有2中情况：评论和回复）
						if ((null == reply.getReply_to())|| ("null".equalsIgnoreCase(reply.getReply_to()))) {
							// 不是回复，仅仅发表评论// 不做任何处理
						} else { // 回复(其实这样要注意自己给自己回复的情况，这是不允许的，暂时先不考虑这样的bug出现)
							
							speakerStr = getString(R.string.cx_fa_reply_text);
							itemReply.append( TextUtil.getNewSpanStr(speakerStr, 14, Color.rgb( 55, 50,47)) );
							
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
							}
							
							if (!speakerStr.equals("")) {
								itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.rgb( 105, 105, 105)) );
							}

						}
						
//						replyTime.setText(DateUtil.getTimeDiffWithNow(reply.getTs()));
						
						speakerStr = " :  ";
						itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.rgb( 105, 105, 105)) );
						
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
							replyTime.append(TextUtil.getNewSpanStr(str2, 9, Color.rgb( 190, 190, 190)));
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
																getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_neighbour_reply_emotion_size), getActivity());
														itemReply.append(spanStr);
														break;
													}
												}	
												if(!isFace){
													itemReply.append(TextUtil.getNewSpanStr(substring, 14, Color.rgb( 55, 50,47)));						
												}
												i=j;
												break;											
											}
										}
										if(!hasright){
											itemReply.append(TextUtil.getNewSpanStr("[", 14, Color.rgb( 55, 50,47)));						
										}
									}else{
										itemReply.append(TextUtil.getNewSpanStr(text.charAt(i)+"", 14, Color.rgb( 55, 50,47)));
									}
									
								}
								
								replyTime.setVisibility(View.GONE);
								itemReply.append(TextUtil.getNewSpanStr(str2, 9, Color.argb(112, 0, 0, 0)));
						
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
												DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
												CxKidApi.getInstance().requestDeleteReply(reply.getReply_id(),deleteReply);
//												CxNeighbourApi.getInstance().requestDeleteReply(reply.getReply_id(),deleteReply);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
									du.getSimpleDialog(getActivity(),null,getString(R.string.cx_fa_delete_comfirm_text),
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

			
//			String strName="";
//			if(!TextUtils.isEmpty(hintCommentName)){
//				strName="评论:"+hintCommentName;
//			}
			
//			final String hintCommentName2=strName;
			final String hintCommentName2="评论:";
			
			commentOrReplayBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mReplyFeedId = tempFeed.getId();
					mReply_to = null;
					// mCommentLayout.setVisibility(View.VISIBLE);
					mInputPanel.setVisibility(View.VISIBLE);
					input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

					mInputText.setFocusable(true);
					mInputText.requestFocus();
					mInputText.requestFocusFromTouch();
					mInputText.setSelection(0);
					mInputText.setCursorVisible(true);
					mInputText.setHint(hintCommentName2);
					mVoicePlayFlag = false;
					// mCommentInputEditer.requestFocus();
				}
			});

			// 日期
//			int len = dayStr.length() + monthStr.length();
			LinearLayout titleLayout = holder.titleLayout;
			TextView speakDate = holder.speakDate;
			
			TextView kidTime1 = holder.kidTime1;
			TextView kidTime2 = holder.kidTime2;
			
			if(mKidsData==null || mKidsData.size()<1){		
				kidTime1.setVisibility(View.GONE);
				kidTime2.setVisibility(View.GONE);
			}else if(mKidsData.size()==1){
				String string = getNameAndAge(createStamp,mKidsData.get(0), 1);	
				if(!TextUtils.isEmpty(string)){
					kidTime1.setVisibility(View.VISIBLE);
					kidTime1.setText(string);
				}else{
					kidTime1.setVisibility(View.GONE);
				}
				kidTime2.setVisibility(View.GONE);
			}else{
				String string1 = getNameAndAge(createStamp,mKidsData.get(0), 1);
				if(!TextUtils.isEmpty(string1)){
					kidTime1.setVisibility(View.VISIBLE);
					kidTime1.setText(string1);
				}else{
					kidTime1.setVisibility(View.GONE);
				}
				String string2 = getNameAndAge(createStamp,mKidsData.get(1), 2);
				if(!TextUtils.isEmpty(string2)){
					kidTime2.setVisibility(View.VISIBLE);
					kidTime2.setText(string2);
				}else{
					kidTime2.setVisibility(View.GONE);
				}
			}
			
			titleLayout.setVisibility(View.VISIBLE);
			speakDate.setText(monthStr+dayStr);

//			if (1 == position) { // 第一行无条件显示
//				
//			} else { // 非第一行
//						//
//						// 判断跟上一天是否是同一天
//				if((position - 2)>=0 && (position - 2)<mAdapterData.size()){
//					KidFeedData preFeed = mAdapterData.get(position - 2);
//					// Log.e("RkUsersPairZone", feedContent.getText()+"  time1:" +
//					// preFeed.getCreate() + "  time2:" + tempFeed.getCreate() );
//					boolean flag = DateUtil.isTheSameDay(Long.valueOf(preFeed.getCreate()) * 1000,
//							Long.valueOf(tempFeed.getCreate()) * 1000);
//	
//					if (flag) {
//						titleLayout.setVisibility(View.GONE);
//					} else {
//						titleLayout.setVisibility(View.VISIBLE);
//						speakDate.setText(monthStr+dayStr);						
//					}
//				}
//			}
			//
			return convertView;
		}

	}

	// item的adapter的holder
	static class NbItemViewHolder {
		
		public LinearLayout titleLayout; 
		public TextView speakDate; // 日期
		public TextView kidTime1;
		public TextView kidTime2;
		public CxImageView speakHead; // 发帖头像
		public TextView speakerName; // 昵称
		public TextView speakTime; // 时间
		public CustomTextView speakWord; // 文字内容
		public TextView textMore;
		public List<CxImageView> sharedPhotos; // 图片内容
		public ImageButton commentBtn; // 评论
		public ImageButton deleteBtn; // 删除本帖子
		public LinearLayout commentAndReplayOfRecord;
	}
	
	
	private String getNameAndAge(Date date,KidFeedChildrenData data,int key){
		String nickname = data.getNickname();
		String allname = data.getName();
		
		
		
		if(TextUtils.isEmpty(nickname)){
			nickname=allname;
		}
		if(TextUtils.isEmpty(nickname)){
			nickname="孩子"+key;
		}
		String birth = data.getBirth();
		String str="";
		
		if(!TextUtils.isEmpty(birth)){
			String age=DateUtil.getChildAge(date,data.getBirth());
			CxLog.i("CxKidFragment_men", "age:"+age+">>>>>");
			if(TextUtils.isEmpty(age)){
				int day = DateUtil.getDaysByNow(date,data.getBirth());
				CxLog.i("CxKidFragment_men", "day:"+day+">>>>>");
				if(day==0){
					str="今天";
				}else{
					str="离出生还有"+day+"天";
				}
			}else{
				String[] split = age.split(":");
				
				if(!split[0].equals("0")){
					str+=split[0]+"岁";
				}
				if(!split[1].equals("0")){
					str+=split[1]+"个月";
				}
				if(!split[2].equals("0")){
					if(TextUtils.isEmpty(str)){
						str+=split[2]+"天";
					}else{
						str+="零"+split[2]+"天";
					}
				}
				if(TextUtils.isEmpty(str)){
					str="今天";
				}
			}
			
			return nickname+str;
		}else{
			return "";
		}
		
	}
	
	
	/**
	 * 孩子标签切换及初始化
	 * @param i
	 * @param focused
	 */
	private void showKidTab(int i,int focused){
		
		CxLog.i("CxKidFragment_men", i+">>>>>>>"+focused);
		
		if(mKidsData!=null && mKidsData.size()>0){
			KidFeedChildrenData data = mKidsData.get(focused-1);
			showKidInfo(data,focused);
		}

		if(i<2){
			healthyText.setText("健康");
			eduText.setText("教育");
			return ;
		}
		
		String nickname1 = mKidsData.get(0).getNickname();
		String allname1 = mKidsData.get(0).getName();
		if(TextUtils.isEmpty(nickname1)){
			nickname1=allname1;
		}
		if(TextUtils.isEmpty(nickname1)){
			nickname1="孩子1";
		}
		if(focused==1){
			healthyText.setText("健康("+nickname1+")");
			eduText.setText("教育("+nickname1+")");
			name1.setText(TextUtil.getNewSpanStr(nickname1, 16, Color.rgb(234, 177, 121)));
			nameLayout1.setBackgroundResource(R.color.kids_home_header_tab_fouced);
		}else{
			name1.setText(TextUtil.getNewSpanStr(nickname1, 14, Color.rgb(105, 105, 105)));
			nameLayout1.setBackgroundResource(R.color.kids_home_header_tab_normal);
		}
		
		String nickname2 = mKidsData.get(1).getNickname();
		String allname2 = mKidsData.get(1).getName();
		if(TextUtils.isEmpty(nickname2)){
			nickname2=allname2;
		}
		if(TextUtils.isEmpty(nickname2)){
			nickname2="孩子2";
		}
		if(focused==2){
			healthyText.setText("健康("+nickname2+")");
			eduText.setText("教育("+nickname2+")");
			name2.setText(TextUtil.getNewSpanStr(nickname2, 16, Color.rgb(234, 177, 121)));
			nameLayout2.setBackgroundResource(R.color.kids_home_header_tab_fouced);
		}else{
			name2.setText(TextUtil.getNewSpanStr(nickname2, 14, Color.rgb(105, 105, 105)));
			nameLayout2.setBackgroundResource(R.color.kids_home_header_tab_normal);
		}
		if(i==2){
			
			return ;
		}
		
		String nickname3 = mKidsData.get(2).getNickname();
		String allname3 = mKidsData.get(2).getName();
		if(TextUtils.isEmpty(nickname3)){
			nickname3=allname3;
		}
		if(TextUtils.isEmpty(nickname3)){
			nickname3="孩子3";
		}
		if(focused==3){
			healthyText.setText("健康("+nickname3+")");
			eduText.setText("教育("+nickname3+")");
			name3.setText(TextUtil.getNewSpanStr(nickname3, 16, Color.rgb(234, 177, 121)));
			nameLayout3.setBackgroundResource(R.color.kids_home_header_tab_fouced);
		}else{
			name3.setText(TextUtil.getNewSpanStr(nickname3, 14, Color.rgb(105, 105, 105)));
			nameLayout3.setBackgroundResource(R.color.kids_home_header_tab_normal);
		}
		if(i==3){
			return ;
		}
		
		String nickname4 = mKidsData.get(3).getNickname();
		String allname4 = mKidsData.get(3).getName();
		if(TextUtils.isEmpty(nickname4)){
			nickname4=allname4;
		}
		if(TextUtils.isEmpty(nickname4)){
			nickname4="孩子4";
		}
		if(focused==4){
			healthyText.setText("健康("+nickname4+")");
			eduText.setText("教育("+nickname4+")");
			name4.setText(TextUtil.getNewSpanStr(nickname4, 16, Color.rgb(234, 177, 121)));
			nameLayout4.setBackgroundResource(R.color.kids_home_header_tab_fouced);
		}else{
			name4.setText(TextUtil.getNewSpanStr(nickname4, 14, Color.rgb(105, 105, 105)));
			nameLayout4.setBackgroundResource(R.color.kids_home_header_tab_normal);
		}
		if(i==4){
			return ;
		}
		
		String nickname5 = mKidsData.get(4).getNickname();
		String allname5 = mKidsData.get(4).getName();
		if(TextUtils.isEmpty(nickname5)){
			nickname5=allname5;
		}
		if(TextUtils.isEmpty(nickname5)){
			nickname5="孩子5";
		}
		if(focused==5){
			healthyText.setText("健康("+nickname5+")");
			eduText.setText("教育("+nickname5+")");
			name5.setText(TextUtil.getNewSpanStr(nickname5, 16, Color.rgb(234, 177, 121)));
			nameLayout5.setBackgroundResource(R.color.kids_home_header_tab_fouced);
		}else{
			name5.setText(TextUtil.getNewSpanStr(nickname5, 14, Color.rgb(105, 105, 105)));
			nameLayout5.setBackgroundResource(R.color.kids_home_header_tab_normal);
		}
	}
	
	/**
	 * 孩子资料显示
	 * @param data
	 */
	private void showKidInfo(KidFeedChildrenData data,int key){
		currentAge="";
		String nickname = data.getNickname();
		String name = data.getName();
		int gender = data.getGender();
		String birth = data.getBirth();
		String avata = data.getAvata();
		icon.setImageResource(R.drawable.cx_fa_kids_defaultimage_kid);
		icon.displayImage(ImageLoader.getInstance(), avata, R.drawable.cx_fa_kids_defaultimage_kid, false, 0);

		if(TextUtils.isEmpty(nickname)){
			if(TextUtils.isEmpty(name)){
				nickNameText.setText("孩子"+key);
			}else{
				nickNameText.setText(name);
			}
		}else{
			if(TextUtils.isEmpty(name)){
				nickNameText.setText(nickname);
			}else{
				nickNameText.setText(nickname+"("+name+")");
			}
		}
		
		if(gender==-1){
			gendarText.setVisibility(View.GONE);
		}else{
			gendarText.setVisibility(View.VISIBLE);
			if(gender==0){
				gendarText.setText("儿子");
			}else{
				gendarText.setText("女儿");
			}
		}
		
		if(TextUtils.isEmpty(birth)){
			currentAge="";
			ageText.setVisibility(View.GONE);
			birText.setVisibility(View.GONE);
		}else{
			ageText.setVisibility(View.VISIBLE);
			birText.setVisibility(View.VISIBLE);

			String age=DateUtil.getChildAge(birth);
			if(TextUtils.isEmpty(age)){
				birText.setText("预产期"+birth.substring(4,6)+"."+birth.substring(6,8));
				ageText.setVisibility(View.GONE);
				
				int day = 280-DateUtil.getDaysByNow(new Date() ,data.getBirth());
				CxLog.i("CxKidFragment_men", "day:"+day+">>>>>");
				if(day<=0){
					day=1;
				}
				int week=day-1/7+1;
				currentAge="-"+(week>9?week:"0"+week);
			}else{
				String[] split = age.split(":");
				String str="";
				if(!split[0].equals("0")){
					str+=split[0]+"岁";
				}
				currentAge+=(Integer.parseInt(split[0])>9?split[0]:"0"+split[0]);
				
				if(!split[1].equals("0")){
					str+=split[1]+"个月";
				}
				currentAge+=(Integer.parseInt(split[1])>9?split[1]:"0"+split[1]);
				
				if(!split[2].equals("0")){
					if(TextUtils.isEmpty(str)){
						str+=split[2]+"天";
					}else{
//						str+="零"+split[2]+"天";
					}
				}
				if(TextUtils.isEmpty(str)){
					str="今天";
				}
				ageText.setText(str);
				birText.setText("生日"+birth.substring(4,6)+"."+birth.substring(6,8));
			}
		}	
	}
	
	
	OnEventListener inputEventListener=new OnEventListener() {
		
		@Override
		public int onMessage(String msg, int flag) {
			if (flag == 0) {// 发普通文字
				if (TextUtils.isEmpty(msg)) {
					ToastUtil.getSimpleToast(getActivity(), -1,getString(R.string.cx_fa_zone_no_content), 1).show();
					return 0;
				}
				try {
					DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
					CxKidApi.getInstance().requestAddReply(mReplyFeedId, "text", msg, null, 0, mReply_to, mSendCommentCallback);
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
		public void onStartRecordEvent(View v, MotionEvent m) {
			mInputPanel.setBackgroundResource(R.drawable.chatview_voice_h);
			CxGlobalParams.getInstance().setRecorderFlag(true);
			((CxMain) getActivity()).availeChildSlide();
			mRecordView.setVisibility(View.VISIBLE);
			mChatRecordRelativeLayout.setBackgroundResource(R.drawable.pub_recorder);
			mRecordCancelTip.setText(R.string.cx_fa_chat_record_tip);
			mRecordStart = true;
			startRecord();
			startTimer();
		}
		@Override
		public void onStopMoveEvent(View v, MotionEvent m) {
			stopRecordEvent(m);
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
		public void onButton2Click(View button) {
		}
		@Override
		public void onButton1Click(View button) {
		}
        @Override
        public void onButton0Click(View button) {        
        }
	};
	
	
	
	OnClickListener headerListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CxLog.i("CxKidFragment_men", ">>>>>>>>>>>>5");
			switch (v.getId()) {
			case R.id.cx_fa_kids_header_name_1_layout:
				kidPos=1;
				showKidTab(mKidsData.size(),kidPos);
				break;
			case R.id.cx_fa_kids_header_name_2_layout:
				kidPos=2;
				showKidTab(mKidsData.size(), kidPos);
				break;
			case R.id.cx_fa_kids_header_name_3_layout:
				kidPos=3;
				showKidTab(mKidsData.size(), kidPos);
				break;
			case R.id.cx_fa_kids_header_name_4_layout:
				kidPos=4;
				showKidTab(mKidsData.size(), kidPos);
				break;
			case R.id.cx_fa_kids_header_name_5_layout:
				kidPos=5;
				showKidTab(mKidsData.size(), kidPos);
				break;
			case R.id.cx_fa_kids_header_icon_civ:
			    
				Intent info1 = new Intent(getActivity(),CxKidsInfo.class);
				info1.putExtra("current_kid_id", kidPos);
				info1.putExtra("kids_info", mKidsData);
				startActivity(info1);
				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			case R.id.cx_fa_kids_header_kidinfo_layout:
				Intent info2 = new Intent(getActivity(),CxKidsInfo.class);
				info2.putExtra("current_kid_id", kidPos);
				info2.putExtra("kids_info", mKidsData);
				startActivity(info2);
				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			case R.id.cx_fa_kids_header_healthy_layout:
				//TODO 跳转健康
			    Intent toHeathPage = new Intent(getActivity(), CxKidsInfoWebViewActivity.class);// shichao
			    toHeathPage.putExtra("redirect_page_id", 1);
			    CxLog.i("CxKidFragment", "current age="+currentAge);
			    toHeathPage.putExtra("redirect_page_age", currentAge);
			    startActivity(toHeathPage);
			    getActivity().overridePendingTransition(R.anim.tran_next_in,
			            R.anim.tran_next_out);
				break;
			case R.id.cx_fa_kids_header_education_layout:
				//TODO 跳转教育
			    Intent toTeachPage = new Intent(getActivity(), CxKidsInfoWebViewActivity.class);// shichao
			    toTeachPage.putExtra("redirect_page_id", 2);
			    CxLog.i("CxKidFragment", "current age="+currentAge);
			    toTeachPage.putExtra("redirect_page_age", currentAge);
			    startActivity(toTeachPage);
			    getActivity().overridePendingTransition(R.anim.tran_next_in,
			            R.anim.tran_next_out);
				break;
			case R.id.cx_fa_kids_home_no_feed_btn:
				//TODO 跳转教育
				Intent changeChatBackground = new Intent(getActivity(),ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom = false;
				ActivitySelectPhoto.kIsCallFilter = true;
				ActivitySelectPhoto.kIsCallSysCamera = false;
				ActivitySelectPhoto.kChoseSingle = false;
				ActivitySelectPhoto.kFrom="CxKidFragment";
//				startActivityForResult(changeChatBackground, ADD_INVITATION);
				startActivity(changeChatBackground);
				break;

			default:
				break;
			}
			
		}
	};
	
	OnClickListener titleListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				mInputPanel.setVisibility(View.GONE);
				mInputPanel.setDefaultMode();
				try {
					((CxMain) getActivity()).toggleMenu();
				} catch (Exception e) {
				}
				break;
			case R.id.cx_fa_activity_title_more:
				Intent changeChatBackground = new Intent(getActivity(),ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom = false;
				ActivitySelectPhoto.kIsCallFilter = true;
				ActivitySelectPhoto.kIsCallSysCamera = false;
				ActivitySelectPhoto.kChoseSingle = false;
				ActivitySelectPhoto.kFrom="CxKidFragment";
//				startActivityForResult(changeChatBackground, ADD_INVITATION);
				startActivity(changeChatBackground);
				break;
			default:
				break;
			}
			
		}
	};
	
	OnLongClickListener titleBtnLongClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			Intent addInvitation = new Intent(getActivity(),CxKidAddFeed.class);
			addInvitation.putExtra(CxGlobalConst.S_KID_SHARED_TYPE, 0);
			startActivity(addInvitation);
			getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
			return false;
		}
	};

	private CxInputPanel mInputPanel;

	private EditText mInputText;

	private ImageView mRecordImageView;

	private RelativeLayout mChatRecordRelativeLayout;

	private TextView mRecordRemainTimeTextView;

	private TextView mRecordCancelTip;

	private ScrollableListView mContentList;

	private CurrentObserver mAddFeedObserver;

	private KidAdapter mAdapter;
	
	
	// 评论帖子的回调
	JSONCaller mSendCommentCallback = new JSONCaller() {

		@Override
		public int call(Object result) {
			// RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback start");
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);	
			if (null == result) {
				// 提示评论失败
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxKidAddReply replyResult = null;
			try {
				replyResult = (CxKidAddReply) result;
			} catch (Exception e) {
			}
			if (null == replyResult || replyResult.getRc()==408) {
				// 提示评论失败
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}

			int rc = -1;
			try {
				rc = replyResult.getRc();
			} catch (Exception e) {
			}
			if (-1 == rc) {
				// 提示评论失败
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -3;
			}

			if (0 != rc) {
				// TODO 提示评论失败
				if(TextUtils.isEmpty(replyResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(replyResult.getMsg(),0);
				}
				return rc;
			}
			KidFeedReply reply = replyResult.getReply();
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
				KidFeedData tempFeed = mFeedsData.get(i);
				if (TextUtils.equals(reply.getFeed_id(), tempFeed.getId())) { // 是这个帖子的回复
					KidFeedPost tempPost = tempFeed.getPost();
					if (null == tempPost) { // 容错处理：帖子内存不存在 （严格来讲，这样的情况属于异常）
						tempPost = new KidFeedPost();
						ArrayList<KidFeedReply> targetReply = new ArrayList<KidFeedReply>();
						targetReply.add(reply);
						tempPost.setReplays(targetReply);
						mFeedsData.get(i).setPost(tempPost);
					} else { // 正常情况
						ArrayList<KidFeedReply> targetReply = new ArrayList<KidFeedReply>();
						targetReply.add(reply);
						if (null != tempPost.getReplays()) {
							targetReply.addAll(tempPost.getReplays());
						}
						mFeedsData.get(i).getPost().setReplays(targetReply);
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
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic deleteResult = null;
			try {
				deleteResult = (CxParseBasic) result;
			} catch (Exception e) {
			}
			if (null == deleteResult || deleteResult.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}

			int rc = deleteResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(deleteResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail),0);
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
				KidFeedData tempFeed = mFeedsData.get(mFeedIndex);
				KidFeedPost feedPost = tempFeed.getPost();
				List<KidFeedReply> tempReplies = feedPost.getReplays();
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
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic deleteResult = null;
			try {
				deleteResult = (CxParseBasic) result;
			} catch (Exception e) {
			}
			CxLog.i("CxKidFragment_men", deleteResult.getRc()+"");
			if (null == deleteResult || deleteResult.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = deleteResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(deleteResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(deleteResult.getMsg(),0);
				}
				return rc;
			}
			// 以下是删除成功
			mFeedsData.remove(mLocation);

			updateListview.sendEmptyMessage(1);

			return 0;
		}
	}

	
	
	
	class CurrentObserver extends CxObserverInterface {

		/* 主要事项有：1、新的分享 2、头像（中、小） */
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}
			CxKidParam param = CxKidParam.getInstance();
			//TODO:需要修改
			if (actionTag.equalsIgnoreCase(CxGlobalParams.KID_TIPS)) { // 对方发帖子成功时long polling告诉更新
				int group = CxGlobalParams.getInstance().getKid_tips();
				if(group>0){
					if (isFirstComplete && isDownComplete) {
						isDownComplete = false;
						//TODO:要修改
						CxKidApi.getInstance().requestFeedList(0, 15,
								new FeedListResponse(true, true), getActivity());
						
					}
				}
				return;
			}
			
			if (CxKidParam.KID_ADD_DATA.equalsIgnoreCase(actionTag)) { // 自己发帖子成功
				CxLog.i("999999", " has receive notify for new feed");
				
				CxKidParser sendParser = new CxKidParser();
				CxKidFeed sendResult = null;
				JSONObject jObj=null;
				
				String result=param.getmFeedsData();
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
				sendResult=sendParser.getAddFeedResult(jObj);
				
				
				if (null == mFeedsData) {
					mFeedsData = new ArrayList<KidFeedData>();   
					mFeedsData.add(sendResult.getData());
					mAdapter.updataView(mFeedsData);
					sendResult=null;
					CxLog.i("source data is null",
							" has receive notify for new feed"); 
					return;
				} else {
					List<KidFeedData> targetData = new ArrayList<KidFeedData>();
					targetData.add(sendResult.getData());
					targetData.addAll(mFeedsData);
					mFeedsData = targetData;
					mAdapter.updataView(mFeedsData);
					sendResult=null;
					CxLog.i("source data is not null"," has receive notify for new feed");
				}
				return;
			}
		}
		
	}
	
	
	
	
	/*************************************** 语音回复 *******************************************************/

	private Timer mRecordTimer;
	private TimerTask mRecordTask;
	private static int mReocrdCount = 0;
	private long mRecorderStopTime;
	private Timer mTimer;
	private TimerTask mTask = null;
	
	private RecMicToMp3 mRecMicToMp3;
	private long mRecorderStartTime;
	private String mSoundFilePath;

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
			du.getSimpleDialog(getActivity(), null,getResources().
					getString(R.string.cx_fa_kids_home_record_sd_fail),null, null).show();
			return;
		}

		try {
			File storageDirectory = new File(
					CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH,
					CxGlobalConst.S_CHUXIN_AUDIO_CACHE_NAME);
			boolean cacheable = CxBaseDiskCache.createDirectory(storageDirectory);
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
		mRecordRemainTimeTextView.setText(String.format(getResources().
				getString(R.string.cx_fa_chat_record_time_remianing),
				(90 - (System.currentTimeMillis() - mRecorderStartTime) / 1000)));
	}

	private void startTimer() {
		if (null == mRecordTimer) {
			mRecordTimer = new Timer();
		}
		if (null == mRecordTask) {
			mRecordTask = new TimerTask() {

				@Override
				public void run() {
					android.os.Message message = android.os.Message.obtain(mKidHandler, UPDATE_RECORD_TIME);
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

	private void deleteFile(String path) {
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
//			android.os.Message message = android.os.Message.obtain(mKidHandler,
//					UPDATE_RECORD_TIME);
//			 message.sendToTarget();
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
		int mDeviceWidth = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		if (y > 0 || x < 0 || x >= mDeviceWidth) {
			android.os.Message msg = android.os.Message.obtain(mKidHandler,SEND_RECORD);
			msg.sendToTarget();
		} else {
			// delete audio file
			deleteFile(mSoundFilePath);
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
							mKidHandler, STOP_READ_RECORD);
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
		
	}

	private void stopRecordAnimation(ImageView image) {
		if (null != mVoiceAd && mVoiceAd.isRunning()) {
			mVoiceAd.stop();
			mVoicePlayFlag = false;
			image.setImageResource(R.drawable.chat_voice3);
		}
	}

	private int mRetryCount = 3; // 重试3次下载

	public String getAudioFile(final String url, final int audioLength,
			final ImageView image, final ProgressBar progress) {
		if ((null == url) || (url.equals("null"))) { // 避免服务器返回"null"
			// RkLog.i(TAG, " param url is null");
			return "";
		}

		final CxAudioFileResourceManager resourceManager = CxAudioFileResourceManager
				.getAudioFileResourceManager(CxKidFragment.this.getActivity());

		if (resourceManager.exists(Uri.parse(url))) {
			// RkLog.i(TAG, "file path local=");
			final File file = resourceManager.getFile(Uri.parse(url));
			CxLog.i("getAudioFile", "filepath0=" + file.getAbsolutePath());
			new Handler(getActivity().getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					preparePlayVoice(file.getAbsolutePath(), audioLength, image);
				}
			}.sendEmptyMessage(1);
			return file.getAbsolutePath();
		} else {
			// checkSdCardExist();
			new Handler(getActivity().getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					image.setVisibility(View.GONE);
					progress.setVisibility(View.VISIBLE);
				}
			}.sendEmptyMessage(1);

			// RkLog.i(TAG, "ready to net download");
			resourceManager.addObserver(new CxAudioFileResourceManager
					.ResourceRequestObserver(Uri.parse(url)) {
				@Override
				public void requestReceived(Observable observable,
						Uri uri, long len) {
					observable.deleteObserver(this);
					try {
						mRetryCount--;
						File file = resourceManager.getFile(uri);
//						RkLog.i("getAudioFile", "contentlength>>>"
//								+ len);
//						RkLog.i("getAudioFile",
//								"file.length>>>" + file.length());
//						RkLog.i("getAudioFile", "audiolength>>>"
//								+ audioLength);

						if (file.length() != len) {
							CxLog.i("getAudioFile","not download file complete, need to reload");
							file.delete();
							// 重试3次，失败了给出提示
							if (mRetryCount > 0) {
								getAudioFile(url, audioLength, image,progress);
							} else {
								new Handler(getActivity().getMainLooper()) {
									public void handleMessage(android.os.Message msg) {
										progress.setVisibility(View.GONE);
										image.setVisibility(View.VISIBLE);
									}
								}.sendEmptyMessage(1);

								ToastUtil.getSimpleToast(getActivity(),-1,
									getString(R.string.cx_fa_not_download_file_complete),1).show();
							}
						} else {
							CxLog.i("getAudioFile","filepath1="+ file.getAbsolutePath());
							final String audioFilePath = file.getAbsolutePath();
							CxLog.i("getAudioFile", "audioFilePath="+ audioFilePath);
							new Handler(getActivity().getMainLooper()) {
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
    // 更新home按钮状态
    public void updateHomeMenu(){
        if(CxGlobalParams.getInstance().getGroup() > 0 || CxGlobalParams.getInstance().getSpaceTips() > 0
        		|| CxGlobalParams.getInstance().getKid_tips() > 0){
        	menuBtn.setBackgroundResource(R.drawable.navi_home_new_btn);
        } else {
        	menuBtn.setBackgroundResource(R.drawable.cx_fa_menu_btn);
        }
    }
	
	Dialog dlg;
	
	class ShareThirdSender implements JSONCaller{

    	private KidFeedData mFeedData;
    	private Activity mActivity;
    	
    	public ShareThirdSender(KidFeedData feedData, Activity activity){
    		this.mFeedData = feedData;
    		this.mActivity = activity;
    		ShareSDK.initSDK(mActivity);
    	}
    	
		@Override
		public int call(Object result) {
			try {
				new Handler(mActivity.getMainLooper()){
					public void handleMessage(Message msg) {
						try {
							dlg.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (null == result) {
				shareFailTipNotice(mActivity);
				return -1;
			}
			CxShareThdRes tempRes = null;
			try {
				tempRes = (CxShareThdRes)result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == tempRes) {
				shareFailTipNotice(mActivity);
				return -2;
			}
			if (0 != tempRes.getRc()) {
				shareFailTipNotice(mActivity);
				return -3;
			}
			String chuxinUrl = tempRes.getData();
			if (null == chuxinUrl) {
				shareFailTipNotice(mActivity);
				return -4;
			}
			
			if (null == mActivity) {
				shareFailTipNotice(mActivity);
				return -1;
			}
			
			String commentStr = null;
			String firstImgUrl = null;
			try {
				commentStr = mFeedData.getPost().getText();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				firstImgUrl = mFeedData.getPost().getPhotos().get(0).getBig();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (TextUtils.isEmpty(commentStr)) {
				//标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
				commentStr = "来自小家APP";
			}
			
			share(mActivity, commentStr, chuxinUrl, firstImgUrl);
			
			return 0;
		}
    	
    }
	
	
	

	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;
	private LinearLayout mRecordView;
	private AudioManager mAudioManager;
	private InputMethodManager input;
	private Button menuBtn;
	private CurrentObserver mServiceObserver;
	public static Handler mKidHandler;

//	private LinearLayout noFeedLayout;
	
	@Override
	public boolean handleMessage(Message msg) {
		if (null == getActivity()) {
			return true;
		}
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: {
				// 成功
				showNotification(2000,
				        CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_third_share_success));
			}
				break;
			case 2: {
				// 失败
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)
						|| "WechatTimelineNotSupportedException".equals(expName)) {
					showNotification(2000, CxApplication.getInstance().getApplicationContext().getString(
							R.string.wechat_client_inavailable));
				} else {
					showNotification(2000,
					        CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_third_share_fail));
				}
			}
				break;
			case 3: {
				// 取消
				showNotification(2000,
				        CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_third_share_cancel));
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
		if (null == getActivity()) {
			return;
		}
		try {
			Context app = getActivity().getApplicationContext();
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
//				UIHandler.sendMessageDelayed(msg, cancelTime, this);
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
		new Handler(CxApplication.getInstance().getMainLooper()) {
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
				ToastUtil.getSimpleToast(getActivity(), id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	
	private void shareFailTipNotice(final Activity activity){
    	
    	if (null == activity) {
			return;
		}
    	new Handler(activity.getMainLooper()){
			public void handleMessage(Message msg) {
				Toast.makeText(activity, getString(
						R.string.share_failed), Toast.LENGTH_SHORT).show();
			};
		}.sendEmptyMessage(0);
    }
	
	private void share(Activity activity,String comment,
    		String chuxinOpenUrl, String imageUrl){
    	final OnekeyShare oks = new OnekeyShare();
    	oks.setNotification(R.drawable.cx_fa_app_icon, 
				activity.getString(R.string.cx_fa_role_app_name));
		oks.setTitle(comment);
		oks.setUrl(chuxinOpenUrl);
		oks.setTitleUrl(chuxinOpenUrl);
		oks.setText(comment);

		if (!TextUtils.isEmpty(imageUrl)) {
			oks.setImageUrl(imageUrl);
		}
		
		oks.setSilent(true);
		oks.setSite("小家");
		oks.setSiteUrl(chuxinOpenUrl);

		// 去除注释，可令编辑页面显示为Dialog模式
//		oks.setDialogMode();

		// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
		oks.setCallback(new OneKeyShareCallback(activity));
		
		//对特殊平台定制需要发送的内容
//		oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());

		oks.show(activity);
    }


}
