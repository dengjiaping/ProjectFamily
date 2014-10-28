package com.chuxin.family.mate;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.R;
import com.chuxin.family.accounting.CxAccountingParam;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.image.RoundAngleImageView;
import com.chuxin.family.libs.gpuimage.CxGpuImageConstants;
import com.chuxin.family.libs.gpuimage.activity.ActivitySelectPhoto;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.neighbour.CxNbOurHome;
import com.chuxin.family.neighbour.CxNeighbourFragment;
import com.chuxin.family.net.CxMateProfileApi;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.CxSettingsParser;
import com.chuxin.family.parse.been.CxChangeHead;
import com.chuxin.family.parse.been.CxFamilyInfoData;
import com.chuxin.family.parse.been.CxUserAccount;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.parse.been.data.DateData;
import com.chuxin.family.parse.been.data.FamilyInfoUserInfo;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.settings.CxSettingActivity;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnEidtSureClickListener;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ScreenUtil;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;
import com.chuxin.family.widgets.TimePicker;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 资料页
 * @author wentong.men
 *
 */
public class CxFamilyInfoFragment extends Fragment {

	
	protected static final int CHANGE_FAMILY_IMG = 0;
	protected static final int CHANGE_USER_IMG = 1;
//	private RkImageView iconImg;
	private TextView nameText;
	private TextView birthdayText;
	private TextView consText;
	private TextView phoneText;
	private TextView remarkText;
	private RoundAngleImageView familyImg;
	private TextView oppoText;
	private TextView meText;
	
	private LinearLayout consLayout;
	private View nameView;
	private View consView;
	private View remarkView;
	private LinearLayout nameLayout;
	private LinearLayout remarkLayout;
	
	
	private int flag=1; //判断tag是对方还是自己  1  对方；2 自己
	
	
	private String familyImgPath="";
	private FamilyInfoUserInfo oppoInfo;
	private FamilyInfoUserInfo meInfo;
	
	private String customKey="";
	private String customValue="";	
	private int customFrom;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		oppoInfo=new FamilyInfoUserInfo();
		meInfo=new FamilyInfoUserInfo();
		
//		Intent intent = getActivity().getIntent();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		version_type = CxGlobalParams.getInstance().getVersion_type();
		View inflate = inflater.inflate(R.layout.cx_fa_fragment_mate_family_info, null);
		init(inflate);
		
		((CxMain) getActivity()).closeMenu();		
		
		CxFamilyInfoCacheData cacheData=new CxFamilyInfoCacheData(getActivity());
		CxFamilyInfoData queryCacheData = cacheData.queryCacheData(CxGlobalParams.getInstance().getUserId());
		if(queryCacheData!=null){
			familyImgPath=queryCacheData.getFamily_icon();
			oppoInfo=queryCacheData.getOppoInfo();
			meInfo=queryCacheData.getMeInfo();
			fillData();
		}
		
		UserApi.getInstance().requestFamilyInfo(getActivity(), familyInfoCaller);
		
