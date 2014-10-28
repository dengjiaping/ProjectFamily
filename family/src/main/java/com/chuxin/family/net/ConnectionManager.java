package com.chuxin.family.net;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLog;

public class ConnectionManager {
	public interface JSONCaller {
		public int call(Object result);
	}

	private static String TAG = "ConnectionManager";

	private ExecutorService mExecutor;
//	private final static DefaultHttpClient sClient = HttpApi.createHttpClient();
	private static HttpApi mApi = new HttpApi(HttpApi.createHttpClient(), Globals.getInstance()
			.getClientVersion());
	private ConcurrentHashMap<HttpRequestBase, JSONCaller> mRequestMap = null;

	public ConnectionManager() {
		mRequestMap = new ConcurrentHashMap<HttpRequestBase, JSONCaller>();
		mExecutor = new ThreadPoolExecutor(4, 10, 60, TimeUnit.SECONDS, 
				new LinkedBlockingQueue<Runnable>(),
				new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	public static HttpApi getHttpApi() {
		return mApi;
	}

	private Callable<HttpRequestBase> newRequestCall(
			final HttpRequestBase request) {
		return new Callable<HttpRequestBase>() {

			public HttpRequestBase call() {
				JSONObject result = null;
				try {
					CxLog.w(TAG, "Request ready send");
					result = mApi.executeHttpRequest(request);
				} catch (Exception e) {
					CxLog.w(TAG, "IOException ----"+e.getMessage());
				} finally {
					CxLog.w(TAG, "Request finished");
					process(request, result);
				}
				return request;
			}
		};
	}

	protected void process(Object key, JSONObject result) {
		JSONCaller callback = (JSONCaller) mRequestMap.get(key);
		callback.call(result);
		mRequestMap.remove(key);
	}
	
	class RkNetwork implements Callable<HttpRequestBase>, Runnable{

		private HttpRequestBase mRequest;
		
		public RkNetwork(HttpRequestBase request){
			mRequest = request;
		}
		
		@Override
		public void run() {
			JSONObject result = null;
			try {
				CxLog.w(TAG, "Request ready send");
				result = mApi.executeHttpRequest(mRequest);
			} catch (Exception e) {
				CxLog.w(TAG, "IOException ----"+e.getMessage());
			} finally {
				CxLog.w(TAG, "Request finished");
				process(mRequest, result);
			}
		}

		@Override
		public HttpRequestBase call() throws Exception {
			return mRequest;
		}
		
	} 

	public void doHttp(HttpRequestBase request, JSONCaller caller) {
		synchronized (mRequestMap) {
			if (mRequestMap.put(request, caller) == null) {
				RkNetwork work = new RkNetwork(request);
				CxLog.w(TAG, "issuing new request for: "
						+ work.mRequest.getURI().getRawPath());
				mExecutor.execute(work);
			}else{
				CxLog.w(TAG, "redo request for network");
			}
			
		}
	}

	public void doHttpGet(String url, JSONCaller caller,
			NameValuePair... nameValuePairs) {
		HttpRequestBase request = mApi.createHttpGet(url, nameValuePairs);
		doHttp(request, caller);
	}

	public void doHttpGet(String url, JSONCaller caller,
			List<NameValuePair> nameValuePairs) {
		HttpRequestBase request = mApi.createHttpGet(url, nameValuePairs);
		doHttp(request, caller);
	}
	
	public void doHttpPost(String url, JSONCaller caller,
			NameValuePair... nameValuePairs) {
		HttpRequestBase request = mApi.createHttpPost(url, nameValuePairs);
		doHttp(request, caller);
	}
	
	public void doHttpPost(String url, JSONCaller caller, List<NameValuePair> nameValuePairs) {
		HttpRequestBase request = mApi.createHttpPost(url, nameValuePairs);
		doHttp(request, caller);
	}

	public CxNetworkInputstream obtainImageFile(String url,
			NameValuePair... nameValuePairs) {
		if (null == url) {
			return null;
		}
		HttpRequestBase request = mApi.createHttpGet(url, nameValuePairs);
		return mApi.requestFile(request);
	}

	public CxNetworkInputstream obtainAudioFile(String url,
			NameValuePair... nameValuePairs) {
		if (null == url) {
			return null;
		}
		HttpRequestBase request = mApi.createHttpGet(url, nameValuePairs);
		return mApi.requestFile(request);
	}

	public void cancelRequest(String url) { // 为long polling 取消网络请求开设接口
		if (null == url) {
			return;
		}
		if (null != mRequestMap) {
			Enumeration<HttpRequestBase> enumeration = mRequestMap.keys();
			if (null == enumeration) {
				return;
			}
			while (enumeration.hasMoreElements()) {
				HttpRequestBase request = enumeration.nextElement();
				if (null == request) {
					continue;
				}
				String s = request.getRequestLine().getUri();
				s = s.substring(0, s.indexOf("?"));
				CxLog.i("!!", ""+s);
				if (url.equals(s)) {
					try {
						CxLog.i("abort request", "ready!!!!");
						synchronized (mRequestMap) {
							mRequestMap.remove(request);
						}
						request.abort();
						CxLog.i("abort request", "***done***");
					} catch (Exception e) {
						CxLog.e("request network", ""+e.getMessage());
					}
				}
			}
		}

	}

}
