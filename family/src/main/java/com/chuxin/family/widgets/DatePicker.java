package com.chuxin.family.widgets;

import com.chuxin.family.R;
import com.chuxin.family.calendar.Lunar;
import com.chuxin.family.utils.CxLog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
/**
 * 
 * @author shichao.wang
 *
 */
public class DatePicker extends RelativeLayout {
	public static final int VISIBLE_YEAR  = 0x01;
	public static final int VISIBLE_MONTH = 0x02;
	public static final int VISIBLE_DAY   = 0x04;
	
	private static final int SPIN_DURATION = 300;
	private ImageButton mYearPlus;
	private ValidEditText  mYearText;
	private ImageButton mYearMinus;
	
	private ImageButton mMonthPlus;
	private ValidEditText  mMonthText;
	private ImageButton mMonthMinus;

	private ImageButton mDayPlus;
	private ValidEditText  mDayText;
	private ImageButton mDayMinus;
	
	private int mYear;
	private int mMonth;
	private int mDay;

	private View mSpinView = null;
	private long mLastSpinDate = 0;
	

	private OnDateChangeListener mOnDateChangeListener = null;
	
	private LinearLayout mDatePickerTitleLayout;
	private TextView mLunarTextView;
	private ImageView mSetLunarImageView;
	private boolean isShowLunar = false;
	private Calendar mCurrentCalendar = null;
	
