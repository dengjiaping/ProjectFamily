package com.chuxin.family.zone;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.OneKeyShareCallback;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.ant.liao.chuxin.EnhancedGifView;
import com.chuxin.family.accounting.CxChangeAccountActivity;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.gallery.CxGalleryActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.mate.CxFamilyInfoActivity;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.neighbour.CxNbOurHome;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.CxZoneApi;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxSettingsParser.SendHeadImageType;
import com.chuxin.family.parse.been.CxChangeBgOfZone;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.parse.been.CxReply;
import com.chuxin.family.parse.been.CxShareThdRes;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.CxZoneFeedList;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.parse.been.data.FeedPost;
import com.chuxin.family.parse.been.data.FeedReply;
import com.chuxin.family.parse.been.data.CxChangeHeadDataField;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.service.CxServiceParams;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxResourceManager;
import com.chuxin.family.utils.CxShareUtil;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.simonvt.menudrawer.CxBaseSlidingMenu;
/**
 * 二人空间
 * @author shichao.wang
 * 备注：首先加载本地数据，再去网络
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class CxUsersPairZone extends Fragment implements OnKeyListener{

	float mSmallImgConner = 0, mMateSmallImgConner = 0;
	
	@Override
	public void onAttach(Activity activity) {
		mSmallImgConner = activity.getResources().getDimension(
				R.dimen.cx_fa_my_small_img_conner);
		mMateSmallImgConner = activity.getResources().getDimension(
				R.dimen.cx_fa_mate_small_img_conner);
		ShareSDK.initSDK(activity);
		
		super.onAttach(activity);
	}

	private ScrollableListView mZoneContent;
	private LinearLayout mSendReplyLayer;
	
	private ZoneAdapter mZoneAdapter;
	private List<FeedListData> mFeedsData; //帖子的数据
	
	private InputMethodManager input;
//	private boolean mCommentType = false; //评论种类：false是文字，true是表情
	private String mReplyFeedId; //评论的帖子ID
	private String mReply_to; //回复给的对象（评论时为null,回复时为对方的ID）
	private int mReplyIndex; //需要删除的回复
	private int mFeedIndex; //需要删除的回复所在的帖子在数据源中的位置
	
//	private int mLastItemData; // add by shichao.wang 显示的最后一条数据
	
	public static String RK_CURRENT_VIEW = "RkUsersPairZone"; 
	
	private boolean isFirstComplete = false; //首次网络请求结束的标识位，完成为true，默认未完成false
	private boolean isDownComplete = true; //向下拉完成的标识位。默认为true表示完成
	private boolean isUpComplete = true; //向上推完成的标识位。默认为true表示完成
	
	private CurrentObserver mServiceObserver;
	private CurrentObserver mGlobalObserver;
	private CurrentObserver mAddFeedObserver;
	
	private CxInputPanel mInputPanel; // inputpanel 引用
	private ImageButton mAddButton; // inputpanel中的添加按钮
	private ImageButton mSendButton; // inputpanel中的录音和发送按钮
	private EditText contentOfSend; //文字信息输入框
	private TextView mNoFeed;//没有内容时的提示信息
	private static ImageButton mMenuBtn;
	private ImageButton mShareImages, mShareText;
	
	private AlertDialog mEditTogetherDate;
	private Button saveEditDateBtn, addYearBtn, reduceYearBtn,addMonthBtn, 
	reduceMonthBtn, addDayBtn, reduceDayBtn;
	
	private TextView yearView, monthView, dayView;
	
	private TextView togetherDaysView;
	
	public static final int UPDATE_HOME_MENU = 5; // 更新home按钮未读消息状态
	    
    public static Handler mRkUserPairZone = null;
    
   
    @Override
    public void onStart() {
        super.onStart();
        CxInputPanel.sInputPanelUse = RK_CURRENT_VIEW;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		input = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		super.onCreate(savedInstanceState);
		
		mRkUserPairZone = new Handler(){

	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            switch(msg.what){
	                case UPDATE_HOME_MENU:
	                	if(CxUsersPairZone.this.isVisible()){
	                		updateHomeMenu();
	                	} 
	                    break;
	            }
	        }
	        
	    };
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View tempView = inflater.inflate(R.layout.cx_fa_fragment_zone, null);
		
		mMenuBtn = (ImageButton)tempView.findViewById(R.id.zoneMenuButton);
        // add by shichao 20131024
		updateHomeMenu();
		mShareImages = (ImageButton)tempView.findViewById(R.id.share_iamges_btn);
		mShareText = (ImageButton)tempView.findViewById(R.id.share_text_feed);
		
		mNoFeed = (TextView) tempView.findViewById(R.id.cx_fa_zone_no_feed);
		
		mMenuBtn.setOnClickListener(titleBtnClick);
		mShareImages.setOnClickListener(titleBtnClick);
		mShareText.setOnClickListener(titleBtnClick);
		
		mInputPanel = (CxInputPanel) tempView.findViewById(R.id.cx_fa_widget_input_layer);
		CxInputPanel.sInputPanelUse = RK_CURRENT_VIEW;
		mAddButton = (ImageButton) mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout1_button1);
		mAddButton.setVisibility(View.GONE);
		mSendButton = (ImageButton) mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout1_button3);
		mSendButton.setImageResource(R.drawable.chat_buttonsend);
		mSendReplyLayer = (LinearLayout)tempView.findViewById(R.id.cx_fa_zone_send_reply); 
		mSendReplyLayer.setVisibility(View.GONE);
		contentOfSend = (EditText)mInputPanel.findViewById(
				R.id.cx_fa_widget_input_panel__layout1_textedit1);
		
//		mCommentInputEditer.setOnKeyListener(this);
		
		mZoneContent = (ScrollableListView)tempView.findViewById(R.id.zoneListView);
		
		//设置观察者
		CxServiceParams mZoneParam = CxServiceParams.getInstance(); //获取model的subject实例
		mServiceObserver = new CurrentObserver(); //生成观察者实例
		//设置观察目标
		List<String> tags = new ArrayList<String>();
		tags.add(CxServiceParams.SPACE_TS); //新的分享资料
		mServiceObserver.setListenTag(tags); //设置观察目标
		mServiceObserver.setMainThread(true); //设置在UI线程执行
		mZoneParam.registerObserver(mServiceObserver); //注册观察者
		
		mGlobalObserver = new CurrentObserver(); //生成观察者实例
		//设置观察目标
		List<String> globalTags = new ArrayList<String>();
		globalTags.add(CxGlobalParams.ICON_SMALL); //自己头像（中、小两种，只注册一种的原因在于：改头像后都会改变大、中、小）
//		globalTags.add(RkGlobalParams.ICON_MIDDLE);
		globalTags.add(CxGlobalParams.PARTNER_ICON_BIG);//对方头像（中、小）
		globalTags.add(CxGlobalParams.ZONE_BACKGROUND); //二人空间背景
		globalTags.add(CxGlobalParams.PARTNER_NAME); //对方名称
		mGlobalObserver.setListenTag(globalTags); //设置观察目标
		mGlobalObserver.setMainThread(true); //设置在UI线程执行
		CxGlobalParams.getInstance().registerObserver(mGlobalObserver); //注册观察者
		
		//自己发帖子成功的监听
		mAddFeedObserver = new CurrentObserver();
		List<String> feedTags = new ArrayList<String>();
		feedTags.add(CxZoneParam.FEED_DATA);
		mAddFeedObserver.setListenTag(feedTags);
		mAddFeedObserver.setMainThread(true);
		CxZoneParam.getInstance().registerObserver(mAddFeedObserver);
		
		mZoneAdapter = new ZoneAdapter();
		mZoneContent.setAdapter(mZoneAdapter);
		mZoneContent.setOnTouchListener(new OnTouchListener(
				) {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputPanel.setDefaultMode();
				mInputPanel.setVisibility(View.GONE);
				return false;
			}
		});
		mZoneContent.setOnHeaderRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if (isFirstComplete && isDownComplete) {
					isDownComplete = false;
					CxZoneApi.getInstance().requestFeedList(0, 15, 
							new FeedResponse(true, false), getActivity());
				}else{
					new Handler(getActivity().getMainLooper()){
						public void handleMessage(android.os.Message msg) {
							mZoneContent.onRefreshComplete();
//							mZoneContent.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
						};
					}.sendEmptyMessageDelayed(1, 500);
				}
				
			}
		});

		mZoneContent.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() { 	
            	
            	if (isUpComplete) {
					isUpComplete = false;
					int offest = (null == mFeedsData ? 0 : mFeedsData.size());
					CxZoneApi.getInstance()
							.requestFeedList(offest, 15,
									new FeedResponse(false, false),
									getActivity());
				} else {
					mZoneContent.refreshComplete();
				}
            }
        });
		// 面板事件添加 add by shichao 20130708
		mInputPanel.setOnEventListener(new OnEventListener() {
			
			@Override
			public void onStopMoveEvent(View v, MotionEvent m) {
				
			}
			
			@Override
			public void onStartRecordEvent(View v, MotionEvent m) {
				
			}
			
			@Override
			public int onMessage(String msg, int flag) {
				if(flag == 0){
				// 发普通文字
						if (TextUtils.isEmpty(msg)) {
							Toast noContentTip = Toast.makeText(getActivity(), 
									getString(R.string.cx_fa_zone_no_content), Toast.LENGTH_LONG);
							noContentTip.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							noContentTip.show();
							return 0;
						}
						try {
						CxZoneApi.getInstance().requestReply(mReplyFeedId, msg, 
								mReply_to, null, mSendCommentCallback);
						//RkMateParams.getInstance().getMateUid()不传，这只是发表评论，不是回复
//						RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%call");
						//关闭输入区
						mInputPanel.setDefaultMode();
						mInputPanel.setVisibility(View.GONE);
						return 0;
					} catch (Exception e) {
						e.printStackTrace();
						return 1;
					}
				} else if(flag == 2) {
					// 表情发送
					try {
						CxZoneApi.getInstance().requestReply(mReplyFeedId, null, 
								mReply_to, msg, mSendCommentCallback);
						//RkMateParams.getInstance().getMateUid()不传，这只是发表评论，不是回复
						//关闭输入区
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
			public void onAcionMoveEvent(View v, MotionEvent m) {
				
			}

            @Override
            public void onOtherEvent(View v, MotionEvent m) {
                
            }

            @Override
            public void onButton0Click(View button) {
                
            }
		});
		
		//加载本地数据(本地保存15条，不至于会超时）
		CxZoneCacheData cacheData = new CxZoneCacheData(getActivity());
		List<FeedListData> feeds = cacheData.queryCacheData();
		if ( (null != feeds) && (feeds.size() > 0) ) {
			mZoneAdapter.updataView(feeds);
		}
		
		//加载本地数据后首先到网络获取第一屏数据
		isFirstComplete = false;
		CxZoneApi.getInstance().requestFeedList(0, 15, 
				new FeedResponse(true, true), getActivity());
		
		((CxMain)getActivity()).closeMenu();
		
		return tempView;
	}
	
	OnClickListener titleBtnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.zoneMenuButton:   
				try {
					((CxMain)getActivity()).toggleMenu();
				} catch (Exception e) {
				}
				break;
			case R.id.share_iamges_btn:
				/*//启动二人空间发帖子界面
				Intent toAddFeed = new Intent(getActivity(), RkZoneAddFeed.class);
	            toAddFeed.putExtra(RkGlobalConst.S_ZONE_SHARED_TYPE, 1);
