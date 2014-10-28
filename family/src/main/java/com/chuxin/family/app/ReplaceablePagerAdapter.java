package com.chuxin.family.app;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.chuxin.family.R;

/**
 * @author shichao.wang
 */
public class ReplaceablePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "ReplaceablePagerAdapter";
    private List<FragmentableView> mContentFragments = new ArrayList<FragmentableView>();

    public ReplaceablePagerAdapter(FragmentManager fm) {
        super(fm);

        List<Integer> defaultPages = new ArrayList<Integer>();
        defaultPages.add(R.layout.cx_fa_activity_blank);
        addPages(defaultPages);
    }

    private void addPages(List<Integer> viewResIds) {
        for (int i = 0; i < viewResIds.size(); i++) {
            mContentFragments.add(FragmentableView.newInstance(viewResIds.get(i)));
        }
    }

    private void addPages(int[] viewResIds) {
        for (int i = 0; i < viewResIds.length; i++) {
            mContentFragments.add(FragmentableView.newInstance(viewResIds[i]));
        }
    }

    public void fillPages(int[] viewResIds) {
        mContentFragments.clear();
        addPages(viewResIds);

        notifyDataSetChanged();
    }

    public void fillPages(List<Integer> viewResIds) {
        mContentFragments.clear();
        addPages(viewResIds);

        notifyDataSetChanged();
    }

    public void removePagesAfter(int position) {

        int maxFragmentIndex = mContentFragments.size() - 1;
        if (maxFragmentIndex > position) {
            for (int i = position + 1; i <= maxFragmentIndex; i++) {
                mContentFragments.remove(position + 1);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mContentFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem() " + position);
        return mContentFragments.get(position);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
