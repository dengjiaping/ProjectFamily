package com.chuxin.family.zone;

import java.util.List;

import com.chuxin.family.model.CxSubjectInterface;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.utils.CxLog;

public class CxZoneParam extends CxSubjectInterface{
	public static final String FEED_DATA = "feed_data";
	private FeedListData mFeedsData; //发帖子成功后缓存的一个帖子数据
	
	private List<FeedPhoto> mPhotos; //二人空间点击看图片时的图片组数据
	
	public static final String S_ADD_PHOTOS_PATH = "addPhotosPath";
	private List<String> mAddPhotosPath; //为二人空间添加的图片
	
	private static CxZoneParam zoneParam;
	
	private CxZoneParam(){};
	
	public static CxZoneParam getInstance(){
		if (null == zoneParam) {
			zoneParam = new CxZoneParam();
		}
		return zoneParam;
	}

	public FeedListData getFeedsData() {
		return mFeedsData;
	}

	public void setFeedsData(FeedListData feedsData) {
		CxLog.i("setFeedsData ready = operate", "feedsData="+feedsData);
		if (null == feedsData) { //这种情况不告知了，因为分享成功后，不会出null数据
			return;
		}
		if (feedsData.equals(mFeedsData)) {
			return;
		}
		CxLog.i("setFeedsData ready notify", "feedsData="+feedsData);
		
		this.mFeedsData = feedsData;
		notifyObserver(FEED_DATA);
	}
	
	/*public void insertFeedData(FeedListData feedData){ //新帖子始终在第一个数据
		if (null == feedData) {
			return;
		}
		if (null == mFeedsData) {
			mFeedsData = new ArrayList<FeedListData>();
		}
		List<FeedListData> tempList = new ArrayList<FeedListData>();
		tempList.add(feedData);
		if ( (null != mFeedsData) && (mFeedsData.size() > 1) ) {
			tempList.addAll(mFeedsData);
		}
		this.mFeedsData = tempList;
	}*/
	
	/*public void removeFeedData(int position){
		if ( (null == mFeedsData) || (position < 0) 
				|| (position > mFeedsData.size()) ){
			return;
		}
		mFeedsData.remove(position);
	}*/
	
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
	
	public List<FeedPhoto> getPhotos() {
		return mPhotos;
	}

	public void setPhotos(List<FeedPhoto> photos) {
		this.mPhotos = photos;
	}
	
}
