package com.chuxin.androidpush.sdk.push;

import com.chuxin.androidpush.sdk.push.net.PushAgent;
import com.chuxin.androidpush.sdk.push.utils.Constant;
import com.chuxin.androidpush.sdk.push.utils.RkPushLog;
import com.chuxin.androidpush.sdk.push.utils.StoreUtil;
import com.chuxin.androidpush.sdk.push.utils.TeeLog;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

@SuppressLint("NewApi")
public class PushService extends Service {
	
	private static final String TAG = "Push";
	private static boolean sIsRunnig = false;
	private static final long POKE_INTERVAL = 60 * 1000;
    enum NetworkState {
    	NETWORK_OFF,
    	NETWORK_WIFI_ON,
    	NETWORK_CELL_ON,
    };
    
	private boolean initFlag = false;
    private NetworkState mNetworkState = NetworkState.NETWORK_WIFI_ON;
    private PushAgent mAgent;
    private long mLastPokeTimestamp = 0;
    
    private void init() {
    	if (initFlag)
    		return;

    	initFlag = true;
    	mAgent = PushAgent.getInstance();
    	mAgent.init(getApplicationContext());
    	
    	updateNetworkState();
    	updateStrictModePolicy();
    	SystemEventsHandler.triggerNextAlarm(this);
    }
    
    public static boolean isRunning() {
    	return sIsRunnig;
    }
    
