package com.chuxin.family.utils;

import com.chuxin.family.net.ConnectionManager;
import com.chuxin.family.net.CxNetDownloadFile;
import com.chuxin.family.net.CxNetworkInputstream;

import android.net.Uri;

import java.io.InputStream;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CxAudioFileFetcher extends Observable {

    private CxFileBaseDiskCache mResourceCache;
    private ExecutorService mExecutor;

    private ConcurrentHashMap<Request, Callable<Request>> mActiveRequestsMap 
    	= new ConcurrentHashMap<Request, Callable<Request>>();

    public CxAudioFileFetcher(CxFileBaseDiskCache cache) {
        mResourceCache = cache;
        mExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }

    //对外调用的接口:下载文件
    public void fetch(Uri uri, String hash) {
        Request request = new Request(uri, hash);
        synchronized (mActiveRequestsMap) {
            Callable<Request> fetcher = newRequestCall(request);
            if (mActiveRequestsMap.putIfAbsent(request, fetcher) == null) {
                mExecutor.submit(fetcher);
            } else {
                CxLog.d("", "Already have a pending request for: " + uri);
            }
        }
    }

    public void shutdown() {
        mExecutor.shutdownNow();
    }

    private Callable<Request> newRequestCall(final Request request) {
        return new Callable<Request>() {
            public Request call() {
                CxNetDownloadFile rkFile = new CxNetDownloadFile();
                try {
                	CxLog.e("download file ", request.hash);
                	CxNetworkInputstream rkInputstream = new ConnectionManager().obtainImageFile(request.hash);
                	if (null == rkInputstream) {
						return request;
					}
                	InputStream is = rkInputstream.netIs;
                	rkFile.fileUri = request.uri;
                	rkFile.len = rkInputstream.contentLength;
//                    InputStream is = new ConnectionManager().obtainImageFile(request.hash);
                    if (null == is) {
						return request;
					}
                    mResourceCache.store(request.hash, is);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mActiveRequestsMap.remove(request);
                    notifyObservers(rkFile);
                }
                return request;
            }
        };
    }

    private static class Request {
        Uri uri;
        String hash;

        public Request(Uri requestUri, String requestHash) {
            uri = requestUri;
            hash = requestHash;
        }
    }
    
}
