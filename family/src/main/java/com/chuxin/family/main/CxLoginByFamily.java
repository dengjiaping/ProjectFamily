package com.chuxin.family.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.R;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.main.CxAuthenNew.DisplayAlertInfo;
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxLogin;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.login.CxThirdAccessToken;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;

public class CxLoginByFamily extends Activity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_main_login_by_family);

		initTitle();
		
		init();

	}

	private void init() {
		accountEdit = (EditText) findViewById(R.id.cx_fa_main_login_email_account);
		tokenEdit = (EditText) findViewById(R.id.cx_fa_main_login_email_token);
		LinearLayout loginLayout = (LinearLayout) findViewById(R.id.cx_fa_main_login_btn_layout);
		LinearLayout regLayout = (LinearLayout) findViewById(R.id.cx_fa_main_login_toReg_btn_layout);
		
		loginLayout.setOnClickListener(listener);
		regLayout.setOnClickListener(listener);
	}

	private void initTitle() {
		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		TextView titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);

		titleText.setText("小家帐号登录");
		backBtn.setText(getString(R.string.cx_fa_navi_back));
		backBtn.setOnClickListener(listener);

	}
	
	
	OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				back();
				break;
			case R.id.cx_fa_main_login_btn_layout:
				login();
				break;
			case R.id.cx_fa_main_login_toReg_btn_layout:
				Intent intent=new Intent(CxLoginByFamily.this, CxRegisterByFamily.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
				finish();
				break;

			default:
				break;
			}
		}
	};
	private EditText accountEdit;
	private EditText tokenEdit;
	
	protected void login() {
		account = accountEdit.getText().toString().trim();
		token = tokenEdit.getText().toString().trim();
		
		if(TextUtils.isEmpty(account) || TextUtils.isEmpty(token) ){
			ToastUtil.getSimpleToast(this, -3, "请输入帐号密码", 1).show();
			return ;
		}
		
		try {
			DialogUtil.getInstance().getLoadingDialogShow(CxLoginByFamily.this, -1);
			AccountApi.getInstance().doLogin("email", account, token, 
					-1, loginCaller);
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			showResponseToast("登录失败", 0);
		}
		
	}
	
	
	JSONCaller loginCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			if (null == result) { // 属于异常情况  基本不可能
//				showResponseToast(getString(R.string.cx_fa_net_response_code_null),0);
//				return -1;
//			}
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			CxLogin loginResult = (CxLogin) result;

			if (null == loginResult){   //注意此处把uid放在msg字段
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null),0);
				return -2;
			}
			
			if(408==loginResult.getRc()){
				showResponseToast(CxApplication.getInstance().getApplicationContext().getString(R.string.cx_fa_net_response_code_null), 0);
				return -2;
			}
			
			if(0!=loginResult.getRc()){
				String msg = loginResult.getMsg();
				if(TextUtils.isEmpty(msg)){
					msg=getString(R.string.cx_fa_net_response_code_fail);
				}
				showResponseToast(msg, 0);
				return -3;
			}
			// 正常登录成功，需要获取用户的基本资料（对应接口：用户信息 api/User/get），再进入主界面
			
			CxThirdAccessToken localToken = new CxThirdAccessToken(token, account, "", "email");
			CxThirdAccessTokenKeeper.keepAccessToken(CxLoginByFamily.this, localToken);

	
			Message obtain = Message.obtain();
			obtain.obj=loginResult.getData();
			obtain.what=CxAuthenNew.FetchMyInfo;
			CxAuthenNew.mAuthenHandler.sendMessage(obtain);
			
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			
			return 0;
		}
	};
	private String account;
	private String token;
	
	
	protected void back() {
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void showResponseToast(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(CxLoginByFamily.this.getMainLooper()) {
			public void handleMessage(Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				int id=-1;
				if(msg.arg1==0){
					id= R.drawable.chatbg_update_error;
				}else if(msg.arg1==1){
					id=R.drawable.chatbg_update_success;
				}
				ToastUtil.getSimpleToast(CxLoginByFamily.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}

}
