/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.chuxin.family.zone;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chuxin.family.R;
import com.chuxin.family.app.CxRootActivity;
import com.chuxin.family.global.CxGlobalConst;
import com.chuxin.family.utils.CxLog;
import com.chuxin.family.widgets.CxZoomImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

/**
 * 添加feed图片时候，点击查看大图片
 * @author shichao.wang
 * 
 */
public class CxAddFeedImagePager extends CxRootActivity {

    @Override
	public void overridePendingTransition(int enterAnim, int exitAnim) {
    	super.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	private static final String STATE_POSITION = "STATE_POSITION";

    DisplayImageOptions options;

    ViewPager pager;

    private TextView mTitle;

    private Button mSaveBtn;

    private int mPosition = -1; // 选中的序数

    private ImagePagerAdapter mImagePagerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_fa_activity_image_detail);
        try {
            mPosition = this.getIntent().getIntExtra(CxGlobalConst.S_ZONE_SELECTED_ORDER, -1);
        } catch (Exception e) {
            CxLog.e("", "" + e.getMessage());
        }

        mTitle = (TextView)findViewById(R.id.cx_fa_activity_title_info);
        Button mDisposeBtn = (Button) findViewById(R.id.cx_fa_activity_title_back);
        mDisposeBtn.setText(getString(R.string.cx_fa_navi_back));

        mSaveBtn = (Button)findViewById(R.id.cx_fa_activity_title_more);
        mSaveBtn.setText(getString(R.string.cx_fa_delete_text));
        mSaveBtn.setVisibility(View.VISIBLE);
        mSaveBtn.setOnClickListener(mBtnListener);
        mDisposeBtn.setOnClickListener(mBtnListener);

        pager = (ViewPager)findViewById(R.id.zone_image_viewpager);

        if (-1 == mPosition) {
            mPosition = 0;
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.chatview_imageloading)
                .showImageOnFail(R.drawable.chatview_imageloading).resetViewBeforeLoading(true)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(300))
                .build();
        mImagePagerAdapter = new ImagePagerAdapter();
        pager.setAdapter(mImagePagerAdapter);
        pager.setCurrentItem(mPosition);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mPosition = arg0;
                mTitle.setText((mPosition + 1) + "/"
                        + CxZoneParam.getInstance().getAddPhotosPath().size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        if ((null != CxZoneParam.getInstance().getAddPhotosPath())
                && (mPosition < CxZoneParam.getInstance().getAddPhotosPath().size())) {
            mTitle.setText((mPosition + 1) + "/"
                    + CxZoneParam.getInstance().getAddPhotosPath().size());
            pager.setCurrentItem(mPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private List<String> mImagePathArrString;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
            mImagePathArrString = CxZoneParam.getInstance().getAddPhotosPath();
        }

        public void updateSelf(int position) {
            mImagePathArrString = CxZoneParam.getInstance().getAddPhotosPath();
            ImagePagerAdapter.this.notifyDataSetChanged();
            CxLog.i("^^^^", "adapter update self====" + position);
            if ((mPosition < mImagePathArrString.size()) && (mPosition >= 0)) {
                pager.setCurrentItem(mPosition);
            } else {
                if (((mPosition - 1) < mImagePathArrString.size()) && ((mPosition - 1) >= 0)) {
                    pager.setCurrentItem(mPosition - 1);
                }
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public int getCount() {
            if (null == mImagePathArrString) {
                return 0;
            }
            return mImagePathArrString.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            CxZoomImage imageView = (CxZoomImage)imageLayout.findViewById(R.id.image);

            CxLog.i("RkImagePager", mImagePathArrString.get(position));
            imageLoader.displayImage("file://" + mImagePathArrString.get(position).replace("file://", ""), imageView);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
        super.onPause();
    }

    OnClickListener mBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cx_fa_activity_title_back:
//                    CxAddFeedImagePager.this.finish();
                    break;
                case R.id.cx_fa_activity_title_more: // 保存照片 or 删除图片
                    mSaveBtn.setClickable(false);
                    CxZoneParam.getInstance().removeSpecialImage(mPosition);
                    // mImageViewAdapter.
                    // 修正mPosition
                    if (mPosition >= CxZoneParam.getInstance().getAddPhotosPath().size()) {
                        mPosition = CxZoneParam.getInstance().getAddPhotosPath().size() - 1;
                    }
                    if ((null == CxZoneParam.getInstance().getAddPhotosPath()) // 无图片应该关闭此界面
                            || (CxZoneParam.getInstance().getAddPhotosPath().size() < 1)) {
//                        CxAddFeedImagePager.this.finish();
                        return;
                    }
                    mImagePagerAdapter.updateSelf(mPosition);
                    mTitle.setText((mPosition + 1) + "/"
                            + CxZoneParam.getInstance().getAddPhotosPath().size());
                    break;

                default:
                    break;
            }

        }
    };

}
