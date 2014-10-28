package com.chuxin.family.image;

import com.chuxin.family.utils.CxLog;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * A BitmapDrawable that keeps track of whether it is being displayed or cached.
 * When the drawable is no longer being displayed or cached,
 * {@link Bitmap#recycle() recycle()} will be called on this drawable's bitmap.
 */
public class RecyclingBitmapDrawable extends BitmapDrawable {

    static final String LOG_TAG = "CountingBitmapDrawable";

    private int mCacheRefCount = 0;
    private int mDisplayRefCount = 0;

    private boolean mHasBeenDisplayed;

    public RecyclingBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    /**
     * Notify the drawable that the displayed state has changed. Internally a
     * count is kept so that the drawable knows when it is no longer being
     * displayed.
     *
     * @param isDisplayed - Whether the drawable is being displayed or not
     */
    public void setIsDisplayed(boolean isDisplayed) {
        // Check to see if recycle() can be called
    	synchronized (this) {
            if (isDisplayed) {
                mDisplayRefCount++;
                mHasBeenDisplayed = true;
            } else {
                mDisplayRefCount--;
            }
        }
        checkState();
    }

    /**
     * Notify the drawable that the cache state has changed. Internally a count
     * is kept so that the drawable knows when it is no longer being cached.
     *
     * @param isCached - Whether the drawable is being cached or not
     */
    public void setIsCached(boolean isCached) {
        // Check to see if recycle() can be called
    	synchronized (this) {
            if (isCached) {
                mCacheRefCount++;
            } else {
                mCacheRefCount--;
            }
        }
        checkState();
    }

    private synchronized void checkState() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle
    	CxLog.i(LOG_TAG, "mCacheRefCount="+mCacheRefCount+",mDisplayRefCount="+mDisplayRefCount
    			+",mHasBeenDisplayed="+mHasBeenDisplayed);
    	if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed
                && hasValidBitmap()) {

            getBitmap().recycle();
            try {
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
            CxLog.e("imageView", "release bitmap---------------------------------");
        }else{
        	 CxLog.w("imageView", "*************do not release bitmap****************");
        }
    }

    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }

}
