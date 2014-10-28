package com.chuxin.family.neighbour;

import java.util.ArrayList;
import java.util.List;

import com.chuxin.family.model.CxSubjectInterface;
import com.chuxin.family.models.Neighbour;
import com.chuxin.family.parse.been.data.AnswerHomeWeekItem;
import com.chuxin.family.parse.been.data.FeedListData;
import com.chuxin.family.parse.been.data.FeedPhoto;
import com.chuxin.family.parse.been.data.InvitationData;
import com.chuxin.family.parse.been.data.InvitationPhoto;
import com.chuxin.family.parse.been.data.InvitationReply;
import com.chuxin.family.parse.been.data.InvitationUserInfo;
import com.chuxin.family.utils.CxLog;

public class CxNeighbourParam extends CxSubjectInterface {
	
	public static final String NEIGHBOUR_DATA = "nb_feed_data"; 
	private String mFeedsData; //发帖子成功后缓存的一个帖子数据
	
	public static final String S_ADD_PHOTOS_PATH = "nb_addPhotosPath";
	
	public static final String S_ADD_MESSAGE_PHOTOS_PATH = "nb_addMessagePhotosPath";
	
	public static final String NB_WIFE_ICON="nb_wife_icon";
	private String mNbWifeIcon;
	
	public static final String NB_HUSBAND_ICON="nb_husband_icon";
	private String mNbHusbandIcon;
	
	public static final String NB_ADD_REPLY="nb_add_reply";
	private InvitationReply mNbAddReply;
	
	public static final String NB_DEL_REPLY="nb_del_reply";
	private InvitationReply mNbDelReply;
	
	public static final String NB_CHANGE_NAME="nb_change_name";
	private InvitationUserInfo mNbChangeName;
	
	public static final String NB_DEL_INVITATION="nb_del_invitation";
	private InvitationData mNbDelInvitation;
	
	public static final String NB_ADD_MESSAGE_GROUP_ID="nb_message_group_id";//到私聊页面时需要传递的参数

	
	public String getNbWifeIcon() {
		return mNbWifeIcon;
	}

	public void setNbWifeIcon(String nbWifeIcon) {
		if(nbWifeIcon==null){
			return;
		}	
		if(nbWifeIcon.equals(mNbWifeIcon)){
			return;
		}		
		this.mNbWifeIcon = nbWifeIcon;
		notifyObserver(NB_WIFE_ICON);
	}

	public String getNbHusbandIcon() {
		return mNbHusbandIcon;
	}

	public void setNbHusbandIcon(String nbHusbandIcon) {
		if(nbHusbandIcon==null){
			return;
		}	
		if(nbHusbandIcon.equals(mNbHusbandIcon)){
			return;
		}		
		this.mNbHusbandIcon = nbHusbandIcon;
		notifyObserver(NB_HUSBAND_ICON);
	}

	public InvitationReply getNbAddReply() {
		return mNbAddReply;
	}

	public void setNbAddReply(InvitationReply nbAddReply) {
		if(nbAddReply==null){
			return;
		}	
		
		if(nbAddReply.equals(mNbAddReply)){
			return ;
		}
		
		this.mNbAddReply = nbAddReply;
		notifyObserver(NB_ADD_REPLY);
	}

	public InvitationReply getNbDelReply() {
		return mNbDelReply;
	}

	public void setNbDelReply(InvitationReply nbDelReply) {
		if(nbDelReply==null){
			return;
		}	
		if(nbDelReply.equals(mNbDelReply)){
			return ;
		}
		
		this.mNbDelReply = nbDelReply;
		notifyObserver(NB_DEL_REPLY);
	}

	public InvitationUserInfo getNbChangeName() {
		return mNbChangeName;
	}

	public void setNbChangeName(InvitationUserInfo nbChangeName) {
		if(nbChangeName==null){
			return; 
		}			
		
		this.mNbChangeName = nbChangeName;
		notifyObserver(NB_CHANGE_NAME);
	}

	public InvitationData getNbDelInvitation() {
		return mNbDelInvitation;
	}

