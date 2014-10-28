package com.chuxin.androidpush.sdk.push.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class StoreUtil {

	private static StoreUtil mSelf;
	private String mStoreFile;
	private final boolean writeFlg = false;
	
	private StoreUtil(){
		mStoreFile = Environment.getExternalStorageDirectory().getAbsolutePath()
		+File.separator+"rekoo"+File.separator+"pushLog.txt";
	}
	
	public static StoreUtil getInstance(){
		if (null == mSelf) {
			mSelf = new StoreUtil();
		}
		return mSelf;
	}
	
	public void writeIn(String info){
		if (!writeFlg) {
			return;
		}
		if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File logFile = new File(mStoreFile);
		if (!logFile.exists()) {
			try {
				logFile.getParentFile().mkdirs();
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(logFile, true);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			fos.write(("push event time:"+sdf.format(new Date())).getBytes());
			fos.write(("***"+info).getBytes());
			fos.write("\r\n".getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
