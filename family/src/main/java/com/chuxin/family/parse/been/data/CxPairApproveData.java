package com.chuxin.family.parse.been.data;
/**
 * 同意结对网络应答的data字段
 * @author shichao.wang
 *
 */
public class CxPairApproveData {

	private String pair_id; //两人结对的ID
	
	private String partner_id; //结对的对方ID
	
	private int mode;
	

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getPair_id() {
		return pair_id;
	}

	public void setPair_id(String pair_id) {
		this.pair_id = pair_id;
	}

	public String getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}
	
}
