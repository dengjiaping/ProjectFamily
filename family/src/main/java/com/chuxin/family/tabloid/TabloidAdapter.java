package com.chuxin.family.tabloid;

import java.net.URL;
import java.util.List;

import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.net.CxTabloidApi;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.parse.been.data.TabloidCateConfObj;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.chuxin.family.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabloidAdapter extends BaseAdapter{
	private String TAG = "TabloidAdapter";
	
	List<TabloidCateConfObj> confList;
	String notifyTimeOfDay = "上午";									// 提醒在上午还是下午
	Context context;
	public TabloidAdapter(List<TabloidCateConfObj> list, int notifyHour, Context context){
		this.confList 		= list;
		this.context 		= context;
		
		if(notifyHour>12){
			notifyTimeOfDay = "下午";
		}
	}
	
	@Override
	public int getCount() {
		return confList.size() +1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 第一条显示顶部背景图
		if(position==0){
			View view 		= View.inflate(context, R.layout.cx_fa_activity_tabloid_top_bg, null);			
			return view;
		}
		
		View view = View.inflate(context, R.layout.cx_fa_activity_tabloid_row, null);
		
		final TabloidCateConfObj obj  =  confList.get(position-1);				// 第一条是顶部背景图，所以要-1
		
		CxImageView img 		= (CxImageView)view.findViewById(R.id.cx_fa_tabloid_list_row_icon);		// 分类的图标
		TextView title 				= (TextView)view.findViewById(R.id.cx_fa_tabloid_list_row_title);				// 分类的名称
		TextView pushTime	= (TextView)view.findViewById(R.id.cx_fa_tabloid_list_row_push_time);		// 发布时间
		Button btn 					= (Button)view.findViewById(R.id.cx_fa_tabloid_subscribe_btn);				// 预订或退订按钮
		
		// 显示预定或退定按钮
		if(obj.getNotification_status().equals("false")){
			btn.setBackgroundResource(R.drawable.daily_btn_opensub);
		}else{
			btn.setBackgroundResource(R.drawable.daily_btn_unsub);
		}

		try {
			String imgSrc = obj.getImg() + "@2x.png" ;// 此处需要自己拼地址(?以后要根据屏幕大小来选择用哪个尺寸的图)
//			img.setImage(imgSrc, false, 90, RkTabloidActivity.class , "tabloid", context);
			img.displayImage(ImageLoader.getInstance(), imgSrc, 
					R.drawable.cx_fa_wf_icon_small, true, 
					CxGlobalParams.getInstance().getSmallImgConner());
			
			title.setText(obj.getTitle());
			pushTime.setText("推送时间: " + obj.getFormatNotificationWeek() + notifyTimeOfDay);
			
		} catch (Exception e) {
			CxLog.e(TAG, "getView()给界面赋值出错了!"+ e.toString());
		}
		
		
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final Button btn = (Button)v;
				
				final TabloidDao dao = new TabloidDao(context);
				
				// 如果没有预订，则直接预订。
				if(obj.getNotification_status().equals("false")){
					btn.setBackgroundResource(R.drawable.daily_btn_unsub);
					obj.setNotification_status("true");
					
					// 修改DB
					dao.updateNotificationStatus(obj.getCategory_id(), "true");
					
					// 更新服务端数据
					final String category_ids = String.valueOf( obj.getCategory_id() );
					Handler handler = new Handler(){   
				        public void handleMessage(Message msg) {  
				            CxTabloidApi tabloidApi = new CxTabloidApi();
				            tabloidApi.updateCategoryStatus(updateCategoryStatusCaller, category_ids, "1");
				        }  
				    };
				    handler.sendEmptyMessage(0);	
					
					return;
				}				
				
				// 确定对话中的三个字符资源 (标题，内容，取消)
				String unsubConfirm = context.getResources().getString(R.string.cx_fa_tabloid_unsub_confirm);
				String unsubTipInfo 	= context.getResources().getString(R.string.cx_fa_tabloid_unsub_tip_info);
				String unsubCancel = context.getResources().getString(R.string.cx_fa_cancel_button_text);
				
				// 如果是取消预定，则需要让用户来确认选择
				TextView textView = new TextView(context);
				textView.setText(unsubTipInfo);
				textView.setTextSize(15f);
				textView.setPadding(20, 10, 20, 10);
				
				new AlertDialog.Builder(context)
				.setView(textView)					// 设置对话框显示的View对象
				.setPositiveButton(unsubConfirm , new DialogInterface.OnClickListener(){		// 为对话框设置一个“确定”按钮
					@Override
					public void onClick(DialogInterface dialog, int which){
						btn.setBackgroundResource(R.drawable.daily_btn_opensub);
						obj.setNotification_status("false");
						
						// 修改DB
						dao.updateNotificationStatus(obj.getCategory_id(), "false");
						
						// 更新服务端数据
						final String category_ids = String.valueOf( obj.getCategory_id() );
						Handler handler = new Handler(){   
					        public void handleMessage(Message msg) {  
					            CxTabloidApi tabloidApi = new CxTabloidApi();
					            tabloidApi.updateCategoryStatus(updateCategoryStatusCaller, category_ids, "0");
					        }  
					    };
					    handler.sendEmptyMessage(0);	
					    
					}
				})
				.setNegativeButton(unsubCancel, null	)	// 为对话框设置一个“取消”按钮
				.create().show();		// 创建、并显示对话框
				
			}
			
		});
		return view;
	}
	
	
	// 更新分类订阅状态后的回调(暂时什么也不做)
	public JSONCaller updateCategoryStatusCaller = new JSONCaller(){
		@Override
		public int call(Object result) {
			return 0;
		}
		
	};

}
