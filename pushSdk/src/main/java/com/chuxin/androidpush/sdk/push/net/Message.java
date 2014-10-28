package com.chuxin.androidpush.sdk.push.net;


import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;

import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.RkPushLog;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public abstract class Message {

	public interface Action {
		public abstract int execute();
	}
	
	private static final String TAG = "Message";
    private static final int MSG_TYPE_NOTIFICATION = 1;
    
    public abstract int getMessageType();
    public abstract Message parse(Unpacker unpacker);
    public abstract Action genAction();
    
    public static Message parse(byte[] bytes, int offset, int length) {
    	
    	Message message = null;
        MessagePack msgpack = new MessagePack();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes, offset, length);
        Unpacker unpacker = msgpack.createUnpacker(inputStream);
        int messageId;
        try {
        	unpacker.readArrayBegin();
            messageId = unpacker.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (messageId == MSG_TYPE_NOTIFICATION) {
        	TeeLog.d(TAG, "Receive One NotificationMessage");
            message = new NotificationMessage().parse(unpacker);
        }
        
    	try {
			unpacker.readArrayEnd();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return message;
    }
    
    static class NotificationMessage extends Message {
    	private static final String TAG = "NotificationMessage";

    	private String mApp; //package String
        private int mMessageId; //message id (keep local for dismiss re-message)
        private int mTimestamp; //timestamp
        private int mFlags; //notify flag
        private String mBody; //notify content text
        private int mBadge; //notify numbers
        private String mSound; //notify sound
        private String mExtras; //notify extra data

        @Override
        public int getMessageType() {
            return MSG_TYPE_NOTIFICATION;
        }
        
        private boolean isPackageExists(String targetPackage) {
	        PushAgent push = PushAgent.getInstance();
	        Context context = push.getBindingContext(); 
    		PackageManager pm = context.getPackageManager();
		    try {
			   PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
	        } catch (NameNotFoundException e) {
	    	   return false;
		    }
		    
		    return true;
        }
        
        public Action genAction() {
        	return new Action() {
        		
        	    @Override
        	    public int execute() {
        	        PushAgent push = PushAgent.getInstance();
        	        Context context = push.getBindingContext(); 
        	        
//        	        ComponentName component = push.getStorage().getApp(mApp);
        	        ComponentName component = new ComponentName(mApp, Constant.PUSH_NOTIFICATION_SERVICE);
        	        
        	        Intent intent = new Intent();
        	        intent.setComponent(component);
        	        intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_MSGID, mMessageId);
        	        intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_TIMESTAMP, mTimestamp);
        	        intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_MESSAGE, mBody);
        	        intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_BADGE, mBadge);
        	        if (mSound != null)
        	        	intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_SOUND, mSound);
        	        if (mExtras != null)
        	        	intent.putExtra(Constant.NOTIFY_INTENT_ARGS_FIELD_EXTRAS, mExtras);
        	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       
        	        // verify if I can send out the request;
        	        
        			List<ResolveInfo> list = context.getPackageManager().queryIntentServices(intent, 0);
        			if ((list == null) || (list.size() == 0)) {
        				RkPushLog.w(TAG, "Warn: the TOBE ntotified package cannot be accessed!");
        				
        				// verify if packages exists;
        				if (!isPackageExists(mApp)) {
//	        				AppInfo appInfo = push.getStorage().unregister(mApp);
//	        				if (appInfo != null) {
//	        					push.requestUnregister(mApp, appInfo.mUUID);
//	        				} else {
//	        					push.requestUnregister(mApp, "-");        					
//	        				}
	        				push.requestUnregister(mApp, "-");
        				} 
        				
    					push.requestRead(mApp, mMessageId);
            	        return 0;
        			}

        	        
        	        context.startService(intent);
        	        RkPushLog.i(TAG, "Sending Notify to " + component.getPackageName() + "/" + component.getClassName());
        	        push.requestRead(mApp, mMessageId);
        	        return 0;
        	    }
        	};
        }

        @Override
        public Message parse(Unpacker unpacker) {
        	Value value;
        	
            try {
            	mApp = unpacker.readString();
                mMessageId = unpacker.readInt();
                mTimestamp = unpacker.readInt();
                mFlags = unpacker.readInt();
                mBody = unpacker.readString();
                mBadge = unpacker.readInt();
                
                value = unpacker.readValue();
                if (value.isNilValue()) {
                	mSound = null;
                } else {
                	mSound = value.asRawValue().getString();
                }
                
                try {
					
					RkPushLog.w("", "push content : mApp="+mApp+";mMessageId="+mMessageId
							+";mFlags="+mFlags+";mBody="+mBody+";mBadge="+mBadge
							+";mSound="+mSound);
				} catch (Exception e) {
					e.printStackTrace();
					RkPushLog.e("", "push content error :"+e.toString());
				}
                
                value = unpacker.readValue();
                
                if (value.isNilValue()) {
                	mExtras = null;
                } else {
                	mExtras = value.asRawValue().getString();
                }
                
                TeeLog.d(TAG, "NotificationMessage:" + mApp + mTimestamp + mFlags + mBody);
                return this;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }        
    }
}
