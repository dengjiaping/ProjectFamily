package com.chuxin.family.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.Uri;

import com.chuxin.family.global.CxGlobalConst;
/**
 * 音频文件缓存到SD卡,存、取、删、清空
 * @author wangshichao
 *
 */
public class CxFileBaseDiskCache extends CxBaseDiskCache implements DiskCache {
	

	private static final String NOMEDIA = ".nomedia";
    private static final int MIN_FILE_SIZE_IN_BYTES = 100;

    private File mStorageDirectory;
    /**
     * 使用之前确保SD卡可用
     * @param name 音频文件不同用处的目录名
     * @throws Exception 
     */
    public CxFileBaseDiskCache(String name, Context ctx) {
    	super(name, ctx);
//		super(name);
		File storageDirectory = new File(CxGlobalConst.S_CHUXIN_AUDIO_CACHE_PATH, name);
        boolean cacheable = createDirectory(storageDirectory);
        if (!cacheable) {
			try {
				throw new Exception("the sd card is not useable");
			} catch (Exception e) {
			}
		}
        mStorageDirectory = storageDirectory;
	}
    public void store(String key, InputStream is) {
    	if (null == is) {
			return;
		}
    	CxLog.e("store method ", key);
//    	key = System.currentTimeMillis() + ".mp3";
        is = new BufferedInputStream(is);
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getFile(key)));

            byte[] b = new byte[1024];
            int count=0;

            while (-1 != (count = is.read(b)) ) {
                os.write(b, 0, count);
            }
            os.close();
            is.close();
            CxLog.i("store file", " successfully");
        } catch (IOException e) {
        	e.printStackTrace();
            return;
        }
        //
//        try {
//        	FileInputStream fis = new FileInputStream(getFile(key));
//			fis.close();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
    }
    public File getFile(String hash) {
    	if (null == hash) {
			return null;
		}
    	String fileName = Uri.encode(hash);
//    	String fileName = hash;
    	CxLog.i("getFile", fileName);
    	File file;
		try {
			file = new File(mStorageDirectory, fileName);
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
    }
    
}

