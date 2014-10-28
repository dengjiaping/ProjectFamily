package com.chuxin.family.parse.been.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KidFeedChildrenData implements Serializable{

    private String id; // 孩子id
    private String avata; // 孩子头像 
    private String uid; // 用户uid
    private String name; // 孩子全名
    private String nickname; // 孩子昵称
    private int gender = -1; // 孩子性别(0: male  1: female  -1: unknown)
    private String birth; // 孩子生日
    private String note; // 备注
    private String data; // 其他数据
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAvata() {
		return avata;
	}
	public void setAvata(String avata) {
		this.avata = avata;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
	
	
	
	
}
