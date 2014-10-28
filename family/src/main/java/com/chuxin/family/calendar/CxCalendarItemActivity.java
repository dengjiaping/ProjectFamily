
package com.chuxin.family.calendar;

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
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.widgets.CxImageView;

import android.content.Intent;
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
import java.util.Date;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxCalendarItemActivity extends CxRootActivity {

    private String[] targets1 = {
            "我提醒自己", "我提醒对方","我提醒双方"
    };
    
    private String[] targets2 = {
    		"提醒我", "提醒双方"
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

    private LinearLayout mItemReminderDetailLinearLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.cx_fa_activity_calendar_item);

        initTitle();

        init();

        fillData();
    }

    private void init() {

        mIconImg = (CxImageView)findViewById(R.id.cx_fa_calendar_item_content_icon_iv);
        mContentText = (TextView)findViewById(R.id.cx_fa_calendar_item_content_tv);
        mDateText = (TextView)findViewById(R.id.cx_fa_calendar_item_content_date_tv);

        mReminderIcon = (ImageView)findViewById(R.id.cx_fa_calendar_item_reminder_clock_icon);
        mReminderDateText = (TextView)findViewById(R.id.cx_fa_calendar_item_reminder_date_tv);
        mReminderTimeText = (TextView)findViewById(R.id.cx_fa_calendar_item_reminder_time_tv);
        mReminderAdvanceText = (TextView)findViewById(R.id.cx_fa_calendar_item_reminder_advance_tv);
        mReminderTargetText = (TextView)findViewById(R.id.cx_fa_calendar_item_reminder_target_tv);
        mReminderNumberText = (TextView)findViewById(R.id.cx_fa_calendar_item_reminder_number_tv);
        mItemReminderDetailLinearLayout = (LinearLayout)findViewById(R.id.cx_fa_calendar_item_detail_reminder_lv);

        mDeleteBtn = (Button)findViewById(R.id.cx_fa_calendar_item_delete_btn);
        mDeleteBtn.setOnClickListener(titleListener);
        mController = CalendarController.getInstance();
        mCalendarDisplayUtility = new CalendarDisplayUtility(getResources());
    }

    // 初始化标题栏
    private void initTitle() {
        Button backBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        Button editBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
        TextView titleText = (TextView)findViewById(R.id.cx_fa_activity_title_info);

        titleText.setText(getString(R.string.cx_fa_calendar_item_title_text));
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
        if (TextUtils.equals(mCalendarDataObj.getAuthor(), CxGlobalParams.getInstance().getUserId())) { // 属于自己创建
            // cx_fa_hb_icon_small
            mIconImg.displayImage(imageLoader, CxGlobalParams.getInstance().getIconSmall(),
                    CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, CxGlobalParams.getInstance()
                            .getSmallImgConner());
        } else { // 对方创建
            mIconImg.displayImage(imageLoader, CxGlobalParams.getInstance().getPartnerIconBig(),
            		CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, CxGlobalParams.getInstance()
                            .getMateSmallImgConner());
        }
        mContentText.setText(mCalendarDataObj.getContent());
        SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy年MM月dd日,HH:mm");
        CxLog.i("RkCalendarEditActivity_men",
                ">>>>>>>>>"
                        + nowFormat.format(new Date(
                                (long)mCalendarDataObj.getBaseTimestamp() * 1000)));
        String[] times = nowFormat.format((long)mCalendarDataObj.getBaseTimestamp() * 1000).split(
                ",");
        String dateText = times[0];
        String timeText = times[1];
        mDateText.setText(dateText);
        if (mCalendarDataObj.getIsRemind()) {
            mItemReminderDetailLinearLayout.setVisibility(View.VISIBLE);
            mReminderIcon.setVisibility(View.VISIBLE);
            mDeleteBtn.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mCalendarDataObj.getAuthor(), CxGlobalParams.getInstance().getUserId())) { // 属于自己创建
                mReminderIcon.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_me);
            } else {
                mReminderIcon.setBackgroundResource(CxResourceDarwable.getInstance().dr_calendar_clock_img_oppo);
            }
        } else {
            // mReminderIcon.setVisibility(View.GONE);
            // mDeleteBtn.setVisibility(View.GONE);
            mItemReminderDetailLinearLayout.setVisibility(View.GONE);
        }
        mReminderDateText.setText(mCalendarDisplayUtility.createNLSReminderPeriodLabel(mCalendarDataObj));
        //mReminderTimeText.setText(timeText);
        mReminderTimeText.setVisibility(View.GONE);
        if(mCalendarDataObj.getAdvance()==0){
        	mReminderAdvanceText.setVisibility(View.GONE);
        }else{
        	mReminderAdvanceText.setVisibility(View.VISIBLE);
        	mReminderAdvanceText.setText(advances[mCalendarDataObj.getAdvance()]);
        }
        int target = mCalendarDataObj.getTarget();
        boolean b = TextUtils.equals(mCalendarDataObj.getAuthor(), CxGlobalParams.getInstance().getUserId());
        String str=getString(CxResourceString.getInstance().str_pair)+CxGlobalParams.getInstance().getPartnerName();
        if(target==0){
        	mReminderTargetText.setText(targets1[0]);
    	}else if(target==1){
    		if(b){
    			mReminderTargetText.setText(targets1[1]);
    		}else{
    			mReminderTargetText.setText(str+targets2[0]);
    		}
    	}else if(target==2){
    		if(b){
    			mReminderTargetText.setText(targets1[2]);
    		}else{
    			mReminderTargetText.setText(str+targets2[1]);
    		}
    	}

       
