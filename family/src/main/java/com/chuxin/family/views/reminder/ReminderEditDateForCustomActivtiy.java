package com.chuxin.family.views.reminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.widgets.NiceSelector;
import com.chuxin.family.R;

public class ReminderEditDateForCustomActivtiy extends CxRootActivity {
    private static final String TITLE = "ReminderEditDateForCustomView";
    private Button mReturnButton;
    private NiceSelector mCustomSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.cx_fa_view_reminder_edit_custom_date);
    	init();
    }

    private void fillData() {
    	ReminderController controller = ReminderController.getInstance();
    	
    	int [] customizes = ReminderController.convertCustomizeToArray(controller.getCustomize());
    	String [] selection = new String[customizes.length];
    	
    	for (int i = 0; i < customizes.length; i++) {
    		selection[i] = String.valueOf(customizes[i]);
    	}
	
    	mCustomSelector.setSelection(selection);
    }
    
    private boolean saveUserInput() {
    	String[] customizes = mCustomSelector.getSelection();
    	int customize = 0;
    	
    	for (int i = 0; i < customizes.length; i++) {
    		customize += Integer.valueOf(customizes[i]);
    	}
    	
    	ReminderController controller = ReminderController.getInstance();
    	controller.setCustomize(customize);

        return true;
    }

    private void init() {
        mReturnButton = (Button)findViewById(R.id.cx_fa_activity_title_back);
        mCustomSelector = (NiceSelector)findViewById(R.id.cx_fa_view_reminder_edit_custom_date__selector);
        
        mReturnButton.setText(getString(R.string.cx_fa_navi_save_and_back));
        mReturnButton.setPadding(30, 0, 10, 0);
        
        mReturnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
            	saveUserInput();
                Intent intent = new Intent(ReminderEditDateForCustomActivtiy.this, ReminderCreateActivity.class);
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
            Intent intent = new Intent(ReminderEditDateForCustomActivtiy.this, ReminderCreateActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    		return false;
    	}
    	return super.onKeyDown(keyCode, event);
    };
    
    
}
