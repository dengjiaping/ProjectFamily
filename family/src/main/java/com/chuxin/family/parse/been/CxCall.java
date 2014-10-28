package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.CxCallDataField;
/**
 * 对应Pair/call接口数据
 * @author shichao.wang
 *
 */
public class CxCall extends CxParseBasic{

	private CxCallDataField data;

	public CxCallDataField getData() {
		return data;
	}

	public void setData(CxCallDataField data) {
		this.data = data;
	}
	
}
