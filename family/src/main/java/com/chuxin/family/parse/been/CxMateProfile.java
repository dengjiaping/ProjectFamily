package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.CxMateProfileDataField;

/**
 * 用户结对的对方的资料获取类
 * @author shichao.wang
 *
 */
public class CxMateProfile extends CxParseBasic{
	private CxMateProfileDataField data;

	public CxMateProfileDataField getData() {
		return data;
	}

	public void setData(CxMateProfileDataField data) {
		this.data = data;
	}
	
}
