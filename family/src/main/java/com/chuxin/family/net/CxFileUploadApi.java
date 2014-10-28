package com.chuxin.family.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.settings.CxFile;
import com.chuxin.family.utils.Globals;
import com.chuxin.family.utils.CxLog;

/**
 * 带文件的数据提交API
 * 
 * @author shichao
 * 
 */
public class CxFileUploadApi extends ConnectionManager{

	private static final String S_BOUNDARY = getBoundry();

	private static final String DEFAULT_CLIENT_VERSION = "com.joelapenna.foursquare";
	private static final String CLIENT_VERSION_HEADER = "Owner-Agent";

	private ExecutorService mExecutor;
	private static HashMap<String, RkNetCallBack> mCallbacks;

//	private static HttpApi mApi = new HttpApi(HttpApi.createHttpClient(),
//	        Globals.getInstance().getClientVersion());

	public interface RkNetCallBack {
		public void callback(Object obj);
	}

	public CxFileUploadApi() {
		mExecutor = Executors.newCachedThreadPool();
		mCallbacks = new HashMap<String, CxFileUploadApi.RkNetCallBack>();
	}

	/**
	 * 提交文件
	 * 
	 * @param url
	 *            url路径
	 * @param params
	 *            表单参数
	 * @param files
	 *            要提交的图片
	 * @param back
	 *            回调
	 */
	public void requestSend(String url, Map<String, String> params,
			List<CxFile> files, RkNetCallBack back) {
		synchronized (mCallbacks) {
			if (mCallbacks.put(url, back) != null) { // 防止重复提交
				return;
			}
		}
		MyTask task = new MyTask(url, params, files);
		mExecutor.submit(task);
	}

	class MyTask implements Runnable {
		private String mPath;
		private Map<String, String> mParams;
		private List<CxFile> mFiles;

		private MyTask(String path, Map<String, String> params,
				List<CxFile> files) {
			this.mPath = path;
			this.mParams = params;
			this.mFiles = files;
		}

		@Override
		public void run() {
			JSONObject netObject = null;
			try {
//				netObject = RkFileUploadApi.sendDataWithImage(mPath, mParams,
//						mFiles);
				netObject = CxFileUploadApi.sendDataWithFile(mPath, mParams,
						mFiles);
			} catch (Exception e) {
				e.printStackTrace();
			}
			RkNetCallBack tempCallback = mCallbacks.get(mPath);
			if (null != tempCallback) {
				tempCallback.callback(netObject);
				mCallbacks.remove(mPath); // 释放
			} else {
				CxLog.i("neterr", "post send multData callback nullpoint");
			}
		}

	}

	private static JSONObject sendDataWithFile(String path,
			Map<String, String> params, List<CxFile> files) throws Exception {
		MultipartEntity mEntity = new MultipartEntity();
		if (null != params) {
			// 构建表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				StringBody par = new StringBody(entry.getValue());
				mEntity.addPart(entry.getKey(), par);
			}
		}
		
		if (null != files) {
			// 上传的文件
			for (CxFile file : files) {
				FileBody fileBody = new FileBody(new File(file.getFilname()));  
	            mEntity.addPart(file.getNameField(), fileBody);
			} // end for(files)
		} // end if(files)
		
		HttpPost post = new HttpPost(path); 
		post.setEntity(mEntity);
		
		DefaultHttpClient client = HttpApi.createHttpClient();
		HttpResponse response = client.execute(post);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != 200)
			return new JSONObject("{\"rc\":" + responseCode + "}");
		
		HttpEntity responseEntity = response.getEntity();
		InputStream is = responseEntity.getContent();
		int ch;
		StringBuilder b = new StringBuilder();
		while ((ch = is.read()) != -1) {
			b.append((char) ch);
		}

		return new JSONObject(b.toString());
	}

	/**
	 * HttpURLConnection的POST方式提交带文件（也可以无文件）数据
	 * 
	 * @param path
	 *            上传路径
	 * @param params
	 *            请求参数。 key为参数名,value为参数值
	 * @param files
	 *            上传的文件
	 * @return 正常就返回jsonobject, 否则返回null
	 */
//	private static JSONObject sendDataWithImage(String path,
//			Map<String, String> params, List<RkFile> files) {
//		try {
//
//			/*
//			 * URL url = new URL(path); HttpURLConnection conn =
//			 * (HttpURLConnection) url.openConnection(); conn.setDoInput(true);
//			 * conn.setDoOutput(true); conn.setUseCaches(false);//不使用Cache
//			 * conn.setRequestMethod("POST");
//			 * conn.setRequestProperty("Connection", "Keep-Alive");
//			 * conn.setRequestProperty("Charset", "UTF-8");
//			 * conn.setRequestProperty("Content-Type", S_MULTIPART_FORM_DATA +
//			 * "; boundary=" + S_BOUNDARY);
//			 */
//
//			URL url = new URL(path);
//			HttpURLConnection conn = mApi.createHttpURLConnectionPost(url,
//					S_BOUNDARY);
//
//			DataOutputStream outStream = new DataOutputStream(
//					conn.getOutputStream());
//
//			if (null != params) {
//				StringBuilder sb = new StringBuilder();
//				// 构建表单参数
//				for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
//					sb.append("--");
//					sb.append(S_BOUNDARY);
//					sb.append("/r/n");
//					sb.append("Content-Disposition: form-data; name=\""
//							+ entry.getKey() + "\"/r/n/r/n");
//					sb.append(entry.getValue());
//					sb.append("/r/n");
//				}
//
//				outStream.write(sb.toString().getBytes());// 发送表单数据
//			} // end if(params)
//
//			if (null != files) {
//				// 上传的文件
//				for (RkFile file : files) {
//					StringBuilder tempHead = new StringBuilder();
//					tempHead.append("--");
//					tempHead.append(S_BOUNDARY);
//					tempHead.append("/r/n");
//					tempHead.append("Content-Disposition: form-data;name=\""
//							+ file.getNameField() + "\";filename=\""
//							+ file.getFilname() + "\"/r/n");
//					tempHead.append("Content-Type: " + file.getContentType()
//							+ "/r/n/r/n");
//					outStream.write(tempHead.toString().getBytes());
//					// 将文件内容写入网络
//					if ((null == file.getFilname())
//							|| (!new File(file.getFilname()).exists())) {
//						continue;
//					}
//					FileInputStream fis = new FileInputStream(file.getFilname());
//					int tempInt = -1;
//					while (-1 != (tempInt = fis.read())) {
//						outStream.write(tempInt);
//					}
//
//					outStream.write("/r/n".getBytes());
//				} // end for(files)
//			} // end if(files)
//
//			byte[] end_data = ("--" + S_BOUNDARY + "--/r/n").getBytes();// 数据结束标志
//			outStream.write(end_data);
//			outStream.flush();
//			outStream.close();
//
//			int responseCode = conn.getResponseCode();
//			if (responseCode != 200)
//				return new JSONObject("{\"rc\":" + responseCode + "}");
//
//			InputStream is = conn.getInputStream();
//			int ch;
//			StringBuilder b = new StringBuilder();
//			while ((ch = is.read()) != -1) {
//				b.append((char) ch);
//			}
//			// outStream.close();
//			conn.disconnect();
//
//			// 不在此做json解析，否则没有通用性
//
//			return new JSONObject(b.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0) {
				_sb.append((char) time % 9);
			} else if (time % 3 == 1) {
				_sb.append((char) (65 + time % 26));
			} else {
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}
	
	

}
