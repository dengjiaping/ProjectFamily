package com.chuxin.family.tabloid;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.main.CxAuthenNew;
import com.chuxin.family.models.TabloidMessage;
import com.chuxin.family.parse.been.data.TabloidCateConfObj;
import com.chuxin.family.parse.been.data.TabloidObj;
import com.chuxin.family.service.CxBackgroundService;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.views.chat.ChatFragment;
import com.chuxin.family.widgets.VoiceTip;
import com.chuxin.family.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

public class TabloidReceiver extends android.content.BroadcastReceiver {
	private String TAG = "TabloidReceiver";
	static final int NOTIFICATION_ID = 0x123;
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent arg1) {
			mContext = context;
			
			CxLog.d(TAG, "TabloidReceiver被触发啦!");
			
			// 往聊天消息库写小报数据
			boolean haveData = sendChatDataForToday();
			if(haveData==false){
				// 本地小报库中已经没有数据了，就直接返回。不用再提醒了
				CxLog.e(TAG, "本地小报库中没有数据了!");
				return;			
			}
			
			// 判断是否在后台，如果在后台就发一个系统通知。如果在前台，就播放一个新消息的提醒音
			if( !CxGlobalParams.getInstance().isAppStatus() ){
				
				// 创建一个启动其他Activity的Intent
				Intent intent 			= new Intent(context, CxAuthenNew.class);
				PendingIntent pi 	= PendingIntent.getActivity(context, 0, intent, 0);
				
				String title		 = context.getResources().getString(R.string.cx_fa_role_app_name);
				String content = context.getResources().getString(R.string.cx_fa_tabloid_reminder_tip_content);
				
				/*
				Notification notify = new Notification.Builder(context)
					.setAutoCancel(true)			// 设置打开该通知，该通知自动消失
					.setTicker("有新消息")			// 设置显示在状态栏的通知提示信息
					.setSmallIcon(R.drawable.chatview_daily_icon)			// 设置通知的图标
					.setContentTitle(title)								// 设置通知内容的标题(写应用的名称)
					.setContentText(content)		// 设置通知内容
					.setDefaults(Notification.DEFAULT_SOUND	|Notification.DEFAULT_LIGHTS)		//  设置使用系统默认的声音、默认LED灯
					//.setSound(Uri.parse("android.resource://org.crazyit.ui/"	+ R.raw.msg))				// 设置通知的自定义声音
					
					.setWhen(System.currentTimeMillis())
					// 设改通知将要启动程序的Intent
					.setContentIntent(pi).build();
				// 发送通知
				// 获取系统的NotificationManager服务
				NotificationManager		nm = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				nm.notify(NOTIFICATION_ID, notify);
				*/
				
				// 为了兼容旧的android版本，所以采用以下方式写
				Notification notify = new Notification(R.drawable.cx_fa_app_icon,content, System.currentTimeMillis() );
				notify.setLatestEventInfo(context, title, content, pi);
				//notify.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.cx_fa_role_push);
				
				Uri soundUri 		= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				notify.defaults 	= (Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
		        notify.flags 			= Notification.FLAG_AUTO_CANCEL;
		     //   notify.number 	= 1;																// 显示有多少条数据，暂时隐藏
		        notify.sound 		= soundUri;
				
				NotificationManager		nm = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				nm.notify(NOTIFICATION_ID, notify);
				
				CxLog.d(TAG, "tabloid receiver 接收通知成功!");
			}else{
				 // 程序在前台，播放新消息提示音
				 VoiceTip.tip(context, VoiceTip.VOICE_TIP_MODE_VIBRATE  | VoiceTip.VOICE_TIP_MODE_VOICE);
			}
			
			// 通知观察者有小报信息了
			CxGlobalParams.getInstance().setHaveTabloidMsg(true);
	}
	
	/**
	 * 推送今天的小报数据
	 * @return
	 */
	private boolean sendChatDataForToday(){
		TabloidDao dao = new TabloidDao(mContext);
		List<TabloidCateConfObj> list = dao.getCateConfList(2);		// 得到所有用户预定的分类
		
		boolean haveData = false;			// 是否有数据
		
		// 先取出要发送的小报数据
		JSONArray arr = new JSONArray();
		for(TabloidCateConfObj cateObj : list){
			int cateId = cateObj.getCategory_id();
			
			TabloidObj obj 	= dao.getTabloidByCateIdForSend(cateId);
			if(obj==null)
				continue;
			
			haveData = true;			
			
			JSONObject jsonObj 	= new JSONObject();
			try{
				jsonObj.put("category_id", cateId);
				jsonObj.put("title", cateObj.getTitle());
				jsonObj.put("id",     obj.getId());
				jsonObj.put("text",  obj.getText());
			}catch(Exception e){
				CxLog.e(TAG, "getSendDataForToday()中出错:" + e.getMessage());
			}
			
			arr.put(jsonObj);
			
			// 删除已发送的数据
			dao.delTabloidById(cateId, obj.getId());
		}
		
		if(!haveData){
			return haveData;
		}
		
		// 写到聊天数据库中
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("id", System.currentTimeMillis());			// 注意这个ID
			jsonObj.put("content", arr.toString());
			jsonObj.put("type", "tabloid");
			jsonObj.put("send_success", 1);
			jsonObj.put("sender", "tabloid");
			jsonObj.put("create_time", (int)(System.currentTimeMillis()/1000) );
		} catch (JSONException e) {
			CxLog.e(TAG, "json转换出错:" + e.getMessage());
		}
		
		TabloidMessage tabloidMessage = new TabloidMessage(jsonObj,  mContext);
		tabloidMessage.put();
		
		return haveData;		
	}

}