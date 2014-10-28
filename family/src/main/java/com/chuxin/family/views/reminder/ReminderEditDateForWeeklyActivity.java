package com.chuxin.family.views.reminder;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.utils.CalendarUtil;
import com.chuxin.family.widgets.NiceSelector;
import com.chuxin.family.R;

public class ReminderEditDateForWeeklyActivity extends CxRootActivity {
	private static final String TITLE = "ReminderEditDateForWeeklyView";
	private Button mReturnButton;
	private NiceSelector mDateSelector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_view_reminder_edit_weekly_date);
		init();
	}

	private void fillData() {
		ReminderController controller = ReminderController.getInstance();

		long time = controller.getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));

		int day = 1;
		try {
			day = CalendarUtil.getInstance().importDayOfWeek(
					calendar.get(Calendar.DAY_OF_WEEK));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] selection = new String[] { String.valueOf(day) };
		mDateSelector.setSelection(selection);
	}

	private boolean saveUserInput() {
		int day;
		try {
			day = CalendarUtil.getInstance().exportDayOfWeek(
					Integer.valueOf(mDateSelector.getSelection()[0]));
			ReminderController controller = ReminderController.getInstance();
			long time = controller.getTime();
			Calendar calendar = Calendar.getInstance();
	        calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.setTime(new Date(time));
			calendar.set(Calendar.DAY_OF_WEEK, day);
			controller.setTime(calendar.getTime().getTime());

			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void init() {
		mReturnButton = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mDateSelector = (NiceSelector) findViewById(R.id.cx_fa_view_reminder_edit_weekly_date__selector);

		mReturnButton.setText(getString(R.string.cx_fa_navi_save_and_back));
		mReturnButton.setPadding(30, 0, 10, 0);
		mReturnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveUserInput();
				Intent intent = new Intent(
						ReminderEditDateForWeeklyActivity.this,
						ReminderCreateActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			}

		});
		fillData();
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			saveUserInput();
			Intent intent = new Intent(
					ReminderEditDateForWeeklyActivity.this,
					ReminderCreateActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
}
