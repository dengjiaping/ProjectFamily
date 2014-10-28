package com.chuxin.family.parse.been;
/**
 * 版本检测更新
 * @author shichao.wang
 *
 */
public class CxCheckVersion extends CxParseBasic {
	private int flag; // 0 - 已经是最新; 1 - 必须更新到最新版; 2 - 能更新的话最好，不更新也可以
	private String version; //客户端的最高版本
	private String url; //浏览该地址获取合适的下载版本
	private String msg;  // 更新文案
	
	public int getFlag() {
		return flag;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
