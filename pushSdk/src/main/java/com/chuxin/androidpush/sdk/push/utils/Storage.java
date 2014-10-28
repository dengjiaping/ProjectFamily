package com.chuxin.androidpush.sdk.push.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

interface Storageable {
	public boolean registerApp(String appName, String notifyClassName, String uuid);
	public String getUUID(String appName);
	public ComponentName getPreferPush();
	public String getPreferPackage();
	public int getPreferPackageVersion();
	public boolean setPreferPackage(String packageName, String className, int version);
	public AppInfo unregister(String appName);
	public int getDataVersion();
	public ComponentName getApp(String appName);
	public AppInfo[] getAllApps();
}

public class Storage implements Storageable {
	private static final String TAG = "Storage";
	private Storageable impl;
	private static final int MODE = 1;
	
	// 0 for private;
	// 1 for family;
	// 2 for public;
	
	public Storage(Context context) {
		if (MODE == 0) {
			impl = new PrivateStorageImpl(context);
		} else {
			impl = new FileStorageImpl(context);
		}
	}

	@Override
	public boolean registerApp(String appName, String className, String uuid) {
		return impl.registerApp(appName, className, uuid);
	}

	@Override
	public String getUUID(String appName) {
		return impl.getUUID(appName);
	}

	@Override
	public String getPreferPackage() {
		return impl.getPreferPackage();
	}

	@Override
	public int getPreferPackageVersion() {
		return impl.getPreferPackageVersion();
	}

	@Override
	public boolean setPreferPackage(String packageName, String className, int version) {
		return impl.setPreferPackage(packageName, className, version);
	}

	@Override
	public AppInfo unregister(String appName) {
		return impl.unregister(appName);
	}

	@Override
	public ComponentName getApp(String appName) {
		return impl.getApp(appName);
	}
	
	@Override
	public int getDataVersion() {
		return impl.getDataVersion();
	}
	
	@Override
	public AppInfo[] getAllApps() {
		return impl.getAllApps();
	}

	static class PrivateStorageImpl implements Storageable {
		private SharedPreferences mStorage;
		private String mPackageName;
		
		public PrivateStorageImpl(Context context) {
			mStorage = context.getSharedPreferences(Constant.APP_STORAGE_FILE, Context.MODE_PRIVATE);
			mPackageName = context.getPackageName();
		}
		
		public boolean registerApp(String appName, String className, String uuid) {
			return true;
		}
		
		public String getUUID(String appName) {
			return mStorage.getString(Constant.APP_STORAGE_PREFIX_UUID, null);
		}	
	
		public String getPreferPackage() {
			return mPackageName;
		}
		
		public int getPreferPackageVersion() {
			return Constant.PUSH_VERSION;
		}
	
		public boolean setPreferPackage(String packageName, String className, int version) {
			return true;
		}
		
		public AppInfo unregister(String appName) {
			return null;
		}
		
		public ComponentName getApp(String appName) {
			return new ComponentName(mPackageName, Constant.PUSH_NOTIFICATION_SERVICE);
		}

		@Override
		public int getDataVersion() {
			return 0;
		}

		@Override
		public AppInfo[] getAllApps() {
			return null;
		}

		@Override
		public ComponentName getPreferPush() {
			return null;
		}
	
	}
	
	static class FileStorageImpl implements Storageable {
		private final static String SECRET = "DEMO";
		private Context mBindingContext;
		
		private JSONObject mBackup = null;
		
		private File getStorageFile(boolean p1) {
			return getInternalStorageFile(p1);
		}

		private File getInternalStorageFile(boolean p1) {
		    File path = mBindingContext.getFilesDir();
		    File dataDir = new File(path, Constant.DATA_ROOT_DIR);
		    if (!dataDir.isDirectory()) {
			    boolean rc = dataDir.mkdir();
			    if (!rc) {
			    	TeeLog.e(TAG, "Error: failed to mkdir() for " + dataDir.getAbsolutePath() + " returns " + rc);
			    }
		    }
		    
		    try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    return new File(dataDir, Constant.PUSH_FILE);
		}

		private File getExternalStorageFile(boolean readOnly) {
			
			boolean externalStorageAvailable = false;
			boolean externalStorageWriteable = false;
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
			    // We can read and write the media
			    externalStorageAvailable = externalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			    // We can only read the media
			    externalStorageAvailable = true;
			    externalStorageWriteable = false;
			    
			    if (!readOnly)
			    	return null;
			} else {
			    // Something else is wrong. It may be one of many other states, but all we need
			    //  to know is we can neither read nor write
			    externalStorageAvailable = externalStorageWriteable = false;
		    	return null;
			}
			
