package com.chuxin.family.parse.been.data;
/**
 * pair结对时init接口中data字段中的类
 * @author shichao.wang
 *
 */
public class CxPairInitData {

	private String identifie; //邀请人的号码
	
	private String uid; //邀请人的UID
	
	private String name; //邀请人的名称
	
	private String icon; //头像

	public String getIdentifie() {
		return identifie;
	}

	public void setIdentifie(String identifie) {
		this.identifie = identifie;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
