
package com.chuxin.family.calendar;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.utils.Globals;
import com.squareup.timessquare.CalendarView;
import com.squareup.timessquare.CalendarView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarView.OnDetailClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Date;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxCalendarDetailActivity extends CxRootActivity{
    private CalendarView mCalendarView;
	private Date date;
	private Calendar instance=Calendar.getInstance();
	
	public  static Handler detailHandler;
	
	private Calendar tempC;
//	private GestureDetector mGestureDetector;
	
	private GestureDetector detector;
	
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.cx_fa_widget_calendar_detail);
        
        detailHandler=new Handler(){
        	public void handleMessage(android.os.Message msg) {
        		Calendar c=(Calendar)msg.obj;
        		mCalendarView.selectDate(c, false);
        	};
        };
        
        
        Intent intent = getIntent();
        String extra = intent.getStringExtra("nowDate");
        if(!TextUtils.isEmpty(extra)){
        	instance.set(Integer.parseInt(extra.substring(0,4)), Integer.parseInt(extra.substring(4,6))-1, 
        			Integer.parseInt(extra.substring(6,8)));
        }
//        mGestureDetector = new GestureDetector(this, new GestureListener());
        init();
    }
    
    private void init(){
        mCalendarView=(CalendarView)findViewById(R.id.cx_fa_calendar_detail_view);
        mCalendarView.selectDate2(instance,false);
        mCalendarView.setOnDetailClickListener(new OnDetailClickListener() {
            
            @Override
            public void onDetailClick() {
                Globals.getInstance().setCalendarIsLandSpace(false);
                CxCalendarDetailActivity.this.finish();
            }
        });

        mCalendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
			
			@Override
			public void onDateUnselected(Calendar date) {				
			}

			@Override
			public void onDateSelected(Calendar date, boolean isRead) {
			
				if(CalendarUtil.getInstance().isSameMonth(date, instance)){
					instance=date;
					android.os.Message calendarMessage = CxCalendarFragment.getInstance().calendarHandler
		        		.obtainMessage(CxCalendarFragment.getInstance().CHANGE_DAY_OF_MONTH,date);
					calendarMessage.sendToTarget();

				}else{
					instance=date;
					sendMessage(date);		
				}	
			}
		});
    }
    
    private void sendMessage(Calendar date){
  	
        android.os.Message calendarMessage = CxCalendarFragment.getInstance().calendarHandler
        	.obtainMessage(CxCalendarFragment.getInstance().UPDATE_DATE_CALENDAR,date);
        calendarMessage.sendToTarget();
    }
    
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            Globals.getInstance().setCalendarIsLandSpace(false);
            CxCalendarDetailActivity.this.finish();
            overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
