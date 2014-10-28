package com.chuxin.family.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.ShareCore;

import com.chuxin.family.R;

public class CxShareUtil {
	
	private Context mCtx;
	private Handler.Callback mCallback;
	
	private CxShareUtil(){};
	
	public CxShareUtil(Context ctx, Handler.Callback callback){
		this.mCtx = ctx;
		this.mCallback = callback;
	}
	
	public void shareToThird(String comment, String chuxinOpenUrl, 
			String imageUrl, List<Platform> plats) throws Exception{
		if ((null == mCallback) || (null == mCtx)) {
			throw new Exception("any one of UI params can not be null");
		}
		if (null == chuxinOpenUrl) {
			throw new Exception("param chuxinOpenUrl can not be null");
		}
		if ((null == plats) || (plats.size() < 1)) {
			return;
		}
		HashMap<String, Object> params = null;
		try {
			params = getThird(comment, chuxinOpenUrl, imageUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null == params) {
			throw new Exception("params can not construct successfully");
		}
		
		try {
			HashMap<Platform, HashMap<String, Object>> targets 
			= new HashMap<Platform, HashMap<String,Object>>();
			for(Platform plat : plats){
				targets.put(plat, params);
			}
			
			share(targets, mCtx, mCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private HashMap<String, Object> getThird(String comment, 
			String chuxinOpenUrl, String imageUrl){
		HashMap<String,Object> params = new HashMap<String, Object>();
		//仅在微信（包括好友和朋友圈）中使用，否则可以不提供
		params.put("url", chuxinOpenUrl); 
		
		//标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
		params.put("titleUrl", chuxinOpenUrl);
		
		if (!TextUtils.isEmpty(imageUrl)) {
			//是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
			params.put("imageUrl", imageUrl); //网络图片的地址
		}else{ //无图发送老公老婆的ICON
			
		}
		
//		String commentStr = null;
		if (TextUtils.isEmpty(comment)) {
			//标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
			params.put("title", "来自小家APP");
			//是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
//			commentStr = "#老公老婆#"+" "+chuxinOpenUrl;
			//是分享文本，所有平台都需要这个字段
			params.put("text", "来自小家APP");
		}else{
			//标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
			params.put("title", comment);
			//是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
//			commentStr = "#老公老婆#"+comment+" "+chuxinOpenUrl;
			//是分享文本，所有平台都需要这个字段
			params.put("text", comment);
		}
		
		/*if (!TextUtils.isEmpty(commentStr)) {
			params.put("comment", commentStr);
		}*/
		
		try {
			params.put("site", "小家");
			params.put("siteUrl", chuxinOpenUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return params;
	}
	
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;
	
	/** 循环执行分享 */
	private void share(HashMap<Platform, HashMap<String, Object>> shareData,
			Context ctx, Handler.Callback callback) {
		boolean started = false;
		for (Entry<Platform, HashMap<String, Object>> ent : shareData.entrySet()) {
			try {
				Platform plat = ent.getKey();
				String name = plat.getName();
				boolean isWechat = "WechatMoments".equals(name) || "Wechat".equals(name);
				if (isWechat && !plat.isValid()) {
					Message msg = new Message();
					msg.what = MSG_TOAST;
					msg.obj = ctx.getString(R.string.wechat_client_inavailable);
					UIHandler.sendMessage(msg, callback);
					continue;
				}

				HashMap<String, Object> data = ent.getValue();
				int shareType = Platform.SHARE_TEXT;
				String imagePath = null;
				try {
					imagePath = String.valueOf(data.get("imagePath"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (imagePath != null && (new File(imagePath)).exists()) {
					shareType = Platform.SHARE_IMAGE;
					if (data.containsKey("url") && !TextUtils.isEmpty(
							data.get("url").toString())) {
						shareType = Platform.SHARE_WEBPAGE;
					}
				}else {
					Object imageUrl = data.get("imageUrl");
					if (imageUrl != null && !TextUtils.isEmpty(String.valueOf(imageUrl))) {
						shareType = Platform.SHARE_IMAGE;
						if (data.containsKey("url") && !TextUtils.isEmpty(
								String.valueOf(data.get("url")))) {
							shareType = Platform.SHARE_WEBPAGE;
						}
					}
				}
				data.put("shareType", shareType);

				if (!started) {
					started = true;
					showNotification(2000, ctx.getString(
							R.string.sharing), ctx, callback);
				}
				plat.setPlatformActionListener(new PlatformActionListener() {
					
					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {
						CxLog.i("share fail ", arg0.getName()+", arg1:"+arg1+",error:"+arg2.toString());
						showShareResult(/*arg0.getName()+*/mCtx.getString(R.string.share_failed));
					}
					
					@Override
					public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
						CxLog.i("share complete ", "**********************");
						showShareResult(/*arg0.getName()+*/mCtx.getString(R.string.share_completed));
					}
					
					@Override
					public void onCancel(Platform arg0, int arg1) {
						CxLog.i("share cancel ", "**********************");
						showShareResult(/*arg0.getName()+*/mCtx.getString(R.string.share_canceled));
					}
				});
				ShareCore shareCore = new ShareCore();
				shareCore.share(plat, data);
			} catch (Exception e) {
				e.printStackTrace();
				CxLog.i("share", " error:"+e.toString());
			}
		}
	}
	
	// 在状态栏提示分享操作
	@SuppressWarnings("deprecation")
	private void showNotification(long cancelTime, String text, 
			Context ctx, Handler.Callback callback) {
		try {
			Context app = ctx.getApplicationContext();
			NotificationManager nm = (NotificationManager) app
					.getSystemService(Context.NOTIFICATION_SERVICE);
			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);

			long when = System.currentTimeMillis();
			Notification notification = new Notification(R.drawable.cx_fa_app_icon, text, when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(), 0);
			notification.setLatestEventInfo(app, ctx.getString(R.string.share), text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);

			if (cancelTime > 0) {
				Message msg = new Message();
				msg.what = MSG_CANCEL_NOTIFY;
				msg.obj = nm;
				msg.arg1 = id;
				UIHandler.sendMessageDelayed(msg, cancelTime, callback);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showShareResult(final String tipStr){
		if (null == mCtx) {
			return;
		}
		new Handler(mCtx.getMainLooper()){
			public void handleMessage(Message msg) {
				Toast.makeText(mCtx, tipStr, Toast.LENGTH_SHORT).show();
			};
		}.sendEmptyMessage(0);
	}
	
}
