
package com.chuxin.family.calendar;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.net.CalendarApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.utils.DateUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.DatePicker;
import com.chuxin.family.widgets.DatePicker.OnLunarImgClickListener;
import com.chuxin.family.widgets.OnDateChangeListener;
import com.chuxin.family.widgets.QuickMessage;
import com.chuxin.family.widgets.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxCalendarEditActivity extends CxRootActivity {

    public enum EditMode {
        ADD_MODE, UPDATE_MODE
    }

    private final String TAG = "RkCalendarEditActivity";

    protected static final int COMMON_MEMORIAL = 0;

    private PopupWindow mLunarDatePickerDialog = null;
    
    private PopupWindow mDatePickerDialog = null;

    private CalendarController mController = null;

    private EditMode mode; // 判断是新添模式还是编辑模式

    private int type = 0; // 判断是事项还是纪念日 这两项判断需要先执行已进行不同的UI展示 0 事项 1 纪念日

    private int memorialType = 2; // 判断是一般纪念日还是生日 2 一般纪念日 1 生日

    private TextView titleText;

    private TextView mReminderDateText;

    private ImageView mReminderSettingImg;

    private LinearLayout mReminderShowLayout;

    private TextView mReminderTargetText;

    private TextView mReminderTimeText;

    private TextView mReminderCycleText;

    private TextView mReminderAdvanceText;

    private LinearLayout mReminderDateLayout;

    private LinearLayout mMemorialTypeLayout;

    private LinearLayout mReminderTimeLayout;

    private LinearLayout mReminderCycleLayout;

    private LinearLayout mTabItemLayout;

    private TextView mTabItemText;

    private LinearLayout mTabMemorialLayout;

    private TextView mTabMemorialText;

    private EditText mContentEdit;

    private TextView mMemorialTypeText;

    private int itemReminderFlag = 0; // 0,不提醒；1,提醒。

    private String itemDate; // yyyy-MM-dd
    
    private String itemTempDate; // yyyy-MM-dd 

    private int itemTarget = 2; // 显示对象，0:自己 1:对方 2:双方

    private String itemTime = "10:00"; // HH-mm;

    private int itemCycle = 0; // 循环周期类型，0：不循环，1：按天，2：按周，3：按月，4：按年
    
    private int itemTempCycle = 0; // 循环周期类型，0：不循环，1：按天，2：按周，3：按月，4：按年

    private int itemAdvance = 0; // 提前提醒类型，0：不提前，1：15分钟，2：1小时，3：1天，4：3天 ，5：5天

    private long itemFullTime = 0;

    private int memorialReminderFlag = 1;

    private String memorialDate;

    private int memorialTarget = 2;

    private String memorialTime = "10:00"; // 默认十点

    private int memorialCycle = 4; // 默认按年

    private int memorialAdvance = 0;

    private long memorialFullTime = 0;

    private int memorialLunar = 0; // 0,公历；1 农历。

    private String mCalendarId = "";

    // private long fullTime=0;
    private Button mDeleteBtn;

    private String[] targets = {
            "只给自己看", "给对方看", "给双方看"
    };

    private String[] cycles = {
            "提醒一次", "每天一次", "每周一次", "每月一次", "每年一次"
    };

    private String[] advances = {
            "不提前提醒", "提前15分钟", "提前1小时", "提前1天", "提前3天", "提前5天"
    };

    private String[] memorialTypes = {
            "生日", "一般纪念日"
    };

    private String[] dateWeeks = {
            "周一", "周二", "周三", "周四", "周五", "周六", "周日"
    };

    private TextView mCommonText;

    private Dialog dialog;

    private PopupWindow mTimePickerDialog;

    private PopupWindow mDatePickerDialogWithMonthFixed;

    private PopupWindow mDatePickerDialogWithAnnuallyFixed;

    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.cx_fa_activity_calendar_edit);

        // itemFullTime = memorialFullTime = System.currentTimeMillis();

        Locale locale = Locale.getDefault();
        Calendar today = Calendar.getInstance(locale);
        itemFullTime = today.getTimeInMillis();

        Calendar cal = Calendar.getInstance(locale);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        memorialFullTime = cal.getTimeInMillis();
        SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
        String[] times = nowFormat.format(itemFullTime).split(",");
        itemDate = memorialDate = times[0];
        itemTime = times[1];

        Intent intent = getIntent();
        int modeExtra = intent.getIntExtra(CxCalendarParam.CALENDAR_EDIT_MODE, 0);
        int typeExtra = intent.getIntExtra(CxCalendarParam.CALENDAR_EDIT_TYPE, 0);
        dateExtra = intent.getStringExtra(CxCalendarParam.CALENDAR_EDIT_DATE);
        if (modeExtra == CxCalendarParam.CALENDAR_EDIT_ADD) {
            mode = EditMode.ADD_MODE;
        } else {
            mode = EditMode.UPDATE_MODE;
        }

        if (typeExtra == CxCalendarParam.CALENDAR_TYPE_ITEM) {
            type = 0;
        } else {
            type = 1;
        }

        if (modeExtra == CxCalendarParam.CALENDAR_EDIT_UPDATE
                && typeExtra == CxCalendarParam.CALENDAR_TYPE_MEMORIAL) {
            int intExtra = intent.getIntExtra(CxCalendarParam.CALENDAR_MEMORIAL_TYPE, 0);
            if (intExtra == CxCalendarParam.CALENDAR_MEMORIAL_BIRTHDAY) {
                memorialType = 1;
            } else {
                memorialType = 2;
            }
        }
        
        
        
        initTitle();
        init();

        setEditState(); // 设置UI的各个参数展示

        // fillData();
    }

    private void setEditState() {
        // if(null == mController.getId() ||
        // TextUtils.isEmpty(mController.getId())){
        // mode = EditMode.ADD_MODE;
        // } else {
        // mode = EditMode.UPDATE_MODE;
        // }
        if (mode == EditMode.ADD_MODE) {
            add();
        } else {
            update();
        }
    }

    // 添加模式
    private void add() {
        mDeleteBtn.setVisibility(View.GONE);
        
        if(!TextUtils.isEmpty(dateExtra)){
        	Calendar instance = Calendar.getInstance();
        	instance.set(Calendar.YEAR, Integer.parseInt(dateExtra.substring(0, 4)));
        	instance.set(Calendar.MONTH, Integer.parseInt(dateExtra.substring(4, 6))-1);
        	instance.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateExtra.substring(6, 8)));
        	itemFullTime=instance.getTimeInMillis();
        	
        	instance.set(Calendar.HOUR_OF_DAY, 10);
        	instance.set(Calendar.MINUTE, 0);
        	instance.set(Calendar.SECOND, 0);
        	memorialFullTime = instance.getTimeInMillis();
        	
        	SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
        	String[] times = nowFormat.format(itemFullTime).split(",");
        	itemDate = memorialDate = times[0];
        	itemTime = times[1];
        }
        
        
        if (type == 0) {
            addItem();
        } else {
            addMemorial();
        }
    }

    // 添加事项
    private void addItem() {
        titleText.setText(R.string.cx_fa_calendar_edit_title_text1);// 标题
        itemTab();// UI状态切换

        setItemReminderShow(); // 提醒UI显示
        setRemainderData(); // 提醒数据显示
        
        CxLog.i("RkCalendarEidtAcitivity_men", "itemReminderFlag:"+itemReminderFlag+",itemDate:"+itemDate
        		+",itemTime:"+itemTime+",itemCycle:"+itemCycle+",itemAdvance:"+itemAdvance
        		+",mCalendarId:"+"null");
    }

    // 添加纪念日
    private void addMemorial() {
        titleText.setText(R.string.cx_fa_calendar_edit_title_text2);
        memorialTab();
        setMemorialReminderShow();
        setRemainderData();
        
        CxLog.i("RkCalendarEidtAcitivity_men", "memorialReminderFlag:"+memorialReminderFlag+",memorialDate:"+memorialDate
        		+",memorialTarget:"+memorialTarget+",memorialAdvance:"+memorialAdvance+",memorialLunar:"+memorialLunar
        		+",mCalendarId:"+"null"+",memorialType:"+memorialType);

    }

    // 修改模式
    private void update() {
        type = mController.getType();
        mDeleteBtn.setVisibility(View.VISIBLE);
        // mController.reset();
        if(!TextUtils.isEmpty(mController.getContent())){
	        mContentEdit.setText(mController.getContent());
	        mContentEdit.setSelection(mController.getContent().length());
        }
        if (type == 0) {
            updateItem();
        } else {
            updateMemorial();
        }
    }

    private void updateItem() {
        titleText.setText(R.string.cx_fa_calendar_edit_title_text3);
        itemTab();
        // 根据intent传过来的参数进行赋值。

        itemReminderFlag = mController.getIsRemind();
        itemFullTime = (long)mController.getTime() * 1000;
        // RkLog.i("RkCalendarEditActivity_men",
        // ">>>>>>>>>" + nowFormat.format(new Date((long)(itemFullTime*1000))));
        SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
        String[] times = nowFormat.format(itemFullTime).split(",");
        itemDate = times[0];
        itemTime = times[1];
        
        itemTarget=mController.getTarget();
        itemCycle = mController.getPeriod();
        itemAdvance = mController.getAdvance();
        mCalendarId = mController.getId();
        setItemReminderShow();
        setRemainderData();
        
        CxLog.i("RkCalendarEidtAcitivity_men", "itemReminderFlag:"+itemReminderFlag+",itemDate:"+itemDate
        		+",itemTime:"+itemTime+",itemCycle:"+itemCycle+",itemAdvance:"+itemAdvance
        		+",mCalendarId:"+mCalendarId);

    }

    private void updateMemorial() {
        titleText.setText(R.string.cx_fa_calendar_edit_title_text4);
        memorialTab();

        // 根据intent传过来的参数进行赋值。

        memorialReminderFlag = mController.getIsRemind();
        memorialFullTime = (long)(mController.getTime() * 1000);
        SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
        String[] times = nowFormat.format(memorialFullTime).split(",");
        memorialDate = times[0];
        memorialTarget = mController.getTarget();
        memorialAdvance = mController.getAdvance();
        memorialLunar=mController.getIsLunar();
        mCalendarId = mController.getId();
        setMemorialReminderShow();
        setRemainderData();
        
        CxLog.i("RkCalendarEidtAcitivity_men", "memorialReminderFlag:"+memorialReminderFlag+",memorialDate:"+memorialDate
        		+",memorialTarget:"+memorialTarget+",memorialAdvance:"+memorialAdvance+",memorialLunar:"+memorialLunar
        		+",mCalendarId:"+mCalendarId+",memorialType:"+memorialType);
    }

    private void setItemReminderShow() {
//        if (mode == EditMode.UPDATE_MODE) {
//            mContentEdit.setHint(mController.getContent());
//        }
        if (itemReminderFlag == 1) {
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_on);
            mReminderTimeLayout.setVisibility(View.VISIBLE);
            mReminderShowLayout.setVisibility(View.VISIBLE);
        } else {
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_off);
            mReminderTimeLayout.setVisibility(View.GONE);
            mReminderShowLayout.setVisibility(View.GONE);
        }
    }

    private void setRemainderData() {
        if (type == 0) {
            mReminderDateText.setText(itemDate);
            mReminderTargetText.setText(targets[itemTarget]);
            mReminderTimeText.setText(itemTime);
            mReminderCycleText.setText(cycles[itemCycle]);
            mReminderAdvanceText.setText(advances[itemAdvance]);
        } else {
            mMemorialTypeText.setText(memorialTypes[memorialType - 1]);
            mReminderDateText.setText(memorialDate);
            mReminderTargetText.setText(targets[memorialTarget]);
            mReminderAdvanceText.setText(advances[memorialAdvance]);
        }
    }

    private void setMemorialReminderShow() {
//        if (mode == EditMode.UPDATE_MODE) {
//            mContentEdit.setHint(mController.getContent());
//        }
        if (memorialReminderFlag == 1) {
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_on);
            mReminderShowLayout.setVisibility(View.VISIBLE);
        } else {
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_off);
            mReminderShowLayout.setVisibility(View.GONE);
        }
    }

    private void setReminderDateType() {

        if (type == 0) {
            mReminderDateLayout.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(itemFullTime);
  
            Calendar c1 = Calendar.getInstance();
        	c1.set(Calendar.HOUR, c.get(Calendar.HOUR));
        	c1.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        	c1.set(Calendar.SECOND, c.get(Calendar.SECOND));
            
            CxLog.i("RkCalendarEditActivity_men",
                    ">>>>>>>>>" + c.get(Calendar.YEAR) + ":" + c.get(Calendar.MONTH) + ":"
                            + c.get(Calendar.DAY_OF_MONTH) + ":" + c.get(Calendar.DAY_OF_WEEK));

            SimpleDateFormat nowFormat = null;
            if (itemCycle == 0) {
                nowFormat = new SimpleDateFormat("yyyy-MM-dd");
                itemDate = nowFormat.format(new Date(itemFullTime));
            } else if (itemCycle == 1) {          	
            	itemFullTime=c1.getTimeInMillis();
                mReminderDateLayout.setVisibility(View.GONE);
            } else if (itemCycle == 2) {
            	itemFullTime=c1.getTimeInMillis();
                itemDate = DateUtil.getCatipalNumber(c1.get(Calendar.DAY_OF_WEEK));
            } else if (itemCycle == 3) {
            	c1.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
            	itemFullTime=c1.getTimeInMillis();
                itemDate = String.format(getResources().getString(R.string.cx_fa_nls_reminder_everymonth_format),
                        c1.get(Calendar.DAY_OF_MONTH));
            } else {
            	c1.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
            	c1.set(Calendar.MONTH, c.get(Calendar.MONTH));
            	itemFullTime=c1.getTimeInMillis();
                nowFormat = new SimpleDateFormat("MM-dd");
                itemDate = nowFormat.format(new Date(itemFullTime));
            }
            mReminderDateText.setText(itemDate);
        } else {
            mReminderDateText.setText(memorialDate);
        }
    }

    // 获取布局文件参数
    private void init() {
        mController = CalendarController.getInstance();
        mTabItemLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_tab_item_layout);
        mTabItemText = (TextView)findViewById(R.id.cx_fa_calendar_edit_tab_item_tv);
        mTabMemorialLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_tab_memorial_layout);
        mTabMemorialText = (TextView)findViewById(R.id.cx_fa_calendar_edit_tab_memorial_tv);

        mContentEdit = (EditText)findViewById(R.id.cx_fa_calendar_edit_content_et);

        mCommonText = (TextView)findViewById(R.id.cx_fa_calendar_edit_content_recommend_tv);

        mMemorialTypeLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_memorial_day_selecter_layout);
        mMemorialTypeText = (TextView)findViewById(R.id.cx_fa_calendar_edit_memorial_day_selecter_tv);

        mReminderDateLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_date_layout);
        mReminderDateText = (TextView)findViewById(R.id.cx_fa_calendar_edit_reminder_date_tv);
        LinearLayout mReminderTargetLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_target_layout);
        mReminderTargetText = (TextView)findViewById(R.id.cx_fa_calendar_edit_reminder_target_tv);
        mReminderTimeLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_time_layout);
        mReminderTimeText = (TextView)findViewById(R.id.cx_fa_calendar_edit_reminder_time_tv);
        mReminderCycleLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_cycle_layout);
        mReminderCycleText = (TextView)findViewById(R.id.cx_fa_calendar_edit_reminder_cycle_tv);
        LinearLayout mReminderAdvanceLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_advance_layout);
        mReminderAdvanceText = (TextView)findViewById(R.id.cx_fa_calendar_edit_reminder_advance_tv);

        mReminderShowLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_is_show_layout);
        LinearLayout mReminderSettingLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_edit_reminder_setting_layout);
        mReminderSettingImg = (ImageView)findViewById(R.id.cx_fa_calendar_edit_reminder_setting_iv);
        mDeleteBtn = (Button)findViewById(R.id.cx_fa_calendar_edit_delete_btn);

        mTabItemLayout.setOnClickListener(contentListener);
        mTabMemorialLayout.setOnClickListener(contentListener);
        mCommonText.setOnClickListener(contentListener);
        mMemorialTypeLayout.setOnClickListener(contentListener);
        mReminderDateLayout.setOnClickListener(reminderListener);
        mReminderTargetLayout.setOnClickListener(reminderListener);
        mReminderTimeLayout.setOnClickListener(reminderListener);
        mReminderCycleLayout.setOnClickListener(reminderListener);
        mReminderAdvanceLayout.setOnClickListener(reminderListener);
        mReminderSettingLayout.setOnClickListener(reminderListener);
        mDeleteBtn.setOnClickListener(reminderListener);
        
        mContentEdit.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
