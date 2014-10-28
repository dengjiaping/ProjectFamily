package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.FamilyInfoUserInfo;

public class CxFamilyInfoData extends CxParseBasic {

	private String family_icon;
	private FamilyInfoUserInfo oppoInfo;
	private FamilyInfoUserInfo meInfo;
	
	public String getFamily_icon() {
		return family_icon;
	}
	public void setFamily_icon(String family_icon) {
		this.family_icon = family_icon;
	}
	public FamilyInfoUserInfo getOppoInfo() {
		return oppoInfo;
	}
	public void setOppoInfo(FamilyInfoUserInfo oppoInfo) {
		this.oppoInfo = oppoInfo;
	}
	public FamilyInfoUserInfo getMeInfo() {
		return meInfo;
	}
	public void setMeInfo(FamilyInfoUserInfo meInfo) {
		this.meInfo = meInfo;
	}
	
	
	
	
	
}
