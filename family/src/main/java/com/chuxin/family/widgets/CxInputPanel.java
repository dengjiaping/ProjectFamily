package com.chuxin.family.widgets;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.neighbour.CxNeighbourAddInvitation;
import com.chuxin.family.neighbour.CxNeighbourAddMessage;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.HttpApi;
import com.chuxin.family.net.CxEmotionApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxEmotionConfigList;
import com.chuxin.family.parse.been.data.EmotionItem;
import com.chuxin.family.parse.been.data.EmotionList;
import com.chuxin.family.parse.been.data.EmotionSet;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.CxZipUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.views.chat.Emoticon;
import com.chuxin.family.views.chat.EmoticonDao;
import com.chuxin.family.views.chat.EmoticonFragment;
import com.chuxin.family.views.chat.EmotionCacheData;
import com.chuxin.family.views.chat.EmotionParam;
import com.chuxin.family.views.chat.EmoticonFragment.GridViewClickListener;
import com.chuxin.family.whip.WhipActivity;
import com.chuxin.family.zone.CxUsersPairZone;
import com.chuxin.family.zone.CxZoneAddFeed;
import com.chuxin.family.kids.CxKidAddFeed;
import com.chuxin.family.kids.CxKidFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;
/**
 * 
 * @author shichao.wang 2013.04.15
 *
 */
public class CxInputPanel extends LinearLayout {

    public enum EditMode {
        DEFAULT_MODE, VOICE_MODE, TEXT_MODE, FAST_PHRASE_MODE, FACE_MODE, POPUPMENU_MODE, GUESS_MODE, SOUND_MODE
    }

    public static interface OnEventListener {

        public void onButton0Click(View button);

        // Invoked when the 1st button in popup menu is clicked
        public void onButton1Click(View button);

        // Invoked when the 2nd button in popup menu is clicked
        public void onButton2Click(View button);

        // Invoked when user press "send" button
        // or user select some item in grid view;
        // >= 0 indicates sucess
        // < 0 means failure
        public int onMessage(String msg, int flag);

        public void onStartRecordEvent(View v, MotionEvent m);

        public void onAcionMoveEvent(View v, MotionEvent m);

        public void onStopMoveEvent(View v, MotionEvent m);

        public void onOtherEvent(View v, MotionEvent m);
    }

    private OnEventListener mOnEventListener = null;

    // menu for button plus;
    private LinearLayout mPopupMenu;

    // panel 1
    private LinearLayout mTextPanel;

    private ImageButton mPlusButton1, mSoundEffectButton;

    private ImageButton mFaceButton1;// , mFaceButton2;

    private EditText mInputText;

    private ImageButton mSendButton;

    // panel 2

    private LinearLayout mVoicePanel;

    private ImageButton mTextButton;

    private Button mRecordBar;

    private LinearLayout mRecordbarLinearLayout;

    // panel 3
    private LinearLayout mFastPhrasePanel;

    private ArrayList<View> mPhrasePageViews;

    private ViewPager mPhraseViewPager;

    private ImageView mPhraseImageView;

    private ArrayList<ImageView> mPhraseImageViews;

    private ViewGroup mPhraseViewGroupMain;

    private ViewGroup mPhraseGroup;

    private GridView mFastPhraseGridView1;

    private GridView mFastPhraseGridView2;

    private GridView mFastPhraseGridView3;

    // panel 4
    private LinearLayout mFacePanel;

    private ViewPager mFaceViewPager;

    private ImageView mFaceImageView;

    private ArrayList<ImageView> mFaceImageViews;

    private ViewGroup mFaceViewGroupMain;

    private ViewGroup mFaceGroup;

    // panel5
    private ImageButton mPhotoBtn, mCarmeraBtn, mLocationBtn, mPhraseBtn, mWhipBtn, mGuessBtn;

    private TextView mWhipBtnTextView;

    private LinearLayout mGuessPanel;

    private LinearLayout mSoundPanel;

    private EditMode mEditMode;

    public boolean mEnableSoftKeyboard = false;

    private View mActionBarView = null;

    private Context mContext;

    public static String sInputPanelUse = "";

    private boolean mShowFaceButton = true;// 表情按钮和文字按钮直接切换的变量

    private boolean mIsGuessReply = false; // 是否是回拳

    private String mGuessId = "-1"; // 猜拳id (回拳的时侯才需要)

    private int mGuessPartnerValue = -1; // 对方出的什么拳

    private int mGuessRequestMsgId = -1; // 发起猜拳的信息ID(主要用于，在回复猜拳时，将原来的信息置为已回拳)
    
    public static final int RKDSP_EFFECT_YUANSHENG = 0; // 原声
    public static final int RKDSP_EFFECT_HANHAN = 1; // 装憨
    public static final int RKDSP_EFFECT_HUAIHUAI = 2; // 装坏
    public static final int RKDSP_EFFECT_YOUYOU = 3; // 装幼
    public static final int RKDSP_EFFECT_SHASHA = 4; // 装傻
    public static int mCurrentSoundEffectState = 0; // 默认为原声

    private ImageView mSoundImageView1, mSoundImageView2, mSoundImageView3;
    private LinearLayout mSoundBtn1, mSoundBtn2, mSoundBtn3;

    private ArrayList<EmoticonFragment> mFacePageFragments;

    public CxInputPanel(Context context, AttributeSet attrs) throws Exception {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.cx_fa_widget_input_panel, this);
        
        addFacePageViews(); 
        initNativeEmotionConfig();
        downloadEmotionConfig();
        
        addPhrasePageViews();

        if (mContext instanceof CxMain) {
            main = (CxMain)mContext;
        }

        panel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel); 

        // panel 1
        mTextPanel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__linearlayout1);
        mPlusButton1 = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel__layout1_button1);
        mFaceButton1 = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel__layout1_button2);
        mInputText = (EditText)findViewById(R.id.cx_fa_widget_input_panel__layout1_textedit1);
        mSendButton = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel__layout1_button3);

        // panel 2
        mVoicePanel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__layout2);
        mTextButton = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel__layout2_button1);
        mRecordBar = (Button)findViewById(R.id.cx_fa_widget_input_panel__layout2_button2);
        
        mRecordBar.setText(R.string.cx_fa_inputpannel_sound_text);
        mSoundEffectButton = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel__layout2_button3);
        // mFaceButton2 = (ImageButton)
        // findViewById(R.id.cx_fa_widget_input_panel__layout2_button4);

        mRecordbarLinearLayout = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel_layout2_record_btn_linearlayout);

        // mRecordbarLinearLayout =
        // (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel_layout2_record_btn_linearlayout);

        // panel 3
        mFastPhrasePanel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__layout3);
        initPhrasePanel();

        // panel 4
        mFacePanel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__layout4);
