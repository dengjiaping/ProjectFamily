package com.chuxin.family.parse.been;
/**
 * 修改二人空间的背景
 * @author shichao.wang
 *
 */
public class CxChangeBgOfZone extends CxParseBasic{
	private String bg_big; //聊天背景
	
	//这里暂时不对其他字段作解析，因为只关注背景，而且没有背景小图，只有背景大图
	
	public String getBg_big() {
		return bg_big;
	}

	public void setBg_big(String bg_big) {
		this.bg_big = bg_big;
	}
	
}
