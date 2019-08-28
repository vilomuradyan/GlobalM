package com.globalm.platform.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.adapters.ContactsAdapter;
import com.globalm.platform.adapters.RequestsAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.Contact;
import com.globalm.platform.models.GetContentListModel;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, ContactsAdapter.ContactActionListener {
    public static boolean SHOULD_REFRESH_REQUESTS_DATA = false;
    public static boolean SHOULD_REFRESH_PENDING_DATA = false;
    private int pageNumber = GlobalmAPI.FIRST_PAGE;
    private RecyclerView mListRequests;
    private TextView mTextRequestNotification;
    private TextView mTextRequestNothingFound;
    private RequestsAdapter mRequestsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Contact> mContactList = new ArrayList<>();
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    private Type type;

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setUserVisibleHint(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);
        bindViews(rootView);
        setupView();
        getData();
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && shouldRefreshOnStart()) {
            resetData();
            getData();
        }
    }

    @Override
    public void onRefresh() {
        resetData();
        getData();
    }

    //region ContactsAdapter.ContactActionListener
    @Override
    public void onRemove(Contact contact) {

    }

    @Override
    public void onDecline(Contact contact) {
        RequestManager.getInstance().rejectContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                resetData();
                getData();
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onSendContactRequest(Contact contact) {
        //implement if needed
    }

    @Override
    public void onCancelContactRequest(Contact contact) {
        RequestManager.getInstance().cancelContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                resetData();
                getData();
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onAccept(Contact contact) {
        RequestManager.getInstance().acceptContactRequest(contact.getId(), new CallbackListener<BaseResponseBody<List<Void>, Object[]>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, Object[]> o) {
                resetData();
                getData();
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(R.string.an_error_has_occured);
            }
        });
    }

    @Override
    public void onSendMessage(Contact contact) {
        startActivity(new Intent(getContext(), MessageActivity.class));
    }

    @Override
    public void onOpen(Contact contact) {

    }
    //endregion

    private void getData() {
        CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>> callbackListener = new CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>> o) {
                if (!o.getError()) {
                    List<Contact> contacts = o.getData().getItems();
                    if (contacts != null && contacts.size() > 0) {
                        mContactList.addAll(contacts);
                    }
                }
                mRequestsAdapter.setData(mContactList);
                mSwipeRefreshLayout.setRefreshing(false);
                mEndlessRecyclerViewScrollListener.setLoading(false);
                resetShouldRefreshState();
                mTextRequestNothingFound.setVisibility(mContactList.size() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onFailure(Throwable error) {
                mSwipeRefreshLayout.setRefreshing(false);
                mEndlessRecyclerViewScrollListener.setLoading(false);
                showMessage(R.string.an_error_has_occured);
                resetShouldRefreshState();
            }
        };
        if (type == Type.REQUESTS) {
            RequestManager.getInstance().getContactRequests(pageNumber, callbackListener);
        } else if (type == Type.PENDING) {
            RequestManager.getInstance().getContactRequestsPending(pageNumber, callbackListener);
        }
    }

    private boolean shouldRefreshOnStart() {
        return (type == Type.PENDING && SHOULD_REFRESH_PENDING_DATA) || (type == Type.REQUESTS && SHOULD_REFRESH_REQUESTS_DATA);
    }

    private void resetShouldRefreshState() {
        if (type == Type.REQUESTS) {
            SHOULD_REFRESH_REQUESTS_DATA = false;
        } else if (type == Type.PENDING) {
            SHOULD_REFRESH_PENDING_DATA = false;
        }
    }

    private void resetData() {
        pageNumber = GlobalmAPI.FIRST_PAGE;
        mContactList.clear();
    }

    private void bindViews(View rootView) {
        mListRequests = rootView.findViewById(R.id.list_requests);
        mTextRequestNotification = rootView.findViewById(R.id.text_request_notification);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        mTextRequestNothingFound = rootView.findViewById(R.id.requests_tv_nothing_found);
    }

    private void setupView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRequestsAdapter = new RequestsAdapter(this, type);
        mListRequests.setLayoutManager(mLinearLayoutManager);
        mListRequests.setAdapter(mRequestsAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mListRequests.addOnScrollListener(mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageNumber++;
                getData();
            }
        });
        mTextRequestNothingFound.setVisibility(mContactList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public enum Type {
        REQUESTS, PENDING
    }

}