    private void updateStrictModePolicy() {
        if(Build.VERSION.SDK_INT >= 9) {
        	try {
		    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    	StrictMode.setThreadPolicy(policy);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
    	}
    }
        
    private boolean processIntent(Intent intent) {
    	RkPushLog.i(TAG, "----------rkpush processIntent method--------");
    	StoreUtil.getInstance().writeIn("----------rkpush processIntent method--------");
    	Bundle bundle = intent.getExtras();
    	int action = bundle.getInt(Constant.PUSH_INTENT_ARGS_ACTION, -1);
    	RkPushLog.i(TAG, "----------rkpush processIntent method--------action="+action);
    	StoreUtil.getInstance().writeIn("----------rkpush processIntent method--------action="+action);
    	if (action == -1) {
    		TeeLog.d(TAG, "invalid action specified!");
    		StoreUtil.getInstance().writeIn("invalid action specified!");
    		return false;
    	}
    	
    	switch (action) {
    	
    		case Constant.PUSH_INTENT_ACTION_QUIT: {
    			TeeLog.d(TAG, "Invoking PUSH_INTENT_ACTION_QUIT");
    			mAgent.quit();
    			stopSelf();
    		}
    		return false;
	    	
	    	case Constant.PUSH_INTENT_ACTION_LAUNCH: {
	    		int version = bundle.getInt(Constant.PUSH_INTENT_ARGS_VERSION,
	    									Constant.PUSH_VERSION);
	    		boolean networkStateChanges = bundle.getBoolean(Constant.PUSH_INTENT_ARGS_NETWORK_CHANGE,
	    														false);
	    		int flags = bundle.getInt(Constant.PUSH_INTENT_ARGS_FLAGS,
	    								  0);
	    		
//	    		switch (flags) {
//	    			case 0:
//	    				TeeLog.d(TAG, "MAN action"); break;
//	    			case 1:
//	    				TeeLog.d(TAG, "BOOT action"); break;
//	    			case 2:
//	    				TeeLog.d(TAG, "ALARM action"); break;
//	    			case 3:
//	    				TeeLog.d(TAG, "NETCHG action"); break;
//	    			case 4:
//	    				TeeLog.d(TAG, "LOCAL APPs changed"); break;
//    				default:
//    					TeeLog.d(TAG, "Unkown action " + flags); break;
//	    		}

	    		poke(flags);
	    	}
    		return true;
	    	
//	    	case Constant.PUSH_INTENT_ACTION_REGISTER: {
//	    		int version = bundle.getInt(Constant.PUSH_INTENT_ARGS_VERSION,
//						Constant.PUSH_VERSION);
//	    		String packageName = bundle.getString(Constant.PUSH_INTENT_ARGS_PACKAGE_NAME);
//	    		String className = bundle.getString(Constant.PUSH_INTENT_ARGS_NOTIFY_CLASS_NAME);
//	    		String uuid = bundle.getString(Constant.PUSH_INTENT_ARGS_UUID);
//	    		mAgent.getStorage().registerApp(packageName, className, uuid);
//	        	poke(4);
//	    	}
//    		return;
    	}
    	
		return false;
    }

    private void updateNetworkState() {
    	Context context = getApplicationContext();
    	
    	ConnectivityManager cm =
    	        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	 
    	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	if (activeNetwork == null) {
    		mNetworkState = NetworkState.NETWORK_OFF;
    		return;
    	}
    	
    	boolean isConnected = activeNetwork.isConnectedOrConnecting();
    	
    	if (!isConnected) {
    		mNetworkState = NetworkState.NETWORK_OFF;
    	} else {
    		if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
    			mNetworkState = NetworkState.NETWORK_WIFI_ON;
    		} else {
    			mNetworkState = NetworkState.NETWORK_CELL_ON;
    		}
    	}
    }
    
        
    public void poke(int flags) {
//    	TeeLog.d(TAG, "poke() " + getPackageName());
    	RkPushLog.i(TAG, "flags="+flags);
    	
        if ((3 == flags) || (mNetworkState == NetworkState.NETWORK_OFF)) {
        	// if there is network status change or network is down;
        	// update network status;
        	RkPushLog.i(TAG, "ready to updateNetworkState");
        	StoreUtil.getInstance().writeIn("ready to updateNetworkState");
        	updateNetworkState();
        }

        if (mNetworkState != NetworkState.NETWORK_OFF) {
        	if (mAgent.isOnline()) {
//	        	if (4 == flags) {
//	        		// local apps changed, notify it.
//	        		mAgent.requestRegister();
//	        	}
        	
	        	RkPushLog.i(TAG, "PushAgent is online, ignore!");
	        	StoreUtil.getInstance().writeIn("PushAgent is online, ignore!");
	            return;
        	} else {
            	RkPushLog.i(TAG, "The network is fine, try re-connect the PushAgent!");
            	StoreUtil.getInstance().writeIn("The network is fine, try re-connect the PushAgent!");
            	mAgent.launchPolling();        
        		
        	}
        } else {
        	mAgent.quit();
        	RkPushLog.i(TAG, "PushAgent is offline, quite push!");
        	StoreUtil.getInstance().writeIn("PushAgent is offline, quite push!");
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	RkPushLog.i(TAG, "service onStartCommand method--------");
    	StoreUtil.getInstance().writeIn("PushService onStartCommand method--------");
        super.onStartCommand(intent, flags, startId);
        
        /*init();

        boolean launch = false;
        
        if (intent != null) {
            launch = processIntent(intent);
        }

        if (launch)
        	return START_NOT_STICKY;
        else
        	return START_STICKY;*/
        new CommandTask().execute(intent);
        return START_STICKY;
        
    }

    @Override  
    public void onCreate() {  
        super.onCreate();  

        sIsRunnig = true;
		TeeLog.init(this);
    } 

    @Override  
    public void onDestroy() {  
        super.onDestroy();
        sIsRunnig = false;
        TeeLog.d(TAG, getPackageName() + ".PushService::onDestory() is invoked.");
    }
    
    public static void launch(Context context,
    						  ComponentName target,
    						  boolean networkChange,
    						  int flags) {
    	if (target == null) {
    		// so luanch myself;
    		target = new ComponentName(context, PushService.class);
    	}
    	
		Intent intent = new Intent()
				.setComponent(target)
				.putExtra(Constant.PUSH_INTENT_ARGS_ACTION, Constant.PUSH_INTENT_ACTION_LAUNCH)
				.putExtra(Constant.PUSH_INTENT_ARGS_VERSION, Constant.PUSH_VERSION)
				.putExtra(Constant.PUSH_INTENT_ARGS_NETWORK_CHANGE, networkChange)
				.putExtra(Constant.PUSH_INTENT_ARGS_FLAGS, flags);
		context.startService(intent);
    }
        
    public static void register(Context context,
    							ComponentName target,
    							String packageName,
    							String notifyClassName,
    							String uuid) {
    	if (target == null) {
    		// so luanch myself;
    		target = new ComponentName(context, PushService.class);
    	}
    	
		Intent intent = new Intent()
				.setComponent(target)
				.putExtra(Constant.PUSH_INTENT_ARGS_ACTION, Constant.PUSH_INTENT_ACTION_REGISTER)
				.putExtra(Constant.PUSH_INTENT_ARGS_VERSION, Constant.PUSH_VERSION)
				.putExtra(Constant.PUSH_INTENT_ARGS_PACKAGE_NAME, packageName)
				.putExtra(Constant.PUSH_INTENT_ARGS_NOTIFY_CLASS_NAME, notifyClassName)
				.putExtra(Constant.PUSH_INTENT_ARGS_UUID, uuid);
		context.startService(intent);
	}
    
//    public static void stop(Context context,
//    						ComponentName target) {
//    	Intent intent = new Intent().setComponent(target);
//    	context.stopService(intent);
//    }
//    
//    public static void stopMyself(Context context) {
//    	Intent intent = new Intent(context, PushService.class);
//    	context.stopService(intent);
//    }
    
    public static void stop(Context context,
			ComponentName target) {
		Intent intent = new Intent()
			.setComponent(target)
			.putExtra(Constant.PUSH_INTENT_ARGS_ACTION, Constant.PUSH_INTENT_ACTION_QUIT)
			.putExtra(Constant.PUSH_INTENT_ARGS_VERSION, Constant.PUSH_VERSION);
		context.startService(intent);
	}

	public static void stopMyself(Context context) {
		if (isRunning()) {
			Intent intent = new Intent(context, PushService.class)
				.putExtra(Constant.PUSH_INTENT_ARGS_ACTION, Constant.PUSH_INTENT_ACTION_QUIT)
				.putExtra(Constant.PUSH_INTENT_ARGS_VERSION, Constant.PUSH_VERSION);
			context.startService(intent);
		}
	}
	
	class CommandTask extends AsyncTask<Intent, Integer, Integer>{

		@Override
		protected Integer doInBackground(Intent... params) {
			init();

	        if (params[0] != null) {
	            processIntent(params[0]);
	        }

			return null;
		}
		
	}
	
}
