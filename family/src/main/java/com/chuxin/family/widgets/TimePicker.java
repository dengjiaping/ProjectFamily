package com.chuxin.family.widgets;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.chuxin.family.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class TimePicker extends RelativeLayout {
	
	private static final int SPIN_DURATION = 100;
	private ImageButton mHourPlus;
	private ValidEditText  mHourText;
	private ImageButton mHourMinus;
	
	private ImageButton mMinutePlus;
	private ValidEditText  mMinuteText;
	private ImageButton mMinuteMinus;

	private Button mMorningButton;
	
	private int mHour;
	private int mMinute;
	private boolean mMorning;

	private View mSpinView = null;
	private long mLastSpinDate = 0;
	private int mSpinTimes = 0;
	
	private OnDateChangeListener mOnTimeChangeListener = null;
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return onTouchHandler(v, event);
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
	
	public TimePicker(Context context) {
		super(context);
		init();
	}
	
	public TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	public void setOnTimeChangeListener(OnDateChangeListener listener) {
		mOnTimeChangeListener = listener;
	}
	
	public boolean testTime(int hour, int minute, boolean isMorning) {
		Calendar temp = Calendar.getInstance();
		
		if (!isMorning)
			hour += 12;
		
		temp.set(Calendar.HOUR_OF_DAY, hour);
		temp.set(Calendar.MINUTE, minute);
		
		if ((temp.get(Calendar.HOUR_OF_DAY) != hour) ||
				(temp.get(Calendar.MINUTE) != minute)) {
			return false;
		}
		
		return true;
	}
	
	public void reset() {
		Calendar test = Calendar.getInstance();
		mHour = test.get(Calendar.HOUR);
		mMinute = test.get(Calendar.MINUTE) + 1;
		mMorning = (test.get(Calendar.AM_PM) == Calendar.AM);
		updateText();
	}
	
	private void storeText() {
		int hour = Integer.valueOf(mHourText.getText().toString());
		int minute = Integer.valueOf(mMinuteText.getText().toString());
		
		if (testHour(hour))
			mHour = hour;
		
		if (testMinute(minute))
			mMinute = minute;		
	}

	private void updateText() {
		if (!mHourText.getText().toString().equals(String.valueOf(mHour)))
			mHourText.setValue(String.valueOf(mHour));

		if (!mMinuteText.getText().toString().equals(String.valueOf(mMinute)))
			mMinuteText.setValue(String.valueOf(mMinute));
		
		if (mMorning)
			mMorningButton.setText(R.string.cx_fa_nls_datepicker_monring);
		else
			mMorningButton.setText(R.string.cx_fa_nls_datepicker_afternoon);
	}

	public boolean setTime(int hour, int minute, boolean morning) {
		if (!testTime(hour, minute, morning))
			return false;
		
		mHour = hour;
		mMinute = minute;
		mMorning = morning;
		
		updateText();
		
		if (mOnTimeChangeListener != null) {
			if (!morning)
				hour += 12;
			
			mOnTimeChangeListener.onTimeChange(this, hour, minute);
		}
		
		return true;
	}
	
	public int getHour() {
		return mHour;
	}

	public int getHourOfDay() {
		if (mMorning)
			return mHour;
		else
			return mHour + 12;
	}
	
	public int getMinute() {
		return mMinute;
	}
	
	public boolean isMorning() {
		return mMorning;
	}
	
	public boolean setHourOfDay(int hour) {

		if (hour > 23)
			return false;
		
		boolean morning = true;
		if (hour > 11) {
			hour -= 12;
			morning = false;
		}
		
		return setTime(hour, getMinute(), morning);
	}
	
	public boolean setHour(int hour) {
		boolean morning = isMorning();
		if (hour < 0) {
			hour = 11;
			morning = !morning;
		} else if (hour > 11) {
			hour = 0;
			morning = !morning;
		}
		
		return setTime(hour, getMinute(), morning);
	}

	public boolean setMinute(int minute) {
		if (minute < 0) {
			minute = 59;
		} else if (minute > 59) {
			minute = 0;
		}

		return setTime(getHour(), minute, isMorning());
	}
	
	public boolean setMonring(boolean morning) {
		return setTime(getHour(), getMinute(), morning);
	}
	
	public boolean testHour(int hour) {
		if (hour < 0) {
			return false;
		} else if (hour > 11) {
			return false;
		}
		
		return testTime(hour, getMinute(), isMorning());
	}
	
	public boolean testMinute(int minute) {
		if (minute < 0)
			return false;
		if (minute > 59)
			return false;
		return testTime(getHour(), minute, isMorning());
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.cx_fa_widget_timepicker, this);
		
		mHourPlus = (ImageButton)findViewById(R.id.cx_fa_widget_timepicker__hour__plus);
		mHourText = (ValidEditText)findViewById(R.id.cx_fa_widget_timepicker__hour__edit);
		mHourMinus = (ImageButton)findViewById(R.id.cx_fa_widget_timepicker__hour__minus);
		
		mMinutePlus = (ImageButton)findViewById(R.id.cx_fa_widget_timepicker__minute__plus);
		mMinuteText = (ValidEditText)findViewById(R.id.cx_fa_widget_timepicker__minute__edit);
		mMinuteMinus = (ImageButton)findViewById(R.id.cx_fa_widget_timepicker__minute__minus);

		mMorningButton = (Button)findViewById(R.id.cx_fa_widget_timepicker__morning__button);

		mHourPlus.setOnTouchListener(mOnTouchListener);
		mHourMinus.setOnTouchListener(mOnTouchListener);
		mMinutePlus.setOnTouchListener(mOnTouchListener);
		mMinuteMinus.setOnTouchListener(mOnTouchListener);
		
		mHourText.setValidation(new ValidEditText.Validation() {
			
			@Override
			public boolean valid(String s) {
				int num = 0;
				try {
					num = Integer.valueOf(s);
				} catch(Exception e) {
					return false;
				}
				
				return setHour(num);
			}
		});
		
		mMinuteText.setValidation(new ValidEditText.Validation() {
			
			@Override
			public boolean valid(String s) {
				int num = 0;
				try {
					num = Integer.valueOf(s);
				} catch(Exception e) {
					return false;
				}
				
				return setMinute(num);
			}
		});

		mMorningButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View f) {
				setMonring(!isMorning());
			}
			
		});
		
		reset();
		mTimer.schedule(mTask, Calendar.getInstance().getTime(), SPIN_DURATION);
	}
	
	private void stopSpinTask() {
		mSpinTimes = 0;
		mSpinView = null;
	}
	
	private void spinerTask(boolean timerModel) {
		if (mSpinView == null)
			return;
		
		if (timerModel) {
			if ((System.currentTimeMillis() - mLastSpinDate) < SPIN_DURATION)
				return;
		}
		
		mSpinTimes++;
		View v = mSpinView;

		storeText();
		if (v == mHourPlus) {
			if (mSpinTimes % 2 == 1)
				setHour(getHour() + 1);
		} else if (v == mHourMinus) {
			if (mSpinTimes % 2 == 1)
				setHour(getHour() - 1);
		} else if (v == mMinutePlus) {
			setMinute(getMinute() + 1);
		} else if (v == mMinuteMinus) {
			setMinute(getMinute() - 1);
		} else {
			stopSpinTask();
			return;
		}
		
		mLastSpinDate = System.currentTimeMillis();
	}
	
	private boolean onTouchHandler(View v, MotionEvent event) {
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
	
	
}
