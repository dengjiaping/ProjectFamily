package com.chuxin.family.utils;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chuxin.family.app.CxApplication;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.R;

public class Globals {
	private static final Globals instance = new Globals();
	private static final String TAG = "Globals"; 
//	private static String sMyName = null;
//	private static String sMyCapticalName = null;
//	private static String sPartnerName = null;
//	private static String sPartnerCapitalName = null;
	private static String mClientVersion = "0.0.1";
	
//	private int mProjectCode = 0; // 0 for wife, 1 for husband
//	private String mUid;  // user uid
//	private String mVia;  // login type: QQ/Weibo
//	private String mAccount; // account;
//	private String mToken; // user token
//	private String mParnterUid;
//	private String mPairId = "";
//	private String mPollingTimer;
	
	private boolean mCalendarIsLandSpace = false;
	
	static class PhraseData {
		public int mRes;
		public String mMsg;
		public PhraseData(int res, String msg) {
			mRes = res;
			mMsg = msg; 
		}
	};
	
	
//	private HashMap<String, PhraseData> mPhraseMap = null;
//	private HashMap<String, Integer> mFaceMap = null;
//	private String mPackageName = null;
	
	public void registerPollingHandler() {
		
	}
	
	
	private Globals() {
	}
	
//	public String getMyIconUrl() {
//		return "http://192.168.1.46:447/static/01.png";
//	}
//	
//	public String getPartnerIconUrl() {
//		return "http://192.168.1.46:447/static/02.png";
//	}

	public static Globals getInstance() {
		return instance;
	}
	
//	public boolean amIHusband() {
//		return (mProjectCode == 0);
//	}
//	
//	public boolean amIWife() {
//		return (mProjectCode == 1);
//	}

//	public String getMyName(Resources resources) {
//		if (sMyName != null)
//			return sMyName;
//
//		if (amIWife())
//			sMyName = resources.getString(R.string.cx_fa_nls_wife);
//		else
//			sMyName = resources.getString(R.string.cx_fa_nls_husband);
//		return sMyName;
//	}

//	public String getMyCapitalName(Resources resources) {
//		if (sMyCapticalName != null)
//			return sMyCapticalName;
//
//		if (amIWife())
//			sMyCapticalName = resources.getString(R.string.cx_fa_nls_capital_wife);
//		else
//			sMyCapticalName = resources.getString(R.string.cx_fa_nls_capital_husband);
//		return sMyCapticalName;
//	}

//	public String getPartnerName(Resources resources) {
//		if (sPartnerName != null)
//			return sPartnerName;
//
//		if (amIWife())
//			sPartnerName = resources.getString(R.string.cx_fa_nls_husband);
//		else
//			sPartnerName = resources.getString(R.string.cx_fa_nls_wife);
//		return sPartnerName;
//	}

//	public String getPartnerCapitalName(Resources resources) {
//		if (sPartnerCapitalName != null)
//			return sPartnerCapitalName;
//
//		if (amIWife())
//			sPartnerCapitalName = resources.getString(R.string.cx_fa_nls_capital_wife);
//		else
//			sPartnerCapitalName = resources.getString(R.string.cx_fa_nls_capital_husband);
//		return sPartnerCapitalName;
//	}
//
//	public String getUid() {
//		return mUid;
//	}
//
//	public void setUid(String mUid) {
//		this.mUid = mUid;
//		
//		createDirectory(getRootDir());
//		createDirectory(getUserDir());
//	}
//
//	public String getVia() {
//		return mVia;
//	}
//
//	public void setVia(String mVia) {
//		this.mVia = mVia;
//	}
//
//	public String getAccount() {
//		return mAccount;
//	}

//    public void setPackageName(String packageName) {
//    	Log.d(TAG, "setPackageName:" + packageName);
//    	mPackageName = packageName;
//    }
//    
//    public String getPackageName() {
//    	return mPackageName;
//    }


//	public void setAccount(String mAccount) {
//		this.mAccount = mAccount;
//	}
//
//	public String getToken() {
//		return mToken;
//	}
//
//	public void setToken(String mToken) {
//		this.mToken = mToken;
//	}
//	
//	public void dump() {
//		Log.d(TAG, "Globals: via=" + mVia + ", account=" + mAccount + ", token=" + mToken + ", uid=" + mUid);
//	}