		    File path = Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_PICTURES);
		    File dataDir = new File(path, Constant.DATA_ROOT_DIR);
		    dataDir.mkdir();
		    
		    try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    return new File(dataDir, Constant.PUSH_FILE);
		}
		
		
		private static byte[] __d(byte[] content, String password) {  
	        try {  
				KeyGenerator kgen = KeyGenerator.getInstance("AES");  
				kgen.init(128, new SecureRandom(password.getBytes()));  
				SecretKey secretKey = kgen.generateKey();  
				byte[] enCodeFormat = secretKey.getEncoded();  
				SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
				Cipher cipher = Cipher.getInstance("AES");// 创建密码�? 
                cipher.init(Cipher.DECRYPT_MODE, key);// 初始�? 
                byte[] result = cipher.doFinal(content);  
                return result; // 加密  
	        } catch (NoSuchAlgorithmException e) {  
                e.printStackTrace();  
	        } catch (NoSuchPaddingException e) {  
                e.printStackTrace();  
	        } catch (InvalidKeyException e) {  
                e.printStackTrace();  
	        } catch (IllegalBlockSizeException e) {  
                e.printStackTrace();  
	        } catch (BadPaddingException e) {  
                e.printStackTrace();  
	        }  
	        return null;  
		}
		
		private static byte[] __e(String content, String password) {  
	        try {             
                KeyGenerator kgen = KeyGenerator.getInstance("AES");  
                kgen.init(128, new SecureRandom(password.getBytes()));  
                SecretKey secretKey = kgen.generateKey();  
                byte[] enCodeFormat = secretKey.getEncoded();  
                SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
                Cipher cipher = Cipher.getInstance("AES");// 创建密码�? 
                byte[] byteContent = content.getBytes("utf-8");  
                cipher.init(Cipher.ENCRYPT_MODE, key);// 初始�? 
                byte[] result = cipher.doFinal(byteContent);  
                return result; // 加密  
	        } catch (NoSuchAlgorithmException e) {  
                e.printStackTrace();  
	        } catch (NoSuchPaddingException e) {  
                e.printStackTrace();  
	        } catch (InvalidKeyException e) {  
                e.printStackTrace();  
	        } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
	        } catch (IllegalBlockSizeException e) {  
                e.printStackTrace();  
	        } catch (BadPaddingException e) {  
                e.printStackTrace();  
	        }  
	        return null; 
		}
		
		private void _p(JSONObject data) {
			mBackup = data;
			
			File file = getStorageFile(false);
			if (file == null)
				return;
			
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			try {
				output.write(__e(data.toString(), SECRET));
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private JSONObject _g() {
			File file = getStorageFile(true);
			if (file == null)
				return mBackup;
			
			FileInputStream input = null;			
			try {
				input = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return mBackup;
			}
			
			if (file.length() == 0)
				return null;
			
			byte[] data = new byte[(int) file.length()];
			try {
				input.read(data);
				mBackup = new JSONObject(new String(__d(data, SECRET)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return mBackup;
		}
		
		public FileStorageImpl(Context context) {
			mBindingContext = context;
			init();
		}
		
		public JSONObject init() {
			JSONObject entries = null;
			JSONObject root = _g();
			if (root == null) {
				try {
					root = new JSONObject();
					root.put(Constant.APP_STORAGE_FIELD_VERSION, Constant.DATA_FILE_VERSION);
					
					entries = new JSONObject();
					root.put(Constant.APP_STORAGE_FIELD_APP_LIST, entries);

					_p(root);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return root;
		}
	
		public AppInfo unregister(String appName) {
			JSONObject root = init();
			if (root != null) {
				AppInfo result = _getApp(root, appName);
				if (result != null) {
					_delApp(root, appName);
					_p(root);
				}
				return result;
			}
			
			return null;
		}
		
		private static AppInfo _getApp(JSONObject root, String app) {
			JSONObject entries = null;
			try {
				entries = root.getJSONObject(Constant.APP_STORAGE_FIELD_APP_LIST);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			JSONObject entry = null;
			try {
				entry = entries.getJSONObject(app);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			try {
				String packageName = entry.getString(Constant.APP_STORAGE_FIELD_PACKAGE);
				String uuid = entry.getString(Constant.APP_STORAGE_FIELD_UUID);
				String notifyClass = entry.getString(Constant.APP_STORAGE_FIELD_CLASS);
				return new AppInfo(app, uuid, packageName, notifyClass);
			} catch (Exception e) {
				e.printStackTrace();
				return null;				
			}
		}
		
		private static AppInfo[] _getAllApps(JSONObject root) {
			JSONObject entries = null;
			try {
				entries = root.getJSONObject(Constant.APP_STORAGE_FIELD_APP_LIST);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (entries != null) {
				List<AppInfo> appInfos = new ArrayList<AppInfo>();
				Iterator<?> itor = entries.keys();
				while (itor.hasNext()) {
					String app = (String)itor.next();
					if (app.length() > 0) {
						AppInfo appInfo = _getApp(root, app);
						if (appInfo != null) {
							appInfos.add(appInfo);
						}
					}
				}
				
				return appInfos.toArray(new AppInfo[appInfos.size()]);
			}
			
			return null;
		}
		
		private static void _delApp(JSONObject root, String app) {
			JSONObject entries = null;
			try {
				entries = root.getJSONObject(Constant.APP_STORAGE_FIELD_APP_LIST);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (entries != null)
				entries.remove(app);
		}
		
		private static boolean _addApp(JSONObject root, String app, String className, String uuid) throws JSONException {
			
			try {
				AppInfo appInfo = _getApp(root, app);
				if ((appInfo != null) &&
						(appInfo.equals(new AppInfo(app, uuid, app, className))))
					return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JSONObject entries = null;
			try {
				entries = root.getJSONObject(Constant.APP_STORAGE_FIELD_APP_LIST);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (entries == null) {
				entries = new JSONObject();
				root.put(Constant.APP_STORAGE_FIELD_APP_LIST, entries);
			}
			
			JSONObject data = new JSONObject();
			data.put(Constant.APP_STORAGE_FIELD_PACKAGE, app);
			data.put(Constant.APP_STORAGE_FIELD_UUID, uuid);
			data.put(Constant.APP_STORAGE_FIELD_CLASS, className);
			
			entries.put(app, data);
			
			int dataVersion = 0;
			try {
				dataVersion = root.getInt(Constant.APP_STORAGE_FIELD_DATA_VERSION);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataVersion += 1;
			root.put(Constant.APP_STORAGE_FIELD_DATA_VERSION, dataVersion);
			
			return true;
		}
		
		public boolean registerApp(String appName, String notifyClassName, String uuid) {
			boolean rc = false;
			try {
				JSONObject root = init();
				rc = _addApp(root, appName, notifyClassName, uuid);	
				_p(root);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return rc;
		}
		
		public ComponentName getApp(String appName) {
			JSONObject root = init();
			
			AppInfo info = _getApp(root, appName);
			if (info == null)
				return null;
			
			String packageName = info.mPackageName;
			String className = info.mNotifyClassName;
			
			return new ComponentName(packageName, className);		
		}
		
		public String getUUID(String appName) {		
			JSONObject root = init();
			
			AppInfo info = _getApp(root, appName);
			if (info == null)
				return null;
			
			return info.mUUID;
		}	
	
		public String getPreferPackage() {
			try {
				JSONObject root = init();
				return root.getString(Constant.APP_STORAGE_FIELD_PREFER_PACKAGE_NAME);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public ComponentName getPreferPush() {
			try {
				JSONObject root = init();
				String packageName = root.getString(Constant.APP_STORAGE_FIELD_PREFER_PACKAGE_NAME);
				String className = root.getString(Constant.APP_STORAGE_FIELD_PREFER_CLASS_NAME);
				return new ComponentName(packageName, className);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public int getPreferPackageVersion() {
			try {
				JSONObject root = init();
				return root.getInt(Constant.APP_STORAGE_FIELD_PREFER_PACKAGE_VERSION);
			} catch (JSONException e) {
				e.printStackTrace();
				return 0;
			}
		}
	
		public boolean setPreferPackage(String packageName, String className, int version) {
			try {
				JSONObject root = init();
				root.put(Constant.APP_STORAGE_FIELD_PREFER_PACKAGE_NAME, packageName);
				root.put(Constant.APP_STORAGE_FIELD_PREFER_CLASS_NAME, className);
				root.put(Constant.APP_STORAGE_FIELD_PREFER_PACKAGE_VERSION, version);
				_p(root);
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
			return true;		
		}


		@Override
		public int getDataVersion() {
			JSONObject root = init();
			try {
				return root.getInt(Constant.APP_STORAGE_FIELD_DATA_VERSION);
			} catch(Exception e) {
				return 0;
			}
		}


		@Override
		public AppInfo[] getAllApps() {
			JSONObject root = init();
			return _getAllApps(root);
		}
			
	}

	@Override
	public ComponentName getPreferPush() {
		return impl.getPreferPush();
	}

}