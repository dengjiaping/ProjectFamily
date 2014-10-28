package com.chuxin.family.resource;


/**
 * 角色资源管理类
 * @author wentong.men
 *
 */
public class CxResource {

	private CxResource() {}
	
	private static CxResource resource;
	
	
	
	public static CxResource getInstance(){
		
		if(resource==null){
			resource=new CxResource();
		}
		
		return resource;
	}

	/**
	 * 
	 * @param flag  true  男 ，  false  女
	 * @return
	 */
	public  void setType(boolean flag) {
		
		CxResourceDarwable.getInstance().setDrawableType(flag);
		CxResourceRaw.getInstance().setRawType(flag);
		CxResourceColor.getInstance().setColorType(flag);
		CxResourceString.getInstance().setStringType(flag);
	}
	
	
}
