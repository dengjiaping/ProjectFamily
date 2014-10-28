package com.chuxin.family.net;

import java.io.InputStream;
/**
 * 为文件下载进度而写的
 * @author shichao
 *
 */
public class CxNetworkInputstream {

	public long contentLength; //连接上时候获取的文件总长度（以这个参数来矫正文件是否下载完整）
	
	public InputStream netIs; //返还给客户端的流
}
