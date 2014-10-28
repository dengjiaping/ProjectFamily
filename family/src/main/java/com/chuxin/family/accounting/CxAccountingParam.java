package com.chuxin.family.accounting;

import com.chuxin.family.model.CxSubjectInterface;

/**
 * 记账参数类
 * @author wentong.men
 *
 */
public class CxAccountingParam extends CxSubjectInterface {
	
	private CxAccountingParam(){};
	
	private static CxAccountingParam param;
	
	public static CxAccountingParam getInstance(){
		if (null == param) {
			param = new CxAccountingParam();
		}
		return param;
	}
	
	public static final String ACCOUNT_CONTENT="account_content";//账目从明细修改是的参数
	
	//账目增删改的监听参数
	public static final String ADD_ACCOUNT="add_account";
	private String mAddAccount;
	
	public static final String UPDATE_ACCOUNT="update_account";
	private String mUpdateAccount;
	
	public static final String DELETE_ACCOUNT="delete_account";
	private String mDelAccount;

	
	
	public String getAddAccount() {
		return mAddAccount;
	}
	public void setAddAccount(String addAccount) {
		if(addAccount==null){
			return;
		}	
//		if(addAccount.equals(mAddAccount)){
//			return;
//		}		
		this.mAddAccount = addAccount;
		notifyObserver(ADD_ACCOUNT);
	}
	
	public String getUpdateAccount() {
		return mUpdateAccount;
	}
	public void setUpdateAccount(String updateAccount) {
		if(updateAccount==null){
			return;
		}	
//		if(updateAccount.equals(mUpdateAccount)){
//			return;
//		}		
		this.mUpdateAccount = updateAccount;
		notifyObserver(UPDATE_ACCOUNT);
	}
	
	public String getDelAccount() {
		return mDelAccount;
	}
	public void setDelAccount(String delAccount) {
		if(delAccount==null){
			return;
		}	
		if(delAccount.equals(mDelAccount)){
			return;
		}		
		this.mDelAccount = delAccount;
		notifyObserver(DELETE_ACCOUNT);
	}


}