//	            toAddFeed.putExtra(RkGlobalConst.S_ZONE_SHARED_IMAGE, imagePath);
	            startActivity(toAddFeed);*/ //预改模式
				//再启动拍照
				Intent changeChatBackground = new Intent(getActivity(), ActivitySelectPhoto.class);
	            ActivitySelectPhoto.kIsCallPhotoZoom = false;
	            ActivitySelectPhoto.kIsCallFilter = true;
	            ActivitySelectPhoto.kIsCallSysCamera = false;
	            ActivitySelectPhoto.kChoseSingle = false;
	            ActivitySelectPhoto.kFrom = "RkUserPairZone";
//	            startActivityForResult(changeChatBackground, ADD_ZONE_FEED);			
				startActivity(changeChatBackground);
				/*Intent test = new Intent(getActivity(), CxGalleryActivity.class);
				startActivity(test);*/
				break;
			case R.id.share_text_feed:
				Intent toShareText = new Intent(getActivity(), CxZoneAddFeed.class);
                toShareText.putExtra(CxGlobalConst.S_ZONE_SHARED_TYPE, 0);
                startActivity(toShareText);
                getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			default:
				break;
			}
			
		}
	};
	
	//空间列表请求的网络应答
	class FeedResponse implements JSONCaller{

		private boolean isPushDown = true; //默认向下拉
		private boolean isFirst = false; //默认不是第一次获取空间资源
		
		public FeedResponse(boolean pushDown, boolean first){
			this.isFirst = first;
			this.isPushDown = pushDown;
		}
		
		@Override
		public int call(Object result) {
			if (isFirst) { //将首次刷新界面结束的标识位置为true
				isFirstComplete = true;
			}
			
			if (isPushDown) { //往下拉(或者push)
				isDownComplete = true;
			}else{
				isUpComplete = true;
			}
			
			if (null == getActivity()) {
				return -1;
			}
			
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(android.os.Message msg) {
					if (isPushDown) {
						mZoneContent.onRefreshComplete();
					}else{
						mZoneContent.refreshComplete();
					}
					
				};
			}.sendEmptyMessage(1);
			
			if (null == result) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxZoneFeedList feedList = null;
			try {
				feedList = (CxZoneFeedList)result;
			} catch (Exception e) {
			}
			if (null == feedList || feedList.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = feedList.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(feedList.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
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
				
				mFeedsData = feedList.getData();
				updateListview.sendEmptyMessage(1);
				
				CxGlobalParams.getInstance().setSpaceTips(0);// 进入二人空间获取数据后，未读消息数置空  by jianyin.du 20131123
				CxServiceParams.getInstance().setSpace_tips(0);
				Message nbMsg = CxUsersPairZone.mRkUserPairZone.obtainMessage(CxUsersPairZone.UPDATE_HOME_MENU);
	            nbMsg.sendToTarget();
	            if(null != CxBaseSlidingMenu.mBaseSlidingMenuHandler){
	                Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler.obtainMessage(CxBaseSlidingMenu.UPDATE_SPACE_UNREAD_MESSAGE);
	                baseMsg.sendToTarget();
	            }	
	            
				return 0;
			}
			
			
			CxGlobalParams.getInstance().setSpaceTips(0);// 进入二人空间获取数据后，未读消息数置空  by jianyin.du 20131123
			CxServiceParams.getInstance().setSpace_tips(0);
			Message nbMsg = CxUsersPairZone.mRkUserPairZone.obtainMessage(CxUsersPairZone.UPDATE_HOME_MENU);
            nbMsg.sendToTarget();
            if(null != CxBaseSlidingMenu.mBaseSlidingMenuHandler){
                Message baseMsg = CxBaseSlidingMenu.mBaseSlidingMenuHandler.obtainMessage(CxBaseSlidingMenu.UPDATE_SPACE_UNREAD_MESSAGE);
                baseMsg.sendToTarget();
            }
            
			/*if (null != mFeedsData) {
				mFeedsData.clear();
				mFeedsData = null;
			}*/
			
			if (isPushDown) { //往下拉(或者push)
				
				mFeedsData = feedList.getData(); //直接换成最新数据即可
				
			}else{ //往上翻
				if ( (null == feedList.getData()) 
						|| (feedList.getData().size() < 1) ) {
					//没有数据
					return 0;
				}
				if (null == mFeedsData) {
					mFeedsData = new ArrayList<FeedListData>();
				}
				mFeedsData.addAll(feedList.getData());
			}
			updateListview.sendEmptyMessage(1);
			return 0;
		}
		
	}
	
	Handler updateListview = new Handler(){
		public void handleMessage(android.os.Message msg) {
			mZoneAdapter.updataView(mFeedsData);
		};
	};
	
	//评论帖子的回调
	JSONCaller mSendCommentCallback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback start");
			if (null == result) {
				//提示评论失败
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxReply replyResult = null;
			try {
				replyResult = (CxReply)result;
			} catch (Exception e) {
			}
			if (null == replyResult || replyResult.getRc()==408) {
				//提示评论失败
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			int rc = -1;
			try {
				rc = replyResult.getRc();
			} catch (Exception e) {
			}
			if (-1 == rc) {
				//提示评论失败
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -3;
			}
			
			if (0 != rc) {
				if(TextUtils.isEmpty(replyResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(replyResult.getMsg(),0);
				}
				return rc;
			}
			FeedReply reply = replyResult.getData();
			if (null == reply) {
				return 0;
			}
			//在帖子列表中插入数据，更新列表
//			FeedListData 
			if ( (null == mFeedsData) || (mFeedsData.size() < 1) ) { //属于异常(没有帖子数据就没有回复的动作）
				return -1;
			}
			int tempSize = mFeedsData.size();
			for(int i = 0; i < tempSize; i++){
				FeedListData tempFeed = mFeedsData.get(i);
				
				if (TextUtils.equals(reply.getFeed_id(), tempFeed.getId())) { //是这个帖子的回复
					FeedPost tempPost = tempFeed.getPost();
					if (null == tempPost) { //容错处理：帖子内存不存在 （严格来讲，这样的情况属于异常）
						tempPost = new FeedPost();
						List<FeedReply> targetReply = new ArrayList<FeedReply>();
						targetReply.add(reply);
//						tempPost.setReplays(targetReply);
						mFeedsData.get(i).getPost().setReplays(targetReply);
					}else{ //正常情况
						List<FeedReply> targetReply = new ArrayList<FeedReply>();
						targetReply.add(reply);
						if (null != tempPost.getReplays()) {
							targetReply.addAll(tempPost.getReplays());
						}
						mFeedsData.get(i).getPost().setReplays(targetReply);
					}
//					RkLog.i("111", "%%%%%%%%%%%%%%%%%%%%%%%callback end");
					updateListview.sendEmptyMessage(1);
					break;
				}
				
			} //end for(i)
			
			return 0;
		}
	};
	
	@SuppressLint("HandlerLeak")
	private JSONCaller updateTogether = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {
				return -1;
			}
			CxUserProfile userProfile = null;
			try {
				userProfile = (CxUserProfile)result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (null == userProfile) {
				return -2;
			}
			
			if (0 != userProfile.getRc()) {
				return userProfile.getRc();
			}
			
			if (null == userProfile.getData()){
				return -4;
			}
			
			if (null == userProfile.getData().getTogetherDay()) {
				return -3;
			}
			
			CxGlobalParams.getInstance().setTogetherDayStr(userProfile.getData().getTogetherDay());
			updateListview.sendEmptyMessage(1);
			
			return 0;
		}
	};
	
	private OnClickListener editDateListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.cx_fa_save_edit_date:
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					String editDate = yearView.getText().toString()
					+monthView.getText().toString()+dayView.getText().toString();
					String nowStr = sdf.format(new Date());
					if((sdf.parse(editDate).getTime() - sdf.parse(nowStr).getTime()) > 0){
						if (null != CxGlobalParams.getInstance().getTogetherDayStr()) {
							yearView.setText(CxGlobalParams.getInstance().getTogetherDayStr().substring(0, 4));
							monthView.setText(CxGlobalParams.getInstance().getTogetherDayStr().substring(4, 6));
							dayView.setText(CxGlobalParams.getInstance().getTogetherDayStr().substring(6));
						}else{
							yearView.setText(nowStr.substring(0, 4));
							monthView.setText(nowStr.substring(4, 6));
							dayView.setText(nowStr.substring(6));
						}
						Toast.makeText(getActivity(), "你设定的时间不能比当前时间大："+editDate, Toast.LENGTH_SHORT).show();
						CxLog.w("you set time for together is more :", ""+editDate);
						return;
					}
					
					if ((null != mEditTogetherDate) && (mEditTogetherDate.isShowing()) ) {
						mEditTogetherDate.dismiss();
					}
					//提交在一起修改的请求
					UserApi.getInstance().updateUserProfile(editDate, null, null, null, null, updateTogether);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			case R.id.cx_fa_add_year:
				String yearStr = yearView.getText().toString();
				yearView.setText(""+(Integer.parseInt(yearStr)+1));
				break;
			case R.id.cx_fa_reduce_year:
				String yearStrRe = yearView.getText().toString();
				yearView.setText(""+(Integer.parseInt(yearStrRe)-1));
				break;
			case R.id.cx_fa_add_month:
				String monthStrAdd = monthView.getText().toString();
				int monthIntAdd = 1;
				try {
					monthIntAdd = Integer.parseInt(monthStrAdd);
				} catch (Exception e) {
					e.printStackTrace();
				}
				monthIntAdd++;
				if (1 > monthIntAdd) {
					return;
				}
				if (12 < monthIntAdd) {
					return;
				}
				if (10 > monthIntAdd) {
					monthStrAdd = "0"+monthIntAdd;
				}else{
					monthStrAdd = ""+monthIntAdd;
				}
				monthView.setText(monthStrAdd);
				dayView.setText("15");
				break;
			case R.id.cx_fa_reduce_month:
				String monthStrRe = monthView.getText().toString();
				int monthIntRe = 1;
				try {
					monthIntRe = Integer.parseInt(monthStrRe);
				} catch (Exception e) {
					e.printStackTrace();
				}
				monthIntRe--;
				if (1 > monthIntRe) {
					return;
				}
				if (12 < monthIntRe) {
					return;
				}
				if (10 > monthIntRe) {
					monthStrRe = "0"+monthIntRe;
				}else{
					monthStrRe = ""+monthIntRe;
				}
				monthView.setText(monthStrRe);
				dayView.setText("15");
				break;
			case R.id.cx_fa_add_day:
				String dayStrAdd = dayView.getText().toString();
				int dayInt = 1;
				try {
					dayInt = Integer.parseInt(dayStrAdd);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dayInt++;
				
				boolean normalAdd = false;
				try {
					normalAdd = isNormalDate(yearView.getText().toString(), 
							monthView.getText().toString(), dayInt);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!normalAdd) {
					return;
				}
				if (dayInt < 10) {
					dayStrAdd = "0"+dayInt;
				}else{
					dayStrAdd = ""+dayInt;
				}
				
				dayView.setText(dayStrAdd);
				break;
			case R.id.cx_fa_reducce_day:
				String dayStrRe = dayView.getText().toString();
				int dayIntRe = 1;
				try {
					dayIntRe = Integer.parseInt(dayStrRe);
				} catch (Exception e) {
					e.printStackTrace();
				}
				dayIntRe--;
				
				boolean normal = false;
				try {
					normal = isNormalDate(yearView.getText().toString(), 
							monthView.getText().toString(), dayIntRe);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!normal) {
					return;
				}
				
				if (dayIntRe < 10) {
					dayStrRe = "0"+dayIntRe;
				}else{
					dayStrRe = ""+dayIntRe;
				}
				
				dayView.setText(dayStrRe);
				break;

			default:
				break;
			}

		}
	};

	//item的adapter
	class ZoneAdapter extends BaseAdapter{
		private final int HEAD_VIEW = 0;
		private final int ITEM_VIEW = 1;

		private List<FeedListData> mAdapterData;
		
		public synchronized void updataView(List<FeedListData> adapterData){
			
			mAdapterData = adapterData;
			
			ZoneAdapter.this.notifyDataSetChanged();
		}
		
		private String filterStartZero(String stamp){
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
//			RkLog.i("getCount", ""+ (null == mAdapterData ? 1 : (mAdapterData.size()+1) ) );
			
			if(null == mAdapterData || mAdapterData.size()==0){
				mNoFeed.setVisibility(View.VISIBLE);
			}else{
				mNoFeed.setVisibility(View.GONE);
			}
			
			
			if (null == mAdapterData) {	
				return 1;
			}
			
			
			return mAdapterData.size()+1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("Recycle")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CxLog.i("Adapter getView", ""+position);
			
			if (0 == position) {
				if (null == convertView) {
					convertView = getActivity().getLayoutInflater().inflate(
							R.layout.cx_fa_fragment_zone_header, null);
				}
				//加载二人空间的背景图、对方头像，自己的头像
				CxImageView zoneBackground = (CxImageView)convertView.findViewById(R.id.cx_fa_zone_bg);
				ViewGroup.LayoutParams param = zoneBackground.getLayoutParams();
				param.height = (int)(getResources().getDisplayMetrics().widthPixels * 0.66f + 0.5f);
				zoneBackground.setLayoutParams(param);
				zoneBackground.displayImage(ImageLoader.getInstance(),CxGlobalParams.getInstance().getZoneBackground(), 
						CxResourceDarwable.getInstance().dr_zone_defaultimage, false, 0);
//				zoneBackground.setImage(RkGlobalParams.getInstance().getZoneBackground(), 
//						false, 260, RkUsersPairZone.this, "zone_bg", RkUsersPairZone.this.getActivity());
				
				// 自己的头像
				CxImageView mMiddleIcon = (CxImageView)convertView.findViewById(R.id.myHeadView);
//				mMiddleIcon.setImageResource(R.drawable.cx_fa_wf_small_icon);	
//				mMiddleIcon.setImage(RkGlobalParams.getInstance().getIconMid(), 
//							false, 100, RkUsersPairZone.this, "head", RkUsersPairZone.this.getActivity());
				mMiddleIcon.displayImage(ImageLoader.getInstance(), 
						CxGlobalParams.getInstance().getIconMid(), 
						CxResourceDarwable.getInstance().dr_zone_icon_small_me, true, (int)mSmallImgConner);
				// 对方的头像
				CxImageView mateMiddleIcon = (CxImageView)convertView.findViewById(R.id.mateHeadView);
//				mateMiddleIcon.setImageResource(R.drawable.cx_fa_hb_small_icon);
//				mateMiddleIcon.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), 
//						false, 100, RkUsersPairZone.this, "head", RkUsersPairZone.this.getActivity());
				mateMiddleIcon.displayImage(ImageLoader.getInstance(), 
						CxGlobalParams.getInstance().getPartnerIconBig(), 
						CxResourceDarwable.getInstance().dr_zone_icon_small_oppo, true, (int)mMateSmallImgConner);
				
				TextView mWithMe = (TextView)convertView.findViewById(R.id.mateWithMe);
				mWithMe.setText(CxResourceString.getInstance().str_zone_mate_with_me);
				
				//在一起的天数
				togetherDaysView = (TextView)convertView.findViewById(R.id.pairSpan);
				LinearLayout modifySpans = (LinearLayout)convertView.findViewById(R.id.cx_fa_modify_together_days);
				modifySpans.setOnClickListener(new OnClickListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(View arg0) {
						//修改在一起的日期
						mEditTogetherDate = new AlertDialog.Builder(getActivity()).create();
						mEditTogetherDate.show();
						
						Window win = mEditTogetherDate.getWindow();
						WindowManager.LayoutParams winParam = win.getAttributes();
						winParam.x=0;
						winParam.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
						mEditTogetherDate.onWindowAttributesChanged(winParam);
						
						Display dis = getActivity().getWindowManager().getDefaultDisplay();
						int width = dis.getWidth();
						LayoutParams lp = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
						
						View contentView = LayoutInflater.from(getActivity()).inflate(
								R.layout.cx_fa_view_zone_together_dialog, null);
						
						saveEditDateBtn = (Button)contentView.findViewById(R.id.cx_fa_save_edit_date);
						addYearBtn = (Button)contentView.findViewById(R.id.cx_fa_add_year);
						reduceYearBtn = (Button)contentView.findViewById(R.id.cx_fa_reduce_year);
						addMonthBtn = (Button)contentView.findViewById(R.id.cx_fa_add_month);
						reduceMonthBtn = (Button)contentView.findViewById(R.id.cx_fa_reduce_month);
						addDayBtn = (Button)contentView.findViewById(R.id.cx_fa_add_day);
						reduceDayBtn = (Button)contentView.findViewById(R.id.cx_fa_reducce_day);
						yearView = (TextView)contentView.findViewById(R.id.cx_fa_year_info);
						monthView = (TextView)contentView.findViewById(R.id.cx_fa_month_info);
						dayView = (TextView)contentView.findViewById(R.id.cx_fa_day_info);
						
						try {
							if (null != CxGlobalParams.getInstance().getTogetherDayStr()) {
								yearView.setText(CxGlobalParams.getInstance().getTogetherDayStr().substring(0, 4));
								monthView.setText(CxGlobalParams.getInstance().getTogetherDayStr().substring(4, 6));
								dayView.setText(CxGlobalParams.getInstance().getTogetherDayStr().substring(6));
							}
							
						} catch (Exception e) {
							CxLog.i("", ""+e.toString());
						}
						
						saveEditDateBtn.setOnClickListener(editDateListener);
						addYearBtn.setOnClickListener(editDateListener);
						reduceYearBtn.setOnClickListener(editDateListener);
						addMonthBtn.setOnClickListener(editDateListener);
						reduceMonthBtn.setOnClickListener(editDateListener);
						addDayBtn.setOnClickListener(editDateListener);
						reduceDayBtn.setOnClickListener(editDateListener);
						
						mEditTogetherDate.setContentView(contentView, lp);
						
						
					}
				});
				if(null != CxGlobalParams.getInstance().getTogetherDayStr()){
					try {
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						Date spanDay = sdf.parse(CxGlobalParams.getInstance().getTogetherDayStr());
						long spanTime = (new Date().getTime()) - spanDay.getTime();
						long daysInt = 0;
						if(0 == (spanTime % CxGlobalConst.S_DAY_TIME)){
							 daysInt = spanTime / CxGlobalConst.S_DAY_TIME;
						}else{
							daysInt = spanTime / CxGlobalConst.S_DAY_TIME + 1;
						}
						
						togetherDaysView.setText(String.format(getString(
								R.string.cx_fa_together_tip), ""+daysInt));
						togetherDaysView.setVisibility(View.VISIBLE);
					} catch (Exception e) {
						CxLog.w("", ""+e.toString());
						togetherDaysView.setText(String.format(getString(
								R.string.cx_fa_together_tip), "??"));
						togetherDaysView.setVisibility(View.VISIBLE);
					}
					
				}else{
					togetherDaysView.setText(String.format(getString(
							R.string.cx_fa_together_tip), "??"));
					togetherDaysView.setVisibility(View.VISIBLE);
				}
				
				zoneBackground.setOnClickListener(changeImage);
				mMiddleIcon.setOnClickListener(changeImage);
				mateMiddleIcon.setOnClickListener(changeImage);
				return convertView; 
			}
			
			
			final int itemLocation = position - 1;
			ZoneItemViewHolder holder = null;
			if (null == convertView) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.cx_fa_fragment_zone_list_item, null);
				holder = new ZoneItemViewHolder();
				holder.commentOrReplayBtn = (ImageButton)convertView.findViewById(
						R.id.commentOrReplayBtn);
				holder.deleteRecord = (ImageButton)convertView.findViewById(R.id.deleteRecord);
				holder.speakDate = (TextView)convertView.findViewById(R.id.speakDate);
				holder.speakerHead = (CxImageView)convertView.findViewById(R.id.speakerHead);
				holder.speakerName = (TextView)convertView.findViewById(R.id.speakerName);
				holder.speakTime = (TextView)convertView.findViewById(R.id.speakTime);
				holder.speakWord = (CustomTextView)convertView.findViewById(R.id.speakWord);
				holder.commentAndReplayOfRecord = (LinearLayout)convertView.findViewById(
						R.id.commentAndReplayOfRecord);
				holder.textMore=(TextView) convertView.findViewById(R.id.space_feed_text_more);
				holder.itemLayout=(LinearLayout) convertView.findViewById(R.id.cx_fa_zone_item_layout);
				
				holder.sharedPhotos = new ArrayList<CxImageView>();
				CxImageView firstImage = (CxImageView)convertView.findViewById(R.id.cx_fa_first_9image);
				holder.sharedPhotos.add(firstImage);
				CxImageView secondImage = (CxImageView)convertView.findViewById(R.id.cx_fa_second_9image);
				holder.sharedPhotos.add(secondImage);
				CxImageView thirdImage = (CxImageView)convertView.findViewById(R.id.cx_fa_third_9image);
				holder.sharedPhotos.add(thirdImage);
				CxImageView forthImage = (CxImageView)convertView.findViewById(R.id.cx_fa_forth_9image);
				holder.sharedPhotos.add(forthImage);
				CxImageView fifthImage = (CxImageView)convertView.findViewById(R.id.cx_fa_fifth_9image);
				holder.sharedPhotos.add(fifthImage);
				CxImageView sixthImage = (CxImageView)convertView.findViewById(R.id.cx_fa_sixth_9image);
				holder.sharedPhotos.add(sixthImage);
				CxImageView seventhImage = (CxImageView)convertView.findViewById(R.id.cx_fa_seventh_9image);
				holder.sharedPhotos.add(seventhImage);
				CxImageView eighthImage = (CxImageView)convertView.findViewById(R.id.cx_fa_eighth_9image);
				holder.sharedPhotos.add(eighthImage);
				CxImageView ninethImage = (CxImageView)convertView.findViewById(R.id.cx_fa_nineth_9image);
				holder.sharedPhotos.add(ninethImage);
				
				convertView.setTag(holder);
				
			}else{
				holder = (ZoneItemViewHolder)convertView.getTag();
			}
			
			if ( (null == mAdapterData) || (mAdapterData.size() < position) ) { //第一个item不需要数据
				return convertView;
			}
			final FeedListData tempFeed = mAdapterData.get(position-1); //第一个item不需要数据

			LinearLayout itemLayout = holder.itemLayout;
			
			int isNew = tempFeed.getIsNew();
			if(1==isNew){
				itemLayout.setBackgroundColor(Color.rgb(252, 237, 228));
			}else{
				itemLayout.setBackgroundColor(Color.rgb(228, 228, 234));
			}
			
			
			
			//头像、名称和删除按钮
			CxImageView headView = holder.speakerHead;
			TextView speakerName = holder.speakerName;
			TextView textMore = holder.textMore;
			speakerName.setTextColor(Color.BLACK);
			ImageButton deleteRecord = holder.deleteRecord; //删除按钮
			if (TextUtils.equals(CxGlobalParams.getInstance().getUserId(), tempFeed.getAuthor())) { //创建者是自己
//				headView.setBackgroundResource(R.drawable.cx_fa_wf_icon_small);
//				headView.setImage(RkGlobalParams.getInstance().getIconSmall(), 
//						false, 74, RkUsersPairZone.this, "head", RkUsersPairZone.this.getActivity());
				headView.displayImage(ImageLoader.getInstance(), 
						CxGlobalParams.getInstance().getIconSmall(), 
						CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, (int)mSmallImgConner);
				
				headView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) { //自己就修改头像
						Intent selectImageForHead = new Intent(getActivity(), ActivitySelectPhoto.class);
						ActivitySelectPhoto.kIsCallPhotoZoom =true;
						ActivitySelectPhoto.kIsCallFilter = false;
						ActivitySelectPhoto.kIsCallSysCamera = true;
						startActivityForResult(selectImageForHead, MODIFY_MY_HEAD_REQUEST);
					}
				});
				speakerName.setText(getActivity().getString(R.string.cx_fa_nls_me));
				
				deleteRecord.setVisibility(View.VISIBLE);
				deleteRecord.setOnClickListener(new View.OnClickListener() {
					
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
										CxZoneApi.getInstance().sendShareRequest("space", 
												tempFeed.getId(), new ShareThirdSender(tempFeed, getActivity()));
									} catch (Exception e) {
										e.printStackTrace();
										Toast.makeText(getActivity(), getString(
												R.string.share_failed), Toast.LENGTH_SHORT).show();
										dlg.dismiss();
									}
									break;
									
								case 1:
									// 删除帖子
									CxZoneApi.getInstance().requestDeleteFeed(tempFeed.getId(), 
											new DeleteFeedBack(itemLocation));
									break;

								default:
									break;
								}
								
							}
						});
						builder.show();
						
						/*DialogUtil du = DialogUtil.getInstance();
						du.setOnSureClickListener(new OnSureClickListener() {
							
							@Override
							public void surePress() {
								// 删除帖子
								RkZoneApi.getInstance().requestDeleteFeed(tempFeed.getId(), 
										new DeleteFeedBack(itemLocation));								
							}
						});
						
						du.getSimpleDialog(getActivity(), null, 
								getActivity().getString(R.string.cx_fa_delete_comfirm_text), 
								null, null).show();*/
						
						
					}
				});
			}else{ //创建者是对方
//				headView.setBackgroundResource(R.drawable.cx_fa_hb_icon_small);
//				headView.setImage(RkGlobalParams.getInstance().getPartnerIconBig(), 
//						false, 74, RkUsersPairZone.this, "head", RkUsersPairZone.this.getActivity());
				headView.displayImage(ImageLoader.getInstance(), 
						CxGlobalParams.getInstance().getPartnerIconBig(), 
						CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, (int)mMateSmallImgConner);
				headView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) { //对方进备忘资料
						Intent toMateProfile = new Intent(getActivity(), CxFamilyInfoActivity.class);
						startActivity(toMateProfile);
						getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);//by wentong.men 131109
					}
				});
				if (null != CxGlobalParams.getInstance().getPartnerName()) {
					speakerName.setText(
							getActivity().getString(CxResourceString.getInstance().str_pair)
							+CxGlobalParams.getInstance().getPartnerName());
				}else{
					speakerName.setText(getActivity().getString(CxResourceString.getInstance().str_pair));
				}
				
				deleteRecord.setVisibility(View.GONE);
			}
			//时间
			TextView speakTime = holder.speakTime;
			SimpleDateFormat sdf = new SimpleDateFormat("MM月:dd:HH:mm");
			CxLog.i("zone feed create time:", ""+tempFeed.getCreate());
			Date createStamp = new Date(Long.parseLong(tempFeed.getCreate())*1000L);
			String stampStr = sdf.format(createStamp);
			String []stampArray = stampStr.split(":");
			String dayStr = null, monthStr = null, timeStr = null;
			monthStr = filterStartZero(stampArray[0]);
			dayStr = stampArray[1];				//filterStartZero(stampArray[1]);
			timeStr = stampArray[2] + ":" + stampArray[3];
			
			speakTime.setText(timeStr);
			
			CustomTextView speakWord = holder.speakWord; 
			LinearLayout photosLayout = (LinearLayout)convertView.findViewById(R.id.cx_fa_shared_photos);
			FeedPost feedContent = tempFeed.getPost(); 
			if (null != feedContent) {
				//文字内容
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
				}else{
					speakWord.setVisibility(View.GONE);
					textMore.setVisibility(View.GONE);
				}
				//图片
				final List<FeedPhoto> photos = feedContent.getPhotos();
				if ( (null == photos) || (photos.size() < 1) ) { //没有照片
					photosLayout.setVisibility(View.GONE);
				}else{ //有照片
					photosLayout.setVisibility(View.VISIBLE);
					int len = photos.size();
					
					/* 分别得到有多张图时和只有一张图时，每个图片的显示布局。  目的:只有一张图时，显示大图 */ 
					// 有多张图片时每张图片的布局参数
					int screen_w 	= getResources().getDisplayMetrics().widthPixels;			// 屏幕宽度
					int row_w 		= screen_w  -  ScreenUtil.dip2px(getActivity(), 16*2 + 6*6 - 2*6 );		// 三个图像减去父对象margin及padding后，可用的宽度
					int w = (Integer)(row_w/3);		// 自动算图的宽度(有一个风险，如果屏幕的宽大于高，这个就会特别难看)
					int h = w;
					LinearLayout.LayoutParams layoutParaForMorePic = new LinearLayout.LayoutParams(w, h); 
					int margin = ScreenUtil.dip2px(getActivity(), 2);
					layoutParaForMorePic.setMargins(margin, margin, margin, margin);
								
					// 只有一个图片的布局参数
					w = ScreenUtil.dip2px(getActivity(), 170);					// dp转换为pix
					h = w;
					LinearLayout.LayoutParams layoutParaForOnlyOnePic = new LinearLayout.LayoutParams(w, h); 
					
					// 得到图片的所有路径，供看大图时使用 (
					ArrayList<String> imagepaths = new ArrayList<String>();
					for(int i=0; i<len; i++){
						FeedPhoto tempPhoto = photos.get(i);
						if(tempPhoto.getBig()!=null){
							imagepaths.add( tempPhoto.getBig() );
						}
					}
					final ArrayList<String> imgs = imagepaths;
					
					for(int i = 0; i < len; i++){ //
						CxImageView tempImage = holder.sharedPhotos.get(i);
						tempImage.setVisibility(View.VISIBLE);
						FeedPhoto tempPhoto = photos.get(i);
						// shichao
//						tempImage.setImage(tempPhoto.getThumb(),
//								false, 74, RkUsersPairZone.this, "head", 
//								RkUsersPairZone.this.getActivity());
						tempImage.displayImage(ImageLoader.getInstance(), 
								tempPhoto.getThumb(), R.drawable.chatview_imageloading, false, 0);
						final int clickItem = i;
						tempImage.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								CxZoneParam.getInstance().setPhotos(photos);
//								Intent imageDetail = new Intent(getActivity(), RkZoneImageDetail.class);
								Intent imageDetail = new Intent(getActivity(), CxImagePager.class);
								imageDetail.putExtra(CxGlobalConst.S_ZONE_TITLE_MORE_BUTTTON, true);
								imageDetail.putExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER, clickItem);
				                imageDetail.putExtra(CxGlobalConst.S_STATE, CxImagePager.STATE_ZONE_MYSELF);
				                imageDetail.putStringArrayListExtra("imagespath", imgs);
				                
								startActivity(imageDetail);
								getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
							}
						});
						
						/* 如果只有一张图，则把它变为大图 
						       说明:为了防止多图情况下的第一张图变成大图(上一条是单图,下一条是多图时会出现)。 在此需要重新设多图情况的第一张图的布局)*/  
						if(len==1){
								tempImage.setLayoutParams(layoutParaForOnlyOnePic);
						}else{
								tempImage.setLayoutParams(layoutParaForMorePic);
						}
					}
					
					int gapSize = 9 - len; //一共9张
					for(int k = 0; k < gapSize; k++){
						CxImageView tempImage = holder.sharedPhotos.get(8 - k);
						tempImage.setVisibility(View.GONE);
					}
				}
				//评论的内容
				LinearLayout commentAndReplayOfRecord = holder.commentAndReplayOfRecord;
				commentAndReplayOfRecord.removeAllViews();
				List<FeedReply> replies = feedContent.getReplays();
				if ( (null == replies) || (replies.size() < 1) ){
					commentAndReplayOfRecord.setVisibility(View.GONE);
				}else{
					commentAndReplayOfRecord.setVisibility(View.VISIBLE);
					for(int k = 0; k < replies.size(); k++){
						final int tempIndex = k;
						View replyView = getActivity().getLayoutInflater().inflate(
								R.layout.cx_fa_fragment_zone_list_reply_item, null);
						TextView itemReply = (TextView)replyView.findViewById(R.id.reply_text_content);
						TextView replyTime = (TextView)replyView.findViewById(R.id.reply_time);
						ImageView replyUnread = (ImageView) replyView.findViewById(R.id.zone_post_reply_unread);
						
						EnhancedGifView gifView = (EnhancedGifView)replyView.findViewById(R.id.reply_expression_content);
						itemReply.setText("");
						replyTime.setText("");
						
						final FeedReply reply = replies.get(k);
						
						if(1==reply.getIsNew()){
							replyUnread.setVisibility(View.VISIBLE);
						}
						
						if (TextUtils.equals(reply.getAuthor(), 
								CxGlobalParams.getInstance().getUserId())) { //是自己说的可以删除评论或回复，否则是回复
							replyView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//弹窗确认是否删除
//									AlertDialog comfirmDeleteReply = new AlertDialog
//									.Builder(getActivity())
//									.setMessage(getString(R.string.cx_fa_delete_comfirm_text))
//									.setNegativeButton(getString(R.string.cx_fa_confirm_text), 
//											new DialogInterface.OnClickListener() {
//										
//										@Override
//										public void onClick(DialogInterface dialog, int which) {
//											try {
//												mFeedIndex = itemLocation;
//												mReplyIndex = tempIndex;
//												RkZoneApi.getInstance().requestDeleteReply(
//														reply.getReply_id(), deleteReply);
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//										}
//									})
//									.setPositiveButton(getString(R.string.cx_fa_cancel_button_text), null)
//									.create();
//									comfirmDeleteReply.show();
									
									DialogUtil du = DialogUtil.getInstance();
									du.setOnSureClickListener(new OnSureClickListener() {
										@Override
										public void surePress() {
											try {
												mFeedIndex = itemLocation;
												mReplyIndex = tempIndex;
												CxZoneApi.getInstance().requestDeleteReply(reply.getReply_id(), deleteReply);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
									du.getSimpleDialog(getActivity(),null,getString(R.string.cx_fa_delete_comfirm_text),null,null).show();
									
								}
							});
							
						}else{ //对方说的就要回复
							replyView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									mReplyFeedId = tempFeed.getId();
									mReply_to = CxMateParams.getInstance().getMateUid();
//									mCommentLayout.setVisibility(View.VISIBLE);
									mInputPanel.setVisibility(View.VISIBLE);
									mSendReplyLayer.setVisibility(View.VISIBLE);
									input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
									contentOfSend.setFocusable(true);
									contentOfSend.requestFocus();
									contentOfSend.requestFocusFromTouch();
									contentOfSend.setSelection(0);
									contentOfSend.setCursorVisible(true);
									
								}
							});
							
						}
						
						String speakerStr = "";
						//谁评论
						if (TextUtils.equals(CxGlobalParams.getInstance().getUserId(), reply.getAuthor())) {
							speakerStr = getString(R.string.cx_fa_nls_me);
						}else{
							speakerStr = getString(CxResourceString.getInstance().str_zone_mate)
							+( null == CxGlobalParams.getInstance().getPartnerName() ? 
									"" : CxGlobalParams.getInstance().getPartnerName());
						}
