package com.chuxin.family.parse.been;

import java.util.List;

import com.chuxin.family.parse.been.data.CxPairInitData;

public class CxPairInit {
	private String msg;
	
	private int ts;
	
	private int rc;
	
	private List<CxPairInitData> data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}

	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public List<CxPairInitData> getData() {
		return data;
	}

	public void setData(List<CxPairInitData> data) {
		this.data = data;
	}
	
}