//        initFacePanel();

        // 猜拳
        mGuessPanel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__layout6);
        initGuessPanel();

        // 语音变声
        mSoundPanel = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__layout7);
        initSoundPanel();

        // panel 5
        mPopupMenu = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel__layout5);
        mPhotoBtn = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel_popmenu__button0);
        mCarmeraBtn = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel_popmenu__button1);
        mLocationBtn = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel_popmenu__button2);
        mPhraseBtn = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel_popmenu__button3);
        mWhipBtn = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel_popmenu__wipe);
        mWhipBtn.setImageResource(CxResourceDarwable.getInstance().dr_chat_inputpanel_whip_btn);
        TextView mWhipText = (TextView)findViewById(R.id.cx_fa_widget_input_panel_popmenu__wipe_text);
        mWhipText.setText(CxResourceString.getInstance().str_chat_input_popuimenu_whip_btn);
        mGuessBtn = (ImageButton)findViewById(R.id.cx_fa_widget_input_panel_popmenu__guess);

        /*
         * mWhipBtnTextView =
         * (TextView)findViewById(R.id.cx_fa_widget_input_panel_popmenu_wipe_text
         * ); if ((null != sInputPanelUse) && (sInputPanelUse.equals(
         * ChatFragment.S_INPUTPANEL_CURRENT_VIEW))){ //确定在聊天界面 if (0 ==
         * RkGlobalParams.getInstance().getGender()) { //老婆版,显示弹脑壳
         * mWhipBtnTextView
         * .setText(mContext.getString(R.string.cx_fa_role_chat_popupmenu_whip_btn
         * ));
         * mWhipBtn.setImageResource(R.drawable.cx_fa_role_inputpanel_whip_btn_s
         * ); }else{ //老公版，显示抽鞭子
         * mWhipBtnTextView.setText(mContext.getString(R.string
         * .cx_fa_role_chat_popupmenu_whip_btn));
         * mWhipBtn.setImageResource(R.drawable
         * .cx_fa_role_inputpanel_whip_btn_s); } }
         */

        installListeners();

        // enter default mode
        setDefaultMode();
        
    }

   

	private void addPhrasePageViews() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v1 = inflater.inflate(R.layout.cx_fa_widget_phraseitem01, null);
        View v2 = inflater.inflate(R.layout.cx_fa_widget_phraseitem02, null);
        View v3 = inflater.inflate(R.layout.cx_fa_widget_phraseitem03, null);
        mPhrasePageViews = new ArrayList<View>();
        mPhrasePageViews.add(v1);
        mPhrasePageViews.add(v2);
        mPhrasePageViews.add(v3);

        mPhraseImageViews = new ArrayList<ImageView>();
        mPhraseViewGroupMain = (ViewGroup)inflater.inflate(
                R.layout.cx_fa_widget_input_panel_phrase, this);

        mPhraseGroup = (ViewGroup)mPhraseViewGroupMain
                .findViewById(R.id.cx_fa_widget_phrase_viewGroup);
        mPhraseViewPager = (ViewPager)mPhraseViewGroupMain
                .findViewById(R.id.cx_fa_widget_phrase_phrasePages);

        mPhraseGroup.removeAllViews();
        mPhraseImageViews.clear();

        for (int i = 0; i < mPhrasePageViews.size(); i++) {

            mPhraseImageView = new ImageView(mContext);

            LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(
                    R.dimen.cx_fa_dimen_chat_emotion_dot_w), getResources().getDimensionPixelSize(
                    R.dimen.cx_fa_dimen_chat_emotion_dot_h));// 设置imageview的宽高
            if (i == 0){
                lp.setMargins(0,getResources().getDimensionPixelSize(
                    R.dimen.cx_fa_dimen_chat_emotion_dot_top), 0, 0);// 设置和其他控件的距离
            } else{
                lp.setMargins(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_left), 
                	getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_top),0, 0);
            }
            mPhraseImageView.setLayoutParams(lp);

            // mPhraseImageViews[i] = mPhraseImageView;
            mPhraseImageViews.add(mPhraseImageView);

            if (i == mPhraseViewPager.getCurrentItem()) {
                mPhraseImageViews.get(i).setImageResource(R.drawable.dot_focused);// 设置背景
            } else {
                mPhraseImageViews.get(i).setImageResource(R.drawable.dot_normal);
            }

            mPhraseGroup.addView(mPhraseImageViews.get(i));
        }
        LayoutInflater.from(getContext()).inflate(R.layout.cx_fa_widget_input_panel_phrase,
                mPhraseViewGroupMain);

        mFastPhraseGridView1 = (GridView)v1.findViewById(R.id.cx_fa_widget_input_panel__layout3_grid1);
        mFastPhraseGridView2 = (GridView)v2.findViewById(R.id.cx_fa_widget_input_panel__layout3_grid2);
        mFastPhraseGridView3 = (GridView)v3.findViewById(R.id.cx_fa_widget_input_panel__layout3_grid3);
    }


    private GuideFacePageAdapter adapter;

    private EmoticonDao dao;

    private LinearLayout viewPagerll;

    private LinearLayout downloadll;

    private CxImageView imagebg;

    private ImageView imagedown;

    private ProgressBar imagepb;

    private LinearLayout progressll;

    private ImageView imagecancel;

    private int mEmoCount;

    private DefaultHttpClient mClient;

    private boolean discontinue = false;

    private HashMap<Integer, Integer> currentPages;

    private CxMain main;

    private View emotionBar;

    private LinearLayout panel;
    
    private int barPosition=0;
    
    private ArrayList<EmotionSet>  emotions;
    
    private boolean isLoading=false;
    
	private EmotionCacheData cacheData;

	private LinearLayout mFaceLinearLayout;

	private String urlStr; 
	
	private void initNativeEmotionConfig(){
		cacheData = new EmotionCacheData(mContext);
		CxEmotionConfigList data = cacheData.queryCacheData(CxGlobalParams.getInstance().getUserId());
		if(data!=null){
			EmotionList emotionList2 = data.getList();
			CxLog.i("RkInputPanel_men",  (emotionList2.getDatas()==null)+"");		
			if(emotionList2!=null && emotionList2.getDatas()!=null &&  emotionList2.getDatas().size()>0){
				emotions=emotionList2.getDatas();
			}
		}
		EmotionParam.getInstance().setEmotions(emotions);
		initEmotionData();
	}
	
    
    //下载表情配置文件及检查配置文件更新
    private void downloadEmotionConfig() {
    	cacheData = new EmotionCacheData(mContext);
        int cacheVersion = cacheData.queryCacheVersion(CxGlobalParams.getInstance().getUserId());
        CxLog.i("RkInputPanel_men", cacheVersion+"");       
        CxEmotionApi.getInstance().requestEmotionConfig(mContext, cacheVersion, emotionConfigCaller);	
	}
    
    //配置文件联网回调
    JSONCaller emotionConfigCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
			if (null == result) {
	
				return -1;
			}
			CxEmotionConfigList  list= null;
			try {
				list = (CxEmotionConfigList) result;
			} catch (Exception e) {
			}
			if (null == list) {
				return -2;
			}
			int rc = list.getRc();
			if (0 != rc) {

				return rc;
			}
		
			EmotionList emotionList = list.getList();
			if(emotionList!=null && emotionList.getDatas()!=null &&  emotionList.getDatas().size()>0){
				emotions=emotionList.getDatas();
			}else{
				return -4;
			}
			
			if(emotions==null || emotions.size()<1){			
				return -3;
			}
			
			EmotionParam.getInstance().setEmotions(emotions);
			
			new Handler(mContext.getMainLooper()){
				public void handleMessage(android.os.Message msg) {
					initEmotionData();//联网成功刷新
				}

			}.sendEmptyMessage(1);
			
			return 0;	
	
		}
	};
    
	//联网成功刷新
	private void initEmotionData() {
		if(emotions==null){
			emotionBar.setVisibility(View.GONE);
			return;
		}
		
      for (int i = 0; i <emotions.size()+1; i++) {
    	  currentPages.put(i, 0);      // 默认开始都是第0页
      }
      
      initEmotionBar();//初始化emotionbar
		
	};
    
    

    /**
     * 加载表情栏
     */
    private void addFacePageViews() {

    	barPosition=0;
    	
        LayoutInflater inflater = LayoutInflater.from(mContext);  
//
        mFacePageFragments = new ArrayList<EmoticonFragment>();// fragment的集合
        mFaceImageViews = new ArrayList<ImageView>();// 标识小圆点的集合
//
        currentPages = new HashMap<Integer, Integer>();// 每套表情当前页的记录集合
        
//        for (int i = 0; i <emotions.size()+1; i++) {
//            currentPages.put(i, 0);      // 默认开始都是第0页
//        }

        mFaceViewGroupMain = (ViewGroup)inflater.inflate(R.layout.cx_fa_widget_input_panel_face,
                this);

        mFaceGroup = (ViewGroup)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_viewGroup);// 小圆点的显示最外层布局
        mFaceViewPager = (ViewPager)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_facePages);// viewpager
        mFaceLinearLayout = (LinearLayout) mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_chatview_emotion_bar_layout);
        mFaceHolder = mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_placeholder);
        mFaceRelativeLayout = (RelativeLayout) mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_total_layout);
        
        
