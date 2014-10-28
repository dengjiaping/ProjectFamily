package com.chuxin.androidpush.sdk.push.utils;

public class Constant {
	
	/* common prefixes */
	public  static final String SDK_NAME			= "RKPushSDK";
	private static final String PREFIX_OFFICAL		= "RK";
	private static final String PREFIX_INTERNAL		= "__rk";
	private static final String SDK_PACKAGE			= "com.chuxin.androidpush.sdk";
	
	/* error codes */
	public static final String ERROR_CODE_NO_VALID_META_ENTRY		= "10000-001";
	public static final String ERROR_CODE_NO_VALID_META_VALUE		= "10000-002";
	public static final String ERROR_CODE_NO_THIS_PACKAGE_INFO		= "10000-003";
	public static final String ERROR_CODE_REMOTE_SERVER_DOWN		= "10000-004";

	/* general defines */
	public static final String PUSH_VERSION_META = "__" + PREFIX_OFFICAL + "_PUSH_KEY__";
	public static final int PUSH_VERSION = 1;
	public static final int DATA_FILE_VERSION = 1;

	/* storage file */
	public static final String PUSH_RES_RAW_FILE = PREFIX_INTERNAL + "_push";

	/* activity names */
	public static final String PUSH_NOTIFICATION_SERVICE = SDK_PACKAGE + ".push.NotificationService";
	public static final String ALARM_ACTION = SDK_PACKAGE + ".push.SystemEventsHandler.ALARM_ACTION";

	// push service interface
	public static final String PUSH_SERVICE_ACTION = SDK_PACKAGE + ".push.PushService.ACTION";
	public static final String PUSH_SERVICE_CLASS_NAME = SDK_PACKAGE + ".push.PushService";

	public static final String PUSH_INTENT_ARGS_ACTION = PREFIX_INTERNAL + "_a";
	public static final String PUSH_INTENT_ARGS_VERSION = PREFIX_INTERNAL + "_v";
	public static final String PUSH_INTENT_ARGS_APPID = PREFIX_INTERNAL + "_i";
	public static final String PUSH_INTENT_ARGS_UUID = PREFIX_INTERNAL + "_u";
	public static final String PUSH_INTENT_ARGS_CLASS = PREFIX_INTERNAL + "_c";
	public static final String PUSH_INTENT_ARGS_NETWORK_CHANGE = PREFIX_INTERNAL + "_n";
	public static final String PUSH_INTENT_ARGS_FLAGS = PREFIX_INTERNAL + "_f";
	public static final String PUSH_INTENT_ARGS_PACKAGE_NAME = PREFIX_INTERNAL + "_p";
	public static final String PUSH_INTENT_ARGS_NOTIFY_CLASS_NAME = PREFIX_INTERNAL + "_nc";
	
	public static final int PUSH_INTENT_ACTION_LAUNCH = 0;
	public static final int PUSH_INTENT_ACTION_REGISTER = 1;
	public static final int PUSH_INTENT_ACTION_UNREGISTER = 2;
	public static final int PUSH_INTENT_ACTION_QUIT = 3;
	
	// activity status
	public static final String ACTIVITY_STATUS_FILE = PREFIX_INTERNAL + "s";
	public static final String ACTIVITY_STATUS_FIELD = PREFIX_INTERNAL + "_s";
	
	// storage

	public final static String DATA_ROOT_DIR = "." + PREFIX_INTERNAL;
	public final static String PUSH_FILE = ".push_1";
	public final static String LOG_FILE = ".log_1";
	
	public static final String APP_STORAGE_FILE = PREFIX_INTERNAL + "_c";
	public static final String APP_STORAGE_FIELD_DATA_VERSION = PREFIX_INTERNAL + "_dv";
	public static final String APP_STORAGE_FIELD_VERSION = PREFIX_INTERNAL + "_v";
	public static final String APP_STORAGE_FIELD_PREFER_PACKAGE_NAME = PREFIX_INTERNAL + "_pp";
	public static final String APP_STORAGE_FIELD_PREFER_CLASS_NAME = PREFIX_INTERNAL + "_pc";
	public static final String APP_STORAGE_FIELD_PREFER_PACKAGE_VERSION = PREFIX_INTERNAL + "_pv";
	public static final String APP_STORAGE_FIELD_APP_LIST = PREFIX_INTERNAL + "_apps";
	
	public static final String APP_STORAGE_FIELD_PACKAGE = PREFIX_INTERNAL + "_p";
	public static final String APP_STORAGE_FIELD_CLASS = PREFIX_INTERNAL + "_c";
	public static final String APP_STORAGE_FIELD_UUID = PREFIX_INTERNAL + "_i";
	
	public static final String APP_STORAGE_PREFIX_PACKAGE = "p|";
	public static final String APP_STORAGE_PREFIX_CLASS = "c|";
	public static final String APP_STORAGE_PREFIX_UUID = "i|";
	public static final String APP_STORAGE_PREFIX_APP = "a|";
	
	public static final String SDK_INTERNAL_LOG_FILE = PREFIX_INTERNAL + "_log";

	public static final String SDK_INTERNAL_STORAGE_FILE = PREFIX_INTERNAL + "_push";
	public static final String SDK_INTERNAL_STORAGE_FIELD_ALARM_TICK = PREFIX_INTERNAL + "_a";
	public static final String SDK_INTERNAL_STORAGE_FIELD_LAST_MSGID = PREFIX_INTERNAL + "_m";
	public static final String SDK_INTERNAL_STORAGE_FIELD_NOTIFICATION_TYPE = PREFIX_INTERNAL + "_n";
	public static final String SDK_INTERNAL_STORAGE_FIELD_LAST_BMSGIDS = PREFIX_INTERNAL + "_bm";
	
	// notify
	public static final String NOTIFY_INTENT_ARGS_FIELD_HASH = PREFIX_INTERNAL + "_h";
	public static final String NOTIFY_INTENT_ARGS_FIELD_MSGID = PREFIX_INTERNAL + "_i";
	public static final String NOTIFY_INTENT_ARGS_FIELD_TIMESTAMP = PREFIX_INTERNAL + "_s";
	public static final String NOTIFY_INTENT_ARGS_FIELD_TITLE = PREFIX_INTERNAL + "_t";
	public static final String NOTIFY_INTENT_ARGS_FIELD_MESSAGE = PREFIX_INTERNAL + "_m";
	public static final String NOTIFY_INTENT_ARGS_FIELD_SOUND = PREFIX_INTERNAL + "_a";
	public static final String NOTIFY_INTENT_ARGS_FIELD_BADGE = PREFIX_INTERNAL + "_b";
	public static final String NOTIFY_INTENT_ARGS_FIELD_EXTRAS = PREFIX_INTERNAL + "_e";
	
	public static final int NOTIFY_INTENT_CODE = 11213;
}
