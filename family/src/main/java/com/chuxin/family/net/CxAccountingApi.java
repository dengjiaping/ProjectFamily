package com.chuxin.family.net;

import java.util.Iterator;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.chuxin.family.models.ChargeHome;
import com.chuxin.family.models.ChargeMonthDetail;
import com.chuxin.family.models.ChargeYearDetail;
import com.chuxin.family.models.Model;
import com.chuxin.family.parse.CxAccountingParser;
import com.chuxin.family.parse.been.CxAccountDetailDayList;
import com.chuxin.family.parse.been.CxAccountDetailMonthList;
import com.chuxin.family.parse.been.CxAccountHomeList;
import com.chuxin.family.parse.been.CxParseBasic;
import com.chuxin.family.utils.CxLog;



public class CxAccountingApi extends ConnectionManager {
	
	private final String ACCOUNT_LIST = HttpApi.HTTP_SERVER_PREFIX + "Charge/home"; //记账首页
	private final String ACCOUNT_ADD_OR_UPDATE = HttpApi.HTTP_SERVER_PREFIX + "Charge/update"; //记账页面添加和修改账目
	private final String ACCOUNT_DELETE = HttpApi.HTTP_SERVER_PREFIX + "Charge/delete"; //删除账目账目
	private final String ACCOUNT_CHAT = HttpApi.HTTP_SERVER_PREFIX + "Charge/chart"; //饼图
	
	private final String ACCOUNT_YEAR_DETAIL = HttpApi.HTTP_SERVER_PREFIX + "Charge/year_detail"; //年度明细
	private final String ACCOUNT_MONTH_DETAIL = HttpApi.HTTP_SERVER_PREFIX + "Charge/month_detail"; //月度明细
	
	
	private CxAccountingApi() {};
	private static CxAccountingApi api;
	
