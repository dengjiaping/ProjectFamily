package com.chuxin.family.parse.been.data;

import java.util.ArrayList;
import java.util.List;

public class TabloidData {
	private int category_id;																					// 所属分类ID
	private List<TabloidObj> tabloids = new ArrayList<TabloidObj>();			// 小报数据列表
	
	
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public List<TabloidObj> getTabloids() {
		return tabloids;
	}
	public void setTabloids(List<TabloidObj> tabloids) {
		this.tabloids = tabloids;
	}
	
	

}
