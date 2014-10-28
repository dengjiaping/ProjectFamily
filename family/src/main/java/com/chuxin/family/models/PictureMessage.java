package com.chuxin.family.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

	public class PictureMessage extends Message {

	private static final String TAG_PHOTO = "photo";
	private static final String TAG_PHOTO_THUMBNAIL = "photo_mid";
	private static final String TAG_PHOTO_BIG = "photo_big";
	
	private static final String CATEGORY_ID = "categroyId";
	private static final String IMAGE_ID = "imageId";
	private static final String IMAGENAME_ID = "imageName";
	private static final String IMAGETYPE_ID = "imageType";
	private static final String ISEMOTION = "isEmotion";
	
	private JSONObject mPhoto;
	
	public PictureMessage(JSONObject data, Context context) {
		super(data, context);
		mPhoto = null;
		mContext = context;
	}
	
	@Override
	public int getType() {
		return MESSAGE_TYPE_PICTURE;
	}
	
	public int getIsEmotion(){
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getInt(ISEMOTION);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return -1;
	}
	
	
	
	public int getCategoryId(){
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getInt(CATEGORY_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return -1;
	}
	
	public int getImageId(){
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getInt(IMAGE_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return -1;
	}
	
	public String getImageName(){
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getString(IMAGENAME_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	public String getImageType(){
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getString(IMAGETYPE_ID);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
	
	
	public String getThumbnailUrl() {
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getString(TAG_PHOTO_THUMBNAIL);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}

	public String getUrl() {
		try {
			if (mPhoto == null) {
				mPhoto = mData.getJSONObject(TAG_PHOTO);
			}
			return mPhoto.getString(TAG_PHOTO_BIG);
		} catch (JSONException e) {
			e.printStackTrace();
			assert(false);
		}
		return null;
	}
}
