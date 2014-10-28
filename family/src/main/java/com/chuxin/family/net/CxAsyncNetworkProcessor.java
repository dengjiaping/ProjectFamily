package com.chuxin.family.net;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

public abstract class CxAsyncNetworkProcessor {
	private boolean mPostFlag = false; //默认是FALSE表示get方式， TRUE为post方式
	private String mSubUrl = null; //请求路径（不包含域名等）
	private List<NameValuePair> mParams = null;
	private String mSavePath = null;
	
	public CxAsyncNetworkProcessor(boolean isPost, String subUrl, 
			List<NameValuePair> params, String savePath){
		this.mPostFlag = isPost;
		this.mParams = params;
		this.mSubUrl = subUrl;
		this.mSavePath = savePath;
	}
	
	public boolean isPostMethod(){
		return mPostFlag;
	}
	
	public String getUrl(){
		return mSubUrl;
	}
	
	public List<NameValuePair> getParams(){
		return mParams;
	}
	
	public String getSavePath(){
		return mSavePath;
	}

	public abstract void preExeNetwork(); //发送请求之前
//	public abstract void doNetwork(String method, String url, NameValuePair... nameValuePairs); //发送请求
	public abstract void pushUpdateProgress(int progress); //网络进度更新
	public abstract void dealResOfNetwork(Object obj); //针对解析后的数据处理
	
}