//						itemReply.append( TextUtil.getSpanStr(speakerStr, 0.8f, Color.BLACK));
						itemReply.append(TextUtil.getNewSpanStr(speakerStr, 14, Color.argb(170, 0, 0, 0)));
						
						//是否是回复（有2中情况：评论和回复）
						speakerStr = "";
						if ((null == reply.getReply_to()) 
								|| ("null".equalsIgnoreCase(reply.getReply_to())) ) { //不是回复，仅仅发表评论
							//不做任何处理
						}else{ //回复(其实这样要注意自己给自己回复的情况，这是不允许的，暂时先不考虑这样的bug出现)
							/*  "回复"两个字 ， 需要用灰色(跟回复内容用同一种颜色) */
    						speakerStr = getString(R.string.cx_fa_reply_text);
//							itemReply.append( TextUtil.getSpanStr(speakerStr, 0.8f, Color.GRAY) );
    						itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.argb(117, 0, 0, 0)) );
							
							/* 回复了谁(被回复的人) */
							speakerStr = "";
							if (reply.getReply_to().equals(CxGlobalParams.getInstance().getUserId())) { //回复我
								speakerStr += getString(R.string.cx_fa_nls_me);
							}else{ //回复对方
								speakerStr += getString(CxResourceString.getInstance().str_zone_mate);
								speakerStr += (null == CxGlobalParams.getInstance().getPartnerName() ? 
										"" : CxGlobalParams.getInstance().getPartnerName());
							}
							if(!speakerStr.equals("")){
//								itemReply.append( TextUtil.getSpanStr(speakerStr, 0.8f, Color.BLACK) );
								itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.argb(170, 0, 0, 0)) );
							}
							
						}
						
						// 最后的一个冒号 （黑体）
						speakerStr = " :  ";		
