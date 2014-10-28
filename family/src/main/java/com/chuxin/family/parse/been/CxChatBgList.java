package com.chuxin.family.parse.been;


import com.chuxin.family.parse.been.data.ChatBgData;

public class CxChatBgList extends CxParseBasic {

	
	private int flag;
	private String message;
	private String url;
	private String version;
	
	private ChatBgData data;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ChatBgData getData() {
		return data;
	}

	public void setData(ChatBgData data) {
		this.data = data;
	}
	
	
	
	
	
	
}
