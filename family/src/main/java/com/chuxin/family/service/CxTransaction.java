package com.chuxin.family.service;

public class CxTransaction {
	private String mTimestamp; //本次事务的时间戳(设置为字符串形式为了便于list检测存在与否）
	
	private Runnable mTask; //事务 
	
	public CxTransaction(String timestamp, Runnable task){
		this.mTask = task;
		this.mTimestamp = timestamp;
	}

	public String getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(String timestamp) {
		this.mTimestamp = timestamp;
	}

	public Runnable getmTask() {
		return mTask;
	}

	public void setTask(Runnable task) {
		this.mTask = task;
	}

}
