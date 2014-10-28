package com.chuxin.family.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

/**
 * 本项目的图片截取
 * @author shichao.wang
 *
 */
public class BitmapScaler {
	
	/**
	 * 
	 * @param source, 图片的全名
	 * @param imageView， 加载图片的控件
	 * @param defaultHeight，默认控件的高度
	 * @param defaultWidth，默认控件的宽度
	 * @return
	 */
	public Bitmap scaleFileResource(String source, ImageView imageView, 
			int defaultHeight, int defaultWidth){
		if (null == imageView) {
			return null;
		}
		Bitmap rawImage = null;
		try {
			rawImage = BitmapFactory.decodeFile(source);
		} catch (Exception e) {
		}
		if (null == rawImage) {
			return null;
		}
		int rawHeight, rawWidth;
		rawHeight = rawImage.getHeight();
		rawWidth = rawImage.getWidth();
		
		int targetHeight, targetWidth;
		if (imageView.isShown()) {
			targetHeight = imageView.getHeight();
			targetWidth = imageView.getWidth();
		}else{
			targetHeight = defaultHeight;
			targetWidth = defaultWidth;
		}
		
		//
		int scaleType = judgeAdapteType(targetHeight, targetWidth, rawHeight, rawWidth);
		
		
		//Bitmap.createBitmap(source, x, y, width, height);
		
		
		
		return null;
	}
	
	private void createBitmap(float scale){
		
		
		
		
	}
	
	//小图放大策略：按最大比例扩大直到那边等于控件对应的边的长度，之后另一边也居中截取即可
	//大图缩小策略：按比例最小的缩小到那边的长度，之后另一边居中截取即可
	private static float calculateScale(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		
		float scaleHeight, scaleWidth, scale;
		scaleHeight = reqHeight / ((float)height);
		scaleWidth = reqWidth / ((float)width);
		
		scale = scaleHeight > scaleWidth ? scaleHeight : scaleWidth; 
		Matrix mtr = new Matrix();
		mtr.postScale(scale, scale);
//		Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtr, true);
		
		return scaleHeight > scaleWidth ? scaleHeight : scaleWidth; //不管哪种情况，都是取缩放比例最大的参数
		
		/*if (height >= reqHeight) {
			if(width >= reqWidth){
				//宽高都比控件的宽高大，需要缩小：取缩放比例最大的参数
				return scaleHeight > scaleWidth ? scaleHeight : scaleWidth;
			}else{
				//图片高大于控件的高，图片的宽小于控件的宽，需要扩大图片:取缩放比例最大的参数
				return scaleHeight > scaleWidth ? scaleHeight : scaleWidth;
			}
		}
		
		if(width > reqWidth){
			if (height < reqHeight) { //height >= reqHeight的情况以及滤掉，不需要考虑了
				//图片的宽足够，但图片的高没有控件的高大，需要扩大图片
				return scaleHeight > scaleWidth ? scaleHeight : scaleWidth;
			}
		}

		//以下就是小图，需要扩大图片
		
		return scaleHeight > scaleWidth ? scaleHeight : scaleWidth;*/
	}
	
//	private boolean 
	
	/**
	 * 图片剪裁
	 * @param img
	 * @param orientation, true表示width大于控件的宽,横向截取;否则是
	 * @param targetLen
	 * @return
	 */
	private Bitmap cutBitmap(Bitmap img, boolean orientation, int targetLen){
		if (null == img) {
			return null;
		}
		if (orientation) { //width is longer
			int templen = img.getWidth();
			if (templen < targetLen) {
				return img;
			}
			int sub = (templen - targetLen) / 2;
			int height = img.getHeight();
			Bitmap rs = null;
			try {
				rs = Bitmap.createBitmap(img, sub, 0, targetLen, height);
				return rs;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return img;
		}else{ //height is longer
			int templen = img.getHeight();
			if (templen < targetLen) { //shorter 
				return img;
			}
			int sub = (templen - targetLen) / 2;
			int width = img.getWidth();
			Bitmap rs = null;
			try {
				rs = Bitmap.createBitmap(img, 0, sub, width, targetLen);
				return rs;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return img;
			
		}
		
	}
	
	
	private int judgeAdapteType(int targetHeight, int targetWidth,
			int rawHeight, int rawWidth){
		if ((rawHeight == targetHeight) && (rawWidth == targetWidth)) {
			return BITMAP_EQUAL_TARGET;
		}
		
		if ((rawHeight > targetHeight) && (rawWidth > targetWidth)) {
			return BITMAP_BEYONG_TARGET;
		}
		
		int gapHeight = targetHeight - rawHeight, gapWidth = targetWidth - rawWidth;
		if (gapHeight > gapWidth) {
			return BITMAP_SMALL_HEIGHT;
		}else{
			return BITMAP_SMALL_WIDTH;
		}
	}
	
	private final int BITMAP_EQUAL_TARGET = 0; //图片宽高都等于控件宽高
	private final int BITMAP_BEYONG_TARGET = 1; //图片宽高都比控件宽高大
//	private final int BITMAP_SMALL_TARGET = 2; //图片宽高都比控件宽高小
	/* 之所以用差值而不是最大比例，就是因为：要满足图片的宽高都大于或等于控件的宽高，
	 * 然后上下居住，横向从0开始截取控件大小 */
	private final int BITMAP_SMALL_WIDTH = 3; //(控件的宽-图片宽 ) > (控件的高-图片高)
	private final int BITMAP_SMALL_HEIGHT = 4; //(控件的高-图片高 ) > (控件的宽-图片宽)
	
}
