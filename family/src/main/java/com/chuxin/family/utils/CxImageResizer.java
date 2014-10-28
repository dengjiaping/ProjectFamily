package com.chuxin.family.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;

/**
 * 处理图片的缩放
 * @author shichao.wang
 *
 */
public class CxImageResizer {

	public synchronized Bitmap creatBitmap(final String fileName, int reqWidth, int reqHeight){
		if (null == fileName) {
			return null;
		}
		if ((0 == reqWidth) || (0 == reqHeight)) {
			try {
				File sourceFile = new File(fileName);
				if (!sourceFile.exists()) {
					return null;
				}
				return BitmapFactory.decodeFile(fileName);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		File sourceFile = new File(fileName);
		if (!sourceFile.exists()) {
			return null;
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		FileDescriptor descriptor = null;
		try {
			descriptor = fis.getFD();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null == descriptor) {
			return null;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(descriptor, null, options);
		
		Bitmap img = BitmapFactory.decodeFile(fileName);
		if (null == img) {
			return null;
		}
		
		final int height = options.outHeight;
		final int width = options.outWidth;
		float scaleHeight, scaleWidth, scale;
		scaleHeight = reqHeight / ((float)height);
		scaleWidth = reqWidth / ((float)width);
		scale = (scaleHeight > scaleWidth ? scaleHeight : scaleWidth);
		
		boolean orientation = false;
		int targetLen = 0;
		if (scale == scaleHeight) {
			orientation = true;
			targetLen = reqWidth;
		}else{
			orientation = false;
			targetLen = reqHeight;
		}
		
		Matrix mtr = new Matrix();
		mtr.postScale(scale, scale);
		Bitmap bmp = Bitmap.createBitmap(img, 0, 0, options.outWidth, 
				options.outHeight, mtr, true);
		
		if (null == bmp) {
			return null;
		}
		
		String tempFilePath = fileName+".temp";
		File tempFile = new File(tempFilePath);
		if (!tempFile.exists()) {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			bmp.compress(CompressFormat.JPEG, 80, fos);
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		if (null != fos) {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bmp.recycle();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		Bitmap tempImg = BitmapFactory.decodeFile(tempFilePath);
		if (null == tempImg) {
			return null;
		}
		Bitmap rempTargetImg = cutBitmap(tempImg, orientation, targetLen);
		if (null != tempImg) {
			tempImg.recycle();
		}
		
		String targetPath = fileName+".target";
		File finalFile = new File(targetPath);
		if (!finalFile.exists()) {
			try {
				finalFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream finalFos = null;
		try {
			finalFos = new FileOutputStream(finalFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		rempTargetImg.compress(CompressFormat.JPEG, 80, finalFos);
		if (null != finalFos) {
			try {
				finalFos.flush();
				finalFos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			rempTargetImg.recycle();
			if (null != tempImg) {
				tempImg.recycle();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			tempFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			sourceFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			finalFile.renameTo(new File(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			return BitmapFactory.decodeFile(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 图片剪裁
	 * @param img
	 * @param orientation, true表示width大于控件的宽,横向截取;否则是
	 * @param targetLen
	 * @return
	 */
	private Bitmap cutBitmap(Bitmap img, boolean isWidth, int targetLen){
		if (null == img) {
			return null;
		}
		if (isWidth) { //width is longer
			int templen = img.getWidth();
			if (templen < targetLen) {
				return img;
			}
			int sub = (templen - targetLen) / 2;
			int height = img.getHeight();
			Bitmap rs = null;
			try {
				rs = Bitmap.createBitmap(img, sub, 0, targetLen, height);
//				img.recycle();
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
//				img.recycle();
				return rs;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return img;
		}
	}
	
}