//        initEmotionBar();
//         //添加的底部标签栏
        


        // 获取数据库，存放每套的基本信息
        dao = new EmoticonDao(mContext);

        imagebg = (CxImageView)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_menu_imagebiaoqing);
        imagedown = (ImageView)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_menu_imagedown);
        imagepb = (ProgressBar)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_menu_imagepb);
        imagecancel = (ImageView)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_menu_imagecancel);

        // viewpageer页面，下载页面，下载进度条页面需要进行切换
        viewPagerll = (LinearLayout)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_linearLayout01);
        downloadll = (LinearLayout)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_linearLayout02);
        progressll = (LinearLayout)mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_linearLayout03);

        emotionBar = mFaceViewGroupMain.findViewById(R.id.cx_fa_widget_face_hsv);
        emotionBar.setVisibility(View.VISIBLE);

        setEmotionDownloadOnClick();// 设置下载条和取消按钮的点击事件
        initFacePanel();
       

    }
    

    //初始化表情底栏
    private void initEmotionBar(){
    		
    	CxLog.i("RkInputPanel_men", "initEmotionBar");
    	mFaceLinearLayout.removeAllViews();      	       
        for(int i=0;i<emotions.size()+1;i++){
 
        	View emotionItemView = View.inflate(mContext, R.layout.cx_fa_widget_input_panel_face_item, null);  
        	ImageView arrow = (ImageView) emotionItemView.findViewById(R.id.cx_fa_widget_face_menu_arrow_iv);
        	CxImageView menuItem = (CxImageView) emotionItemView.findViewById(R.id.cx_fa_widget_face_menu_emotion_iv);
        	EmotionSet set=null;
        	if(i==0){
        		if(barPosition==0){
        			arrow.setVisibility(View.VISIBLE);
        			menuItem.setImageResource(R.drawable.biaoqing_btnmood_h);
        		}else{
        			arrow.setVisibility(View.GONE);
        			menuItem.setImageResource(R.drawable.biaoqing_btnmood);
        		}	
        	}else{
        		set = emotions.get(i-1);
        		
        		boolean show = set.isShow();
        		if(!show){
        			continue;
        		}
        		
        		String norCateImage = set.getNorCateImage();
        		String lockCateImage = set.getLockCateImage();
//        		RkLog.i("RkInputPanel_men", norCateImage.toLowerCase());
        	
        		int norId = getResources().getIdentifier(norCateImage.toLowerCase(), "drawable", mContext.getPackageName());
//        		RkLog.i("RkInputPanel_men", norId+"");
        		int lockId = getResources().getIdentifier(lockCateImage.toLowerCase(), "drawable", mContext.getPackageName());
        		if(norId<=0){
        			norId=getResources().getIdentifier(CxResourceString.getInstance().getStringByFlag(
        					norCateImage.toLowerCase(),CxGlobalParams.getInstance().getVersion()),"drawable", mContext.getPackageName());
//        			RkLog.i("RkInputPanel_men", norId+">>>>");
        		}
        		if(lockId<=0){
        			lockId=getResources().getIdentifier(CxResourceString.getInstance().getStringByFlag(
        					lockCateImage.toLowerCase(),CxGlobalParams.getInstance().getVersion()), "drawable", mContext.getPackageName());
        		}
        		
        		String urlStr = set.getResourceUrl()+File.separator+"emotions_"+set.getCategoryId()+
        			File.separator+mContext.getString(CxResourceString.getInstance().str_chat_emotion_version_en)+File.separator;
        		
        		if(barPosition==i){//本地没有则联网下载
        			arrow.setVisibility(View.VISIBLE);
        			if(norId>0){
        				menuItem.setImageResource(norId);
        			}else{
        				menuItem.displayImage(ImageLoader.getInstance(), urlStr+norCateImage+"@2x.png", R.drawable.chatview_imageloading, false, 0);
        			}
        			
        		}else{
        			arrow.setVisibility(View.GONE);
        			if(lockId>0){
        				menuItem.setImageResource(lockId);
        			}else{
        				menuItem.displayImage(ImageLoader.getInstance(), urlStr+lockCateImage+"@2x.png", R.drawable.chatview_imageloading, false, 0);
        			}
        		}
        	}
	
//        	emotionItemView.setClickable(!isLoading);
        	
        	final EmotionSet emotionSet=set;
        	final int position=i;
        	emotionItemView.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {					
					if(barPosition==position){
						return; //不重复刷新
					}		
					if(isLoading){
						return;//正在下载则不刷新
					}
					
					barPosition=position;
					emotionBarOnClick(emotionSet);
				}
			});
        	
        	mFaceLinearLayout.addView(emotionItemView);
        }    
    }
    
    //emotionbar点击事件相应
    private void emotionBarOnClick(EmotionSet set){
    	
    	initEmotionBar();
    	
    	CxLog.i("men", barPosition+"");

    	if(barPosition==0){
    		setEmoticon(set);
    		return;
    	}
    	
        Emoticon emoticon = dao.find(CxGlobalParams.getInstance().getUserId(),set.getCategoryId()+"");// 在数据库中根据表情名得到emoticon对象
        if (emoticon == null || "false".equals(emoticon.getIsDown())) {
            // 不存在则显示下载页面，设置下载图为相应的
            // System.out.println("emoticon==null");
        	String guideImage = set.getGuideImage();
        	urlStr = set.getResourceUrl()+File.separator+"emotions_"+set.getCategoryId()+
				File.separator+mContext.getString(CxResourceString.getInstance().str_chat_emotion_version_en)+File.separator;
        	int guideId = getResources().getIdentifier(guideImage.toLowerCase(), "drawable", mContext.getPackageName());
        	if(guideId<=0){
        		guideId=getResources().getIdentifier(CxResourceString.getInstance().getStringByFlag(
        				guideImage.toLowerCase(),CxGlobalParams.getInstance().getVersion()), "drawable", mContext.getPackageName());
    		}
        	if(guideId>0){
        		imagebg.setImageResource(guideId);
			}else{
				imagebg.displayImage(ImageLoader.getInstance(), urlStr+guideImage+"@2x.png", R.drawable.chatview_imageloading, false, 0);
			}
        	
            setDownloadShow();          
        } else {
            // 存在则刷新viewpager
            setEmoticon(set);

        }
    	
    }
      

    /**
     * 初始化表情栏
     */
    private void initFacePanel() {

        setViewPagerShow(); // 设置viewpager页面显示

        FragmentActivity a = (FragmentActivity)mContext;
        FragmentManager fm = a.getSupportFragmentManager(); // 获取fm

        // 创建经典表情的fragment对象并加入list，及里面图标的点击监听
        EmoticonFragment f1 = EmoticonFragment.newInstance(barPosition, 1,7);
        EmoticonFragment f2 = EmoticonFragment.newInstance(barPosition, 2,7);
        f1.setGridViewClickListener(new GridViewClickListener() {

            @Override
            public void click(String msg) {
                // onMessage(msg, 0); //add by shichao
                mInputText.append(msg);
                if(sInputPanelUse.equals(CxNeighbourAddInvitation.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxNeighbourAddMessage.RK_CURRENT_VIEW)
                		|| sInputPanelUse.equals(CxKidAddFeed.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxZoneAddFeed.RK_CURRENT_VIEW) ){
                	CxLog.i("men", msg+">>>2");
                	moodListener.onMoodOnClick(msg);
                }
            }
        });
        f2.setGridViewClickListener(new GridViewClickListener() {

            @Override
            public void click(String msg) {
                // onMessage(msg, 0); // add by shichao
                mInputText.append(msg);
                if(sInputPanelUse.equals(CxNeighbourAddInvitation.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxNeighbourAddMessage.RK_CURRENT_VIEW)
                		|| sInputPanelUse.equals(CxKidAddFeed.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxZoneAddFeed.RK_CURRENT_VIEW)){
                	CxLog.i("men", msg+">>>2");
                	moodListener.onMoodOnClick(msg);
                }
            }
        });
        mFacePageFragments.add(f1);
        mFacePageFragments.add(f2);

        mEmoCount = 2; // 页面数量
        initDots(mEmoCount);// 初始化小圆点

        adapter = new GuideFacePageAdapter(fm, mFacePageFragments);// 设置viewpager的adapter
        mFaceViewPager.setAdapter(adapter);
        mFaceViewPager.setOnPageChangeListener(new GuidePageChangeListener2(mFaceImageViews));// 设置页面改变监听
    }



    /**
     * 设置下载条和取消按钮的点击事件
     */
    private void setEmotionDownloadOnClick() {


        imagedown.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
                	isLoading=true;
                    downloadEmotionZip();// 下载条点击事件去检查表情版本 
                } else {
                    Toast.makeText(mContext, "很抱歉，没检测到您的SD卡，无法下载！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 取消按钮的点击事件
        imagecancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                discontinue = true;
                setDownloadShow();
                imagepb.setProgress(0);
//                setMenuEnable(true);

            }
        });
    }
    //下载表情包并解压
    private void downloadEmotionZip(){
    	 new AsyncTask<Void, Void, Void>() {
             @Override
             protected void onPreExecute() {
                 super.onPreExecute();
                 setLoadingShow();// 显示londing页面 
                 HttpApi mApi = ConnectionManager.getHttpApi();
                 mClient = mApi.getmHttpClient();// 获取httpclient                 
             }

             @Override
             protected Void doInBackground(Void... params) {

                 HttpGet getEmos = new HttpGet(urlStr+mContext.getString(CxResourceString.getInstance().str_chat_emotion_version_en)+".zip");// 获取下载包的get
                 

                 // 联网获取当前套表情的下载zip包到本地并解压，顺便更新进度条
                 try {

                     // mClient.getConnectionManager().
                     HttpParams reParams = mClient.getParams();
                     HttpClientParams.setRedirecting(reParams, true);

                     HttpResponse response2 = mClient.execute(getEmos);

                     if (200 == response2.getStatusLine().getStatusCode()) {

                         long length = response2.getEntity().getContentLength();
                         imagepb.setMax((int)length);

                         HttpEntity entity = response2.getEntity();

                         InputStream in = null;

                         InputStream responseStream = null;
                         try {
                             responseStream = entity.getContent();
                         } catch (Exception e1) {
                         }
                         if (responseStream != null) {
                             in = responseStream;
                         }
                         Header header = entity.getContentEncoding();
                         if (header != null) {
                             String contentEncoding = header.getValue();
                             if (contentEncoding != null) {
                                 if (contentEncoding.contains("gzip")) {
                                     in = new GZIPInputStream(responseStream);
                                 }
                             }
                         }

                         // InputStream in = response2.getEntity().getContent();

                         String folderName = "chuxin" + File.separator + "emotion";
                         String fileName = emotions.get(barPosition-1).getCategoryId() + ".zip";
                         File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                         File file = new File(path, folderName + File.separator + fileName);
                         if (!file.getParentFile().exists())
                             file.getParentFile().mkdirs();

                         FileOutputStream out = new FileOutputStream(file);
                         int len = 0;
                         byte[] buffer = new byte[1024];
                         int total = 0;// 当前这次下载的数据大小
                         // 点击取消按钮会把discontinue置为ture，则下载中断
                         while ((len = in.read(buffer)) != -1 && !discontinue) {
                             out.write(buffer, 0, len);
                             total += len;
                             imagepb.setProgress(total);
                         }

                         if (discontinue) {// 如果下载中断，删除zip包
                             if (file != null)
                                 file.delete();
                         }
                         if (total == (int)length) {
                             // 下载完成则解压
                             CxZipUtil.upZipFile(file, path + File.separator + folderName
                                     + File.separator + "emotions");
                             // if(file!=null)
                             // file.delete();
                         }
                     }

                 } catch (ClientProtocolException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return null;
             }

             @Override
             protected void onPostExecute(Void result) {
                 super.onPostExecute(result);             
                     checkFinish(); 
             }

         }.execute();
    }
    
    //检测是否表情包下载完成
    public void checkFinish(){

    	isLoading=false;
    	if(discontinue){ // 如果中断完成则回复discontinue为false
    		setDownloadShow();
    		imagepb.setProgress(0);
    		discontinue=false;
    		return;
    	}
        // setViewPagerShow();//显示viewpager页面
        imagepb.setProgress(0);// 回复进度条为初始值
        try {
        	EmotionSet set = emotions.get(barPosition-1);
        	EmotionItem item = set.getItems().get(0);
          
            File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File tempFile = new File(path, CxGlobalConst.S_CHAT_EMOTION + File.separator
                    + item.getImage() + "." + item.getType());
//            RkLog.i("RkInputPanel_men", tempFile.getAbsolutePath());
            if (!tempFile.exists()) {
                setDownloadShow();
                ToastUtil.getSimpleToast(mContext, R.drawable.chatbg_update_error,
                        "表情包下载失败了~重试一下吧", 0).show();
                return;
            }

            Emoticon emoticon = new Emoticon();
            
            emoticon.setEmoName(set.getCategoryId()+"");
            emoticon.setIsDown("true");
            
            
            Emoticon emoticon2 = dao.find(CxGlobalParams.getInstance().getUserId(),set.getCategoryId()+"");
            if(emoticon2==null){
            	long i = dao.add(emoticon);
            }else{
            	boolean b = dao.update(emoticon);
            }

            // 把每套表情的数据加入数据库
            setEmoticon(set);// 刷新viewpager

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    

    // 显示loading页面
    private void setLoadingShow() {
        viewPagerll.setVisibility(View.GONE);
        downloadll.setVisibility(View.VISIBLE);
        imagedown.setVisibility(View.GONE);
        progressll.setVisibility(View.VISIBLE);
    }

    // 显示下载页面
    private void setDownloadShow() {
        viewPagerll.setVisibility(View.GONE);
        downloadll.setVisibility(View.VISIBLE);
        imagedown.setVisibility(View.VISIBLE);
        progressll.setVisibility(View.GONE);
    }

    // 显示vierpager页面
    private void setViewPagerShow() {
        viewPagerll.setVisibility(View.VISIBLE);
        downloadll.setVisibility(View.GONE);
        imagedown.setVisibility(View.VISIBLE);
        progressll.setVisibility(View.GONE);
    }


    /**
     * 发帖加表情部分处理
     */
    public static interface OnMoodEmotionListener{
    	void onMoodOnClick(String msg);
    };
    
    private OnMoodEmotionListener  moodListener=null;
    
    public void setOnMoodEmotionListener(OnMoodEmotionListener listener){
    	this.moodListener=listener;
    }



    // 刷新viewpager
    protected void setEmoticon(EmotionSet set) {
    	
    	
    	if(set!=null){
    		EmotionItem item = set.getItems().get(0);
            
            File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File tempFile = new File(path, CxGlobalConst.S_CHAT_EMOTION + File.separator
                    + item.getImage() + "." + item.getType());

            if (!tempFile.exists()) {
                setDownloadShow();
                
                Emoticon emoticon = new Emoticon();            
                emoticon.setEmoName(set.getCategoryId()+"");
                emoticon.setIsDown("false");
                dao.update(emoticon);
                ToastUtil.getSimpleToast(mContext, R.drawable.chatbg_update_error,
                        "SD卡的表情文件被删除了，请重新下载。", 0).show();
                return;
            }
    	}
    	

        setViewPagerShow();// 显示viewpager的页面

        mFacePageFragments.clear();// 清空fragment集合

        int countPerPage = 0;// 获取每页数量
        int totalNumber = 0;// 获取总数量
        CxLog.i("men", barPosition+">>>>");
        CxLog.i("men", (set==null)+">>>>");
        if(barPosition==0 || set==null){
        	countPerPage=21;
        	totalNumber=42;
        }else{
        	countPerPage = set.getCountPerPage();// 获取每页数量
            totalNumber = set.getItems().size();// 获取总数量
        }
        
        
        
        mEmoCount = (totalNumber + countPerPage - 1) / countPerPage;// 获取页数

        initDots(mEmoCount);// 刷新小圆点
        for (int i = 0; i < mEmoCount; i++) {
            EmoticonFragment f = EmoticonFragment.newInstance(barPosition, i + 1,countPerPage/2);
            mFacePageFragments.add(f);
            f.setGridViewClickListener(new GridViewClickListener() {

                @Override
                public void click(String msg) {
//                	RkLog.i("men", msg+">>>0");
                    if (barPosition==0) {
                        // onMessage(msg,0);//经典表情走text机制 //add by shichao
//                    	RkLog.i("men", msg+">>>1");
                        mInputText.append(msg);

                    } else {
//                    	RkLog.i("men", msg);
                        onMessage(msg, 11);// 非经典走emotion机制
                        // System.out.println(msg);
                    }

                }
            });
        }

        // 着两行代码不能放到方法开头 因为当上一套表情只有两屏，这套有三屏，当前页也是第三屏，如果在开头后自动显示第二屏。
        
        int page = 0;// 设置每套表情的当前页为上次的页面
        if(currentPages!=null){
        	page=currentPages.get(barPosition);
        }
        
        mFaceViewPager.setCurrentItem(page);

        adapter.notifyDataSetChanged();// 刷新数据
    }

   
    
 

    /**
     * 设置表情下面表示每一页的小圆点，黑色表示当前页
     * 
     * @param number 小圆点的个数
     */
    private void initDots(int number) {

        mFaceGroup.removeAllViews();// 清空linearlayout
        mFaceImageViews.clear();
        for (int i = 0; i < number; i++) {

            mFaceImageView = new ImageView(mContext);

            LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(
                    R.dimen.cx_fa_dimen_chat_emotion_dot_w), getResources().getDimensionPixelSize(
                    R.dimen.cx_fa_dimen_chat_emotion_dot_h));// 设置imageview的宽高
            if (i == 0){
                lp.setMargins(0,getResources().getDimensionPixelSize(
                   R.dimen.cx_fa_dimen_chat_emotion_dot_top), 0, 0);// 设置和其他控件的距离
            }else{
                lp.setMargins(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_left), getResources()
                   .getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_top),0, 0);
            }
            mFaceImageView.setLayoutParams(lp);

            mFaceImageViews.add(mFaceImageView);

            if (i == mFaceViewPager.getCurrentItem()) {
                mFaceImageViews.get(i).setImageResource(R.drawable.dot_focused);// 设置背景
            } else {
                mFaceImageViews.get(i).setImageResource(R.drawable.dot_normal);
            }

            mFaceGroup.addView(mFaceImageViews.get(i));
        }
    }
    
  
    

    
    



    private void initPhrasePanel() {
        TypedArray phraseImageResIds1 = getResources().obtainTypedArray(
                R.array.cx_fa_ids_input_panel_phrase_images1);
        String[] phraseValues1 = getResources().getStringArray(
                R.array.cx_fa_strs_input_panel_phrase_values1);
        String[] phraseDescs1 = getResources().getStringArray(
                R.array.cx_fa_strs_input_panel_phrase_descs1);

        TypedArray phraseImageResIds2 = getResources().obtainTypedArray(
                R.array.cx_fa_ids_input_panel_phrase_images2);
        String[] phraseValues2 = getResources().getStringArray(
                R.array.cx_fa_strs_input_panel_phrase_values2);
        String[] phraseDescs2 = getResources().getStringArray(
                R.array.cx_fa_strs_input_panel_phrase_descs2);

        TypedArray phraseImageResIds3 = getResources().obtainTypedArray(
                R.array.cx_fa_ids_input_panel_phrase_images3);
        String[] phraseValues3 = getResources().getStringArray(
                R.array.cx_fa_strs_input_panel_phrase_values3);
        String[] phraseDescs3 = getResources().getStringArray(
                R.array.cx_fa_strs_input_panel_phrase_descs3);

        mFastPhraseGridView1.setAdapter(new PhraseAdapter(phraseImageResIds1, phraseDescs1,
                phraseValues1));
        mFastPhraseGridView2.setAdapter(new PhraseAdapter(phraseImageResIds2, phraseDescs2,
                phraseValues2));
        mFastPhraseGridView3.setAdapter(new PhraseAdapter(phraseImageResIds3, phraseDescs3,
                phraseValues3));
        mPhraseViewPager.setAdapter(new GuidePageAdapter(mPhrasePageViews));
        mPhraseViewPager.setOnPageChangeListener(new GuidePageChangeListener(mPhraseImageViews));
    }

    /**
     * 初始化猜拳
     */
    private void initGuessPanel() {
        ImageView guessBtn1 = (ImageView)findViewById(R.id.cx_fa_widget_input_panel_guess_scissors);
        ImageView guessBtn2 = (ImageView)findViewById(R.id.cx_fa_widget_input_panel_guess_rock);
        ImageView guessBtn3 = (ImageView)findViewById(R.id.cx_fa_widget_input_panel_guess_paper);

        guessBtn1.setOnClickListener(guessBtnListener);
        guessBtn2.setOnClickListener(guessBtnListener);
        guessBtn3.setOnClickListener(guessBtnListener);
        
        int versionInt = CxGlobalParams.getInstance().getVersion();
        if (0 == versionInt) {
        	guessBtn1.setImageResource(R.drawable.cx_fa_inputpanel_guess_rock_btn);
        	guessBtn2.setImageResource(R.drawable.cx_fa_inputpanel_guess_scissors_btn);
		}else{
			guessBtn1.setImageResource(R.drawable.cx_fa_inputpanel_guess_scissors_btn);
			guessBtn2.setImageResource(R.drawable.cx_fa_inputpanel_guess_rock_btn);
		}
        
    }

    /**
     * 初始化语音变声
     */
    private void initSoundPanel() {
        mSoundBtn1 = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel_sound_yuansheng);
        mSoundBtn2 = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel_sound_zhuangyou);
        mSoundBtn3 = (LinearLayout)findViewById(R.id.cx_fa_widget_input_panel_sound_zhuangsha);

        mSoundBtn1.setOnClickListener(soundBtnListener);
        mSoundBtn2.setOnClickListener(soundBtnListener);
        mSoundBtn3.setOnClickListener(soundBtnListener);
        
        mSoundImageView1 = (ImageView)findViewById(R.id.cx_fa_widget_input_panel_sound_image1);
        mSoundImageView2 = (ImageView)findViewById(R.id.cx_fa_widget_input_panel_sound_image2);
        mSoundImageView3 = (ImageView)findViewById(R.id.cx_fa_widget_input_panel_sound_image3); 
        
        TextView mText2 = (TextView)findViewById(R.id.cx_fa_widget_input_panel_sound_zhuangyou_text); 
        TextView mText3 = (TextView)findViewById(R.id.cx_fa_widget_input_panel_sound_zhuangsha_text); 
        mText2.setText(CxResourceString.getInstance().str_chat_input_soundeffect_text1);
        mText3.setText(CxResourceString.getInstance().str_chat_input_soundeffect_text2);
        
        int version = CxGlobalParams.getInstance().getVersion();
        if(0==version){
        	mSoundImageView1.setImageResource(R.drawable.sound_yuansheng_s);
            mSoundImageView2.setImageResource(R.drawable.cx_fa_sound_zhuanghan);
            mSoundImageView3.setImageResource(R.drawable.cx_fa_sound_zhuanghuai);                   
        } else {
        	mSoundImageView1.setImageResource(R.drawable.sound_yuansheng);
            mSoundImageView2.setImageResource(R.drawable.cx_fa_sound_zhuangyou);
            mSoundImageView3.setImageResource(R.drawable.cx_fa_sound_zhuangsha);   
        }
    }

    /**
     * 处理猜拳点击
     */
    private OnClickListener guessBtnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // 如果是回拳，就用以前的id。如果是发起出拳，就用一个新的ID
            String guess_id = "";
            if (mIsGuessReply) {
                guess_id = mGuessId;
            } else {
                guess_id = UUID.randomUUID().toString(); // 生成一个id
            }
            int myValue = 1; // 我出的拳
            int versionInt = CxGlobalParams.getInstance().getVersion();

            switch (v.getId()) {
                case R.id.cx_fa_widget_input_panel_guess_scissors://公：剪刀； 婆：石头
                	if (0 == versionInt) {
                		myValue = 2;
					}else{
						myValue = 1;
					}
                    break;
                case R.id.cx_fa_widget_input_panel_guess_rock://公：石头； 婆：剪刀
                	if (0 == versionInt) {
                		myValue = 1;
                	}else{
                		myValue = 2;
                	}
                    break;
                case R.id.cx_fa_widget_input_panel_guess_paper:
                    myValue = 3;
                    break;
            }
            if (mIsGuessReply) { // 回拳
                // 判断结果
                int result = 0;
                if (mGuessPartnerValue == myValue) { // 平局
                    result = 0;
                } else if (myValue == 1) { // 剪刀
                    if (mGuessPartnerValue == 2) { // 石头
                        result = -1;
                    } else if (mGuessPartnerValue == 3) { // 布
                        result = 1;
                    }
                } else if (myValue == 2) { // 石头
                    if (mGuessPartnerValue == 1) { // 剪刀
                        result = 1;
                    } else if (mGuessPartnerValue == 3) { // 布
                        result = -1;
                    }
                } else if (myValue == 3) { // 布
                    if (mGuessPartnerValue == 1) { // 剪刀
                        result = -1;
                    } else if (mGuessPartnerValue == 2) { // 石头
                        result = 1;
                    }
                }
                // 回拳时，让value1和value2对调一下。让自己出的拳为value1. (ios是这样做的，android必须对应)
                String msg2 = mGuessRequestMsgId + "," + guess_id + "," + myValue + ","
                        + mGuessPartnerValue + "," + result;
                ChatFragment.getInstance().sendMessage(msg2,
                        com.chuxin.family.models.Message.MESSAGE_TYPE_GUESS_RESPONSE);
            } else { // 发起新的猜拳
                String msg2 = guess_id + "," + myValue;
                ChatFragment.getInstance().sendMessage(msg2,
                        com.chuxin.family.models.Message.MESSAGE_TYPE_GUESS_REQUEST);
            }
            setDefaultMode();
        }

    };

    /**
     * 处理语音变声点击
     */
    private OnClickListener soundBtnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String version = getResources().getString(CxResourceString.getInstance().str_pair);
            switch (v.getId()) {
                case R.id.cx_fa_widget_input_panel_sound_yuansheng:
                    mCurrentSoundEffectState = RKDSP_EFFECT_YUANSHENG;
                    break;
                case R.id.cx_fa_widget_input_panel_sound_zhuangyou:
                    if("老公".equals(version)){
                        mCurrentSoundEffectState = RKDSP_EFFECT_YOUYOU;
                    } else {
                        mCurrentSoundEffectState = RKDSP_EFFECT_HANHAN;
                    }
                    break;
                case R.id.cx_fa_widget_input_panel_sound_zhuangsha:
                    if("老公".equals(version)){
                        mCurrentSoundEffectState = RKDSP_EFFECT_SHASHA;
                    } else {
                        mCurrentSoundEffectState = RKDSP_EFFECT_HUAIHUAI;
                    }
                    break;
            }
            setVoiceMode();
        }

    };

	private View mFaceHolder;

	private RelativeLayout mFaceRelativeLayout;

	
	public void setOnEventListener(OnEventListener listener) {
        mOnEventListener = listener;
    }

    private void onMessage(String msg, int flag) {
        if (mOnEventListener != null) {
            int rc = mOnEventListener.onMessage(msg, flag);
            if (rc == 1) {
                ;
            } else if (rc == 0) {
                mInputText.setText("");
            }
        } else {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void onButton0Click(View button) {
        if (mOnEventListener != null) {
            mOnEventListener.onButton0Click(button);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "Button 0 click!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onButton1Click(View button) {
        if (mOnEventListener != null) {
            mOnEventListener.onButton1Click(button);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "Button 1 click!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onButton2Click(View button) {
        if (mOnEventListener != null) {
            mOnEventListener.onButton2Click(button);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "Button 2 click!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onStartRecordEvent(View v, MotionEvent motionEvent) {
        if (mOnEventListener != null) {
            mOnEventListener.onStartRecordEvent(v, motionEvent);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "action down!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onAcionMoveEvent(View v, MotionEvent motionEvent) {
        if (mOnEventListener != null) {
            mOnEventListener.onAcionMoveEvent(v, motionEvent);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "action move!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onStopMoveEvent(View v, MotionEvent motionEvent) {
        if (mOnEventListener != null) {
            mOnEventListener.onStopMoveEvent(v, motionEvent);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "action up!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onOtherEvent(View v, MotionEvent motionEvent) {
        if (mOnEventListener != null) {
            mOnEventListener.onOtherEvent(v, motionEvent);
        } else {
            // FOR DEBUG PURPOSE
            Toast.makeText(getContext(), "action other!", Toast.LENGTH_SHORT).show();
        }
    }

    // install listeners
    private void installListeners() {
        mInputText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                CxLog.v("RkInputPanel", "come onTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                CxLog.v("RkInputPanel", "come beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {

                CxLog.v("RkInputPanel", "come afterTextChanged");
                if (s.length() > 0) {
                    mSendButton.setImageResource(R.drawable.chat_buttonsend);
                } else {
                    mSendButton.setImageResource(R.drawable.chat_buttonvoice);
                }
            }
        });

        mFastPhraseGridView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = (String)mFastPhraseGridView1.getAdapter().getItem(position);
                onMessage(msg, 1);
            }

        });
        mFastPhraseGridView2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = (String)mFastPhraseGridView2.getAdapter().getItem(position);
                onMessage(msg, 1);
            }

        });
        mFastPhraseGridView3.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = (String)mFastPhraseGridView3.getAdapter().getItem(position);
                onMessage(msg, 1);
            }

        });

        mSendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = mInputText.getText().toString();
                if (msg.length() > 0 || sInputPanelUse == CxUsersPairZone.RK_CURRENT_VIEW) {
                    onMessage(msg, 0);
                } else {
                    setVoiceMode();
                }
            }

        });

        mTextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CxLog.i("CxInputPanel_men", "text click come in");
                setTextMode();
            }

        });

        mInputText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    CxLog.i("CxInputPanel_men", "mInputText click come in");
                    setTextMode();
                }
            }

        });

        mPlusButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setPopupMenuMode();
                enableSoftKeyboard(true);
            }

        });

        mSoundEffectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // setPopupMenuMode();
                // enableSoftKeyboard(true);
                
                if (mEditMode != EditMode.SOUND_MODE) {
                    setSoundMode();
                } else if(mEditMode == EditMode.SOUND_MODE){
                    setVoiceMode();
                }
            }

        });

        mRecordbarLinearLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                // RkLog.v("RkInputPanel", "call recordbar ontouch");
                // RkLog.d("InputPanle", "m.x>>>" + motionEvent.getX() +
                // " : m.y>>>" + motionEvent.getY());

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        // RkLog.i("rkinputpanel", "action down");
                        onStartRecordEvent(v, motionEvent);

                        // mRecordBar.setClickable(false);
                        // mRecordBar
                        // .setImageResource(R.drawable.chatview_audio_input_h);
                        // mRecordBar.releaseFocus();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        onAcionMoveEvent(v, motionEvent);
                        // mRecordBar
                        // .setImageResource(R.drawable.chatview_audio_input_h);
                        // mRecordBar.releaseFocus();
                        break;
                    case MotionEvent.ACTION_UP:
                        // RkLog.i("rkinputpanel", "action up");
                        // mRecordBar.setClickable(true);
                        onStopMoveEvent(v, motionEvent);
                        // mRecordBar
                        // .setImageResource(R.drawable.chatview_audio_input);
                        // mRecordBar.captureFocus();
                        break;
                    default:
                        onOtherEvent(v, motionEvent);
                        break;
                }
                return true;
            }
        });

        mPhotoBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onButton0Click(v);
                setDefaultMode();
            }
        });

        mCarmeraBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onButton1Click(v);
                setDefaultMode();
            }
        });

        mLocationBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onButton2Click(v);
                setDefaultMode();
            }
        });

        mWhipBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatFragment.getInstance().getActivity(),
                        WhipActivity.class);
                ChatFragment.getInstance().startActivity(intent);
                setDefaultMode();
            }
        });

        mFaceButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowFaceButton) {
                    mShowFaceButton = false;
                    mFaceButton1.setImageResource(R.drawable.cx_fa_inputpanel_text_btn);
                    setFaceMode();

                    // ((RkMain)ChatFragment.getInstance().getActivity()).availeChildSlide();
                } else {
                    mShowFaceButton = true;
                    mFaceButton1.setImageResource(R.drawable.cx_fa_inputpanel_face_btn);
                    CxLog.i("CxInputPanel_men", "mFaceButton1 click come in");
                    setTextMode();
                }
            }
        });
        /*
         * mFaceButton2.setOnClickListener( new OnClickListener() {
         * @Override public void onClick(View v) { if(mShowFaceButton){
         * mShowFaceButton = false;
         * mFaceButton2.setImageResource(R.drawable.cx_fa_inputpanel_text_btn);
         * mFaceButton1.setImageResource(R.drawable.cx_fa_inputpanel_text_btn);
         * setFaceMode(); } else { mShowFaceButton = true;
         * mFaceButton1.setImageResource(R.drawable.cx_fa_inputpanel_face_btn);
         * mFaceButton2.setImageResource(R.drawable.cx_fa_inputpanel_face_btn);
         * setTextMode(); } } });
         */// 20131207 by shichao

        mPhraseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setFastPhraseMode();
            }
        });

        mGuessBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setGuessMode(false, -1, "-1", -1);
            }
        });

    }

    private void changeSendButtonResource() {
        if (sInputPanelUse.equals(CxUsersPairZone.RK_CURRENT_VIEW)) {
            mSendButton.setImageResource(R.drawable.chat_buttonsend);
        } else {
            if ((null != mInputText.getText().toString())
                    && (mInputText.getText().toString().length() > 0)) {
                mSendButton.setImageResource(R.drawable.chat_buttonsend);
            } else {
                mSendButton.setImageResource(R.drawable.chat_buttonvoice);
            }
        }
    }

    public void setDefaultMode() {
        if (mEditMode != EditMode.DEFAULT_MODE) {
            mEditMode = EditMode.DEFAULT_MODE;
        }
        if (mContext instanceof CxMain) {
            main.inavaileChildSlide();
        }

        // force to display the soft keyboard
        enableSoftKeyboard(false);

        if (mVoicePanel.getVisibility() != GONE) {
        	CxLog.i("CxInputPanel_men", ">>>>>>>>>4");
            mVoicePanel.setVisibility(GONE);
        }
        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mSoundPanel.getVisibility() != GONE) {
            mSoundPanel.setVisibility(GONE);
        }

        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        

        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }

        mPlusButton1.setFocusableInTouchMode(true);
        mPlusButton1.requestFocus();
        if (mTextPanel.getVisibility() != VISIBLE) {
            mInputText.setFocusableInTouchMode(true);
            mInputText.requestFocus();
            mTextPanel.setVisibility(VISIBLE);
        }
        mShowFaceButton = true;
        mFaceButton1.setImageResource(R.drawable.cx_fa_inputpanel_face_btn);
        changeSendButtonResource();
    }

    public void setVoiceMode() {
        if (mEditMode != EditMode.VOICE_MODE) {
            mEditMode = EditMode.VOICE_MODE;
        }

        // force to display the soft keyboard
        enableSoftKeyboard(false);
        
        CxLog.i("CxInputPanel_men", "setVoiceMode>>>>>>>>>>1");

        if (mContext instanceof CxMain) {
            main.inavaileChildSlide();
        }

        if (mTextPanel.getVisibility() != GONE) {
            mTextPanel.setVisibility(GONE);
        }
        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }
        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mSoundPanel.getVisibility() != GONE) {
            mSoundPanel.setVisibility(GONE);
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != VISIBLE) {
        	CxLog.i("CxInputPanel_men", "setVoiceMode>>>>>>>>>>1");
            mVoicePanel.setVisibility(VISIBLE);
        }
        changeRecordBarBackground(false);
    }

    public void setFastPhraseMode() {
        if (mEditMode != EditMode.FAST_PHRASE_MODE) {
            mEditMode = EditMode.FAST_PHRASE_MODE;
        }

        // force to display the soft keyboard
        enableSoftKeyboard(false);
        if (mContext instanceof CxMain) {
            main.availeChildSlide();
        }

        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != GONE) {
            mVoicePanel.setVisibility(GONE);
        }

        if (mFastPhrasePanel.getVisibility() != VISIBLE) {
            mFastPhrasePanel.setVisibility(VISIBLE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        mPlusButton1.setFocusableInTouchMode(true);
        mPlusButton1.requestFocus();
        if (mTextPanel.getVisibility() != VISIBLE) {
            mTextPanel.setFocusableInTouchMode(true);
            mTextPanel.requestFocus();
            mTextPanel.setVisibility(VISIBLE);
        }
        changeSendButtonResource();
    }

    /**
     * 猜拳
     */
    public void setGuessMode(boolean isReply, int guessRequestMsgId, String guess_id, int value1) {
        // 处理点击事件时要使用的变量
        this.mIsGuessReply = isReply;
        this.mGuessRequestMsgId = guessRequestMsgId;
        this.mGuessId = guess_id;
        this.mGuessPartnerValue = value1;

        // 是回拳，还是发出猜拳
        TextView guessTxt = (TextView)findViewById(R.id.cx_fa_widget_input_panel_guess_txt);
        if (isReply) {
            guessTxt.setText(R.string.cx_fa_guess_input_panel_reply);
        } else {
            guessTxt.setText(R.string.cx_fa_guess_input_panel_start);
        }

        if (mEditMode != EditMode.GUESS_MODE) {
            mEditMode = EditMode.GUESS_MODE;
        }

        // force to display the soft keyboard
        enableSoftKeyboard(false);
        if (mContext instanceof CxMain) {
            main.availeChildSlide();
        }

        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }

        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }

        if (mGuessPanel.getVisibility() != VISIBLE) {
            mGuessPanel.setVisibility(VISIBLE);
        }

        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        mPlusButton1.setFocusableInTouchMode(true);
        mPlusButton1.requestFocus();
        if (mTextPanel.getVisibility() != VISIBLE) {
            mTextPanel.setFocusableInTouchMode(true);
            mTextPanel.requestFocus();
            mTextPanel.setVisibility(VISIBLE);
        }
        changeSendButtonResource();

    }

    /**
     * 语音变声模式
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setSoundMode() {
        if (mEditMode != EditMode.SOUND_MODE) {
            mEditMode = EditMode.SOUND_MODE;
        }
        if (mContext instanceof CxMain) {
            main.inavaileChildSlide();
        }

        // force to display the soft keyboard
        enableSoftKeyboard(false);
        if (mTextPanel.getVisibility() != GONE) {
            mTextPanel.setVisibility(GONE);
        }
        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }
        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != VISIBLE) {
            mVoicePanel.setVisibility(VISIBLE);
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        if (mSoundPanel.getVisibility() != VISIBLE) {
            mSoundPanel.setVisibility(VISIBLE);
        }
        
        mRecordBar.setTextColor(getResources().getColor(R.color.cx_fa_co_reminder_tip_deep_gray));
        switch(mCurrentSoundEffectState){
            case RKDSP_EFFECT_YUANSHENG:
                mSoundBtn1.setBackgroundResource(R.drawable.sound_box);
                mRecordBar.setText(R.string.cx_fa_inputpannel_sound_text);
                mSoundBtn2.setBackgroundDrawable(null);
                mSoundBtn3.setBackgroundDrawable(null);
                break;
            case RKDSP_EFFECT_HANHAN:
            case RKDSP_EFFECT_YOUYOU:
                mSoundBtn2.setBackgroundResource(R.drawable.sound_box);
                mRecordBar.setText(CxResourceString.getInstance().str_chat_inputpannel_sound_text1);
                mSoundBtn1.setBackgroundDrawable(null);
                mSoundBtn3.setBackgroundDrawable(null);
                break;
            case RKDSP_EFFECT_HUAIHUAI:
            case RKDSP_EFFECT_SHASHA:
                mSoundBtn3.setBackgroundResource(R.drawable.sound_box);
                mRecordBar.setText(CxResourceString.getInstance().str_chat_inputpannel_sound_text2);
                mSoundBtn1.setBackgroundDrawable(null);
                mSoundBtn2.setBackgroundDrawable(null);
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    public void changeRecordBarBackground(boolean isStart){
        if(!isStart){
            mRecordBar.setBackgroundResource(R.drawable.chatview_voice);
            mRecordBar.setTextColor(R.color.cx_fa_co_reminder_tip_deep_gray);
            switch(mCurrentSoundEffectState){
                case RKDSP_EFFECT_YUANSHENG:
                    mRecordBar.setText(R.string.cx_fa_inputpannel_sound_text);
                    break;
                case RKDSP_EFFECT_HANHAN:
                case RKDSP_EFFECT_YOUYOU:
                    mRecordBar.setText(CxResourceString.getInstance().str_chat_inputpannel_sound_text1);
                    break;
                case RKDSP_EFFECT_HUAIHUAI:
                case RKDSP_EFFECT_SHASHA:
                    mRecordBar.setText(CxResourceString.getInstance().str_chat_inputpannel_sound_text2);
                    break;
            }
        } else {
            mRecordBar.setBackgroundResource(R.drawable.chatview_voice_h);
            mRecordBar.setTextColor(R.color.cx_fa_co_reminder_tip_deep_gray);
            mRecordBar.setText(R.string.cx_fa_inputpannel_sound_release_text);
            
        }
    }
    
    public void setFaceMode() {
        if (mEditMode != EditMode.FACE_MODE) {
            mEditMode = EditMode.FACE_MODE;
        }
        // force to display the soft keyboard
        enableSoftKeyboard(false);
        
        CxLog.i("RkInputPanel_men", sInputPanelUse);

        if (sInputPanelUse.equals(CxUsersPairZone.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxKidFragment.RK_CURRENT_VIEW)
                || sInputPanelUse.equals(CxNeighbourFragment.RK_CURRENT_VIEW) || emotions==null) {
        	CxLog.i("RkInputPanel_men", sInputPanelUse+">>>>>>>1");
            emotionBar.setVisibility(View.GONE);
            mFaceHolder.setVisibility(View.VISIBLE);
            mFaceRelativeLayout.setBackgroundColor(getResources().getColor(R.color.cx_fa_co_add_emotion_bg));
        } else if(sInputPanelUse.equals(CxNeighbourAddInvitation.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxNeighbourAddMessage.RK_CURRENT_VIEW)
        		|| sInputPanelUse.equals(CxZoneAddFeed.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxKidAddFeed.RK_CURRENT_VIEW)){
        	CxLog.i("RkInputPanel_men", sInputPanelUse+">>>>>>>3");
        	emotionBar.setVisibility(View.GONE);
            mFaceHolder.setVisibility(View.VISIBLE);
            mFaceRelativeLayout.setBackgroundColor(getResources().getColor(R.color.cx_fa_co_add_emotion_bg));
        }else{
        	CxLog.i("RkInputPanel_men", sInputPanelUse+">>>>>>>2");
            emotionBar.setVisibility(View.VISIBLE);
            mFaceHolder.setVisibility(View.GONE);
            mFaceRelativeLayout.setBackgroundResource(R.drawable.biaoqing_bg);
        }
     
        

//        boolean b = sp.getBoolean("DoubaoIsShow", false);
//        if (b) {
//            doubao_fl.setVisibility(View.VISIBLE);
//        } else {
//            doubao_fl.setVisibility(View.GONE);
//        }

        if (mContext instanceof CxMain) {
            main.availeChildSlide();
        }
        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != GONE) {
            mVoicePanel.setVisibility(GONE);
        }

        if (mFacePanel.getVisibility() != VISIBLE) {
            mFacePanel.setVisibility(VISIBLE);
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        mPlusButton1.setFocusableInTouchMode(true);
        mPlusButton1.requestFocus();
        mInputText.clearFocus();
        if (mTextPanel.getVisibility() != VISIBLE) {
            // mInputText.setFocusableInTouchMode(true);
            // mInputText.requestFocus();
            mTextPanel.setVisibility(VISIBLE);
        }
        changeSendButtonResource();
    }
    
    public void setOnlyFaceMode() {
        if (mEditMode != EditMode.FACE_MODE) {
            mEditMode = EditMode.FACE_MODE;
        }
        // force to display the soft keyboard
        enableSoftKeyboard(false);
        
        CxLog.i("RkInputPanel_men", sInputPanelUse);
        
        emotionBar.setVisibility(View.GONE);
        mFaceHolder.setVisibility(View.VISIBLE);
        mFaceRelativeLayout.setBackgroundColor(getResources().getColor(R.color.cx_fa_co_add_emotion_bg));
        
        
//        if (sInputPanelUse.equals(CxUsersPairZone.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxKidFragment.RK_CURRENT_VIEW)
//                || sInputPanelUse.equals(CxNeighbourFragment.RK_CURRENT_VIEW) || emotions==null) {
//            CxLog.i("RkInputPanel_men", sInputPanelUse+">>>>>>>1");
//            emotionBar.setVisibility(View.GONE);
//            mFaceHolder.setVisibility(View.VISIBLE);
//            mFaceRelativeLayout.setBackgroundColor(getResources().getColor(R.color.cx_fa_co_add_emotion_bg));
//        } else if(sInputPanelUse.equals(CxNeighbourAddInvitation.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxNeighbourAddMessage.RK_CURRENT_VIEW)
//                || sInputPanelUse.equals(CxZoneAddFeed.RK_CURRENT_VIEW) || sInputPanelUse.equals(CxKidAddFeed.RK_CURRENT_VIEW)){
//            CxLog.i("RkInputPanel_men", sInputPanelUse+">>>>>>>3");
//            emotionBar.setVisibility(View.GONE);
//            mFaceHolder.setVisibility(View.VISIBLE);
//            mFaceRelativeLayout.setBackgroundColor(getResources().getColor(R.color.cx_fa_co_add_emotion_bg));
//        }else{
//            CxLog.i("RkInputPanel_men", sInputPanelUse+">>>>>>>2");
//            emotionBar.setVisibility(View.VISIBLE);
//            mFaceHolder.setVisibility(View.GONE);
//            mFaceRelativeLayout.setBackgroundResource(R.drawable.biaoqing_bg);
//        }
//        
        
        
//        boolean b = sp.getBoolean("DoubaoIsShow", false);
//        if (b) {
//            doubao_fl.setVisibility(View.VISIBLE);
//        } else {
//            doubao_fl.setVisibility(View.GONE);
//        }
        
        if (mContext instanceof CxMain) {
            main.availeChildSlide();
        }
        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != GONE) {
            mVoicePanel.setVisibility(GONE);
        }
        
        if (mFacePanel.getVisibility() != VISIBLE) {
            mFacePanel.setVisibility(VISIBLE);
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        mPlusButton1.setFocusableInTouchMode(true);
        mPlusButton1.requestFocus();
        mInputText.clearFocus();
        if (mTextPanel.getVisibility() != GONE) {
            // mInputText.setFocusableInTouchMode(true);
            // mInputText.requestFocus();
            mTextPanel.setVisibility(GONE);
        }
        //changeSendButtonResource();
    }

    public void setTextMode() {
        if (mContext instanceof CxMain) {
            main.inavaileChildSlide();
        }
        
        CxLog.i("CxInputPanel_men", ">>>>>>>>>1");

        if (mEditMode != EditMode.TEXT_MODE) {
            mEditMode = EditMode.TEXT_MODE;
        }
        if (mPopupMenu.getVisibility() != GONE) {
            mPopupMenu.setVisibility(GONE);
        }
        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mSoundPanel.getVisibility() != GONE) {
            mSoundPanel.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != GONE) {
        	CxLog.i("CxInputPanel_men", ">>>>>>>>>2");
            mVoicePanel.setVisibility(GONE);
        }
        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }
        mInputText.setFocusableInTouchMode(true);
        mInputText.requestFocus();
        if (mTextPanel.getVisibility() != VISIBLE) {
            mPlusButton1.setFocusableInTouchMode(true);
            mPlusButton1.requestFocus();
            mTextPanel.setVisibility(VISIBLE);
        }
        mShowFaceButton = true;
        mFaceButton1.setImageResource(R.drawable.cx_fa_inputpanel_face_btn);
        changeSendButtonResource();
        // force to show soft keyboard
        enableSoftKeyboard(true);

    }

    public void setPopupMenuMode() {
        if (mEditMode != EditMode.POPUPMENU_MODE) {
            mEditMode = EditMode.POPUPMENU_MODE;
        }
        // force to display the soft keyboard
        enableSoftKeyboard(false);
        if (mContext instanceof CxMain) {
            main.inavaileChildSlide();
        }

        if (mFastPhrasePanel.getVisibility() != GONE) {
            mFastPhrasePanel.setVisibility(GONE);
        }
        if (mGuessPanel.getVisibility() != GONE) {
            mGuessPanel.setVisibility(GONE);
        }
        if (mSoundPanel.getVisibility() != GONE) {
            mSoundPanel.setVisibility(GONE);
        }
        if (mVoicePanel.getVisibility() != GONE) {
            mVoicePanel.setVisibility(GONE);
        }

        if (mFacePanel.getVisibility() != GONE) {
            mFacePanel.setVisibility(GONE);
        }
        mPlusButton1.setFocusableInTouchMode(true);
        mPlusButton1.requestFocus();
        if (mTextPanel.getVisibility() != VISIBLE) {
            mTextPanel.setFocusableInTouchMode(true);
            mTextPanel.requestFocus();
            mTextPanel.setVisibility(VISIBLE);
        }
        if (mPopupMenu.getVisibility() != VISIBLE) {
            mPopupMenu.setVisibility(VISIBLE);
        }
        changeSendButtonResource();
    }

    private void enableSoftKeyboard(boolean enable) {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (enable) {
            imm.showSoftInput(mInputText, InputMethodManager.SHOW_IMPLICIT);
            mEnableSoftKeyboard = true;
        } else {
            imm.hideSoftInputFromWindow(mInputText.getWindowToken(), 0);
            mEnableSoftKeyboard = false;
        }
    }

    class FaceAdapter extends BaseAdapter {

        private int mCount = 0;

        private TypedArray mImages = null;

        private String[] mTexts = null;

        private String[] mValues = null;

        public FaceAdapter(TypedArray images, String[] text, String[] values) {
            super();

            mCount = images.length();

            if (text != null)
                assert (mCount == text.length);

            assert (mCount == values.length);

            mImages = images;
            mTexts = text;
            mValues = values;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(int position) {
            return mValues[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.cx_fa_widget_face_cell, null);
            } else {
                view = convertView;
            }

            if (mImages != null) {
                ImageView image = (ImageView)view.findViewById(R.id.cx_fa_widget_face_cell__image);
                int imageResId = mImages.getResourceId(position, 0);
                image.setImageResource(imageResId);
            }

            return view;
        }

    }

    class PhraseAdapter extends BaseAdapter {

        private int mCount = 0;

        private TypedArray mImages = null;

        private String[] mTexts = null;

        private String[] mValues = null;

        public PhraseAdapter(TypedArray images, String[] text, String[] values) {
            super();

            mCount = images.length();

            if (text != null)
                assert (mCount == text.length);

            assert (mCount == values.length);

            mImages = images;
            mTexts = text;
            mValues = values;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(int position) {
            return mValues[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.cx_fa_widget_phrase_cell, null);
            } else {
                view = convertView;
            }

            if (mImages != null) {
                ImageView image = (ImageView)view
                        .findViewById(R.id.cx_fa_widget_face_phrase__image);
                int imageResId = mImages.getResourceId(position, 0);
                image.setImageResource(imageResId);
            }
            if (mTexts != null) {
                TextView text = (TextView)view.findViewById(R.id.cx_fa_widget_face_phrase__text);
                text.setText(mTexts[position]);
            }

            return view;
        }

    }

    /**
     * 表情viewpager要设置的adapter FragmentStatePagerAdapter
     * 
     * @author Administrator
     */
    private class GuideFacePageAdapter extends FragmentStatePagerAdapter {

        ArrayList<EmoticonFragment> fragments;

        public GuideFacePageAdapter(FragmentManager fm, ArrayList<EmoticonFragment> list) {
            super(fm);
            fragments = list;
        }

        @Override
        public Fragment getItem(int arg0) {

            return fragments.get(arg0);
        }

        @Override
        public int getCount() {

            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE; // 这个返回值和方法必须复写
        }

    }

    private class GuidePageAdapter extends PagerAdapter {
        private ArrayList<View> mPageViews;

        public GuidePageAdapter(ArrayList<View> pageViews) {
            this.mPageViews = pageViews;
        }

        @Override
        public int getCount() {
            return mPageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {

            ((ViewPager)arg0).removeView(mPageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {

            ((ViewPager)arg0).addView(mPageViews.get(arg1));
            return mPageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {

            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    private class GuidePageChangeListener implements OnPageChangeListener {
        private ArrayList<ImageView> mImageViews;

        public GuidePageChangeListener(ArrayList<ImageView> imageViews) {
            this.mImageViews = imageViews;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {

            int size = mImageViews.size();
            mImageViews.clear();
            mPhraseGroup.removeAllViews();
            for (int i = 0; i < size; i++) {
                mPhraseImageView = new ImageView(mContext);

                LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(
                        R.dimen.cx_fa_dimen_chat_emotion_dot_w), getResources()
                        .getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_h));// 设置imageview的宽高
                if (i == 0)
                    lp.setMargins(
                            0,
                            getResources().getDimensionPixelSize(
                                    R.dimen.cx_fa_dimen_chat_emotion_dot_top), 0, 0);// 设置和其他控件的距离
                else
                    lp.setMargins(
                            getResources().getDimensionPixelSize(
                                    R.dimen.cx_fa_dimen_chat_emotion_dot_left),
                            getResources().getDimensionPixelSize(
                                    R.dimen.cx_fa_dimen_chat_emotion_dot_top), 0, 0);
                mPhraseImageView.setLayoutParams(lp);
                mImageViews.add(mPhraseImageView);

                if (i == arg0) {
                    mImageViews.get(i).setImageResource(R.drawable.dot_focused);
                } else {
                    mImageViews.get(i).setImageResource(R.drawable.dot_normal);
                }
                mPhraseGroup.addView(mImageViews.get(i));
            }
        }
    }

    // int currentIndex;

    /**
     * viewpager的换页监听 每换一夜小圆点标识要随之变化
     */
    private class GuidePageChangeListener2 implements OnPageChangeListener {

        private ArrayList<ImageView> mImageViews = null;

        public GuidePageChangeListener2(ArrayList<ImageView> imageViews) {
            this.mImageViews = imageViews;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        /**
         * 换页时小圆点随之变化，但该种方式太耗内存，有待改进 和initDots相似
         */
        @Override
        public void onPageSelected(int arg0) {

            // System.out.println(arg0);
            // currentIndex=arg0;
            mFaceGroup.removeAllViews();
            // System.out.println("page change");
            mImageViews.clear();
            for (int i = 0; i < mEmoCount; i++) {
                mFaceImageView = new ImageView(mContext);

                LayoutParams lp = new LayoutParams(getResources().
                		getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_w), 
                		getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_h));// 设置imageview的宽高
                
                if (i == 0){
                    lp.setMargins(0,getResources().getDimensionPixelSize(
                    		R.dimen.cx_fa_dimen_chat_emotion_dot_top), 0, 0);// 设置和其他控件的距离
                }else{
                    lp.setMargins(getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_left),
                            getResources().getDimensionPixelSize(R.dimen.cx_fa_dimen_chat_emotion_dot_top), 0, 0);
                }
                
                mFaceImageView.setLayoutParams(lp);
                mImageViews.add(mFaceImageView);

                if (i == arg0) {
                    mImageViews.get(i).setImageResource(R.drawable.dot_focused);
                } else {
                    mImageViews.get(i).setImageResource(R.drawable.dot_normal);
                }
                mFaceGroup.addView(mImageViews.get(i));
            }

            currentPages.put(barPosition, arg0);
            // System.out.println(emoId+">>>>>>>>>>>>>>>>>>>>>>>"+arg0);

        }
    }
}