	public static CxAccountingApi getInstance(){
		if (null == api) {
			api = new CxAccountingApi();
		}
		return api;
	}
	/**
	 * 记账首页数据列表
	 * @param callback 
	 * @param ctx
	 */
	public void requestAccountList(final JSONCaller callback,final Context ctx){
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;

				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAccountingApi_men", jObj.toString());
				CxAccountingParser parser=new CxAccountingParser();
				try {
					callback.call(parser.getAccountHomeList(jObj, ctx,false));
				} catch (Exception e) {
					e.printStackTrace();
				}
				

				return 0;
			}
		};

		this.doHttpGet(ACCOUNT_LIST, netCallback);	
	}
	
	/**
	 * 记账首页本地缓存
	 * @param context
	 * @return
	 */
	public CxAccountHomeList requestNativeHomeList(Context context){
		List<Model> chargehomedata= new ChargeHome(null, context).gets("1=1", new String[] {}, null, 0, 0); ///取全部数据
        if(null == chargehomedata|| chargehomedata.size() == 0){
            return null;
        }
        Iterator<Model> i = chargehomedata.iterator();
        JSONObject obj=null;
        while(i.hasNext()){
            ChargeHome chargehome = (ChargeHome)i.next();
            obj = chargehome.mData;
	    // 逻辑处理过程
        }
        CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>"+obj.toString());
        CxAccountingParser parser=new CxAccountingParser();
        CxAccountHomeList list=null;
		list = parser.getAccountHomeList(obj, context,true);

        return list;
	}
	
	
	
	
	/**
	 * 记账页面记账或修改账目
	 * @param id  账目id  第一次记录不需要
	 * @param type  类型
	 * @param money 金额
	 * @param category 消费类型
	 * @param from 来自
	 * @param date 日期
	 * @param desc 备注
	 * @param callback 
	 * @throws Exception
	 */
	public void requestAccountAddOrUpdate(String id,int type,String money,int category,
			int from,String date,String desc,final JSONCaller callback ) {
		
		if(TextUtils.isEmpty(money)){
			try {
				throw new Exception("money can not null");
			} catch (Exception e) {		
				e.printStackTrace();
			}
		}
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAccountingApi_men", jObj.toString());
				try {										
					int rc = -1;
					rc = jObj.getInt("rc");
					if(rc!=0){
						callback.call(null);
						return -2;
					}

					CxParseBasic saveResult = new CxParseBasic();
					saveResult.setRc(rc);
					saveResult.setMsg(jObj.getString("msg"));
					saveResult.setTs(jObj.getInt("ts"));
					callback.call(saveResult);
					
				} catch (Exception e) {
					e.printStackTrace();
				}		
				return 0;
			}
		};
		
		CxLog.i("RkAccountingApi_men", TextUtils.isEmpty(id)+"");
		
		if(!TextUtils.isEmpty(id)){
			this.doHttpPost(ACCOUNT_ADD_OR_UPDATE, netCallback,new BasicNameValuePair("id",id),new BasicNameValuePair("type",type+""),
					new BasicNameValuePair("money",money),new BasicNameValuePair("category",category+""),new BasicNameValuePair("from",from+""),
					new BasicNameValuePair("date",date),new BasicNameValuePair("desc",desc));	
		}else{
			this.doHttpPost(ACCOUNT_ADD_OR_UPDATE, netCallback,new BasicNameValuePair("type",type+""),new BasicNameValuePair("money",money),
					new BasicNameValuePair("category",category+""),new BasicNameValuePair("from",from+""),
					new BasicNameValuePair("date",date),new BasicNameValuePair("desc",desc));
		}
	}
	
	/**
	 * 记账页面删除账目
	 * @param id  账目id
	 * @param callback
	 * @throws Exception
	 */
	public void requestAccountDelete(String id,final JSONCaller callback){
		
		if(TextUtils.isEmpty(id)){
			try {
				throw new Exception("account id can not null");
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAccountingApi_men", jObj.toString());
				try {										
					int rc = -1;
					rc = jObj.getInt("rc");
					if(rc!=0){
						callback.call(null);
						return -2;
					}
					CxParseBasic deleteResult = new CxParseBasic();
					deleteResult.setRc(rc);
					deleteResult.setMsg(jObj.getString("msg"));
					deleteResult.setTs(jObj.getInt("ts"));
					callback.call(deleteResult);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			
				return 0;
			}
		};

		this.doHttpGet(ACCOUNT_DELETE, netCallback,new BasicNameValuePair("id",id));	
		
	}
	
	/**
	 * 年度明细
	 * @param date
	 * @param callback
	 */
	public void requestAccountYearDetails(final Context context,final String date,final int type,final JSONCaller callback){
		if(TextUtils.isEmpty(date)){
			try {
				throw new Exception("date id can not null,year");
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAccountingApi_men", jObj.toString());
				try {										
					CxAccountingParser parser=new CxAccountingParser();
					callback.call(parser.getAccountMonths(jObj,context,date,false,type));
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			
				return 0;
			}
		};

		this.doHttpGet(ACCOUNT_YEAR_DETAIL, netCallback,new BasicNameValuePair("date",date),new BasicNameValuePair("type",type+""));	
	}
	
	/**
	 * 年度明细本地缓存
	 * @param context
	 * @param date
	 * @return
	 */
	public CxAccountDetailMonthList requestNativeYearDetails(final Context context,final String date){
		
		CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>"+date);
//		List<Model> chargehomedata= new ChargeYearDetail(null,date, context).gets("1=1", new String[] {}, null, 0, 0); ///取全部数据
		Model chargehomedata= new ChargeYearDetail(null,date, context).get(date); ///取数据
        if(null == chargehomedata){
            return null;
        }
        
        JSONObject obj=null;
    	ChargeYearDetail chargehome = (ChargeYearDetail)chargehomedata;
        obj = chargehome.mData;
	    // 逻辑处理过程
        CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>"+chargehomedata.toString()+">>>>>>>>>>>>>>>>>>"+obj.toString());
        CxAccountingParser parser=new CxAccountingParser();
        CxAccountDetailMonthList list=null;
		list = parser.getAccountMonths(obj, context,date,true,2);//此处的2无意义，只为凑参数

        return list;

	}
	
	/**
	 * 月度明细
	 * @param date
	 * @param callback
	 */
	public void requestAccountMonthDetails(final Context context,final String date,final int type,final JSONCaller callback){
		if(TextUtils.isEmpty(date)){
			try {
				throw new Exception("date id can not null,month");
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		
		JSONCaller netCallback = new JSONCaller() {
			
			@Override
			public int call(Object result) {
				JSONObject jObj = null;
				try {
					jObj = (JSONObject)result;
				} catch (Exception e) {
				}
				if (null == result) {
					callback.call(null);
					return -1;
				}
				CxLog.i("RkAccountingApi_men", jObj.toString());
				try {										
					CxAccountingParser parser=new CxAccountingParser();
					callback.call(parser.getAccountDays(jObj,context,date,false,type));	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return 0;
			}
		};
		CxLog.i("RkAccountingApi_men", date);
		this.doHttpGet(ACCOUNT_MONTH_DETAIL, netCallback,new BasicNameValuePair("date",date),new BasicNameValuePair("type",type+""));	
	}
	
	/**
	 * 月度明细本地缓存
	 * @param context
	 * @param date: for example:"201212"
	 * @return
	 */
	public CxAccountDetailDayList  requestNativeMonthDetails(final Context context,final String date){
		CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>"+date);
//		List<Model> chargehomedata= new ChargeMonthDetail(null,date, context).gets("1=1", new String[] {}, null, 0, 0); ///取全部数据
		Model chargehomedata= new ChargeMonthDetail(null,date, context).get(date); ///取全部数据
        if(null == chargehomedata){
            return null;
        }
        JSONObject obj=null;
    	ChargeMonthDetail chargehome = (ChargeMonthDetail)(chargehomedata);
        obj = chargehome.mData;
	    // 逻辑处理过程
        CxLog.i("men", ">>>>>>>>>>>>>>>>>>>>"+obj.toString());
        CxAccountingParser parser=new CxAccountingParser();
        CxAccountDetailDayList  list=null;
		list = parser.getAccountDays(obj, context,date,true,2);//此处的2无意义，只为凑参数

        return list;

	}
	

	/**
	 * 
	 * @param date
	 * @param callback
	 */
	public void requestAccountChart(String date,int type,final JSONCaller callback){
        if (TextUtils.isEmpty(date)) {
            try {
                throw new Exception("date can not null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONCaller netCallback = new JSONCaller() {

            @Override
            public int call(Object data) {
                JSONObject result = (JSONObject)data;
                try {
                	CxLog.i("RkAccountingApi_men", result.toString());
                    int rc = result.getInt("rc");
                    if(rc != 0 ){
                        callback.call(result);
                        return 0;
                    }
                    callback.call(result);
                } catch (JSONException e) {
                    CxLog.e("requestNeighborQuery", e.getMessage());
                }
                return 0;
            }
        };

        this.doHttpGet(ACCOUNT_CHAT, netCallback, new BasicNameValuePair("date", date),new BasicNameValuePair("type", type+"")); 
	}
}

