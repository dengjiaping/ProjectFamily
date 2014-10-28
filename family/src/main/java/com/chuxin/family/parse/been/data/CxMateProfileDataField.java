package com.chuxin.family.parse.been.data;

import java.util.LinkedHashMap;

/**
 * 获取对方资料接口的data字段
 * @author shichao.wang
 *
 */
public class CxMateProfileDataField {
	private String name;
	
	private String mobile;
	
	private String partner_id; //对方UID
	
	private String email;
	
	private String note; //备注
	
	private int birth;
	
	private LinkedHashMap<String, String> data; //其他资料
	
	private String id; //当前用户的uid
	
	private String icon; //对方头像

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getBirth() {
		return birth;
	}

	public void setBirth(int birth) {
		this.birth = birth;
	}

	public LinkedHashMap<String, String> getData() {
		if(data!=null)
				return data;
		else{
				return new LinkedHashMap<String,String>();
		}
	}

	public void setData(LinkedHashMap<String, String> data) {
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}



}
