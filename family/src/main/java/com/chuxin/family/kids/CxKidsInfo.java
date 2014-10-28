
package com.chuxin.family.kids;

import com.chuxin.family.R;
import com.chuxin.family.accounting.CxChangeAccountActivity.AccountMode;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.image.RoundAngleImageView;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.net.CxKidApi;
import com.chuxin.family.parse.been.CxKidsInfoData;
import com.chuxin.family.parse.been.data.DateData;
import com.chuxin.family.parse.been.data.KidFeedChildrenData;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnEidtSureClickListener;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class CxKidsInfo extends CxRootActivity {
    private static final int CHANGE_KID_IMG = 0;
    private KidFeedChildrenData mCurrentKidInfo;

    private RoundAngleImageView mKidsImage;
    private CxImageView mKidsImageRect;
    private LinearLayout mKidsImageLayout, mKidsImageRectLayout;

    private LinearLayout mKidsWholeNameLayout, mKidsUserNameLayout, mKidsGenderLayout,
            mKidsBirthdayLayout, mUserConsLayout, mRemarkLayout, mCustomLayout;

    private TextView mWholeNameText, mUserNameText, mGenderText, mBirthdayText, mConsText, mRemarkText,
            mCustomText;
    
    private ImageView mConsImage;
    private View mConsView;
    private View mAddView;

    private Dialog mSelectGenderDialog;

    private int mGender = -1; // 默认为-1； 男0；女1; -1 没有设置性别
    
    
    private String[] mGenderString = {"男", "女"};
    private String mCurrentKidId = null;
    private int mCurrentKidNum = 1;
    
    private LinearLayout mKidInfoTabLayout, mKidInfo1TabLayout, mKidInfo2TabLayout, mKidInfo3TabLayout, mKidInfo4TabLayout, mKidInfo5TabLayout;
    private TextView mKidInfoText, mKidInfo1Text, mKidInfo2Text, mKidInfo3Text, mKidInfo4Text, mKidInfo5Text;
    private Button mAddButton;
    private int mKidNum = 1;
    private Button mDeleteButton;
    private ArrayList<KidFeedChildrenData> mKidsData;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_fa_activity_mate_kids_info);
        init();
        Intent intent = getIntent();
        mCurrentKidNum = intent.getIntExtra("current_kid_id", 1);
        mKidsData = (ArrayList<KidFeedChildrenData>)intent.getSerializableExtra("kids_info");
        if(null == mKidsData || mKidsData.size() == 0){
            //默认初始化第一个孩子的数据
            KidFeedChildrenData kidData= new KidFeedChildrenData();
            mKidsData.add(kidData);
        }
        mCurrentKidInfo = mKidsData.get(mCurrentKidNum-1);
        fillData();
    }

    private void init() {
        Button backBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        backBtn.setBackgroundResource(R.drawable.cx_fa_back_btn);
        backBtn.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_navi_back), 16,
                Color.rgb(240, 236, 234)));
        backBtn.setOnClickListener(titleListener);
        
        TextView titleText = (TextView)findViewById(R.id.cx_fa_activity_title_info);
        titleText.setText(getString(R.string.cx_fa_kids_info_title_text));
        
        mAddButton = (Button)findViewById(R.id.cx_fa_activity_title_more);
        mAddButton.setVisibility(View.VISIBLE);
        mAddButton.setBackgroundResource(R.drawable.cx_fa_title_right_btn_bg);
        mAddButton.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_kids_info_add_kid), 16,
                Color.rgb(240, 236, 234)));
        mAddButton.setOnClickListener(titleListener);

        mKidsImage = (RoundAngleImageView)findViewById(R.id.cx_fa_mate_kids_info_icon);
        mKidsImageRect = (CxImageView)findViewById(R.id.cx_fa_mate_kids_info_icon1);
        int screen_w = getResources().getDisplayMetrics().widthPixels; // 屏幕宽度
        int imgWidth = screen_w - ScreenUtil.dip2px(this, 20) * 2; // 头像的宽 =
                                                                   // 屏幕的宽度-左右margin（假设margin-left、margin-right都是20px）
        int imgHeight = (int)(imgWidth * 0.67f); // 头像的高

        LinearLayout.LayoutParams lp = new LayoutParams(imgWidth, imgHeight);
        mKidsImage.setLayoutParams(lp);
        mKidsImageRect.setLayoutParams(lp);
        
        mKidsImageLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_family_info_icon_layout);
        mKidsImageRectLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_family_info_icon1_layout);
        mKidsImageLayout.setVisibility(View.VISIBLE);
        mKidsImageRectLayout.setVisibility(View.GONE);
        
        mKidInfoTabLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_layout);
        mKidInfo1TabLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info1_layout);
        mKidInfo2TabLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info2_layout);
        mKidInfo3TabLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info3_layout);
        mKidInfo4TabLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info4_layout);
        mKidInfo5TabLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info5_layout);
        mKidInfoTabLayout.setVisibility(View.GONE);
        
        mKidsWholeNameLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_whole_name_layout);
        mKidsUserNameLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_user_name_layout);
        mKidsGenderLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_gender_layout);
        mKidsBirthdayLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_birthday_layout);
        mUserConsLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_cons_layout);
        mRemarkLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_remark_layout);
        mCustomLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kdis_info_custom_layout);
        kidOtherLayout = (LinearLayout)findViewById(R.id.cx_fa_mate_kids_info_other_layout);

        mKidInfo1Text = (TextView)findViewById(R.id.cx_fa_mate_kids_info1_tv);
        mKidInfo2Text = (TextView)findViewById(R.id.cx_fa_mate_kids_info2_tv);
        mKidInfo3Text = (TextView)findViewById(R.id.cx_fa_mate_kids_info3_tv);
        mKidInfo4Text = (TextView)findViewById(R.id.cx_fa_mate_kids_info4_tv);
        mKidInfo5Text = (TextView)findViewById(R.id.cx_fa_mate_kids_info5_tv);
        
        mWholeNameText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_whole_name_tv);
        mUserNameText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_user_name_tv);
        mGenderText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_gender_tv);
        mBirthdayText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_birthday_tv);
        mConsText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_cons_tv);
        mRemarkText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_remark_tv);
        mCustomText = (TextView)findViewById(R.id.cx_fa_mate_kids_info_custom_tv);
        
        mConsImage = (ImageView)findViewById(R.id.cx_fa_mate_kids_info_user_cons_iv);
        mConsView = (View)findViewById(R.id.cx_fa_mate_kids_info_cons_view);
        
        mDeleteButton = (Button)findViewById(R.id.cx_fa_kids_info_delete_btn);
        mDeleteButton.setVisibility(View.GONE);
        
        mKidInfo1TabLayout.setOnClickListener(tabListener);
        mKidInfo2TabLayout.setOnClickListener(tabListener);
        mKidInfo3TabLayout.setOnClickListener(tabListener);
        mKidInfo4TabLayout.setOnClickListener(tabListener);
        mKidInfo5TabLayout.setOnClickListener(tabListener);
        
        mKidsImage.setOnClickListener(infoListener);
        mKidsImageRect.setOnClickListener(infoListener);
        
        mKidsWholeNameLayout.setOnClickListener(infoListener);
        mKidsUserNameLayout.setOnClickListener(infoListener);
        mKidsGenderLayout.setOnClickListener(infoListener);
        mKidsBirthdayLayout.setOnClickListener(infoListener);
        mUserConsLayout.setOnClickListener(infoListener);
        mRemarkLayout.setOnClickListener(infoListener);
        mCustomLayout.setOnClickListener(infoListener);
        mDeleteButton.setOnClickListener(infoListener);
        
        kidAddedTitles = new ArrayList<String>();
        kidTitleValues = new HashMap<String, String>();

    }
    
    private void fillData(){

        String kidTab1Text = "";
        String kidTab2Text = "";
        String kidTab3Text = "";
        String kidTab4Text = "";
        String kidTab5Text = "";
        if(mKidsData.size() < 2){
            if(!TextUtils.isEmpty(mKidsData.get(0).getNickname())){
                kidTab1Text = mKidsData.get(0).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(0).getName())){
                kidTab1Text = mKidsData.get(0).getName();
            } else {
                kidTab1Text = getString(R.string.cx_fa_add_kid1_tab_text);
            }
            mKidInfoTabLayout.setVisibility(View.GONE);
            mKidsImageLayout.setVisibility(View.VISIBLE);
            mKidsImageRectLayout.setVisibility(View.GONE);
        } else if(mKidsData.size() == 2){
            mKidInfoTabLayout.setVisibility(View.VISIBLE);
            mKidInfo1TabLayout.setVisibility(View.VISIBLE);
            mKidInfo2TabLayout.setVisibility(View.VISIBLE);
            mKidInfo3TabLayout.setVisibility(View.GONE);
            mKidInfo4TabLayout.setVisibility(View.GONE);
            mKidInfo5TabLayout.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(mKidsData.get(0).getNickname())){
                kidTab1Text = mKidsData.get(0).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(0).getName())){
                kidTab1Text = mKidsData.get(0).getName();
            } else {
                kidTab1Text = getString(R.string.cx_fa_add_kid1_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(1).getNickname())){
                kidTab2Text = mKidsData.get(1).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(1).getName())){
                kidTab2Text = mKidsData.get(1).getName();
            } else {
                kidTab2Text = getString(R.string.cx_fa_add_kid2_tab_text);
            }
            mKidsImageLayout.setVisibility(View.GONE);
            mKidsImageRectLayout.setVisibility(View.VISIBLE);
        } else if(mKidsData.size() == 3){
            mKidInfoTabLayout.setVisibility(View.VISIBLE);
            mKidInfo1TabLayout.setVisibility(View.VISIBLE);
            mKidInfo2TabLayout.setVisibility(View.VISIBLE);
            mKidInfo3TabLayout.setVisibility(View.VISIBLE);
            mKidInfo4TabLayout.setVisibility(View.GONE);
            mKidInfo5TabLayout.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(mKidsData.get(0).getNickname())){
                kidTab1Text = mKidsData.get(0).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(0).getName())){
                kidTab1Text = mKidsData.get(0).getName();
            } else {
                kidTab1Text = getString(R.string.cx_fa_add_kid1_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(1).getNickname())){
                kidTab2Text = mKidsData.get(1).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(1).getName())){
                kidTab2Text = mKidsData.get(1).getName();
            } else {
                kidTab2Text = getString(R.string.cx_fa_add_kid2_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(2).getNickname())){
                kidTab3Text = mKidsData.get(2).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(2).getName())){
                kidTab3Text = mKidsData.get(2).getName();
            } else {
                kidTab3Text = getString(R.string.cx_fa_add_kid3_tab_text);
            }
            mKidsImageLayout.setVisibility(View.GONE);
            mKidsImageRectLayout.setVisibility(View.VISIBLE);
        } else if(mKidsData.size() == 4){
            mKidInfoTabLayout.setVisibility(View.VISIBLE);
            mKidInfo1TabLayout.setVisibility(View.VISIBLE);
            mKidInfo2TabLayout.setVisibility(View.VISIBLE);
            mKidInfo3TabLayout.setVisibility(View.VISIBLE);
            mKidInfo4TabLayout.setVisibility(View.VISIBLE);
            mKidInfo5TabLayout.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(mKidsData.get(0).getNickname())){
                kidTab1Text = mKidsData.get(0).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(0).getName())){
                kidTab1Text = mKidsData.get(0).getName();
            } else {
                kidTab1Text = getString(R.string.cx_fa_add_kid1_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(1).getNickname())){
                kidTab2Text = mKidsData.get(1).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(1).getName())){
                kidTab2Text = mKidsData.get(1).getName();
            } else {
                kidTab2Text = getString(R.string.cx_fa_add_kid2_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(2).getNickname())){
                kidTab3Text = mKidsData.get(2).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(2).getName())){
                kidTab3Text = mKidsData.get(2).getName();
            } else {
                kidTab3Text = getString(R.string.cx_fa_add_kid3_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(3).getNickname())){
                kidTab4Text = mKidsData.get(3).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(3).getName())){
                kidTab4Text = mKidsData.get(3).getName();
            } else {
                kidTab4Text = getString(R.string.cx_fa_add_kid4_tab_text);
            }
            mKidsImageLayout.setVisibility(View.GONE);
            mKidsImageRectLayout.setVisibility(View.VISIBLE);
        } else if(mKidsData.size() == 5){
            mKidInfoTabLayout.setVisibility(View.VISIBLE);
            mKidInfo1TabLayout.setVisibility(View.VISIBLE);
            mKidInfo2TabLayout.setVisibility(View.VISIBLE);
            mKidInfo3TabLayout.setVisibility(View.VISIBLE);
            mKidInfo4TabLayout.setVisibility(View.VISIBLE);
            mKidInfo5TabLayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(mKidsData.get(0).getNickname())){
                kidTab1Text = mKidsData.get(0).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(0).getName())){
                kidTab1Text = mKidsData.get(0).getName();
            } else {
                kidTab1Text = getString(R.string.cx_fa_add_kid1_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(1).getNickname())){
                kidTab2Text = mKidsData.get(1).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(1).getName())){
                kidTab2Text = mKidsData.get(1).getName();
            } else {
                kidTab2Text = getString(R.string.cx_fa_add_kid2_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(2).getNickname())){
                kidTab3Text = mKidsData.get(2).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(2).getName())){
                kidTab3Text = mKidsData.get(2).getName();
            } else {
                kidTab3Text = getString(R.string.cx_fa_add_kid3_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(3).getNickname())){
                kidTab4Text = mKidsData.get(3).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(3).getName())){
                kidTab4Text = mKidsData.get(3).getName();
            } else {
                kidTab4Text = getString(R.string.cx_fa_add_kid4_tab_text);
            }
            if(!TextUtils.isEmpty(mKidsData.get(4).getNickname())){
                kidTab5Text = mKidsData.get(4).getNickname();
            } else if(!TextUtils.isEmpty(mKidsData.get(4).getName())){
                kidTab5Text = mKidsData.get(4).getName();
            } else {
                kidTab5Text = getString(R.string.cx_fa_add_kid5_tab_text);
            }
            mAddButton.setVisibility(View.INVISIBLE);
            mKidsImageLayout.setVisibility(View.GONE);
            mKidsImageRectLayout.setVisibility(View.VISIBLE);
        } else if(mKidsData.size() > 5){
            mAddButton.setVisibility(View.INVISIBLE);
            mKidsImageLayout.setVisibility(View.GONE);
            mKidsImageRectLayout.setVisibility(View.VISIBLE);
        }
        
        mKidInfo1TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        mKidInfo2TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        mKidInfo3TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        mKidInfo4TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        mKidInfo5TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        
        mKidInfo1Text.setText(TextUtil.getNewSpanStr(kidTab1Text, 16, Color.argb(144, 0, 0, 0)));
        mKidInfo2Text.setText(TextUtil.getNewSpanStr(kidTab2Text, 16, Color.argb(144, 0, 0, 0)));
        mKidInfo3Text.setText(TextUtil.getNewSpanStr(kidTab3Text, 16, Color.argb(144, 0, 0, 0)));
        mKidInfo4Text.setText(TextUtil.getNewSpanStr(kidTab4Text, 16, Color.argb(144, 0, 0, 0)));
        mKidInfo5Text.setText(TextUtil.getNewSpanStr(kidTab5Text, 16, Color.argb(144, 0, 0, 0)));
        
        if(mCurrentKidNum == 1){
            mKidInfo1TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
            mKidInfo1Text.setText(TextUtil.getNewSpanStr(kidTab1Text, 18, Color.rgb(235, 161, 121)));
        } else if(mCurrentKidNum == 2){
            mKidInfo2TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
            mKidInfo2Text.setText(TextUtil.getNewSpanStr(kidTab2Text, 18, Color.rgb(235, 161, 121)));
        } else if(mCurrentKidNum == 3){
            mKidInfo3TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
            mKidInfo3Text.setText(TextUtil.getNewSpanStr(kidTab3Text, 18, Color.rgb(235, 161, 121)));
        } else if(mCurrentKidNum == 4){
            mKidInfo4TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
            mKidInfo4Text.setText(TextUtil.getNewSpanStr(kidTab4Text, 18, Color.rgb(235, 161, 121)));
        } else if(mCurrentKidNum == 5){
            mKidInfo5TabLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
            mKidInfo5Text.setText(TextUtil.getNewSpanStr(kidTab5Text, 18, Color.rgb(235, 161, 121)));
        }
        
        if( null == mCurrentKidInfo){
            return;
        }
        
        if(!TextUtils.isEmpty(mCurrentKidInfo.getId())){
            mDeleteButton.setVisibility(View.VISIBLE);
        } else {
            mDeleteButton.setVisibility(View.GONE);
        }
       
        mKidsImage.displayImage(ImageLoader.getInstance(), mCurrentKidInfo.getAvata(), 
                R.drawable.cx_fa_memo_defaultimage_kid, true, 0);
        mKidsImageRect.displayImage(ImageLoader.getInstance(), mCurrentKidInfo.getAvata(), 
                R.drawable.cx_fa_memo_defaultimage_kid, true, 0);
        mWholeNameText.setText(mCurrentKidInfo.getName());
        mUserNameText.setText(mCurrentKidInfo.getNickname());
        if(mCurrentKidInfo.getGender() == -1){
            mGenderText.setText("");
        } else {
            mGenderText.setText(mGenderString[mCurrentKidInfo.getGender()]);
        }
        
        String birth = mCurrentKidInfo.getBirth();
        if(TextUtils.isEmpty(birth)|| birth.length()<8){
            mUserConsLayout.setVisibility(View.GONE);
            mConsView.setVisibility(View.GONE);
            mBirthdayText.setText("");               
        }else{
            mUserConsLayout.setVisibility(View.VISIBLE);
            mConsView.setVisibility(View.VISIBLE);
            mBirthdayText.setText(birth.substring(0,4)+"-"+birth.substring(4,6)+"-"+birth.substring(6,8));
            mConsText.setText(getSign(birth.substring(4,8)));
            mConsImage.setImageResource(getSignResource());
        }
        mRemarkText.setText(mCurrentKidInfo.getNote());
        
        String data = mCurrentKidInfo.getData();
        
        if(!TextUtils.isEmpty(data)){
            kidAddedTitles.clear();
            kidTitleValues.clear();
            kidOtherLayout.removeAllViews();
            try {
                JSONObject dataObj = new JSONObject(data);
                Iterator it = dataObj.keys();
                while(it.hasNext()){
                    String key = it.next().toString();
                    String value = dataObj.getString(key);
                    kidAddedTitles.add(key);
                    kidTitleValues.put(key, value);
                    addView(key, value, 1);
                }
            } catch (JSONException e) {
                CxLog.e("fillData", ""+e.getMessage());
            }
        }
    }

    OnClickListener titleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back:
                    back();
                    break;
                case R.id.cx_fa_activity_title_more:
                    addNewKid();
                    break;
            }
        }
    };
    
    private void addNewKid(){
        mCurrentKidInfo = new KidFeedChildrenData();
        //mCurrentKidNum += 1;
        mKidsData.add(mKidsData.size(), mCurrentKidInfo);
        mCurrentKidNum = mKidsData.size();
        kidAddedTitles.clear();
        kidTitleValues.clear();
        kidOtherLayout.removeAllViews();
        fillData();
    }
    
    OnClickListener tabListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.cx_fa_mate_kids_info1_layout:
                    mCurrentKidInfo = mKidsData.get(0);
                    mCurrentKidNum = 1;
                    fillData();
                    break;
                case R.id.cx_fa_mate_kids_info2_layout:
                    mCurrentKidInfo = mKidsData.get(1);
                    mCurrentKidNum = 2;
                    fillData();
                    break;
                case R.id.cx_fa_mate_kids_info3_layout:
                    mCurrentKidInfo = mKidsData.get(2);
                    mCurrentKidNum = 3;
                    fillData();
                    break;
                case R.id.cx_fa_mate_kids_info4_layout:
                    mCurrentKidInfo = mKidsData.get(3);
                    mCurrentKidNum = 4;
                    fillData();
                    break;
                case R.id.cx_fa_mate_kids_info5_layout:
                    mCurrentKidInfo = mKidsData.get(4);
                    mCurrentKidNum = 5;
                    fillData();
                    break;
                default:
                    break;
            }
                
        }
        
    };

    OnClickListener infoListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_mate_kids_info_icon:
                    Intent intent = new Intent(CxKidsInfo.this, ActivitySelectPhoto.class);
                    ActivitySelectPhoto.kIsCallPhotoZoom = true;
                    ActivitySelectPhoto.kIsCallFilter = false;
                    ActivitySelectPhoto.kIsCallSysCamera = true;
                    ActivitySelectPhoto.kChoseSingle = true;
                    startActivityForResult(intent, CHANGE_KID_IMG);
                    break;
                case R.id.cx_fa_mate_kids_info_icon1:
                    Intent intent1 = new Intent(CxKidsInfo.this, ActivitySelectPhoto.class);
                    ActivitySelectPhoto.kIsCallPhotoZoom = true;
                    ActivitySelectPhoto.kIsCallFilter = false;
                    ActivitySelectPhoto.kIsCallSysCamera = true;
                    ActivitySelectPhoto.kChoseSingle = true;
                    startActivityForResult(intent1, CHANGE_KID_IMG);
                    break;
                case R.id.cx_fa_mate_kids_info_whole_name_layout:
                    showWholeNameDialog();
                    break;
                case R.id.cx_fa_mate_kids_info_user_name_layout:
                    showNickNameDialog();
                    break;
                case R.id.cx_fa_mate_kids_info_gender_layout:
                    showGenderDialog();
                    break;
                case R.id.cx_fa_mate_kids_info_birthday_layout:
                    showBirthdayDialog();
                    break;
                case R.id.cx_fa_mate_kids_info_cons_layout:
                    break;
                case R.id.cx_fa_mate_kids_info_remark_layout:
                    showRemarkDialog();
                    break;
                case R.id.cx_fa_mate_kdis_info_custom_layout:
                    showMoreDialog();
                    break;
                case R.id.cx_fa_kids_info_delete_btn:
                    deleteKidInfo(mCurrentKidInfo.getId());
                    break;
            }
        }
    };
    
    private void deleteKidInfo(final String kidId){
        DialogUtil du = DialogUtil.getInstance();
        du.getSimpleDialog(CxKidsInfo.this, null, "确认删除？", null, null).show();
        du.setOnSureClickListener(new OnSureClickListener() {
            
            @Override
            public void surePress() {
                CxKidApi.getInstance().doDeleteKid(kidId, CxKidsInfo.this, deleteCallBack);
            }
        });
        
    }
    private void deleteMemoryKidInfo(){
        mKidsData.remove(mCurrentKidNum-1);
        mCurrentKidNum = mCurrentKidNum-1;
        new Handler(CxKidsInfo.this.getMainLooper()) {
            public void handleMessage(Message msg) {
                if(mCurrentKidNum >0){
                    mCurrentKidInfo = mKidsData.get(mCurrentKidNum-1);
                    fillData();
                } else {
                    addNewKid();
                }
            };
        }.sendEmptyMessage(0);
        
    }

    protected void showWholeNameDialog() {
        showDialog(mWholeNameText, "设置全名", 0, 1);
    }

    protected void showNickNameDialog() {
        showDialog(mUserNameText, "设置昵称", 0, 2);
    }
    
    protected void showRemarkDialog() {
        showDialog(mRemarkText,"设置备注",0,3);
    }

    protected void showGenderDialog() {
        View view = View.inflate(CxKidsInfo.this, R.layout.cx_fa_kids_info_eidt_gender, null);
        TextView manText = (TextView)view.findViewById(R.id.cx_fa_kids_info_edit_gender_man);
        TextView womenText = (TextView)view.findViewById(R.id.cx_fa_kids_info_edit_gender_women);
        Button cancleButton = (Button)view.findViewById(R.id.cx_fa_kids_info_edit_common_dialog_cancel);

        mSelectGenderDialog = new Dialog(this, R.style.simple_dialog);
        mSelectGenderDialog.setContentView(view);
        mSelectGenderDialog.show();

        manText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectGenderDialog.dismiss();
                mGenderText.setText(getString(R.string.cx_fa_kids_info_gender_man));
                mGender = 0;
                try {
                    CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), null, null, mGender+"", null, null, null, null, CxKidsInfo.this, genderCaller);
                } catch (Exception e) {
                    CxLog.i("CxKidsInfo", ""+e.getMessage());
                }
            }
        });
        womenText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectGenderDialog.dismiss();
                mGenderText.setText(getString(R.string.cx_fa_kids_info_gender_women));
                mGender = 1;
                try {
                    CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), null, null, mGender+"", null, null, null, null, CxKidsInfo.this, genderCaller);
                } catch (Exception e) {
                    CxLog.i("CxKidsInfo", ""+e.getMessage());
                }
            }
        });

        cancleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectGenderDialog.dismiss();
            }
        });
    }

    protected void showBirthdayDialog() {

        View view = View.inflate(CxKidsInfo.this,
                R.layout.cx_fa_widget_accounting_date_dialog, null);
        final DatePicker dateDp = (DatePicker)view
                .findViewById(R.id.cx_fa_accounting_change_account_date_dialog_dp);
        TextView titleText = (TextView)view
                .findViewById(R.id.cx_fa_mate_family_info_user_birthday_tv);
        Button cancelBtn = (Button)view
                .findViewById(R.id.cx_fa_accounting_change_account_date_dialog_cancel);
        Button okBtn = (Button)view
                .findViewById(R.id.cx_fa_accounting_change_account_date_dialog_ok);
        titleText.setText("设置生日");
        final Calendar c1 = Calendar.getInstance();

        String dateStr = mBirthdayText.getText().toString().trim();
        int year = 0;
        int month = 0;
        int day = 0;

        if (!TextUtils.isEmpty(dateStr)) {
            String[] split = dateStr.split("-");
            year = Integer.parseInt(split[0]);
            month = Integer.parseInt(split[1]) - 1;
            day = Integer.parseInt(split[2]);
        } else {
            year = c1.get(Calendar.YEAR);
            month = c1.get(Calendar.MONTH);
            day = c1.get(Calendar.DAY_OF_MONTH);
        }
        
        
        EditText monthE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1);	
		EditText yearE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1);	
		EditText dayE = (EditText)((ViewGroup)((ViewGroup) ((ViewGroup) dateDp.getChildAt(0)).getChildAt(0)).getChildAt(2)).getChildAt(1);	

		if(yearE != null) {
			yearE.setTextSize(16);
		}
		if(monthE != null) {
			monthE.setTextSize(16);
		}
		if(dayE != null) {
			dayE.setTextSize(16);
		}

        dateDp.init(year, month, day, new OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                c1.set(Calendar.YEAR, year);
                c1.set(Calendar.MONTH, monthOfYear);
                c1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });

        final Dialog dialog = new Dialog(CxKidsInfo.this, R.style.simple_dialog);
        dialog.setContentView(view);
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final DateData newDate = DateUtil.getNumberTime(c1.getTimeInMillis());

                dialog.dismiss();
                String date = newDate.getDateStr();
                mBirthdayText.setText(date);
                mConsText.setText(getSign(date.substring(5).replace("-", "")));
                mConsImage.setImageResource(getSignResource());
                if (mUserConsLayout.getVisibility() != View.VISIBLE) {
                    mUserConsLayout.setVisibility(View.VISIBLE);
                    mConsView.setVisibility(View.VISIBLE);
                }
                // TODO 网络接口
                try {
                    CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), null, null, null, date.replace("-", ""), null, null, null, CxKidsInfo.this, birthdayCaller);
                } catch (Exception e) {
                    CxLog.i("CxKidsInfo", ""+e.getMessage());
                }
            }
        });
        dialog.show();

    }

    /**
     * @param view
     * @param title
     * @param type
     * @param from 1 全名；2 昵称； 3 备注； 4 自定义
     */
    private void showDialog(final TextView view, final String title, int type, final int from) {
        DialogUtil nameDu = DialogUtil.getInstance();
        nameDu.getEditDialog(CxKidsInfo.this, view.getText().toString(), title, null, null, type)
                .show();
        nameDu.setOnEidtSureClickListener(new OnEidtSureClickListener() {
            @Override
            public void editSurePress(String str) {
                view.setText(str);
                if(from == 1){
                    try {
                        CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), str, null, null, null, null, null, null, CxKidsInfo.this, wholeNameCaller);
                    } catch (Exception e) {
                        CxLog.i("CxKidsInfo", ""+e.getMessage());
                    }
                } else if(from == 2){
                    try {
                        CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), null, str, null, null, null, null, null, CxKidsInfo.this, nickNameCaller);
                    } catch (Exception e) {
                        CxLog.i("CxKidsInfo", ""+e.getMessage());
                    }
                } else if(from == 3){
                    try {
                        CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), null, null, null, null, str, null, null, CxKidsInfo.this, noteCaller);
                    } catch (Exception e) {
                        CxLog.i("CxKidsInfo", ""+e.getMessage());
                    }
                } else {
                    updateCustomItems(title.substring(2), str, from);
                }
            }
        });
    }
    
    /**
     * 根据年月得到星座
     * @param strMmdd
     * @return
     */
    int mSignIndex = -1;
    private LinearLayout kidOtherLayout;
    
    private String getSign(String strMmdd){
        if (strMmdd.length() != 4 )
            return "";
        
        mSignIndex = -1;

        String arr[] = { CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Sagittarius),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Capricorn),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Aquarius),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Pisces),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Aries),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Taurus),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Gemini),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Cance),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Leo),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Virgo),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Libra),
                CxKidsInfo.this.getString(R.string.cx_fa_nls_mateprofile_Scorpio)};
        
       
        int intMmdd = Integer.parseInt(strMmdd);
        if(intMmdd>=1123 && intMmdd<=1221)
            mSignIndex = 0;
        else if(intMmdd >=1222 || intMmdd<=119)
            mSignIndex = 1;
        else if(intMmdd >=120 &&  intMmdd<=218)
            mSignIndex = 2;
        else if(intMmdd >=219 &&  intMmdd<=320)
            mSignIndex = 3;
        else if(intMmdd >=321 &&  intMmdd<=419)
            mSignIndex = 4;
        else if(intMmdd >=420 &&  intMmdd<=520)
            mSignIndex = 5;
        else if(intMmdd >=521 &&  intMmdd<=621)
            mSignIndex = 6;
        else if(intMmdd >=622 &&  intMmdd<=722)
            mSignIndex = 7;
        else if(intMmdd >=723 &&  intMmdd<=822)
            mSignIndex = 8;
        else if(intMmdd >=823 &&  intMmdd<=922)
            mSignIndex = 9;
        else if(intMmdd >=923 &&  intMmdd<=1023)
            mSignIndex = 10;
        else if(intMmdd >=1024 &&  intMmdd<=1122)
            mSignIndex = 11;
        
        return arr[mSignIndex];
    }
    
    //获取星座图标
    private int getSignResource() {
        if (mSignIndex == 0) return R.drawable.memo_astro_0;
        if (mSignIndex == 1) return R.drawable.memo_astro_1;
        if (mSignIndex == 2) return R.drawable.memo_astro_2;
        if (mSignIndex == 3) return R.drawable.memo_astro_3;
        if (mSignIndex == 4) return R.drawable.memo_astro_4;
        if (mSignIndex == 5) return R.drawable.memo_astro_5;
        if (mSignIndex == 6) return R.drawable.memo_astro_6;
        if (mSignIndex == 7) return R.drawable.memo_astro_7;
        if (mSignIndex == 8) return R.drawable.memo_astro_8;
        if (mSignIndex == 9) return R.drawable.memo_astro_9;
        if (mSignIndex == 10) return R.drawable.memo_astro_10;
        if (mSignIndex == 11) return R.drawable.memo_astro_11;
        
        return 0;
    }
    
    private PopupWindow mAddDialog = null;
    private LinearLayout mBgItem1, mBgItem2, mBgItem3, mBgItem4, mBgItem5, 
    mBgItem6, mBgItem7, mBgItem8, mBgItem9, mBgItem10;
    private TextView mTextItem1, mTextItem2, mTextItem3, mTextItem4, 
    mTextItem5, mTextItem6, mTextItem7, mTextItem8, mTextItem9, mTextItem10;
    private ImageButton mBtnAddMore;
    
    private ArrayList<String> kidAddedTitles=null;
    
    private HashMap<String, String> kidTitleValues;

    

    protected void showMoreDialog() {
        
        if (mAddView == null) {
            mAddView = CxKidsInfo.this.getLayoutInflater().inflate(R.layout.cx_fa_activity_mateprofile_add, null);  
            mBgItem1 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button1); 
            mBgItem2 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button2);       
            mBgItem3 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button3); 
            mBgItem4 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button4); 
            mBgItem5 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button5); 
            mBgItem6 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button6); 
            mBgItem7 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button7); 
            mBgItem8 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button8); 
            mBgItem9 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button9); 
            mBgItem10 = (LinearLayout)mAddView.findViewById(R.id.mateprofile_add_button10);
            
            mTextItem1 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button1_text); 
            mTextItem2 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button2_text);        
            mTextItem3 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button3_text); 
            mTextItem4 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button4_text); 
            mTextItem5 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button5_text); 
            mTextItem6 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button6_text); 
            mTextItem7 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button7_text); 
            mTextItem8 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button8_text); 
            mTextItem9 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button9_text); 
            mTextItem10 = (TextView)mAddView.findViewById(R.id.mateprofile_add_button10_text);  
            
            mBtnAddMore = (ImageButton)mAddView.findViewById(R.id.mateprofile_add_more_button); 
            mBtnAddMore.setOnClickListener(moreClickListener);          
            
            mBgItem1.setOnClickListener(moreClickListener);
            mBgItem2.setOnClickListener(moreClickListener);
            mBgItem3.setOnClickListener(moreClickListener);
            mBgItem4.setOnClickListener(moreClickListener);
            mBgItem5.setOnClickListener(moreClickListener);
            mBgItem6.setOnClickListener(moreClickListener);
            mBgItem7.setOnClickListener(moreClickListener);
            mBgItem8.setOnClickListener(moreClickListener);
            mBgItem9.setOnClickListener(moreClickListener);
            mBgItem10.setOnClickListener(moreClickListener);            
        }
        
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme1), mBgItem1, mTextItem1);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme2), mBgItem2, mTextItem2);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme3), mBgItem3, mTextItem3);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme4), mBgItem4, mTextItem4);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme5), mBgItem5, mTextItem5);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme6), mBgItem6, mTextItem6);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme7), mBgItem7, mTextItem7);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme8), mBgItem8, mTextItem8);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme9), mBgItem9, mTextItem9);
        setIsInUsed(getString(R.string.cx_fa_nls_mateprofile_edit_add_itme10), mBgItem10, mTextItem10);

        if (mAddDialog == null) {
            mAddDialog = new PopupWindow(mAddView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mAddDialog.setWidth(LayoutParams.MATCH_PARENT);
            mAddDialog.setHeight(LayoutParams.WRAP_CONTENT);
            mAddDialog.setBackgroundDrawable(getResources().getDrawable(R.color.cx_fa_co_border_grey));
            mAddDialog.setTouchable(true);
            mAddDialog.setOutsideTouchable(true);           
        } else {
            if (mAddDialog.isShowing()) {
                mAddDialog.dismiss();
                return;
            }           
        }
        
        mAddDialog.showAtLocation(mAddView, Gravity.BOTTOM, 0, 0);
    }
    
    OnClickListener moreClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            // 添加更多 相关按键
                case R.id.mateprofile_add_button1:
                case R.id.mateprofile_add_button2:
                case R.id.mateprofile_add_button3:
                case R.id.mateprofile_add_button4:
                case R.id.mateprofile_add_button5:
                case R.id.mateprofile_add_button6:
                case R.id.mateprofile_add_button7:
                case R.id.mateprofile_add_button8:
                case R.id.mateprofile_add_button9:
                case R.id.mateprofile_add_button10:
                    mAddDialog.dismiss();

                    String title = getTitle(v.getId());

                    ArrayList<String> mAddedTitles;
                    HashMap<String, String> titleValues;
                    mAddedTitles = kidAddedTitles;
                    titleValues = kidTitleValues;


                    // 判断是否已添加过
                    for (int i = 0; i < mAddedTitles.size(); i++) {
                        if (title.equalsIgnoreCase(mAddedTitles.get(i))) {
                            return;
                        }
                    }

                    mAddedTitles.add(title);
                    titleValues.put(title, "");

                    addView(title, "", 0);
                   

                    break;

                case R.id.mateprofile_add_more_button:
                    mAddDialog.dismiss();
                    showAddTitleDialog();

                    break;

                default:
                    break;
            }

        }
    };
    
    private void setIsInUsed(String title, LinearLayout bg, TextView text) {
        
        ArrayList<String>   mAddedTitles;
        mAddedTitles=kidAddedTitles;
        
        for(int i = 0; i < mAddedTitles.size(); i++) {   
            if (title.equalsIgnoreCase(mAddedTitles.get(i))) {
                bg.setBackgroundResource(R.drawable.cx_fa_mate_add_select_bg);
                text.setTextColor(getResources().getColor(R.color.cx_fa_co_btn_brown_text));
                return;
            }
        }       
        bg.setBackgroundResource(R.drawable.cx_fa_mate_add_unselect_bg);
        text.setTextColor(getResources().getColor(R.color.cx_fa_co_grey));
    }
    
    private void showAddTitleDialog(){
        DialogUtil nameDu = DialogUtil.getInstance();
        nameDu.getEditDialog(CxKidsInfo.this,null,"比如“爱好”" , "设置自定义资料项", null, null,0).show();
        nameDu.setOnEidtSureClickListener(new OnEidtSureClickListener() {                   
            @Override
            public void editSurePress(String str) {
                kidAddedTitles.add(str);
                kidTitleValues.put(str, "");
                addView(str,"",0);    
            }
        });
    }
    
    /**
     * 增加自定义字段
     * @param index
     * @param title
     * @param value
     * @param isNote
     * @param from 0，刚添加 需要联网保存；1 从网络获取data用来展示不需要联网保存
     */
    private void addView(final String title,final String value,int from){
        
            LinearLayout otherLayout;
            ArrayList<String> mAddedTitles;
            HashMap<String, String> titleValues;
            otherLayout=kidOtherLayout;
            mAddedTitles=kidAddedTitles;
            titleValues=kidTitleValues;
            
            final View tempView = CxKidsInfo.this.getLayoutInflater().inflate(R.layout.cx_fa_fragment_mate_family_info_item, null);

            LinearLayout otherLayout2 = (LinearLayout)tempView.findViewById(R.id.cx_fa_mate_family_info_user_other_layout);
            TextView otherTitleText = (TextView)tempView.findViewById(R.id.cx_fa_mate_family_info_user_other);
            final TextView otherText = (TextView)tempView.findViewById(R.id.cx_fa_mate_family_info_user_other_tv);
            
            otherTitleText.setText(title);
            otherText.setText(value);
            
            otherLayout2.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {                   
                    showDialog(otherText, "设置"+title, 0,4);
                }
            });
            

            LinearLayout delImg = (LinearLayout)tempView.findViewById(R.id.cx_fa_mate_family_info_user_other_del_layout);
            final LinearLayout otherLayout3=otherLayout;
            final ArrayList<String> mAddedTitles3=mAddedTitles;
            final HashMap<String, String> titleValues3=titleValues;
            delImg.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    DialogUtil du = DialogUtil.getInstance();
                    du.getSimpleDialog(CxKidsInfo.this, title, "确认删除？", null, null).show();
                    du.setOnSureClickListener(new OnSureClickListener() {
                        
                        @Override
                        public void surePress() {
                            otherLayout3.removeView(tempView);
                            mAddedTitles3.remove(title);
                            titleValues3.remove(title);
                            updateCustomItems(title, value,3);
                        }
                    });
                }
            });      
            otherLayout.addView(tempView);
            
            if(from==0){
                updateCustomItems(title, value,1);
            }       
    }
    
    /**
     * 
     * @param title
     * @param str
     * @param from  1 增加自定义；2 修改 ； 3 删除
     */
    private void updateCustomItems(String title,String str,int from){
        
        String data = "{";
        for (int i = 0; i < kidAddedTitles.size(); i++) {
            String key = kidAddedTitles.get(i);
//            CxLog.i("updateCustomItems", "key>>>>>>>>>>"+key);
            String value = kidTitleValues.get(key);
//            CxLog.i("updateCustomItems", "value>>>>>>>>>>"+value);
//            CxLog.i("updateCustomItems", "title>>>>>>>>>>"+title);
            if(key.equals(title)){
                value=str;
                kidTitleValues.put(key, value);
            }
            if("{".equals(data)){
                data += "\""+key + "\":\"" + value + "\"";
            } else {
                data += ",\""+key + "\":\"" + value + "\"";
            }
            
        }
        data += "}";
        
        CxLog.i("updateCustomItems", str+">>>>>>>>>>"+data);
        //TODO 添加网络接口
        try {
            CxKidApi.getInstance().updateKidInfo(mCurrentKidInfo.getId(), null, null, null, null, null, data, null, CxKidsInfo.this, customCaller);
        } catch (Exception e) {
            CxLog.i("CxKidsInfo", ""+e.getMessage());
        }
        
    }
    
    private String getTitle(int id) {
        switch (id) {
            case R.id.mateprofile_add_button1:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme1);
            case R.id.mateprofile_add_button2:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme2);
            case R.id.mateprofile_add_button3:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme3);
            case R.id.mateprofile_add_button4:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme4);
            case R.id.mateprofile_add_button5:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme5);
            case R.id.mateprofile_add_button6:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme6);
            case R.id.mateprofile_add_button7:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme7);
            case R.id.mateprofile_add_button8:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme8);
            case R.id.mateprofile_add_button9:
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme9);
            case R.id.mateprofile_add_button10: 
                return getString(R.string.cx_fa_nls_mateprofile_edit_add_itme10);
        }
        
        return "";
    }
    
 // 回调方法，从第二个页面回来的时候会执行这个方法  
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
            
        if (data == null){
            Log.e("CxKidsInfo", "更换头像时，从相机或相册返回的图片数据为空.");
            return;
        }
            
        
        String path = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
        
        CxLog.i("system return head image ", path);
        File file = new File(path.substring(7));
        if (!file.exists()) {
            Log.e("CxKidsInfo", "更换头像时，图片文件不存在！ path:"+ path);
            return;
        }
        //上传至服务器
        try {
            if(requestCode==CHANGE_KID_IMG){
                try {
                    CxKidApi.getInstance().updateKidInfoAvata(mCurrentKidInfo.getId(), null, null, null, null, null, null, path.substring(7), CxKidsInfo.this, kidImageCaller);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                                
                return;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void resetData(){
        new Handler(CxKidsInfo.this.getMainLooper()) {
            public void handleMessage(Message msg) {
                fillData();
            };
        }.sendEmptyMessage(0);
    }
    
    
    JSONCaller wholeNameCaller=new JSONCaller() {
            
            @Override
            public int call(Object result) {
                if(result==null){
                    showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                    resetData();
                    return -1;
                }
                
                
                CxKidsInfoData kidInfoObj = (CxKidsInfoData)result;
                String text=getString(R.string.cx_fa_net_response_code_fail);
                int rc = kidInfoObj.getRc();
                String msg = kidInfoObj.getMsg();
                if(rc != 0){
                    if(rc==408){
                        showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                        resetData();
                        return -2; 
                    } 
                    
                    if(!TextUtils.isEmpty(msg)){
                        text=msg;
                    }
                    showResponseToast(text,0);
                    
                    resetData();
                    return -2;
                }
    
                KidFeedChildrenData kidInfo = kidInfoObj.getKidInfo();
                String wholeName = kidInfo.getName();
                mCurrentKidInfo = kidInfo;
                mKidsData.remove(mCurrentKidNum-1);
                mKidsData.add(mCurrentKidNum-1, kidInfo);
                if(TextUtils.isEmpty(wholeName)){
                    showResponseToast(text,0);
                    resetData();
                }
                resetData();
                return 0;
            }
     };
     
     JSONCaller nickNameCaller=new JSONCaller() {
         
         @Override
         public int call(Object result) {
             if(result==null){
                 showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                 resetData();
                 return -1;
             }
             
             
             CxKidsInfoData kidInfoObj = (CxKidsInfoData)result;
             String text=getString(R.string.cx_fa_net_response_code_fail);
             int rc = kidInfoObj.getRc();
             String msg = kidInfoObj.getMsg();
             if(rc != 0){
                 if(rc==408){
                     showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                     resetData();
                     return -2; 
                 } 
                 
                 if(!TextUtils.isEmpty(msg)){
                     text=msg;
                 }
                 showResponseToast(text,0);
                 
                 resetData();
                 return -2;
             }
 
             KidFeedChildrenData kidInfo = kidInfoObj.getKidInfo();
             String nickName = kidInfo.getNickname();
             mCurrentKidInfo = kidInfo;
             mKidsData.remove(mCurrentKidNum-1);
             mKidsData.add(mCurrentKidNum-1, kidInfo);
             if(TextUtils.isEmpty(nickName)){
                 showResponseToast(text,0);
                 resetData();
             }
             resetData();
             return 0;
         }
  };
  
  JSONCaller genderCaller=new JSONCaller() {
      
      @Override
      public int call(Object result) {
          if(result==null){
              showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
              resetData();
              return -1;
          }
          
          
          CxKidsInfoData kidInfoObj = (CxKidsInfoData)result;
          String text=getString(R.string.cx_fa_net_response_code_fail);
          int rc = kidInfoObj.getRc();
          String msg = kidInfoObj.getMsg();
          if(rc != 0){
              if(rc==408){
                  showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                  resetData();
                  return -2; 
              } 
              
              if(!TextUtils.isEmpty(msg)){
                  text=msg;
              }
              showResponseToast(text,0);
              
              resetData();
              return -2;
          }

          KidFeedChildrenData kidInfo = kidInfoObj.getKidInfo();
          int gender = kidInfo.getGender();
          mCurrentKidInfo = kidInfo;
          mKidsData.remove(mCurrentKidNum-1);
          mKidsData.add(mCurrentKidNum-1, kidInfo);
          if(-1 == gender){
              showResponseToast(text,0);
              resetData();
          }
          resetData();
          return 0;
      }
  };
  
  JSONCaller birthdayCaller = new JSONCaller() {

      @Override
      public int call(Object result) {
          if(result==null){
              showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
              resetData();
              return -1;
          }
          
          
          CxKidsInfoData kidInfoObj = (CxKidsInfoData)result;
          String text=getString(R.string.cx_fa_net_response_code_fail);
          int rc = kidInfoObj.getRc();
          String msg = kidInfoObj.getMsg();
          if(rc != 0){
              if(rc==408){
                  showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                  resetData();
                  return -2; 
              } 
              
              if(!TextUtils.isEmpty(msg)){
                  text=msg;
              }
              showResponseToast(text,0);
              
              resetData();
              return -2;
          }

          KidFeedChildrenData kidInfo = kidInfoObj.getKidInfo();
          String birthday = kidInfo.getBirth();
          mCurrentKidInfo = kidInfo;
          mKidsData.remove(mCurrentKidNum-1);
          mKidsData.add(mCurrentKidNum-1, kidInfo);
          if(TextUtils.isEmpty(birthday)){
              showResponseToast(text,0);
              resetData();
          }
          resetData();
          return 0;
      }
  };
  
    JSONCaller noteCaller = new JSONCaller() {

        @Override
        public int call(Object result) {
            if(result==null){
                showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                resetData();
                return -1;
            }
            
            
            CxKidsInfoData kidInfoObj = (CxKidsInfoData)result;
            String text=getString(R.string.cx_fa_net_response_code_fail);
            int rc = kidInfoObj.getRc();
            String msg = kidInfoObj.getMsg();
            if(rc != 0){
                if(rc==408){
                    showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                    resetData();
                    return -2; 
                } 
                
                if(!TextUtils.isEmpty(msg)){
                    text=msg;
                }
                showResponseToast(text,0);
                
                resetData();
                return -2;
            }

            KidFeedChildrenData kidInfo = kidInfoObj.getKidInfo();
            String note = kidInfo.getNote();
            mCurrentKidInfo = kidInfo;
            mKidsData.remove(mCurrentKidNum-1);
            mKidsData.add(mCurrentKidNum-1, kidInfo);
            if(TextUtils.isEmpty(note)){
                showResponseToast(text,0);
                resetData();
            }
            resetData();
            return 0;
        }
    };
    
    JSONCaller customCaller = new JSONCaller() {
        
        @Override
        public int call(Object result) {
            if(result==null){
                showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                resetData();
                return -1;
            }
            
            
            CxKidsInfoData kidInfoObj = (CxKidsInfoData)result;
            String text=getString(R.string.cx_fa_net_response_code_fail);
            int rc = kidInfoObj.getRc();
            String msg = kidInfoObj.getMsg();
            if(rc != 0){
                if(rc==408){
                    showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                    resetData();
                    return -2; 
                } 
                
                if(!TextUtils.isEmpty(msg)){
                    text=msg;
                }
                showResponseToast(text,0);
                
                resetData();
                return -2;
            }

            KidFeedChildrenData kidInfo = kidInfoObj.getKidInfo();
            String data = kidInfo.getData();
            mCurrentKidInfo = kidInfo;
            mKidsData.remove(mCurrentKidNum-1);
            mKidsData.add(mCurrentKidNum-1, kidInfo);
            if(TextUtils.isEmpty(data)){
                showResponseToast(text,0);
                resetData();
            }
            resetData();
            return 0;
        }
    };
    
    JSONCaller deleteCallBack = new JSONCaller() {
        
        @Override
        public int call(Object result) {
            if (result == null) {
                showResponseToast(getString(R.string.cx_fa_net_response_code_null), 0);
                resetData();
                return -1;
            }
            JSONObject kidInfoObj = (JSONObject)result;
            String text = getString(R.string.cx_fa_net_response_code_fail);
            try {
                int rc = kidInfoObj.getInt("rc");
                String msg = kidInfoObj.getString("msg");
                if (rc != 0) {
                    if (rc == 408) {
                        showResponseToast(getString(R.string.cx_fa_net_response_code_null), 0);
                        resetData();
                        return -22;
                    }
                    
                    if (!TextUtils.isEmpty(msg)) {
                        text = msg;
                    }
                    showResponseToast(text, 0);
                    resetData();
                    return -2;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            deleteMemoryKidInfo();
            
            // 更新本地数据存储
            resetData();
            return 0;
        }
    };
    
    JSONCaller kidImageCaller=new JSONCaller() {
        
        @Override
        public int call(Object result) {
            
            String text=getString(R.string.cx_fa_net_response_code_fail);
            
            if (null == result) {
                showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                return -1;
            }
            CxKidsInfoData data = (CxKidsInfoData)result;
            
            try {
                int rc = data.getRc();
                String message=data.getMsg();
                if(rc==408){
                    showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
                    return -9;
                }
                
                if(!TextUtils.isEmpty(message)){
                    text=message;
                }
                
                if (0 != rc) {
                    showResponseToast(text,0);
                    return -3;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            KidFeedChildrenData kidInfo = data.getKidInfo();
            
            if (null == kidInfo) {
                showResponseToast(text,0);
                return -4;
            }
             String str = kidInfo.getAvata();
            
            if (str == null) {
                showResponseToast(text,0);
                return -9;
            }
            mCurrentKidInfo = kidInfo;
            mKidsData.remove(mCurrentKidNum-1);
            mKidsData.add(mCurrentKidNum-1, kidInfo);
            final String iconPath=str;
            new Handler(getMainLooper()){
                public void handleMessage(android.os.Message msg) {
                    mKidsImage.displayImage(ImageLoader.getInstance(), 
                            iconPath, R.drawable.cx_fa_memo_defaultimage_kid, false, 0);
                    mKidsImageRect.displayImage(ImageLoader.getInstance(), 
                            iconPath, R.drawable.cx_fa_memo_defaultimage_kid, false, 0);
                };
            }.sendEmptyMessageDelayed(1, 10);
            
            showResponseToast(getString(R.string.cx_fa_kid_info_kid_icon_success), 1);
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
        new Handler(CxKidsInfo.this.getMainLooper()) {
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
                ToastUtil.getSimpleToast(CxKidsInfo.this, id,
                        msg.obj.toString(), 1).show();
            };
        }.sendMessage(msg);
    }
    
    
    
    
    
    

	protected void back() {
		Message message = Message.obtain(CxKidFragment.mKidHandler, CxKidFragment.UPDATE_KIDS_INFO);
	    message.sendToTarget();
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
