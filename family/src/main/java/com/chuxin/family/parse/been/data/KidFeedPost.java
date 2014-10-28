package com.chuxin.family.parse.been.data;

import java.util.ArrayList;

public class KidFeedPost {

	private ArrayList<KidFeedReply> replays; //回复
	
	private ArrayList<KidFeedPhoto> photos; //图片
	
	private String text; //文字信息

	


	public ArrayList<KidFeedReply> getReplays() {
		return replays;
	}

	public void setReplays(ArrayList<KidFeedReply> replays) {
		this.replays = replays;
	}

	public ArrayList<KidFeedPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<KidFeedPhoto> photos) {
		this.photos = photos;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
