package com.chuxin.family.resource;

import com.chuxin.family.R;


/**
 * 颜色角色资源管理类
 * @author wentong.men
 *
 */
public class CxResourceColor {

	
	private CxResourceColor() {}
	
	private static CxResourceColor color;
	
	/**
	 * 
	 * @param flag  true  男 ，  false  女
	 * @return
	 */
	public static CxResourceColor getInstance(){
		
		if(color==null){
			color=new CxResourceColor();
		}
		
		return color;
	}
	
	
	/**
	 * <!-- colors
<color name="cx_fa_role_co_authen_new">#f0e7e0</color>
<color name="cx_fa_role_co_authen_new_s">#ecdfd7</color>

<color name="cx_fa_role_co_accounting_account_item_pink">#edb2b2</color>
<color name="cx_fa_role_co_accounting_account_item_pink_s">#b2c8ed</color>
<color name="cx_fa_role_co_accounting_account_item_blue">#b2c8ed</color>
<color name="cx_fa_role_co_accounting_account_item_blue_s">#edb2b2</color>
 -->
	 */
	
	public  int co_main_login_authen_new=R.color.cx_fa_role_co_authen_new;
	public  int co_accounting_account_item_pink=R.color.cx_fa_role_co_accounting_account_item_pink;
	public  int co_accounting_account_item_blue=R.color.cx_fa_role_co_accounting_account_item_blue;
	
	
	public void setColorType(boolean flag){
		if(flag){
			co_main_login_authen_new=R.color.cx_fa_role_co_authen_new;
			co_accounting_account_item_pink=R.color.cx_fa_role_co_accounting_account_item_pink;
			co_accounting_account_item_blue=R.color.cx_fa_role_co_accounting_account_item_blue;
		}else{
			co_main_login_authen_new=R.color.cx_fa_role_co_authen_new_s;
			co_accounting_account_item_pink=R.color.cx_fa_role_co_accounting_account_item_pink_s;
			co_accounting_account_item_blue=R.color.cx_fa_role_co_accounting_account_item_blue_s;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
