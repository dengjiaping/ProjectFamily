package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.CxUserProfileDataField;

/**
 * /User/get接口解析类
 * @author shichao.wang
 *
 */
public class CxUserProfile extends CxParseBasic {

	private CxUserProfileDataField data;

	public CxUserProfileDataField getData() {
		return data;
	}

	public void setData(CxUserProfileDataField data) {
		this.data = data;
	}
	
}