//						itemReply.append( TextUtil.getSpanStr(speakerStr, 0.8f, Color.BLACK) );
						itemReply.append( TextUtil.getNewSpanStr(speakerStr,14, Color.argb(170, 0, 0, 0)) );
						
						
						//回复内容 (回复时间在图片类型的，要用一个单独的TexView；在文本回复时，一定要跟文字放一块，否则文字超过两行时，会显示不出来回复时间)											// 回复的内容
//						String str2 = DateUtil.getTimeDiffWithNow(reply.getTs());		// 回复的时间距现在的时长(x年前、x月前、x小时前、x分钟前、x秒前)
						String str2 = "   "+DateUtil.getTimeDiffWithNow(reply.getTs());
//						if( (null != reply.getExtra()) && (!"null".equalsIgnoreCase(reply.getExtra())) ){ //是表情回复
//							gifView.setVisibility(View.VISIBLE);
//							int location = getExpressionPositon(reply.getExtra());
//							if ( (location > 0) && (location < mExpressionResource.length) ) {
////							    String[] faceValues = getFaceValues(getResources());
////						        String[] faceTexts = getFaceTexts(getResources());
////						        TypedArray faceImageIds = getResources().obtainTypedArray(R.array.cx_fa_ids_input_panel_face_images);
////
////						        for (int i = 0; i < faceValues.length; i++) {
////						            if (faceValues[i].equals(mExpressionResource[location])) {
////						                RkLog.d("", "face id=" + i);
//////						                face.setGifDecoderImage(Uri.parse("file:///storage/sdcard0/chuxin/face_huaxin.gif"));
////						                gifView.setGifImage(faceImageIds.getResourceId(i, 0));
////						                break;
////						            }
////						        }
////						        faceImageIds.recycle();
//								gifView.setGifImage(mExpressionResource[location]);
//							}else{
//								RkLog.i("expresssion", " no resource for expression");
//							}
//							
//							// 设置回复时间
////							SpannableString spanContentStr = new SpannableString(str2);
////							spanContentStr.setSpan(new RelativeSizeSpan(0.7f), 0, 
////									str2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				// 设置回复内容的字体
////							spanContentStr.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 
////									str2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				// 设置颜色
//							replyTime.append( TextUtil.getSpanStr(str2, 0.7f, Color.GRAY) );
//							
//						}else{//是文字回复
//							gifView.setVisibility(View.GONE);
							
							if (null != reply.getText()) {
							    
                                String[] faceValues = getFaceValues(getResources());
                                String[] faceTexts = getFaceTexts(getResources());
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
                                
                                
	                                
//								String str =  str1 + "  " + str2;
//								SpannableString spanContentStr = new SpannableString(str);
//								spanContentStr.setSpan(new RelativeSizeSpan(0.9f), 0, 
//										str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				// 设置回复内容的字体
//								spanContentStr.setSpan(new RelativeSizeSpan(0.7f),  str1.length(), 
//										str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				// 设置回复时间的字体
//								spanContentStr.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 
//										str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				// 设置颜色
//								itemReply.append(spanContentStr);
							}
