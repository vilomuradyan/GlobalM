package com.globalm.platform.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.adapters.ContentAdapterForApi;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.Content;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;
import com.globalm.platform.utils.SendStreamModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class  ListViewFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private int pageNumber = GlobalmAPI.FIRST_PAGE;

    private RecyclerView mListResults;
    private EditText mFieldSearch;
    private ImageView mButtonFilterResults;
    private Context mContext;
    private GridLayoutManager mGridLayoutManager;
    private ContentAdapterForApi mContentAdapterForApi;
    private ArrayList<Item> mStreamList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExpandableRelativeLayout mExpandableRelativeLayout;
    private ImageView imageViewClearSearchText;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private Tag tag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = GlobalmAPI.FIRST_PAGE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        bindView(rootView);
        setupView();
        initAdapter();
        getContents();
        return rootView;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SendStreamModel event) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
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
    public void onClick(View v) {
        if (v.getId() == R.id.button_filter_results) {
            if (mExpandableRelativeLayout.isExpanded()) {
                mExpandableRelativeLayout.collapse();
            } else {
                mExpandableRelativeLayout.expand();
            }
        }
    }

    @Override
    public void onRefresh() {
        resetData();
        getContents();
    }

    private void getContents() {
        CallbackListener<Content> callbackListener = new CallbackListener<Content>() {
            @Override
            public void onSuccess(Content response) {
                mEndlessRecyclerViewScrollListener.setLoading(false);
                if (response != null) {
                    mStreamList.addAll(response.getData().getItems());
                    mContentAdapterForApi.setData(mStreamList);
                }
                hideSwipeProgress();
            }

            @Override
            public void onFailure(Throwable error) {
                mEndlessRecyclerViewScrollListener.setLoading(false);
                showMessage(error.getMessage(), Toast.LENGTH_LONG);
                hideSwipeProgress();
            }
        };
        if (tag == null) {
            String query = mFieldSearch.getText().toString();
            if (TextUtils.isEmpty(query)) {
                RequestManager.getInstance().getContent(pageNumber, callbackListener);
            } else {
                RequestManager.getInstance().getContent(pageNumber, query, callbackListener);
            }
        } else {
            RequestManager.getInstance().getContentForTag(pageNumber, tag.getId(), callbackListener);
        }
    }

    private void initAdapter() {
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        mListResults.setLayoutManager(mGridLayoutManager);
        mContentAdapterForApi = new ContentAdapterForApi(getActivity(), (tag) -> {
            resetData();
            ListViewFragment.this.tag = tag;
            mFieldSearch.setText("#" + tag.getName());
            getContents();
        }, false);
        mListResults.setAdapter(mContentAdapterForApi);
        mListResults.addOnScrollListener(mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageNumber++;
                getContents();
            }
        });
    }

    private void hideSwipeProgress() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void bindView(View rootView) {
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFieldSearch = rootView.findViewById(R.id.field_search);
        mListResults = rootView.findViewById(R.id.list_results);
        mButtonFilterResults = rootView.findViewById(R.id.button_filter_results);
        mExpandableRelativeLayout = rootView.findViewById(R.id.layout_expandable_contact_search);
        mButtonFilterResults.setOnClickListener(this);
        imageViewClearSearchText = rootView.findViewById(R.id.iv_clear_search_text);
    }

    private void setupView() {
        mFieldSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    resetData();
                    getContents();
                    imageViewClearSearchText.setVisibility(View.INVISIBLE);
                } else {
                    imageViewClearSearchText.setVisibility(View.VISIBLE);
                }
            }
        });
        mFieldSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard(mFieldSearch);
                resetData();
                getContents();
                return true;
            }
            return false;
        });
        imageViewClearSearchText.setOnClickListener((v) -> {
            mFieldSearch.setText("");
            closeKeyboard(mFieldSearch);
        });
        imageViewClearSearchText.setVisibility(mFieldSearch.getText().length() == 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private void resetData() {
        tag = null;
        pageNumber = GlobalmAPI.FIRST_PAGE;
        mStreamList.clear();
    }

}