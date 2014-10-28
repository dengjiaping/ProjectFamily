package com.chuxin.family.model;

import java.util.List;

public abstract class CxObserverInterface {

	private List<String> mListenTag;
	
	private boolean mIsMainThread = true;
	/**
	 * @param listenTag,监听的字段（相应字段的名字在具体的createsubject里面定义）
	 * @param mIsMainThread,true表示receiveUpdate()方法做UI有关的操作，
	 * 		false表示receiveUpdate()是处理UI无关的操作，默认是有关UI主线程
	 */
	public void setListenTag(List<String> listenTag){
		this.mListenTag = listenTag;
	}
	
	public List<String> getListenTag(){
		return mListenTag;
	}
	
	public boolean isMainThread() {
		return mIsMainThread;
	}

	public void setMainThread(boolean isMainThread) {
		this.mIsMainThread = isMainThread;
	}

	public abstract void receiveUpdate(String actionTag);
	
}
