
package com.chuxin.family.settings;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.main.CxMain;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxCheckVersion;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.CxLoadingUtil;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 关于“老公”/“老婆”
 * 
 * @author shichao.wang
 */
public class CxAboutVersion extends CxRootActivity {

    private Button mBackImageBtn, mMoreImageBtn;

    private TextView mTitleInfo;

    private Button mUpdateButton, mUserProtocolButton, mToHelpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_fa_activity_about_software);
        
//        ImageView mAboutImg = (ImageView) findViewById(R.id.cx_fa_setting_about_img);
//        mAboutImg.setImageResource(RkResourceDarwable.getInstance().dr_main_login_wait_logo);
        
        mBackImageBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
        mMoreImageBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
        mTitleInfo = (TextView)findViewById(R.id.cx_fa_activity_title_info);

        mBackImageBtn.setText(getString(R.string.cx_fa_navi_back));

        mBackImageBtn.setBackgroundResource(R.drawable.cx_fa_back_btn);
        mMoreImageBtn.setVisibility(View.INVISIBLE);
        mTitleInfo.setText(R.string.cx_fa_about_version);
        mBackImageBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CxAboutVersion.this.finish();
                overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
            }
        });
        
        mToHelpBtn		= (Button)findViewById(R.id.toHelp);			// 帮助 (功能介绍)
        mToHelpBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toHelpOrSuggest = new Intent(CxAboutVersion.this, CxHelpSuggest.class);
				startActivity(toHelpOrSuggest);
				overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);	
			}
		});
        
        mUpdateButton = (Button)findViewById(R.id.toUpdateSoftware); // 软件更新目前不做
        mUpdateButton.setText(getResources()
        		.getString(R.string.cx_fa_current_version_number) + getVersionName());
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
//					RkLoadingUtil.getInstance().showLoading(RkAboutVersion.this, true);
					DialogUtil.getInstance().getLoadingDialogShow(CxAboutVersion.this, -1);
					UserApi.getInstance().checkVersion(checkVersionCall);//版本检查
				} catch (Exception e) {
//					RkLoadingUtil.getInstance().dismissLoading();
					DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
					e.printStackTrace();
				}
			}
		});
        mUserProtocolButton = (Button)findViewById(R.id.toUserProtocal);
        mUserProtocolButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent protocolIntent = new Intent(CxAboutVersion.this, CxUserProtocol.class);
                startActivity(protocolIntent);
                overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
            }
        });

    }

    private String getVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    	if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
    		finish();
    		this.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    		return false;
    	}
    	return super.onKeyDown(keyCode, event);
    };
    
    private JSONCaller checkVersionCall = new JSONCaller() {

		@SuppressLint("NewApi")
		@Override
		public int call(Object result) {
//			RkLoadingUtil.getInstance().dismissLoading();
			DialogUtil.getInstance().setLoadingDialogDismiss(null, -1, 2000);
			if (null == result) {
				return -1;
			}
			CxCheckVersion version = null;
			try {
				version = (CxCheckVersion) result;
			} catch (Exception e) {
			}
			if (null == version) {
				return -1;
			}
			if (0 != version.getRc()) {
				return version.getRc();
			}
			final String downLoadUri = version.getUrl();
			if (TextUtils.isEmpty(downLoadUri)) {
				return 0;
			}
			
			final String tipMsg = version.getMsg();
			
			if (1 == version.getFlag()) { // 必须更新
				new Handler(getMainLooper()) {
					public void handleMessage(Message msg) {
						AlertDialog dlg = new AlertDialog.Builder(CxAboutVersion.this)
								.create();
						dlg.setTitle(getString(R.string.cx_fa_upgrade_tip_text));
						
						if(tipMsg!=null && !tipMsg.equals("")){
							dlg.setMessage(tipMsg);
						}else{
							dlg.setMessage(getString(R.string.cx_fa_must_upgrade_text));
						}
						
						dlg.setButton(DialogInterface.BUTTON_NEUTRAL,
								getString(R.string.cx_fa_upgrade_text),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										/*new RkUpdateTask().execute(downLoadUri);
										dialog.dismiss();*/
										Intent intent = new Intent();
										intent.setAction(Intent.ACTION_VIEW);
										Uri content_url = Uri.parse(downLoadUri);
										intent.setData(content_url);
										startActivity(intent);
										CxAboutVersion.this.finish();
									}
								});
						dlg.setCancelable(false);
						dlg.show();
					};
				}.sendEmptyMessage(1);
				return 1;
			}
			if (2 == version.getFlag()) { // 能更新的话最好，不更新也可以
				final String versionName = version.getVersion();
				new Handler(getMainLooper()) {
					public void handleMessage(Message msg) {

								AlertDialog dlg = new AlertDialog.Builder(
										CxAboutVersion.this).create();
								dlg.setTitle(getString(R.string.cx_fa_upgrade_tip_text));
								dlg.setCancelable(false);
								String st = "";
								try {
									st = String
											.format(getString(R.string.cx_fa_upgrade_version_text),
													versionName);
								} catch (Exception e) {
									st = getString(R.string.cx_fa_upgrade_version_text);
								}
								if(tipMsg!=null && !tipMsg.equals("")){
									dlg.setMessage(tipMsg);
								}else{
									dlg.setMessage(st);
								}
								dlg.setButton(DialogInterface.BUTTON_NEGATIVE,
										getString(R.string.cx_fa_upgrade_text),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												/*new RkUpdateTask().execute(downLoadUri);*/
												Intent intent = new Intent();
												intent.setAction(Intent.ACTION_VIEW);
												Uri content_url = Uri.parse(downLoadUri);
												intent.setData(content_url);
												startActivity(intent);
												
											}
										});
								dlg.setButton(
										DialogInterface.BUTTON_POSITIVE,
										getString(R.string.cx_fa_cancel_button_text),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										});
								dlg.show();
							};
				}.sendEmptyMessage(1);
				return 2;
			}

			return 0;
		}
	};

}
