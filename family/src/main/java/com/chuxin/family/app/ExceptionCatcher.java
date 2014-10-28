package com.chuxin.family.app;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
/**
 * 
 * @author shichao.wang
 *
 */
public class ExceptionCatcher implements UncaughtExceptionHandler {

	public void uncaughtException(Thread arg0, Throwable arg1) {
		
		if(!Environment.getExternalStorageState()
				.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
			return;
		}
		
		byte[] info = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			PrintStream ps = new PrintStream(baos);
			
			arg1.printStackTrace(ps);
			
			info = baos.toByteArray();
			baos.flush();
			baos.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String rkDic = Environment.getExternalStorageDirectory()
		.getAbsolutePath()+File.separator+"chuxin";
		
		File dicFile = new File(rkDic);
		if (!dicFile.exists()) {
			dicFile.mkdirs();
		}
		
		if (!dicFile.exists()) {
			return;
		}
		
		String errLogPath = rkDic +File.separator +"errlog.txt";
		File errLogFile = new File(errLogPath);
		
		if (!errLogFile.exists()) {
			try {
				errLogFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(errLogFile);
			fos.write(info);
			fos.flush();
			fos.close();
			
		} catch (Exception e) {
		}
		
		try {
			System.exit(0);
		} catch (Exception e) {
		}
		
		/*try {
            RkSendImageApi.getInstance().sendErrorLogFile(errLogPath,
                    new JSONCaller() {

                        @Override
                        public int call(Object result) {
                            return 0;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
	}

}
