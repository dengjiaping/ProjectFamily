package com.chuxin.family.parse.been;
/**
 * 解析类的基类（根据全局信息结构，都含有这三个字段，data字段会有所不同）
 * @author shichao.wang
 *
 */
public class CxParseBasic {
	private int rc;
	
	private int ts;
	
	private String msg;
	
	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
