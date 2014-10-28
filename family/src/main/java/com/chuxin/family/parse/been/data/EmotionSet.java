package com.chuxin.family.parse.been.data;

import java.util.ArrayList;

public class EmotionSet {

	private String  lockCateImage;
	private String  norCateImage;

	private boolean  show;
	
	
	private String  resourceUrl;
	private String  guideImage;
	private String  guideString;
	
	private int  imageWidth;
	private int  imageHeight;
	private int  emoCellSize_width;
	private int  emoCellSize_height;
	
	private int  isSupportMix;
	private int  countPerPage;
	
	private int  categoryId;
	private int  version;
	
	private ArrayList<EmotionItem> items;

	public String getLockCateImage() {
		return lockCateImage;
	}

	public void setLockCateImage(String lockCateImage) {
		this.lockCateImage = lockCateImage;
	}

	public String getNorCateImage() {
		return norCateImage;
	}

	public void setNorCateImage(String norCateImage) {
		this.norCateImage = norCateImage;
	}


	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getGuideImage() {
		return guideImage;
	}

	public void setGuideImage(String guideImage) {
		this.guideImage = guideImage;
	}

	public String getGuideString() {
		return guideString;
	}

	public void setGuideString(String guideString) {
		this.guideString = guideString;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getEmoCellSize_width() {
		return emoCellSize_width;
	}

	public void setEmoCellSize_width(int emoCellSize_width) {
		this.emoCellSize_width = emoCellSize_width;
	}

	public int getEmoCellSize_height() {
		return emoCellSize_height;
	}

	public void setEmoCellSize_height(int emoCellSize_height) {
		this.emoCellSize_height = emoCellSize_height;
	}

	public int getIsSupportMix() {
		return isSupportMix;
	}

	public void setIsSupportMix(int isSupportMix) {
		this.isSupportMix = isSupportMix;
	}

	public int getCountPerPage() {
		return countPerPage;
	}

	public void setCountPerPage(int countPerPage) {
		this.countPerPage = countPerPage;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ArrayList<EmotionItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<EmotionItem> items) {
		this.items = items;
	}
	
	
	
	
	
	
}
