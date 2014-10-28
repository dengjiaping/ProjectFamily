package com.chuxin.family.libs.gpuimage;

import java.util.LinkedList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuxin.family.libs.gpuimage.GPUImageFilterTools.FilterAdjuster;
import com.chuxin.family.libs.gpuimage.GPUImageFilterTools.OnGpuImageFilterChosenListener;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.R;

/**
 * 添加滤镜adapter
 * 
 * @author wangshichao 2013-03-15
 * 
 */
public class GPUImageFilterAdapter extends BaseAdapter {

	private static final String TAG = "GPUIMAGEFILTERADAPTER";
	private int mPosition;
	private ImageButton[] tempImageButtons;
	private GPUImageFilter mFilter;
	private Context mContext;
	private GPUImage mGpuImage;
	private static LinearLayout sLinearLayout;
	private static String[] sImgDrawableNames = new String[] {
			"filter_image_original",//原图
			"filter_image_amaro", //怀旧
			"filter_image_nashville",//乡村
			"filter_image_sierra",//梦想
			"filter_image_walden", //日光
			"filter_image_xproii",//清明
			//"filter_image_miss_etikate",//蓝光
			"filter_image_sepiatone",//金黄
			//"filter_image_soft_elegance",//青緑
			"filter_image_grayscale", //灰度
			//"filter_image_vignette",// 黑光
			"filter_image_sharpen", //杂质
			//"filter_image_opening", //水粉
			
	};// 滤镜分类图片资源名称
    private FilterAdjuster mFilterAdjuster;
	public GPUImageFilterAdapter(Context context, GPUImage gpuImage) {
		mContext = context;
		mGpuImage = gpuImage;
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_original),
				GPUImageFilterTools.FilterTypes.ORIGINAL);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_amaro),
				GPUImageFilterTools.FilterTypes.AMARO);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_nashville),
				GPUImageFilterTools.FilterTypes.NASHVILLE);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_sierra),
				GPUImageFilterTools.FilterTypes.SIERRA);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_walden),
				GPUImageFilterTools.FilterTypes.WALDEN);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_xproii),
				GPUImageFilterTools.FilterTypes.XPROII);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_sepia), 
				GPUImageFilterTools.FilterTypes.SEPIA);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_grayscale), 
				GPUImageFilterTools.FilterTypes.GRAYSCALE);
//		filters.addFilter(mContext.getResources().getString(R.string.filter_name_vignette),
//				GPUImageFilterTools.FilterTypes.VIGNETTE);
		filters.addFilter(mContext.getResources().getString(R.string.filter_name_sharpness),
				GPUImageFilterTools.FilterTypes.SHARPEN);
		tempImageButtons = new ImageButton[filters.names.size()];
	}

	final FilterList filters = new FilterList();

	private static class FilterList {
		public List<String> names = new LinkedList<String>();
		public List<GPUImageFilterTools.FilterTypes> filters = new LinkedList<GPUImageFilterTools.FilterTypes>();

		public void addFilter(final String name,
				final GPUImageFilterTools.FilterTypes filter) {
			names.add(name);
			filters.add(filter);
		}
	}

	private OnClickListener mOnButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final OnGpuImageFilterChosenListener listener = new OnGpuImageFilterChosenListener() {

				@Override
				public void onGpuImageFilterChosenListener(GPUImageFilter filter) {
					switchFilterTo(filter);
				}

			};
			//sLinearLayout.getBackground().setAlpha(255);
			Log.d(TAG, "onGpuImageFilterChosenListener_mPosition = "
					+ v.getTag().toString() + "filters.filters.get(mPosition)="
					+ filters.filters.get(Integer
							.parseInt(v.getTag().toString())));
			listener.onGpuImageFilterChosenListener(GPUImageFilterTools
					.createFilterForType(mContext, filters.filters.get(Integer
							.parseInt(v.getTag().toString()))));
		}
	};

	@Override
	public int getCount() {
		return filters.names.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View retval = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.cx_fa_libgpu_view_item, null);
		mPosition = position;
		TextView title = (TextView) retval.findViewById(R.id.title);
		ImageButton button = (ImageButton) retval
				.findViewById(R.id.imagebutton);
		ImageButton bgImageButton = (ImageButton) retval.findViewById(R.id.bgButton);
		//LinearLayout linearLayout = (LinearLayout) retval.findViewById(R.id.viewItemLinearLayout);
		//linearLayout.getBackground().setAlpha(0);
		//sLinearLayout = linearLayout;
		final ImageButton tempFilterImageButton = button;
//		final ImageButton[] tempBgImageButton = bgImageButton;
		tempImageButtons[position] = bgImageButton;
		int resId = mContext.getResources().getIdentifier(
				sImgDrawableNames[position], "drawable",
				mContext.getPackageName());
		//button.setImageResource(resID);

		//button.setOnClickListener(mOnButtonClicked);
		tempFilterImageButton.setImageResource(resId);
		Log.v(TAG, "tempBgImageButton = "+ mPosition + ":" + tempImageButtons[mPosition]);
//		tempBgImageButton.setVisibility(View.INVISIBLE);
		tempImageButtons[mPosition].setVisibility(View.INVISIBLE);
		tempFilterImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final OnGpuImageFilterChosenListener listener = new OnGpuImageFilterChosenListener() {

					@Override
					public void onGpuImageFilterChosenListener(GPUImageFilter filter) {
						switchFilterTo(filter);
					}

				};
				//sLinearLayout.getBackground().setAlpha(255);
				Log.d(TAG, "onGpuImageFilterChosenListener_mPosition = "
						+ v.getTag().toString() + "filters.filters.get(mPosition)="
						+ filters.filters.get(Integer
								.parseInt(v.getTag().toString())));
				listener.onGpuImageFilterChosenListener(GPUImageFilterTools
						.createFilterForType(mContext, filters.filters.get(Integer
								.parseInt(v.getTag().toString()))));
				//tempBgImageButton.setVisibility(View.VISIBLE);
				for(int i=0; i<tempImageButtons.length; i++){
					if(null != tempImageButtons[i]){					
						if(Integer.parseInt(v.getTag().toString()) == i){
							tempImageButtons[i].setVisibility(View.VISIBLE);
						} else {
								tempImageButtons[i].setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		});
		button.setTag(position);
		title.setText(filters.names.get(position));

		return retval;
	}

	private void switchFilterTo(final GPUImageFilter filter) {
	    CxLog.v("switchFilterTo", "filter>>>>" + filter);
	    CxLog.v("switchFilterTo", "mFilter>>>>" + mFilter);
		if ((mFilter == null && filter != null)
				|| (filter != null && !mFilter.getClass().equals(
						filter.getClass())) || (filter != null && mFilter.getClass().equals(
								filter.getClass()) && filter.mChangeFilter)) {
			mFilter = filter;
			CxLog.v("switchFilterTo", "mFilter>>>>" + mFilter);
			mGpuImage.setFilter(mFilter);
			 mFilterAdjuster = new FilterAdjuster(mFilter);
	            if (mFilterAdjuster != null) {
	                mFilterAdjuster.adjust(100);
	            }
		}
	}
}