//						}
						commentAndReplayOfRecord.addView(replyView);
					} //end for(k)
					
				}
			}else{ //异常情况，帖子没有文字和图片
				speakWord.setVisibility(View.GONE);
				photosLayout.setVisibility(View.GONE);
			}
			
			//评论按钮
			ImageButton commentOrReplayBtn = holder.commentOrReplayBtn;
			commentOrReplayBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mReplyFeedId = tempFeed.getId();
					mReply_to = null;
//					mCommentLayout.setVisibility(View.VISIBLE);
					mInputPanel.setVisibility(View.VISIBLE);
					mSendReplyLayer.setVisibility(View.VISIBLE);
					input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					
					contentOfSend.setFocusable(true);
					contentOfSend.requestFocus();
					contentOfSend.requestFocusFromTouch();
					contentOfSend.setSelection(0);
					contentOfSend.setCursorVisible(true);
//					mCommentInputEditer.requestFocus();
				}
			});
			
			//日期
			int len	= dayStr.length()+monthStr.length();
			TextView speakDate = holder.speakDate;
			SpannableString tempSpanStr = new SpannableString(dayStr+monthStr);
			tempSpanStr.setSpan(new RelativeSizeSpan(3.0f), 0, dayStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);	// 设置日的字号
			tempSpanStr.setSpan(new ForegroundColorSpan(Color.rgb(235, 161, 121)), 
					0, dayStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);													// 设置数字日的颜色
			
			tempSpanStr.setSpan(new ForegroundColorSpan(Color.rgb(156, 156, 163)), 	2, len-1,
					Spanned.SPAN_INCLUSIVE_INCLUSIVE);				// 设置数字月的颜色									
			
			tempSpanStr.setSpan(new RelativeSizeSpan(1.1f), 2, len-1, 	
					Spanned.SPAN_INCLUSIVE_INCLUSIVE);		// 设置数字月的字号(android中没有与ios对应的字体，将数字月与"月"字设为不同字号模拟一下ios的效果)
			//tempSpanStr.setSpan(new TypefaceSpan("serif"), 2, len-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  								// 设置数字月的字体
			tempSpanStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  	// 全部设为设置粗体
			
			if (1 == position) { //第一行无条件显示
				speakDate.setVisibility(View.VISIBLE);
				speakDate.setText(tempSpanStr);
			}else{ //非第一行
				//
				// 判断跟上一天是否是同一天
				FeedListData preFeed = mAdapterData.get(position - 2);
				//Log.e("RkUsersPairZone", feedContent.getText()+"  time1:" + preFeed.getCreate() + "  time2:" + tempFeed.getCreate() );
				boolean flag = DateUtil.isTheSameDay(Long.valueOf(preFeed.getCreate())*1000,  Long.valueOf(tempFeed.getCreate())*1000 );
				
				if (flag) {
					speakDate.setVisibility(View.GONE);
				}else{
					speakDate.setVisibility(View.VISIBLE);
					speakDate.setText(tempSpanStr);
				}
			}
			
			return convertView;
		}
		
		
		
	}
	
	//删除回复的回调
	JSONCaller deleteReply = new JSONCaller() {
		
		@Override
		public int call(Object result) {

			if (null == result) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic deleteResult = null;
			try {
				deleteResult = (CxParseBasic)result;
			} catch (Exception e) {
			}
			if (null == deleteResult || deleteResult.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			int rc = deleteResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(deleteResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(deleteResult.getMsg(),0);
				}
				return rc;
			}
			//删除成功
			if (mFeedIndex >= mFeedsData.size()) {
				//提示删除失败
				return -9;
			}
			try {
				FeedListData tempFeed = mFeedsData.get(mFeedIndex);
				FeedPost feedPost = tempFeed.getPost();
				List<FeedReply> tempReplies = feedPost.getReplays();
				tempReplies.remove(mReplyIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			updateListview.sendEmptyMessage(1);
			//mDeleteReply mFeedIndex
			return 0;
		}
	};
	
	//删帖子的回调
	class DeleteFeedBack implements JSONCaller{
		private int mLocation;
		
		public DeleteFeedBack(int location){
			this.mLocation = location;
		}
		
		@Override
		public int call(Object result) {
			if (null == result) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxParseBasic deleteResult = null;
			try {
				deleteResult = (CxParseBasic)result;
			} catch (Exception e) {
			}
			if (null == deleteResult || deleteResult.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			int rc = deleteResult.getRc();
			if (0 != rc) {
				if(TextUtils.isEmpty(deleteResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(deleteResult.getMsg(),0);
				}
				return rc;
			}
			//以下是删除成功
			mFeedsData.remove(mLocation);	

			updateListview.sendEmptyMessage(1);
			
			return 0;
		}		
	}
	
	
	//item的adapter的holder
	static class ZoneItemViewHolder{
		public TextView speakDate; //日期
		public CxImageView speakerHead; //头像
		public TextView speakerName; //昵称
		public TextView speakTime; //时间
		public CustomTextView speakWord; //文字内容
		public List<CxImageView> sharedPhotos; //图片内容
		public ImageButton commentOrReplayBtn; //评论
		public ImageButton deleteRecord; //删除本帖子
		public LinearLayout commentAndReplayOfRecord; //评论和回复的内容
		public TextView textMore;
		public LinearLayout itemLayout;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((View.VISIBLE == mSendReplyLayer.getVisibility()) 
				/*&& (KeyEvent.KEYCODE_BACK == event.getAction())*/ ) {
			CxLog.i("keyCode", ""+event.getAction());
//			KeyEvent.key
			if ((KeyEvent.KEYCODE_BACK == event.getAction())) {
				mSendReplyLayer.setVisibility(View.GONE);
				if(input.isActive()){
					input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				}
			}
		}
		return false;
	}

	class CurrentObserver extends CxObserverInterface{

		/*主要事项有：1、新的分享 2、头像（中、小）*/
		@Override
		public void receiveUpdate(String actionTag) {
			if (null == actionTag) {
				return;
			}
			
			if (actionTag.equalsIgnoreCase(CxServiceParams.SPACE_TS)) { //对方发帖子成功时long polling告诉更新
				if (isFirstComplete && isDownComplete) {
					isDownComplete = false;
					CxZoneApi.getInstance().requestFeedList(0, 15, 
							new FeedResponse(true, false), getActivity());
				}
				
				return;
			}
			
			if (CxGlobalParams.ICON_SMALL.equalsIgnoreCase(actionTag) //自己的头像修改，要刷新列表
					|| (CxGlobalParams.PARTNER_ICON_BIG.equalsIgnoreCase(actionTag)) //对方的头像修改，要刷新列表
					|| (CxGlobalParams.ZONE_BACKGROUND.equalsIgnoreCase(actionTag)) ) { //空间背景修改
				if (null != mZoneAdapter) {
					mZoneAdapter.notifyDataSetChanged();
				}
				return;
			}
			
			if (CxGlobalParams.PARTNER_NAME.equalsIgnoreCase(actionTag)) {
				if (null != mZoneAdapter) {
					mZoneAdapter.notifyDataSetChanged();
				}
				return;
			}
			
			if (CxZoneParam.FEED_DATA.equalsIgnoreCase(actionTag)) { //自己发帖子成功
				CxLog.i("999999", " has receive notify for new feed");
				if (null == mFeedsData) {
					mFeedsData = new ArrayList<FeedListData>();
					mFeedsData.add(CxZoneParam.getInstance().getFeedsData()); 
					mZoneAdapter.updataView(mFeedsData);
					CxLog.i("source data is null", " has receive notify for new feed");
					return;
				}else{
					List<FeedListData> targetData = new ArrayList<FeedListData>();
					targetData.add(CxZoneParam.getInstance().getFeedsData());
					targetData.addAll(mFeedsData);
					mFeedsData = targetData;
					mZoneAdapter.updataView(mFeedsData);
					CxLog.i("source data is not null", " has receive notify for new feed");
				}
				return;
			}
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		if (null != mServiceObserver) {
			CxServiceParams.getInstance().unRegisterObsercer(mServiceObserver);
		}
		
//		try {
//			CxResourceManager resourceManager = CxResourceManager.getInstance(
//					CxUsersPairZone.this, "thunb", CxUsersPairZone.this.getActivity());
//			if (null != resourceManager) {
//				resourceManager.clearMemory();
//				resourceManager = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		try {
			ShareSDK.stopSDK(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onDestroy();
	}
	
	
	private final int MODIFY_ZONE_BG_REQUEST = 1;
	private final int MODIFY_MY_HEAD_REQUEST = 2;
	private final int ADD_ZONE_FEED = 3;
	//modify background of zone, my head and mate head
	OnClickListener changeImage = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_zone_bg:
				Intent selectImageForBg = new Intent(getActivity(), ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom =true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForBg, MODIFY_ZONE_BG_REQUEST);
				break;
			case R.id.myHeadView:
				Intent selectImageForHead = new Intent(getActivity(), ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom =true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForHead, MODIFY_MY_HEAD_REQUEST);
				break;
			case R.id.mateHeadView:
				Intent toMateProfile = new Intent(getActivity(), CxFamilyInfoActivity.class);
				startActivity(toMateProfile);
				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			default:
				break;
			}
			
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			return;
		}
		
		if (MODIFY_ZONE_BG_REQUEST == requestCode) {
			if (!TextUtils.isEmpty(data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI))) {
				try {
					String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
					imagePath = imagePath.replace("file://", "");
//					RkLoadingUtil.getInstance().showLoading(getActivity(), true);
					DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
					CxSendImageApi.getInstance().changeBackgroundOfZone(imagePath, modifyBgOfZone);
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_modify_bg_of_zone),0);
				}
			}else{
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_modify_bg_of_zone),0);
			}
			return;
		}
		
		if (MODIFY_MY_HEAD_REQUEST == requestCode) {
			if (!TextUtils.isEmpty(data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI))) {
				try {
					String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
					imagePath = imagePath.replace("file://", "");
//					RkLoadingUtil.getInstance().showLoading(getActivity(), true);
					DialogUtil.getInstance().getLoadingDialogShow(getActivity(), -1);
					CxSendImageApi.getInstance().sendHeadImage(imagePath, 
							SendHeadImageType.HEAD_ME, modifyHeadImag);
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_modify_headimg_fail),0);
				}
			}else{
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_modify_headimg_fail),0);
			}
			return;
		}
		
