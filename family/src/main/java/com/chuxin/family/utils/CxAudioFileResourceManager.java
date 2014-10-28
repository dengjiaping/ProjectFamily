/**
 * Copyright 2009 Joe LaPenna
 */

package com.chuxin.family.utils;

import com.chuxin.family.net.CxNetDownloadFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.net.Uri;

public class CxAudioFileResourceManager extends Observable{
    private static final String TAG = "RkAudioFileResourceManager";
    private static ConcurrentHashMap<String, CxAudioFileResourceManager> mResourceManagers = 
    	new ConcurrentHashMap<String, CxAudioFileResourceManager>();

    private CxFileBaseDiskCache mDiskCache;
    private CxAudioFileFetcher mRemoteResourceFetcher;
    private FetcherObserver mFetcherObserver = new FetcherObserver();
    
    //1
    public static CxAudioFileResourceManager getAudioFileResourceManager(Context ctx) {
    	return getResourceManager("audio", ctx);
    }

    //2
    private static CxAudioFileResourceManager getResourceManager(
    		String category, Context ctx) {
    	if (mResourceManagers.containsKey(category)) {
    		return mResourceManagers.get(category);
    	} else {
    		CxAudioFileResourceManager resourceManager = 
    			new CxAudioFileResourceManager(category, ctx);
    		mResourceManagers.put(category, resourceManager);
    		return resourceManager;
    	}
    }

    //3
    private CxAudioFileResourceManager(String cacheName, Context ctx) {
        this(new CxFileBaseDiskCache(cacheName, ctx));
    }

    //4
    private CxAudioFileResourceManager(CxFileBaseDiskCache cache) {
//    	super(cache, ctx);
        mDiskCache = cache;
        mRemoteResourceFetcher = new CxAudioFileFetcher(mDiskCache);
        mRemoteResourceFetcher.addObserver(mFetcherObserver);
    }

    /**
     * If IOException is thrown, we don't have the resource available.
     */
    public File getFile(Uri uri) {
        return mDiskCache.getFile(uri.toString());
    }
    /**
     * If IOException is thrown, we don't have the resource available.
     */
    public InputStream getInputStream(Uri uri) throws IOException {
        return mDiskCache.getInputStream(uri.toString());
    }

    //检测如果本地没有，才调用此接口
    public void request(Uri uri) {
    	CxLog.d("request audio file path ", uri.toString());
        mRemoteResourceFetcher.fetch(uri, uri.toString());
    }
    
    /**
     * Explicitly expire an individual item.
     */
    public void invalidate(Uri uri) {
        mDiskCache.invalidate(Uri.encode(uri.toString()));
    }
    
    public boolean exists(Uri uri) {
        return mDiskCache.exists(uri.toString());
    }

    public void shutdown() {
        mRemoteResourceFetcher.shutdown();
        mDiskCache.cleanup();
    }

    public void clear() {
        mRemoteResourceFetcher.shutdown();
        mDiskCache.clear();
    }

    public static abstract class ResourceRequestObserver implements Observer {

        private Uri mRequestUri;

        abstract public void requestReceived(Observable observable, Uri uri, long len);

        public ResourceRequestObserver(Uri requestUri) {
            mRequestUri = requestUri;
        }

        @Override
        public void update(Observable observable, Object data) {
            CxNetDownloadFile dataFile = (CxNetDownloadFile)data;
            if (dataFile.fileUri.equals(mRequestUri)) { //防止异步出错
                requestReceived(observable, dataFile.fileUri, dataFile.len);
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
