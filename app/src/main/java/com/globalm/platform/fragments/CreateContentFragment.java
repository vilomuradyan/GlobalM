package com.globalm.platform.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.globalm.platform.R;
import com.globalm.platform.adapters.ContentPagerAdapter;
import com.globalm.platform.models.Item;
import com.globalm.platform.view.MapViewPager;

import java.util.ArrayList;
import java.util.List;

public class CreateContentFragment extends Fragment {
    public static final int POSITION_CREATE_EVENT = 0;
    public static final int POSITION_UPLOAD = 1;

    private Context mContext;
    private TabLayout mTabLayoutContent;
    public MapViewPager mViewPagerContent;
    private ContentPagerAdapter mContentPagerAdapter;
    private int positionToOpen = POSITION_CREATE_EVENT;

    public void setPositionToOpen(int positionToOpen) {
        this.positionToOpen = positionToOpen;
        if (mViewPagerContent != null) {
            mViewPagerContent.setCurrentItem(positionToOpen);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_content, container, false);
        mTabLayoutContent = rootView.findViewById(R.id.tab_layout_content);
        mViewPagerContent = rootView.findViewById(R.id.view_pager_content);
        mContentPagerAdapter = new ContentPagerAdapter(
                getActivity().getSupportFragmentManager(),
                mContext,
                createContentPagerFragmentsList());
        mViewPagerContent.setOffscreenPageLimit(2);
        mViewPagerContent.setAdapter(mContentPagerAdapter);
        mTabLayoutContent.setupWithViewPager(mViewPagerContent);
        mViewPagerContent.setCurrentItem(positionToOpen);
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            mViewPagerContent.setCurrentItem(positionToOpen);
        }
    }

    public void setUploadFileUri(Uri uri) {
        Fragment fragment = mContentPagerAdapter.getItem(positionToOpen);
        if (fragment instanceof RootFragment) {
            ((RootFragment)fragment).setSharedFileUri(uri);
        }
    }

    public void setUploadData(Item item){
        Fragment fragment = mContentPagerAdapter.getItem(positionToOpen);
        if (fragment instanceof RootFragment) {
            ((RootFragment)fragment).setUploadData(item);
        }
    }

    private List<Fragment> createContentPagerFragmentsList() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CreateEventFragment());
        fragments.add(new RootFragment());
        return fragments;
    }
}