package com.chuxin.family.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
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
import com.chuxin.family.net.AccountApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.views.login.CxThirdAccessToken;
import com.chuxin.family.views.login.CxThirdAccessTokenKeeper;

public class CxRegisterByFamily extends Activity {

	
	@Override
	protected void onCreate(Bundle arg0) {		
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_main_register_by_family);
		
		
		initTitle();
		
		init();
		
	}
	
	private void init() {
		accountEdit = (EditText) findViewById(R.id.cx_fa_main_register_email_account);
		tokenEdit = (EditText) findViewById(R.id.cx_fa_main_register_email_token);
		tokenTooEdit = (EditText) findViewById(R.id.cx_fa_main_register_email_token_too);
		LinearLayout registerLayout = (LinearLayout) findViewById(R.id.cx_fa_main_register_btn_layout);
		
		registerLayout.setOnClickListener(listener);
		
	}

	private void initTitle() {
		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		TextView titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);

		titleText.setText("小家帐号注册");
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
			case R.id.cx_fa_main_register_btn_layout:
				registerAccount();
				break;
			default:
				break;
			}
		}
	};
	private EditText accountEdit;
	private EditText tokenEdit;
	private EditText tokenTooEdit;
	
	
	private void registerAccount() {
		account = accountEdit.getText().toString().trim();
		token = tokenEdit.getText().toString().trim();
		tokenToo = tokenTooEdit.getText().toString().trim();
		if(TextUtils.isEmpty(account) || TextUtils.isEmpty(token) ||  TextUtils.isEmpty(tokenToo)){
			ToastUtil.getSimpleToast(this, -3, "请输入帐号密码", 1).show();
			return ;
		}
		
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(account);
		boolean  flag = matcher.matches();
		if(!flag){
			ToastUtil.getSimpleToast(this, -3, "邮箱名格式有误", 1).show();
			return ;
		}
		String check2 = "^[a-z0-9A-Z]+$";
		Pattern regex2 = Pattern.compile(check2);
		Matcher matcher2 = regex2.matcher(token);
		boolean  flag2 = matcher2.matches();
		if(!flag2){
			ToastUtil.getSimpleToast(this, -3, "只能输入英文或数字", 1).show();
			return ;
		}
		
		if(token.length()<6){
			ToastUtil.getSimpleToast(this, -3, "请输入6位以上密码", 1).show();
			return ;
		}
		
		if(!token.equals(tokenToo)){
			ToastUtil.getSimpleToast(this, -3, "两次密码输入不一致", 1).show();
			return ;
		}
		
		if(account.length()>30){
			ToastUtil.getSimpleToast(this, -3, "帐号输入过长", 1).show();
			return ;
		}
		if(token.length()>30){
			ToastUtil.getSimpleToast(this, -3, "密码输入过长", 1).show();
			return ;
		}
		
		try {
			DialogUtil.getInstance().getLoadingDialogShow(CxRegisterByFamily.this, -1);
			AccountApi.getInstance().doRegister("email", account,token,null, -1+"", //0：男性 1：女性 -1：未设置
					null/*sina微博没有生日这个字段返回*/, 
					null/*暂时没有client_version*/, 
					"zh_cn", 
					null, regiseCallback);
		} catch (Exception e) {			
			e.printStackTrace();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			showResponseToast("注册失败", 0);
		}
		
	}
	
	
	JSONCaller regiseCallback=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			if (null == result) { //注册失败给予提示
				showResponseToast("注册失败", 0);
				return -1;
			}
			CxLog.i("", "ready to get user chuxin profile:"+result.toString());
			CxParseBasic loginResult = null;
			try {
				loginResult = (CxParseBasic)result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (null == loginResult){ //注意此处把uid放在msg字段
				showResponseToast("注册失败", 0);
				return -1;
			}
			
			if(408==loginResult.getRc()){
				showResponseToast(getString(R.string.cx_fa_net_response_code_null), 0);
				return -1;
			}
			
			if(3001 ==loginResult.getRc()){
				String str="该账号已被注册";
				if(!TextUtils.isEmpty(loginResult.getMsg())){
					str=loginResult.getMsg();
				}
				showResponseToast(str, 0);
				return -1;
			}
			if(0!=loginResult.getRc()){
				showResponseToast(getString(R.string.cx_fa_net_response_code_fail), 0);
				return -1;
			}
			
			CxThirdAccessToken localToken = new CxThirdAccessToken(token, account, "", "email");
			CxThirdAccessTokenKeeper.keepAccessToken(CxRegisterByFamily.this, localToken);
			
			try {
				CxAuthenNew.mAuthenHandler.sendEmptyMessage(CxAuthenNew.AutoLogin);
				finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);		
			} catch (Exception e) {
				e.printStackTrace();
				showResponseToast("登录失败", 0);
			}
			
			CxLog.i("", "registed uid is:"+loginResult.getMsg());
			
			return 0;

		}
	};

	private String account;
	private String token;
	private String tokenToo;
	
	
	protected void back() {
		Intent intent=new Intent(this, CxLoginByFamily.class);
		startActivity(intent);	
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		finish();		
		
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
		new Handler(CxRegisterByFamily.this.getMainLooper()) {
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
				ToastUtil.getSimpleToast(CxRegisterByFamily.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
}
