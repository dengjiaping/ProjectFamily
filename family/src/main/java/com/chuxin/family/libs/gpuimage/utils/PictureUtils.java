package com.chuxin.family.libs.gpuimage.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Surface;

import com.chuxin.family.utils.CxLog;
/**
 * 
 * @author shichao.wang
 *
 */
public class PictureUtils {

	private Context mContext;
	
	public PictureUtils(Context context){
		mContext = context;
	}
	/**
	 * 缩放图片
	 * @param imagePath
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getImage(String imagePath, int width, int height) {
		int degrees = 0;
		try {
		 degrees =	getImageOrientation(imagePath);
		 CxLog.d("getImage", "degrees>>>" + degrees);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		//newbitmap = bitmap.copy(bitmap.getConfig(), false);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		CxLog.v("getImageThumbnail", "be>>>" + be);
		options.inSampleSize = be;
		Bitmap newbitmap = null;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		try {
			newbitmap = bitmapRotate(BitmapFactory.decodeFile(imagePath, options), degrees);
//			newbitmap = BitmapFactory.decodeFile(imagePath, options);
			return newbitmap;
//			bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(Uri.parse(imagePath)), null, options);
		} catch (OutOfMemoryError e) {
			newbitmap.recycle();
			e.printStackTrace();
		} catch(Exception e){
			newbitmap.recycle();
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 图片的旋转处理
	 */
	private Bitmap bitmapRotate(Bitmap bitmap, int degrees){
		CxLog.d("bimapRotate", "degrees>>>" + degrees);
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//		bitmap.recycle();
		return rotateBitmap;
	}
	
    public Bitmap rotateImage(final Bitmap bitmap, final File fileWithExifInfo) {
        if (bitmap == null) {
            return null;
        }
        Bitmap rotatedBitmap = bitmap;
        try {
            int orientation = getImageOrientation(fileWithExifInfo.getAbsolutePath());
            Log.d("pictureutils", "rotateImage orientation>>>>" + orientation);
            if (orientation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }
    
    public int getImageOrientation(final String file) throws IOException {
        ExifInterface exif = new ExifInterface(file);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return 0;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }
	/**
	 * 获取图片缩略图
	 * @param imagePath
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		//newbitmap = bitmap.copy(bitmap.getConfig(), false);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		CxLog.v("getImageThumbnail", "be>>>" + be);
		options.inSampleSize = 2;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		try {
			bitmap = BitmapFactory.decodeFile(imagePath, options);
//			bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(Uri.parse(imagePath)), null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//		bitmap.recycle();
		return bitmap;
	}
	
	/**
	 * 获取图片默认大小缩略图
	 * @param imagePath
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap createImageThumbnail(String imagePath) {
		if (null == imagePath) {
			return null;
		}
		if (! new File(imagePath).exists()) {
			return null;
		}
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / 74;
		int beHeight = h / 74;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		try {
			bitmap = BitmapFactory.decodeFile(imagePath, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 74, 74,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	
    /**
     * 图片uri 转换为 Bitmap
     * 
     * @param picture uri
     * @return bitmap
     */
    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    /**
     * 图片path 转换为 Bitmap
     * 
     * @param picture uri
     * @return bitmap
     */
    public Bitmap decodeUriAsBitmap(String path) {
        Bitmap bitmap = null;
        Uri uri = Uri.parse(path.replace("file://", ""));
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    
	public Bitmap getImageThumbnail(String imagePath) {
		if (null == imagePath) {
			return null;
		}
		if (! new File(imagePath).exists()) {
			return null;
		}
		Bitmap newbitmap = null;
		try {
			newbitmap = BitmapFactory.decodeFile(imagePath);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		newbitmap = ThumbnailUtils.extractThumbnail(newbitmap, 74, 74,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return newbitmap;
	}
	
	public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
	
	   // And to convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Activity act, Uri contentUri) {

            // can post image
            String [] proj={MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = act.managedQuery( contentUri,
                            proj, // Which columns to return
                            null,       // WHERE clause; which rows to return (all rows)
                            null,       // WHERE clause selection arguments (none)
                            null); // Order-by clause (ascending by name)
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
    }
    public Bitmap getBitmapFromUri(String imagepath)
    {
        if (null == imagepath) {
            return null;
        }
        if (! new File(imagepath).exists()) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(imagepath);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap.recycle();
        }
        return bitmap;
    }
    @SuppressWarnings("deprecation")
    public Uri changeContentUri(Uri uri) {
        Log.d("PictureUtils", "changeContentUri uri is " + uri);
        if (null == uri) {
			return null;
		}
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            Log.d("PictureUtils", "changeContentUri path1 is " + path);
            if (path != null) {
                path = Uri.decode(path);
                Log.d("PictureUtils", "changeContentUri path2 is " + path);
                ContentResolver cr = mContext.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    Log.d("PictureUtils", "changeContentUri uri_temp is " + uri_temp);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
                cur.close(); // close
            }
        }
        return uri;
    }

    
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
     

}
