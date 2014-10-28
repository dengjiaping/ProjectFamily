package com.chuxin.family.views.reminder;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.models.Reminder;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.DatePicker;
import com.chuxin.family.widgets.OnDateChangeListener;
import com.chuxin.family.widgets.QuickMessage;
import com.chuxin.family.widgets.TimePicker;
import com.chuxin.family.R;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReminderCreateActivity extends CxRootActivity implements OnClickListener {
	private static final String TAG = "ReminderCreateActivity";

	private Button mReturnButton;
	private Button mSaveButton;
	private TextView mNaviTitle;
	private RelativeLayout mTitleEntry;
	private RelativeLayout mTargetEntry;
	private RelativeLayout mPeriodEntry;
	private RelativeLayout mDateEntry;
	private RelativeLayout mTimeEntry;
	private RelativeLayout mAdvanceEntry;

//	private TextView mNowField;
	private TextView mTitleField;
	private TextView mTargetField;
	private TextView mPeriodField;
	private TextView mDateField;
	private TextView mTimeField;
	private TextView mAdvanceField;

	private PopupWindow mDatePickerDialog = null;
	private PopupWindow mDatePickerDialogWithMonthFixed = null;
	private PopupWindow mDatePickerDialogWithAnnuallyFixed = null;
	private PopupWindow mTimePickerDialog = null;

	private HashMap<Integer, String> mTargetMapping = new HashMap<Integer, String>();
	private HashMap<Integer, String> mPeriodMapping = new HashMap<Integer, String>();
	private HashMap<Integer, String> mAdvanceMapping = new HashMap<Integer, String>();

	private ReminderController mController = null;

//	private OnDateSetListener callBack;

	public static boolean isModifyData = false;
	private ReminderDisplayUtility mDisplayUtility = null;
	private static ReminderCreateActivity mReminderCreateActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_view_reminder_edit_reminder);
		init();
	}

	private void init() {
		mDisplayUtility = new ReminderDisplayUtility(getResources());
		prepare();

		mReturnButton = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mSaveButton = (Button) findViewById(R.id.cx_fa_activity_title_more);
		mSaveButton.setVisibility(View.VISIBLE);
		
		mReturnButton.setText(getString(R.string.cx_fa_navi_back));
		mSaveButton.setText(getString(R.string.cx_fa_navi_save));
		
		mNaviTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		if (ReminderController.getInstance().getId().length() > 0){ 
		    mNaviTitle.setText(R.string.cx_fa_nls_edit_reminder);
		} else {
		    mNaviTitle.setText(R.string.cx_fa_nls_add_reminder);
		}
		
		mTitleEntry = (RelativeLayout) findViewById(R.id.cx_fa_view_reminder_edit_reminder__title_item);
		mTargetEntry = (RelativeLayout) findViewById(R.id.cx_fa_view_reminder_edit_reminder__target_item);
		mPeriodEntry = (RelativeLayout) findViewById(R.id.cx_fa_view_reminder_edit_reminder__period_item);
		mDateEntry = (RelativeLayout) findViewById(R.id.cx_fa_view_reminder_edit_reminder__date_item);
		mTimeEntry = (RelativeLayout) findViewById(R.id.cx_fa_view_reminder_edit_reminder__time_item);
		mAdvanceEntry = (RelativeLayout) findViewById(R.id.cx_fa_view_reminder_edit_reminder__advance_item);

//		mNowField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__now);
		mTitleField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__title_content);
		mTargetField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__target_content);
		mPeriodField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__period_content);
		mDateField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__date_content);
		mTimeField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__time_content);
		mAdvanceField = (TextView) findViewById(R.id.cx_fa_view_reminder_edit_reminder__advance_content);

		fillData();
		installListeners();

		// CleanRightPagesReceiver.clean(ReminderCreateActivity.this);
	}

	private void prepare() {
		// targets
		int[] targetValues = getResources().getIntArray(
				R.array.cx_fa_ints_reminder_target_values);
		String[] targetDisplays = getResources().getStringArray(
				R.array.cx_fa_strs_reminder_target_displays);
		for (int i = 0; i < targetValues.length; i++) {
			mTargetMapping.put(Integer.valueOf(targetValues[i]),
					targetDisplays[i]);
		}

		// period
		int[] periodValues = getResources().getIntArray(
				R.array.cx_fa_ints_reminder_period_values);
		String[] periodDisplays = getResources().getStringArray(
				R.array.cx_fa_strs_reminder_period_displays);

		for (int i = 0; i < periodValues.length; i++) {
			mPeriodMapping.put(Integer.valueOf(periodValues[i]),
					periodDisplays[i]);
		}

		// advance
		int[] advanceValues = getResources().getIntArray(
				R.array.cx_fa_ints_reminder_advance_values);
		String[] advanceDisplays = getResources().getStringArray(
				R.array.cx_fa_strs_reminder_advance_displays);

		for (int i = 0; i < periodValues.length; i++) {
			mAdvanceMapping.put(Integer.valueOf(advanceValues[i]),
					advanceDisplays[i]);
		}

	}

	private void fillData() {
		mController = ReminderController.getInstance();

		mTitleField.setText(mController.getTitle());
		mTargetField.setText(mTargetMapping.get(mController.getTarget()));
		// if (ReminderController.getInstance().getId().length() > 0) {
		// mTargetEntry.setVisibility(View.GONE);
		// mTargetField.setVisibility(View.GONE);
		// } else {
		// mTargetEntry.setVisibility(View.VISIBLE);
		// mTargetField.setVisibility(View.VISIBLE);
		// }
		mPeriodField.setText(mPeriodMapping.get(mController.getPeriod()));

		if (mController.getPeriod() == ReminderController.sReminderPeriodDaily) {
			mDateEntry.setVisibility(View.GONE);
		}
		if (mController.getPeriod() == ReminderController.sReminderPeriodCustomize) {
			mAdvanceEntry.setVisibility(View.GONE);
		}
		mAdvanceField.setText(mAdvanceMapping.get(mController.getAdvance()));
		updateDateFields();
	}

	public void installListeners() {
		mReturnButton.setOnClickListener(this);
		mTitleEntry.setOnClickListener(this);
		mTargetEntry.setOnClickListener(this);
		mPeriodEntry.setOnClickListener(this);
		mDateEntry.setOnClickListener(this);
		mTimeEntry.setOnClickListener(this);
		mAdvanceEntry.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
	}

	private void updateDateFields() {
		Date date = new Date(mController.getTime());
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		if (mController.getPeriod() == ReminderController.sReminderPeriodWeekly) {
			// int week = c.get(Calendar.DAY_OF_WEEK) - 1;
			try {
				int day = CalendarUtil.getInstance().importDayOfWeek(
						c.get(Calendar.DAY_OF_WEEK)) - 1;
				String[] weekDisplays = getResources().getStringArray(
						R.array.cx_fa_strs_reminder_date_weekly_displays);
				mDateField.setText(weekDisplays[day]);
				// mDateField.setText(dateFormat.format(date));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (mController.getPeriod() == ReminderController.sReminderPeriodMonthly) {			
			mDateField.setText(String.format(
					getResources().getString(
							R.string.cx_fa_nls_reminder_everymonth_format),
					c.get(Calendar.DAY_OF_MONTH)));
		} else if (mController.getPeriod() == ReminderController.sReminderPeriodAnnually) {
			SimpleDateFormat dateFormatMonthAndDay = new SimpleDateFormat(
					"MM-dd");
			mDateField.setText(dateFormatMonthAndDay.format(date));
		} else if (mController.getPeriod() == ReminderController.sReminderPeriodCustomize) {
			mDateField
					.setText(mDisplayUtility
							.createNLSReminderPeriodForCustomizeDisplays(ReminderController
									.getInstance().getCustomize()));
		} else {
			mDateField.setText(dateFormat.format(date));
		}
		mTimeField.setText(timeFormat.format(date));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// dismiss PopupWindow dialog before destory this activity
		if (mDatePickerDialogWithMonthFixed != null
				&& mDatePickerDialogWithMonthFixed.isShowing()) {
			mDatePickerDialogWithMonthFixed.dismiss();
		}
		if (mDatePickerDialog != null && mDatePickerDialog.isShowing()) {
			mDatePickerDialog.dismiss();
		}
		if (mTimePickerDialog != null && mTimePickerDialog.isShowing()) {
			mTimePickerDialog.dismiss();
		}
	}

	/**
	 * 每月提醒只显示的每月有多少天的dialog
	 */
	private void showDatePickerDialogWithMonthFixed() {
		final DatePicker datePicker = new DatePicker(
				ReminderCreateActivity.this);
		
		if (mDatePickerDialogWithMonthFixed == null) {
			long time = mController.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));
			datePicker.setVisibleFields(DatePicker.VISIBLE_DAY);
			datePicker.setDate(calendar.get(Calendar.YEAR),
					1, // use the 1st month
					calendar.get(Calendar.DAY_OF_MONTH));
			datePicker.setOnDateChangeListener(new OnDateChangeListener() {

				@Override
				public void onDateChange(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {

					long time = mController.getTime();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(time));
					calendar.set(Calendar.MONTH, 0);
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

					Date now = new Date();
					// while (calendar.getTime().before(now)
					// || calendar.get(Calendar.DAY_OF_MONTH) !=
					// dayOfMonth) {
					// loop until find one day which satisfy:
					// 1) the date is the exactly the day-of-month
					// user specified;
					// 2) the date is after now;
					// calendar.add(Calendar.MONTH, 1);
					// }
					mController.setTime(calendar.getTime().getTime());
					isModifyData = true;
					updateDateFields();
				}

				@Override
				public void onTimeChange(TimePicker view, int hourOfDay,
						int minute) {
					;
				}
				
			});

			mDatePickerDialogWithMonthFixed = new PopupWindow(datePicker,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mDatePickerDialogWithMonthFixed.setWidth(LayoutParams.MATCH_PARENT);
			mDatePickerDialogWithMonthFixed
					.setHeight(LayoutParams.WRAP_CONTENT);
			mDatePickerDialogWithMonthFixed
					.setBackgroundDrawable(getResources().getDrawable(
							R.color.cx_fa_co_datepicker_dialog_background));
			mDatePickerDialogWithMonthFixed.setTouchable(true);
			mDatePickerDialogWithMonthFixed.setOutsideTouchable(true);

		} else {
			if (mDatePickerDialogWithMonthFixed.isShowing()) {
				mDatePickerDialogWithMonthFixed.dismiss();
				return;
			}
		}
		mDatePickerDialogWithMonthFixed.showAtLocation(datePicker,
				Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 每年提醒只显示的月和天的dialog
	 */
	private void showDatePickerDialogWithAnnuallyFixed() {
		final DatePicker datePicker = new DatePicker(
				ReminderCreateActivity.this);
		if (mDatePickerDialogWithAnnuallyFixed == null) {
			long time = mController.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));

			datePicker.setVisibleFields(DatePicker.VISIBLE_DAY|DatePicker.VISIBLE_MONTH);
			datePicker.setDate(calendar.get(Calendar.YEAR),
								calendar.get(Calendar.MONTH+1),
								calendar.get(Calendar.DAY_OF_MONTH));
			datePicker.setOnDateChangeListener(new OnDateChangeListener() {

				@Override
				public void onDateChange(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {

					long time = mController.getTime();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(time));
					calendar.set(Calendar.MONTH, monthOfYear-1);
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

					mController.setTime(calendar.getTime().getTime());
					isModifyData = true;
					updateDateFields();
				}

				@Override
				public void onTimeChange(TimePicker view, int hourOfDay,
						int minute) {
					;
				}
				
			});



			mDatePickerDialogWithAnnuallyFixed = new PopupWindow(datePicker,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mDatePickerDialogWithAnnuallyFixed
					.setWidth(LayoutParams.MATCH_PARENT);
			mDatePickerDialogWithAnnuallyFixed
					.setHeight(LayoutParams.WRAP_CONTENT);
			mDatePickerDialogWithAnnuallyFixed
					.setBackgroundDrawable(getResources().getDrawable(
							R.color.cx_fa_co_datepicker_dialog_background));
			mDatePickerDialogWithAnnuallyFixed.setTouchable(true);
			mDatePickerDialogWithAnnuallyFixed.setOutsideTouchable(true);

		} else {
			if (mDatePickerDialogWithAnnuallyFixed.isShowing()) {
				mDatePickerDialogWithAnnuallyFixed.dismiss();
				return;
			}
		}
		mDatePickerDialogWithAnnuallyFixed.showAtLocation(datePicker,
				Gravity.BOTTOM, 0, 0);
	}

	/**
	 * show time dialog at the bottom
	 */
	private void showTimePickerDialog() {
		TimePicker timePicker = new TimePicker(ReminderCreateActivity.this);
		if (mTimePickerDialog == null) {
			long time = mController.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));

			mTimePickerDialog = new PopupWindow(timePicker,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mTimePickerDialog.setWidth(LayoutParams.MATCH_PARENT);
			mTimePickerDialog.setHeight(LayoutParams.WRAP_CONTENT);
			mTimePickerDialog.setBackgroundDrawable(getResources().getDrawable(
					R.color.cx_fa_co_datepicker_dialog_background));
			mTimePickerDialog.setTouchable(true);
			mTimePickerDialog.setOutsideTouchable(true);

			timePicker.setHourOfDay(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setMinute(calendar.get(Calendar.MINUTE));

			timePicker.setOnTimeChangeListener(new OnDateChangeListener() {

				@Override
				public void onTimeChange(TimePicker view, int hourOfDay, int minute) {
					long time = mController.getTime();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(time));

					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minute);

					// if (calendar.getTime().before(new Date()) &&
					// (mController.getPeriod() ==
					// mController.sReminderPeriodOnce )) {
					// Toast.makeText(ReminderCreateActivity.this,
					// "Invalid Request", Toast.LENGTH_SHORT).show();
					// } else {
					mController.setTime(calendar.getTime().getTime());
					isModifyData = true;
					updateDateFields();
					// }
				}

				@Override
				public void onDateChange(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					;
				}

			});
		} else {
			if (mTimePickerDialog.isShowing()) {
				mTimePickerDialog.dismiss();
				return;
			}
		}
		mTimePickerDialog.showAtLocation(timePicker, Gravity.BOTTOM, 0, 0);
	}

	/**
	 * show date dialog at the bottom
	 */
	private void showDatePickerDialog() {

		DatePicker datePicker = new DatePicker(ReminderCreateActivity.this);
		if (mDatePickerDialog == null) {
			long time = mController.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));

			mDatePickerDialog = new PopupWindow(datePicker,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mDatePickerDialog.setWidth(LayoutParams.MATCH_PARENT);
			mDatePickerDialog.setHeight(LayoutParams.WRAP_CONTENT);
			mDatePickerDialog.setBackgroundDrawable(getResources().getDrawable(
					R.color.cx_fa_co_datepicker_dialog_background));
			mDatePickerDialog.setTouchable(true);
			mDatePickerDialog.setOutsideTouchable(true);

			datePicker.setDate(calendar.get(Calendar.YEAR), 
								calendar.get(Calendar.MONTH)+1, 
								calendar.get(Calendar.DAY_OF_MONTH));
			
			datePicker.setOnDateChangeListener( new OnDateChangeListener() {

						@Override
						public void onDateChange(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							long time = mController.getTime();
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(new Date(time));

							calendar.set(Calendar.YEAR, year);
							calendar.set(Calendar.MONDAY, monthOfYear-1);
							calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

							// if (calendar.getTime().before(new Date())) {
							// Toast.makeText(ReminderCreateActivity.this,
							// "Invalid Request", Toast.LENGTH_SHORT)
							// .show();
							// } else {
							mController.setTime(calendar.getTime().getTime());
							isModifyData = true;
							updateDateFields();
							// }
						}

						@Override
						public void onTimeChange(TimePicker view,
								int hourOfDay, int minute) {
							
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

	@Override
	public void onClick(View v) {
		Toast toast = null;
		switch (v.getId()) {
		case R.id.cx_fa_activity_title_back:
			if (isModifyData) {
				popIsSaveDialog();
			} else {
				startActivity(CxReminderList.class);
			    ReminderCreateActivity.this.finish();
			}
			break;
		case R.id.cx_fa_activity_title_more:
		    mSaveButton.setClickable(false);
			 saveReminder(false);
//			if( mController.getPeriod() == mController.sReminderPeriodCustomize ){
//				saveReminder();
//			} else if (mController.checkTimeIsLegal()) {
//				saveReminder();
//			} else {
//				String msg = getResources().getString(
//						R.string.cx_fa_reminder_checktime_legal_msg);
//				toast = Toast.makeText(getApplicationContext(), msg,
//						Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				LinearLayout toastView = (LinearLayout) toast.getView();
//				ImageView imageCodeProject = new ImageView(
//						getApplicationContext());
//				imageCodeProject.setImageResource(R.drawable.cancel_button);
//				toastView.addView(imageCodeProject, 0);
//				toast.show();
//			}
			break;
		case R.id.cx_fa_view_reminder_edit_reminder__title_item:
			isModifyData = true;
			startActivity(ReminderEditTitleActivity.class);
			break;
		case R.id.cx_fa_view_reminder_edit_reminder__target_item:
			isModifyData = true;
			startActivity(ReminderEditTargetActivity.class);
			break;
		case R.id.cx_fa_view_reminder_edit_reminder__period_item:
			isModifyData = true;
			startActivity(ReminderEditPeriodActivity.class);
			break;
		case R.id.cx_fa_view_reminder_edit_reminder__date_item:
			if (mController.getPeriod() == ReminderController.sReminderPeriodMonthly) {
				showDatePickerDialogWithMonthFixed();
			} else if (mController.getPeriod() == ReminderController.sReminderPeriodWeekly) {
				startActivity(ReminderEditDateForWeeklyActivity.class);
			} else if (mController.getPeriod() == ReminderController.sReminderPeriodAnnually) {
				showDatePickerDialogWithAnnuallyFixed();
			} else if (mController.getPeriod() == ReminderController.sReminderPeriodCustomize) {
				startActivity(ReminderEditDateForCustomActivtiy.class);
			} else {
				showDatePickerDialog();
			}
			break;
		case R.id.cx_fa_view_reminder_edit_reminder__time_item:
			showTimePickerDialog();
			break;
		case R.id.cx_fa_view_reminder_edit_reminder__advance_item:
			isModifyData = true;
			startActivity(ReminderEditAdvanceActivity.class);
			break;
		default:
			break;
		}
	}

	/**
	 * pop is save dialog
	 */
	private void popIsSaveDialog() {
		
		DialogUtil du = DialogUtil.getInstance();
		du.setOnSureClickListener(new OnSureClickListener() {
			
			@Override
			public void surePress() {
				isModifyData = false;
				startActivity(CxReminderList.class);
			    ReminderCreateActivity.this.finish();
				
			}
		});
		du.getSimpleDialog(this, null, getString(R.string.cx_fa_reminder_is_save_data_msg), null, null).show();
		
		
//		AlertDialog.Builder builder = new Builder(ReminderCreateActivity.this);
//		builder.setMessage(R.string.cx_fa_reminder_is_save_data_msg);
//		builder.setPositiveButton(
//				getResources().getString(R.string.cx_fa_confirm_text),
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						isModifyData = false;
//						dialog.dismiss();
//						startActivity(RkReminderList.class);
//					    ReminderCreateActivity.this.finish();
//					}
//				});
//		builder.setNegativeButton(
//				getResources().getString(R.string.cx_fa_cancel_button_text),
//				null);
//		builder.create().show();
	}

	/**
	 * check settime is legal
	 * 
	 * @return true:legal�?false:illegal
	 */
	/*
	 * private boolean checkTimeIsLegal() { try {
	 * if(mController.adjustTime()!=-2){ int realTime =
	 * (int)(mController.getRealTime()/1000); int now = (int)
	 * (System.currentTimeMillis()/1000); if( realTime > now ){ return true; } }
	 * } catch (Exception e) { e.printStackTrace(); return false; } return
	 * false; }
	 */

	/**
	 * save the reminder
	 */
	private void saveReminder(final boolean isBirthdayReminder) {
		CxLoadingUtil.getInstance().showLoading(ReminderCreateActivity.this, true);
		try {
			// check if the user-set time valid;
			if (mController.getPeriod() == ReminderController.sReminderPeriodOnce) {
				Calendar now = Calendar.getInstance();
				Calendar remindTime = Calendar.getInstance();
				remindTime.setTimeInMillis(mController.getRealTime());
				
				if (remindTime.before(now)) {
					QuickMessage.error(this, R.string.cx_fa_nls_reminder_invalid_date_for_creation);
					CxLoadingUtil.getInstance().dismissLoading();
					mSaveButton.setClickable(true);
					return;
				}
			}
			
			if (mController.getId().length() > 0) {
				Log.v(TAG, "cancle flag" + mController.getFlag());
				mController.cancelAlarmReminder(ReminderCreateActivity.this,
						mController.getFlag());
			}
			if (mController.getIsDelay()) {
				try {
					Reminder reminder = mController.getReminder();
					JSONObject reminderObj = reminder.mData;
					reminderObj.put(Reminder.TAG_DELAY, true);
					reminderObj.put(Reminder.TAG_BASE_TS, (int)(mController.getTime()/1000));
					reminderObj.put(Reminder.TAG_TARGET, ReminderController.sReminderTargetMyself);
					String reminderOlderId = reminder.mId;
					int reminderOlderFlag = reminder.getFlag();
					Reminder newreminder = new Reminder(reminderObj, ReminderCreateActivity.this);
					newreminder.setFlag((int)(mController.getTime()/1000));
					newreminder.mId = reminder.mId + "_1"; // 延迟提醒,本地reminder
														// id,默认后面加上_1,以免被网络的覆盖
					newreminder.put();
					delReminder(reminderOlderId);
					ReminderController controller = ReminderController
							.getInstance();
					controller.cancelAlarmReminder(this, reminderOlderFlag);
					controller.setAlarmReminder(this,
							newreminder.getBaseTimestamp(), newreminder.mId,
							newreminder.getPeriodType(), newreminder.getFlag(), newreminder.getTitle());
					isModifyData = false;
//					startActivity(RkMainActivity.class);
					CxLoadingUtil.getInstance().dismissLoading();
					startActivity(CxReminderList.class);
				    ReminderCreateActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
					CxLoadingUtil.getInstance().dismissLoading();
				}
			} else {
				CxLog.d(TAG, "save privious time=" + ReminderDisplayUtility.getDate(mController.getRealTime()));
				mController.submitReminderChanges(this, new JSONCaller() {

					@Override
					public int call(Object result) {
						try {
							JSONObject reminderObj = (JSONObject) result;
							reminderObj.put(Reminder.TAG_DELAY, false);
							Reminder reminder = new Reminder(reminderObj,
									ReminderCreateActivity.this);
							CxLog.d(TAG, "after request api time=" + ReminderDisplayUtility.getDate((long)reminder.getBaseTimestamp()*1000));
							CxLog.v(TAG, "local remindId: " + reminder.mId);
							CxLog.v(TAG,
									"local remind get flag: "
											+ reminder.getFlag());
							if (reminder.adjust() != -1) {
								reminder.setFlag(reminder.getBaseTimestamp());
								reminder.put();
								CxLog.d(TAG, "after adjust time=" + ReminderDisplayUtility.getDate((long)reminder.getBaseTimestamp()*1000));
								if(isBirthdayReminder){
									SharedPreferences reminderBirthdaySf = getSharedPreferences(CxGlobalConst.S_BIRTHDAY_REMINDER_PREFS_NAME, 0);
									reminderBirthdaySf.edit().putString(CxGlobalConst.S_BIRTHDAY_REMINDER_ID, reminder.getId()).commit();
									reminderBirthdaySf.edit().putInt(CxGlobalConst.S_BIRTHDAY_REMINDER_FLAG, reminder.getFlag()).commit();
								}
								//回到列表页面 拉列表时会创建
//								mController.setAlarmReminder(
//										ReminderCreateActivity.this,
//										reminder.getRealTimestamp(),
//										reminder.mId, reminder.getPeriodType(),
//										reminder.getFlag());
								isModifyData = false;
					            mSaveButton.setClickable(true);
	                            android.os.Message reminderMessage = CxReminderList.mReminderListAllHandler
	                                        .obtainMessage(CxReminderList.UPDATE_LOCAL_REMINDER_DATA);
	                            reminderMessage.sendToTarget();
					            CxLoadingUtil.getInstance().dismissLoading();
								startActivity(CxReminderList.class);
							    ReminderCreateActivity.this.finish();
							} else {
								reminder.drop();
								 mSaveButton.setClickable(true);
								CxLoadingUtil.getInstance().dismissLoading();
							}

						} catch (Exception e) {
							CxLog.e(TAG,
									"Error: failed to get object from create reminder result");
							e.printStackTrace();
						}
						return 0;
					}

				});
			}
		} catch (Exception e) {
			CxLog.e(TAG,
					"Failed to submitReminderChanges due to " + e.toString());
			e.printStackTrace();
			CxLoadingUtil.getInstance().dismissLoading();
		}
	}

	private void delReminder(String reminderId) {
		Reminder remind = new Reminder();
		remind.drop(reminderId);
	}

	/**
	 * common start activity
	 * 
	 * @param activityClass
	 */
	private void startActivity(final Class<?> activityClass) {
		startActivity(new Intent(this, activityClass));
		if (activityClass == CxReminderList.class) {
			overridePendingTransition(
					R.anim.tran_pre_in,
					R.anim.tran_pre_out);
		} else {
			overridePendingTransition(R.anim.tran_next_in,
					R.anim.tran_next_out);
		}
		finish();
	}
	public static ReminderCreateActivity getInstance(){
		if(null == mReminderCreateActivity){
			mReminderCreateActivity = new ReminderCreateActivity();
		}
		return mReminderCreateActivity;
	}
	public void createBirthdayReminder(long time){
		SharedPreferences sf = getSharedPreferences(CxGlobalConst.S_BIRTHDAY_REMINDER_PREFS_NAME, 0);
		String birthdayReminderId = sf.getString(CxGlobalConst.S_BIRTHDAY_REMINDER_ID, null);
		int birthdayReminderFlag = sf.getInt(CxGlobalConst.S_BIRTHDAY_REMINDER_FLAG, 0);
		if(null != birthdayReminderId && birthdayReminderId.length() > 0){
			dropBirthdayReminder(birthdayReminderId, birthdayReminderFlag);
		}
		mController.setTime(time);
		mController.setTarget(ReminderController.sReminderTargetMyself);
		mController.setPeriod(ReminderController.sReminderPeriodAnnually);
		mController.setAdvance(ReminderController.sReminderAdvance3Day);
		mController.setTitle(getResources().getString(CxResourceString.getInstance().str_reminder_birthday_tip));
		saveReminder(true);
	}

	private void dropBirthdayReminder(String birthdayReminderId,
			int birthdayReminderFlag) {
		delReminder(birthdayReminderId);
		ReminderController controller = ReminderController
				.getInstance();
		controller.cancelAlarmReminder(this, birthdayReminderFlag);
	}
	
	// by wentong.men 131109
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			if (isModifyData) {
				popIsSaveDialog();
			} else {
				startActivity(CxReminderList.class);
			    ReminderCreateActivity.this.finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};

}
