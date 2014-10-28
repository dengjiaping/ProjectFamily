package com.chuxin.family.settings;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.UserApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.CxUserProfile;
import com.chuxin.family.resource.CxResourceRaw;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.androidpush.sdk.push.RKPush;
import com.chuxin.family.R;

import android.app.Activity;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CxSetPushSound extends CxRootActivity {

	private ImageView firstImgView, secondImgView, thirdImgView;
	private LinearLayout firstSetLayout, secondSetLayout, thirdSetLayout;
//	private int mSetSoundType = 0; //默认是第一种（调皮：老公版； 三弦音：老婆），依次加1，分别为：1--老婆/老公，2--系统通知声音
	private Button mSaveReturnBtn;
	private SoundPool mSoundPlayer/*, mNotificationPlayer*/;
	private int firstSound, secondSound, thirdSound;
	boolean tempSelected = false;
	String soundStr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_set_push_sound);
		
		new RkInitPlayer().execute(); //位置调节，确保初始化SoundPool成功
		
		TextView mFirstSoundText = (TextView) findViewById(R.id.cx_fa_setting_sound_first_tv);
		TextView mSecondSoundText = (TextView) findViewById(R.id.cx_fa_setting_sound_second_tv);
		mFirstSoundText.setText(CxResourceString.getInstance().str_setting_sound_push_first_sound);
		mSecondSoundText.setText(CxResourceString.getInstance().str_setting_sound_push_second_sound);
		
		mSaveReturnBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mSaveReturnBtn.setText(getString(R.string.cx_fa_save_and_back_text));
		mSaveReturnBtn.setGravity(Gravity.CENTER);
		
		firstSetLayout = (LinearLayout)findViewById(R.id.cx_fa_push_sound_first_set_btn);
		secondSetLayout = (LinearLayout)findViewById(R.id.cx_fa_push_sound_second_set_btn);
		thirdSetLayout = (LinearLayout)findViewById(R.id.cx_fa_push_sound_third_set_btn);
		firstImgView = (ImageView)findViewById(R.id.cx_fa_first_sound_selected);
		secondImgView = (ImageView)findViewById(R.id.cx_fa_second_sound_selected);
		thirdImgView = (ImageView)findViewById(R.id.cx_fa_third_sound_selected);
		
		int tempPushType = CxGlobalParams.getInstance().getPushSoundType();
		if (0 == tempPushType) {
			firstImgView.setVisibility(View.VISIBLE);
		}else if(1 == tempPushType){
			secondImgView.setVisibility(View.VISIBLE);
		}else{
			thirdImgView.setVisibility(View.VISIBLE);
		}
		
		firstSetLayout.setOnClickListener(pushSoundSetListener);
		secondSetLayout.setOnClickListener(pushSoundSetListener);
		thirdSetLayout.setOnClickListener(pushSoundSetListener);
		
		mSaveReturnBtn.setOnClickListener(pushSoundSetListener);
		
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (tempSelected) {
				try {
					UserApi.getInstance().updateUserProfile(null, null, soundStr, null, null, setPushSoundCaller);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		if (null != mSoundPlayer) {
			mSoundPlayer.release();
		}
		super.onDestroy();
	}
	
	class RkInitPlayer extends AsyncTask<Object, Integer, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			mSoundPlayer = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
			firstSound = mSoundPlayer.load(CxSetPushSound.this, CxResourceRaw.getInstance().raw_push_first, 1);
			secondSound = mSoundPlayer.load(CxSetPushSound.this, CxResourceRaw.getInstance().raw_push, 1);
			
//			mNotificationPlayer = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100);
//			thirdSound = mNotificationPlayer.load(RkSetPushSound.this, RingtoneManager.TYPE_NOTIFICATION, 1);
			return null;
		}
		
	}
	
	OnClickListener pushSoundSetListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_push_sound_first_set_btn:
				firstImgView.setVisibility(View.VISIBLE);
				secondImgView.setVisibility(View.INVISIBLE);
				thirdImgView.setVisibility(View.INVISIBLE);
				tempSelected = true;
				CxGlobalParams.getInstance().setPushSoundType(0);
				
				if(CxGlobalParams.getInstance().getVersion()==0){
					soundStr = "w_rk_naughty_push.caf";
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ getPackageName() + "/raw/" + "rk_fa_role_push_first_s");
				}else{
					soundStr = "h_rk_naughty_push.caf";
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ getPackageName() + "/raw/" + "rk_fa_role_push_first");
				}

				try {
					mSoundPlayer.play(firstSound, 0.5f, 0.5f, 1, 1, 1.0f);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case R.id.cx_fa_push_sound_second_set_btn:
				firstImgView.setVisibility(View.INVISIBLE);
				secondImgView.setVisibility(View.VISIBLE);
				thirdImgView.setVisibility(View.INVISIBLE);
				
				tempSelected = true;
				CxGlobalParams.getInstance().setPushSoundType(1);
				
				
				if(CxGlobalParams.getInstance().getVersion()==0){
					soundStr = "w_rk_fa_role_push.caf";
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ getPackageName() + "/raw/" + "rk_fa_role_push_s");
				}else{
					soundStr = "h_rk_fa_role_push.caf";
					RKPush.S_NOTIFY_SOUND_URI = Uri.parse("android.resource://" 
							+ getPackageName() + "/raw/" + "rk_fa_role_push");
				}
				
				try {
					mSoundPlayer.play(secondSound, 0.5f, 0.5f, 1, 1, 1.0f);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
			case R.id.cx_fa_push_sound_third_set_btn:
				firstImgView.setVisibility(View.INVISIBLE);
				secondImgView.setVisibility(View.INVISIBLE);
				thirdImgView.setVisibility(View.VISIBLE);
				soundStr = "default";
				tempSelected = true;
				CxGlobalParams.getInstance().setPushSoundType(2);
				RKPush.S_NOTIFY_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				try {
			        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			        r.play();
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
				break;
			case R.id.cx_fa_activity_title_back:
				if (tempSelected) {
					try {
						UserApi.getInstance().updateUserProfile(null, null, soundStr, null, null, setPushSoundCaller);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				CxSetPushSound.this.finish();
				break;
			default:
				tempSelected = false;
				break;
			}
			
		}
	};
	
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	};
	
	JSONCaller setPushSoundCaller = new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if (null == result) {
				return -1;
			}
			CxUserProfile userProfile = null;
			try {
				userProfile = (CxUserProfile)result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (null == userProfile) {
				return -2;
			}
			
			if (0 != userProfile.getRc()) {
				return userProfile.getRc();
			}
			
			if (null == userProfile.getData()){
				return -4;
			}
			if (null == userProfile.getData().getPush_sound()) {
				return -3;
			}
			
			return 0;
		}
	};
	
}
