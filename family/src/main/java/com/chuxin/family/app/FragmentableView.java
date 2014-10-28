package com.chuxin.family.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentableView extends Fragment {
    private int mViewResId;
    private static final String TAG = "FragmentableView";

    public FragmentableView() {
        super();
    }

    public static FragmentableView newInstance(int viewResId) {
        FragmentableView fragmentableView = new FragmentableView();
        fragmentableView.mViewResId = viewResId;
        return fragmentableView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView with mViewResId=" + mViewResId);
        return inflater.inflate(mViewResId, null);
    }
}
