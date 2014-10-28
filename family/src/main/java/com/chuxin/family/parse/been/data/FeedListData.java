package com.chuxin.family.parse.been.data;

public class FeedListData {

	private int status; //帖子的状态（0表示未被删除）
	
	private String author; //帖子的创建者
	
	private String create; //帖子的创建日期
	
	private String pair_id; //结对ID
	
	private String type; //帖子的种类："post"或者其他方式
	
	private String id; //帖子的ID
	
	private FeedPost post; //帖子内容
	
	private int isNew; //是否新帖 控制变色
	
	private String open_url; //分享到第三方平台的url

	public String getOpenUrl() {
		return open_url;
	}

	public void setOpenUrl(String openUrl) {
		this.open_url = openUrl;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
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

	public FeedPost getPost() {
		return post;
	}

	public void setPost(FeedPost post) {
		this.post = post;
	}


}
