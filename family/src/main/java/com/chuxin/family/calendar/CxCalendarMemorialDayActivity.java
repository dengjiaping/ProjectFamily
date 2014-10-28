
package com.chuxin.family.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.CalendarDataObj;
import com.chuxin.family.net.CalendarApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.TextUtil;
import com.chuxin.family.utils.ToastUtil;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxCalendarMemorialDayActivity extends CxRootActivity {

    private String[] targets = {
            "我提醒自己", "我提醒双方","提醒双方"
    };

    private String[] cycles = {
            "提醒一次", "每天一次", "每周一次", "每月一次", "每年一次"
    };

    private String[] advances = {
            "不提前", "提前15分钟", "提前1小时", "提前1天", "提前3天", "提前5天"
    };

    private String[] memorialTypes = {
            "生日", "一般纪念日"
    };

    private String[] dateWeeks = {
            "周一", "周二", "周三", "周四", "周五", "周六", "周日"
    };

    private CalendarDisplayUtility mCalendarDisplayUtility;

    private LinearLayout mMemorialReminderDetailLinearLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.cx_fa_activity_calendar_memorial_day);

        initTitle();
        init();
        fillData();
    }

    private void init() {
        mContentTitleText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_content_title_tv);
        mContentNumberText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_content_number_tv);
        mContentDateText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_content_date_tv);

        mReminderIcon = (ImageView)findViewById(R.id.cx_fa_calendar_memorial_day_reminder_clock_icon);
        mReminderDateText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_reminder_date_tv);
        mReminderTimeText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_reminder_time_tv);
        mReminderAdvanceText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_reminder_advance_tv);
        mReminderTargetText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_reminder_target_tv);
        mReminderNumberText = (TextView)findViewById(R.id.cx_fa_calendar_memorial_day_reminder_number_tv);
        mMemorialReminderDetailLinearLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_memorial_reminder_detail_lv);

        mDeleteBtn = (Button)findViewById(R.id.cx_fa_calendar_memorial_day_delete_btn);
        mDeleteBtn.setOnClickListener(titleListener);
        mController = CalendarController.getInstance();
        mCalendarDisplayUtility = new CalendarDisplayUtility(getResources());

    }

    // 初始化标题栏
    private void initTitle() {
        Button backBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        Button editBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
        TextView titleText = (TextView)findViewById(R.id.cx_fa_activity_title_info);

        titleText.setText(getString(R.string.cx_fa_calendar_memorial_day_title_text));
        backBtn.setText(getString(R.string.cx_fa_navi_back));
        editBtn.setText(getString(R.string.cx_fa_calendar_item_title_edit_btn_text));
        editBtn.setVisibility(View.VISIBLE);

        backBtn.setOnClickListener(titleListener);
        editBtn.setOnClickListener(titleListener);

    }

    private void fillData() {
        mCalendarDataObj = mController.getData();
        if (null == mCalendarDataObj) {
            return;
        }
        mContentTitleText.setText(mCalendarDataObj.getContent());
        SimpleDateFormat contentDateFormat = new SimpleDateFormat("yyyy年MM月dd日,HH:mm");
        SimpleDateFormat remindDateFormat = new SimpleDateFormat("每年MM月dd日,HH:mm");
        CxLog.i("RkCalendarEditActivity_men",
                ">>>>>>>>>"
                        + contentDateFormat.format(new Date(
                                (long)mCalendarDataObj.getBaseTimestamp() * 1000)));
        String[] times = contentDateFormat.format((long)mCalendarDataObj.getBaseTimestamp() * 1000).split(
                ",");
        String[] remindTimes = remindDateFormat.format((long)mCalendarDataObj.getBaseTimestamp() * 1000).split(
                ",");
        String dateText = times[0];
        String timeText = times[1];
        mContentDateText.setText(dateText);
        if (mCalendarDataObj.getIsRemind()) {
            mMemorialReminderDetailLinearLayout.setVisibility(View.VISIBLE);       
            mDeleteBtn.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mCalendarDataObj.getAuthor(), CxGlobalParams.getInstance()
                    .getUserId())) { // 属于自己创建
                mReminderIcon.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_me);
            } else {
                mReminderIcon.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_oppo);
            }
        } else {
            mMemorialReminderDetailLinearLayout.setVisibility(View.GONE);
            mDeleteBtn.setVisibility(View.GONE);
        }
        mReminderDateText.setText(remindTimes[0]);
        mReminderTimeText.setText(timeText);
        if(mCalendarDataObj.getAdvance()==0){
        	mReminderAdvanceText.setVisibility(View.GONE);
        }else{
        	mReminderAdvanceText.setVisibility(View.VISIBLE);
        	mReminderAdvanceText.setText(advances[mCalendarDataObj.getAdvance()]);
        }
        int target = mCalendarDataObj.getTarget();
        if(target==0){
        	mReminderTargetText.setText(targets[0]);
        }else if(target==2 && TextUtils.equals(mCalendarDataObj.getAuthor(), CxGlobalParams.getInstance().getUserId())){
        	mReminderTargetText.setText(targets[1]);
        }else if(target==2 && TextUtils.equals(mCalendarDataObj.getAuthor(), CxGlobalParams.getInstance().getPartnerId())){
        	mReminderTargetText.setText(getString(CxResourceString.getInstance().str_pair)+
        			CxGlobalParams.getInstance().getPartnerName()+targets[mCalendarDataObj.getTarget()]);
        }
        
        try {
            long baseTimestamp = (long)mCalendarDataObj.getBaseTimestamp() * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            String dateStr = format.format(new Date(baseTimestamp));
            Calendar nowC = Calendar.getInstance();
            nowC.setTime(new Date(System.currentTimeMillis()));
            nowC.set(Calendar.HOUR_OF_DAY, 9);
            nowC.set(Calendar.MINUTE, 0);
            nowC.set(Calendar.SECOND, 0);
            
            long nowTime = nowC.getTime().getTime();
         
            
            mContentNumberText.setText("");
            if(mCalendarDataObj.getDayType()==1){
            	Calendar oldC = Calendar.getInstance();
                oldC.setTimeInMillis(baseTimestamp);
                oldC.set(Calendar.YEAR, nowC.get(Calendar.YEAR));
                int day = 0;
                if (mCalendarDataObj.getIsLunar() == 1) {
                    String[] split = dateStr.split("\\.");
                    ChineseCalendar CC = new ChineseCalendar(Integer.parseInt(split[0]),
                            (Integer.parseInt(split[1]) - 1), Integer.parseInt(split[2]));
                    int cMonth = CC.get(ChineseCalendar.CHINESE_MONTH);
                    int cDay = CC.get(ChineseCalendar.CHINESE_DATE);

                    ChineseCalendar CC2 = new ChineseCalendar(true, nowC.get(Calendar.YEAR),cMonth, cDay);
                    Calendar c1 = Calendar.getInstance();
                    c1.set(CC2.get(Calendar.YEAR), CC2.get(Calendar.MONTH),CC2.get(Calendar.DAY_OF_MONTH), 10, 0,0);
                    long oldTime = c1.getTime().getTime();

                    if (oldTime < nowTime) {
                        CC2 = new ChineseCalendar(true, nowC.get(Calendar.YEAR) + 1, cMonth,cDay);
                        c1.set(CC2.get(Calendar.YEAR), CC2.get(Calendar.MONTH),CC2.get(Calendar.DAY_OF_MONTH), 10, 0,0);
                        oldTime = c1.getTime().getTime();
                    }
                    day = (int)((oldTime - nowTime) / (1000 * 3600 * 24));
                } else {
                    long oldTime = oldC.getTime().getTime();
                    if (oldTime < nowTime) {
                        oldC.set(Calendar.YEAR, nowC.get(Calendar.YEAR) + 1);
                        oldTime = oldC.getTime().getTime();
                    }
                    day = (int)((oldTime - nowTime) / (1000 * 3600 * 24));
                }
                
                if(day==0){
                	mContentNumberText.setText(TextUtil.getNewSpanStr("今天", 40, Color.rgb(177, 44, 44)));
                }else{
                	mContentNumberText.setText(TextUtil.getNewSpanStr(day + "天后", 40, Color.rgb(177, 44, 44)));
                }
            }else{
            	Calendar oldC = Calendar.getInstance();
                oldC.setTimeInMillis(baseTimestamp);
                int day = 0;
                long oldTime = oldC.getTime().getTime();
                
                if (oldTime < nowTime) {
                	CxLog.i("RkCalendarMemorialDayActivity_men", ">>>:"+(nowTime - oldTime));
                    day = (int)((nowTime - oldTime) / (1000 * 3600 * 24))+1;
                    mContentNumberText.setText(TextUtil.getNewSpanStr("已"+day + "天", 40, Color.rgb(131, 169, 24)));
                }else{
                	day = (int)((oldTime - nowTime) / (1000 * 3600 * 24));
                    if(day==0){
                    	mContentNumberText.setText(TextUtil.getNewSpanStr("今天", 40, Color.rgb(131, 169, 24)));
                    }else{
                    	mContentNumberText.setText(TextUtil.getNewSpanStr(day + "天后", 40, Color.rgb(131, 169, 24)));
                    }
                }
            }
            if (mCalendarDataObj.getStatus() == 0) {
                mReminderNumberText.setText(mCalendarDisplayUtility.createNLSReminderHappenLabel(mCalendarDataObj));
            } else {
                mReminderNumberText.setText(getString(R.string.cx_fa_calendar_status_remind_expire));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnClickListener titleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back:
                    back();
                    break;
                case R.id.cx_fa_activity_title_more:
                    if (mCalendarDataObj.getAuthor().equals(
                            CxGlobalParams.getInstance().getUserId())) {
                        Intent editIntent = new Intent(CxCalendarMemorialDayActivity.this,
                                CxCalendarEditActivity.class);
                        editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_TYPE,
                                CxCalendarParam.CALENDAR_TYPE_MEMORIAL);
                        int dayType = mCalendarDataObj.getDayType();
                        if(dayType==1){
                        	editIntent.putExtra(CxCalendarParam.CALENDAR_MEMORIAL_TYPE,
                                    CxCalendarParam.CALENDAR_MEMORIAL_BIRTHDAY);
                        }else{
                        	editIntent.putExtra(CxCalendarParam.CALENDAR_MEMORIAL_TYPE,
                                    CxCalendarParam.CALENDAR_MEMORIAL_NORMAL);
                        }                       
                        editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_MODE,
                                CxCalendarParam.CALENDAR_EDIT_UPDATE);
                        mController.setData(mCalendarDataObj.getId(), mCalendarDataObj.getType(),
                                mCalendarDataObj.getTarget(), mCalendarDataObj.getContent(),
                                mCalendarDataObj.getDayType(), mCalendarDataObj.getIsLunar(),
                                mCalendarDataObj.getIsRemindToInt(),
                                mCalendarDataObj.getBaseTimestamp(), mCalendarDataObj.getCycle(),
                                mCalendarDataObj.getAdvance());
                        startActivity(editIntent);
                        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
                        finish();
                    } else {
                        showNotEditCalendarDialog();
                    }
                    break;
                case R.id.cx_fa_calendar_memorial_day_delete_btn:
                    // dropMemorial();
                    break;

                default:
                    break;
            }

        }
    };

    private TextView mContentTitleText;

    private TextView mContentNumberText;

    private TextView mContentDateText;

    private TextView mReminderDateText;

    private TextView mReminderTimeText;

    private TextView mReminderAdvanceText;

    private TextView mReminderTargetText;

    private TextView mReminderNumberText;

    private ImageView mReminderIcon;

    private Button mDeleteBtn;

    private CalendarController mController;

    private CalendarDataObj mCalendarDataObj;

    protected void back() {
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
     * pop is save dialog
     */
    private void showNotEditCalendarDialog() {
//        String msg = getResources().getString(R.string.cx_fa_calendar_check_not_edit_memorial_msg);
//        Toast toast = Toast.makeText(CxCalendarMemorialDayActivity.this, msg, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        LinearLayout toastView = (LinearLayout)toast.getView();
//        ImageView imageCodeProject = new ImageView(CxCalendarMemorialDayActivity.this);
//        imageCodeProject.setImageResource(R.drawable.cancel_button);
//        toastView.addView(imageCodeProject, 0);
//        toast.show();
    	ToastUtil.getSimpleToast(this, -3, getString(R.string.cx_fa_calendar_check_not_edit_memorial_msg), 1).show();
    }

    private void dropMemorial() {
        CxLoadingUtil.getInstance().showLoading(CxCalendarMemorialDayActivity.this, true);
        CalendarApi.getInstance().doDeleteReminder(mCalendarDataObj.getId(), new JSONCaller() {

            @Override
            public int call(Object result) {
                if (mCalendarDataObj.getIsRemind()) {
                    CalendarController.getInstance().cancelAlarmReminder(
                            CxCalendarMemorialDayActivity.this, mCalendarDataObj.getFlag());
                }
                CxLoadingUtil.getInstance().dismissLoading();
                return 0;
            }
        });
    }

}
