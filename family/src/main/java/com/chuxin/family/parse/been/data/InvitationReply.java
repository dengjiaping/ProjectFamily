package com.chuxin.family.parse.been.data;

public class InvitationReply {

	private int status;
	
	private String author;
	
	private String text;
	
	private String extra;
	
	private int ts;
	
	private String feed_id;
	
	private String reply_id;
	
	private String reply_to;
	
	private String type;
	
	private String name;
	
	private String reply_name;
	
	private String audio;
	
	private int  audio_len;
	
	private int isNew;
	
	
	

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReply_name() {
		return reply_name;
	}

	public void setReply_name(String reply_name) {
		this.reply_name = reply_name;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public int getAudio_len() {
		return audio_len;
	}

	public void setAudio_len(int audio_len) {
		this.audio_len = audio_len;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}

	public String getFeed_id() {
		return feed_id;
	}

	public void setFeed_id(String feed_id) {
		this.feed_id = feed_id;
	}

	public String getReply_id() {
		return reply_id;
	}

	public void setReply_id(String reply_id) {
		this.reply_id = reply_id;
	}

	public String getReply_to() {
		return reply_to;
	}

	public void setReply_to(String reply_to) {
		this.reply_to = reply_to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
