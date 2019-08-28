package com.globalm.platform.fragments;

import android.annotation.SuppressLint;
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
import com.globalm.platform.adapters.ContentHistoryAdapter;
import com.globalm.platform.models.ContentHistoryModel;

import java.util.ArrayList;
import java.util.List;

public class ContentHistoryFragment extends Fragment {

    private Context mContext;
    private RecyclerView mListHistory;
    private List<ContentHistoryModel> mHistoryModel = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private ContentHistoryAdapter historyadapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content_history, container, false);
        mListHistory = rootView.findViewById(R.id.list_content_history);
        layoutManager = new LinearLayoutManager(getActivity());
        mListHistory.setLayoutManager(layoutManager);
        mListHistory.setHasFixedSize(true);
        initHistory();
        return rootView;
    }

    public void initHistory(){
        mHistoryModel.add(new ContentHistoryModel("Oliver Jones booked the content","Exclusive 200$", "2 days ago"));
        mHistoryModel.add(new ContentHistoryModel( "Oliver Jones booked the content","Exclusive 200$", "2 days ago"));
        historyadapter = new ContentHistoryAdapter(getActivity(),mHistoryModel);
        mListHistory.setAdapter(historyadapter);

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