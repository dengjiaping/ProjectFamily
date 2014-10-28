package com.chuxin.family.app;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.service.CxBackgroundService;
import com.chuxin.family.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 被动解除方收到的确认框
 * @author shichao.wang
 *
 */
public class CxDialogActivity extends CxRootActivity {

	@Override
	protected void onDestroy() {
		CxBackgroundService.hasNotified = false;
		super.onDestroy();
	}

	private TextView messageView;
	private Button comfirmButton;
	private boolean mType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cx_fa_activity_dialog);
		
		Display display = getWindowManager().getDefaultDisplay();
		WindowManager.LayoutParams layoutParam = getWindow().getAttributes();
		layoutParam.width = (int)(display.getWidth() * 0.8);
		getWindow().setAttributes(layoutParam);
		
		messageView = (TextView)findViewById(R.id.rk_dialog_msg_view);
		comfirmButton = (Button)findViewById(R.id.rk_dialog_button);
		comfirmButton.setOnClickListener(comfirmClcik);
		
		Intent data = this.getIntent();
		mType = data.getBooleanExtra(CxGlobalConst.S_DIALOG_INTENT, false);
		if (mType) { //主动解除结对
			messageView.setText(getString(R.string.cx_fa_unbind_success));
		}else{ //被动解除结对
			messageView.setText(getString(R.string.cx_fa_mate_unbind));
			
		}
		
	}
	
	private OnClickListener comfirmClcik = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!mType) {
				CxGlobalParams.getInstance().setPair(0); //解除结对
			}
			
			CxBackgroundService.hasNotified = false;
			CxDialogActivity.this.finish();
		}
	}; 
	
}
