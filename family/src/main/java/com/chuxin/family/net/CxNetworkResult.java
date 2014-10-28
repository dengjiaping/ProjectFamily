package com.chuxin.family.net;
/**
 * 服务端返回的值
 * @author shichao.wang
 *
 */
public class CxNetworkResult {

	public final static int NET_SUCCESS = 0; //成功：客户端根据API规则解析应答数据；
	public final static int NET_SUCCESS_ADDITION = 1; //成功：但有附加信息;客户端根据API规则解析应答数据；msg字段包含信息内容（文本或URL）
	public final static int NET_FAIL_BY_MAINTENANCE = 2; //失败：因运维操作而导致的访问失败；msg字段包含信息内容（文本或URL）
	public final static int NET_FAIL_BY_LOW_VERSION = 3; //失败：客户端版本不能满足服务器要求；
	public final static int NET_FAIL_BY_LONGPOLLING = 4; //失败；超时导致的失败（用于长轮训）
	public final static int NET_FAIL_BY_NEED_REGISTE = 999; //未注册账号（目前在登录时会发生）
	public final static int NET_FAIL_BY_UNLOGIN = 1000; //用户未登录：业务要求用户登录
	public final static int NET_FAIL_BY_ILLEGAL_STATE = 2000; //用户输入不合法：服务器API应答失败，用户输入不合法，msg包含解析；
	public final static int NET_FAIL_BY_RPC = 3000; //服务器异常：后台RPC服务器失败
	public final static int NET_FAIL_BY_OTHER = -1; //其他原因导致的失败
	
	
}