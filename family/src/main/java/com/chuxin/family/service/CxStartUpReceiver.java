package com.chuxin.family.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.chuxin.family.global.CxGlobalParams;

public class CxStartUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (TextUtils.equals(intent.getAction(), "RK_ALERM_NOTIFY")) {
			new AlarmProcessor(context, intent.getStringExtra("uid"), 
					intent.getStringExtra("rid")).start();
			
			return;
		}
		
		if (intent.getAction().equals("WAKE_UP")) {
			Intent rkService = new Intent(context, CxBackgroundService.class);
			context.startService(rkService);
			return;
		}
		
		if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
			Intent rkService = new Intent(context, CxBackgroundService.class);
			rkService.putExtra("INIT", 3);
			context.startService(rkService);
			return;
		}
		
	}
	
	//在此处理是担心service挂掉，提醒没有及时响应
	class AlarmProcessor extends Thread{
		private Context mContext;
		private String mUerId; //该闹钟的用户ID
		private String mRecordId; //闹钟本地记录ID
		
		public AlarmProcessor(Context context, String uerId, String recordId){
			this.mContext = context;
			this.mUerId = uerId;
			this.mRecordId = recordId;
		}

		@Override
		public void run() {
			if ( (null == mUerId) || (null == mRecordId) 
					|| (!TextUtils.equals(CxGlobalParams.getInstance().getUserId(), mUerId)) ) {
				//异常（没有userid和recordId的情况属于异常）或与当前用户userId不匹配的情况不处理
				return;
			}
			
			//调用提醒中的接口
//			getc
			
//			super.run();
		}
		
	}

}
