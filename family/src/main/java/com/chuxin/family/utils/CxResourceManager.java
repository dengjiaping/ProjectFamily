/**
 * Copyright 2009 Joe LaPenna
 */

package com.chuxin.family.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import com.chuxin.family.widgets.CxImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.view.View;

public class CxResourceManager extends Observable {
    private static final String TAG = "RemoteResourceManager";
    private static final boolean DEBUG = true;
    private static ConcurrentHashMap<String, CxResourceManager> mResourceManagers = 
    	new ConcurrentHashMap<String, CxResourceManager>();

    private DiskCache mDiskCache;
    private CxImageFetcher mRemoteResourceFetcher;
    private FetcherObserver mFetcherObserver = new FetcherObserver();
    
    private String refTag = null; 
    
    /**
     * 图片管理对外的接口
     * @param requester，rkimageview所在的fragment或者activity
     * @param category，图片应存放的目录名称
     * @param ctx，rkimageview所在的activity
     * @return
     */
    public static CxResourceManager getInstance(Object requester, 
    		String category, Context ctx) {
    	if (null == ctx) {
			return null;
		}
    	if (null == category) {
			category = "default";
		}
    	
    	String reqInterface = "";
    	if (null == requester) {
    		return null;
		}else{
			reqInterface = requester.getClass().getName();
		}
    	
    	if (mResourceManagers.containsKey(reqInterface)) {
    		return mResourceManagers.get(reqInterface);
    	} else {
    		CxResourceManager resourceManager = new CxResourceManager(category, ctx, reqInterface);
    		mResourceManagers.put(reqInterface, resourceManager);
    		return resourceManager;
    	}
    }

    //3
    private CxResourceManager(String cacheName, Context ctx, String tag) {
        this(new CxBaseDiskCache(cacheName, ctx), tag);
    }

    //4
    private CxResourceManager(DiskCache cache, String tag) {
    	refTag = tag;
        mDiskCache = cache;
        mRemoteResourceFetcher = new CxImageFetcher(mDiskCache);
        mRemoteResourceFetcher.addObserver(mFetcherObserver);
    }

    /**
     * 对外提供的取内存图片
     * @param uri
     * @return
     */
    public BitmapDrawable getMemoryCacheImage(Uri uri){
    	return mDiskCache.getImageFromMemory(uri.toString());
    }
    
    /**
     * 对外提供取外存储的图片
     * @param name
     * @return
     */
    public BitmapDrawable getExtralBitmapDrawable(String name, View loaderView){
    	if (null == name) {
			return null;
		}
    	if (null == mDiskCache) {
			return null;
		}
    	return mDiskCache.getExtralBitmapDrawable(name, loaderView);
    }
    
    public boolean exists(Uri uri) {
    	if (null == uri) {
			return false;
		}
    	if (null == mDiskCache) {
			return false;
		}
        return mDiskCache.exists(uri.toString());
    }
    
    public boolean existAtMemory(Uri uri){
    	if (null == uri) {
			return false;
		}
    	if (null == mDiskCache) {
			return false;
		}
    	return mDiskCache.existAtMemory(uri.toString());
    }
    
    public File getStringFile(Uri uri){
    	if (null == uri) {
			return null;
		}
    	if (null == mDiskCache) {
			return null;
		}
    	return mDiskCache.getFile(uri.toString());
    }

    /**
     * If IOException is thrown, we don't have the resource available.
     */
    public File getFile(Uri uri) {
    	if (null == uri) {
			return null;
		}
    	if (null == mDiskCache) {
			return null;
		}
        return mDiskCache.getFile(Uri.encode(uri.toString()));
    }

    /**
     * If IOException is thrown, we don't have the resource available.
     */
    public InputStream getInputStream(Uri uri) throws IOException {
    	if (null == uri) {
			return null;
		}
    	if (null == mDiskCache) {
			return null;
		}
        return mDiskCache.getInputStream(uri.toString());
    }

    //检测如果本地没有，才调用此接口
    public void request(Uri uri, int height, int width, View view) {
//    	RkLog.e("request image path ", uri.toString());
        mRemoteResourceFetcher.fetch(uri, uri.toString(), height, width, view);
    }
    
    /**
     * Explicitly expire an individual item.
     */
    public void invalidate(Uri uri) {
        mDiskCache.invalidate(Uri.encode(uri.toString()));
    }

    public void shutdown() {
        mRemoteResourceFetcher.shutdown();
        mDiskCache.cleanup();
    }

    public void clearMemory() {
        try {
			mRemoteResourceFetcher.shutdown();
			mRemoteResourceFetcher = null;
			mDiskCache.clearMemory();
			mDiskCache = null;
			mResourceManagers.remove(refTag);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        try {
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void cleanCache(){
    	mDiskCache.clear();
    }

    public static abstract class ResourceRequestObserver implements Observer {

        private Uri mRequestUri;

        abstract public void requestReceived(Observable observable, Uri uri);

        public ResourceRequestObserver(Uri requestUri) {
            mRequestUri = requestUri;
        }

        @Override
        public void update(Observable observable, Object data) {
            Uri dataUri = (Uri)data;
            if (dataUri == mRequestUri) { //防止异步出错
                requestReceived(observable, dataUri);
            }
        }
    }

    //监听下载完成
    private class FetcherObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            setChanged();
            notifyObservers(data);
        }
    }
}
