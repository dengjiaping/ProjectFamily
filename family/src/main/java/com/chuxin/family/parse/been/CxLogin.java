package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.CxUserProfileDataField;

/**
 * 用户登录接口的解析
 * @author shichao.wang
 *
 */
public class CxLogin extends CxParseBasic {

	private CxUserProfileDataField data;

	public CxUserProfileDataField getData() {
		return data;
	}

	public void setData(CxUserProfileDataField data) {
		this.data = data;
	}
	
}