	public void setNbDelInvitation(InvitationData nbDelInvitation) {
		if(nbDelInvitation==null){
			return;
		}	
		if(nbDelInvitation.equals(mNbDelInvitation)){
			return;
		}
		
		this.mNbDelInvitation = nbDelInvitation;
		notifyObserver(NB_DEL_INVITATION);
	}
	
	

	private List<InvitationPhoto> mPhotos; //密邻点击看图片时的图片组数据
	
	private ArrayList<String> mAddPhotosPath; //为密邻添加的图片
	
	private List<String> mAddMessagePhotosPath; //写留言中的图片
	
	private static CxNeighbourParam nbParam;
	
	private CxNeighbourParam(){};
	
	public static CxNeighbourParam getInstance(){
		if (null == nbParam) {
			nbParam = new CxNeighbourParam();
		}
		return nbParam;
	}
	

	public String getInvitationData() {
		return mFeedsData;
	}
	
	public void setInvitationData(String feedsData) {
		CxLog.i("setFeedsData ready = operate", "feedsData="+feedsData);
		if (null == feedsData) { //这种情况不告知了，因为分享成功后，不会出null数据
			return;
		}
		if (feedsData.equals(mFeedsData)) {
			return;
		}
		
		this.mFeedsData = feedsData;
		notifyObserver(NEIGHBOUR_DATA);
	}
	
	
	
	
	
	
	
	
	
	
	
	public ArrayList<String> getAddPhotosPath() {
		return mAddPhotosPath;
	}

	public void setAddPhotosPath(ArrayList<String> addPhotosPath) {
		this.mAddPhotosPath = addPhotosPath;
	}
	
	protected void removeSpecialImage(int position){
		if ( (null == mAddPhotosPath) || (mAddPhotosPath.size() < 1) ) {
			return;
		}
		if ( (0 > position) ||(position >= mAddPhotosPath.size()) ){
			return;
		}
		mAddPhotosPath.remove(position);
		notifyObserver(S_ADD_PHOTOS_PATH);
	}
	
	
	
	public List<String> getAddMessagePhotosPath() {
		return mAddMessagePhotosPath;
	}
	
	public void setAddMessagePhotosPath(List<String> addPhotosPath) {
		this.mAddMessagePhotosPath = addPhotosPath;
	}
	
	protected void removeMessageSpecialImage(int position){
		if ( (null == mAddMessagePhotosPath) || (mAddMessagePhotosPath.size() < 1) ) {
			return;
		}
		if ( (0 > position) ||(position >= mAddMessagePhotosPath.size()) ){
			return;
		}
		mAddMessagePhotosPath.remove(position);
		notifyObserver(S_ADD_MESSAGE_PHOTOS_PATH);
	}
	
	
	public List<InvitationPhoto> getPhotos() {
		return mPhotos;
	}

	public void setPhotos(List<InvitationPhoto> photos) {
		this.mPhotos = photos;
	}
	
/*****************************谁家最聪明***********************************/
	
	
	//谁家最聪明需要的两个参数
	public static final  String ANSWER_HOME_LIST="answer_home_list";
	public ArrayList<AnswerHomeWeekItem> weekItems;
	public static final  String ANSWER_WEEK_RANK="answer_week_rank";


	public ArrayList<AnswerHomeWeekItem> getWeekItems() {
		return weekItems;
	}

	public void setWeekItems(ArrayList<AnswerHomeWeekItem> weekItems) {
		this.weekItems = weekItems;
	}
	
	/***************************密邻管理*****************************************/
	
	public static final String NEIGHBOUR_REMOVE="neighbour_remove";
	public ArrayList<Neighbour> neighbourItems;


	public ArrayList<Neighbour> getNeighbourItems() {
		return neighbourItems;
	}

	public void setNeighbourItems(ArrayList<Neighbour> neighbourItems) {
		this.neighbourItems = neighbourItems;
		notifyObserver(NEIGHBOUR_REMOVE);
	}
	
	
	
	
	
	
	
	
	
	
}