//        mReminderTargetText.setText(targets[mCalendarDataObj.getTarget()]);
        try {
            if (mCalendarDataObj.getStatus() == 0) {
                mReminderNumberText.setText(mCalendarDisplayUtility
                        .createNLSReminderHappenLabel(mCalendarDataObj));
            } else {
                mReminderNumberText
                        .setText(getString(R.string.cx_fa_calendar_status_remind_expire));
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
                        Intent editIntent = new Intent(CxCalendarItemActivity.this,
                                CxCalendarEditActivity.class);
                        editIntent.putExtra(CxCalendarParam.CALENDAR_EDIT_TYPE,
                                CxCalendarParam.CALENDAR_TYPE_ITEM);
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
                case R.id.cx_fa_calendar_item_delete_btn:
                    // dropItem();
                    break;

                default:
                    break;
            }

        }
    };

    private CxImageView mIconImg;

    private TextView mContentText;

    private TextView mDateText;

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
//        String msg = getResources().getString(R.string.cx_fa_calendar_check_not_edit_item_msg);
//        Toast toast = Toast.makeText(RkCalendarItemActivity.this, msg, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        LinearLayout toastView = (LinearLayout)toast.getView();
//        ImageView imageCodeProject = new ImageView(RkCalendarItemActivity.this);
//        imageCodeProject.setImageResource(R.drawable.cancel_button);
//        toastView.addView(imageCodeProject, 0);
//        toast.show();
    	ToastUtil.getSimpleToast(this, -3, getString(R.string.cx_fa_calendar_check_not_edit_item_msg), 1).show();
    }

    private void dropItem() {
        CxLoadingUtil.getInstance().showLoading(CxCalendarItemActivity.this, true);
        CalendarApi.getInstance().doDeleteReminder(mCalendarDataObj.getId(), new JSONCaller() {

            @Override
            public int call(Object result) {
                if (mCalendarDataObj.getIsRemind()) {
                    CalendarController.getInstance().cancelAlarmReminder(
                            CxCalendarItemActivity.this, mCalendarDataObj.getFlag());
                }
                CxLoadingUtil.getInstance().dismissLoading();
                return 0;
            }
        });
    }
}
