package com.chuxin.family.parse.been.data;
/**
 * 修改头像的data字段（目前只关注头像的3个字段）
 * @author shichao.wang
 *
 */
public class CxChangeHeadDataField {
	private String icon_mid; //中等头像
	
	private String icon_small;//小头像
	
	private String icon_big;//大头像

	public String getIcon_mid() {
		return icon_mid;
	}

	public void setIcon_mid(String icon_mid) {
		this.icon_mid = icon_mid;
	}

	public String getIcon_small() {
		return icon_small;
	}

	public void setIcon_small(String icon_small) {
		this.icon_small = icon_small;
	}

	public String getIcon_big() {
		return icon_big;
	}

	public void setIcon_big(String icon_big) {
		this.icon_big = icon_big;
	}
	
}
