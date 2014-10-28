package com.chuxin.family.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;

import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.image.RecyclingBitmapDrawable;
import com.chuxin.family.utils.CxImageFetcher.Request;
import com.chuxin.family.widgets.CxImageView;

/**
 * 文件缓存到SD卡,存、取、删、清空
 * 
 * @author shichao.wang
 * 
 */
@SuppressLint("NewApi")
public class CxBaseDiskCache implements DiskCache {

	private static final String NOMEDIA = ".nomedia";
	private static final int MIN_FILE_SIZE_IN_BYTES = 100;

	private File mStorageDirectory;

	private HashSet<SoftReference<Bitmap>> mReusableBitmaps;

	private LruCache<String, BitmapDrawable> mMemoryCache;

	private Resources mResource;
	private Context mCtx;

	private final int LIMITED_MEMORY = 8 * 1024 * 1024;
	
	/**
	 * 使用之前确保SD卡可用
	 * 
	 * @param name
	 *            图片不同用处的目录名
	 * @throws Exception
	 */
	public CxBaseDiskCache(String name, Context ctx) {
		mCtx = ctx;
		mResource = ctx.getResources();
		// Lets make sure we can actually cache things!
		File storageDirectory = new File(
				CxGlobalConst.S_CHUXIN_IMAGE_CACHE_PATH, name);
		/*boolean cacheable = */
		createDirectory(storageDirectory);
		/*if (!cacheable) {
			try {
				throw new Exception("the sd card is not useable");
			} catch (Exception e) {
			}
		}*/
		mStorageDirectory = storageDirectory;

		int size = (int) (0.25f * Runtime.getRuntime().maxMemory());
		CxLog.w("create memory size", ""+size);
		if (size > (LIMITED_MEMORY)) {
//			size = LIMITED_MEMORY;
		}
		mReusableBitmaps = new HashSet<SoftReference<Bitmap>>();
		mMemoryCache = new LruCache<String, BitmapDrawable>(size) {

			@Override
			protected void entryRemoved(boolean evicted, String key,
					BitmapDrawable oldValue, BitmapDrawable newValue) {
				if (null != oldValue) {
					if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
                        try {
//							((RecyclingBitmapDrawable) oldValue).getBitmap().recycle();
						} catch (Exception e) {
							e.printStackTrace();
						}
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            // We're running on Honeycomb or later, so add the bitmap
                            // to a SoftRefrence set for possible use with inBitmap later
                            mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                        }else{
                        	try {
								oldValue.getBitmap().recycle();
							} catch (Exception e) {
								e.printStackTrace();
							}
                        }
                    }
					CxLog.i("memory", "bitmap move to reusable memory");
				}
				// super.entryRemoved(evicted, key, oldValue, newValue);
			}

