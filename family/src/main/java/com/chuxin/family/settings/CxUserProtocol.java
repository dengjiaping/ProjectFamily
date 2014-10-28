package com.chuxin.family.settings;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * 用户协议
 * @author shichao.wang
 *
 */
public class CxUserProtocol extends CxRootActivity {

	private Button mBackBtn, mMoreBtn;
	private TextView mTitleInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_user_protocal);
		
//		TextView mProtocal = (TextView) findViewById(R.id.cx_fa_setting_about_protocol_tv);
//		mProtocal.setText(RkResourceString.getInstance().str_setting_about_user_protocol_title);
		
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mMoreBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mTitleInfo = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		
		mBackBtn.setBackgroundResource(R.drawable.cx_fa_back_btn);
		mMoreBtn.setVisibility(View.INVISIBLE);
		mTitleInfo.setText(getString(R.string.cx_fa_user_protocol_text));
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CxUserProtocol.this.finish();
			}
		});
		
	}
	
	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		super.onPause();
	}
	
}
