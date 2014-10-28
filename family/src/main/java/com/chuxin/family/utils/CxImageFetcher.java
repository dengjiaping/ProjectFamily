package com.chuxin.family.utils;

import java.io.InputStream;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.net.Uri;
import android.view.View;

import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.CxNetworkInputstream;
import com.chuxin.family.widgets.CxImageView;

public class CxImageFetcher extends Observable {

    private DiskCache mResourceCache;
    private ExecutorService mExecutor;

    private ConcurrentHashMap<Request, Callable<Request>> mActiveRequestsMap 
    	= new ConcurrentHashMap<Request, Callable<Request>>();

    public CxImageFetcher(DiskCache cache) {
        mResourceCache = cache;
        mExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }

    //对外调用的接口:下载图片
    public void fetch(Uri uri, String hash, int height, int width, View view) {
        Request request = new Request(uri, hash, 0, 0, view);
        synchronized (mActiveRequestsMap) {
            Callable<Request> fetcher = newRequestCall(request);
            if (mActiveRequestsMap.putIfAbsent(request, fetcher) == null) {
                mExecutor.submit(fetcher);
            } else {
//                RkLog.d("", "Already have a pending request for: " + uri);
            }
        }
    }

    public void shutdown() {
        mExecutor.shutdownNow();
    }

    private Callable<Request> newRequestCall(final Request request) {
        return new Callable<Request>() {
            public Request call() {
                try {
//                	RkLog.e("download image ", request.hash);
                	CxNetworkInputstream rkInputStream = new ConnectionManager().obtainImageFile(request.hash);
                	if (null == rkInputStream) {
						return request;
					}
                    InputStream is = rkInputStream.netIs;
                    if (null == is) {
						return request;
					}
                    mResourceCache.store(request, is, true);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mActiveRequestsMap.remove(request);
                    notifyObservers(request.uri);
                }
                return request;
            }
        };
    }

    public class Request {
        Uri uri;
        String hash;
        int height;
        int width;
        View imageview;

        public Request(Uri requestUri, String requestHash, 
        		int height, int width, View imageview) {
            uri = requestUri;
            hash = requestHash;
            this.height = height;
            this.width = width;
            this.imageview = imageview;
        }

        /*@Override
        public int hashCode() {
            return hash.hashCode();
        }*/
    }
    
}
