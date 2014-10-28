package com.chuxin.family.parse.been.data;

import java.util.ArrayList;
public class InvitationPost {

	private ArrayList<InvitationReply> replays; //回复
	
	private ArrayList<InvitationPhoto> photos; //图片
	
	private String text; //文字信息

	public ArrayList<InvitationReply> getReplays() {
		return replays;
	}

	public void setReplays(ArrayList<InvitationReply> replays) {
		this.replays = replays;
	}

	public ArrayList<InvitationPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<InvitationPhoto> photos) {
		this.photos = photos;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
