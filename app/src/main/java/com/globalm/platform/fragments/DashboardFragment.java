package com.globalm.platform.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.globalm.platform.R;
import com.globalm.platform.activities.CameraActivity;
import com.globalm.platform.activities.CreateContentActivity;
import com.globalm.platform.adapters.ContentAdapterForApi;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.Content;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout mButtonGoLive;
    private LinearLayout mButtonPlanEvent;
    private LinearLayout mButtonUploadFile;
    private LinearLayout mLayoutMain;
    private RecyclerView mListUpcoming;
    private RecyclerView mListArchived;
    private ContentAdapterForApi mContentAdapterUpcoming;
    private ContentAdapterForApi mContentAdapterArchived;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private ArrayList<Item> mItemsUpcoming = new ArrayList<>();
    private ArrayList<Item> mItemsArchived = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListenerUpcoming;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListenerArchived;
    private Tag tagUpcoming;
    private Tag tagArchived;
    private int pageNumberUpcoming = GlobalmAPI.FIRST_PAGE;
    private int pageNumberArchived = GlobalmAPI.FIRST_PAGE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bindView(rootView);
        setupView();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Content loading....");
        mProgressDialog.show();

        getContentsUpcoming();
        getContentsArchived();
        return rootView;
    }

    @Override
    public void onRefresh() {
        resetDataUpcoming();
        resetDataArchived();
        getContentsUpcoming();
        getContentsArchived();
    }

    private void getContentsUpcoming() {
        CallbackListener<Content> callbackListener = new CallbackListener<Content>() {
            @Override
            public void onSuccess(Content response) {
                mEndlessRecyclerViewScrollListenerUpcoming.setLoading(false);
                mItemsUpcoming.addAll(Objects.requireNonNull(response).getData().getItems());
                mContentAdapterUpcoming.setData(mItemsUpcoming);
                mProgressDialog.dismiss();
                mLayoutMain.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable error) {
                mEndlessRecyclerViewScrollListenerUpcoming.setLoading(false);
            }
        };
        if (tagUpcoming == null) {
            RequestManager.getInstance().getContent(pageNumberUpcoming, callbackListener);
        } else {
            RequestManager.getInstance().getContentForTag(pageNumberUpcoming, tagUpcoming.getId(), callbackListener);
        }
    }

    private void getContentsArchived() {
        CallbackListener<Content> callbackListener = new CallbackListener<Content>() {
            @Override
            public void onSuccess(Content response) {
                mEndlessRecyclerViewScrollListenerArchived.setLoading(false);
                mItemsArchived.addAll(Objects.requireNonNull(response).getData().getItems());
                mContentAdapterArchived.setData(mItemsArchived);
                mProgressDialog.dismiss();
                mLayoutMain.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable error) {
                mEndlessRecyclerViewScrollListenerArchived.setLoading(false);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        if (tagArchived == null) {
            RequestManager.getInstance().getContent(pageNumberArchived, callbackListener);
        } else {
            RequestManager.getInstance().getContentForTag(pageNumberArchived, tagArchived.getId(), callbackListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_go_live:
                mContext.startActivity(new Intent(mContext, CameraActivity.class));
                break;

            case R.id.button_plan_event:
                CreateContentActivity.start(getContext(), CreateContentFragment.POSITION_CREATE_EVENT);
                break;

            case R.id.button_upload_file:
                CreateContentActivity.start(getContext(), CreateContentFragment.POSITION_UPLOAD);
                break;
        }
    }

    private void bindView(View rootView) {
        mButtonGoLive = rootView.findViewById(R.id.button_go_live);
        mButtonPlanEvent = rootView.findViewById(R.id.button_plan_event);
        mButtonUploadFile = rootView.findViewById(R.id.button_upload_file);
        mListUpcoming = rootView.findViewById(R.id.list_upcoming);
        mListArchived = rootView.findViewById(R.id.list_archived);
        mLayoutMain = rootView.findViewById(R.id.layout_main);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupView() {
        mLayoutMain.setVisibility(View.INVISIBLE);
        mButtonGoLive.setOnClickListener(this);
        mButtonPlanEvent.setOnClickListener(this);
        mButtonUploadFile.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mContentAdapterUpcoming = new ContentAdapterForApi(getActivity(), (tag) -> {
            if (DashboardFragment.this.tagUpcoming == null) {
                resetDataUpcoming();
                DashboardFragment.this.tagUpcoming = tag;
            } else {
                if (DashboardFragment.this.tagUpcoming.getId().equals(tag.getId())) {
                    resetDataUpcoming();
                    DashboardFragment.this.tagUpcoming = null;
                } else {
                    resetDataUpcoming();
                    DashboardFragment.this.tagUpcoming = tag;
                }
            }
            getContentsUpcoming();
        }, true);
        mContentAdapterArchived = new ContentAdapterForApi(getActivity(), (tag) -> {
            if (DashboardFragment.this.tagArchived == null) {
                resetDataArchived();
                DashboardFragment.this.tagArchived = tag;
            } else {
                if (DashboardFragment.this.tagArchived.getId().equals(tag.getId())) {
                    resetDataArchived();
                    DashboardFragment.this.tagArchived = null;
                } else {
                    resetDataArchived();
                    DashboardFragment.this.tagArchived = tag;
                }
            }
            getContentsArchived();
        }, true);
        LinearLayoutManager mLinearLayoutManagerUpcoming = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager mLinearLayoutManagerArchived = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mListUpcoming.setLayoutManager(mLinearLayoutManagerUpcoming);
        mListArchived.setLayoutManager(mLinearLayoutManagerArchived);
        mListUpcoming.setAdapter(mContentAdapterUpcoming);
        mListArchived.setAdapter(mContentAdapterArchived);
        mListUpcoming.addOnScrollListener(mEndlessRecyclerViewScrollListenerUpcoming = new EndlessRecyclerViewScrollListener(mLinearLayoutManagerUpcoming) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageNumberUpcoming++;
                getContentsUpcoming();
            }
        });
        mListArchived.addOnScrollListener(mEndlessRecyclerViewScrollListenerArchived = new EndlessRecyclerViewScrollListener(mLinearLayoutManagerArchived) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageNumberArchived++;
                getContentsArchived();
            }
        });
    }

    private void resetDataUpcoming() {
        tagUpcoming = null;
        pageNumberUpcoming = GlobalmAPI.FIRST_PAGE;
        mItemsUpcoming.clear();
    }

    private void resetDataArchived() {
        tagArchived = null;
        pageNumberArchived = GlobalmAPI.FIRST_PAGE;
        mItemsArchived.clear();
    }

}