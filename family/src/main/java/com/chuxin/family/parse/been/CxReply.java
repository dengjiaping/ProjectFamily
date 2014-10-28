package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.FeedReply;

/**
 * 回复
 * @author shichao.wang
 *
 */
public class CxReply extends CxParseBasic {
	private FeedReply data;

	public FeedReply getData() {
		return data;
	}

	public void setData(FeedReply data) {
		this.data = data;
	}
	
}
