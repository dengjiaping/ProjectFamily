package com.chuxin.family.parse.been;

import java.util.List;

import com.chuxin.family.parse.been.data.TabloidCateConfData;
import com.chuxin.family.parse.been.data.TabloidData;

public class CxTabloid  extends CxParseBasic{
	private List<TabloidData> dataList;

	public List<TabloidData> getDataList() {
		return dataList;
	}

	public void setData(List<TabloidData> dataList) {
		this.dataList = dataList;
	}
}
