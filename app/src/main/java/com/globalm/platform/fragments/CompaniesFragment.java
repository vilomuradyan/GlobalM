package com.globalm.platform.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.OrganizationsAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.GetContentListModel;
import com.globalm.platform.models.Organization;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class CompaniesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private int pageNumber = GlobalmAPI.FIRST_PAGE;
    private EditText mFieldSearchContacts;
    private RecyclerView mListCompanies;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextCompaniesNothingFound;
    private ImageView mButtonClearSearch;
    private OrganizationsAdapter mOrganizationsAdapter;
    private ImageView mButtonFilterContacts;

    private ArrayList<Organization> mOrganizations = new ArrayList<>();
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_companies, container, false);
        bindViews(rootView);
        setupView();
        getOrganizations();
        return rootView;
    }

    @Override
    public void onRefresh() {
        resetData();
        getOrganizations();
    }

    private void getOrganizations() {
        CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>>> callbackListener = new CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>> o) {
                if (!o.getError()) {
                    List<Organization> organizations = o.getData().getItems();
                    if (organizations != null && organizations.size() > 0) {
                        mOrganizations.addAll(organizations);
                    }
                }
                mOrganizationsAdapter.setData(mOrganizations);
                mSwipeRefreshLayout.setRefreshing(false);
                mEndlessRecyclerViewScrollListener.setLoading(false);
                mTextCompaniesNothingFound.setVisibility(mOrganizations.size() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable error) {
                mSwipeRefreshLayout.setRefreshing(false);
                mEndlessRecyclerViewScrollListener.setLoading(false);
            }
        };
        String q = mFieldSearchContacts.getText().toString();
        if (TextUtils.isEmpty(q)) {
            RequestManager.getInstance().getOrganizations(pageNumber, callbackListener);
        } else {
            RequestManager.getInstance().getOrganizations(pageNumber, q, callbackListener);
        }
    }

    private void resetData() {
        pageNumber = GlobalmAPI.FIRST_PAGE;
        mOrganizations.clear();
    }

    private void bindViews(View rootView) {
        mFieldSearchContacts = rootView.findViewById(R.id.field_search);
        mButtonClearSearch = rootView.findViewById(R.id.iv_clear_search_text);
        mListCompanies = rootView.findViewById(R.id.list_companies);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        mButtonFilterContacts = rootView.findViewById(R.id.button_filter_results);
        mTextCompaniesNothingFound = rootView.findViewById(R.id.companies_tv_nothing_found);
    }

    private void setupView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mOrganizationsAdapter = new OrganizationsAdapter();
        mListCompanies.setLayoutManager(mLinearLayoutManager);
        mListCompanies.setAdapter(mOrganizationsAdapter);
        mListCompanies.addOnScrollListener(mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageNumber++;
                getOrganizations();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFieldSearchContacts.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard(mFieldSearchContacts);
                resetData();
                getOrganizations();
                return true;
            }
            return false;
        });

        mFieldSearchContacts.addTextChangedListener(new TextWatcher() {
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
                    getOrganizations();
                    mButtonClearSearch.setVisibility(View.INVISIBLE);
                } else {
                    mButtonClearSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        mButtonClearSearch.setOnClickListener((v) -> {
            mFieldSearchContacts.setText("");
            closeKeyboard(mFieldSearchContacts);
        });
        mButtonClearSearch.setVisibility(mFieldSearchContacts.getText().length() == 0 ? View.INVISIBLE : View.VISIBLE);
        mButtonFilterContacts.setVisibility(View.GONE);
        mTextCompaniesNothingFound.setVisibility(mOrganizations.size() > 0 ? View.GONE : View.VISIBLE);
    }

}