//		if (ADD_ZONE_FEED == requestCode) { // 二人空间调用返回图片
//            //
//            String imagePath = data.getStringExtra(RkGpuImageConstants.KEY_PICTURE_URI);
//            if (null == imagePath) {
//                return;
//            }
//            Intent toAddFeed = new Intent(getActivity(), RkZoneAddFeed.class);
//            toAddFeed.putExtra(RkGlobalConst.S_ZONE_SHARED_TYPE, 1);
//            toAddFeed.putExtra(RkGlobalConst.S_ZONE_SHARED_IMAGE, imagePath);
//            startActivity(toAddFeed);
//        }
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@SuppressLint("HandlerLeak")
	JSONCaller modifyBgOfZone = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(Message msg) {
//					RkLoadingUtil.getInstance().dismissLoading();
				};
			}.sendEmptyMessage(1);
			
			if (null == result) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxChangeBgOfZone changeBg = null;
			try {
				changeBg = (CxChangeBgOfZone)result;
			} catch (Exception e) {
			}
			if (null == changeBg || changeBg.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			if (0 != changeBg.getRc()) {
				if(TextUtils.isEmpty(changeBg.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(changeBg.getMsg(),0);
				}
				return changeBg.getRc();
			}
			//修改聊天背景成功，修改全局变量
			CxGlobalParams.getInstance().setZoneBackground(changeBg.getBg_big());
			return 0;
		}
	};
	
	@SuppressLint("HandlerLeak")
	JSONCaller modifyHeadImag = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(Message msg) {
//					RkLoadingUtil.getInstance().dismissLoading();
				};
			}.sendEmptyMessage(1);
			if (null == result) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			CxChangeHead changeHeadResult = null;
			try {
				changeHeadResult = (CxChangeHead)result;
			} catch (Exception e) {
			}
			if (null == changeHeadResult || changeHeadResult.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			if(0 != changeHeadResult.getRc()){
				if(TextUtils.isEmpty(changeHeadResult.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(changeHeadResult.getMsg(),0);
				}
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
	
	
//	@SuppressLint("HandlerLeak")
//	private void displayModifyHead(String info){
//		Message msg = new Message();
//		msg.obj = info;
//		new Handler(getActivity().getMainLooper()){
//			public void handleMessage(Message msg) {
//				if ( (null == msg) || (null == msg.obj) ){
//					return;
//				}
//				Toast ts = Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT);
//				ts.setGravity(Gravity.CENTER, 0, 0);
//				ts.show();
//			};
//		}.sendMessage(msg);
//	}
	
	@Override
	public void onDestroyView() {
		
		super.onDestroyView();
	}
    private static String[] sFaceValues = null;

    private static String[] sFaceTexts = null;
    public static String[] getFaceValues(Resources res) {
        if (sFaceValues == null) {
            sFaceValues = res.getStringArray(R.array.face_ids);
        }
        return sFaceValues;
    }

    public static String[] getFaceTexts(Resources res) {
        if (sFaceTexts == null) {
            sFaceTexts = res.getStringArray(R.array.face_texts);
        }
        return sFaceTexts;
    }
    
    // 更新home按钮状态
    public static void updateHomeMenu(){

        if(CxGlobalParams.getInstance().getGroup() > 0 || CxGlobalParams.getInstance().getSpaceTips() > 0
        		|| CxGlobalParams.getInstance().getKid_tips() > 0){

            mMenuBtn.setBackgroundResource(R.drawable.navi_home_new_btn);
        } else {
    //      mMainMenuBtn.setImageResource(R.drawable.cx_fa_menu_btn);
            mMenuBtn.setBackgroundResource(R.drawable.cx_fa_menu_btn);
        }
    }
    
    private boolean isLeapYear(int yearInt){
    	if ((yearInt % 4 == 0) && (yearInt % 100 != 0) || (yearInt % 400 == 0)){
			return true;
		}
    	return false;
    }
    
    private int getSpeMonthDays(int yearInt, int monthInt){
    	if (2 == monthInt) {
			if (isLeapYear(yearInt)) {
				return 29;
			}
    		return 28;
		}
    	//--
    	if ((4 == monthInt) || (6 == monthInt) || (9 == monthInt) || (11 == monthInt)) {
			return 30;
		}
    	
    	return 31;
    }
    
    private boolean isNormalDate(String year, String month, int day){
    	int yearInt = 1, monthInt = 1;
    	try {
			yearInt = Integer.parseInt(year);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			monthInt = Integer.parseInt(month);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int MaxDaysOfMonth = getSpeMonthDays(yearInt, monthInt);
		if ((day < 1) || (day > MaxDaysOfMonth)) {
			return false;
		}
    	
    	return true;
    }
    
    Dialog dlg;
    
    class ShareThirdSender implements JSONCaller{

    	private FeedListData mFeedData;
    	private Activity mActivity;
    	
    	public ShareThirdSender(FeedListData feedData, Activity activity){
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
    	CxLog.i("777", "comment="+comment+",chuxinOpenUrl="+chuxinOpenUrl+",imageUrl="+imageUrl);
    	
    	final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.cx_fa_app_icon, 
				activity.getString(R.string.cx_fa_role_app_name));
		oks.setTitle(comment);
		oks.setText(comment);
		oks.setTitleUrl(chuxinOpenUrl);
		oks.setUrl(chuxinOpenUrl);
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
