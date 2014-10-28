package com.chuxin.family.parse.been;

import java.util.ArrayList;

import com.chuxin.family.parse.been.data.KidFeedChildrenData;
import com.chuxin.family.parse.been.data.KidFeedData;

public class CxKidFeedList extends CxParseBasic {

	private ArrayList<KidFeedData>  datas;
	private ArrayList<KidFeedChildrenData> kids;
	
	public ArrayList<KidFeedData> getDatas() {
		return datas;
	}
	public void setDatas(ArrayList<KidFeedData> datas) {
		this.datas = datas;
	}
	public ArrayList<KidFeedChildrenData> getKids() {
		return kids;
	}
	public void setKids(ArrayList<KidFeedChildrenData> kids) {
		this.kids = kids;
	}
	
	
	
}
