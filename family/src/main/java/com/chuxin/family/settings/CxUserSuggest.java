package com.chuxin.family.settings;

import java.io.File;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.HttpApi;
import com.chuxin.family.net.CxSendImageApi;
import com.chuxin.family.net.CxSettingsCommonApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxLogoutResponce;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 意见反馈
 * @author shichao.wang
 *
 */
public class CxUserSuggest extends CxRootActivity {

	private EditText mUserSuggestionContent, mUserContactStr;
	private Button mSendSuggestionBtn;
	
	private Button mBackBtn, mMoreBtn;
	private TextView mTitleInfo, mUserSuggestionUid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_user_suggest);
		
		mUserSuggestionContent = (EditText)findViewById(R.id.userSuggestionContent);
		mUserSuggestionUid			= (TextView)findViewById(R.id.userSuggestionUid);					// 您的UID:xxx
		mUserSuggestionUid.setText( getString(R.string.cx_fa_suggestion_uid) +CxGlobalParams.getInstance().getUserId() );
		
		//for debug
		
		//注意：提交代码不要动这个条件
		if (!HttpApi.HTTP_SERVER_PREFIX.equalsIgnoreCase("http://api.family.rekoo.net/")) {
			String strVerName = "-1";
			try {
				PackageInfo info = CxUserSuggest.this.getPackageManager()
				.getPackageInfo(CxUserSuggest.this.getPackageName(), 0);
				strVerName = info.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			mUserSuggestionContent.setHint("DEBUG Uid:" + CxGlobalParams.getInstance().getUserId() + 
					                       " PartnerUid: " + CxGlobalParams.getInstance().getPartnerId() + 
					                       " Version: " + strVerName);
		}
		
		mUserContactStr = (EditText)findViewById(R.id.userContactStr);
		mSendSuggestionBtn = (Button)findViewById(R.id.sendSuggestionBtn);
		mSendSuggestionBtn.setOnClickListener(buttonListener);
		
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mMoreBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mTitleInfo = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		
		mBackBtn.setText(getString(R.string.cx_fa_navi_back));
		
		mBackBtn.setBackgroundResource(R.drawable.cx_fa_back_btn);
		mMoreBtn.setVisibility(View.INVISIBLE);
		mTitleInfo.setText(getString(R.string.cx_fa_help_feedback));
		mBackBtn.setOnClickListener(buttonListener);
		
	}
	
	OnClickListener buttonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				CxUserSuggest.this.finish();
				break;
			case R.id.sendSuggestionBtn:
				//判断意见是否填写
				if (TextUtils.isEmpty(mUserSuggestionContent.getText().toString())) {
					//TODO 提示填写意见
					Toast nullError = Toast.makeText(CxUserSuggest.this, getString(
							R.string.cx_fa_input_suggestion), Toast.LENGTH_LONG);
					nullError.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 
							mUserSuggestionContent.getLeft(), mUserSuggestionContent.getTop());
					nullError.show();
					mUserSuggestionContent.requestFocus();
					return;
				}
				
				try {
//					RkLoadingUtil.getInstance().showLoading(RkUserSuggest.this, true);
					DialogUtil.getInstance().getLoadingDialogShow(CxUserSuggest.this, -1);
					/*RkSettingsCommonApi.getInstance().requestSuggestion(
							mUserSuggestionContent.getText().toString(), 
							mUserContactStr.getText().toString(), mSuggestBack);*/
					String errLogFile = Environment.getExternalStorageDirectory()
					.getAbsolutePath()+File.separator+"chuxin"+File.separator +"errlog.txt";
					CxSendImageApi.getInstance().sendClientResponce(
							mUserSuggestionContent.getText().toString(), errLogFile, 
							mUserContactStr.getText().toString(), mSuggestBack);
				} catch (Exception e) {
					e.printStackTrace();
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
				}
				
				break;

			default:
				break;
			}
			
		}
	};
	
	JSONCaller mSuggestBack = new JSONCaller() {
		
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			if (null == result) {
				backDataHandler.sendEmptyMessage(-1);
				return -1;
			}
			
			CxLogoutResponce suggestResult = (CxLogoutResponce)result;
			if (0 != suggestResult.getRc()) { //异常
				backDataHandler.sendEmptyMessage(-2);
				return -2;
			}
			
			//正常情况给个成功提示，关闭界面
			backDataHandler.sendEmptyMessage(0);
			CxUserSuggest.this.finish();
			return 0;
		}
	};
	
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
	};
	
	private Handler backDataHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
			case -2:
				Toast nullError = Toast.makeText(CxUserSuggest.this, getString(
						R.string.cx_fa_net_err), Toast.LENGTH_LONG);
				nullError.setGravity(Gravity.CENTER, 0, 0);
				nullError.show();
				
				break;
			
			case 0:
				Toast usedNumber = Toast.makeText(CxUserSuggest.this, getString(
						R.string.cx_fa_suggestion_send_success), Toast.LENGTH_LONG);
				usedNumber.setGravity(Gravity.CENTER, 0,0);
				usedNumber.show();
				
				break;

			default:
				break;
			}
		};
	};
	
}
