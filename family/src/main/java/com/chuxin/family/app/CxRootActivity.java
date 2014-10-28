package com.chuxin.family.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v4.app.FragmentActivity;

import com.chuxin.androidpush.sdk.push.RKPush;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.settings.CxLockScreen;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.utils.Push;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author shichao.wang
 *
 */
public class CxRootActivity extends FragmentActivity {
	
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    
	@Override
	protected void onStop() {
		CxLog.i("rkroot", ""+openedResource.size());
		try {
			openedResource.remove(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (openedResource.size() < 1) {
			if (CxGlobalParams.getInstance().isAppStatus()) {
				CxGlobalParams.getInstance().setAppStatus(false);
				
				try {
					((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setMode(AudioManager.MODE_NORMAL);
				} catch (Exception e) {
					e.printStackTrace();
				}  
				//开启push
				RKPush.S_SEND_FLAG = true;
				CxLog.i("rkroot", "app status foreground turn to background");
			}else{
				CxLog.i("rkroot", "app status is background");
			}
		}else{ //
			CxLog.i("rkroot", "app status foreground, size is:"+openedResource.size());
		}
		
		super.onStop();
	}

	private static List<FragmentActivity> openedResource = new ArrayList<FragmentActivity>();

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// 打开push
//		Log.d("PUSH", "onPause() => enablePush");
		Push.getInstance(this).setNotificationType(null);
	}
	
	@Override
	protected void onStart() {
		super.onResume();

		//关闭push
//		Log.d("PUSH", "onResume() => disenablePush");
		Push.getInstance(this).setNotificationType("ignore");
		
		if (!CxGlobalParams.getInstance().isAppNormal()) {
			this.finish();
			return;
		}
		
		CxLog.i("lock", "AppStatus:"+CxGlobalParams.getInstance().isAppStatus());
		if (!CxGlobalParams.getInstance().isAppStatus()) {
			//开启密码保护
			SharedPreferences sp = getSharedPreferences(
					CxGlobalConst.S_LOCKSCREEN_NAME, Context.MODE_PRIVATE);
			CxLog.i("lock", "FIELD:"+sp.getString(CxGlobalConst.S_LOCKSCREEN_FIELD, null)
					+",isExist:"+CxLockScreen.isExist
					+",gpuImage:"+CxGlobalParams.getInstance().isCallGpuimage() );
			if ((null != sp.getString(CxGlobalConst.S_LOCKSCREEN_FIELD, null)) && (!CxLockScreen.isExist) && (!CxGlobalParams.getInstance().isCallGpuimage())) {
				Intent toLock = new Intent(CxRootActivity.this, CxLockScreen.class);
				toLock.putExtra(CxGlobalConst.S_LOCKSCREEN_TYPE, 0); // password protect
				startActivity(toLock);
			}
			
			CxGlobalParams.getInstance().setAppStatus(true);
			CxLog.i("lock", "AppStatus turn to foreground");
			RKPush.S_SEND_FLAG = false;
		}
		openedResource.add(this);
//		super.onResume();
	}

}
