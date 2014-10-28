package com.chuxin.family.net;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chuxin.family.utils.CxLog;

import android.os.AsyncTask;
/**
 * 专为部分文件下载需要进度而写
 * @author shichao.wang
 *
 */
public class CxAsyncNetwork extends AsyncTask<Object, Integer, Object> {	
	
	private CxAsyncNetworkProcessor mProcessor;
	private HttpUriRequest mRequest = null;
	
	public CxAsyncNetwork(CxAsyncNetworkProcessor processor) throws Exception{
		if (null == processor) {
			throw new Exception("param processor can not be null");
		}
		if (null == mProcessor.getUrl()) {
			throw new Exception("member url of  processor can not be null");
		}
		
		this.mProcessor = processor;
	}
	
	@Override
	protected void onCancelled() {
		if(!mRequest.isAborted()){
			mRequest.abort();
		}
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Object result) {
		mProcessor.dealResOfNetwork(result);
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		mProcessor.preExeNetwork();
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		
		try {
			mProcessor.pushUpdateProgress(values[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onProgressUpdate(values);
	}

	@Override
	protected Object doInBackground(Object... arg0) {
		if (null == mProcessor) {
			return null;
		}
		HttpApi mApi = ConnectionManager.getHttpApi();
		DefaultHttpClient client = mApi.getmHttpClient();
		
		if (mProcessor.isPostMethod()) {
			mRequest = mApi.createHttpPost(mProcessor.getUrl(), mProcessor.getParams());
		}else{
			mRequest = mApi.createHttpGet(mProcessor.getUrl(), mProcessor.getParams());
		}
		try {
			HttpResponse response = client.execute(mRequest);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (CxNetworkState.NETWORK_NORMAL != statusCode) {
				
				return null;
			}
			//以下是网络正常
			HttpEntity entity = response.getEntity();
			InputStream iStream = entity.getContent();
			long contentLength = entity.getContentLength();
			long currLen = 0;
			FileOutputStream fos = new FileOutputStream(mProcessor.getSavePath());
			byte[] buff = new byte[1024];
			int len = -1;
			while(-1 != (len = iStream.read(buff, 0, 1024))){
				fos.write(buff, 0, len);
				currLen += len;
				publishProgress((int)(currLen/contentLength));
			}
			fos.flush();
			fos.close();
			iStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			CxLog.i("", e.toString());
		}
		
		
		return null;
	}

}