//                RkLog.v(TAG, "come afterTextChanged");
//                if(s.length()>0){
//                    mSaveButton.setClickable(true);
//                    mSaveButton.setTextColor(getResources().getColor(R.color.cx_fa_co_navi_button_text));
//                    mSaveButton.setText(getString(R.string.cx_fa_calendar_edit_title_save_text));
//                }else{
//                    mSaveButton.setClickable(false);
//                    mSaveButton.setTextColor(getResources().getColor(R.color.cx_fa_co_grey));
//                    mSaveButton.setText(getString(R.string.cx_fa_calendar_edit_title_save_text));
//                }
            }
        });

    }



    // 初始化标题栏
    private void initTitle() {
        Button backBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        mSaveButton = (Button)findViewById(R.id.cx_fa_activity_title_more);
        titleText = (TextView)findViewById(R.id.cx_fa_activity_title_info);

        backBtn.setText(getString(R.string.cx_fa_navi_back));
        mSaveButton.setTextColor(getResources().getColor(R.color.cx_fa_co_navi_button_text));
        mSaveButton.setText(getString(R.string.cx_fa_calendar_edit_title_save_text));
        mSaveButton.setVisibility(View.VISIBLE);

        backBtn.setOnClickListener(titleListener);
        mSaveButton.setOnClickListener(titleListener);

    }

    private OnClickListener titleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back:
                    back();
                    break;
                case R.id.cx_fa_activity_title_more:
                        mSaveButton.setClickable(false);                       
                        saveCalendar();
                    break;

                default:
                    break;
            }

        }
    };

    private OnClickListener contentListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_calendar_edit_tab_item_layout:// 事项标签切换
                    if (type == 0) {
                        return;
                    }
                    type = 0;
                    changeTab();
                    break;
                case R.id.cx_fa_calendar_edit_tab_memorial_layout:// 纪念日标签切换
                    if (type == 1) {
                        return;
                    }
                    type = 1;
                    changeTab();
                    break;
                case R.id.cx_fa_calendar_edit_content_recommend_tv: // 常用纪念日
                    Intent commonIntent = new Intent(CxCalendarEditActivity.this,
                            CxCalendarCommonMemorialDay.class);
                    startActivityForResult(commonIntent, COMMON_MEMORIAL);
                    overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                    break;
                case R.id.cx_fa_calendar_edit_memorial_day_selecter_layout:  //纪念日/生日切换
                    showTargetOrCycleOrAdvanceDialog(4);
                    break;

                default:
                    break;
            }

        }

    };

    // 表情点击切换
    private void changeTab() {
        if (type == 0) {
            if (mode == EditMode.ADD_MODE) {
                titleText.setText(R.string.cx_fa_calendar_edit_title_text1);
            } else {
                titleText.setText(R.string.cx_fa_calendar_edit_title_text3);
            }
            itemTab();
            setItemReminderShow();
            setRemainderData();
        } else {
            if (mode == EditMode.ADD_MODE) {
                titleText.setText(R.string.cx_fa_calendar_edit_title_text2);
            } else {
                titleText.setText(R.string.cx_fa_calendar_edit_title_text4);
            }
            memorialTab();
            setMemorialReminderShow();
            setRemainderData();
        }

    }

    private OnClickListener reminderListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.cx_fa_calendar_edit_reminder_date_layout: // 日期
                    // showDatePickerDialog();
                    if (type == 0) {
                        if (itemCycle == 3) {
                            showDatePickerDialogWithMonthFixed();
                        } else if (itemCycle == 2) {
                            showReminderEditDateForWeeklyList();
                        } else if (itemCycle == 4) {
                            showDatePickerDialogWithAnnuallyFixed();
                        } else {
                            showDatePickerDialog();
                        }
                    } else {
                        if (memorialType == 1) {
                            showDateLunarPickerDialog(true);
                        } else {
                        	showDatePickerDialog2();
                        }

                    }

                    break;
                case R.id.cx_fa_calendar_edit_reminder_target_layout: // 提醒对象
                    showTargetOrCycleOrAdvanceDialog(1);
                    break;
                case R.id.cx_fa_calendar_edit_reminder_time_layout: // 时间
                    showTimePickerDialog();
                    break;
                case R.id.cx_fa_calendar_edit_reminder_cycle_layout: // 周期
                    showTargetOrCycleOrAdvanceDialog(2);
                    break;
                case R.id.cx_fa_calendar_edit_reminder_advance_layout: // 是否提前
                    showTargetOrCycleOrAdvanceDialog(3);
                    break;
                case R.id.cx_fa_calendar_edit_reminder_setting_layout:// 是否提醒
                    if (type == 0) {
                        setItemReminderClick();
                    } else {
                        setMemorialReminderClick();
                    }
                    break;
                case R.id.cx_fa_calendar_edit_delete_btn:
                    dropItem();
                    break;

                default:
                    break;
            }

        }

    };

    // 显示dialog
    private void showTargetOrCycleOrAdvanceDialog(int which) {
        View view = View.inflate(this, R.layout.cx_fa_widget_calendar_edit_common_dialog, null);
        LinearLayout targetLayout = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_edit_target);
        LinearLayout cycleLayout = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_edit_cycle);
        LinearLayout advanceLayout = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_edit_advance);
        LinearLayout memorialTypeLayout = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_edit_memorial_type);
        LinearLayout dateLayout = (LinearLayout)view.findViewById(R.id.cx_fa_calendar_edit_date);

        Button cancelBtn = (Button)view.findViewById(R.id.cx_fa_calendar_edit_common_dialog_cancel);

        switch (which) {
            case 1:
                targetLayout.setVisibility(View.VISIBLE);
                cycleLayout.setVisibility(View.GONE);
                advanceLayout.setVisibility(View.GONE);
                memorialTypeLayout.setVisibility(View.GONE);
                dateLayout.setVisibility(View.GONE);
                break;
            case 2:
                targetLayout.setVisibility(View.GONE);
                cycleLayout.setVisibility(View.VISIBLE);
                advanceLayout.setVisibility(View.GONE);
                memorialTypeLayout.setVisibility(View.GONE);
                dateLayout.setVisibility(View.GONE);
                break;
            case 3:
                targetLayout.setVisibility(View.GONE);
                cycleLayout.setVisibility(View.GONE);
                advanceLayout.setVisibility(View.VISIBLE);
                memorialTypeLayout.setVisibility(View.GONE);
                dateLayout.setVisibility(View.GONE);
                break;
            case 4:
                targetLayout.setVisibility(View.GONE);
                cycleLayout.setVisibility(View.GONE);
                advanceLayout.setVisibility(View.GONE);
                memorialTypeLayout.setVisibility(View.VISIBLE);
                dateLayout.setVisibility(View.GONE);
                break;
            case 5:
                targetLayout.setVisibility(View.GONE);
                cycleLayout.setVisibility(View.GONE);
                advanceLayout.setVisibility(View.GONE);
                memorialTypeLayout.setVisibility(View.GONE);
                dateLayout.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

        TextView targetMe = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_target_me);
        TextView targetOppo = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_target_oppo);
        TextView targetBoth = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_target_both);
        View targetOppoView = view.findViewById(R.id.cx_fa_calendar_edit_target_oppo_view);

        TextView cycleOnce = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_cycle_once);
        TextView cycleDay = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_cycle_day);
        TextView cycleWeek = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_cycle_week);
        TextView cycleMonth = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_cycle_month);
        TextView cycleYear = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_cycle_year);

        TextView advanceNone = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_advance_none);
        TextView advance15m = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_advance_15m);
        TextView advance1h = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_advance_1h);
        TextView advance1d = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_advance_1d);
        TextView advance3d = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_advance_3d);
        TextView advance5d = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_advance_5d);

        TextView memorialTypeBir = (TextView)view
                .findViewById(R.id.cx_fa_calendar_edit_memorial_type_birthday);
        TextView memorialTypeNormal = (TextView)view
                .findViewById(R.id.cx_fa_calendar_edit_memorial_type_normal);

        TextView date1 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_1);
        TextView date2 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_2);
        TextView date3 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_3);
        TextView date4 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_4);
        TextView date5 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_5);
        TextView date6 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_6);
        TextView date7 = (TextView)view.findViewById(R.id.cx_fa_calendar_edit_date_7);

        if (type == 0) {
            targetOppo.setVisibility(View.VISIBLE);
            targetOppoView.setVisibility(View.VISIBLE);
        } else {
            targetOppo.setVisibility(View.GONE);
            targetOppoView.setVisibility(View.GONE);
        }

        targetMe.setOnClickListener(dialogListener);
        targetOppo.setOnClickListener(dialogListener);
        targetBoth.setOnClickListener(dialogListener);
        cycleOnce.setOnClickListener(dialogListener);
        cycleDay.setOnClickListener(dialogListener);
        cycleWeek.setOnClickListener(dialogListener);
        cycleMonth.setOnClickListener(dialogListener);
        cycleYear.setOnClickListener(dialogListener);
        advanceNone.setOnClickListener(dialogListener);
        advance15m.setOnClickListener(dialogListener);
        advance1h.setOnClickListener(dialogListener);
        advance1d.setOnClickListener(dialogListener);
        advance3d.setOnClickListener(dialogListener);
        advance5d.setOnClickListener(dialogListener);
        memorialTypeBir.setOnClickListener(dialogListener);
        memorialTypeNormal.setOnClickListener(dialogListener);
        date1.setOnClickListener(dialogListener);
        date2.setOnClickListener(dialogListener);
        date3.setOnClickListener(dialogListener);
        date4.setOnClickListener(dialogListener);
        date5.setOnClickListener(dialogListener);
        date6.setOnClickListener(dialogListener);
        date7.setOnClickListener(dialogListener);

        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new Dialog(this, R.style.simple_dialog);
        dialog.setContentView(view);
        dialog.show();
    }

    OnClickListener dialogListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            dialog.dismiss();
            switch (v.getId()) {
                case R.id.cx_fa_calendar_edit_target_me:
                    mReminderTargetText.setText(R.string.cx_fa_calendar_edit_dialog_target_me);
                    if (type == 0) {
                        itemTarget = 0;
                    } else {
                        memorialTarget = 0;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_target_oppo:
                    mReminderTargetText.setText(R.string.cx_fa_calendar_edit_dialog_target_oppo);
                    itemTarget = 1;

                    break;
                case R.id.cx_fa_calendar_edit_target_both:
                    mReminderTargetText.setText(R.string.cx_fa_calendar_edit_dialog_target_both);
                    if (type == 0) {
                        itemTarget = 2;
                    } else {
                        memorialTarget = 2;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_cycle_once:
                    mReminderCycleText.setText(R.string.cx_fa_calendar_edit_dialog_cycle_noce);
                    itemCycle = 0;
                    setReminderDateType();
                    break;
                case R.id.cx_fa_calendar_edit_cycle_day:
                    mReminderCycleText.setText(R.string.cx_fa_calendar_edit_dialog_cycle_day);
                    itemCycle = 1;
                    setReminderDateType();
                    break;
                case R.id.cx_fa_calendar_edit_cycle_week:
                    mReminderCycleText.setText(R.string.cx_fa_calendar_edit_dialog_cycle_week);
                    itemCycle = 2;
                    setReminderDateType();
                    break;
                case R.id.cx_fa_calendar_edit_cycle_month:
                    mReminderCycleText.setText(R.string.cx_fa_calendar_edit_dialog_cycle_month);
                    itemCycle = 3;
                    setReminderDateType();
                    break;
                case R.id.cx_fa_calendar_edit_cycle_year:
                    mReminderCycleText.setText(R.string.cx_fa_calendar_edit_dialog_cycle_year);
                    itemCycle = 4;
                    setReminderDateType();
                    break;
                case R.id.cx_fa_calendar_edit_advance_none:
                    mReminderAdvanceText.setText(R.string.cx_fa_calendar_edit_dialog_advance_none);
                    if (type == 0) {
                        itemAdvance = 0;
                    } else {
                        memorialAdvance = 0;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_advance_15m:
                    mReminderAdvanceText.setText(R.string.cx_fa_calendar_edit_dialog_advance_15m);
                    if (type == 0) {
                        itemAdvance = 1;
                    } else {
                        memorialAdvance = 1;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_advance_1h:
                    mReminderAdvanceText.setText(R.string.cx_fa_calendar_edit_dialog_advance_1h);
                    if (type == 0) {
                        itemAdvance = 2;
                    } else {
                        memorialAdvance = 2;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_advance_1d:
                    mReminderAdvanceText.setText(R.string.cx_fa_calendar_edit_dialog_advance_1d);
                    if (type == 0) {
                        itemAdvance = 3;
                    } else {
                        memorialAdvance = 3;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_advance_3d:
                    mReminderAdvanceText.setText(R.string.cx_fa_calendar_edit_dialog_advance_3d);
                    if (type == 0) {
                        itemAdvance = 4;
                    } else {
                        memorialAdvance = 4;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_advance_5d:
                    mReminderAdvanceText.setText(R.string.cx_fa_calendar_edit_dialog_advance_5d);
                    if (type == 0) {
                        itemAdvance = 5;
                    } else {
                        memorialAdvance = 5;
                    }
                    break;
                case R.id.cx_fa_calendar_edit_memorial_type_birthday:
                    mMemorialTypeText.setText(R.string.cx_fa_calendar_edit_dialog_memorial_type_bir);
                    memorialType = 1;
                    break;
                case R.id.cx_fa_calendar_edit_memorial_type_normal:
                    mMemorialTypeText.setText(R.string.cx_fa_calendar_edit_dialog_memorial_type_normal);
                    memorialType = 2;
                    break;
                case R.id.cx_fa_calendar_edit_date_1:
                    onDateWeekClick(2);
                    break;
                case R.id.cx_fa_calendar_edit_date_2:
                    onDateWeekClick(3);
                    break;
                case R.id.cx_fa_calendar_edit_date_3:
                    onDateWeekClick(4);
                    break;
                case R.id.cx_fa_calendar_edit_date_4:
                    onDateWeekClick(5);
                    break;
                case R.id.cx_fa_calendar_edit_date_5:
                    onDateWeekClick(6);
                    break;
                case R.id.cx_fa_calendar_edit_date_6:
                    onDateWeekClick(7);
                    break;
                case R.id.cx_fa_calendar_edit_date_7:
                    onDateWeekClick(1);
                    break;

                default:
                    break;
            }
        }
    };

	private String dateExtra;

	private PopupWindow mDatePickerMemorialDialog;

    private void onDateWeekClick(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(itemFullTime));
        calendar.set(Calendar.DAY_OF_WEEK, i);
        itemFullTime = calendar.getTimeInMillis();
        setReminderDateType();
    }

    // 提醒点击设置
    private void setItemReminderClick() {

        if (itemReminderFlag == 0) {
        	itemCycle=itemTempCycle;
        	setReminderDateType();
        	
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_on);
            mReminderShowLayout.setVisibility(View.VISIBLE);
            mReminderTimeLayout.setVisibility(View.VISIBLE);
            itemReminderFlag = 1;
        } else {
        	
        	itemTempCycle=itemCycle;
        	itemCycle=0;
        	setReminderDateType();
        	
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_off);
            mReminderShowLayout.setVisibility(View.GONE);
            mReminderTimeLayout.setVisibility(View.GONE);
            itemReminderFlag = 0;
        }
    }

    // 提醒点击设置
    private void setMemorialReminderClick() {

        if (memorialReminderFlag == 0) {
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_on);
            mReminderShowLayout.setVisibility(View.VISIBLE);
            memorialReminderFlag = 1;
        } else {
            mReminderSettingImg.setImageResource(R.drawable.calendar_set_off);
            mReminderShowLayout.setVisibility(View.GONE);
            memorialReminderFlag = 0;
        }
    }

    // 标签切换
    private void itemTab() {
        mCommonText.setVisibility(View.GONE);
        mMemorialTypeLayout.setVisibility(View.GONE);
        mContentEdit.setHint(R.string.cx_fa_calendar_edit_edit_item_hint);

        mReminderCycleLayout.setVisibility(View.VISIBLE);
//        mReminderTimeLayout.setVisibility(View.VISIBLE);

     /*   if (itemCycle == 1) {
        	mReminderTimeLayout.setVisibility(View.GONE);
            mReminderDateLayout.setVisibility(View.GONE);
        } else {
        	mReminderTimeLayout.setVisibility(View.VISIBLE);
            mReminderDateLayout.setVisibility(View.VISIBLE);
        }*/

        mTabItemLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
        mTabItemText.setText(TextUtil.getNewSpanStr(
                getString(R.string.cx_fa_calendar_edit_tab_item), 18, Color.rgb(235, 161, 121)));
        mTabMemorialLayout
                .setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        mTabMemorialText.setText(TextUtil.getNewSpanStr(
                getString(R.string.cx_fa_calendar_edit_tab_memorial_day), 16,
                Color.argb(144, 0, 0, 0)));
    }

    // 标签切换
    private void memorialTab() {
        mCommonText.setVisibility(View.VISIBLE);
        mMemorialTypeLayout.setVisibility(View.VISIBLE);
        mContentEdit.setHint(R.string.cx_fa_calendar_edit_edit_memorial_day_hint);
        mReminderDateLayout.setVisibility(View.VISIBLE);
        mReminderCycleLayout.setVisibility(View.GONE);
        mReminderTimeLayout.setVisibility(View.GONE);

        mTabMemorialLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_white);
        mTabMemorialText.setText(TextUtil.getNewSpanStr(
                getString(R.string.cx_fa_calendar_edit_tab_memorial_day), 18,
                Color.rgb(235, 161, 121)));
        mTabItemLayout.setBackgroundResource(R.drawable.cx_fa_accounting_homepage_detail_bg_gray);
        mTabItemText.setText(TextUtil.getNewSpanStr(
                getString(R.string.cx_fa_calendar_edit_tab_item), 16, Color.argb(144, 0, 0, 0)));
    }

    // 获取常用纪念日
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode) {
            return;
        }

        if (requestCode == COMMON_MEMORIAL) {
            String extra = data.getStringExtra(CxCalendarParam.CALENDAR_COMMON_MEMORIAL);
            if (!TextUtils.isEmpty(extra)) {
                mContentEdit.setText(extra);
                mContentEdit.setSelection(extra.length());
                
                if(extra.contains("生日")){
                	mMemorialTypeText.setText(R.string.cx_fa_calendar_edit_dialog_memorial_type_bir);
                    memorialType = 1;
                }else{
                	mMemorialTypeText.setText(R.string.cx_fa_calendar_edit_dialog_memorial_type_normal);
                    memorialType = 2;
                }
                
            }
        }

    }

    protected void back() {
    	if(mLunarDatePickerDialog!=null && mLunarDatePickerDialog.isShowing()){
    		mLunarDatePickerDialog.dismiss();
    		mLunarDatePickerDialog=null;
    		return ;
    	}
    	
    	if(mDatePickerDialogWithMonthFixed!=null && mDatePickerDialogWithMonthFixed.isShowing()){
    		mDatePickerDialogWithMonthFixed.dismiss();
    		mDatePickerDialogWithMonthFixed=null;
    		return ;
    	}
    	
    	if(mDatePickerDialogWithAnnuallyFixed!=null && mDatePickerDialogWithAnnuallyFixed.isShowing()){
    		mDatePickerDialogWithAnnuallyFixed.dismiss();
    		mDatePickerDialogWithAnnuallyFixed=null;
    		return ;
    	}
    	
    	if(mDatePickerDialog!=null && mDatePickerDialog.isShowing()){
    		mDatePickerDialog.dismiss();
    		mDatePickerDialog=null;
    		return ;
    	}
    	
    	if(mTimePickerDialog!=null && mTimePickerDialog.isShowing()){
    		mTimePickerDialog.dismiss();
    		mTimePickerDialog=null;
    		return ;
    	}
    	
    	if(mDatePickerMemorialDialog!=null && mDatePickerMemorialDialog.isShowing()){
    		mDatePickerMemorialDialog.dismiss();
    		mDatePickerMemorialDialog=null;
    		return ;
    	}
    	
    	if(dialog!=null && dialog.isShowing()){
    		dialog.dismiss();
    		dialog=null;
    		return ;
    	}
    	
    	if(!TextUtils.isEmpty(mContentEdit.getText().toString().trim())){
    		showSureExitDialog();
    		return ;
    	}
    	
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }
    
    private void showSureExitDialog() {
		DialogUtil instance = DialogUtil.getInstance();
		instance.setOnSureClickListener(new OnSureClickListener() {
			
			@Override
			public void surePress() {
				finish();
		        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			}
		});
		instance.getSimpleDialog(CxCalendarEditActivity.this, null, "确定退出吗？", null, null).show();
	}

	protected void back2() {
    	if(mLunarDatePickerDialog!=null && mLunarDatePickerDialog.isShowing()){
    		mLunarDatePickerDialog.dismiss();
    		mLunarDatePickerDialog=null;
    	}
    	
    	if(mDatePickerDialogWithMonthFixed!=null && mDatePickerDialogWithMonthFixed.isShowing()){
    		mDatePickerDialogWithMonthFixed.dismiss();
    		mDatePickerDialogWithMonthFixed=null;
    	}
    	
    	if(mDatePickerDialogWithAnnuallyFixed!=null && mDatePickerDialogWithAnnuallyFixed.isShowing()){
    		mDatePickerDialogWithAnnuallyFixed.dismiss();
    		mDatePickerDialogWithAnnuallyFixed=null;
    	}
    	
    	if(mDatePickerDialog!=null && mDatePickerDialog.isShowing()){
    		mDatePickerDialog.dismiss();
    		mDatePickerDialog=null;
    	}
    	
    	if(mTimePickerDialog!=null && mTimePickerDialog.isShowing()){
    		mTimePickerDialog.dismiss();
    		mTimePickerDialog=null;
    	}
    	
    	if(mDatePickerMemorialDialog!=null && mDatePickerMemorialDialog.isShowing()){
    		mDatePickerMemorialDialog.dismiss();
    		mDatePickerMemorialDialog=null;
    	}
    	
    	if(dialog!=null && dialog.isShowing()){
    		dialog.dismiss();
    		dialog=null;
    	}
    	
    	finish();
    	overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    };

    /**
     * show time dialog at the bottom
     */
    private void showTimePickerDialog() {
        TimePicker timePicker = new TimePicker(this);
        if (mTimePickerDialog == null) {
            // long time = mController.getTime();

            mTimePickerDialog = new PopupWindow(timePicker, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mTimePickerDialog.setWidth(LayoutParams.MATCH_PARENT);
            mTimePickerDialog.setHeight(LayoutParams.WRAP_CONTENT);
            mTimePickerDialog.setBackgroundDrawable(getResources().getDrawable(
                    R.color.cx_fa_co_datepicker_dialog_background));
            mTimePickerDialog.setTouchable(true);
            mTimePickerDialog.setOutsideTouchable(true);

            timePicker.setOnTimeChangeListener(new OnDateChangeListener() {

                @Override
                public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
                    // long time = mController.getTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(itemFullTime));

                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    itemFullTime = calendar.getTimeInMillis();
                    String format = timeFormat.format(new Date(itemFullTime));
                    if (type == 0) {
                        itemTime = format;
                        mReminderTimeText.setText(itemTime);
                    } else {
                        memorialTime = format;
                        mReminderTimeText.setText(memorialTime);
                    }

                }

                @Override
                public void onDateChange(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    ;
                }

            });
        } else {
            if (mTimePickerDialog.isShowing()) {
                mTimePickerDialog.dismiss();
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(itemFullTime));
        timePicker.setHourOfDay(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));
        mTimePickerDialog.showAtLocation(timePicker, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 每月提醒只显示的每月有多少天的dialog
     */
    private void showDatePickerDialogWithMonthFixed() {
        final DatePicker datePicker = new DatePicker(CxCalendarEditActivity.this);

        if (mDatePickerDialogWithMonthFixed == null) {

            datePicker.setVisibleFields(DatePicker.VISIBLE_DAY);
            datePicker.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onDateChange(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(itemFullTime));
                    calendar.set(Calendar.MONTH, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    itemFullTime = calendar.getTimeInMillis();
                    setReminderDateType();
                }

                @Override
                public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
                    ;
                }

            });

            mDatePickerDialogWithMonthFixed = new PopupWindow(datePicker,
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mDatePickerDialogWithMonthFixed.setWidth(LayoutParams.MATCH_PARENT);
            mDatePickerDialogWithMonthFixed.setHeight(LayoutParams.WRAP_CONTENT);
            mDatePickerDialogWithMonthFixed.setBackgroundDrawable(getResources().getDrawable(
                    R.color.cx_fa_co_datepicker_dialog_background));
            mDatePickerDialogWithMonthFixed.setTouchable(true);
            mDatePickerDialogWithMonthFixed.setOutsideTouchable(true);

        } else {
            if (mDatePickerDialogWithMonthFixed.isShowing()) {
                mDatePickerDialogWithMonthFixed.dismiss();
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(itemFullTime));
        datePicker.setDate(calendar.get(Calendar.YEAR), 1, calendar.get(Calendar.DAY_OF_MONTH));// use
                                                                                                // the
                                                                                                // 1st//
                                                                                                // month
        mDatePickerDialogWithMonthFixed.showAtLocation(datePicker, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 每年提醒只显示的月和天的dialog
     */
    private void showDatePickerDialogWithAnnuallyFixed() {
        final DatePicker datePicker = new DatePicker(CxCalendarEditActivity.this);
        if (mDatePickerDialogWithAnnuallyFixed == null) {

            datePicker.setVisibleFields(DatePicker.VISIBLE_DAY | DatePicker.VISIBLE_MONTH);

            datePicker.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onDateChange(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(itemFullTime));
                    calendar.set(Calendar.MONTH, monthOfYear - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // mController.setTime(calendar.getTime().getTime());
                    // updateDateFields();
                    itemFullTime = calendar.getTimeInMillis();
                    setReminderDateType();
                }

                @Override
                public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
                    ;
                }

            });

            mDatePickerDialogWithAnnuallyFixed = new PopupWindow(datePicker,
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mDatePickerDialogWithAnnuallyFixed.setWidth(LayoutParams.MATCH_PARENT);
            mDatePickerDialogWithAnnuallyFixed.setHeight(LayoutParams.WRAP_CONTENT);
            mDatePickerDialogWithAnnuallyFixed.setBackgroundDrawable(getResources().getDrawable(
                    R.color.cx_fa_co_datepicker_dialog_background));
            mDatePickerDialogWithAnnuallyFixed.setTouchable(true);
            mDatePickerDialogWithAnnuallyFixed.setOutsideTouchable(true);

        } else {
            if (mDatePickerDialogWithAnnuallyFixed.isShowing()) {
                mDatePickerDialogWithAnnuallyFixed.dismiss();
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(itemFullTime));
        datePicker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialogWithAnnuallyFixed.showAtLocation(datePicker, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示每周提醒列表
     */
    private void showReminderEditDateForWeeklyList() {

        showTargetOrCycleOrAdvanceDialog(5);
    }

    private void showDateLunarPickerDialog(boolean isShowSetLunar) {

        final DatePicker datePicker = new DatePicker(CxCalendarEditActivity.this);
        if (isShowSetLunar) {
            datePicker.setChangeToLunar();
        } else {
            datePicker.hideLunarLayout();
        }
        if (mLunarDatePickerDialog == null) {

            mLunarDatePickerDialog = new PopupWindow(datePicker, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mLunarDatePickerDialog.setWidth(LayoutParams.MATCH_PARENT);
            mLunarDatePickerDialog.setHeight(LayoutParams.WRAP_CONTENT);
            mLunarDatePickerDialog.setBackgroundDrawable(getResources().getDrawable(
                    R.color.cx_fa_co_datepicker_dialog_background));
            mLunarDatePickerDialog.setTouchable(true);
            mLunarDatePickerDialog.setOutsideTouchable(true);

            datePicker.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onDateChange(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    // long time = mController.getTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(memorialFullTime));

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONDAY, monthOfYear - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    memorialFullTime = calendar.getTimeInMillis();   
                    datePicker.setLunar(calendar);     
                    datePicker.setLunar(calendar);
                    SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd");
                    memorialDate = nowFormat.format(memorialFullTime);
                    mReminderDateText.setText(memorialDate);
                }

                @Override
                public void onTimeChange(TimePicker view, int hourOfDay, int minute) {

                }
            });

            datePicker.setOnLunarImgClickListener(new OnLunarImgClickListener() {

                @Override
                public void onOnLunarImgClick(boolean isSelected) {
                    if (isSelected) {
                        memorialLunar = 1;
                    } else {
                        memorialLunar = 0;
                    }
                }
            });

        } else {
            if (null != mDatePickerDialog && mDatePickerDialog.isShowing()) {
                mDatePickerDialog.dismiss();
                return;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(memorialFullTime));
        if(memorialLunar==0){
        	datePicker.hideLunar();
        }else{
        	datePicker.showLunar(calendar);
        }
        datePicker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DAY_OF_MONTH));     
        mLunarDatePickerDialog.showAtLocation(datePicker, Gravity.BOTTOM, 0, 0);
    }

    /**
     * show date dialog at the bottom
     */
    private void showDatePickerDialog() {

        DatePicker datePicker = new DatePicker(CxCalendarEditActivity.this);

        if (mDatePickerDialog == null) {

            mDatePickerDialog = new PopupWindow(datePicker, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mDatePickerDialog.setWidth(LayoutParams.MATCH_PARENT);
            mDatePickerDialog.setHeight(LayoutParams.WRAP_CONTENT);
            mDatePickerDialog.setBackgroundDrawable(getResources().getDrawable(R.color.cx_fa_co_datepicker_dialog_background));
            mDatePickerDialog.setTouchable(true);
            mDatePickerDialog.setOutsideTouchable(true);

            datePicker.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onDateChange(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    // long time = mController.getTime();
                	
            		Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(itemFullTime));

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONDAY, monthOfYear - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    itemFullTime = calendar.getTimeInMillis();
                    setReminderDateType();
                }

                @Override
                public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
                }
            });

        } else {
            if (mDatePickerDialog.isShowing()) {
                mDatePickerDialog.dismiss();
                return;
            }
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(itemFullTime);
        datePicker.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.showAtLocation(datePicker, Gravity.BOTTOM, 0, 0);
    }
    
    private void showDatePickerDialog2() {
    	
    	DatePicker datePicker = new DatePicker(CxCalendarEditActivity.this);
    	
    	if (mDatePickerMemorialDialog == null) {
    		
    		mDatePickerMemorialDialog = new PopupWindow(datePicker, LayoutParams.MATCH_PARENT,
    				LayoutParams.MATCH_PARENT);
    		mDatePickerMemorialDialog.setWidth(LayoutParams.MATCH_PARENT);
    		mDatePickerMemorialDialog.setHeight(LayoutParams.WRAP_CONTENT);
    		mDatePickerMemorialDialog.setBackgroundDrawable(getResources().getDrawable(R.color.cx_fa_co_datepicker_dialog_background));
    		mDatePickerMemorialDialog.setTouchable(true);
    		mDatePickerMemorialDialog.setOutsideTouchable(true);
    		
    		datePicker.setOnDateChangeListener(new OnDateChangeListener() {
    			
    			@Override
    			public void onDateChange(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    				
    				// long time = mController.getTime();
    			
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(memorialFullTime));
					
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.MONDAY, monthOfYear - 1);
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					
					memorialFullTime = calendar.getTimeInMillis();
					SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd");
					memorialDate = nowFormat.format(memorialFullTime);
					mReminderDateText.setText(memorialDate);
    				
    			}
    			
    			@Override
    			public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
    			}
    		});
    		
    	} else {
    		if (mDatePickerMemorialDialog.isShowing()) {
    		    mDatePickerMemorialDialog.dismiss();
    			return;
    		}
    	}
    	Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(memorialFullTime);
    		
    	datePicker.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
    			c.get(Calendar.DAY_OF_MONTH));
    	mDatePickerMemorialDialog.showAtLocation(datePicker, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 保存日历或者纪念日
     */
    private void saveCalendar() {
        if (type == 0) { // 事项
        	
        	CxLog.i("RkCalendarEditActivity_men", "mCalendarId:"+mCalendarId+",type:"+type+",itemTarget:"+itemTarget+
        			",memorialType:"+memorialType+",memorialLunar:"+memorialLunar+",itemReminderFlag:"+itemReminderFlag
        			+",itemCycle:"+itemCycle+",itemAdvance:"+itemAdvance);
        	
        	
            mController.setData(mCalendarId, type, itemTarget, mContentEdit.getText().toString(),
                    memorialType, memorialLunar, itemReminderFlag, itemFullTime, itemCycle,
                    itemAdvance);
        } else { // 纪念日
        	
        	CxLog.i("RkCalendarEditActivity_men", "mCalendarId:"+mCalendarId+",type:"+type+",memorialTarget:"+memorialTarget+
        			",memorialType:"+memorialType+",memorialLunar:"+memorialLunar+",memorialReminderFlag:"+memorialReminderFlag
        			+",memorialCycle:"+memorialCycle+",memorialAdvance:"+memorialAdvance);
        	
            mController.setData(mCalendarId, type, memorialTarget, mContentEdit.getText()
                    .toString(), memorialType, memorialLunar, memorialReminderFlag,
                    memorialFullTime, memorialCycle, memorialAdvance);
        }
        
        if(TextUtils.isEmpty(mContentEdit.getText())){
            //QuickMessage.info(this, R.string.cx_fa_calendar_content_can_not_empty);
            ToastUtil.getSimpleToast(CxCalendarEditActivity.this, -3,
                    getString(R.string.cx_fa_calendar_content_can_not_empty), 1).show();
            mSaveButton.setClickable(true);
            return;
        }

//        RkLoadingUtil.getInstance().showLoading(RkCalendarEditActivity.this, true);
        // check if the user-set time valid;
        try {
            if (mController.getPeriod() == CalendarController.sReminderPeriodOnce && (mController.getIsRemind()==1)) {
                Calendar now = Calendar.getInstance();
                Calendar remindTime = Calendar.getInstance();
                remindTime.setTimeInMillis(mController.getRealTime());

                if (remindTime.before(now)) {
                	mSaveButton.setClickable(true);
                    QuickMessage.error(this, R.string.cx_fa_nls_reminder_invalid_date_for_creation);
//                    RkLoadingUtil.getInstance().dismissLoading();
                    return;
                }
            }
            if (mController.getId().length() > 0) {
                mController.cancelAlarmReminder(CxCalendarEditActivity.this, mController.getFlag());
            }
            CxLog.d(TAG,"save privious time="+ CalendarDisplayUtility.getDateStr(mController.getRealTime()));
            
            DialogUtil.getInstance().getLoadingDialogShow(CxCalendarEditActivity.this, -1);
            
            mController.submitCalendarChanges(this, new JSONCaller() {

                @Override
                public int call(Object result) {
                	
                	DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
                	new Handler(getMainLooper()){
                		public void handleMessage(Message msg) {
                			mSaveButton.setClickable(true);
                		};
                	}.sendEmptyMessage(0);

                	if(result==null){
                		showResponseToast(getString(R.string.cx_fa_net_response_code_null), 0);
                		return -1;
                	}
                	
                	CxLog.i("RkCalendarEditActivity_men", result.toString());
                	
                    try {
                    	JSONObject reminderObj = (JSONObject)result;
                    	if(!reminderObj.isNull("rc")){
    						int rc = reminderObj.getInt("rc");
    						if(rc==408){
    							showResponseToast(getString(R.string.cx_fa_net_response_code_null), 0);    							
    							return -2;
    						}
    						if(rc!=0){
    							showResponseToast(getString(R.string.cx_fa_net_response_code_fail), 0);
    							return -3;
    						}
                    	}
                        
                        CalendarDataObj reminder = new CalendarDataObj(reminderObj,CxCalendarEditActivity.this);
                        CxLog.d(TAG, "after request api time=" + CalendarDisplayUtility.getDateStr((long)reminder.getBaseTimestamp() * 1000));
                        CxLog.v(TAG, "local remindId: " + reminder.mId);
                        CxLog.v(TAG, "local remind get flag: " + reminder.getFlag());
                        int basetime = reminder.getBaseTimestamp();
                        CxLog.d(TAG,"after adjust time="+ CalendarDisplayUtility.getDateStr((long)reminder.getBaseTimestamp() * 1000));
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis((long)basetime * 1000);
                        android.os.Message calendarMessage = CxCalendarFragment.getInstance().calendarHandler.obtainMessage(CxCalendarFragment.getInstance().REFRESH_CALENDAR_DATA,c);
                        calendarMessage.sendToTarget();
                        back2();
                    } catch (Exception e) {
                        CxLog.e(TAG, "Error: failed to get object from create reminder result");
                        e.printStackTrace();
                    }
                    return 0;
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
//            RkLoadingUtil.getInstance().dismissLoading();
        }
    }
    
    private void dropItem(){
//        RkLoadingUtil.getInstance().showLoading(RkCalendarEditActivity.this, true);
    	DialogUtil.getInstance().getLoadingDialogShow(this, -1);
        CalendarApi.getInstance().doDeleteReminder(mController.getData().getId(), new JSONCaller() {
            
            @Override
            public int call(Object result) {
            	DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
            	
            	if(result!=null){
            		try {
						JSONObject obj=(JSONObject)result;
						if(!obj.isNull("rc")){
    						int rc = obj.getInt("rc");
    						if(rc==408){
    							showResponseToast(getString(R.string.cx_fa_net_response_code_null), 0);    							
    							return -2;
    						}
    						if(rc!=0){
    							showResponseToast(getString(R.string.cx_fa_net_response_code_fail), 0);
    							return -3;
    						}
                    	}else{
                    		showResponseToast(getString(R.string.cx_fa_net_response_code_fail), 0);
							return -3;
                    	}
					} catch (JSONException e) {
						e.printStackTrace();
					}
            	}
            	
                if(mController.getData().getIsRemind()){
                    CalendarController.getInstance().cancelAlarmReminder(CxCalendarEditActivity.this, mController.getData().getFlag());
                }
                Message calendarMessage = CxCalendarFragment.calendarHandler
                        .obtainMessage(CxCalendarFragment.getInstance().REFRESH_CALENDAR_DATA);
                calendarMessage.sendToTarget();
                back2();
//                RkLoadingUtil.getInstance().dismissLoading();
                return 0;
            }
        });
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
		new Handler(CxCalendarEditActivity.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxCalendarEditActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}


}
