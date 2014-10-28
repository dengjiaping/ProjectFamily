package com.chuxin.family.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * long polling 中如果有新的结对请求时，需要启动这个类
 * @author shichao.wang
 *
 */
public class CxTransactionExecutor {
	
	private LinkedBlockingQueue<Runnable> mQueue;
	private ThreadPoolExecutor mExecutor;
	private static List<String> mRequstMatches = new ArrayList<String>(); //存储时间戳
	private static CxTransactionExecutor mTransaction;
	
	private CxTransactionExecutor(){
		mQueue = new LinkedBlockingQueue<Runnable>();
		mExecutor = new RkSubThreadpool(1, 1, 60, 
				TimeUnit.SECONDS, mQueue); //
	}
	
	public static CxTransactionExecutor getInstance(){
		if (null == mTransaction) {
			mTransaction = new CxTransactionExecutor();
		}
		return mTransaction;
	}
	
	public synchronized void addTask(final CxTransaction transaction){
		if (null == transaction) {
			return;
		}
		if (mRequstMatches.contains(transaction.getTimestamp())) {
			return;
		}
		
		mRequstMatches.add(transaction.getTimestamp());
		if (null == mExecutor) {
			mQueue = new LinkedBlockingQueue<Runnable>();
			mExecutor = new RkSubThreadpool(1, 1, 60, 
					TimeUnit.SECONDS, mQueue);
		}
		mExecutor.submit(transaction.getmTask());
	}
	
	class RkSubThreadpool extends ThreadPoolExecutor{

		public RkSubThreadpool(int corePoolSize, int maximumPoolSize,
				long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			
			try {
				if ( (null == this.getQueue()) || (this.getQueue().size() < 1) ){
					this.shutdownNow();
					mTransaction = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
