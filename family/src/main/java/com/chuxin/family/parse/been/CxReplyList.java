package com.chuxin.family.parse.been;

import java.util.List;

import com.chuxin.family.parse.been.data.FeedReply;
/**
 * 回复列表
 * @author shichao.wang
 *
 */
public class CxReplyList extends CxParseBasic {

	private List<FeedReply> replies;

	public List<FeedReply> getReplies() {
		return replies;
	}

	public void setReplies(List<FeedReply> replies) {
		this.replies = replies;
	}
	
}
