package com.chuxin.androidpush.sdk.push.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.util.Log;

public class TeeLog {
	private static final int LOGGING_STDOUT = 0x01;
	private static final int LOGGING_FILE   = 0x02;
//	private static final int LOGGING_FLAGS = (LOGGING_STDOUT | LOGGING_FILE);
	private static final int LOGGING_FLAGS = (LOGGING_STDOUT);
	
	private static FileLogger mFileLogger = null;
	
	public static void init(Context context) {
		if ((LOGGING_FLAGS & LOGGING_FILE) == LOGGING_FILE) {
			if (mFileLogger == null) {
				mFileLogger = new FileLogger(context);
			}
		}
	}
	
	static class FileLogger {
		private Logger mLogger;		
		private static final int LOG_FILE_SIZE = 1024 * 1024;
		public static String LOG_FILE_PATH = null;
		
		private static String getLogFile(Context context, String tag) {
			File path = context.getFilesDir();
		    File dataDir = new File(path, Constant.DATA_ROOT_DIR);
		    if (!dataDir.isDirectory())
		    	dataDir.mkdir();
		    
		    try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    return new File(dataDir, tag + Constant.LOG_FILE).getAbsolutePath();
		}
		
		public FileLogger(Context context) {
			if (LOG_FILE_PATH == null) {
				String packageName = context.getPackageName();
				LOG_FILE_PATH = getLogFile(context, packageName);
				
				if (LOG_FILE_PATH == null) {
					mLogger = null;
					return;
				}
			}
			
			mLogger = Logger.getLogger(Constant.SDK_INTERNAL_LOG_FILE);
            mLogger.setLevel(Level.ALL);

            FileHandler fileHandler = null;
			try {
				fileHandler = new FileHandler(LOG_FILE_PATH, LOG_FILE_SIZE, 1, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mLogger = null;
				return;
			}
			
            fileHandler.setLevel(Level.ALL);
            mLogger.addHandler(fileHandler);
            mLogger.info("init()");
		}
		
		public void close() {
			if (mLogger == null)
				return;
			
			Handler[] handlers = mLogger.getHandlers();
			for (int i = 0; i < handlers.length; i++) {
				handlers[i].close();
			}
			for (int i = 0; i < handlers.length; i++) {
				mLogger.removeHandler(handlers[i]);
			}
		}

		public void w(final String TAG, final String msg) {
			if (mLogger == null)
				return;
			
			String log = TAG + ":" + msg;
			synchronized(mLogger) {
				mLogger.warning(log);
			}			
		}
		
		public void e(final String TAG, final String msg) {
			if (mLogger == null)
				return;

			String log = TAG + ":" + msg;
			synchronized(mLogger) {
				mLogger.severe(log);
			}			
		}
		
		public void i(final String TAG, final String msg) {
			if (mLogger == null)
				return;
			
			String log = TAG + ":" + msg;
			synchronized(mLogger) {
				mLogger.info(log);
			}			
		}
		
		public void d(final String TAG, final String msg) {
			if (mLogger == null)
				return;

			String log = TAG + ":" + msg;
			synchronized(mLogger) {
				mLogger.fine(log);
			}			
		}
	};

	public static void w(final String TAG, final String msg) {
		if ((LOGGING_FLAGS&LOGGING_STDOUT) == LOGGING_STDOUT)
			Log.w(TAG, msg);
		
		if ((LOGGING_FLAGS&LOGGING_FILE) == LOGGING_FILE)
			if (mFileLogger != null)
				mFileLogger.w(TAG, msg);
	}
	
	public static void s(final String code, final String msg) {
		String desc = "ErrorCode: " + code + ", " + msg; 
		if ((LOGGING_FLAGS&LOGGING_STDOUT) == LOGGING_STDOUT)
			Log.e(Constant.SDK_NAME, desc);
		
		if ((LOGGING_FLAGS&LOGGING_FILE) == LOGGING_FILE)
			if (mFileLogger != null)
				mFileLogger.e(Constant.SDK_NAME, desc);
	}
	
	public static void e(final String TAG, final String msg) {
		if ((LOGGING_FLAGS&LOGGING_STDOUT) == LOGGING_STDOUT)
			Log.e(TAG, msg);
		
		if ((LOGGING_FLAGS&LOGGING_FILE) == LOGGING_FILE)
			if (mFileLogger != null)
				mFileLogger.e(TAG, msg);
	}
	
	public static void i(final String TAG, final String msg) {
		if ((LOGGING_FLAGS&LOGGING_STDOUT) == LOGGING_STDOUT)
			Log.i(TAG, msg);
		
		if ((LOGGING_FLAGS&LOGGING_FILE) == LOGGING_FILE)
			if (mFileLogger != null)
				mFileLogger.i(TAG, msg);
	}
	
	public static void d(final String TAG, final String msg) {
		if ((LOGGING_FLAGS&LOGGING_STDOUT) != 0)
			Log.d(TAG, msg);

		if ((LOGGING_FLAGS&LOGGING_FILE) == LOGGING_FILE)
			if (mFileLogger != null)
				mFileLogger.d(TAG, msg);

	}
}
