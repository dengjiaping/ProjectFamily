package com.chuxin.family.parse.been;

import java.util.List;

import com.chuxin.family.parse.been.data.FeedListData;
/**
 * 帖子列表的数据封装
 * @author shichao.wang
 *
 */
public class CxZoneFeedList extends CxParseBasic {
	private String togetherDay;

	private List<FeedListData> data;
	
	public List<FeedListData> getData() {
		return data;
	}

	public void setData(List<FeedListData> data) {
		this.data = data;
	}
	
	public String getTogetherDay() {
		return togetherDay;
	}

	public void setTogetherDay(String togetherDay) {
		this.togetherDay = togetherDay;
	}
	
}
