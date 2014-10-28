package com.chuxin.family.parse.been.data;

/**
 * 我家小报的分类配置对象
 * @author dujy
 *
 */
public class TabloidCateConfObj {
	
	private int category_id;							// 分类ID
	private String notification_week;			// 每周的提醒时间  格式 : [1, 2, 3, 4, 5, 7]
	private String img;									// 图标地址
	private String title;									// 分类名称
	private String notification_status;			// 提醒状态
	
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public String getNotification_week() {
		return notification_week;
	}
	public void setNotification_week(String notification_week) {
		this.notification_week = notification_week;
	}
	public String getImg() {
		return img ;			
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNotification_status() {
		return notification_status;
	}
	public void setNotification_status(String notification_status) {
		this.notification_status = notification_status;
	}
	
	
	/**
	 * 得到格式化的提醒时间(在页面显示时使用)
	 * 说明：
	 *         原格式： 【1，3】
	 *         格式化后：每周一/三
	 * @return
	 */
	public String getFormatNotificationWeek(){
		String str 	= notification_week.replaceAll("\\[|\\]", "");		// 去掉左右中括号
		String[] arr 	= str.split(",");
		if(arr.length==7){
			return "每天";
		}
		
		StringBuilder result = new StringBuilder();
		for(String k : arr){
			if( result.length()>0){
				result = result.append("/");
			}
			if(k.equals("1")){
					result.append("一");
			}else if(k.equals("2")){
					result.append("二");
			}else if(k.equals("3")){
					result.append( "三");
			}else if(k.equals("4")){
					result.append("四");
			}else if(k.equals("5")){
					result.append("五");
			}else if(k.equals("6")){
					result.append("六");
			}else if(k.equals("7")){
					result.append("日");
			}
		}
		
		return "每周" + result.toString() ;		
	}
}
