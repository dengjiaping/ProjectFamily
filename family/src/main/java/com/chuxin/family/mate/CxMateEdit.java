package com.chuxin.family.mate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.models.Reminder;
import com.chuxin.family.neighbour.CxNeighbourList;
import com.chuxin.family.net.ReminderApi;
import com.chuxin.family.net.CxMateProfileApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxMateProfile;
import com.chuxin.family.parse.been.data.CxMateProfileDataField;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.views.reminder.ReminderController;
import com.chuxin.family.views.reminder.ReminderCreateActivity;
import com.chuxin.family.views.reminder.ReminderDisplayUtility;
import com.chuxin.family.views.reminder.CxReminderList;
import com.chuxin.family.widgets.QuickMessage;
import com.chuxin.family.R;
import com.chuxin.family.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

enum  MateProfileAlertType {  
    MP_QUIT, MP_DELETE  
}  
/**
 * 
 * @author wentong.men
 *
 */
public class CxMateEdit extends CxRootActivity implements OnClickListener {
	LinearLayout itemLayout;

	private final String TAG = "RkMateEdit";
	private int index;
	private CxMateProfile mMateProfileData;
    private Button mReturnButton;
    private Button mSaveButton;
    private ImageButton mAddButton;
    private ImageButton mDelButton;
    
    private int totalRow;
    private int mDelRowIndex;
    private TextView tvDate;
    
    private PopupWindow mDatePickerDialog = null;
    
    private PopupWindow mAddDialog = null;
    private View mAddView = null;  
    private LinearLayout mBgItem1, mBgItem2, mBgItem3, mBgItem4, mBgItem5, 
    mBgItem6, mBgItem7, mBgItem8, mBgItem9, mBgItem10;
    private TextView mTextItem1, mTextItem2, mTextItem3, mTextItem4, 
    mTextItem5, mTextItem6, mTextItem7, mTextItem8, mTextItem9, mTextItem10;
    private ImageButton mBtnAddMore;
    
    private List<String> mAddedTitles = null;  
    
    private int requestCode;
    private Boolean mChanged;
   private LinearLayout naviLayout;
    private TextView naviTitle;
    private int mLastBirth;
    private ReminderController mController;
    
    private RelativeLayout navi_layout_top ;				// 最外层的顶部导航
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_mateprofile);

		// 把顶部的导航隐藏(聂超9.10做改动后，配置文件中出现了两个导航。要隐藏掉一个)
		navi_layout_top = (RelativeLayout)findViewById(R.id.cx_fa_mate_edit_navi_layout);
		navi_layout_top.setVisibility(View.GONE);
		
		itemLayout = (LinearLayout)findViewById(R.id.mateprofile_layout);
		naviLayout = (LinearLayout)findViewById(R.id.cx_fa_activity_title_layout);
		naviLayout.setVisibility(View.VISIBLE);
		
    	mReturnButton = (Button)findViewById(R.id.cx_fa_activity_title_back);		// 返回按钮
		mSaveButton = (Button)findViewById(R.id.cx_fa_activity_title_more);		// 保存按钮
		mSaveButton.setVisibility(View.VISIBLE);
		
		mReturnButton.setText(getString(R.string.cx_fa_navi_back));
		mSaveButton.setText(getString(R.string.cx_fa_navi_save));
		
		naviTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		naviTitle.setText(getString(R.string.cx_fa_nls_mateprofile_edit_title));
		
        mAddButton = (ImageButton)findViewById(R.id.cx_fa_edit_mate_profile__add);

        mAddButton.setVisibility(View.VISIBLE);
        
		mReturnButton.setOnClickListener(this);
		mSaveButton.setOnClickListener(this); 
		mAddButton.setOnClickListener(this);

		mChanged = false;
		mController = ReminderController.getInstance();
		loadData();
		
		// 防止进入页面就弹出输入法