		return inflate;
	}

	private void fillData() {
		
		
		if(version_type==2){
			familyImg.setImageResource(R.drawable.memo_defaultimage_family);
			familyImg.displayImage(ImageLoader.getInstance(), familyImgPath, R.drawable.memo_defaultimage_family, false, 0);
		}else{
			familyImg.setImageResource(R.drawable.memo_defaultimage);
			familyImg.displayImage(ImageLoader.getInstance(), familyImgPath, R.drawable.memo_defaultimage, false, 0);
		}
		
		fillCommonData();

		fillOppoData();

		fillMeData();	
				
	}
	
	
	
	private void fillOppoData(){
		String data = oppoInfo.getData();
		oppoAddedTitles.clear();
		oppoTitleValues.clear();
		oppoOtherLayout.removeAllViews();
		if(!TextUtils.isEmpty(data)){
			String[] strLine = data.split("\\n");
			
			for (int i = 0; i < strLine.length; i++) {
				String[] strKV = strLine[i].split(":");
				if (strKV[0].length() == 0) continue;
				
				if (strKV.length == 1) {	
					oppoAddedTitles.add(strKV[0]);
					oppoTitleValues.put(strKV[0], "");
					addView(strKV[0], "",1,1);
				}else{
					oppoAddedTitles.add(strKV[0]);
					oppoTitleValues.put(strKV[0], strKV[1]);
					addView(strKV[0], strKV[1],1,1);
				}
			}
		}
	}
	
	
	private void fillMeData(){
		String data = meInfo.getData();
		meAddedTitles.clear();
		meTitleValues.clear();
		meOtherLayout.removeAllViews();
		if(!TextUtils.isEmpty(data)){
			String[] strLine = data.split("\\n");
		
			for (int i = 0; i < strLine.length; i++) {
				String[] strKV = strLine[i].split(":");
				if(strKV[0].length()==0) continue;
				if(strKV.length==1){
					meAddedTitles.add(strKV[0]);				
					meTitleValues.put(strKV[0], "");
					addView(strKV[0], "",1,2);
				}else{
					meAddedTitles.add(strKV[0]);				
					meTitleValues.put(strKV[0], strKV[1]);
					addView(strKV[0], strKV[1],1,2);
				}
				
			}
		}
	}
	
	
	
	
	private void fillCommonData(){
		if(flag==1){
			iconOppoImg.displayImage(ImageLoader.getInstance(), oppoInfo.getIcon(), 
					CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, false, 0);
			nameText.setText(oppoInfo.getName());
			
			String birth = oppoInfo.getBirth();
			if(TextUtils.isEmpty(birth)|| birth.length()<8){
				consLayout.setVisibility(View.GONE);
				consView.setVisibility(View.GONE);				
				birthdayText.setText("");				
			}else{
				consLayout.setVisibility(View.VISIBLE);
				consView.setVisibility(View.VISIBLE);
				birthdayText.setText(birth.substring(0,4)+"-"+birth.substring(4,6)+"-"+birth.substring(6,8));
				consText.setText(getSign(birth.substring(4,8)));
				consImg.setImageResource(getSignResource());
			}
			
			phoneText.setText(oppoInfo.getMobile());			
			remarkText.setText(oppoInfo.getNote());	
		}else{
			iconMeImg.displayImage(ImageLoader.getInstance(), meInfo.getIcon(), 
					CxResourceDarwable.getInstance().dr_chat_icon_small_me, false, 0);
			birthdayText.setText(meInfo.getName());
			
			String birth = meInfo.getBirth();
			if(TextUtils.isEmpty(birth)|| birth.length()<8){
				consLayout.setVisibility(View.GONE);
				consView.setVisibility(View.GONE);
				birthdayText.setText("");				
			}else{
				consLayout.setVisibility(View.VISIBLE);
				consView.setVisibility(View.VISIBLE);
				birthdayText.setText(birth.substring(0,4)+"-"+birth.substring(4,6)+"-"+birth.substring(6,8));
				consText.setText(getSign(birth.substring(4,8)));
				consImg.setImageResource(getSignResource());
			}
			phoneText.setText(meInfo.getMobile());
		}
	}
	
	

	private void init(View view) {
		
		Button menuBtn = (Button) view.findViewById(R.id.cx_fa_mate_family_info_title_menu);
		menuBtn.setOnClickListener(tabListener);
		
		
		familyImg = (RoundAngleImageView) view.findViewById(R.id.cx_fa_mate_family_info_icon);
		
//		LinearLayout familyImgLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_icon_layout);
		
		
		int screen_w = getResources().getDisplayMetrics().widthPixels; // 屏幕宽度					
	    int imgWidth 	= screen_w - ScreenUtil.dip2px(getActivity(), 20)*2;				// 头像的宽 = 屏幕的宽度-左右margin（假设margin-left、margin-right都是20px）
		int imgHeight 	= (int)( imgWidth*0.67f );					// 头像的高
		
		LinearLayout.LayoutParams lp =new LayoutParams(imgWidth,imgHeight);
		familyImg.setLayoutParams(lp);
		
		
		oppoLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_oppo_layout);
		meLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_me_layout);
		LinearLayout homeLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_home_layout);
		LinearLayout kidLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_kid_layout);
		if(version_type==2){
			kidLayout.setVisibility(View.VISIBLE);
		}else{
			kidLayout.setVisibility(View.GONE);
		}
		
		oppoText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_oppo_tv);
		meText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_me_tv);
		
		iconOppoLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_icon_oppo_layout);
		iconMeLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_icon_me_layout);
		
		nameLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_name_layout);
		LinearLayout birthdayLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_birthday_layout);
		consLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_cons_layout);
		LinearLayout phoneLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_phone_layout);
		
		oppoOtherLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_other_oppo_layout);
		meOtherLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_other_me_layout);
		
		remarkLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_remark_layout);
		LinearLayout moreLayout = (LinearLayout) view.findViewById(R.id.cx_fa_mate_family_info_user_more_layout);
		
		nameView = (View) view.findViewById(R.id.cx_fa_mate_family_info_user_name_view);
		consView = (View) view.findViewById(R.id.cx_fa_mate_family_info_user_cons_view);
		remarkView = (View) view.findViewById(R.id.cx_fa_mate_family_info_user_remark_view);
		
		iconOppoImg = (CxImageView) view.findViewById(R.id.cx_fa_mate_family_info_user_icon_oppo_riv);		
		iconMeImg = (CxImageView) view.findViewById(R.id.cx_fa_mate_family_info_user_icon_me_riv);		
		
		nameText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_user_name_tv);		
		birthdayText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_user_birthday_tv);
		consText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_user_cons_tv);
		consImg = (ImageView) view.findViewById(R.id.cx_fa_mate_family_info_user_cons_iv);
		phoneText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_user_phone_tv);
		remarkText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_user_remark_tv);
		
		oppoLayout.setOnClickListener(tabListener);
		meLayout.setOnClickListener(tabListener);
		homeLayout.setOnClickListener(tabListener);
		kidLayout.setOnClickListener(tabListener);
		
		familyImg.setOnClickListener(userListener);
		iconOppoLayout.setOnClickListener(userListener);
		iconMeLayout.setOnClickListener(userListener);
		nameLayout.setOnClickListener(userListener);
		birthdayLayout.setOnClickListener(userListener);
		consLayout.setOnClickListener(userListener);
		phoneLayout.setOnClickListener(userListener);
		remarkLayout.setOnClickListener(userListener);
		moreLayout.setOnClickListener(userListener);
		
		oppoAddedTitles=new ArrayList<String>();
		meAddedTitles=new ArrayList<String>();
		oppoTitleValues=new HashMap<String, String>();
		meTitleValues=new HashMap<String, String>();
		
		initData();
		
	}
	
	
	private void initData() {
		
		if(flag==1){
			oppoTab();
			nameLayout.setVisibility(View.VISIBLE);
			nameView.setVisibility(View.VISIBLE);
			remarkLayout.setVisibility(View.VISIBLE);
			remarkView.setVisibility(View.VISIBLE);
//			iconImg.setImageResource(RkResourceDarwable.getInstance().dr_chat_icon_small_oppo);
			
		}else if(flag==2){
			meTab();
			nameLayout.setVisibility(View.GONE);
			nameView.setVisibility(View.GONE);
			remarkLayout.setVisibility(View.GONE);
			remarkView.setVisibility(View.GONE);
//			iconImg.setImageResource(RkResourceDarwable.getInstance().dr_chat_icon_small_me);
			
		}
		
	}


	OnClickListener tabListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.cx_fa_mate_family_info_oppo_layout:
				if(flag==1){
					return;
				}
				flag=1;
				initData();
				fillCommonData();
				break;
			case R.id.cx_fa_mate_family_info_me_layout:
				if(flag==2){
					return;
				}
				flag=2;
				initData();
				fillCommonData();
				break;
			case R.id.cx_fa_mate_family_info_home_layout:
				String wifeUrl="";
				String husbandUrl="";
				
				if(CxGlobalParams.getInstance().getVersion()==0){
					wifeUrl=CxGlobalParams.getInstance().getPartnerIconBig();
					husbandUrl=CxGlobalParams.getInstance().getIconBig();
				}else{
					husbandUrl=CxGlobalParams.getInstance().getPartnerIconBig();
					wifeUrl=CxGlobalParams.getInstance().getIconBig();
				}
				
				Intent intent=new Intent(getActivity(),CxNbOurHome.class);
				intent.putExtra(CxGlobalConst.S_NEIGHBOUR_WIFE_URL,wifeUrl);
				intent.putExtra(CxGlobalConst.S_NEIGHBOUR_HUSBAND_URL,husbandUrl);
				intent.putExtra(CxGlobalConst.S_NEIGHBOUR_PAIR_ID,CxGlobalParams.getInstance().getPairId());
				startActivity(intent);	
				getActivity().overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
			case R.id.cx_fa_mate_family_info_title_menu:
				if(mAddDialog!=null && mAddDialog.isShowing()){
					mAddDialog.dismiss();
				}
				((CxMain)getActivity()).toggleMenu();
				break;
			case R.id.cx_fa_mate_family_info_kid_layout:
				((CxMain)getActivity()).menuEvent(CxMain.KID);
				break;
			}
			
		}
	};
	
	
	OnClickListener userListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.cx_fa_mate_family_info_icon: //家庭背景
				Intent intent = new Intent(getActivity(), ActivitySelectPhoto.class);
        		ActivitySelectPhoto.kIsCallPhotoZoom = true;
        		ActivitySelectPhoto.kIsCallFilter = false;
        		ActivitySelectPhoto.kIsCallSysCamera = true;
        		ActivitySelectPhoto.kChoseSingle = true;
        		startActivityForResult(intent, CHANGE_FAMILY_IMG);  
				break;
			case R.id.cx_fa_mate_family_info_user_icon_oppo_layout: //对方或自己的头像
			case R.id.cx_fa_mate_family_info_user_icon_me_layout:
				Intent selectImageForHead = new Intent(getActivity(), ActivitySelectPhoto.class);
				ActivitySelectPhoto.kIsCallPhotoZoom =true;
				ActivitySelectPhoto.kIsCallFilter = false;
				ActivitySelectPhoto.kIsCallSysCamera = true;
				ActivitySelectPhoto.kChoseSingle = true;
				startActivityForResult(selectImageForHead, CHANGE_USER_IMG);   	
				break;
			case R.id.cx_fa_mate_family_info_user_name_layout://昵称
				showNameDialog();  
				break;
			case R.id.cx_fa_mate_family_info_user_birthday_layout://生日
				showBirthdayDialog();
				break;
			case R.id.cx_fa_mate_family_info_user_cons_layout:
				
				break;
			case R.id.cx_fa_mate_family_info_user_phone_layout://电话
				showPhoneDialog();
				break;
			case R.id.cx_fa_mate_family_info_user_remark_layout://备注
				showRemarkDialog();
				break;
			case R.id.cx_fa_mate_family_info_user_more_layout: 
				showMoreDialog();
				break;
			}
			
		}
	};
	private LinearLayout oppoLayout;
	private LinearLayout meLayout;
	private View mAddView;


	private void oppoTab() {
		oppoOtherLayout.setVisibility(View.VISIBLE);
		meOtherLayout.setVisibility(View.GONE);
		iconOppoLayout.setVisibility(View.VISIBLE);
		iconMeLayout.setVisibility(View.GONE);
		oppoLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
		oppoText.setText(TextUtil.getNewSpanStr(getString(CxResourceString.getInstance().str_pair), 18, Color.rgb(235, 161, 121)));
		meLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
		meText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_mate_family_info_tab_me), 16, Color.argb(144, 0, 0, 0)));
	}
	

	private void meTab() {
		oppoOtherLayout.setVisibility(View.GONE);
		meOtherLayout.setVisibility(View.VISIBLE);
		iconOppoLayout.setVisibility(View.GONE);
		iconMeLayout.setVisibility(View.VISIBLE);
		oppoLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
		oppoText.setText(TextUtil.getNewSpanStr(getString(CxResourceString.getInstance().str_pair), 16, Color.argb(144, 0, 0, 0)));
		meLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
		meText.setText(TextUtil.getNewSpanStr(getString(R.string.cx_fa_mate_family_info_tab_me), 18, Color.rgb(235, 161, 121)));
	}
	

	protected void showNameDialog() {
		showDialog(nameText,"设置昵称",0,1);		
	}
	
	
	protected void showBirthdayDialog() {
		
		View view = View.inflate(getActivity(), R.layout.cx_fa_widget_accounting_date_dialog,null);
		final DatePicker dateDp = (DatePicker) view.findViewById(R.id.cx_fa_accounting_change_account_date_dialog_dp);
		TextView titleText = (TextView) view.findViewById(R.id.cx_fa_mate_family_info_user_birthday_tv);
		Button cancelBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_date_dialog_cancel);
		Button okBtn = (Button) view.findViewById(R.id.cx_fa_accounting_change_account_date_dialog_ok);
		titleText.setText("设置生日");
		final Calendar c1 = Calendar.getInstance();	
		
		String dateStr = birthdayText.getText().toString().trim();
		int year=0;
		int month=0;
		int day=0;
		
		if(!TextUtils.isEmpty(dateStr)){
			String[] split = dateStr.split("-");
			year=Integer.parseInt(split[0]);
			month=Integer.parseInt(split[1])-1;
			day=Integer.parseInt(split[2]);
		}else{
			year=c1.get(Calendar.YEAR);
			month=c1.get(Calendar.MONTH);
			day=c1.get(Calendar.DAY_OF_MONTH);
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
		
		
		dateDp.init(year,month,day, new OnDateChangedListener(){

            public void onDateChanged(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
            	
            	c1.set(Calendar.YEAR, year);
            	c1.set(Calendar.MONTH, monthOfYear);
            	c1.set(Calendar.DAY_OF_MONTH, dayOfMonth);                     	
            }
        });
		
//		dateDp.setDate(year, month + 1,day);
//		dateDp.setOnDateChangeListener(new OnDateChangeListener() {
//			
//			@Override
//			public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
//			}
//			
//			@Override
//			public void onDateChange(DatePicker view, int year, int monthOfYear,
//					int dayOfMonth) {
//				c1.set(Calendar.YEAR, year);
//            	c1.set(Calendar.MONTH, monthOfYear-1);
//            	c1.set(Calendar.DAY_OF_MONTH, dayOfMonth);    
//				
//			}
//		});
		
		
		final Dialog dialog=new Dialog(getActivity(), R.style.simple_dialog);		
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
				
				final DateData newDate=DateUtil.getNumberTime(c1.getTimeInMillis());
				
				dialog.dismiss();
				String date = newDate.getDateStr();
				birthdayText.setText(date);
				consText.setText(getSign(date.substring(5).replace("-", "")));
				consImg.setImageResource(getSignResource());
				if(consLayout.getVisibility()!=View.VISIBLE){
					consLayout.setVisibility(View.VISIBLE);
					consView.setVisibility(View.VISIBLE);
				}
				try {
					if(flag==1){
						CxMateProfileApi.getInstance().postMateProfileInfo(null, date.replace("-", ""), null, null, null, null, oppoBirthCaller);
					}else{
						UserApi.getInstance().updateUserProfile(null, date.replace("-", ""), null, null, null, null, null, meBirthCaller);
					}
				} catch (Exception e) {				
					e.printStackTrace();
				}	
			}
		});
		dialog.show();
		
	}
	
	
	protected void showPhoneDialog() {
		
		showDialog(phoneText,"设置电话",1,2);
	}
	
	protected void showRemarkDialog() {
		showDialog(remarkText,"设置备注",0,3);
	}
	
	private PopupWindow mAddDialog = null;
	private LinearLayout mBgItem1, mBgItem2, mBgItem3, mBgItem4, mBgItem5, 
    mBgItem6, mBgItem7, mBgItem8, mBgItem9, mBgItem10;
    private TextView mTextItem1, mTextItem2, mTextItem3, mTextItem4, 
    mTextItem5, mTextItem6, mTextItem7, mTextItem8, mTextItem9, mTextItem10;
    private ImageButton mBtnAddMore;
    
    private ArrayList<String> oppoAddedTitles=null;
    private ArrayList<String> meAddedTitles=null;
    
    private HashMap<String, String> oppoTitleValues;
    private HashMap<String, String> meTitleValues;

    

	protected void showMoreDialog() {
		
		if (mAddView == null) {
			mAddView = getActivity().getLayoutInflater().inflate(R.layout.cx_fa_activity_mateprofile_add, null);  
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
	
	private void setIsInUsed(String title, LinearLayout bg, TextView text) {
		
		ArrayList<String>   mAddedTitles;
		if(flag==1){
			mAddedTitles=oppoAddedTitles;
		}else{
			mAddedTitles=meAddedTitles;
		}
		
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
	

	/**
	 * 
	 * @param view
	 * @param title
	 * @param type
	 * @param from  1 昵称；2 电话；3 备注； 4 自定义
	 */
	private void showDialog(final TextView view,final String title,int type,final int from){
		DialogUtil nameDu = DialogUtil.getInstance();
		nameDu.getEditDialog(getActivity(), view.getText().toString(), title, null, null,type).show();
		nameDu.setOnEidtSureClickListener(new OnEidtSureClickListener() {					
			@Override
			public void editSurePress(String str) {
				view.setText(str);
				if(from==1){
					try {
						CxMateProfileApi.getInstance().postMateProfileInfo(str, null, null, null, null, null, nameCaller);
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}else if(from==2){
					try {
						if(flag==1){
							CxMateProfileApi.getInstance().postMateProfileInfo(null, null, str, null, null, null, oppoPhoneCaller);
						}else{
							UserApi.getInstance().updateUserProfile(null, null, null, null, null, str, null, mePhoneCaller);
						}					
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}else if(from==3){
					try {
						CxMateProfileApi.getInstance().postMateProfileInfo(null, null, null, str, null, null, remarkCaller);
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}else{
					updateCustomItems(title.substring(2), str,2);
				}
			}
		});
	}
	
	private void showAddTitleDialog(){
		DialogUtil nameDu = DialogUtil.getInstance();
		nameDu.getEditDialog(getActivity(),null,"比如“爱好”" , "设置自定义资料项", null, null,0).show();
		nameDu.setOnEidtSureClickListener(new OnEidtSureClickListener() {					
			@Override
			public void editSurePress(String str) {
				if(flag==1){
					oppoAddedTitles.add(str);
					oppoTitleValues.put(str, "");
					addView(str,"",0,1);	
				}else{
					meAddedTitles.add(str);
					meTitleValues.put(str, "");
					addView(str,"",0,1);	
				}
				
			}
		});
	}
	
	
	
	OnClickListener moreClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//添加更多 相关按键
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
				if(flag==1){				
					mAddedTitles=oppoAddedTitles;
					titleValues=oppoTitleValues;
				}else{
					mAddedTitles=meAddedTitles;
					titleValues=meTitleValues;
				}
				
				//判断是否已添加过
				for(int i = 0; i < mAddedTitles.size(); i++) {   
				    if (title.equalsIgnoreCase(mAddedTitles.get(i))) {
				    	return;
				    }
				}
								
    			mAddedTitles.add(title);
    			titleValues.put(title, "");
    			
    			if(flag==1){
    				addView(title,"",0,1);
    			}else{
    				addView(title,"",0,2);
    			}
    			
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
	private ImageView consImg;
	
	
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
	
	
	/**
	 * 增加自定义字段
	 * @param index
	 * @param title
	 * @param value
	 * @param isNote
	 * @param from 0，刚添加 需要联网保存；1 从网络获取data用来展示不需要联网保存
	 */
	private void addView(final String title,final String value,int from,int flag){
		
			LinearLayout otherLayout;
			ArrayList<String> mAddedTitles;
			HashMap<String, String> titleValues;
			if(flag==1){
				otherLayout=oppoOtherLayout;
				mAddedTitles=oppoAddedTitles;
				titleValues=oppoTitleValues;
			}else{
				otherLayout=meOtherLayout;
				mAddedTitles=meAddedTitles;
				titleValues=meTitleValues;
			}
			
			final View tempView = getActivity().getLayoutInflater().inflate(R.layout.cx_fa_fragment_mate_family_info_item, null);

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
					du.getSimpleDialog(getActivity(), title, "确认删除？", null, null).show();
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

	

	
	
	// 回调方法，从第二个页面回来的时候会执行这个方法  
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	  	
    	if (data == null){
    		Log.e("RkFamilyInfoFragment.java", "更换头像时，从相机或相册返回的图片数据为空.");
    		return;
    	}
    		
    	
    	String path = data.getStringExtra(CxGpuImageConstants.KEY_PICTURE_URI);
    	
    	CxLog.i("system return head image ", path);
    	File file = new File(path.substring(7));
    	if (!file.exists()) {
    		Log.e("RkMate.java", "更换头像时，图片文件不存在！ path:"+ path);
			return;
		}
	    //上传至服务器
		try {
			if(requestCode==CHANGE_FAMILY_IMG){
				try {
					UserApi.getInstance().sendFamilyImage(path.substring(7), "background", familyImgCaller);
				} catch (Exception e) {
					e.printStackTrace();
				}
								
				return;
			}
			
			if(requestCode==CHANGE_USER_IMG){
				if(flag==1){
					try {
						CxSendImageApi.getInstance().sendHeadImage(path.substring(7),
								CxSettingsParser.SendHeadImageType.HEAD_PARTNER, oppoIconCaller);	
					} catch (Exception e) {
						e.printStackTrace();
					}						
				}else if(flag==2){
					try {
						CxSendImageApi.getInstance().sendHeadImage(path.substring(7),
								CxSettingsParser.SendHeadImageType.HEAD_ME, meIconCaller);	
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    } 
    
    
    JSONCaller familyImgCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
//			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			
//			String text=RkFamilyInfoFragment.this.getActivity().getString(R.string.cx_fa_mate_family_info_family_icon_fail);
			String text=CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail);
			
			if (null == result) {

				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			JSONObject changeBg = null;
			try {
				changeBg = (JSONObject) result;
			} catch (Exception e) {
			}
			if (null == changeBg) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}

			
			
			try {
				int rc = changeBg.getInt("rc");
				String message="";
				if(!changeBg.isNull("msg")){
					message=changeBg.getString("msg");
				}
				if(rc==408){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
					return -9;
				}
				
				if(!TextUtils.isEmpty(message)){
					text=message;
				}
				
				if (0 != rc) {
					showResponseToast(text,0);
					return -3;
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
				return -4;
			}
			String str = null;
			
			try {				
				str=data.getString("family_big");
			} catch (Exception e) {
				
			}
			
			if (str == null) {
				showResponseToast(text,0);
				return -9;
			}
			final String iconPath=str;
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(android.os.Message msg) {
					/*mImageView.setImage(RkMateParams.getInstance().getMateIcon(), 
							false, 240, RkMate.this, "head", RkMate.this.getActivity());*/
					if(version_type==2){
						familyImg.displayImage(ImageLoader.getInstance(), 
								iconPath, R.drawable.memo_defaultimage_family, false, 0);
						
					}else{
						familyImg.displayImage(ImageLoader.getInstance(), 
								iconPath, R.drawable.memo_defaultimage, false, 0);
					}
					// 更新缓存
					//（暂时不需要存对方的头像如数据库，因为每次登录或者结对成功，对方的资料都会拉一次）
				
				};
			}.sendEmptyMessageDelayed(1, 10);
			
			showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_mate_family_info_family_icon_success), 1);
			CxGlobalParams.getInstance().setFamily_big(iconPath);

			return 0;
		}
	};
	
	JSONCaller meIconCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			
				if (null == result) {
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
					return -1;
				}
				
				CxChangeHead meProfileData = null;
				try {
					meProfileData = (CxChangeHead)result;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null == meProfileData || meProfileData.getRc()==408 ) {
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
					return -1;
				}
				if (0 != meProfileData.getRc()) {
					if(TextUtils.isEmpty(meProfileData.getMsg())){
						showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
					}else{
						showResponseToast(meProfileData.getMsg(),0);
					}
					return 1;
				}
				if (null == meProfileData.getData()) {
					return 0;
				}
				final String iconPath=meProfileData.getData().getIcon_big();
				new Handler(getActivity().getMainLooper()){
					public void handleMessage(android.os.Message msg) {
						/*mImageView.setImage(RkMateParams.getInstance().getMateIcon(), 
								false, 240, RkMate.this, "head", RkMate.this.getActivity());*/
						meInfo.setIcon(iconPath);
						iconMeImg.displayImage(ImageLoader.getInstance(), 
								iconPath, 
								CxResourceDarwable.getInstance().dr_chat_icon_small_me, false, 0);
						CxGlobalParams.getInstance().setIconBig(iconPath);
						CxGlobalParams.getInstance().setIconMid(iconPath);
						CxGlobalParams.getInstance().setIconSmall(iconPath);
						// 更新缓存
						//（暂时不需要存对方的头像如数据库，因为每次登录或者结对成功，对方的资料都会拉一次）
					
					};
				}.sendEmptyMessageDelayed(1, 10);
				//目前只返回了大图 先这么做  (中小头像已经不需要了)
				
				
				return 0;
		}
	};
	JSONCaller oppoIconCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxChangeHead mateProfileData = null;
			try {
				mateProfileData = (CxChangeHead)result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == mateProfileData || mateProfileData.getRc()==408) {
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			if (0 != mateProfileData.getRc()) {
				if(TextUtils.isEmpty(mateProfileData.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(mateProfileData.getMsg(),0);
				}
				return 1;
			}
			if (null == mateProfileData.getData()) {
				return 0;
			}
			
			CxMateParams.getInstance().setMateIcon(mateProfileData.getData().getIcon_big());
			
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(android.os.Message msg) {
					/*mImageView.setImage(RkMateParams.getInstance().getMateIcon(), 
							false, 240, RkMate.this, "head", RkMate.this.getActivity());*/
					oppoInfo.setIcon(CxMateParams.getInstance().getMateIcon());
					iconOppoImg.displayImage(ImageLoader.getInstance(), 
							CxMateParams.getInstance().getMateIcon(), 
							CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, false, 0);
					CxGlobalParams.getInstance().setPartnerIconBig(CxMateParams.getInstance().getMateIcon());
					// 更新缓存
					//（暂时不需要存对方的头像如数据库，因为每次登录或者结对成功，对方的资料都会拉一次）				
				};
			}.sendEmptyMessageDelayed(1, 10);
			//目前只返回了大图 先这么做  (中小头像已经不需要了)
			
			
			return 0;
		}
	};
    
    JSONCaller familyInfoCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -1;
			}
			
			CxFamilyInfoData data=(CxFamilyInfoData)result;
			if(data.getRc()!=0){
				if(data.getRc()==408){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				}else if(TextUtils.isEmpty(data.getMsg())){
					showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				}else{
					showResponseToast(data.getMsg(),0);
				}
				return -2;
			}
			
			if(data.getMeInfo()==null || data.getOppoInfo()==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				return -3;
			}
			
			familyImgPath=data.getFamily_icon();
			meInfo=data.getMeInfo();
			oppoInfo=data.getOppoInfo();
			
			new Handler(getActivity().getMainLooper()){
				public void handleMessage(android.os.Message msg) {
					fillData();
				};
			}.sendEmptyMessageDelayed(1, 10);
			
			return 0;
		}
	};
	
	
	JSONCaller nameCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				resetData(1);
				return -1;
			}
			
			JSONObject nameObj=(JSONObject)result;
			String text=CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail);
			try {
				if(nameObj.isNull("rc") || nameObj.getInt("rc")!=0){
					int rc=nameObj.getInt("rc");
					String msg="";
					if(!nameObj.isNull("msg")){
						msg=nameObj.getString("msg");
					}
					if(rc==408){
						showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
						resetData(1);
						return -22;	
					} 
					
					if(!TextUtils.isEmpty(msg)){
						text=msg;
					}
					showResponseToast(text,0);
					
					resetData(1);
					return -2;
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}

			try {
				JSONObject jsonObject = nameObj.getJSONObject("data");
				if(jsonObject==null || jsonObject.isNull("name")){
					showResponseToast(text,0);
					resetData(1);
					return -3;
				}
				
				oppoInfo.setName(jsonObject.getString("name"));
				CxGlobalParams.getInstance().setPartnerName(jsonObject.getString("name"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return 0;
		}
	};
	
	JSONCaller oppoBirthCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				resetData(1);
				return -1;
			}
		
			JSONObject resultObj=(JSONObject)result;
			String text=CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail);
			try {
				if(resultObj.isNull("rc") || resultObj.getInt("rc")!=0){
					int rc=resultObj.getInt("rc");
					String msg="";
					if(!resultObj.isNull("msg")){
						msg=resultObj.getString("msg");
					}
					if(rc==408){
						showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
						resetData(1);
						return -22;	
					} 
					
					if(!TextUtils.isEmpty(msg)){
						text=msg;
					}
					showResponseToast(text,0);
					resetData(1);
					return -2;
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}

			try {
				JSONObject jsonObject = resultObj.getJSONObject("data");
				if(jsonObject==null || jsonObject.isNull("birth")){
					showResponseToast(text,0);
					resetData(1);
					return -3;
				}					
				oppoInfo.setBirth(jsonObject.getString("birth"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		

			return 0;
		}
	};
	
	JSONCaller meBirthCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				resetData(1);
				return -1;
			}
					
			CxUserProfile userProfile=(CxUserProfile)result;		
			if(userProfile.getData()==null || userProfile.getData().getBirth()==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail),0);
				resetData(1);
				return -4;
			}
			
			meInfo.setBirth(userProfile.getData().getBirth());
			return 0;
		}
	};
	
	
	JSONCaller oppoPhoneCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				resetData(1);
				return -1;
			}
		
			JSONObject resultObj=(JSONObject)result;
			String text=getString(R.string.cx_fa_net_response_code_fail);
			try {
				if(resultObj.isNull("rc") || resultObj.getInt("rc")!=0){
					int rc=resultObj.getInt("rc");
					String msg="";
					if(!resultObj.isNull("msg")){
						msg=resultObj.getString("msg");
					}
					if(rc==408){
						showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
						resetData(1);
						return -22;	
					} 
					
					if(!TextUtils.isEmpty(msg)){
						text=msg;
					}
					resetData(1);
					return -2;
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}

			try {
				JSONObject jsonObject = resultObj.getJSONObject("data");
				if(jsonObject==null || jsonObject.isNull("mobile")){
					showResponseToast(text,0);
					resetData(1);
					return -3;
				}
				
				oppoInfo.setNote(jsonObject.getString("mobile"));
				CxMateParams.getInstance().setmMateMobile(jsonObject.getString("mobile"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			return 0;
		}
	};
	
	JSONCaller mePhoneCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
				resetData(1);
				return -1;
			}
			
			CxUserProfile userProfile=(CxUserProfile)result;
			if(userProfile.getData()==null || userProfile.getData().getMobile()==null){
				showResponseToast(getString(R.string.cx_fa_net_response_code_fail),0);
				resetData(1);
				return -4;
			}
			
			meInfo.setMobile(userProfile.getData().getMobile());
			return 0;
		}
	};
	JSONCaller remarkCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(getActivity().getString(R.string.cx_fa_net_response_code_null),0);
				resetData(1);
				return -1;
			}
			
			JSONObject resultObj=(JSONObject)result;
			String text=CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail);
			try {
				if(resultObj.isNull("rc") || resultObj.getInt("rc")!=0){
					int rc=resultObj.getInt("rc");
					String msg="";
					if(!resultObj.isNull("msg")){
						msg=resultObj.getString("msg");
					}
					if(rc==408){
						showResponseToast(getActivity().getString(R.string.cx_fa_net_response_code_null),0);
						resetData(1);
						return -22;	
					} 
					
					if(!TextUtils.isEmpty(msg)){
						text=msg;
					}
					resetData(1);
					return -2;
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}

			try {
				JSONObject jsonObject = resultObj.getJSONObject("data");
				if(jsonObject==null || jsonObject.isNull("note")){
					showResponseToast(text,0);
					resetData(1);
					return -3;
				}
				
				oppoInfo.setNote(jsonObject.getString("note"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return 0;
		}
	};
	
	JSONCaller oppoCustomCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);
				resetData(2);
				return -1;
			}
			
			String str="";
