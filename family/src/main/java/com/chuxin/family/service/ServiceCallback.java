package com.chuxin.family.service;

import com.chuxin.family.net.ConnectionManager.JSONCaller;

public class ServiceCallback implements JSONCaller {

	public boolean complete = false; //默认false未完成
	public Object result;

	@Override
	public int call(Object result) {
		// 对数据进行处理
		this.result = result;

		complete = true;
		return 0;
	}
	
}