//		mAddButton.setFocusable(true);
//		mAddButton.requestFocus();
	}	
	
	
	private void loadData()	{
		mMateProfileData = new CxMateProfile();
		CxMateProfileDataField dataField = new CxMateProfileDataField();
		dataField.setBirth(CxMateParams.getInstance().getMateBirth());
		dataField.setData(CxMateParams.getInstance().getMateData());
		dataField.setMobile(CxMateParams.getInstance().getMateMobile());
		dataField.setName(CxMateParams.getInstance().getMateName());
		dataField.setNote(CxMateParams.getInstance().getMateNote());

		mMateProfileData.setData(dataField);
		
		updateListview.sendEmptyMessage(1);
	}
	
	Handler updateListview = new Handler(){
		public void handleMessage(android.os.Message msg) {
			itemLayout.removeViews(0, itemLayout.getChildCount() );
			
			// 设为四个角全为圆角 (资料展示页和修改资料页共用一个Layout，展示页上部全是直角，而修改页四个角全是圆角)
			LinearLayout itemLayoutWrap = (LinearLayout) itemLayout.getParent();
			itemLayoutWrap.setBackgroundResource(drawable.cx_fa_round_corner_white_bg);
			
						
			String birth = mMateProfileData.getData().getBirth() + "";
			String disBirth = "";
			
			mLastBirth = mMateProfileData.getData().getBirth();
			
			if (!birth.equalsIgnoreCase("0")) {
				String yyyy = birth.substring(0,4);
				String mm = birth.substring(4,6);
				String dd = birth.substring(6,8);
				disBirth = yyyy + "-" + mm + "-" + dd; 
			}

			index = 0;
			
//			addView(index++, getString(R.string.cx_fa_nls_mateprofile_item_name), mMateProfileData.getData().getName(), false);
//			addView(index++, getString(R.string.cx_fa_nls_mateprofile_item_birth), disBirth, false);
//			addView(index++, getString(R.string.cx_fa_nls_mateprofile_item_mobile), mMateProfileData.getData().getMobile(), false);
			
			
			// 昵称
			EditText nicknameValue = (EditText)findViewById(R.id.mateprofile_nickname_value);
			nicknameValue.setText(CxMateParams.getInstance().getMateName());
			nicknameValue.setFocusable(true);
			nicknameValue.setHint(CxResourceString.getInstance().str_mate_mateprofile_name_hint);
			
			nicknameValue.setFilters(new  InputFilter[]{ new  InputFilter.LengthFilter(10)}); 			// 昵称, 设置它最长为10个字
			
		    nicknameValue.addTextChangedListener(new CustomTextWatcher(index++));
		    nicknameValue.setOnClickListener(CxMateEdit.this);	    
			
		    // 生日
		    EditText birthdayValue = (EditText)findViewById(R.id.mateprofile_birthday_value);
	    	birthdayValue.setVisibility(View.GONE);
	    	
		    tvDate = (TextView)findViewById(R.id.mateprofile_date_value);
		    tvDate.setVisibility(View.VISIBLE);
		    
		    if (disBirth == null || disBirth.length() == 0) {
		    	tvDate.setText(CxResourceString.getInstance().str_mate_mateprofile_birth_hint);
		    	tvDate.setTextColor(getResources().getColor(R.color.cx_fa_co_grey));
		    } else {
		    	tvDate.setText(disBirth);
		    	tvDate.setTextColor(getResources().getColor(R.color.cx_fa_co_near_black));
		    }
		    
		    tvDate.setOnClickListener(CxMateEdit.this);
		    tvDate.addTextChangedListener(new CustomTextWatcher(index++));
		  //  tvDate.setOnClickListener(RkMateEdit.this);	  
		    
		    // 将星座隐藏
		    LinearLayout signLayou 	= (LinearLayout)findViewById(R.id.mateprofile_item_sign_layout);		// 星座那一行
		    View bottomLine 				= (View)findViewById(R.id.mateprofile_item_sign_bottomLine);			// 底边横线
		    signLayou.setVisibility(View.GONE);
		    bottomLine.setVisibility(View.GONE);
		    
	    	// 手机号码
	    	EditText mobileValue = (EditText)findViewById(R.id.mateprofile_mobile_value);
	    	mobileValue.setText(CxMateParams.getInstance().getMateMobile());
	    	mobileValue.setFocusable(true);
	    	mobileValue.setHint(CxResourceString.getInstance().str_mate_mateprofile_mobile_hint);
	    	mobileValue.setInputType(InputType.TYPE_CLASS_PHONE);
	    	
	    	mobileValue.addTextChangedListener(new CustomTextWatcher(index++));
	    	mobileValue.setOnClickListener(CxMateEdit.this);	  
	    	
	    	// 备注
	    	EditText noteValue = (EditText)findViewById(R.id.mateprofile_note_value);
	    	noteValue.setText(CxMateParams.getInstance().getMateNote());
	    	noteValue.setFocusable(true);
	    	noteValue.setHint(R.string.cx_fa_mateprofile_note_hint);
			
	    	noteValue.addTextChangedListener(new CustomTextWatcher(index++));
	    	noteValue.setOnClickListener(CxMateEdit.this);	  
			
			
			// 加自定义字段
			if (mAddedTitles == null) {
				mAddedTitles = new ArrayList<String>();
			}
			
			mAddedTitles.clear();
			
			//扩展字段
			if(mMateProfileData.getData().getData()!=null){
				Iterator<String> iter = mMateProfileData.getData().getData().keySet().iterator();
				while (iter.hasNext()) { 
			        String key = (String)iter.next();
			        if (key == null || key.length() == 0) continue;
			        String value = mMateProfileData.getData().getData().get(key);
			        addView(index++, key, value, false);
			        
			        mAddedTitles.add(key);
				} 
			}

			totalRow = index;
			
		};
	};

	
	/**
	 * 增加自定义字段
	 * @param index
	 * @param title
	 * @param value
	 * @param isNote
	 */
	private void addView(int index, String title, String value, Boolean isNote){
			View tempView = this.getLayoutInflater().inflate(R.layout.cx_fa_activity_mateprofile_item, null);
			
			TextView tv = (TextView)tempView.findViewById(R.id.mateprofile_title);
		    tv.setText(title);

	    	mDelButton = (ImageButton)tempView.findViewById(R.id.cx_fa_edit_mate_profile__del);
	    	mDelButton.setVisibility(View.VISIBLE);

	    	mDelButton.setOnClickListener(new DelListener(index));
		    EditText tv1 = (EditText)tempView.findViewById(R.id.mateprofile_value);
		    tv1.setText(value);
		    
		    tv1.addTextChangedListener(new CustomTextWatcher(index));		// 昵称、生日、手机号、备注都已经写死了。自定义的字段从index=4开始
		    tv1.setOnClickListener(this);	    
		    
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(tv1.getWindowToken(),0) ;	
			
			itemLayout.addView(tempView, index-4);		// 昵称、生日、手机号、备注都已经写死了。自定义的字段从index=4开始
	}

	/**
	 * common start activity
	 * @param activityClass 
	 */
