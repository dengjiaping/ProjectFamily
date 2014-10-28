package com.chuxin.family.parse.been.data;

import java.util.List;

/**
 * 我家小报的分类配置
 * @author dujy
 * 说明:
 *       为了保持和后端一致，在本方法中采用了"_"命名的方式
 *
 */
public class TabloidCateConfData {
	
	private String version;												// 版本号, 根据版本号来判断分类的配置是否发生了变化
	private int max_amount;											// 给的最大数据条数
	private String fetch_resource_time;							// 可以拉取的时间		示例:  fetch_resource_time: ["9:00","23:30"]
	private String notification_time;								// 通知时间					示例:  notification_time: [8, 30]
	private List<TabloidCateConfObj> config;				// 分类配置列表
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getMax_amount() {
		return max_amount;
	}
	public void setMax_amount(int max_amount) {
		this.max_amount = max_amount;
	}
	public String getFetch_resource_time() {
		return fetch_resource_time;
	}
	public void setFetch_resource_time(String fetch_resource_time) {
		this.fetch_resource_time = fetch_resource_time;
	}
	public String getNotification_time() {
		return notification_time;
	}
	public void setNotification_time(String notification_time) {
		this.notification_time = notification_time;
	}
	public List<TabloidCateConfObj> getConfig() {
		return config;
	}
	public void setConfig(List<TabloidCateConfObj> config) {
		this.config = config;
	}

	
	
}
