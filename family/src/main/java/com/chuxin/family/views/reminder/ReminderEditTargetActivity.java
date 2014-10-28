package com.chuxin.family.views.reminder;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.widgets.NiceSelector;
import com.chuxin.family.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReminderEditTargetActivity extends CxRootActivity {
	private static final String TITLE = "ReminderEditTargetView";
	private Button mReturnButton;
	private NiceSelector mTargetSelector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_view_reminder_edit_target);
		init();
	}

	private void fillData() {
		ReminderController controller = ReminderController.getInstance();

		int target = controller.getTarget();
		String[] selection = new String[] { String.valueOf(target) };
		mTargetSelector.setSelection(selection);
	}

	private boolean saveUserInput() {
		String target = mTargetSelector.getSelection()[0];

		ReminderController controller = ReminderController.getInstance();
		controller.setTarget(Integer.valueOf(target));

		return true;
	}

	private void init() {
		mReturnButton = (Button) findViewById(R.id.cx_fa_activity_title_back);
		mTargetSelector = (NiceSelector) findViewById(R.id.cx_fa_view_reminder_edit_target__selector);

		mReturnButton.setText(getString(R.string.cx_fa_navi_save_and_back));
		mReturnButton.setPadding(30, 0, 10, 0);
		mReturnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (saveUserInput()) {
					Intent intent = new Intent(ReminderEditTargetActivity.this,
							ReminderCreateActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				} else {
					Toast.makeText(ReminderEditTargetActivity.this,
							"Validation Check failed!", Toast.LENGTH_SHORT)
							.show();
				}
			}

		});

		fillData();
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			if (saveUserInput()) {
				Intent intent = new Intent(ReminderEditTargetActivity.this,
						ReminderCreateActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			} else {
				Toast.makeText(ReminderEditTargetActivity.this,
						"Validation Check failed!", Toast.LENGTH_SHORT)
						.show();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
	
	
	
}