//	private void startActivity(final Class<?> activityClass) {
//		startActivity(new Intent(this, activityClass));
//		finish();
//		
//		if(activityClass == RkMain.class){
//		    overridePendingTransition(R.anim.cx_fa_anim_activity_enter_right_in, R.anim.cx_fa_anim_activity_enter_right_out);
//		} else {
//		    overridePendingTransition(R.anim.cx_fa_anim_activity_enter_left_in, R.anim.cx_fa_anim_activity_enter_left_out);
//		}
//	}	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.cx_fa_activity_title_back:
				if (mChanged)
					dialog(MateProfileAlertType.MP_QUIT);
				else
//					startActivity(RkMainActivity.class);
				    CxMateEdit.this.finish();
					overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				break;
			case R.id.cx_fa_activity_title_more:			// 保存
				try {
					v.setEnabled(false);
					saveMateProfile();
				} catch (Exception e) {
					e.printStackTrace();
					v.setEnabled(true);
				}
				break;
			
			case R.id.cx_fa_edit_mate_profile__add:
				showAddDialog();
				break;
				
			case R.id.mateprofile_date_value:
				showDatePickerDialog(v);
				break;

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
				
				//判断是否已添加过
				for(int i = 0; i < mAddedTitles.size(); i++) {   
				    if (title.equalsIgnoreCase(mAddedTitles.get(i))) {
				    	return;
				    }
				}
								
    			mAddedTitles.add(title);
    			mMateProfileData.getData().getData().put(title, "");
    			
    			addView(totalRow, title, "", false);
    			totalRow++;
    			mChanged = true;
				break;
				
			//add more
			case R.id.mateprofile_add_more_button:
				Intent mIntent = new Intent();  
			    mIntent.setClass(this, CxMateAddCustom.class);  
			    requestCode = 0;
				startActivityForResult(mIntent, requestCode); 
				overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				break;
				
			default:
				break;
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
	
	//删除按钮专用 
	private class DelListener implements OnClickListener {
	    private int mIndex;

	    public DelListener(int mIndex) {
	    	this.mIndex = mIndex;
	    }

	    public void onClick(View v) {
	    	mDelRowIndex = mIndex;
	    	dialog(MateProfileAlertType.MP_DELETE);
	    }
	}
	
	//
	private class CustomTextWatcher implements TextWatcher {
	    private int mIndex;

	    public CustomTextWatcher(int mIndex) {
	    	this.mIndex = mIndex;
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    }

	    public void afterTextChanged(Editable s) {
	    	mChanged = true;

	    	//禁止输入回车
            for(int i = s.length(); i > 0; i--){
                if(s.subSequence(i-1, i).toString().equals("\n"))
                     s.replace(i-1, i, "");
            }
	    	
	    	String text = s.toString();
	    	if (mIndex == 0) {  //昵称
	    		mMateProfileData.getData().setName(text);
	    	} else if (mIndex == 1) {  //生日
	    		if(text.length()>=10){
					String yyyy = text.substring(0,4);
					String mm = text.substring(5,7);
					String dd = text.substring(8,10);
					String birth = yyyy + mm + dd; 
		    		mMateProfileData.getData().setBirth(Integer.valueOf(birth));
	    		}
	    	} else if (mIndex == 2) {  //手机
	    		mMateProfileData.getData().setMobile(text);
	    	} else {  //备注 或 扩展字段
	    		if (mIndex == 3) { //备注
	    			mMateProfileData.getData().setNote(text);
	    		} else { //扩展字段
	    			int range = mIndex - 4;
	    			String key = mAddedTitles.get(range);
	    			
	    			mMateProfileData.getData().getData().put(key, text);	    				
	    		}
	    	}
	    }	
	}
	
	private void saveMateProfile() {
		try {
			CxLoadingUtil.getInstance().showLoading(CxMateEdit.this, true);
			//data, hashmap -> string
			String data = "";

			for (int i = 0; i < mAddedTitles.size(); i++) {
		        String key = mAddedTitles.get(i);
		        String value = mMateProfileData.getData().getData().get(key);
		        data += key + ":" + value + "\n";
			} 
			
			CxMateProfileApi.getInstance().postMateProfileInfo(mMateProfileData.getData().getName(),
					mMateProfileData.getData().getBirth()+"",
					mMateProfileData.getData().getMobile(),
					mMateProfileData.getData().getNote(),
					data,
					"",
					callback);
		} catch (Exception e) {
			CxLoadingUtil.getInstance().dismissLoading();
			e.printStackTrace();
		}
	}
	
	JSONCaller callback = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			mSaveButton.setEnabled(true);
			CxLoadingUtil.getInstance().dismissLoading();
			if (null == result) {
				showSaveFail();
				
				return -1;
			}
			CxMateProfile mateProfileData = null;
			try {
				mateProfileData = (CxMateProfile)result;
			} catch (Exception e) {
				showSaveFail();
				
				CxLog.e("sace mate profile", ""+e.getMessage());
			}
			if (null == mateProfileData) {
				showSaveFail();
				
				return -2;
			}
			int rc = mateProfileData.getRc();
			
			if (0 != rc) {
				showSaveFail();
				return 1;
			}
			
			mMateProfileData = mateProfileData;
			
			CxMateParams.getInstance().setMateData(mateProfileData.getData().getData());
			CxMateParams.getInstance().setMateIcon(mateProfileData.getData().getIcon());
			CxMateParams.getInstance().setmMateBirth(mateProfileData.getData().getBirth());
			CxMateParams.getInstance().setmMateMobile(mateProfileData.getData().getMobile());
			CxMateParams.getInstance().setmMateName(mateProfileData.getData().getName());
			CxMateParams.getInstance().setmMateNote(mateProfileData.getData().getNote());
			
			//add for update by shichao
			CxGlobalParams.getInstance().setPartnerName(mateProfileData.getData().getName());
			CxGlobalParams.getInstance().setPartnerIconBig(mateProfileData.getData().getIcon());
			
			if (mLastBirth != mateProfileData.getData().getBirth()) {
				Calendar calendar = Calendar.getInstance();
				String str = mateProfileData.getData().getBirth() + "";
				
				String yyyy = str.substring(0,4);
				String mm = str.substring(4,6);
				String dd = str.substring(6,8);
		
				calendar.set(Calendar.YEAR, Integer.valueOf(yyyy).intValue());
				calendar.set(Calendar.MONDAY, (Integer.parseInt(mm)-1));
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
				calendar.set(Calendar.HOUR_OF_DAY, 10);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				long timeToSet = calendar.getTime().getTime();
				
				createBirthdayReminder(timeToSet);
			}

			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			//startActivity(RkMainActivity.class); 
			return 0;
		}
	};
	
	private void showSaveFail(){
		new Handler(getMainLooper()){
			public void handleMessage(android.os.Message msg) {
				ToastUtil.getSimpleToast(CxMateEdit.this, -1, getString(R.string.cx_fa_save_fail), 0).show();
				mSaveButton.setEnabled(true);
			};
		}.sendEmptyMessage(1);
	}
	
	/**
	 * show date dialog at the bottom
	 */
	private void showDatePickerDialog(View v) {

		try {
			InputMethodManager imm = (InputMethodManager)getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DatePicker datePicker = new DatePicker(CxMateEdit.this);
		if (mDatePickerDialog == null) {
			long time = System.currentTimeMillis();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));
			
			// 判断以前是否设置过生日，如果设置过，此次就显示上次设置的日期
			TextView tv = (TextView)findViewById(R.id.mateprofile_date_value);
			String birth = tv.getText().toString();
			if(birth.length()==10 && birth.indexOf("-")==4){
				String yyyy = birth.substring(0,4);
				String mm = birth.substring(5,7);
				String dd = birth.substring(8,10);
				
				// 月份等于中国的月份-1, 所以此处要做判断
				int mmInt = Integer.parseInt(mm);
				mmInt--;
				if(mmInt==-1){
					mmInt = 12;
				}
				
				calendar.set(Calendar.YEAR, Integer.parseInt(yyyy));
				calendar.set(Calendar.MONTH, mmInt);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
			}

			mDatePickerDialog = new PopupWindow(datePicker,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mDatePickerDialog.setWidth(LayoutParams.MATCH_PARENT);
			mDatePickerDialog.setHeight(LayoutParams.WRAP_CONTENT);
			mDatePickerDialog.setBackgroundDrawable(getResources().getDrawable(
					R.color.cx_fa_co_grey));
			mDatePickerDialog.setTouchable(true);
			mDatePickerDialog.setOutsideTouchable(true);

			datePicker.init(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					new OnDateChangedListener() {

						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							long time = System.currentTimeMillis();
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(new Date(time));

							calendar.set(Calendar.YEAR, year);
							calendar.set(Calendar.MONDAY, monthOfYear);
							calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

							long time1 = calendar.getTime().getTime();
							
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							Timestamp now = new Timestamp(time1); 
							String str = df.format(now); 
							tvDate.setText(str);
							
							String yyyy = str.substring(0,4);
							String mm = str.substring(5,7);
							String dd = str.substring(8,10);
							String birth = yyyy + mm + dd; 
				    		mMateProfileData.getData().setBirth(Integer.valueOf(birth));
							mChanged = true;
						}
					});

		} else {
			if (mDatePickerDialog.isShowing()) {
				mDatePickerDialog.dismiss();
				return;
			}
		}
		mDatePickerDialog.showAtLocation(datePicker, Gravity.BOTTOM, 0, 0);
	}	
	
	//弹出 在底部显示的 增加备忘项面板
	private void showAddDialog() {
		if (mAddView == null) {
			mAddView = getLayoutInflater().inflate(R.layout.cx_fa_activity_mateprofile_add, null);  
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
			mBtnAddMore.setOnClickListener(this);			
			
			mBgItem1.setOnClickListener(this);
			mBgItem2.setOnClickListener(this);
			mBgItem3.setOnClickListener(this);
			mBgItem4.setOnClickListener(this);
			mBgItem5.setOnClickListener(this);
			mBgItem6.setOnClickListener(this);
			mBgItem7.setOnClickListener(this);
			mBgItem8.setOnClickListener(this);
			mBgItem9.setOnClickListener(this);
			mBgItem10.setOnClickListener(this);			
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

	//提示框 用于 退出编辑， 删除资料项
	private void dialog(final MateProfileAlertType alertType) {
		String tipText;
		if (alertType == MateProfileAlertType.MP_QUIT)
			tipText = this.getString(R.string.cx_fa_quit_comfirm_text);
		else
			tipText = this.getString(R.string.cx_fa_delete_comfirm_text);
		
		
		DialogUtil du = DialogUtil.getInstance();
		
		du.setOnSureClickListener(new OnSureClickListener() {
			
			@Override
			public void surePress() {
				switch (alertType) { 
				case MP_QUIT:
//					startActivity(RkMainActivity.class);
				    finish();
				    overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
					break;
				case MP_DELETE:
					delItem();
					mChanged = true;
					break;
				}
				
			}
		});
		
		du.getSimpleDialog(this, null, tipText, null, null).show();
		
//		AlertDialog comfirmDialog = new AlertDialog.Builder(this)
//		.setNegativeButton(this.getString(R.string.cx_fa_confirm_text), 
//				new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch (alertType) { 
//					case MP_QUIT:
////						startActivity(RkMainActivity.class);
//					    finish();
//						break;
//					case MP_DELETE:
//						delItem();
//						mChanged = true;
//						break;
//				}
//			}
//		})
//		.setPositiveButton(this.getString(R.string.cx_fa_cancel_button_text), null)
//		.setTitle(this.getString(R.string.cx_fa_tip_text))
//		.setMessage(tipText)
//		.create();
//		
//		comfirmDialog.setCancelable(true);
//		comfirmDialog.setIcon(this.getResources()
//				.getDrawable(android.R.color.transparent));
//		
//		comfirmDialog.show();			
	}

	private void delItem() {
		int range = mDelRowIndex - 4;		// 昵称、生日、手机号、备注都已经写死了。自定义的字段从index=4开始
		if (mAddedTitles.size() == 0) return;

		String key = mAddedTitles.get(range);
//		RkLog.e("", "long delItem--- range:" + range + " key:" +  key + " title_list_size:" + mAddedTitles.size() + " hashmap_size:" + mMateProfileData.getData().getData().size());
	
		mMateProfileData.getData().getData().remove(key);
		
		mAddedTitles.remove(key);
		updateListview.sendEmptyMessage(1);
	}
	
    // 回调方法，从第二个页面回来的时候会执行这个方法  
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	mAddDialog.dismiss();
    	if (data == null) return;
    	String customTitle = data.getStringExtra("customTitle");  
    	
		mAddedTitles.add(customTitle);
		mMateProfileData.getData().getData().put(customTitle, "");
		addView(totalRow, customTitle, "", false);
		totalRow++;
		mChanged = true;
    }
	/**
	 * save the reminder
	 */
	private void saveReminder() {
		
		try {
			// check if the user-set time valid;
				CxLog.d(TAG, "save privious time=" + ReminderDisplayUtility.getDate(mController.getRealTime()));
				mController.submitReminderChanges(CxMateEdit.this, new JSONCaller() {

					@Override
					public int call(Object result) {
						try {
							JSONObject reminderObj = (JSONObject) result;
							reminderObj.put(Reminder.TAG_DELAY, false);
							Reminder reminder = new Reminder(null, CxMateEdit.this);
							reminder.mData = reminderObj;
							reminder.mId = reminder.getReminderId();
							CxLog.d(TAG, "after request api time=" + ReminderDisplayUtility.getDate((long)reminder.getBaseTimestamp()*1000));
							CxLog.v(TAG, "local remindId: " + reminder.mId);
							CxLog.v(TAG,
									"local remind get flag: "
											+ reminder.getFlag());
							if (reminder.adjust() != -1) {
								reminder.setFlag(reminder.getBaseTimestamp());
								reminder.put();
								CxLog.d(TAG, "after adjust time=" + ReminderDisplayUtility.getDate((long)reminder.getBaseTimestamp()*1000));
								SharedPreferences reminderBirthdaySf = getSharedPreferences(CxGlobalConst.S_BIRTHDAY_REMINDER_PREFS_NAME, 0);
								reminderBirthdaySf.edit().putString(CxGlobalConst.S_BIRTHDAY_REMINDER_ID, reminder.getId()).commit();
								reminderBirthdaySf.edit().putInt(CxGlobalConst.S_BIRTHDAY_REMINDER_FLAG, reminder.getFlag()).commit();
//								Toast.makeText(RkMateEdit.this, "保存生日提醒成功", Toast.LENGTH_LONG).show();
							} else {
								reminder.drop();
							}

						} catch (Exception e) {
							CxLog.e(TAG,
									"Error: failed to get object from create reminder result" + e.toString());
							e.printStackTrace();
						}
						return 0;
					}

				});
		} catch (Exception e) {
			CxLog.e(TAG,
					"Failed to submitReminderChanges due to " + e.toString());
			e.printStackTrace();
		}
	}

	private void delReminder(final String reminderId, final int birthdayflag, final long time) {
	      ReminderApi.getInstance().doDeleteReminder(reminderId,
	              new JSONCaller() {

	                  @Override
	                  public int call(Object result) {
	                      Reminder reminder = (Reminder)new Reminder(null, CxMateEdit.this).get(reminderId);
	                      ReminderController.getInstance().cancelAlarmReminder(
	                              CxMateEdit.this, birthdayflag);
	                      if(null != reminder){
	                          reminder.drop(reminderId);
	                      }
//	                      Toast.makeText(
//	                              RkMateEdit.this,
//	                              "delete " + reminderId + " returns "
//	                                      + result.toString(), Toast.LENGTH_SHORT)
//	                              .show();
	                      createReminder(time);
	                      return 0;
	                  }

	              });
	}

	public void createBirthdayReminder(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String date = dateFormat.format(new Date(time));
		SharedPreferences sf = getSharedPreferences(CxGlobalConst.S_BIRTHDAY_REMINDER_PREFS_NAME, 0);
		String birthdayReminderId = sf.getString(CxGlobalConst.S_BIRTHDAY_REMINDER_ID, null);
		int birthdayReminderFlag = sf.getInt(CxGlobalConst.S_BIRTHDAY_REMINDER_FLAG, 0);
		if(null != birthdayReminderId && birthdayReminderId.length() > 0){
//			dropBirthdayReminder(birthdayReminderId, birthdayReminderFlag);
			delReminder(birthdayReminderId, birthdayReminderFlag, time);
		} else {
		    createReminder(time);
		}
	}
	
	private void createReminder(long time){
        mController.reset();
        mController.setTime(time);
        mController.setTarget(ReminderController.sReminderTargetMyself);
        mController.setPeriod(ReminderController.sReminderPeriodAnnually);
        mController.setAdvance(ReminderController.sReminderAdvance3Day);
        mController.setTitle(getResources().getString(CxResourceString.getInstance().str_reminder_birthday_tip));
        saveReminder();
	}
	
	//by wentong.men 131109
	
	  public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
			if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				finish();
				this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				return false;
			}
			return super.onKeyDown(keyCode, event);
		};
	
}
