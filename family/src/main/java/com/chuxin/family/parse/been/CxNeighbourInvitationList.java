package com.chuxin.family.parse.been;

import java.util.List;

import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.InvitationList;
/**
 * 帖子列表的数据封装
 * @author shichao.wang
 *
 */
public class CxNeighbourInvitationList extends CxParseBasic {
	
	private InvitationList data;
	
	public InvitationList getData() {
		return data;
	}

	public void setData(InvitationList data) {
		this.data = data;
	}
	
}
