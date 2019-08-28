package com.globalm.platform.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalm.platform.R;
import com.globalm.platform.activities.BaseActivity;
import com.globalm.platform.adapters.ContentSummaryPagerAdapter;
import com.globalm.platform.view.MapViewPager;

public class ContentSummaryFragment extends Fragment {

    private Context mContext;
    private TabLayout mTabLayoutContentSummary;
    private MapViewPager mViewPagerContentSummary;
    private ContentSummaryPagerAdapter mContentSummaryPagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content_summary, container, false);
        mTabLayoutContentSummary = rootView.findViewById(R.id.tab_layout_content_summary);
        mViewPagerContentSummary = rootView.findViewById(R.id.view_pager_content_summary);
        mContentSummaryPagerAdapter = new ContentSummaryPagerAdapter(getActivity().getSupportFragmentManager(), mContext);
        mViewPagerContentSummary.setOffscreenPageLimit(2);
        mViewPagerContentSummary.setAdapter(mContentSummaryPagerAdapter);
        mTabLayoutContentSummary.setupWithViewPager(mViewPagerContentSummary);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}