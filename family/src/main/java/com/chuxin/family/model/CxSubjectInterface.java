package com.chuxin.family.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class CxSubjectInterface {
	private List<CxObserverInterface> observers = new ArrayList<CxObserverInterface>();

    public synchronized void registerObserver(CxObserverInterface observer){
    	if (null == observer) {
			return;
		}
    	if (null == observers) {
			observers = new ArrayList<CxObserverInterface>();
		}
    	observers.add(observer);
    }
	
	public synchronized void unRegisterObsercer(CxObserverInterface observer){
		if (null == observers) {
			return;
		}
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}
	
	public void notifyObserver(final String fieldTag){
		if (null == observers) {
			return;
		}
		
		for(CxObserverInterface observer : observers){
			//当注册监听时传入null监听全局，或者更新的字段与传入的tag一致才告知更新
			if (null == observer.getListenTag() || 
					( (null != observer.getListenTag()) && observer.getListenTag().contains(fieldTag)) ) {
				if (!observer.isMainThread()) { //不在主线程形式
					observer.receiveUpdate(fieldTag);
					continue;
				}
				
				final CxObserverInterface tempObserver = observer;
				//以下是主线程
				Handler tempHandler = new Handler(Looper.getMainLooper()){
					@Override
					public void handleMessage(Message msg) {
						tempObserver.receiveUpdate(fieldTag);
						super.handleMessage(msg);
					}
				};
				tempHandler.sendEmptyMessage(1);
			}
		}
		
	};
	
}
