package com.chuxin.family.mate;

import java.util.LinkedHashMap;

import android.text.TextUtils;

import com.chuxin.family.model.CxSubjectInterface;
/**
 * 伴侣资料
 * @author shichao.wang
 *
 */
public class CxMateParams extends CxSubjectInterface {

	private static CxMateParams mMateParams;
	
	public final static String FAMILY_FLAG= "family_flag";
	
	
	public final static String MATE_NAME= "mateName";
	private String mMateName;
	
	public final static String MATE_MOBILE = "mateMobile";
	private String mMateMobile;
	
	public final static String MATE_UID= "mateUid";
	private String mMateUid; //对方UID
	
	public final static String MATE_EMAIL= "mateEmail";
	private String mMateEmail;
	
	public final static String MATE_NOTE= "mateNote";
	private String mMateNote; //备注
	
	public final static String MATE_BIRTH= "mateBirth";
	private int mMateBirth;
	
	public final static String MATE_DATA= "mateData";
	private LinkedHashMap<String, String> mMateData; //其他资料
	
	public final static String MATE_ICON= "mateIcon";
	private String mMateIcon; //对方头像
	
	private CxMateParams(){}
	
	public static CxMateParams getInstance(){
		if (null == mMateParams) {
			mMateParams = new CxMateParams();
		}
		return mMateParams;
	}

	public String getMateName() {
		return mMateName;
	}

	public void setmMateName(String mateName) {
		if (TextUtils.equals(this.mMateName, mateName)) {
			return;
		}
		this.mMateName = mateName;
		notifyObserver(MATE_NAME);
	}

	public String getMateMobile() {
		return mMateMobile;
	}

	public void setmMateMobile(String mateMobile) {
		if (TextUtils.equals(mateMobile, mMateMobile)) {
			return;
		}
		this.mMateMobile = mateMobile;
		notifyObserver(MATE_MOBILE);
	}

	public String getMateUid() {
		return mMateUid;
	}

	public void setmMateUid(String mateUid) {
		if (TextUtils.equals(mMateUid, mateUid)){
			return;
		}
		this.mMateUid = mateUid;
		notifyObserver(MATE_UID);
	}

	public String getMateEmail() {
		return mMateEmail;
	}

	public void setmMateEmail(String mateEmail) {
		if (TextUtils.equals(mateEmail, mMateEmail)) {
			return;
		}
		this.mMateEmail = mateEmail;
		notifyObserver(MATE_EMAIL);
	}

	public String getMateNote() {
		return mMateNote;
	}

	public void setmMateNote(String mateNote) {
		if (TextUtils.equals(mateNote, mMateNote)) {
			return;
		}
		this.mMateNote = mateNote;
		notifyObserver(MATE_NOTE);
	}

	public int getMateBirth() {
		return mMateBirth;
	}

	public void setmMateBirth(int mateBirth) {
		if (mateBirth == mMateBirth) {
			return;
		}
		this.mMateBirth = mateBirth;
		notifyObserver(MATE_BIRTH);
	}

	public LinkedHashMap<String, String> getMateData() {
		return mMateData;
	}

	public void setMateData(LinkedHashMap<String, String> mateData) {
		if (mateData.equals(mMateData)) {
			return;
		}
		this.mMateData = mateData;
		notifyObserver(MATE_DATA);
	}

	public String getMateIcon() {
		return mMateIcon;
	}

	public void setMateIcon(String mateIcon) {
		if (TextUtils.equals(mateIcon, mMateIcon)) {
			return;
		}
		this.mMateIcon = mateIcon;
		notifyObserver(MATE_ICON);
	}
}
