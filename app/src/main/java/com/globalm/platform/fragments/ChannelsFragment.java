package com.globalm.platform.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalm.platform.R;
import com.globalm.platform.adapters.ChannelsAdapter;
import com.globalm.platform.models.ChannelModel;

import java.util.ArrayList;

public class ChannelsFragment extends Fragment {

    private Context mContext;
    private RecyclerView mListChannels;
    private ArrayList<ChannelModel> mChannelModels = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private ChannelsAdapter mChannelsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_channels, container, false);
        mListChannels = rootView.findViewById(R.id.list_channels);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mChannelsAdapter = new ChannelsAdapter(getActivity());
        mListChannels.setLayoutManager(mLinearLayoutManager);
        mListChannels.setAdapter(mChannelsAdapter);
        initDummyList();
        return rootView;
    }

    private void initDummyList() {
        mChannelModels.add(new ChannelModel("John Williams", 2));
        mChannelModels.add(new ChannelModel("John Williams", 4));
        mChannelModels.add(new ChannelModel("John Williams", 10));
        mChannelModels.add(new ChannelModel("John Williams", 4));
        mChannelModels.add(new ChannelModel("John Williams", 34));
        mChannelModels.add(new ChannelModel("John Williams", 4));
        mChannelsAdapter.setData(mChannelModels);
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