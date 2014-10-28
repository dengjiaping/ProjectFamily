package com.chuxin.family.resource;

import com.chuxin.family.R;
/**
 * raw文件角色资源管理类
 * @author wentong.men
 *
 */
public class CxResourceRaw {

	
	private CxResourceRaw() {}
	
	private static CxResourceRaw raw;
	
	/**
	 * 
	 * @param flag  true  男 ，  false  女
	 * @return
	 */
	public static CxResourceRaw getInstance(){
		
		if(raw==null){
			raw=new CxResourceRaw();
		}
		
		return raw;
	}
	
	
	/**
	 * <!-- raw
cx_fa_role_push_first_s.mp3
cx_fa_role_push_first.MP3
cx_fa_role_push_s.mp3
cx_fa_role_push.mp3
 -->
	 * 
	 */
	
	
	public int raw_push_first=R.raw.rk_fa_role_push_first;
	public int raw_push=R.raw.rk_fa_role_push;
	
	
	public void setRawType(boolean flag){
		if(flag){
			raw_push_first=R.raw.rk_fa_role_push_first;
			raw_push=R.raw.rk_fa_role_push;
		}else{
			raw_push_first=R.raw.rk_fa_role_push_first_s;
			raw_push=R.raw.rk_fa_role_push_s;
		}
	}
	
	
	
	
	
}