	public String getClientVersion() {
		return mClientVersion;
	}
	
//	public String getRootDir() {
//		//return "/data/data/" + mPackageName + "/";
//	    return "/data/data/" + mPackageName + "/";
//	}
//
//	public String getUserDir() {
//		//return "/data/data/" + mPackageName + "/" + this.mUid + "-" + this.mPairId + "/";
//	  return "/data/data/" + mPackageName + "/" + RkGlobalParams.getInstance().getUserId() + "_" +RkGlobalParams.getInstance().getPairId() + "/";
//	}

//	public String createUserDir() {
//		String userDir = getUserDir(); 
//		createDirectory(userDir);
//		return userDir;
//	}
//
//    private static final void createDirectory(String path) {
//    	File dir = new File(path);
//        if (!dir.exists()) {
//    		dir.mkdirs();
//        }
//    }

//	public SQLiteDatabase openOrCreateMyDatabase(String prefix) {
//		String dbFile = createUserDir() + prefix + ".db";
//		return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
//	}
	public SQLiteDatabase openOrCreateMyDatabase(Context context) {
//	    mPackageName = context.getPackageName();
	    //RkLog.v(TAG, "mPackageName =" + mPackageName);
//        String dbFile = createUserDir() + prefix + ".db";
		context=CxApplication.getInstance().getContext();
	    if(null == context){
	        return null;
	    }
	   
	    CxLog.d(TAG, "context>>>" + context);
	    CxLog.d(TAG, "chatframgment pairid>>>" + CxGlobalParams.getInstance().getPairId());
	    SQLiteDatabase sdb;
        synchronized (TAG) {
            sdb = context.openOrCreateDatabase(CxGlobalParams.getInstance().getUserId() + "_"
                    + CxGlobalParams.getInstance().getPairId() + "_family.db", 0, null);
        }
        return sdb;
    }
	
	   public void closeDatabase(Context context) {
//	      mPackageName = context.getPackageName();
	        //RkLog.v(TAG, "mPackageName =" + mPackageName);
//	        String dbFile = createUserDir() + prefix + ".db";
	       
	       SQLiteDatabase db = openOrCreateMyDatabase(context);
	       db.close();
	    }
//	
//	public String getParnterUid() {
//		return mParnterUid;
//	}
//
//	public void setParnterUid(String mParnterUid) {
//		this.mParnterUid = mParnterUid;
//	}
//
//	public String getPairId() {
//		return mPairId;
//	}
//
//	public void setPairId(String mPairId) {
//		this.mPairId = mPairId;
//	}
//	
//	public void initResources(Resources res) {
//		String[] phraseIds = res.getStringArray(R.array.phrase_ids);
//		String[] phraseDescs = res.getStringArray(R.array.phrase_desc);
//		TypedArray phraseResIds = res.obtainTypedArray(R.array.phrase_images);
//		
//		mPhraseMap = new HashMap<String, PhraseData>();
//		for(int i = 0; i < phraseIds.length; i++) {
//			mPhraseMap.put(phraseIds[i], new PhraseData(
//					phraseResIds.getResourceId(i, 0),
//					phraseDescs[i]
//			));
//		}
//		phraseResIds.recycle();
//
//		String[] faceIds = res.getStringArray(R.array.face_ids);
//		TypedArray faceResIds = res.obtainTypedArray(R.array.face_images);
//		mFaceMap = new HashMap<String, Integer>();
//		for(int i = 0; i < faceIds.length; i++) {
//			mFaceMap.put(faceIds[i], Integer.valueOf(faceResIds.getResourceId(i, 0)));
//		}
//		faceResIds.recycle();
//	}
//	public int getFaceResId(String face) {
//		Log.d(TAG, "getFaceResId:" + face);
//		if (face == null) return 0;
//		return mFaceMap.get(face).intValue();
//	}
//	
//	public int getPhraseResId(String phrase) {
//		Log.d(TAG, "getPhraseResId:" + phrase);
//		if (phrase == null) return 0;
//		return mPhraseMap.get(phrase).mRes;
//	}
//	
//	public String getPhraseMsg(String phrase) {
//		Log.d(TAG, "getPhraseMsg:" + phrase);
//		if (phrase == null) return "null";
//		return mPhraseMap.get(phrase).mMsg;		
//	}


    public boolean getCalendarIsLandSpace() {
        return mCalendarIsLandSpace;
    }


    public void setCalendarIsLandSpace(boolean mCalendarIsLandSpace) {
        this.mCalendarIsLandSpace = mCalendarIsLandSpace;
    }

}
