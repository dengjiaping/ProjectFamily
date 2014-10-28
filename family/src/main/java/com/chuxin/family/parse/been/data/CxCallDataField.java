package com.chuxin.family.parse.been.data;

import java.util.List;
/**
 * 用于Pair/call接口对应的data字段
 * @author shichao.wang
 *
 */
public class CxCallDataField {
	private List<CxPairInitData> matchs; //相关邀请码对应的人
	
	private String invite_code; //我的邀请码

	public List<CxPairInitData> getMatchs() {
		return matchs;
	}

	public void setMatchs(List<CxPairInitData> matchs) {
		this.matchs = matchs;
	}

	public String getInvite_code() {
		return invite_code;
	}

	public void setInvite_code(String invite_code) {
		this.invite_code = invite_code;
	}
	
}
