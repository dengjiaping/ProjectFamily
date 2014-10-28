package com.chuxin.family.kids;

import java.util.List;

import com.chuxin.family.model.CxSubjectInterface;
import com.chuxin.family.neighbour.CxNeighbourParam;
import com.chuxin.family.utils.CxLog;

public class CxKidParam extends CxSubjectInterface{

	
	private CxKidParam(){};
	private static CxKidParam param;
	public static CxKidParam getInstance(){
		if (null == param) {
			param = new CxKidParam();
		}
		return param;
	}
	
	public static String KID_ADD_DATA="kid_add_data";
	private String mFeedsData; //发帖子成功后缓存的一个帖子数据
	public String getmFeedsData() {
		return mFeedsData;
	}
	public void setFeedsData(String feedsData) {
		if (null == feedsData) { //这种情况不告知了，因为分享成功后，不会出null数据
			return;
		}
		if (feedsData.equals(mFeedsData)) {
			return;
		}
		this.mFeedsData = feedsData;
		notifyObserver(KID_ADD_DATA);
	}
	
	
	public static final String S_ADD_PHOTOS_PATH = "addPhotosPath";
	private List<String> mAddPhotosPath; //为二人空间添加的图片
	
	
	public List<String> getAddPhotosPath() {
		return mAddPhotosPath;
	}

	public void setAddPhotosPath(List<String> addPhotosPath) {
		this.mAddPhotosPath = addPhotosPath;
//		notifyObserver(S_ADD_PHOTOS_PATH);
	}
	
	protected void removeSpecialImage(int position){
		if ( (null == mAddPhotosPath) || (mAddPhotosPath.size() < 1) ) {
			return;
		}
		if ( (0 > position) ||(position >= mAddPhotosPath.size()) ){
			return;
		}
		
		mAddPhotosPath.remove(position);
		CxLog.i("########", "has deleted add feed image position="+position);
		notifyObserver(S_ADD_PHOTOS_PATH);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
