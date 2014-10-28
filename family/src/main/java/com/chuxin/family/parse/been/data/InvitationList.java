package com.chuxin.family.parse.been.data;

import java.util.ArrayList;

public class InvitationList {

	private ArrayList<InvitationData>  datas;
	
	private String bgUrl;//已不用
	
	private InvitationUserInfo userInfo;
	
	

	public InvitationUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(InvitationUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public ArrayList<InvitationData> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<InvitationData> datas) {
		this.datas = datas;
	}

	public String getBgUrl() {
		return bgUrl;
	}

	public void setBgUrl(String bgUrl) {
		this.bgUrl = bgUrl;
	}
	
	
	
	
}
