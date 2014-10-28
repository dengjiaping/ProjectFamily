package com.chuxin.family.views.login;
/**
 * 存储第三方的授权信息
 * @author shichao.wang
 *
 */
public class CxThirdAccessToken {
	private String mToken;
	
	private String mUid;
	
	private String mExpiresTime;
	
	private String mPlatName;
	
	public CxThirdAccessToken(){}
	
	/**
	 * @param token 第三方授权的accesstoken
	 * @param uid 第三方UID
	 * @param expiresTime 第三方授权有效期
	 * @param platName 第三方平台名
	 */
	public CxThirdAccessToken(String token, String uid, 
			String expiresTime, String platName){
		this.mExpiresTime = expiresTime;
		this.mUid = uid;
		this.mToken = token;
		this.mPlatName = platName;
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String token) {
		this.mToken = token;
	}

	public String getUid() {
		return mUid;
	}

	public void setUid(String uid) {
		this.mUid = uid;
	}

	public String getExpiresTime() {
		return mExpiresTime;
	}

	public void setExpiresTime(String expiresTime) {
		this.mExpiresTime = expiresTime;
	}

	public String getPlatName() {
		return mPlatName;
	}

	public void setPlatName(String platName) {
		this.mPlatName = platName;
	}
	
}