//			if(customFrom==1){
//				str="增加"+customKey+"失败";
//			}else if(customFrom==2){
//				str="修改"+customKey+"失败";
//			}else{
//				str="删除"+customKey+"失败";
//			}
			
			str=CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail);

			JSONObject resultObj=(JSONObject)result;
			
			try {
				if(resultObj.isNull("rc") || resultObj.getInt("rc")!=0){
					showResponseToast(str,0);
				
					resetData(2);
				
					return -2;
				}
			} catch (JSONException e) {				
				e.printStackTrace();
			}

			try {
				JSONObject jsonObject = resultObj.getJSONObject("data");
				if(jsonObject==null || jsonObject.isNull("data")){
					showResponseToast(str,0);					
					resetData(2);		
					return -3;
				}		
				oppoInfo.setData(jsonObject.getString("data"));					
				resetData(2);			
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return 0;
		}
	};
	
	JSONCaller meCustomCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				showResponseToast(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null),0);				
				resetData(3);			
				return -1;
			}
			
			String str="";
//			if(customFrom==1){
//				str="增加"+customKey+"失败";
//			}else if(customFrom==2){
//				str="修改"+customKey+"失败";
//			}else{
//				str="删除"+customKey+"失败";
//			}
			
			str=CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail);
			CxUserProfile userProfile=(CxUserProfile)result;
			if(userProfile.getData()==null || userProfile.getData().getData()==null){
				showResponseToast(str,0);
				resetData(3);
				return -4;
			}
			
			meInfo.setData(userProfile.getData().getData());			
			resetData(3);
			
			return 0;
		}
	};
	
	/**
	 *  
	 * @param resetId  1 fillCommonData(); 2 fillOppoData(); 3 fillMeData();
	 */
	private void resetData(final int resetId){
		new Handler(getActivity().getMainLooper()) {
			public void handleMessage(Message msg) {
				if(resetId==1){
					fillCommonData();
				}else if(resetId==2){
					CxLog.i("CxFramilyInfoFragment_men", ">>>>>>>>>>>3");
					fillOppoData();
				}else{
					fillMeData();
				}
			};
		}.sendEmptyMessage(0);
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
    
    /**
     * 
     * @param title
     * @param str
     * @param from  1 增加自定义；2 修改 ； 3 删除
     */
    private void updateCustomItems(String title,String str,int from){
    	
    	customFrom=from;
    	
    	String data = "";
		if(flag==1){	
			for (int i = 0; i < oppoAddedTitles.size(); i++) {
		        String key = oppoAddedTitles.get(i);
		        String value = oppoTitleValues.get(key);
		        if(key.equals(title)){	        	
		        	value=str;
		        	customKey=title;
		        	customValue=value;
		        }
		        
		        data += key + ":" + value + "\n";
			} 	
			
			CxLog.i("men", str+">>>>>>>>>>"+data);
			
			try {
				CxMateProfileApi.getInstance().postMateProfileInfo(null, null, null, null, data, null, oppoCustomCaller);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			for (int i = 0; i < meAddedTitles.size(); i++) {
		        String key = meAddedTitles.get(i);
		        String value = meTitleValues.get(key);
		        if(key.equals(title)){
		        	value=str;
		        	customKey=title;
		        	customValue=value;
		        }
//		        if("".equals(value)){
//		        	value="null";
//		        }
		        data += key + ":" + value + "\n";
			} 
			try {
				CxLog.i("CxFamilyInfoFragment_men", data+">>>>>>>>>>1");
				UserApi.getInstance().updateUserProfile(null, null, null, null, null, null, data, meCustomCaller);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
    }
    
    
    
    
    
    
    
    
    
    
	
    /**
	 * 根据年月得到星座
	 * @param strMmdd
	 * @return
	 */
    int mSignIndex = -1;
	private CxImageView iconOppoImg;
	private CxImageView iconMeImg;
	private LinearLayout iconOppoLayout;
	private LinearLayout iconMeLayout;
	private LinearLayout oppoOtherLayout;
	private LinearLayout meOtherLayout;
	private int version_type;
    
	private String getSign(String strMmdd){
	    if (strMmdd.length() != 4 )
	        return "";
	    
	    mSignIndex = -1;

	    String arr[] = { getActivity().getString(R.string.cx_fa_nls_mateprofile_Sagittarius),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Capricorn),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Aquarius),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Pisces),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Aries),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Taurus),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Gemini),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Cance),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Leo),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Virgo),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Libra),
	    						 getActivity().getString(R.string.cx_fa_nls_mateprofile_Scorpio)};
	    
	   
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
	
	
	
}
