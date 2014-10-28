package com.chuxin.family.parse.been;

import com.chuxin.family.parse.been.data.KidFeedChildrenData;
/**
 * 孩子资料数据接口解析
 * @author shichao.wang
 *
 */
public class CxKidsInfoData extends CxParseBasic {

	private KidFeedChildrenData kidInfo = new KidFeedChildrenData();
	
    public KidFeedChildrenData getKidInfo() {
        return kidInfo;
    }
    public void setKidInfo(KidFeedChildrenData kidInfo) {
        this.kidInfo = kidInfo;
    }
	
}
