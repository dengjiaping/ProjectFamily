package com.chuxin.family.settings;

import java.util.ArrayList;
import java.util.List;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CxLockScreen extends CxRootActivity implements OnKeyListener{
	private Button firstKeyBtn, secondKeyBtn, 
		thirdKeyBtn, forthKeyBtn, fifthKeyBtn, sixKeyBtn, sevenKeyBtn, 
		eightKeyBtn, ninethKeyBtn, zeroKeyBtn, noneKeyBtn;
	private ImageButton deleteKeyBtn;
	
	private ImageView firstImage, secondImage, thirdImage, forthImage;
	List<ImageView> passwordImages = new ArrayList<ImageView>();
	
	private String lockPassword;
	
	private String password="";
	private String rePassword="";
	private int inputCount = 0; //介于0 ～4之间
	private int setLockCount = 0; //设置锁屏时的密码位数计数器
	
	private int fromType = 0; //for password protect by default
	private Button mBackBtn, mRightBtn;
	private TextView mTitle; //
	private View mTitleLayer;
	
	public static boolean isExist = false; //true indicates this activity is opened, else close;
	
	private ImageView unlockView;
	
	private TextView mInputTip; //
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_lockscreen);
		isExist = true;
		SharedPreferences sp = getSharedPreferences(CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
		lockPassword = sp.getString(CxGlobalConst.S_LOCKSCREEN_FIELD, null);
		
		unlockView = (ImageView)findViewById(R.id.unlockImage);
		
		mInputTip = (TextView)findViewById(R.id.cx_fa_lock_input_tip);
		firstImage = (ImageView)findViewById(R.id.first_password);
		secondImage = (ImageView)findViewById(R.id.second_password);
		thirdImage = (ImageView)findViewById(R.id.third_password);
		forthImage = (ImageView)findViewById(R.id.forth_password);
		firstImage.setVisibility(View.INVISIBLE);
		secondImage.setVisibility(View.INVISIBLE);
		thirdImage.setVisibility(View.INVISIBLE);
		forthImage.setVisibility(View.INVISIBLE);
		passwordImages.add(firstImage);
		passwordImages.add(secondImage);
		passwordImages.add(thirdImage);
		passwordImages.add(forthImage);
		
		firstKeyBtn = (Button)findViewById(R.id.one_btn);
		secondKeyBtn = (Button)findViewById(R.id.two_btn);
		thirdKeyBtn = (Button)findViewById(R.id.three_btn);
		forthKeyBtn = (Button)findViewById(R.id.four_btn);
		fifthKeyBtn = (Button)findViewById(R.id.five_btn);
		sixKeyBtn = (Button)findViewById(R.id.six_btn);
		sevenKeyBtn = (Button)findViewById(R.id.seven_btn);
		eightKeyBtn = (Button)findViewById(R.id.eight_btn);
		ninethKeyBtn = (Button)findViewById(R.id.nine_btn);
		zeroKeyBtn = (Button)findViewById(R.id.zero_btn);
		noneKeyBtn = (Button)findViewById(R.id.none_btn);
		deleteKeyBtn = (ImageButton)findViewById(R.id.delete_btn);
		
		mTitleLayer = findViewById(R.id.cx_fa_lock_title);
		mBackBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mRightBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		mRightBtn.setVisibility(View.INVISIBLE);
		Intent dataIntent = getIntent();
		if (null == dataIntent) {
			fromType = 0;
		}else{
			fromType = dataIntent.getIntExtra(CxGlobalConst.S_LOCKSCREEN_TYPE, 0); //0 for password protect by default
		}
		
		if (0 != fromType) { //set password of lockscreen
			//disable back button and key back event
			mBackBtn.setText(getString(R.string.cx_fa_navi_back));
			mBackBtn.setVisibility(View.VISIBLE);
			mBackBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CxLockScreen.this.finish();
				}
			});
			
			if (null == lockPassword) { //set password for lock screen
				mTitle.setText(getString(R.string.cx_fa_set_lockscreen_pswd));
			}else{ //cancel password for lock screen
				mTitle.setText(getString(R.string.cx_fa_cancel_lockscreen_pswd));
			}
			
			unlockView.setVisibility(View.GONE);
		}else{ //lock screen
			mBackBtn.setVisibility(View.INVISIBLE);
			mTitleLayer.setVisibility(View.GONE);
			unlockView.setVisibility(View.VISIBLE);
		}
		
		firstKeyBtn.setOnClickListener(keyBtnListener);
		secondKeyBtn.setOnClickListener(keyBtnListener);
		thirdKeyBtn.setOnClickListener(keyBtnListener);
		forthKeyBtn.setOnClickListener(keyBtnListener);
		fifthKeyBtn.setOnClickListener(keyBtnListener);
		sixKeyBtn.setOnClickListener(keyBtnListener);
		sevenKeyBtn.setOnClickListener(keyBtnListener);
		eightKeyBtn.setOnClickListener(keyBtnListener);
		ninethKeyBtn.setOnClickListener(keyBtnListener);
		zeroKeyBtn.setOnClickListener(keyBtnListener);
		noneKeyBtn.setOnClickListener(keyBtnListener);
		deleteKeyBtn.setOnClickListener(keyBtnListener);

	}
	private String tempStr="";
	OnClickListener keyBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			RkLog.i("lockscreen", "inputCount="+inputCount
//					+",lockPassword="+lockPassword+",password="+password
//					+",rePassword="+rePassword+",setLockCount="+setLockCount);
			
			CxLog.i("CxLockScreen_men", (null == lockPassword)+">>>1");
			if (null == lockPassword) { //锁屏密码没有,设置锁屏密码
				
				if (v.getId() == R.id.delete_btn) {
					CxLog.i("CxLockScreen_men", setLockCount+">>>2");
					if (setLockCount <= 4) {
						if (inputCount == 0) {
							return ;
						}
						if(!TextUtils.isEmpty(tempStr)){
							password = password.substring(0, password.length()-1);
						}
						passwordImages.get(inputCount-1).setVisibility(View.INVISIBLE);
						inputCount--;
						setLockCount--;
						
					}else{
						if (setLockCount == 4) {
							return ;
						}
						if(!TextUtils.isEmpty(tempStr)){
							rePassword = rePassword.substring(0, rePassword.length()-1);
						}
						passwordImages.get(inputCount-1).setVisibility(View.INVISIBLE);
						inputCount--;
						setLockCount--;
					}
					
					return;
				} //删除密码
				
				
				if (setLockCount <= 3) {
					CxLog.i("CxLockScreen_men", setLockCount+">>>3");
					passwordImages.get(inputCount).setVisibility(View.VISIBLE);
					inputCount++;
					setLockCount++;
					tempStr=getInputData(v.getId());
					password += tempStr;
					
					if (inputCount == 4) {
						inputCount = 0;
						//全部清空等待输入确认密码
						invisibleAllPassword.sendEmptyMessageDelayed(1, 100);
						mInputTip.setText(getString(R.string.cx_fa_reinput_lockscreen_password));
					}	
					
				} else{
					CxLog.i("CxLockScreen_men", setLockCount+">>>4");
					passwordImages.get(inputCount).setVisibility(View.VISIBLE);
					inputCount++;
					setLockCount++;
					tempStr=getInputData(v.getId());
					rePassword += tempStr;
					
					if (setLockCount == 8) { //需要匹对 "密码" 与 "重复密码"
						if ( (!TextUtils.isEmpty(rePassword)) 
								&& (TextUtils.equals(password, rePassword)) ){
							//set password success
							Toast.makeText(CxLockScreen.this, getString(
									R.string.cx_fa_success_setpswd), Toast.LENGTH_SHORT).show();
							
							SharedPreferences sp = getSharedPreferences(
									CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
							Editor editor = sp.edit();
							editor.putString(CxGlobalConst.S_LOCKSCREEN_FIELD, password);
							editor.commit();
							CxLockScreen.this.finish();
						}else{ //不匹配，需要重置，方便下次输入
							mInputTip.setText(getString(R.string.cx_fa_input_lockscreen_password));
							setLockCount = 0;
							inputCount = 0;
							password = "";
							rePassword = "";
							Toast.makeText(CxLockScreen.this, getString(
									R.string.cx_fa_different_password), Toast.LENGTH_SHORT).show();
							invisibleAllPassword.sendEmptyMessageDelayed(1, 100);
						}
					}
					
				}//输入的是 密码
				
				
				return ;
			}
			
			
			
			if (v.getId() == R.id.delete_btn) {
				
				if (inputCount == 0) {
					return ;
				}
				if(!TextUtils.isEmpty(tempStr)){
					password = password.substring(0, password.length()-1);
				}
				passwordImages.get(inputCount-1).setVisibility(View.INVISIBLE);
				inputCount--;
					
				return ;
			}
			CxLog.i("CxLockScreen_men", inputCount+">>>5");
			passwordImages.get(inputCount).setVisibility(View.VISIBLE);
			inputCount++;
			tempStr=getInputData(v.getId());
			password += tempStr;
			
			if (inputCount == 4) {
				if (TextUtils.equals(lockPassword, password)) { //匹配
					if (1 == fromType) { //cancel password for lockscreen
						//clear lock password
						SharedPreferences sp = getSharedPreferences(
								CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						editor.clear();
						editor.commit();
					}
					CxLockScreen.this.finish();
				}else{ //不匹配需要重置变量
					inputCount = 0;
					password="";
					invisibleAllPassword.sendEmptyMessageDelayed(1, 100);
					Toast.makeText(CxLockScreen.this, getString(
							R.string.cx_fa_wrong_lock_password), Toast.LENGTH_SHORT).show();
				}
			}	
			
			
			
/*			if (v.getId() == R.id.delete_btn) { //删除密码
				if (inputCount <= 0) { //容错处理
					inputCount = 0;
					return;
				}else{
					//密码图标少一个,已经输入的密码减少1位
					if (null == lockPassword) { //设置锁屏密码的删除
						if ( (null == rePassword) || (rePassword.length() < 1) ){ //密码
							if ((null == password) || (password.length() < 1) ) { //没有密码
								return;
							}else{ //有密码
								password = password.substring(0, password.length()-1);
								passwordImages.get(inputCount-1).setVisibility(View.INVISIBLE);
								inputCount--;
								setLockCount--;
								if (inputCount < 0) {
									inputCount = 0;
								}
								if (setLockCount < 0) {
									setLockCount = 0;
								}
							}
						}else{ //重复密码
							rePassword = rePassword.substring(0, rePassword.length()-1);
							passwordImages.get(inputCount-1).setVisibility(View.INVISIBLE);
							inputCount--;
							setLockCount--;
							if (inputCount < 0) {
								inputCount = 0;
							}
						}
					}else{ //输入密码进应用的删除
						if ((null == password) || (password.length() < 1) ){ //已经没有密码数字了
							return;
						}else{ //只有
							password = password.substring(0, password.length()-1);
							passwordImages.get(inputCount-1).setVisibility(View.INVISIBLE);
							inputCount--;
							if (inputCount < 0) {
								inputCount = 0;
							}
						}
					}
				}
				return;
			}else{ //填入密码------------------------------------------------
				passwordImages.get(inputCount).setVisibility(View.VISIBLE);
				inputCount++;
				
				if (null == lockPassword) { //锁屏密码没有,设置锁屏密码
					if (inputCount >= 4) {
						inputCount = 0;
						//全部清空等待输入确认密码
						invisibleAllPassword.sendEmptyMessageDelayed(1, 100);
						mInputTip.setText(getString(R.string.cx_fa_reinput_lockscreen_password));
					}
					
					setLockCount++;
					if (setLockCount <= 4) { //输入的是 密码
						password += getInputData(v.getId());
					}else{ //输入的是确认密码
						rePassword += getInputData(v.getId());
					}
					
					if (setLockCount == 8) { //需要匹对 "密码" 与 "重复密码"
						if ( (!TextUtils.isEmpty(rePassword)) 
								&& (TextUtils.equals(password, rePassword)) ){
							//set password success
							Toast.makeText(CxLockScreen.this, getString(
									R.string.cx_fa_success_setpswd), Toast.LENGTH_SHORT).show();
							
							SharedPreferences sp = getSharedPreferences(
									CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
							Editor editor = sp.edit();
							editor.putString(CxGlobalConst.S_LOCKSCREEN_FIELD, password);
							editor.commit();
							CxLockScreen.this.finish();
						}else{ //不匹配，需要重置，方便下次输入
							setLockCount = 0;
							inputCount = 0;
							password = "";
							rePassword = "";
							Toast.makeText(CxLockScreen.this, getString(
									R.string.cx_fa_different_password), Toast.LENGTH_SHORT).show();
							invisibleAllPassword.sendEmptyMessageDelayed(1, 100);
						}
					}
					
				}else{ //锁屏密码存在,输入密码进入应用（bug,还有可能是取消锁屏密码）
					passwordImages.get(inputCount-1).setVisibility(View.VISIBLE);
					password += getInputData(v.getId());
					if (inputCount == 4) { //需要与设置的密码匹对
						if (TextUtils.equals(lockPassword, password)) { //匹配
							if (1 == fromType) { //cancel password for lockscreen
								//clear lock password
								SharedPreferences sp = getSharedPreferences(
										CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.clear();
								editor.commit();
							}
							CxLockScreen.this.finish();
						}else{ //不匹配需要重置变量
							inputCount = 0;
							password="";
							invisibleAllPassword.sendEmptyMessageDelayed(1, 100);
							Toast.makeText(CxLockScreen.this, getString(
									R.string.cx_fa_wrong_lock_password), Toast.LENGTH_SHORT).show();
						}
					}
					
				}
				
			}*/
			
		}
	};
	
	Handler invisibleAllPassword = new Handler(){
		public void handleMessage(android.os.Message msg) {
			firstImage.setVisibility(View.INVISIBLE);
			secondImage.setVisibility(View.INVISIBLE);
			thirdImage.setVisibility(View.INVISIBLE);
			forthImage.setVisibility(View.INVISIBLE);
		};
	};

	private String getInputData(int btnId){
		switch (btnId) {
		case R.id.one_btn:
			return "1";
		case R.id.two_btn:
			return "2";
		case R.id.three_btn:
			return "3";
		case R.id.four_btn:
			return "4";
		case R.id.five_btn:
			return "5";
		case R.id.six_btn:
			return "6";
		case R.id.seven_btn:
			return "7";
		case R.id.eight_btn:
			return "8";
		case R.id.nine_btn:
			return "9";
		case R.id.none_btn:
			return "";
		case R.id.zero_btn:
			return "0";
		case R.id.delete_btn:
			return "";
		default:
			break;
		}
		
		return "";
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (0 != fromType) { //set password protect
				CxLockScreen.this.finish();
				return true;
			}else{ //password protect
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		super.onPause();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (0 != fromType) { //set password of lockscreen
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		isExist = false;
		super.onDestroy();
	}
	
}
