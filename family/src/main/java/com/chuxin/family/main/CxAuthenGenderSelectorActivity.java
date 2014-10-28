package com.chuxin.family.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuxin.family.accounting.CxChangeAccountActivity;
import com.chuxin.family.app.CxApplication;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenNew.DisplayAlertInfo;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.resource.CxResource;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.androidpush.sdk.push.RKPush;
import com.chuxin.family.R;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxAuthenGenderSelectorActivity extends CxRootActivity {

	private boolean extra;
	private final String PROFILE_FILE_NAME = "profile_name";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_main_login_gender_selector);

		Intent intent = getIntent();
		extra = intent.getBooleanExtra("exist", false);

//		initTitle();
		init();
	}

	private void init() {
		ImageView mHusbandImg = (ImageView) findViewById(R.id.cx_fa_main_login_gender_selector_husband_iv);
		ImageView mWifeImg = (ImageView) findViewById(R.id.cx_fa_main_login_gender_selector_wife_iv);

		mHusbandImg.setOnClickListener(clickListener);
		mWifeImg.setOnClickListener(clickListener);

	}

//	private void initTitle() {
//		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
//		TextView titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
//
//		backBtn.setVisibility(View.INVISIBLE);
//		titleText.setText("选择你的角色");
//	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_main_login_gender_selector_husband_iv:
				DialogUtil du = DialogUtil.getInstance();
				du.getSimpleDialog(CxAuthenGenderSelectorActivity.this, null,
						"性别选择后将不可更改，是否确定？", null, null).show();
				du.setOnSureClickListener(new OnSureClickListener() {

					@Override
					public void surePress() {
						try {
							DialogUtil.getInstance().getLoadingDialogShow(CxAuthenGenderSelectorActivity.this, -1);
							UserApi.getInstance().updateUserProfile(null, null,null, 0 + "", null, genderCaller);
						} catch (Exception e) {
							e.printStackTrace();
							DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
						}

					}
				});

				//
				break;
			case R.id.cx_fa_main_login_gender_selector_wife_iv:

				DialogUtil dul = DialogUtil.getInstance();
				dul.getSimpleDialog(CxAuthenGenderSelectorActivity.this, null,
						"性别选择后将不可更改，是否确定？", null, null).show();
				dul.setOnSureClickListener(new OnSureClickListener() {

					@Override
					public void surePress() {
						try {
							DialogUtil.getInstance().getLoadingDialogShow(CxAuthenGenderSelectorActivity.this, -1);
							UserApi.getInstance().updateUserProfile(null, null,null, 1 + "", null, genderCaller);
						} catch (Exception e) {
							e.printStackTrace();
							DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
						}

					}
				});

				break;
			default:
				break;
			}

		}
	};

	JSONCaller genderCaller = new JSONCaller() {

		@Override
		public int call(Object result) {
			
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 1000);
			if (null == result) {
				displayResultInfo(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_null), 0);
				return -1;
			}

			CxUserProfile userInitInfo = null;
			try {
				userInitInfo = (CxUserProfile) result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null == userInitInfo || userInitInfo.getData() == null) {
				displayResultInfo(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail), 0);
				//
				return -1;
			}
			if (0 != userInitInfo.getRc()) {
				displayResultInfo(CxApplication.getInstance().getString(R.string.cx_fa_net_response_code_fail), 0);
				return -2;
			}

			SharedPreferences sp = getSharedPreferences(PROFILE_FILE_NAME,
					Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putInt("gender", userInitInfo.getData().getGender());
			edit.commit();

			int gender = userInitInfo.getData().getGender();
			
			CxGlobalParams global = CxGlobalParams.getInstance();

			global.setPushSoundType(1);
			if(gender==0){
				RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
						+ CxAuthenGenderSelectorActivity.this.getPackageName() + "/raw/" + "rk_fa_role_push_s");
			}else{
				RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
						+ CxAuthenGenderSelectorActivity.this.getPackageName() + "/raw/" + "rk_fa_role_push");
			}

			global.setVersion(gender);

			int version = CxGlobalParams.getInstance().getVersion();
			if (version == 0) {
				CxResource.getInstance().setType(false);
			} else {
				CxResource.getInstance().setType(true);
			}
			
			if(0==CxGlobalParams.getInstance().getVersion_type()){
				Intent it = new Intent(CxAuthenGenderSelectorActivity.this, CxAuthenChildrenSelectorActivity.class);
				it.putExtra("exist", extra);
				startActivity(it);
				overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out);
				finish();
				return 3;
			}
			
			

			Intent it = new Intent(CxAuthenGenderSelectorActivity.this,
					CxMain.class);
			it.putExtra("exist", extra);
			it.putExtra("hasToken", 0);
			startActivity(it);
			overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
			finish();
			return 0;
		}
	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			// back();
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};

	/**
	 * 
	 * @param info
	 * @param number
	 *            0 失败；1 成功；2 不要图。
	 */
	private void displayResultInfo(String info, int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1 = number;
		new Handler(CxAuthenGenderSelectorActivity.this.getMainLooper()) {
			public void handleMessage(Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				int id = -1;
				if (msg.arg1 == 0) {
					id = R.drawable.chatbg_update_error;
				} else if (msg.arg1 == 1) {
					id = R.drawable.chatbg_update_success;
				}
				ToastUtil.getSimpleToast(CxAuthenGenderSelectorActivity.this,
						id, msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}

}
