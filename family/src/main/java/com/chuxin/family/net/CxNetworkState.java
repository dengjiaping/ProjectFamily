package com.chuxin.family.net;

public class CxNetworkState {

	public static int NETWORK_NORMAL = 200;
	public static int NETWORK_UNREACHEABLE = 404; //请求资源不存在（路径错误）
	public static int NETWORK_FORBIDDEN = 401; //需要身份验证
	public static int NETWORK_ILLEGAL_STATE = 400; //语法错误
	public static int NETWORK_INTERNAL_ERROR = 500; //服务器错误
	public static int NETWORK_TERMINAL_FAIL = -1; //终端失败（可能脱网登录失败）
	
}
