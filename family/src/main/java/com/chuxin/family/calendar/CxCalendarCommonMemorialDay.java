package com.chuxin.family.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.utils.CxLog;
/**
 * 
 * @author wentong.men
 *
 */
public class CxCalendarCommonMemorialDay extends CxRootActivity {

	
	private  String[] commons=null;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cx_fa_activity_calendar_edit_common_memorial_day);
		
		initTitle();
		
		init();
		
	}

	private void init() {
		
		commons=new String[]{"老婆的生日","老公的生日","孩子的生日","老婆的父亲生日","老婆的母亲生日","老公的父亲生日",
				"老公的母亲生日","在一起的日子","结婚纪念日","第一次约会","第一次接吻","第一次爱爱","第一次旅行","领结婚证","拍婚纱照"};
		
		mCommonList = (ListView) findViewById(R.id.cx_fa_calendar_edit_common_lv);
		mCommonList.setAdapter(new dataAdapter());
	}

	
	
	private class dataAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return commons.length;
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
			ViewHolder holder=null;
			if(convertView==null){
				convertView=CxCalendarCommonMemorialDay.this.getLayoutInflater().inflate(R.layout.cx_fa_activity_calendar_edit_common_memorial_day_item, null);
				holder=new ViewHolder();
				holder.mText = (TextView) convertView.findViewById(R.id.cx_fa_calendar_edit_common_item_tv);
				holder.mLayout = (LinearLayout) convertView.findViewById(R.id.cx_fa_calendar_edit_common_item_layout);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			
			TextView mText=holder.mText;
			LinearLayout mLayout=holder.mLayout;			
			mText.setText(""+commons[position]);
			final int pos=position;
			mLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent =new Intent();
					intent.putExtra(CxCalendarParam.CALENDAR_COMMON_MEMORIAL, commons[pos]);
					setResult(Activity.RESULT_OK, intent);
					back();
				}
			});
			return convertView;
		}
		
	}
	
	class ViewHolder{
		TextView mText;
		LinearLayout mLayout;
	}
	
	


	//初始化标题栏
	private void initTitle() {
		Button backBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
		TextView titleText = (TextView) findViewById(R.id.cx_fa_activity_title_info);
		
		titleText.setText(getString(R.string.cx_fa_calendar_edit_common_memotial_day_title_text));
		backBtn.setText(getString(R.string.cx_fa_navi_back));

		backBtn.setOnClickListener(titleListener);
		
	}
	
	
	private OnClickListener titleListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back:
				back();
				break;

			default:
				break;
			}
			
		}
	};
	private ListView mCommonList;
	
	
	
	protected void back() {	
		finish();				
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			back();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	
	
	
	
}