	private Context context;
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return onTouchEvent(v, event);
		}
	};
	
	private Timer mTimer = new Timer();
	private Handler mHandler = new Handler(){  
		  
        public void handleMessage(Message msg) {  
            switch (msg.what) {      
            case 1:      
                spinerTask(true);
                break;      
            }
            super.handleMessage(msg);  
        }  
          
    };
    
    private TimerTask mTask = new TimerTask(){  
  
        public void run() {  
            Message message = new Message();      
            message.what = 1;      
            mHandler.sendMessage(message);
        }  
          
    };  
	
	public DatePicker(Context context) {
		super(context);
		this.context=context;
		init();
	}
	
	public DatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	public void setOnDateChangeListener(OnDateChangeListener listener) {
		mOnDateChangeListener = listener;
	}
	
	public void setVisibleFields(int fields) {
		if ((fields & VISIBLE_YEAR) == VISIBLE_YEAR) {
			mYearPlus.setVisibility(View.VISIBLE);
			mYearText.setVisibility(View.VISIBLE);
			mYearMinus.setVisibility(View.VISIBLE);
		} else {
			mYearPlus.setVisibility(View.GONE);
			mYearText.setVisibility(View.GONE);
			mYearMinus.setVisibility(View.GONE);			
		}
		
		if ((fields & VISIBLE_MONTH) == VISIBLE_MONTH) {
			mMonthPlus.setVisibility(View.VISIBLE);
			mMonthText.setVisibility(View.VISIBLE);
			mMonthMinus.setVisibility(View.VISIBLE);
		} else  {
			mMonthPlus.setVisibility(View.GONE);
			mMonthText.setVisibility(View.GONE);
			mMonthMinus.setVisibility(View.GONE);
		}
		
		if ((fields & VISIBLE_DAY) == VISIBLE_DAY) {
			mDayPlus.setVisibility(View.VISIBLE);
			mDayText.setVisibility(View.VISIBLE);
			mDayMinus.setVisibility(View.VISIBLE);
		} else {
			mDayPlus.setVisibility(View.GONE);
			mDayText.setVisibility(View.GONE);
			mDayMinus.setVisibility(View.GONE);
		}
	}
	
	public boolean testDate(int year, int month, int day) {
		Calendar temp = Calendar.getInstance();
		
		--month;
		
		temp.set(Calendar.YEAR, year);
		temp.set(Calendar.MONTH, month);
		temp.set(Calendar.DAY_OF_MONTH, day);
		
		if ((temp.get(Calendar.YEAR) != year) ||
				(temp.get(Calendar.MONTH) != month) ||
				(temp.get(Calendar.DAY_OF_MONTH) != day)) {
			return false;
		}
		
		return true;
	}
	
	public void reset() {
		Calendar test = Calendar.getInstance();
		mYear = test.get(Calendar.YEAR);
		mMonth = test.get(Calendar.MONTH) + 1;
		mDay = test.get(Calendar.DAY_OF_MONTH);
		updateText();
	}
	
	private void storeText() {
		int year = Integer.valueOf(mYearText.getText().toString());
		int month = Integer.valueOf(mMonthText.getText().toString());
		int day = Integer.valueOf(mDayText.getText().toString());
		
		if (testYear(year))
			mYear = year;
		
		if (testMonth(month))
			mMonth = month;
		
		if (testDay(day))
			mDay = day;
	}

	private void updateText() {
		if (!mYearText.getText().toString().equals(String.valueOf(mYear)))
			mYearText.setValue(String.valueOf(mYear));

		if (!mMonthText.getText().toString().equals(String.valueOf(mMonth)))
			mMonthText.setValue(String.valueOf(mMonth));
		
		if (!mDayText.getText().toString().equals(String.valueOf(mDay)))
			mDayText.setValue(String.valueOf(mDay));
	}

	public boolean setDate(int year, int month, int dayOfMonth) {
		if (!testDate(year, month, dayOfMonth))
			return false;
		
		mYear = year;
		mMonth = month;
		mDay = dayOfMonth;
		
		updateText();
		
		if (mOnDateChangeListener != null) {
			mOnDateChangeListener.onDateChange(this, mYear, mMonth, mDay);
		}
		
		return true;
	}
	
	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}
	
	public int getDay() {
		return mDay;
	}
	
	public boolean setYear(int year) {
		if (year < 1900) {
			year = 2100;
		} else if (year > 2100) {
			year = 1900;
		}
		
		return setDate(year, getMonth(), getDay());
	}

	public boolean setMonth(int month) {
		if (month <= 0) {
			month = 12;
		} else if (month > 12) {
			month = 1;
		}
		
		int year = getYear();
		int day = getDay();
		
		while ((day > 28) && !testDate(year, month, day)) {
			--day;
		}
		
		return setDate(year, month, day);
	}
	
	public boolean increaseDay() {
		int year = getYear();
		int month = getMonth();
		int day = getDay();
		
		++day;
		
		if (day > 31) {
			day = 1;
		} else {
			if (!testDate(year, month, day)) {
				if (testDate(year, month, day-1)) {
					day = 1;
				}
			}
		}

		return setDate(year, month, day);
	}
	
	public boolean setDay(int day) {
		
		if (day <= 0) {
			day = 31;
		} else if (day > 31) {
			day = 1;
		}

		int year = getYear();
		int month = getMonth();
		
		while ((day > 28) && !testDate(year, month, day)) {
			--day;
		}
		
		return setDate(year, month, day);
	}
	
	public boolean testYear(int year) {
		if (year < 1900) {
			return false;
		} else if (year > 2100) {
			return false;
		}
		
		return testDate(year, getMonth(), getDay());
	}
	
	public boolean testMonth(int month) {
		return testDate(getYear(), month, getDay());
	}

	public boolean testDay(int day) {
		return testDate(getYear(), getMonth(), day);
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.cx_fa_widget_datepicker, this);
		
		mYearPlus = (ImageButton)findViewById(R.id.cx_fa_widget_datepicker__year__plus);
		mYearText = (ValidEditText)findViewById(R.id.cx_fa_widget_datepicker__year__edit);
		mYearMinus = (ImageButton)findViewById(R.id.cx_fa_widget_datepicker__year__minus);
		
		mMonthPlus = (ImageButton)findViewById(R.id.cx_fa_widget_datepicker__month__plus);
		mMonthText = (ValidEditText)findViewById(R.id.cx_fa_widget_datepicker__month__edit);
		mMonthMinus = (ImageButton)findViewById(R.id.cx_fa_widget_datepicker__month__minus);

		mDayPlus = (ImageButton)findViewById(R.id.cx_fa_widget_datepicker__date__plus);
		mDayText = (ValidEditText)findViewById(R.id.cx_fa_widget_datepicker__date__edit);
		mDayMinus = (ImageButton)findViewById(R.id.cx_fa_widget_datepicker__date__minus);
		
		mDatePickerTitleLayout = (LinearLayout)findViewById(R.id.cx_fa_datepicker_title_layout);
		mDatePickerTitleLayout.setVisibility(View.GONE);
		mLunarTextView = (TextView)findViewById(R.id.cx_fa_widget_datepicker_lunar_tv);
		mLunarTextView.setText("");
		mSetLunarImageView = (ImageView)findViewById(R.id.cx_fa_widget_datepicker_setting_iv);
		LinearLayout mSetLunarLayout = (LinearLayout)findViewById(R.id.cx_fa_widget_datepicker_setting_layout);
		
		mSetLunarLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(isShowLunar){
                    isShowLunar = false;
                    hideLunar();
                    
                    
                } else {
                    isShowLunar = true;
                    showLunar(mCurrentCalendar);
                   
                }
                
                lunarImgClickListener.onOnLunarImgClick(isShowLunar);
            }
        });

		mYearPlus.setOnTouchListener(mOnTouchListener);
		mYearMinus.setOnTouchListener(mOnTouchListener);
		mMonthPlus.setOnTouchListener(mOnTouchListener);
		mMonthMinus.setOnTouchListener(mOnTouchListener);
		mDayPlus.setOnTouchListener(mOnTouchListener);
		mDayMinus.setOnTouchListener(mOnTouchListener);
		
		mYearText.setValidation(new ValidEditText.Validation() {
			
			@Override
			public boolean valid(String s) {
				int num = 0;
				try {
					num = Integer.valueOf(s);
				} catch(Exception e) {
					return false;
				}
				
				return setYear(num);
			}
		});
		
		mMonthText.setValidation(new ValidEditText.Validation() {
			
			@Override
			public boolean valid(String s) {
				int num = 0;
				try {
					num = Integer.valueOf(s);
				} catch(Exception e) {
					return false;
				}
				
				return setMonth(num);
			}
		});

		mDayText.setValidation(new ValidEditText.Validation() {
			
			@Override
			public boolean valid(String s) {
				int num = 0;
				try {
					num = Integer.valueOf(s);
				} catch(Exception e) {
					return false;
				}
				
				return setDay(num);
			}
		});
		
		reset();
		mTimer.schedule(mTask, Calendar.getInstance().getTime(), SPIN_DURATION);
		

	}
	
	private void stopSpinTask() {
		mSpinView = null;
	}
	
	private void spinerTask(boolean timerModel) {
		if (mSpinView == null)
			return;
		
		if (timerModel) {
			if ((System.currentTimeMillis() - mLastSpinDate) < SPIN_DURATION)
				return;
		}
		
		View v = mSpinView;

		storeText();
		if (v == mYearPlus) {
			setYear(getYear() + 1);
		} else if (v == mYearMinus) {
			setYear(getYear() - 1);
		} else if (v == mMonthPlus) {
			setMonth(getMonth() + 1);
		} else if (v == mMonthMinus) {
			setMonth(getMonth() - 1);
		} else if (v == mDayPlus) {
			increaseDay();
		} else if (v == mDayMinus) {
			setDay(getDay() - 1);
		} else {
			stopSpinTask();
			return;
		}
		
		mLastSpinDate = System.currentTimeMillis();
	}
	
	private boolean onTouchEvent(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mSpinView = v;
			spinerTask(false);
			break;
		default:
			stopSpinTask();
			break;
		}
		return false;
	}
	
	public void showLunar(Calendar cal){
	    this.mCurrentCalendar = cal;
	    Lunar lunar = new Lunar(cal);
	    mLunarTextView.setVisibility(View.VISIBLE);
	    mLunarTextView.setText(lunar.getLunarMontthAndDayString());
	    mSetLunarImageView.setImageResource(R.drawable.calendar_set_on);   
	}
	
	public void setLunar(Calendar cal){
	    this.mCurrentCalendar = cal;
	    Lunar lunar = new Lunar(cal);
	    mLunarTextView.setText(lunar.getLunarMontthAndDayString());
	}
	
	public void hideLunar(){
	    mLunarTextView.setVisibility(View.INVISIBLE);
	    mSetLunarImageView.setImageResource(R.drawable.calendar_set_off);
	}
	
	public void setChangeToLunar(){
		new Handler(context.getMainLooper()){
			public void handleMessage(Message msg) {
				mDatePickerTitleLayout.setVisibility(View.VISIBLE);	
			};
		}.sendEmptyMessage(0);   
		
	}
	
	public void hideLunarLayout(){
		new Handler(context.getMainLooper()){
			public void handleMessage(Message msg) {
				mDatePickerTitleLayout.setVisibility(View.GONE);
			};
		}.sendEmptyMessage(0);   
	}
	
	public interface OnLunarImgClickListener{
		 void onOnLunarImgClick(boolean isSelected);
	}
	
	private OnLunarImgClickListener lunarImgClickListener;
	
	public void setOnLunarImgClickListener(OnLunarImgClickListener listener){
		lunarImgClickListener=listener;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
