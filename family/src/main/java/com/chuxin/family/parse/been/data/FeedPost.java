package com.chuxin.family.parse.been.data;

import java.util.List;

public class FeedPost {

	private List<FeedReply> replays; //回复
	
	private List<FeedPhoto> photos; //图片
	
	private String text; //文字信息

	public List<FeedReply> getReplays() {
		return replays;
	}

	public void setReplays(List<FeedReply> replays) {
		this.replays = replays;
	}

	public List<FeedPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(List<FeedPhoto> photos) {
		this.photos = photos;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
