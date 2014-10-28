package com.chuxin.family.settings;

import java.util.ArrayList;

import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.main.CxSingleGuiderPager;
import com.chuxin.family.resource.CxResourceDarwable;
import com.chuxin.family.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 应用首界面和帮助界面，注意界面的复用
 * @author shichao.wang
 *
 */
public class CxHelpSuggest extends CxRootActivity {
	
	private ViewPager mFlyView;
	private ImageView mIndicator1, mIndicator2, mIndicator3, mIndicator4,mIndicator5,mIndicator6;
	private Button mBackImageBtn, mMoreImageBtn; //“返回”与“反馈意见”按钮
	private TextView mTitleInfo;
//	private View titleLayer; //整个标题部分（包含"退出", "标题信息","功能按钮"
	ArrayList<ImageView> mIndicators = new ArrayList<ImageView>();
//	private boolean mIsIndex = true; //默认是登录界面
	
	public static boolean isInitialed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_fa_activity_help);

//		titleLayer = findViewById(R.id.title_for_index_or_suggest);
		mBackImageBtn = (Button)findViewById(R.id.cx_fa_activity_title_back);
		mMoreImageBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
		mBackImageBtn.setOnClickListener(mButtonListener);
		//mMoreImageBtn.setOnClickListener(mButtonListener);
		
		
		mBackImageBtn.setText(getString(R.string.cx_fa_navi_back));
		mMoreImageBtn.setText(getString(R.string.cx_fa_navi_suggest));		
		
		mTitleInfo = (TextView)findViewById(R.id.cx_fa_activity_title_info);
		
		mMoreImageBtn.setVisibility(View.INVISIBLE);
		mTitleInfo.setText(getString(R.string.cx_fa_to_help));
		
		mIndicator1 = (ImageView)findViewById(R.id.first_help_indicator2);
		mIndicator2 = (ImageView)findViewById(R.id.second_help_indicator2);
		mIndicator3 = (ImageView)findViewById(R.id.third_help_indicator2);
		mIndicator4 = (ImageView)findViewById(R.id.forth_help_indicator2);
		mIndicator5 = (ImageView)findViewById(R.id.fifth_help_indicator2);
		mIndicator6 = (ImageView)findViewById(R.id.sixth_help_indicator2);
		mIndicator1.setImageResource(R.drawable.cx_fa_main_guide_dot_focused);
		mIndicators.add(mIndicator1);
		mIndicators.add(mIndicator2);
		mIndicators.add(mIndicator3);
		mIndicators.add(mIndicator4);
		mIndicators.add(mIndicator5);
		mIndicators.add(mIndicator6);
		
		mFlyView = (ViewPager)findViewById(R.id.help_image_viewpager);
		mFlyView.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		mFlyView.setOnPageChangeListener(mPageChange);
		
		initView();
	}
	
	private void initView(){
	}
	
	OnClickListener mButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cx_fa_activity_title_back: //返回
				CxHelpSuggest.this.finish();
				break;
			case R.id.cx_fa_activity_title_more: //意见反馈
				Intent toSuggest = new Intent(CxHelpSuggest.this, CxUserSuggest.class);
				startActivity(toSuggest);
				break;
			default:
				break;
			}
			
		}
	};
	
	OnPageChangeListener mPageChange = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			setSelectedFocus(arg0);
			
		}
		
		private void setSelectedFocus(int position){
			for(int i = 0; i < mIndicators.size(); i++){
				if (i == position) {
					mIndicators.get(i).setImageResource(R.drawable.cx_fa_main_guide_dot_focused);
					continue;
				}
				mIndicators.get(i).setImageResource(R.drawable.cx_fa_main_guide_dot_normal);
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	};

	@Override
	protected void onDestroy() {
		isInitialed = false;
		super.onDestroy();
	}
	
	class PagerAdapter extends FragmentStatePagerAdapter{

		
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			arg0 = arg0 % 6;
			switch (arg0) {
			case 0:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction1, false);
			case 1:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction2, false);
			case 2:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction3, false);
			case 3:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction4, false);
			case 4:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction5, false);
			case 5:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction6, false);
			default:
				return CxSingleGuiderPager.getInstance(R.drawable.cx_fa_role_login_introduction6, false);
			}
			
		}

		@Override
		public int getCount() {
			return 6;
		}
		
	}
	
}
