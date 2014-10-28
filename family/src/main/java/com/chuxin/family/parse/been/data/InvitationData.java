package com.chuxin.family.parse.been.data;

public class InvitationData {

	private int status; //帖子的状态（0表示未被删除）
	
	private int flag;
	
	private String author; //帖子的创建者
	
	private String name;
	
	private String create; //帖子的创建日期
	
	private String update_time; //帖子更新日期  //如果是私聊贴在右上角显示更新时间
	
	private String pair_id; //结对ID
	
	private String type; //帖子的种类："post"或者其他方式
	
	private String id; //帖子的ID
	
	private int isNew;
	
	private String message_group_id;
	
	private InvitationPost post; //帖子内容
	
	private InvitationUserInfo userInfo;
	
	private String open_url; //分享公开的页面
	
	
	
	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getOpen_url() {
		return open_url;
	}

	public void setOpen_url(String open_url) {
		this.open_url = open_url;
	}

	public String getMessage_group_id() {
		return message_group_id;
	}

	public void setMessage_group_id(String message_group_id) {
		this.message_group_id = message_group_id;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InvitationUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(InvitationUserInfo userInfo) {
		this.userInfo = userInfo;
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

	public String getCreate() {
		return create;
	}

	public void setCreate(String create) {
		this.create = create;
	}

	public String getPair_id() {
		return pair_id;
	}

	public void setPair_id(String pair_id) {
		this.pair_id = pair_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InvitationPost getPost() {
		return post;
	}

	public void setPost(InvitationPost post) {
		this.post = post;
	}

	
}
