package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.CxPairApproveData;

/**
 * 同意结对网络应答
 * @author shichao.wang
 *
 */
public class CxPairApprove {

	private String msg;
	
	private int rc;
	
	private int ts;
	
	private CxPairApproveData data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

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

	public CxPairApproveData getData() {
		return data;
	}

	public void setData(CxPairApproveData data) {
		this.data = data;
	}
	
}
