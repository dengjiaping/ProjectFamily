package com.chuxin.family.settings;

public class CxFile {
	
	/* 文件名称(全路径）,也对应filename字段 */
	private String mFileName;
	
	/* name字段名称 */
	private String mNameField;
	
	/* 内容类型 */
//	private String contentType = "application/octet-stream";//image/
	private String mContentType = "image/jpg"; //本次项目仅支持jpg图片

	/**
	 * @param filname ,文件全路径
	 * @param nameField ，协议中name字段对应的值
	 * @param contentType
	 */
	public CxFile(String filname, String nameField, String contentType) {
		this.mFileName = filname;
		this.mNameField = nameField;
		if (contentType != null)
			this.mContentType = contentType;
	}

	public String getFilname() {
		return mFileName;
	}

	public void setFilname(String filname) {
		this.mFileName = filname;
	}

	public String getNameField() {
		return mNameField;
	}

	public void setNameField(String nameField) {
		this.mNameField = nameField;
	}

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		this.mContentType = contentType;
	}

}