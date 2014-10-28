
package com.chuxin.family.views.chat;

// 
// shichao: refer to http://android.amberfog.com/?p=296
//         HowTo: ListView, Adapter, getView and different list items’ layouts in one ListView
//

import com.chuxin.family.R;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.image.ImageFetcher;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivityCamera;
import com.chuxin.family.libs.gpuimage.activity.ActivityGallery;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.mate.CxFamilyInfoActivity;
import com.chuxin.family.mate.CxMateParams;
import com.chuxin.family.model.CxObserverInterface;
import com.chuxin.family.models.Message;
import com.chuxin.family.models.Model;
import com.chuxin.family.neighbour.answer.CxAnswerActivity;
import com.chuxin.family.net.ChatApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.CxZoneApi;
import com.chuxin.family.pair.CxPairActivity;
import com.chuxin.family.parse.CxSettingsParser;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxZoneSendFeed;
import com.chuxin.family.parse.been.data.CxChangeHeadDataField;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxBaseDiskCache;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.CxInputPanel;
import com.chuxin.family.widgets.CxInputPanel.OnEventListener;
import com.chuxin.family.widgets.QuickActionBar;
import com.chuxin.family.widgets.ScrollableListView;
import com.chuxin.family.widgets.ScrollableListView.OnRefreshListener;
import com.chuxin.family.widgets.VoiceTip;
import com.chuxin.family.zone.CxZoneParam;
import com.uraroji.garage.android.mp3recvoice.RecMicToMp3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class ChatFragment extends Fragment implements OnClickListener, SensorEventListener {
    private static final String TAG = "ChatFragment";

    public static final int FETCH_NEW_MESSAGE = 0; // 获取新的消息

    private static final int UPDATE_RECORD_TIME = 1; // 更新语音消息播放时间

    private static final int SEND_RECORD = 2; // 发送语音消息

    public static final int UPDATE_CHAT_VIEW = 3; // 更新视图

    public static final int UPDATE_CHAT_TITLE = 4; // 更新聊天标题
    
    public static final int UPDATE_HOME_MENU = 5; // 更新home按钮未读消息状态
    
    public static final int POP_TIP = 6; // 弹出提示

    protected static final int MAX_VU_SIZE = 14; // set phone volume level 14

    // private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();
    // private RkSoundManager mRkSoundManager;

    private RecMicToMp3 mRecMicToMp3 = null;

    // private ScrollView mScroll;
    public MediaPlayer mCurrentMediaPlayer;

    private static ChatFragment mChatFragment;

    public static ScrollableListView mListView;

    private ChatLogAdapter mChatLogAdapter;

    private SQLiteDatabase mDB;

    private OnEventListener mOnEventListener = null;

    // private Context mChatViewContext;
    private View mChatView;

    private int mChatTs;

    private int mLimitNum = 5; // every page display data num

    public static Handler mChatHandler;

    private CxInputPanel mInputPanel;

    private QuickActionBar mActionBar = null;

    public Button mDelBtn, mCopyBtn, mDumpBtn;

    private static Context mContext;

    private static View mRecordView;

    private ImageView mRecordImageView;

    private TextView mRecordRemainTimeTextView, mRecordCancelTip;

    private int mDeviceWidth;

    private int mDeviceHeight;

    // private File mSoundFile;
    private String mSoundFilePath;

    // private MediaRecorder mMediaRecorder = new MediaRecorder();
    private long mRecorderStartTime;

    private long mRecorderStopTime;

    public MediaPlayer mMediaPlayer;

    // private ArrayList<String> mListItems = new ArrayList<String>();
    private Message mChatMessage;

    // private AnimationDrawable mRecordingAnimationDrawable;

    private static RelativeLayout mChatRecordLayout;

    // private static LinearLayout mRecordLayout;

    public static boolean mRecordStart;

    private static int SEND_IMAGE_CODE = 1000;

    private static int UPDATE_HEADIMAGE_CODE = 1001;
    
    private final int SYSTEM_MESSAGE_NOT_REDIRECT = 0; // 不跳转
    private final int SYSTEM_MESSAGE_REDIRECT_SPACE = 1; // 跳转日子
    private final int SYSTEM_MESSAGE_REDIRECT_NEIGHBOUR = 2; // 跳转密邻 
    private final int SYSTEM_MESSAGE_REDIRECT_REMINDER = 3; // 跳转提醒
    private final int SYSTEM_MESSAGE_REDIRECT_RKMATE = 4; // 跳转对方资料
    private final int SYSTEM_MESSAGE_REDIRECT_SETTING = 5; // 跳转设置
    private final int SYSTEM_MESSAGE_REDIRECT_ACCOUNTS = 6; // 跳转记账
    private final int SYSTEM_MESSAGE_REDIRECT_ANSWER = 7; // 谁家最聪明
    private final int SYSTEM_MESSAGE_REDIRECT_CALENDAR = 8; // 日历
    private final int SYSTEM_MESSAGE_REDIRECT_KIDS = 9; // 孩子空间

    private static String[] sRecordingDrawables = new String[] {
            "pub_microphone_volume_1", "pub_microphone_volume_2", "pub_microphone_volume_3",
            "pub_microphone_volume_4", "pub_microphone_volume_5", "pub_microphone_volume_6",
            "pub_microphone_volume_7", "pub_microphone_volume_8", "pub_microphone_volume_9",
            "pub_microphone_volume_10", "pub_microphone_volume_11", "pub_microphone_volume_12",
            "pub_microphone_volume_13", "pub_microphone_volume_14",
    };

    private RelativeLayout mChatRelativeLayout = null;

    public AnimationDrawable mLastVoiceAd;

    private SmartCursor mSmartCursor;

    public static String S_INPUTPANEL_CURRENT_VIEW = "ChatFragment";

    private RelativeLayout mChatRecordRelativeLayout;

    public static String mDateTimePrevious = null;

    public ImageFetcher mImageFetcher;

    public static boolean mShowReceiveMessage = false;
    
    public static boolean mShowLoading = false; //显示登录
    
    public static int mShowWhichTitle=0;  //0 老公说；1 网络未连接  ； 2 网络有点问题。
    
    private boolean isCreateViewFinish=false;

    public RecordEntry mPlayRecordEntry = null;

    // head title
    private ImageButton mMainMenuBtn;
    
    private Button mPhoneBtn;

    private TextView mTitle;

    private ProgressBar mPb;

    private CxImageView mBackgroundRkImageView;

    private HeadImgObserver headImgObserver;
    private TabloidObserver tabloidObserver;
    
    private SensorManager mSensorManager = null; // 传感器管理器  
    private Sensor mProximiny = null; // 传感器实例  
  
    private float mFproximiny; // 当前传感器距离
    
    private AudioManager mAudioManager = null;
    
    private LinearLayout mVoicePanel;

    class SmartCursor {
        private static final int GAIN_NUM_ONCE = 10;

        private SQLiteDatabase mSqliteDb;

        private int mBase;

        public SmartCursor(SQLiteDatabase db) {
            mSqliteDb = db;
            mBase = 0;
            init();
        }

        public void close() {
            if (mSqliteDb != null) {
                mSqliteDb.close();
                mSqliteDb = null;
            }
        }

        private void init() {
            int size = 0;
            String sql = "select count(_ROWID_) as size from messages";
            Cursor cursor = mDB.rawQuery(sql, null);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                size = cursor.getInt(cursor.getColumnIndex("size"));
            }

            cursor.close();
            mBase = size;

            gainMore();
        }

        public void reset() {
            init();
        }

        public boolean gainMore() {
            if (mBase == 0)
                return false;

            mBase -= GAIN_NUM_ONCE;
            if (mBase < 0)
                mBase = 0;
            return true;
        }

        public Cursor getCursor() {
            String sql = "select _ROWID_ as _id, k,v,flag from messages order by rowid limit 4096 offset "
                    + mBase;
            
            if(null == mSqliteDb){
                return null;
            }
            Cursor cursor = mSqliteDb.rawQuery(sql, null);
            CxLog.d(TAG, "Query (" + cursor.getCount() + ") " + sql);
            return cursor;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatHandler = new Handler() {

            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case FETCH_NEW_MESSAGE:
                        mChatTs = msg.arg1;
                        CxLog.v(TAG, "handel chatts=" + mChatTs);
                        fetchNewMessages();
                        // updateChatView();
                        break;
                    case UPDATE_RECORD_TIME:
                        mChatRecordRelativeLayout.setBackgroundResource(R.drawable.pub_recorder);
                        int vuSize = MAX_VU_SIZE * mRecMicToMp3.getVolume() / 100;
                        int resId = getResources().getIdentifier(sRecordingDrawables[vuSize],
                                "drawable", getActivity().getPackageName());
                        mRecordImageView.setImageResource(resId);
                        if (mRecordRemainTimeTextView.getVisibility() != View.VISIBLE) {
                            mRecordRemainTimeTextView.setVisibility(View.VISIBLE);
                        }
                        mRecordRemainTimeTextView
                                .setText(String.format(getResources().getString(R.string.cx_fa_chat_record_time_remianing),
                                                (91 - mReocrdCount)));
//                        mRecordRemainTimeTextView
//                        .setText(String
//                                .format(getResources().getString(
//                                        R.string.cx_fa_chat_record_time_remianing),
//                                        (90 - ((System.currentTimeMillis() - mRecorderStartTime) / 1000))));
                        mRecordCancelTip.setText(R.string.cx_fa_chat_record_tip);
                        if((90 - ((System.currentTimeMillis() - mRecorderStartTime) / 1000)) <= 0){
                            mRecordView.setVisibility(View.GONE);
//                            RkInputPanel.mRecordBar.setBackgroundResource(R.drawable.chatview_voice);
                            mInputPanel.changeRecordBarBackground(false);
                            stopRecord();
                        }
                        break;
                    case SEND_RECORD:
                        CxLog.v(TAG, "filePath=" + mSoundFilePath);
                        int audioLength = (int)((mRecorderStopTime - mRecorderStartTime) / 1000);
                        if (audioLength > 1) {
                            ChatFragment.this.sendMessage(mSoundFilePath, 4, audioLength, CxInputPanel.mCurrentSoundEffectState);
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    getResources().getString(
                                            R.string.cx_fa_chat_record_time_short_msg),
                                    Toast.LENGTH_LONG).show();
                        }
                        break;
                    case UPDATE_CHAT_VIEW:
                        updateChatView();
                        break;
                    case UPDATE_CHAT_TITLE:
                    	CxLog.i("ChatFragment_men", isCreateViewFinish+">>>>>>>>");
                        if (!isCreateViewFinish) {
                            break;
                        }
//                        if(!ChatFragment.this.isVisible()){
//                        	break;
//                        }
//                    	ChatFragment.this.isVisible();
                        if (ChatFragment.mShowReceiveMessage || ChatFragment.mShowLoading) {
                            mPb.setVisibility(View.VISIBLE);
                            String titleInfo = CxApplication.getInstance().getContext().getString(R.string.cx_fa_receive_message_progress);
                            mTitle.setText(titleInfo);
                        } else {
                            if (mPb.getVisibility() == View.VISIBLE) {
                                mPb.setVisibility(View.GONE);
                            }
                            
                            if(ChatFragment.mShowWhichTitle==0){
                            	if (!TextUtils.isEmpty(CxGlobalParams.getInstance().getPartnerName())) {
                                    String titleInfo = CxApplication.getInstance().getContext().getString(CxResourceString.getInstance().str_pair)
                                            + CxGlobalParams.getInstance().getPartnerName() 
                                            + CxApplication.getInstance().getContext().getString(R.string.cx_fa_chat_title_speak);
                                    mTitle.setText(titleInfo);
                                } else {
                                    String titleInfo = CxApplication.getInstance().getContext().getString(CxResourceString.getInstance().str_pair) 
                                    	+  CxApplication.getInstance().getContext().getString(R.string.cx_fa_chat_title_speak);
                                    mTitle.setText(titleInfo);
                                }
                            }else if(ChatFragment.mShowWhichTitle==1){
                            	mTitle.setText(R.string.cx_fa_net_response_code_null);
                            }else{
                            	mTitle.setText(R.string.cx_fa_net_response_code_fail);
                            }
                            
                        }
                        break;
                    case UPDATE_HOME_MENU:
                    	if(ChatFragment.this.isVisible()){
                    		updateHomeMenu();
                    	} 
                        break;
                    case POP_TIP:
                        String message = msg.obj.toString();
                        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                        break;
                    default:
                        break;
                }
            }

        };
        mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager == null)
            return;
        if (CxGlobalParams.getInstance().isChatEarphone()) {
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        } else {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CxGlobalParams globalParam = CxGlobalParams.getInstance();
        headImgObserver 	= new HeadImgObserver();
        tabloidObserver 		= new TabloidObserver();
        
        List<String> listenTags = new ArrayList<String>();
        listenTags.add(CxGlobalParams.PARTNER_ICON_BIG);
        listenTags.add(CxGlobalParams.ICON_SMALL);
        listenTags.add(CxGlobalParams.SINGLE_MODE);
        listenTags.add(CxGlobalParams.GENDER);
        headImgObserver.setListenTag(listenTags);
        headImgObserver.setMainThread(true);
        globalParam.registerObserver(headImgObserver);

        // 增加"我家小报"监听者
        List<String> listenTabloidTags = new ArrayList<String>();
        listenTabloidTags.add(CxGlobalParams.HAVE_TABLOID_MSG);
        tabloidObserver.setListenTag(listenTabloidTags);
        tabloidObserver.setMainThread(true);
        globalParam.registerObserver(tabloidObserver);
        
        mContext = getActivity();
        mChatView = init();
        mChatFragment = this;

        ((CxMain)getActivity()).closeMenu();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);  
        mProximiny = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); 
        
        isCreateViewFinish=true;
        return mChatView;
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	 // 设置聊天的背景，如果用户未设置过，则默认取配置文件中的第一张
        // loadBackground(RkGlobalParams.getInstance().getChatBackgroundBig());
        String chatBg = CxGlobalParams.getInstance().getChatBackgroundBig();
        if(chatBg!=null && !chatBg.equals("null")){
        	mBackgroundRkImageView.setChatbgImage(chatBg, ChatFragment.this, getActivity());
        }else{        	
        	mBackgroundRkImageView.setImageDrawable(getResources().getDrawable(CxResourceDarwable.getInstance().dr_chat_chatbg_default));
        }

    }
    
   

    class HeadImgObserver extends CxObserverInterface {

        @Override
        public void receiveUpdate(String actionTag) {
            if (null == actionTag) {
                return;
            }
            if (CxGlobalParams.PARTNER_ICON_BIG.equalsIgnoreCase(actionTag)) {
                if (null != mChatLogAdapter) {
                    updateChatView(false);
                }
                return;
            }
            

            if (CxGlobalParams.ICON_SMALL.equalsIgnoreCase(actionTag)) {
                if (null != mChatLogAdapter) {
                    updateChatView(false);
                }
                return;
            }
            
            if (CxGlobalParams.GENDER.equalsIgnoreCase(actionTag)) {
            	if(CxGlobalParams.getInstance().getSingle_mode()==0){
            		return ;
            	}
            	mPhoneBtn.setText(getString(CxResourceString.getInstance().str_pair_invite_text));
            	if (null != mChatLogAdapter) {
                    updateChatView(false);
                }
            	return;
            }
            
            
            if (CxGlobalParams.SINGLE_MODE.equalsIgnoreCase(actionTag)) {
            	int mode = CxGlobalParams.getInstance().getSingle_mode();
            	if(mode==0){
            		new Handler(getActivity().getMainLooper()){
            			public void handleMessage(android.os.Message msg) {
            				mPhoneBtn.setBackgroundResource(R.drawable.cx_fa_call_btn);
            			};
            		}.sendEmptyMessage(0);
            	}
            	return;
            }

        }

    }
    
    // “我家小报”观察者类 
    class TabloidObserver extends CxObserverInterface {

        @Override
        public void receiveUpdate(String actionTag) {
            if (null == actionTag) {
                return;
            }
            if (CxGlobalParams.HAVE_TABLOID_MSG.equals(actionTag)) {
                if (null != mChatLogAdapter) {
                    updateChatView(false);
                }
                return;
            }
        }

    }

    public static ChatFragment getInstance() {
        if (null == mChatFragment) {
            mChatFragment = new ChatFragment();
        }
        return mChatFragment;
    }

    private void installListener() {
        ;
    }

    private void initData() {
        mChatMessage = new Message(null, getActivity());
        mChatMessage.init();
        mDB = Globals.getInstance().openOrCreateMyDatabase(getActivity());
        mSmartCursor = new SmartCursor(mDB);

        mLimitNum = 5; // init
        // Cursor cursor = mDB.rawQuery(
        // "select _ROWID_ as _id, k,v from messages order by k", null);
        String sql = "select * from (select _ROWID_ as _id, k,v,flag from messages order by rowid desc limit "
                + mLimitNum + ") order by _id";
        // String sql = "select _ROWID_ as _id, k,v,flag from messages";

        Cursor cursor = mSmartCursor.getCursor();
        CxLog.v(TAG, "cursor>>>" + cursor.getCount());
        if (cursor.getCount() == 0) {
            Message message0 = Message.buildMessageObject(
                    getActivity().getString(R.string.cx_fa_chat_header_message), "welcome", 1,
                    System.currentTimeMillis());
            message0.put();
            
            int single_mode = CxGlobalParams.getInstance().getSingle_mode();
            if(single_mode==1){
            	Message message2 = Message.buildMessageObject(
                        getActivity().getString(CxResourceString.getInstance().str_chat_welcome_single_mode_default),
                        "default", 1, System.currentTimeMillis());
                message2.put();
            }else{
            	Message message1 = Message.buildMessageObject(
                        getActivity().getString(CxResourceString.getInstance().str_chat_welcome_first_msg), "text", 1,
                        System.currentTimeMillis());
                message1.put();
                Message message2 = Message.buildMessageObject(
                        getActivity().getString(CxResourceString.getInstance().str_chat_welcome_second_msg),
                        "default", 1, System.currentTimeMillis());
                message2.put();
            }
            // reopen the cursor
            cursor.close();
            cursor = mSmartCursor.getCursor();
        }

        mChatLogAdapter = new ChatLogAdapter();
        mListView.setAdapter(mChatLogAdapter);
        mChatLogAdapter.updateMessages(cursor, mListView, mContext, mChatFragment, -1);

        try {
            mListView.setSelection(cursor.getCount() + 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //shichao 临时数据添加
        //addTempData();

    }

    public void fetchNewMessages() {
        mShowReceiveMessage = true;
        android.os.Message message = android.os.Message.obtain(mChatHandler, UPDATE_CHAT_TITLE);
        message.sendToTarget();

        ChatApi.getInstance().doGetMessages(new JSONCaller() {

            @Override
            public int call(Object result) {
                CxLog.i(TAG, "result>>>" + result.toString());
                CxLog.v(TAG, "mChatts=" + mChatTs);
                int msgid = -1;
                int msgNum = 0;

                try {
                    JSONArray array = (JSONArray)result;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject data = array.getJSONObject(i);
                        msgNum++;
                        if (data.getString("type").equals("audio")) {
                            // download audio file
                            JSONObject audio = data.getJSONObject("audio");
                            data.put("is_read", false); // user is read, init
                                                        // false
                            String url = audio.getString("url");
                            int audioLength = audio.getInt("len");
//                            getAudioFile(url, data, audioLength);
//                            jsonData.getJSONObject("audio").put("url", audioFilePath);
                            Message message = Message.buildMessageObject(data);
                            message.put();
                            msgid = message.getMsgId();
                            VoiceTip.tip(mContext, VoiceTip.VOICE_TIP_MODE_VIBRATE
                                    | VoiceTip.VOICE_TIP_MODE_VOICE);
//                            mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

                        } else {
                            Message message = Message.buildMessageObject(array.getJSONObject(i));
                            message.put();
                            msgid = message.getMsgId();
                            VoiceTip.tip(mContext, VoiceTip.VOICE_TIP_MODE_VIBRATE
                                    | VoiceTip.VOICE_TIP_MODE_VOICE);
                        }
                    }
                    updateReadMessages(msgid);
                    // auto scroll to list bottom
                    // mListView.setStackFromBottom(true);
                    updateChatView();
                    mShowReceiveMessage = false;
                    android.os.Message message = android.os.Message.obtain(mChatHandler,
                            UPDATE_CHAT_TITLE);
                    message.sendToTarget();

                } catch (Exception e) {
                    CxLog.e(TAG, "" + e.getMessage());
                    mShowReceiveMessage = false;
                    android.os.Message message = android.os.Message.obtain(mChatHandler,
                            UPDATE_CHAT_TITLE);
                    message.sendToTarget();
                }

                // auto scroll to list bottom
                // mListView.setStackFromBottom(true);
                // mListView
                // .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                // updateChatView();

                // VoiceTip.tip(mChatFragment.getActivity()
                // .getApplicationContext(),
                // VoiceTip.VOICE_TIP_MODE_VIBRATE
                // | VoiceTip.VOICE_TIP_MODE_VOICE);
                return 0;
            }

        });
    }
    
/*    public  void addTempData(){
    	String json = "{\"redirect\" : 0, \"btn2_name\": \"agree\",\"sender\": \"\",\"title\": \"nihao\",\"text\": \"nnnnn\",\"icon_type\": 0,\"method\": \"group.agree\"," +
    			"\"create_time\": 1382081252,\"btn1_name\":\"deagree\",\"mode\": 0,\"template\": \"default\",\"icon_url\": \"\",\"type\": \"system\",\"id\":  \"184\", \"value1\":\"10001\", \"value2\":\"10002\"}";
    	try {
			JSONObject jsonObj = new JSONObject(json);
			 Message message = Message.buildMessageObject(jsonObj);
			 message.put();
			 updateChatView();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }*/

    @Override
    public void onStart() {
        super.onStart();
        CxLog.i("ChatFragment_men", mShowReceiveMessage+":"+mShowLoading+":"+mShowWhichTitle);
        android.os.Message chatMessage = mChatHandler.obtainMessage(ChatFragment.UPDATE_CHAT_TITLE);
	 	chatMessage.sendToTarget();
        // mRkSoundManager.mFMODAudioDevice.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
			if (null != mSmartCursor) {
			    mSmartCursor.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        try {
			if (null != mDB) {
			    // mDB.endTransaction();
			    mDB.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        if (null != mDateTimePrevious) {
            mDateTimePrevious = null;
        }
        // if(mRkSoundManager!=null){
        // mRkSoundManager.cFmodStop();
        // mRkSoundManager.mFMODAudioDevice.stop();
        // }

        CxGlobalParams.getInstance().unRegisterObsercer(headImgObserver);
        CxGlobalParams.getInstance().unRegisterObsercer(tabloidObserver);
        
        CxGlobalParams.getInstance().setRecorderFlag(false);
    }

    private static void updateReadMessages(int msgid) {
        if (msgid <= 0)
            return;

        ChatApi.getInstance().doReadMessage(msgid, new JSONCaller() {
            @Override
            public int call(Object result) {
                // polling();
                return 0;
            }
        });
    }

    // 更新Adapter中的数据。(语音消息、抽鞭子会用到。播放后，对应的Entry类中已更新该条数据的内容。Listview中也要同步更新,要不然数据依旧是以前的)
    public void updateChatViewDB() {
        Cursor cursor = mSmartCursor.getCursor();
        mChatLogAdapter.updateDB(cursor);
    }
    
    private void updateChatView(boolean removeMode) {
        Cursor cursor = mSmartCursor.getCursor();
/*        List<Message> messages = new ArrayList<Message>();
        for(int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            String value = cursor.getString(2);
            Message message = null;
            try {
                message = Message.buildMessageObject(new JSONObject(value));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            messages.add(message);
        }
        
        Comparator<Message> cp = new Comparator<Message>() {
            public int compare(Message r1, Message r2){
                long time = r1.getCreateTimestamp() - r2.getCreateTimestamp();
                return (int)time;
            }
        };
        Collections.sort(messages, cp);*/
        
        if (removeMode) {
            mChatLogAdapter.updateMessages(cursor, mListView, mContext, mChatFragment,
                    mListView.getFirstVisiblePosition());
        } else {
            mChatLogAdapter.updateMessages(cursor, mListView, mContext, mChatFragment, -1);
        }
    }

    private void updateChatView() {
        updateChatView(false);
    }
    
    
    public void setOnEventListener(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    @SuppressWarnings("deprecation")
    protected View init() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cx_fa_view_chat_chatting,
                null);

        // ((RkMain)getActivity()).getcontentView().setOnTouchListener(this);

        mDeviceWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mDeviceHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

        CxLog.d(TAG, "chatframgment pairid>>>" + CxGlobalParams.getInstance().getPairId());

        // head title
        mMainMenuBtn = (ImageButton)view.findViewById(R.id.cx_fa_chat_head_menu_btn);
        // add by shichao 20131024
        updateHomeMenu();
        mPhoneBtn = (Button)view.findViewById(R.id.cx_fa_chat_head_call_btn);
        mTitle = (TextView)view.findViewById(R.id.cx_fa_chat_title_info_text);
        mPb = (ProgressBar)view.findViewById(R.id.receive_message_circleProgressBar);
        
        int single_mode = CxGlobalParams.getInstance().getSingle_mode();
        if(single_mode==1){
        	mPhoneBtn.setBackgroundResource(R.drawable.cx_fa_title_right_btn_bg);
        	mPhoneBtn.setText(getString(CxResourceString.getInstance().str_pair_invite_text));
        	mPhoneBtn.setTextColor(Color.WHITE);
        	mPhoneBtn.setTextSize(16);	
        }
        
        mMainMenuBtn.setOnClickListener(this);
        mPhoneBtn.setOnClickListener(this);
        if (!TextUtils.isEmpty(CxGlobalParams.getInstance().getPartnerName())) {
            String titleInfo = getString(CxResourceString.getInstance().str_pair)
                    + CxGlobalParams.getInstance().getPartnerName()
                    + getString(R.string.cx_fa_chat_title_speak);
            mTitle.setText(titleInfo);
        } else {
            String titleInfo = getString(CxResourceString.getInstance().str_pair) + getString(R.string.cx_fa_chat_title_speak);
            mTitle.setText(titleInfo);
        }
        mBackgroundRkImageView = (CxImageView)view.findViewById(R.id.cx_fa_chat_background_image);
        mListView = (ScrollableListView)view.findViewById(R.id.cx_fa_view_chat_chatting__chatlog);
        mInputPanel = (CxInputPanel)view.findViewById(R.id.cx_fa_view_chat_chatting__input);
        mVoicePanel = (LinearLayout)mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout2);
        CxInputPanel.sInputPanelUse = S_INPUTPANEL_CURRENT_VIEW;
        // mRecordBar =
        // (ImageButton)mInputPanel.findViewById(R.id.cx_fa_widget_input_panel__layout2_button2);
        mRecordView = (LinearLayout)view.findViewById(R.id.cx_fa_view_chat_chatting_record_include);
        mChatRelativeLayout = (RelativeLayout)view
                .findViewById(R.id.cx_fa_view_chat_chatting_relativelayout);
        
//        // 设置聊天的背景，如果用户未设置过，则默认取配置文件中的第一张
//        // loadBackground(RkGlobalParams.getInstance().getChatBackgroundBig());
//        String chatBg = RkGlobalParams.getInstance().getChatBackgroundBig();
//        if(chatBg!=null && !chatBg.equals("null")){
//        	mBackgroundRkImageView.setChatbgImage(chatBg, false,320, ChatFragment.this, "chat_bg", getActivity());
//        }else{
//        	// 如果该用户未设置背景，就取数组中的第一个
////        	TypedArray ta = getResources().obtainTypedArray(R.array.cx_fa_chat_bg);
//        	
//        	mBackgroundRkImageView.setImageDrawable(getResources().getDrawable(R.drawable.cx_fa_role_chatbg_default));
//        }
        
        mRecordImageView = (ImageView)mRecordView
                .findViewById(R.id.cx_fa_dialog_chat_record_imageview);
        mChatRecordRelativeLayout = (RelativeLayout)mRecordView
                .findViewById(R.id.cx_fa_dialog_chat_record_relativelayout);
        mRecordRemainTimeTextView = (TextView)mRecordView
                .findViewById(R.id.cx_fa_dialog_chat_record_recordtime_textview);
        mRecordCancelTip = (TextView)mRecordView
                .findViewById(R.id.cx_fa_dialog_chat_record_textview_tip);

        mActionBar = new QuickActionBar(R.layout.cx_fa_widget_quick_action_bar,
                QuickActionBar.DISPLAY_X_EXACT_MIDDLE_OF_ANCHOR,
                QuickActionBar.DISPLAY_Y_ABOVE_OF_ANCHOR);
        final View actionBarView = LayoutInflater.from(getActivity()).inflate(
                R.layout.cx_fa_widget_quick_action_bar, null);
        mDelBtn = (Button)actionBarView.findViewById(R.id.button1);
        mCopyBtn = (Button)actionBarView.findViewById(R.id.button2);
        mDumpBtn = (Button)actionBarView.findViewById(R.id.button3);
        // auto scroll to list bottom
        // mListView.setStackFromBottom(true);
        // mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        mChatRecordLayout = (RelativeLayout)view
                .findViewById(R.id.cx_fa_view_chat_chatting_record_relativelayout);
        CxLog.v(TAG, "mRecordView = " + mRecordView);

        // delete message
        mActionBar.setOnClickListener(R.id.button1, new OnClickListener() {

            @Override
            public void onClick(View v) {
                CxLog.v(TAG, "v tag = " + v.getTag());
                mActionBar.dismiss();
                final ChatLogAdapter tag = (ChatLogAdapter)v.getTag();
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.cx_fa_chat_del_msg)
                        .setPositiveButton(R.string.cx_fa_del_msg,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CxLog.v(TAG, "DEL THIS MESSAGE" + tag.mMsgId);
                                        // drop(tag.mId, tag.mBaseTimeStamp);
                                        delMessage(tag.mMsgId);
                                    }
                                }).setNegativeButton(R.string.cx_fa_cancel_text, null).show();
            }

        });
        // copy message
        mActionBar.setOnClickListener(R.id.button2, new OnClickListener() {

            @Override
            public void onClick(View v) {
                CxLog.v(TAG, "v tag = " + v.getTag());
                mActionBar.dismiss();
                final ChatLogAdapter tag = (ChatLogAdapter)v.getTag();

                copyText(getActivity(), tag.mMsgText);
                Toast.makeText(getActivity(), tag.mMsgText, Toast.LENGTH_SHORT).show();
            }

        });
        // dump message
        mActionBar.setOnClickListener(R.id.button3, new OnClickListener() {

            @Override
            public void onClick(View v) {
                CxLog.v(TAG, "v tag = " + v.getTag());
                mActionBar.dismiss();
                final ChatLogAdapter tag = (ChatLogAdapter)v.getTag();
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.cx_fa_chat_dump_msg)
                        .setPositiveButton(R.string.cx_fa_confirm_text,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CxLog.v(TAG, "DUMP THIS MESSAGE" + tag.mMsgId);
                                        List<String> imagePaths = new ArrayList<String>();
//                                        RkResourceManager resourceManager = RkResourceManager
//                                                .getInstance(ChatFragment.this, "thunb",
//                                                        ChatFragment.this.getActivity());
                                        if (!TextUtils.isEmpty(tag.mImagePath)) {
                                            /*if (tag.mImagePath.startsWith("http")) {
                                                // imagePaths.add(getFilePath(tag.mImagePath));
                                                if (resourceManager.exists(Uri
                                                        .parse(tag.mImagePath))) {
                                                    File file = resourceManager.getFile(Uri
                                                            .parse(tag.mImagePath));
                                                    imagePaths.add(Uri.decode(file.getPath()));
                                                }
                                            } else {
                                                imagePaths.add(tag.mImagePath
                                                        .replace("file://", ""));
                                            }*/
                                        	imagePaths.add(tag.mImagePath
                                                    .replace("file://", ""));
                                        }
                                        // dump message
                                        dumpMessage(tag.mMsgId, tag.mMsgText, imagePaths);
                                    }
                                }).setNegativeButton(R.string.cx_fa_cancel_text, null).show();
            }

        });

        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent m) {
                // RkLog.v(TAG, "chatview x>>>" + m.getX() + " : y>>>" +
                // m.getY());

                mInputPanel.setDefaultMode();
                return false;
            }
        });
        mListView.setOnHeaderRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshNewChatData();
            }

        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int firstVisiblePosition = mListView.getFirstVisiblePosition(); // find
                                                                                // the
                                                                                // first
                                                                                // visible
                                                                                // pos;
                int visiblePosition = position - firstVisiblePosition;

                CxLog.v(TAG, "parent=" + parent + " view=" + view + " position=" + position
                        + " id=" + id + " visiblePosition=" + visiblePosition);

                view = mListView.getChildAt(visiblePosition);
                if (view == null) {
                    return true;
                }

                final ChatLogAdapter tag = (ChatLogAdapter)view.getTag();
                if (tag == null) {
                    return true;
                }

                CxLog.v(TAG, "chat type=" + tag.mChatType);

                switch (tag.mChatType) {
                    case Message.MESSAGE_TYPE_PHRASE:
                        mCopyBtn.setVisibility(View.GONE);
                        mDumpBtn.setVisibility(View.GONE);
                        mActionBar.show(view, actionBarView);
                        break;
                    case Message.MESSAGE_TYPE_FACE:
                    case Message.MESSAGE_TYPE_EMOTION:
                        mCopyBtn.setVisibility(View.GONE);
                        mDumpBtn.setVisibility(View.GONE);
                        mActionBar.show(view, actionBarView);
                        break;
                    case Message.MESSAGE_TYPE_PICTURE:
                        mCopyBtn.setVisibility(View.GONE);
                        if(tag.isPicture){
                        	 mDumpBtn.setVisibility(View.VISIBLE);
                        }else{
                        	mDumpBtn.setVisibility(View.GONE);
                        }
                       
                        mActionBar.show(view, actionBarView);
                        break;
                    case Message.MESSAGE_TYPE_TEXT:
                        mCopyBtn.setVisibility(View.VISIBLE);
                        mDumpBtn.setVisibility(View.VISIBLE);
                        mActionBar.show(view, actionBarView);
                        break;
                    case Message.MESSAGE_TYPE_VOICE:
                    case Message.MESSAGE_TYPE_FEED:
                    case Message.MESSAGE_TYPE_SYSTEM:
                        mCopyBtn.setVisibility(View.GONE);
                        mDumpBtn.setVisibility(View.GONE);
                        mActionBar.show(view, actionBarView);
                        break;
                    case Message.MESSAGE_TYPE_LOCATION:
                        mCopyBtn.setVisibility(View.VISIBLE);
                        mDumpBtn.setVisibility(View.GONE);
                        mActionBar.show(view, actionBarView);
                        break;
                    case Message.MESSAGE_TYPE_TABLOID:
                    	 mCopyBtn.setVisibility(View.GONE);
                         mDumpBtn.setVisibility(View.GONE);
                         mActionBar.show(view, actionBarView);
                         break;
                   case Message.MESSAGE_TYPE_ANIMATION:
                   	 	mCopyBtn.setVisibility(View.GONE);
                        mDumpBtn.setVisibility(View.GONE);
                        mActionBar.show(view, actionBarView);
                        break;
                   case Message.MESSAGE_TYPE_GUESS_REQUEST :
                  	 	mCopyBtn.setVisibility(View.GONE);
                       mDumpBtn.setVisibility(View.GONE);
                       mActionBar.show(view, actionBarView);
                       break;
                   case Message.MESSAGE_TYPE_GUESS_RESPONSE :
                 	 	mCopyBtn.setVisibility(View.GONE);
	                    mDumpBtn.setVisibility(View.GONE);
	                    mActionBar.show(view, actionBarView);
	                    break;
                    default:
                        mCopyBtn.setVisibility(View.VISIBLE);
                        mDumpBtn.setVisibility(View.VISIBLE);
                        mActionBar.show(view, actionBarView);
                        break;
                }
                return false;
            }
        });

        mListView.setSelector(R.color.cx_fa_co_transparent);

        mInputPanel.setOnEventListener(new OnEventListener() {

            @Override
            public void onButton1Click(View button) {
                CxLog.i(TAG, "click photo button");
                Intent intent = new Intent(getActivity(), ActivityCamera.class);
                ActivitySelectPhoto.kIsCallPhotoZoom = false;
                ActivitySelectPhoto.kIsCallFilter = true;
                ActivitySelectPhoto.kIsCallSysCamera = false;
                ActivitySelectPhoto.kChoseSingle = true;
                startActivityForResult(intent, SEND_IMAGE_CODE);
            }

            @Override
            public void onButton2Click(View button) {
                // if (mOnEventListener != null) {
                // mOnEventListener.onButton2Click(button);
                // call google map
                Intent callMap = new Intent(getActivity(), MyLocation.class);
                getActivity().startActivity(callMap);

                // }

            }

            @Override
            public int onMessage(final String msg, int flag) {

                if (mOnEventListener != null) {
                    mOnEventListener.onMessage(msg, flag);
                }
                sendMessage(msg, flag);

                if (flag == 0)
                    return 0;
                else
                    return 1;
            }

            @Override
            public void onStartRecordEvent(View v, MotionEvent event) {
                // start record
//                RkLog.v(TAG, "come onStartRecordEvent");               
//                RkInputPanel.mRecordBar.setBackgroundResource(R.drawable.chatview_voice_h);
                mInputPanel.changeRecordBarBackground(true);
                CxGlobalParams.getInstance().setRecorderFlag(true);
                ((CxMain)getActivity()).availeChildSlide();
                mChatRecordLayout.setVisibility(View.VISIBLE);
                mRecordView.setVisibility(View.VISIBLE);
                mChatRecordRelativeLayout.setBackgroundResource(R.drawable.pub_recorder);
                mRecordCancelTip.setText(R.string.cx_fa_chat_record_tip);
                mRecordStart = true;               
                startRecord();
                startTimer();
                 android.os.Message msg0 = android.os.Message.obtain(mChatHandler,
                         UPDATE_RECORD_TIME);
//                 msg0.sendToTarget();
            }

            @Override
            public void onAcionMoveEvent(View v, MotionEvent m) {
                moveRecordEvent(m);
            }

            @Override
            public void onStopMoveEvent(View v, MotionEvent m) {
//                RkLog.v(TAG, "come onStopRecordEvent");
                stopRecordEvent(m);
            }

            @Override
            public void onOtherEvent(View v, MotionEvent m) {
                stopRecordEvent(m);
            }

            @Override
            public void onButton0Click(View button) {
                Intent intent = new Intent(getActivity(), ActivityGallery.class);
                ActivitySelectPhoto.kIsCallPhotoZoom = false;
                ActivitySelectPhoto.kIsCallFilter = true;
                ActivitySelectPhoto.kIsCallSysCamera = false;
                ActivitySelectPhoto.kChoseSingle = true;
                startActivityForResult(intent, SEND_IMAGE_CODE);
            }

        });

        initData();
        installListener();
        return view;
    }

    private void refreshNewChatData() {
        if (!mSmartCursor.gainMore()) {
            mListView.onRefreshComplete();
            return;
        }

        Cursor cursor = mSmartCursor.getCursor();
        // not auto scroll to list bottom
        // mListView.setStackFromBottom(false);
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mChatLogAdapter.updateMessages(cursor, mListView, mContext, mChatFragment, 0);

    }

    private void delMessage(int msgId) {
        String sql = "delete from messages where k=" + msgId;
        mDB.execSQL(sql);
        updateChatView(true);
    }

    @SuppressWarnings("deprecation")
    public static void copyText(Context context, String text) {
        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    private void dumpMessage(int id, String text, List<String> photo) {
        try {
            CxZoneApi.getInstance().requestSendFeed(text, photo, 0, 0, sendCallback); //此处转的都不是公开的帖子
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONCaller sendCallback = new JSONCaller() {

        @Override
        public int call(Object result) {
            if (null == result) {
                // TODO分享失败
                return -1;
            }
            CxZoneSendFeed sendResult = null;
            try {
                sendResult = (CxZoneSendFeed)result;
            } catch (Exception e) {
            }
            if (null == sendResult) {
                // 分享失败
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Auto-generated method stub
                        Toast toast = Toast.makeText(getActivity(),
                                getString(R.string.cx_fa_chat_dump_fail_text), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
                return -2;
            }
            int rc = sendResult.getRc();
            if (0 != rc) {
                // 提示服务器返回的原因
                return rc;
            }
            // 把数据告知二人空间界面
            // FeedListData feedData = sendResult.getData(); //modify by niechao
            // 2013.7.13
            // RkZoneParam.getInstance().insertFeedData(feedData);
            if (null != sendResult.getData()) {
                CxZoneParam.getInstance().setFeedsData(sendResult.getData());
            }
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Auto-generated method stub
                    Toast toast = Toast.makeText(getActivity(),
                            getString(R.string.cx_fa_chat_dump_success_text), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });

            return 0;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CxLog.v(TAG, "requestCode = " + requestCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (null == data) {
            return;
        }
        if (requestCode == SEND_IMAGE_CODE) {

            final String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI)
                    .replace("file://", "");
            final String imageUri = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
            sendMessage(imageUri, 3);
        }
        if (requestCode == UPDATE_HEADIMAGE_CODE) {
            // update headimage
            final String imagePath = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI)
                    .replace("file://", "");
            final String imageUri = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
            CxLog.i(TAG, "imagePath=" + imagePath);
            try {
                CxSendImageApi.getInstance().sendHeadImage(imagePath,
                        CxSettingsParser.SendHeadImageType.HEAD_ME, modifyHeadImag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateHeadImage() {
        Intent intent = new Intent(getActivity(), ActivitySelectPhoto.class);
        ActivitySelectPhoto.kIsCallPhotoZoom = true;
        ActivitySelectPhoto.kIsCallFilter = false;
        ActivitySelectPhoto.kIsCallSysCamera = true;
        ActivitySelectPhoto.kChoseSingle = true;
        startActivityForResult(intent, UPDATE_HEADIMAGE_CODE);
    }

    private void startRecord() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.cx_fa_alert_dialog_tip)
                    .setMessage(R.string.cx_fa_unuseable_extralstorage)
                    .setPositiveButton(R.string.cx_fa_confirm_text,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Auto-generated method stub
                                    mRecordView.setVisibility(View.GONE);
//                                    RkInputPanel.mRecordBar
//                                            .setBackgroundResource(R.drawable.chatview_voice);
                                    mInputPanel.changeRecordBarBackground(false);
                                }
                            }).show();
            return;
        }
        // mChatRecordLayout.setVisibility(View.VISIBLE);
        // mRecordView.setVisibility(View.VISIBLE);
        // mChatRecordRelativeLayout.setBackgroundResource(R.drawable.pub_recorder);
        // mRecordCancelTip.setText(R.string.cx_fa_chat_record_tip);
        // stopVoice();
//        mRecordStart = true;
        try {
            File storageDirectory = new File(CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH,
                    CxGlobalConst.S_CHUXIN_AUDIO_CACHE_NAME);
            boolean cacheable = CxBaseDiskCache.createDirectory(storageDirectory);
            if (!cacheable) {
                try {
                    throw new Exception("the sd card is not useable");
                } catch (Exception e) {
                    CxLog.e("startRecord", "" + e.getMessage());
                }
            }

            String fileName = CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH + "/"
                    + CxGlobalConst.S_CHUXIN_AUDIO_CACHE_NAME + "/" + System.currentTimeMillis()
                    + ".mp3";
            mRecMicToMp3 = new RecMicToMp3(fileName, 16000);
            mRecMicToMp3.start();
            mRecorderStartTime = System.currentTimeMillis();
            mSoundFilePath = fileName;
        } catch (IllegalStateException e) {
            CxLog.e("startRecord", "" + e.getMessage());
        } catch (Exception e) {
            CxLog.e("startRecord", "" + e.getMessage());
        }
        mRecordRemainTimeTextView.setText(String.format(
                getResources().getString(R.string.cx_fa_chat_record_time_remianing),
                (90 - (System.currentTimeMillis() - mRecorderStartTime) / 1000)));
        int vuSize = MAX_VU_SIZE * mRecMicToMp3.getVolume() / 100;
//        RkLog.d(TAG, "vuSize>>>" + vuSize);
        int resId = getResources().getIdentifier(sRecordingDrawables[vuSize], "drawable",
                getActivity().getPackageName());
        mRecordImageView.setImageResource(resId);
    }

    private void stopRecord() {
        if(null != mRecMicToMp3){
            mRecMicToMp3.stop();
            mRecorderStopTime = System.currentTimeMillis();
        }
    }

    public void sendRecord(final String filePath, final int audioLength, final int effect) {
        // final String filePath = mSoundFile.getAbsolutePath();
        CxLog.v(TAG, "filePath=" + filePath);
        // final int audioLength = (int) ((mRecorderStopTime -
        // mRecorderStartTime) / 1000);
        final String msg = "{\"url\": \"" + filePath + "\",\"len\": " + audioLength + ",\"type\": " + effect + "}";
        CxLog.i(TAG, "msg:" + msg);
        final long id = System.currentTimeMillis();
        Message.buildMessageObject(msg, "audio", 0, id).put();
        updateChatView();
        try {
            CxSendImageApi.getInstance().sendAudioFile(filePath, audioLength, effect, new JSONCaller() {

                @Override
                public int call(Object result) {
                    // Auto-generated method stub
                    JSONObject data = (JSONObject)result;
                    try {
                        // String msg = "{\"url\": \"" + filePath
                        // + "\",\"len\": " + audioLength + "}";
                        // RkLog.v(TAG, "msg:" + msg);
                        if (data.has("msgid")) {
                            Message.buildMessageObject(msg, "audio", 1, id).put();
                        } else {
                            Message.buildMessageObject(msg, "audio", 2, id).put();
                        }
                        updateChatView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * send message
     * 
     * @param msg
     * @param flag text:0, phrase:1, face:2, photo:3, record:4, location:5
     * @param msgid message id
     */
    public void sendMessage(final String msg, int flag) {
        if (flag == 0) {
            // 需要对编辑的消息内容中包含的双引号特殊字符进行转换，再存储发送
            final String msgContent = msg.replace("\"", "\\\"");
            final long id = System.currentTimeMillis();
            Message message = Message.buildMessageObject(msgContent, "text", 0, id);
            message.put();
            updateChatView();
            ChatApi.getInstance().doSendTextMessage(msg, new JSONCaller() {

                @Override
                public int call(Object result) {
                    JSONObject data = (JSONObject)result;
                    try {
                        if (data.has("msgid")) { // send success
                            Message message = Message.buildMessageObject(msgContent, "text", 1, id);
                            message.put();
                        } else { // send failed
                            Message message = Message.buildMessageObject(msgContent, "text", 2, id);
                            message.put();
                        }
                        updateChatView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }

            });
        } else if (flag == 1) {
            final long id = System.currentTimeMillis();
            Message.buildMessageObject(msg, "phrase", 0, id).put();
            updateChatView();
            ChatApi.getInstance().doSendPhraseMessage(msg, new JSONCaller() {

                @Override
                public int call(Object result) {
                    JSONObject data = (JSONObject)result;
                    try {
                        if (data.has("msgid")) {
                            Message.buildMessageObject(msg, "phrase", 1, id).put();
                        } else {
                            Message.buildMessageObject(msg, "phrase", 2, id).put();
                        }
                        updateChatView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }

            });
        } else if (flag == 2) {
            final long id = System.currentTimeMillis();
            Message.buildMessageObject(msg, "face", 0, id).put();
            updateChatView();
            ChatApi.getInstance().doSendFaceMessage(msg, new JSONCaller() {

                @Override
                public int call(Object result) {
                    JSONObject data = (JSONObject)result;
                    try {
                        if (data.has("msgid")) {
                            Message.buildMessageObject(msg, "face", 1, id).put();
                        } else {
                            Message.buildMessageObject(msg, "face", 2, id).put();
                        }
                        updateChatView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }

            });
        } else if (flag == 3) {
            final String imagePath = msg.replace("file://", "");
            final String message = "{\"photo_mid\": \"\",\"photo_big\": \"" + msg + "\"}";
            CxLog.v(TAG, "message:" + message);
            final long id = System.currentTimeMillis();
            Message.buildMessageObject(message, "photo", 0, id).put();
            updateChatView();
            try {
                CxSendImageApi.getInstance().sendChatImage(imagePath, new JSONCaller() {

                    @Override
                    public int call(Object result) {
                        // Auto-generated method stub
                        JSONObject data = (JSONObject)result;
                        try {
                            // String message =
                            // "{\"photo_mid\": \"\",\"photo_big\": \""
                            // + msg + "\"}";
                            // RkLog.v(TAG, "message:" + message);
                            if (data.has("msgid")) {
                                Message.buildMessageObject(message, "photo", 1, id).put();
                            } else {
                                Message.buildMessageObject(message, "photo", 2, id).put();
                            }
                            updateChatView();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (flag == 5) {
        } else if (flag == 11) {
            final long id = System.currentTimeMillis();
            Message.buildMessageObject(msg, "emotion", 0, id).put();
            updateChatView();
            ChatApi.getInstance().doSendEmotionMessage(msg, new JSONCaller() {

                @Override
                public int call(Object result) {
                    JSONObject data = (JSONObject)result;
                    CxLog.i("ChatFragment_men", data.toString());
                    try {
                        if (data.has("msgid")) {
                            Message.buildMessageObject(msg, "emotion", 1, id).put();
                        } else {
                            Message.buildMessageObject(msg, "emotion", 2, id).put();
                        }
                        updateChatView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }

            });
        }else if(flag==Message.MESSAGE_TYPE_ANIMATION){
        	String[] arr = msg.split(",");
        	int animation_id		= Integer.valueOf(arr[0]);
        	int effect					= Integer.valueOf(arr[1]);
        	
        	final long id = System.currentTimeMillis();
        	Message.buildMessageObject(msg, "animation", 0, id).put();
        	
        	 updateChatView();
             ChatApi.getInstance().doSendAnimationMessage(animation_id, effect,  new JSONCaller() {

                 @Override
                 public int call(Object result) {
                     JSONObject data = (JSONObject)result;
                     try {
                    	 	if(data.has("rc")){
                    	 		// 判断对方是否支持"抽鞭子"和"弹脑壳"
                    	 		int rc = data.getInt("rc");
                    	 		if(rc==3000){			// 对方版本不支持
	                    	 		String msg2 = msg + ",3000";
	                    	 		Message.buildMessageObject(msg2, "animation", 1, id).put();
                    	 		}else{
                    	 			Message.buildMessageObject(msg, "animation", 2, id).put();			// 发送失败
                    	 		}
                    	 	}else{
		                    	 if (data.has("msgid")) {
		                             Message.buildMessageObject(msg, "animation", 1, id).put();
		                         } else {
		                             Message.buildMessageObject(msg, "animation", 2, id).put();
		                         }
                    	 	}
                             updateChatView();
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     return 0;
                 }

             });
        }else if(flag==Message.MESSAGE_TYPE_GUESS_REQUEST){
        	String[] arr = msg.split(",");
        	String guess_id	= arr[0];
        	int value1			= Integer.valueOf(arr[1]);
        	
        	final long id = System.currentTimeMillis();
        	Message.buildMessageObject(msg, "guess_request", 0, id).put();
        	
        	 updateChatView();
             ChatApi.getInstance().doSendGuessRequestMessage(guess_id, value1, new JSONCaller() {

                 @Override
                 public int call(Object result) {
                     JSONObject data = (JSONObject)result;
                     try {
                    	 	if(data.has("rc")){
                    	 		// 判断对方是否支持"猜拳"
                    	 		int rc = data.getInt("rc");
                    	 		if(rc==3000){			// 对方版本不支持
	                    	 		String msg2 = msg + ",3000";
	                    	 		Message.buildMessageObject(msg2, "guess_request", 1, id).put();
                    	 		}else{
                    	 			Message.buildMessageObject(msg, "guess_request", 2, id).put();;			// 发送失败
                    	 		}
                    	 	}else{
		                    	 if (data.has("msgid")) {
		                             Message.buildMessageObject(msg, "guess_request", 1, id).put();
		                         } else {
		                             Message.buildMessageObject(msg, "guess_request", 2, id).put();		// 发送失败
		                         }
                    	 	}
                             updateChatView();
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     return 0;
                 }

             });
        }else if(flag==Message.MESSAGE_TYPE_GUESS_RESPONSE){
        	String[] arr 					= msg.split(",");
        	final String oldMsgId	= arr[0];
        	String guess_id			= arr[1];
        	int value1					= Integer.valueOf(arr[2]);
        	int value2					= Integer.valueOf(arr[3]);
        	int result						= Integer.valueOf(arr[4]);
        	
        	final long id = System.currentTimeMillis();
        	final Message resMsg = Message.buildMessageObject(msg, "guess_response", 0, id);
        	//TODO 有空指针异常，等待测试复现，然后处理
        	try {
				resMsg.mData.put("request_msg_id", oldMsgId);		// 加入请求时的msgId, 主要用于信息发送失败时，重发依旧能把发起猜拳的信息置为已回拳
			} catch (JSONException e) {
				CxLog.e(TAG, e.getMessage());
			}
        	resMsg.put();
        	
        	 updateChatView();
             ChatApi.getInstance().doSendGuessResponseMessage(guess_id, value1, value2, result, new JSONCaller() {

                 @Override
                 public int call(Object result) {
                     JSONObject data = (JSONObject)result;
                     try {
	                    	 // 将收到的猜拳请求的msg，置为已回复 （不管回复是否成功，都先设为已回复）
	                         Message message 		= new Message();
	                         Model resSourceMsg 	= message.get(oldMsgId);
	                         if(resSourceMsg!=null){
	                        	 resSourceMsg.mData.put("is_reply", true);
	                        	 resSourceMsg.update();
	                         }
                         
                    	 	if(data.has("rc")){
                    	 		// 判断对方是否支持"猜拳"
                    	 		int rc = data.getInt("rc");
                    	 		if(rc==3000){			// 对方版本不支持
	                    	 		String msg2 = msg + ",3000";
	                    	 		Message.buildMessageObject(msg2, "guess_response", 1, id).put();
                    	 		}else{
                    	 			// 发送失败
                    	 			//Message.buildMessageObject(msg, "guess_response", 2, id).put();	
                    	 			resMsg.mData.put("send_success", 2);
                    	 			resMsg.update();
                    	 		}
                    	 	}else{
		                    	 if (data.has("msgid")) {
		                             Message.buildMessageObject(msg, "guess_response", 1, id).put();
		                         } else {
		                            resMsg.mData.put("send_success", 2);
                    	 			resMsg.update();
		                         }
                    	 	}
                             updateChatView();
                     } catch (Exception e) {
                    	 CxLog.e(TAG, e.getMessage());
                     }
                     return 0;
                 }

             });
        }  
    }

    /**
     * send message
     * 
     * @param msg
     * @param flag text:0, phrase:1, face:2, photo:3, record:4, location:5
     * @param msgid message id
     */
    public void sendMessage(final String msg, int flag, int audioLength, final int effect) {
        if (flag == 4) {
            sendRecord(msg, audioLength, effect);
        }
    }

    /**
     * send message
     * 
     * @param msg
     * @param flag text:0, phrase:1, face:2, photo:3, record:4, location:5
     * @param msgid message id google map send location infomation
     */
    public void sendMessage(final String msg, int flag, final float lat, final float lon) {
        final long id = System.currentTimeMillis();
        Message message = Message.buildMessageObject(msg, "geo", lat, lon, 0, id);
        message.put();
        updateChatView();
        if (flag == 5) {
            ChatApi.getInstance().doSendLoctionMessage(msg, lat, lon, new JSONCaller() {

                @Override
                public int call(Object result) {
                    JSONObject data = (JSONObject)result;
                    try {
                        if (data.has("msgid")) { // send success
                            Message message = Message.buildMessageObject(msg, "geo", lat, lon, 1,
                                    id);
                            message.put();
                        } else { // send failed
                            Message message = Message.buildMessageObject(msg, "geo", lat, lon, 2,
                                    id);
                            message.put();
                        }
                        updateChatView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }

            });
        }
    }

    /**
     * resend message
     * 
     * @param msg
     * @param flag text:0, phrase:1, face:2, photo:3, record:4, location:5
     * @param msgid
     */
    public void reSendMessage(final String msg, final int flag, final int msgid) {
        CxLog.d(TAG, "DEL THIS MESSAGE" + msgid);
        delMessage(msgid);
        sendMessage(msg, flag);
    }

    /**
     * resend message
     * 
     * @param msg
     * @param flag text:0, phrase:1, face:2, photo:3, record:4, location:5
     * @param msgid
     */
    public void reSendMessage(final String msg, final int flag, final int msgid,
            final int audioLenght, final int effect) {
//        new AlertDialog.Builder(getActivity())
//                .setTitle(R.string.cx_fa_alert_dialog_tip)
//                .setMessage(R.string.cx_fa_chat_resend_msg)
//                .setPositiveButton(R.string.cx_fa_chat_button_resend_text,
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                RkLog.d(TAG, "DEL THIS MESSAGE" + msgid);
                                delMessage(msgid);
                                sendMessage(msg, flag, audioLenght, effect);
//                            }
//                        }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();
    }

    /**
     * resend message
     * 
     * @param msg
     * @param flag text:0, phrase:1, face:2, photo:3, record:4, location:5
     * @param msgid
     */
    public void reSendMessage(final String msg, final int flag, final int msgid, final float lat,
            final float lon) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.cx_fa_alert_dialog_tip)
                .setMessage(R.string.cx_fa_chat_resend_msg)
                .setPositiveButton(R.string.cx_fa_chat_button_resend_text,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CxLog.d(TAG, "DEL THIS MESSAGE" + msgid);
                                delMessage(msgid);
                                sendMessage(msg, flag, lat, lon);
                            }
                        }).setNegativeButton(R.string.cx_fa_cancel_button_text, null).show();
    }

    /**
     * clear all message func
     */
    public void clearAllMessages() {
        if (null == mChatMessage) {
            mChatMessage = new Message(null, getActivity());
        }
        mChatMessage.dropAll();
    }

    public void gotoOtherFragment(String category) {
    	
        if ("reminder".equals(category)) {
//            ((RkMain)getActivity()).changeFragment(RkMain.REMINDER);
//        	Intent reminderIntent = new Intent(getActivity(), RkReminderList.class);
//        	startActivity(reminderIntent);
        } else if ("space".equals(category)) {
        	int version_type = CxGlobalParams.getInstance().getVersion_type();
        	if(version_type==1){
        		((CxMain)getActivity()).menuEvent(CxMain.ZONE2);
        	}else{
        		((CxMain)getActivity()).menuEvent(CxMain.ZONE);
        	}
        } else if ("rkmate".equals(category)) {
        	Intent mateProfileIntent = new Intent(getActivity(), CxFamilyInfoActivity.class);
            mateProfileIntent.putExtra(CxMateParams.FAMILY_FLAG, 1);
            startActivity(mateProfileIntent);
            getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
        } else if("settings".equals(category)){
            Intent mateProfileIntent = new Intent(getActivity(), CxFamilyInfoActivity.class);
            mateProfileIntent.putExtra(CxMateParams.FAMILY_FLAG, 2);
            startActivity(mateProfileIntent);
            getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
        }
    }
    
    /**
     * 系统化消息跳转逻辑
     * @param redirect
     */
    public void redirectFragment(int redirect) {
    	int version_type = CxGlobalParams.getInstance().getVersion_type();
        if(SYSTEM_MESSAGE_REDIRECT_SPACE == redirect){
        	if(version_type==1){
        		((CxMain)getActivity()).menuEvent(CxMain.ZONE2);
        	}else{
        		((CxMain)getActivity()).menuEvent(CxMain.ZONE);
        	}
        } else if(SYSTEM_MESSAGE_REDIRECT_NEIGHBOUR == redirect){
            ((CxMain)getActivity()).menuEvent(CxMain.NEIGHBOUR);
        } else if(SYSTEM_MESSAGE_REDIRECT_REMINDER == redirect){
//            Intent reminderIntent = new Intent(getActivity(), RkReminderList.class);
//            startActivity(reminderIntent);
        } else if(SYSTEM_MESSAGE_REDIRECT_RKMATE == redirect){
            Intent mateProfileIntent = new Intent(getActivity(), CxFamilyInfoActivity.class);
            mateProfileIntent.putExtra(CxMateParams.FAMILY_FLAG, 1);
            startActivity(mateProfileIntent);
            getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
        } else if(SYSTEM_MESSAGE_REDIRECT_SETTING == redirect){
            Intent mateProfileIntent = new Intent(getActivity(), CxFamilyInfoActivity.class);
            mateProfileIntent.putExtra(CxMateParams.FAMILY_FLAG, 2);
            startActivity(mateProfileIntent);
            getActivity().overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
        } else if(SYSTEM_MESSAGE_REDIRECT_ACCOUNTS == redirect){
        	if(version_type==1){
        		((CxMain)getActivity()).menuEvent(CxMain.ACCOUNT2);
        	}else{
        		((CxMain)getActivity()).menuEvent(CxMain.ACCOUNT);
        	}
        } else if(SYSTEM_MESSAGE_REDIRECT_ANSWER == redirect){
            Intent mateProfileIntent = new Intent(getActivity(), CxAnswerActivity.class);
            startActivity(mateProfileIntent);
            getActivity().overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
        	
//        	((CxMain)getActivity()).menuEvent(CxMain.ANSWER);
        } else if(SYSTEM_MESSAGE_REDIRECT_CALENDAR == redirect){
        	if(version_type==1){
        		((CxMain)getActivity()).menuEvent(CxMain.CALENDAR2);
        	}else{
        		((CxMain)getActivity()).menuEvent(CxMain.CALENDAR);
        	}
            
        } else if(SYSTEM_MESSAGE_REDIRECT_KIDS == redirect){
            if(version_type!=1){
                ((CxMain)getActivity()).menuEvent(CxMain.KID);
            }
        }
        
   }

    // open google locaion map
    public void openLoactionMap(float lat, float lon, String location) {
        // open map
        Intent intent = new Intent(getActivity(), MyLocation.class);
        Bundle bundle = new Bundle();
        bundle.putString(CxGlobalConst.S_LOCATION_TEXT, location);
        bundle.putFloat(CxGlobalConst.S_LOCATION_LAT, lat);
        bundle.putFloat(CxGlobalConst.S_LOCATION_LON, lon);
        bundle.putInt(CxGlobalConst.S_LOCATION_TYPE, 1);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void callPhone(String phone) {
        Uri callUri = Uri.parse("tel:" + phone);
        Intent returnIntent = new Intent(Intent.ACTION_CALL, callUri);
        startActivity(returnIntent);
    }

    JSONCaller modifyHeadImag = new JSONCaller() {

        @Override
        public int call(Object result) {
        	if (null == getActivity()) {
				return -1;
			}
            new Handler(getActivity().getMainLooper()) {
                public void handleMessage(android.os.Message msg) {
                    CxLoadingUtil.getInstance().dismissLoading();
                };
            }.sendEmptyMessage(1);
            if (null == result) {
                displayModifyHead(getString(R.string.cx_fa_modify_headimg_fail));
                return -1;
            }
            CxChangeHead changeHeadResult = null;
            try {
                changeHeadResult = (CxChangeHead)result;
            } catch (Exception e) {
            }
            if (null == changeHeadResult) {
                displayModifyHead(getString(R.string.cx_fa_modify_headimg_fail));
                return -2;
            }
            if (0 != changeHeadResult.getRc()) {
                displayModifyHead(changeHeadResult.getMsg());
                return changeHeadResult.getRc();
            }
            // 修改头像成功
            CxChangeHeadDataField headData = changeHeadResult.getData();
            CxGlobalParams.getInstance().setIconSmall(headData.getIcon_small());
            CxGlobalParams.getInstance().setIconBig(headData.getIcon_big());
            CxGlobalParams.getInstance().setIconMid(headData.getIcon_mid());

            return 0;
        }
    };

    private void displayModifyHead(String info) {
        android.os.Message msg = new android.os.Message();
        msg.obj = info;
        new Handler(getActivity().getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                if ((null == msg) || (null == msg.obj)) {
                    return;
                }
                Toast ts = Toast.makeText(getActivity(), msg.obj.toString(), 1000);
                ts.setGravity(Gravity.CENTER, 0, 0);
                ts.show();
            };
        }.sendMessage(msg);
    }

//    public String getFilePath(String imageName) {
//        String folderName = "chuxin/image";
//        File path = Environment.getExternalStorageDirectory();
//        File file = new File(path, folderName + "/" + imageName.replace("http://", "http:////"));
//        String imagepath = file.getPath();
//        return imagepath;
//    }

    @Override
    public void onClick(View v) {
        // Auto-generated method stub
        switch (v.getId()) {
            case R.id.cx_fa_chat_head_menu_btn:
                mInputPanel.setDefaultMode(); // add by shichao 解决表情状态滑动崩溃
                ((CxMain)getActivity()).toggleMenu();
                break;
            case R.id.cx_fa_chat_head_call_btn:
            	int single_mode = CxGlobalParams.getInstance().getSingle_mode();
                if(single_mode==1){               	
                	Intent inviteIntent=new Intent(getActivity(),CxPairActivity.class);
    				startActivity(inviteIntent);
    				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                }else{
                	showCallDialog();
                }                
                break;
        }
    }

    private void showCallDialog() {
//        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
//        ad.setTitle(R.string.cx_fa_alert_dialog_tip);
//        final String phone = RkMateParams.getInstance().getMateMobile();
//        if (TextUtils.isEmpty(phone)) {
//            ad.setMessage(R.string.cx_fa_chat_not_call_msg);
//            ad.setPositiveButton(R.string.cx_fa_chat_call_setphone_btn,
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent mateProfileIntent = new Intent(getActivity(),
//                                    RkMateActivity.class);
//                            startActivity(mateProfileIntent);
//                            getActivity().overridePendingTransition(android.R.anim.slide_in_left,
//                                    android.R.anim.slide_out_right);
//
//                        }
//                    });
//        } else {
//            ad.setMessage(String.format(getResources().getString(R.string.cx_fa_chat_call_msg),
//                    phone));
//            ad.setPositiveButton(R.string.cx_fa_chat_call_btn,
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            callPhone(phone);
//                        }
//                    });
//        }
//
//        ad.setNegativeButton(R.string.cx_fa_cancel_button_text, null);
//        ad.show();
    	final String phone = CxMateParams.getInstance().getMateMobile();
    	
    	DialogUtil du = DialogUtil.getInstance();
		
    	if (TextUtils.isEmpty(phone)) {
    		du.setOnSureClickListener(new OnSureClickListener() {
    			
    			@Override
    			public void surePress() {				
    				Intent mateProfileIntent = new Intent(getActivity(),CxFamilyInfoActivity.class);
    				startActivity(mateProfileIntent);
    				getActivity().overridePendingTransition(R.anim.tran_next_in,
                          R.anim.tran_next_out);
    			}
    		});
    		du.getSimpleDialog(getActivity(), null,  getString(R.string.cx_fa_chat_not_call_msg), 
    				getString(R.string.cx_fa_chat_call_setphone_btn), null,false).show();
    	}else{
    		du.setOnSureClickListener(new OnSureClickListener() {
    			
    			@Override
    			public void surePress() {				
    				callPhone(phone);
    			}
    		});
    		du.getSimpleDialog(getActivity(), null,String.format(getResources().getString(R.string.cx_fa_chat_call_msg),phone), 
    				getString(R.string.cx_fa_chat_call_btn), null).show();
    	}
    	
    	
    	
    	
    }
    
    // 录音停止事件
    private void stopRecordEvent(MotionEvent m){
        int layerLocation[] = new int[2];
        mVoicePanel.getLocationInWindow(layerLocation);
        int layerX = layerLocation[0];
        int layerY = layerLocation[1];

        float x = m.getX();
        float y = m.getY();
        mRecordView.setVisibility(View.GONE);
//        RkInputPanel.mRecordBar.setBackgroundResource(R.drawable.chatview_voice);
        mInputPanel.changeRecordBarBackground(false);
        stopRecord();
        if (y >0 || x < 0 || x >=mDeviceWidth ) {
            android.os.Message msg = android.os.Message.obtain(mChatHandler, SEND_RECORD);
            msg.sendToTarget();
        } else {
            // delete audio file
            deleteFile(mSoundFilePath);
        }
        stopTimer();
        mRecordStart = false;
        //((RkMain)getActivity()).inavaileChildSlide();
        CxGlobalParams.getInstance().setRecorderFlag(false);
    }
    // 录音滑动事件
    private void moveRecordEvent(MotionEvent m){
        int layerLocation[] = new int[2];
        mVoicePanel.getLocationInWindow(layerLocation);
        int layerX = layerLocation[0];
        int layerY = layerLocation[1];

//        RkLog.i("onAcionMoveEvent", "layerX:layerY>>>" + layerX + " : " + layerY);
        
        float x = m.getX();
        float y = m.getY();
//        RkLog.i("onAcionMoveEvent", "X:Y>>>" + x + " : " + y);
        
//        RkInputPanel.mRecordBar.setBackgroundResource(R.drawable.chatview_voice_h);
        mInputPanel.changeRecordBarBackground(true);
        if (y < 0) {
            mChatRecordRelativeLayout.setBackgroundResource(R.drawable.pub_cancel);
            mRecordImageView.setImageResource(R.drawable.pub_cancel);
            if (mRecordRemainTimeTextView.getVisibility() == View.VISIBLE) {
                mRecordRemainTimeTextView.setVisibility(View.INVISIBLE);
            }
            mRecordCancelTip.setText(R.string.cx_fa_chat_record_cancle_tip);
        } else {
            android.os.Message message = android.os.Message.obtain(mChatHandler,
                    UPDATE_RECORD_TIME);
//            message.sendToTarget();
        }
    }
    private Timer mRecordTimer = null;
    private TimerTask mRecordTask = null;
    private static int mReocrdCount = 0;
    private void startTimer(){
        if(null == mRecordTimer){
            mRecordTimer = new Timer();
        }
        if(null == mRecordTask){
            mRecordTask = new TimerTask() {
                
                @Override
                public void run() {
                    CxLog.i(TAG, "hear me?" + mReocrdCount);
                    android.os.Message message = android.os.Message.obtain(mChatHandler,
                            UPDATE_RECORD_TIME);
                    message.sendToTarget();
                    if(mReocrdCount >= 90){
                        stopTimer();
                    }
                    mReocrdCount++;
                }
            };
        }
        mRecordTimer.schedule(mRecordTask, 0, 1 * 1000);
    }
    private void stopTimer(){
        if(null != mRecordTimer){
            mRecordTimer.cancel();
            mRecordTimer = null;
        }
        if(null != mRecordTask){
            mRecordTask.cancel();
            mRecordTask = null;
        }
        mReocrdCount = 0; 
    }

    private void deleteFile(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

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
    public void onResume() {
        super.onResume();       
        if(null != mSensorManager){
            mSensorManager.registerListener(this, mProximiny,  
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if( null != mSensorManager){
            mSensorManager.unregisterListener(this); 
        }
    }
    
    public void processSystemMessage(final int msgid, String method, String btn, String value1, String value2, String msg){
        
        if(TextUtils.isEmpty(msg)){
            requestNetSystemMessage(msgid, method, btn, value1, value2);
        } else {
            popReConfirmDialog(msgid, method, btn, value1, value2, msg);
        }

    }
    
    public void requestNetSystemMessage(final int msgid, String method, String btn, String value1, String value2){
        ChatApi.getInstance().requestProcessSystemMessage(method, btn, value1, value2, new JSONCaller() {
            
            @Override
            public int call(Object result) {
                
                try {
                    
                    if(null == result){
                        return -1;
                    }
                    Message msg = (Message) new Message(null, mContext).get(""+msgid);
                    JSONObject sysMsg = msg.mData;
                    
                    JSONObject obj = (JSONObject)result;
                    
                    JSONObject data = obj.getJSONObject("data");
//                  sysMsg.put("error_msg", data.getString("error_msg")); // toast tip, not need store
                    if(!TextUtils.isEmpty(data.getString("text"))){
                        sysMsg.put("text", data.getString("text"));
                    }
                    sysMsg.put("mode", 1);
                    sysMsg.put("btn_name", data.getString("btn_name"));
                    msg.put();
                    android.os.Message chatMessage = mChatHandler.obtainMessage(ChatFragment.UPDATE_CHAT_VIEW);
                    chatMessage.sendToTarget();
                    
                    if(!TextUtils.isEmpty(data.getString("error_msg"))){
                        android.os.Message message = mChatHandler.obtainMessage(ChatFragment.POP_TIP);
                        message.obj = data.getString("error_msg");
                        message.sendToTarget();
                    } 
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                return 0;
            }
        });
    }
    
    public void popReConfirmDialog(final int msgid, final String method, final String btn, final String value1, final String value2, String msg){
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(R.string.cx_fa_alert_dialog_tip);
        ad.setMessage(msg);        
        ad.setPositiveButton(R.string.cx_fa_cancel_button_text, null);
        ad.setNegativeButton(R.string.cx_fa_confirm_text,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestNetSystemMessage(msgid, method, btn, value1, value2);
                    }
                });
        ad.show();
    }
    // 更新home按钮状态
    public void updateHomeMenu(){
        if(CxGlobalParams.getInstance().getGroup() > 0 || CxGlobalParams.getInstance().getSpaceTips() > 0
        		|| CxGlobalParams.getInstance().getKid_tips() > 0){
            mMainMenuBtn.setBackgroundResource(R.drawable.navi_home_new_btn);
        } else {
//          mMainMenuBtn.setImageResource(R.drawable.cx_fa_menu_btn);
//            mMainMenuBtn.setImageDrawable(null);
        	mMainMenuBtn.setBackgroundResource(R.drawable.cx_fa_menu_btn);
        }
    }
    
    /**
     * 置为猜拳模式(用于回拳)
     * @param guessRequestMsgId : 发起猜拳的信息ID
     * @param guess_id					  : 猜拳ID
     * @param value1						  : 发起人出的拳
     */
    public void showInputPanelGuessMode(int guessRequestMsgId, String guess_id, int value1){
    	mInputPanel.setGuessMode(true,guessRequestMsgId, guess_id, value1 );
    }
}
