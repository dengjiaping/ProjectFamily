package com.chuxin.family.views.reminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.widgets.NiceEditText;
import com.chuxin.family.widgets.QuickMessage;
import com.chuxin.family.R;

public class ReminderEditTitleActivity extends CxRootActivity {
	private static final String TITLE = "ReminderEditTitleView";
	private Button mReturnButton;
	private NiceEditText mTitleField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_view_reminder_edit_title);
		init();
	}

	private void fillData() {
		ReminderController controller = ReminderController.getInstance();
		mTitleField.setText(controller.getTitle());
	}

	private boolean saveUserInput() {
		String title = mTitleField.getText().toString();
		if (title.length() == 0) {
			return false;
		}

		ReminderController controller = ReminderController.getInstance();
		controller.setTitle(title);

		return true;
	}

	private void init() {
		mReturnButton = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mTitleField = (NiceEditText) findViewById(R.id.cx_fa_view_reminder_edit_title__title);
		mTitleField.setTextColor(Color.BLACK);

		mReturnButton.setText(getString(R.string.cx_fa_navi_save_and_back));
		mReturnButton.setPadding(30, 0, 10, 0);
		mReturnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (saveUserInput()) {
					Intent intent = new Intent(ReminderEditTitleActivity.this,
							ReminderCreateActivity.class);
					startActivity(intent);					
					finish();	
					overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				} else {
					QuickMessage.error(ReminderEditTitleActivity.this, R.string.cx_fa_nls_invalid_reminder_title);
				}
			}

		});

		fillData();
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			if (saveUserInput()) {
				Intent intent = new Intent(ReminderEditTitleActivity.this,
						ReminderCreateActivity.class);
				startActivity(intent);					
				finish();	
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			} else {
				QuickMessage.error(ReminderEditTitleActivity.this, R.string.cx_fa_nls_invalid_reminder_title);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
}