			@Override
			protected int sizeOf(String key, BitmapDrawable value) {
				if (null == value) {
					return 0;
				}

				if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
					return value.getBitmap().getByteCount();
				} else {
					return value.getBitmap().getRowBytes()
							* value.getBitmap().getHeight();
				}

			}

		};

	}

	@Override
	public synchronized boolean exists(String key) {
		// RkLog.i("exists method", "file path--->"+key);
		return getFile(key).exists();
	}

	public synchronized File getFile(String hash) {
		if (null == hash) {
			return null;
		}
		String fileName = Uri.encode(hash);
		// RkLog.i("getFile", fileName);
		return new File(mStorageDirectory, fileName);
	}

	public synchronized InputStream getInputStream(String hash) throws IOException {
		// RkLog.i("getInputStream", "path is "+hash);
		if (null == hash) {
			return null;
		}
		// RkLog.i("getInputStream",
		// "target file exist-->"+getFile(hash).exists());

		InputStream is = new FileInputStream(getFile(hash));

		return is;
	}

	public synchronized BitmapDrawable getExtralBitmapDrawable(String name, View loaderView) {
		// RkLog.i("getInputStream", "path is "+hash);
		if (null == name) {
			return null;
		}
		File sourceImg = getFile(name);
		if (!sourceImg.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sourceImg);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (null == fis) {
			return null;
		}
		BitmapDrawable drawable = null;
		
		try {
			int width = 0, height = 0;
			if ((null != loaderView) && (loaderView.getWidth() > 0)
					&& (loaderView.getHeight() > 0)) {
				width = loaderView.getWidth();
				height = loaderView.getHeight();
			}else{
//				width = RkGlobalParams.getInstance().getWidth();
//				height = RkGlobalParams.getInstance().getHeight();
				width = 200;
				height = 200;
			}
			Log.i("getExtralBitmapDrawable method ", "width="+width+",height="+height);
			Bitmap bitmap = null;
			try {
				bitmap = decodeSampledBitmapFromDescriptor(fis.getFD(), width, height, this);
//				bitmap = BitmapFactory.decodeFileDescriptor(fis.getFD());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (bitmap != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// Running on Honeycomb or newer, so wrap in a standard
					// BitmapDrawable
					drawable = new BitmapDrawable(mResource, bitmap);
				} else {
					// Running on Gingerbread or older, so wrap in a
					// RecyclingBitmapDrawable
					// which will recycle automagically
					drawable = new RecyclingBitmapDrawable(mResource, bitmap);
				}

				if (drawable != null) {
					mMemoryCache.put(name, drawable);
				}
			}

		} catch (Exception e) {
//			e.printStackTrace();
		}
		return drawable;
	}

	/*
	 * public void saveImage(String key, InputStream is){ if ( (null == is) ||
	 * (null == key) ) { return; }
	 * 
	 * 
	 * }
	 */

	public synchronized void store(Request request, InputStream is, boolean isImage) {
		if (null == is) {
			return;
		}
		// RkLog.e("store method ", key);
		if (!Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())) {
			CxLog.i("sd card", "extra storage is no mounted");

			// SD卡不可放就考虑放到cache
			try {
				if (is.available() > 800000) { // 文件过大不考虑了
					CxLog.i("save", "image from net is larger than 800K");
					return;
				}

				/*
				 * File tempFile = new File(mCtx.getCacheDir(), request.hash);
				 * if (!tempFile.exists()) { tempFile.createNewFile(); }
				 */
				String fileName = mCtx.getCacheDir().getParent()
						+ File.separator + Uri.encode(request.hash);
				FileOutputStream fos = new FileOutputStream(fileName);
				byte[] tempSuf = new byte[1024];
				int len = -1;
				while (-1 != (len = is.read(tempSuf))) {
					fos.write(tempSuf, 0, len);
				}
				fos.flush();
				fos.close();
				is.close();
				is = null;

				if (!isImage) { // 非图片不处理
					return;
				}

				// 根据控件来处理图片
				try {
					View view = request.imageview;
					
					int width = 0, height = 0;
					if ((null != view) && (view.getWidth() > 0)
							&& (view.getHeight() > 0)) {
						width = view.getWidth();
						height = view.getHeight();
					}

					Bitmap downloadImage = null;
					if ((0 != width) && (0 != height)) {
						try {
							FileDescriptor fd = new FileInputStream(fileName).getFD();
							downloadImage = decodeSampledBitmapFromDescriptor(fd, width, height, this);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (null == downloadImage) {
						return;
					}
					BitmapDrawable drawable = null;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// Running on Honeycomb or newer, so wrap in a standard
						// BitmapDrawable
						drawable = new BitmapDrawable(mResource, downloadImage);
					} else {
						// Running on Gingerbread or older, so wrap in a
						// RecyclingBitmapDrawable
						// which will recycle automagically
						drawable = new RecyclingBitmapDrawable(mResource,
								downloadImage);
					}

					if (null != drawable) {
						mMemoryCache.put(request.hash, drawable);
						CxLog.i("put", "put in memory");
					} else {
						CxLog.i("save",
								"this is a image, but it's larger than 1M, so save local card but memory");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				CxLog.i("save", "happen error:" + e.getMessage());
			} finally {
				try {
					if (null != is) {
						is.close();
					}
				} catch (IOException e) {
				}
			}

			return;
		}
		// 以下是SD卡正常
		is = new BufferedInputStream(is);
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getFile(request.hash)));

			byte[] b = new byte[1024];
			int count = 0;

			while (-1 != (count = is.read(b))) {
				os.write(b, 0, count);
			}
			os.close();

			 CxLog.i("store image", " successfully");
		} catch (IOException e) {
			CxLog.e("save image locally error",""+e.getMessage());
			return;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				CxLog.e("finally,close inputstream when save image locally error", ""+e.getMessage());
			}
		}

		if (!isImage) { // 非图片就不处理了
			CxLog.i("save",
					"this is not a image, so it will not be proccess and put in memory");
			return;
		}

		// 根据控件来处理图片
		try {
			View view = request.imageview;
			int width = 0, height = 0;
			if ((null != view) && (view.getWidth() > 0)
					&& (view.getHeight() > 0)) {
				width = view.getWidth();
				height = view.getHeight();
			}else{
//				width = RkGlobalParams.getInstance().getWidth();
//				height = RkGlobalParams.getInstance().getHeight();
				width = 200;
				height = 200;
			}

			Bitmap downloadImage = null;
			if ((0 != width) && (0 != height)) {
				try {
					FileDescriptor fd = new FileInputStream(getFile(request.hash)).getFD();
					downloadImage = decodeSampledBitmapFromDescriptor(fd, width, height, this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (null == downloadImage) {
				return;
			}
			BitmapDrawable drawable = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// Running on Honeycomb or newer, so wrap in a standard
				// BitmapDrawable
				drawable = new BitmapDrawable(mResource, downloadImage);
			} else {
				// Running on Gingerbread or older, so wrap in a
				// RecyclingBitmapDrawable
				// which will recycle automagically
				drawable = new RecyclingBitmapDrawable(mResource, downloadImage);
			}

			if (null != drawable) {
				mMemoryCache.put(request.hash, drawable);
				CxLog.i("put", "put in memory");
			} else {
				CxLog.i("save",
						"this is a image, but it's larger than 1M, so save local card but memory");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void invalidate(String key) {
		getFile(key).delete();
	}

	public synchronized void cleanup() {
		// removes files that are too small to be valid. Cheap and cheater way
		// to remove files that
		// were corrupted during download.
		String[] children = mStorageDirectory.list();
		if (children != null) { // children will be null if the directory does
								// not exist.
			for (int i = 0; i < children.length; i++) { // remove too small file
				File child = new File(mStorageDirectory, children[i]);
				if (!child.equals(new File(mStorageDirectory, NOMEDIA))
						&& child.length() <= MIN_FILE_SIZE_IN_BYTES) {
					try {
						child.delete();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * Temporary fix until we rework this disk cache. We delete the first 50
	 * youngest files if we find the cache has more than 1000 images in it.
	 */
	public synchronized void cleanupSimple() {
		final int maxNumFiles = 1000;
		final int numFilesToDelete = 50;

		String[] children = mStorageDirectory.list();
		if (children != null) {
			if (children.length > maxNumFiles) {
				for (int i = children.length - 1, m = i - numFilesToDelete; i > m; i--) {
					File child = new File(mStorageDirectory, children[i]);
					try {
						child.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public synchronized void clear() {
		// clear memory

		try {
			
			
			/*Map<String, BitmapDrawable> cache = mMemoryCache.snapshot();
			if ((null != cache) && (cache.size() > 0)) {
				Set<Entry<String, BitmapDrawable>>  cacheSet = cache.entrySet();
				Iterator<Entry<String, BitmapDrawable>> cacheIterator = cacheSet.iterator();
				while(cacheIterator.hasNext()){
					Entry<String, BitmapDrawable> tempEntry = cacheIterator.next();
					if (null == tempEntry) {
						continue;
					}
					BitmapDrawable tempBitmap = tempEntry.getValue();
					if (null == tempBitmap) {
						continue;
					}
					if (tempBitmap instanceof RecyclingBitmapDrawable) {
						((RecyclingBitmapDrawable)tempBitmap).setIsCached(false);
					}else{
						if(null != tempBitmap.getBitmap()){
							try {
								tempBitmap.getBitmap().recycle();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}*/

			if (null != mMemoryCache) {
				CxLog.i("release memory", "bitmap count is "+mMemoryCache.size());
				mMemoryCache.evictAll();
			}
			
			if (null != mReusableBitmaps) {
				mReusableBitmaps.clear();
			}
			
		} catch (Exception e) {
		}

		// Clear the whole cache. Coolness.
		String[] children = mStorageDirectory.list();
		if (children != null) { // children will be null if hte directyr does
								// not exist.
			for (int i = 0; i < children.length; i++) {
				File child = new File(mStorageDirectory, children[i]);
				if (!child.equals(new File(mStorageDirectory, NOMEDIA))) {
					try {
						child.delete();
					} catch (Exception e) {
					}
				}
			}
		}
		mStorageDirectory.delete();

	}

	public static final boolean createDirectory(File storageDirectory) {
		if (!storageDirectory.exists()) {
			storageDirectory.mkdirs();
		}

		// check writeable
		File nomediaFile = new File(storageDirectory, NOMEDIA);
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				// RkLog.e("", "Unable to create nomedia file.");
			}
		}

		// After we best-effort try to create the file-structure we need,
		// lets make sure it worked.
		if ((storageDirectory.isDirectory() && nomediaFile.exists())) {
			return true;
		}

		// RkLog.e("", "Unable to create storage directory and nomedia file.");
		return false;
	}

	@Override
	public synchronized boolean existAtMemory(String key) {
		/*
		 * if ( (mReusableBitmaps.containsKey(key)) && (null !=
		 * mMemoryCache.get(key)) ){ return true; }
		 */

		if ((null != mMemoryCache) && (null != mMemoryCache.get(key))) {
			return true;
		}
		return false;
	}

	/**
	 * 必须先调用existAtMemory返回true才能调用此方法
	 * 
	 * @param key
	 * @return
	 */
	public synchronized BitmapDrawable getImageFromMemory(String key) {
		// return mReusableBitmaps.get(key).get();
		return mMemoryCache.get(key);
	}

	private static Bitmap decodeSampledBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth, int reqHeight,
			CxBaseDiskCache cache) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		// If we're running on Honeycomb or newer, try to use inBitmap
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			addInBitmapOptions(options, cache);
		}

		try {
			return BitmapFactory
					.decodeFileDescriptor(fileDescriptor, null, options);
		} catch (Exception e) {
			CxLog.i("decode image", ""+e.toString());
			return null;
		}
	}

	private static void addInBitmapOptions(BitmapFactory.Options options,
			CxBaseDiskCache cache) {
		// inBitmap only works with mutable bitmaps so force the decoder to
		// return mutable bitmaps.
		options.inMutable = true;

		if (cache != null) {
			// Try and find a bitmap to use for inBitmap
			Bitmap inBitmap = cache.getBitmapFromReusableSet(options, cache);

			if (inBitmap != null) {
				options.inBitmap = inBitmap;
			}
		}
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image
			// with both dimensions larger than or equal to the requested height
			// and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * @param options
	 *            - BitmapFactory.Options with out* options populated
	 * @return Bitmap that case be used for inBitmap
	 */
	private synchronized Bitmap getBitmapFromReusableSet(BitmapFactory.Options options,
			CxBaseDiskCache cache) {
		Bitmap bitmap = null;
		HashSet<SoftReference<Bitmap>> mReusableBitmaps = cache.mReusableBitmaps;
		// mReusableBitmaps.
		if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
			final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps
					.iterator();
			Bitmap item;

			while (iterator.hasNext()) {
				item = iterator.next().get();

				if (null != item && item.isMutable()) {
					// Check to see it the item can be used for inBitmap
					if (canUseForInBitmap(item, options)) {
						bitmap = item;

						// Remove from reusable set so it can't be used again
						iterator.remove();
						CxLog.i("Remove",
								"Remove from reusable set so it can't be used again");
						break;
					}
				} else {
					// Remove from the set if the reference has been cleared.
					CxLog.i("Remove",
							"Remove from the set if the reference has been cleared");
					iterator.remove();
				}
			}
		}

		return bitmap;
	}

	/**
	 * @param candidate
	 *            - Bitmap to check
	 * @param targetOptions
	 *            - Options that have the out* value populated
	 * @return true if <code>candidate</code> can be used for inBitmap re-use
	 *         with <code>targetOptions</code>
	 */
	private static boolean canUseForInBitmap(Bitmap candidate,
			BitmapFactory.Options targetOptions) {
		int width = targetOptions.outWidth / targetOptions.inSampleSize;
		int height = targetOptions.outHeight / targetOptions.inSampleSize;

		return candidate.getWidth() == width && candidate.getHeight() == height;
	}

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @param cache
	 *            The ImageCache used to find candidate bitmaps for use with
	 *            inBitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight, CxBaseDiskCache cache) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// If we're running on Honeycomb or newer, try to use inBitmap
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			addInBitmapOptions(options, cache);
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @param cache
	 *            The ImageCache used to find candidate bitmaps for use with
	 *            inBitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight, CxBaseDiskCache cache) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// If we're running on Honeycomb or newer, try to use inBitmap
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			addInBitmapOptions(options, cache);
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	@Override
	public synchronized void clearMemory() {
		CxLog.i("clear memory", "clearMemory");
		/*
		Map<String, BitmapDrawable> cache = mMemoryCache.snapshot();
		if ((null != cache) && (cache.size() > 0)) {
			Set<Entry<String, BitmapDrawable>>  cacheSet = cache.entrySet();
			Iterator<Entry<String, BitmapDrawable>> cacheIterator = cacheSet.iterator();
			while(cacheIterator.hasNext()){
				Entry<String, BitmapDrawable> tempEntry = cacheIterator.next();
				if (null == tempEntry) {
					continue;
				}
				BitmapDrawable tempBitmap = tempEntry.getValue();
				if (null == tempBitmap) {
					continue;
				}
				if (tempBitmap instanceof RecyclingBitmapDrawable) {
					((RecyclingBitmapDrawable)tempBitmap).setDither(false);
					RkLog.i("release bitmap ", " RecyclingBitmapDrawable");
				}else{
					if(null != tempBitmap.getBitmap()){
						try {
							tempBitmap.getBitmap().recycle();
							RkLog.i("release bitmap ", " successfully");
						} catch (Exception e) {
							e.printStackTrace();
							RkLog.i("release bitmap exception", " "+e.getMessage());
						}
					}
				}
			}
		}*/
		
		try {
			if (null != mMemoryCache) {
				mMemoryCache.evictAll();
				mMemoryCache = null;
			}
			
			if (null != mReusableBitmaps) {
				mReusableBitmaps.clear();
				mReusableBitmaps = null;
			}
			System.gc();
		} catch (Exception e) {
		}

	}

}
