package com.chuxin.family.neighbour;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chuxin.family.R;
import com.chuxin.family.accounting.CxChangeAccountActivity;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.global.CxGlobalParams;
import com.chuxin.family.models.Model;
import com.chuxin.family.models.Neighbour;
import com.chuxin.family.neighbour.CxNeighbourList.NeighbourListAdapter.NeighbourViewHolder;
import com.chuxin.family.net.ConnectionManager.JSONCaller;
import com.chuxin.family.net.CxNeighbourApi;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.resource.CxResourceString;
import com.chuxin.family.utils.DialogUtil;
import com.chuxin.family.utils.ToastUtil;
import com.chuxin.family.utils.DialogUtil.OnSureClickListener;
import com.chuxin.family.widgets.CxImageView;

public class CxNeighbourManageActivity extends CxRootActivity {

	private NeighbourManageAdapter adapter;

	private int mPostion=-1;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_neighbour_manage);
		
		initTitle();
		init();
		loadLocalData();
	}

	private void init() {
		ListView mList = (ListView) findViewById(R.id.cx_fa_neighbour_manage_list);
		adapter = new NeighbourManageAdapter();
		mList.setAdapter(adapter);
	}

	//初始化标题栏
	private void initTitle() {
		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		Button saveBtn = (Button) findViewById(R.id.cx_fa_activity_title_more);
		TextView titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
		
		titleText.setText(R.string.cx_fa_neighbour_manage_title_text);
		backBtn.setVisibility(View.INVISIBLE);
		saveBtn.setText(getString(R.string.cx_fa_neighbour_manage_title_save));
		saveBtn.setVisibility(View.VISIBLE);
		
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			}
		});
		
	}
	
	public void loadLocalData(){
    	List<Model> neighbours = new Neighbour(null, CxNeighbourManageActivity.this).gets("1=1", new String[] {}, null, 0, 0);
    	if(null == neighbours){
    		adapter.setNeighbourLists(null);
    		return;
    	}
    	newNeighbours = new ArrayList<Neighbour>();
    	Iterator<Model> i = neighbours.iterator();
    	while(i.hasNext()){
    		Neighbour neighbour = (Neighbour)i.next();
    		if(neighbour.getStatus()==2 && !neighbour.getNeighbourId().equals(CxGlobalParams.getInstance().getPairId())){
    			newNeighbours.add(neighbour);
    		}	
    	}

    	adapter.setNeighbourLists(newNeighbours);
    }
	
	
	
	class NeighbourManageAdapter extends BaseAdapter{
    	
		
    	private ArrayList<Neighbour> mNeighbours = null;
    	
		public void setNeighbourLists(final ArrayList<Neighbour> neighbours) {
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

				@Override
				public void run() {
					
					mNeighbours=neighbours;
					adapter.notifyDataSetChanged();
					
				}

			}, 1);
		}

		
		@Override
		public int getCount() {
			if(null == mNeighbours){
				return 0;
			}
			return mNeighbours.size();
		}

		@Override
		public Object getItem(int position) {
			if(null != mNeighbours){
				return mNeighbours.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(CxNeighbourManageActivity.this).inflate(R.layout.cx_fa_activity_neighbour_manage_item, null);
				holder.wifeImageView = (CxImageView) convertView.findViewById(R.id.cx_fa_neighbour_manage_item_wife_avatar);
				holder.husImageView = (CxImageView) convertView.findViewById(R.id.cx_fa_neighbour_manage_item_husband_avatar);
				holder.nameText = (TextView) convertView.findViewById(R.id.cx_fa_neighbour_manage_item_name);
				holder.removeLayout = (LinearLayout) convertView.findViewById(R.id.cx_fa_neighbour_manage_item_remove_layout);
				holder.removeBtn=(Button) convertView.findViewById(R.id.cx_fa_neighbour_manage_item_remove_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			CxImageView wifeImageView=holder.wifeImageView;
			CxImageView husImageView=holder.husImageView;
			TextView nameText=holder.nameText;
			LinearLayout removeLayout=holder.removeLayout;
			Button removeBtn=holder.removeBtn;

			final Neighbour neighbour = (Neighbour)mNeighbours.get(position);
			String nameStr="";
            String version = getString(CxResourceString.getInstance().str_pair);
            if("老公".equals(version)){
            	husImageView.displayImage(imageLoader, 
            			neighbour.getHusbandAvatar(), 
            			CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
     
            
            	wifeImageView.displayImage(imageLoader, 
            			neighbour.getWifeAvatar(), 
            			CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
            	nameStr=neighbour.getWifeName()+"和"+neighbour.getHusbandName()+"一家";
            	nameText.setText(nameStr);
                
            }else{
            	husImageView.displayImage(imageLoader, 
            			neighbour.getWifeAvatar(), 
            			CxResourceDarwable.getInstance().dr_chat_icon_small_oppo, true, 
						CxGlobalParams.getInstance().getSmallImgConner());

            	wifeImageView.displayImage(imageLoader, 
            			neighbour.getHusbandAvatar(), 
            			CxResourceDarwable.getInstance().dr_chat_icon_small_me, true, 
						CxGlobalParams.getInstance().getSmallImgConner());
            	
            	nameStr=neighbour.getWifeName()+"和"+neighbour.getHusbandName()+"一家";
            	nameText.setText(nameStr);
            }

            final int pos=position;
            final String nameStr2=nameStr;
            removeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
					new Handler(getMainLooper()){
						public void handleMessage(Message msg) {
							DialogUtil du = DialogUtil.getInstance();
							du.setOnSureClickListener(new OnSureClickListener() {
								
								@Override
								public void surePress() {
									
									mPostion=pos;
									
									try {
										CxNeighbourApi.getInstance().requestNeighborRemove(neighbour.getNeighbourId(), removeCaller);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
							du.getSimpleDialog(CxNeighbourManageActivity.this, null, "确认与“"+nameStr2+"”解除关系吗？", null, null).show();
						};
					}.sendEmptyMessage(0);
							
				}
			});
            
			
			return convertView;
		}
    }

	class ViewHolder{
		CxImageView wifeImageView;
		CxImageView husImageView;
		TextView nameText;
		Button removeBtn;
		LinearLayout removeLayout;
	}
	
	
	JSONCaller removeCaller=new JSONCaller() {
		
		@Override
		public int call(Object result) {
			if(result==null){
				displayModifyHead("解除亲友失败！",0);
				return -1;
			}
			
			JSONObject  obj=(JSONObject) result;
			
			try {
				if(obj.getInt("rc")!=0){
					displayModifyHead("解除亲友失败！",0);
					return -3;
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			new Handler(getMainLooper()){
				public void handleMessage(Message msg) {
					newNeighbours.remove(mPostion);
					adapter.setNeighbourLists(newNeighbours);
					CxNeighbourParam.getInstance().setNeighbourItems(null);
					if(newNeighbours==null || newNeighbours.size()<=0){
						finish();
						overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
					}
				};
			}.sendEmptyMessage(0);
			displayModifyHead("解除亲友成功！",1);
			return 0;
		}
	};
	private ArrayList<Neighbour> newNeighbours;
	
	
	/**
	 * 
	 * @param info
	 * @param number 0 失败；1 成功；2 不要图。
	 */
	private void displayModifyHead(String info,int number) {
		Message msg = new Message();
		msg.obj = info;
		msg.arg1=number;
		new Handler(CxNeighbourManageActivity.this.getMainLooper()) {
			public void handleMessage(Message msg) {
				if ((null == msg) || (null == msg.obj)) {
					return;
				}
				int id=-1;
				if(msg.arg1==0){
					id= R.drawable.chatbg_update_error;
				}else if(msg.arg1==1){
					id=R.drawable.chatbg_update_success;
				}
				ToastUtil.getSimpleToast(CxNeighbourManageActivity.this, id,
						msg.obj.toString(), 1).show();
			};
		}.sendMessage(msg);
	}
	
	
	
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
