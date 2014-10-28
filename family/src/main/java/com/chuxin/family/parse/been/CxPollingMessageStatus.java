package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.CxPollingDataField;

/**
 * 这是LONG-POLLING形式检查消息状态的所有内容
 * @author shichao.wang
 *
 */
public class CxPollingMessageStatus {
	private int rc;
	
	private int ts;
	
	private String msg;
	
	private CxPollingDataField data;

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

	public CxPollingDataField getData() {
		return data;
	}

	public void setData(CxPollingDataField data) {
		this.data = data;
	}

}

