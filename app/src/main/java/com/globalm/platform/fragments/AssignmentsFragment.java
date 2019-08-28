package com.globalm.platform.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.activities.AssignmentDetailsActivity;
import com.globalm.platform.adapters.AssignmentsAdapter;
import com.globalm.platform.dialogs.RespondDialog;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.PaginationData;
import com.globalm.platform.models.assingments.Assignment;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;
import com.globalm.platform.utils.Utils;

import java.util.List;

public class AssignmentsFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AssignmentsAdapter adapter;
    private int page = 0;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = GlobalmAPI.FIRST_PAGE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_assignments, container, false);
        setupSwipe(rootView);
        setupRecycler(rootView);
        getAssignments();

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (endlessRecyclerViewScrollListener != null) {
            recyclerView.removeOnScrollListener(endlessRecyclerViewScrollListener);
        }
        endlessRecyclerViewScrollListener = null;
        super.onDestroy();
    }

    private void setupSwipe(View rootView) {
        swipeRefreshLayout = findView(rootView, R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clearAllAssignments();
            page = GlobalmAPI.FIRST_PAGE;
            getAssignments();
        });
    }

    private void setupRecycler(View rootView) {
        recyclerView = findView(rootView, R.id.rv_assignment);
        adapter = new AssignmentsAdapter(getOnItemClickListener(), getOnMenuClickListener());

        LinearLayoutManager linearLayoutManager = getRecyclerLayoutManager();
        setupEndlessScroll(linearLayoutManager);
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private Utils.InvokeCallback<Integer> getOnMenuClickListener() {
        return (id) -> {
            if (id != null) {
                RespondDialog.createDialog(getContext(), id).show();
            }
        };
    }

    private void setupEndlessScroll(LinearLayoutManager linearLayoutManager) {
        if (endlessRecyclerViewScrollListener == null) {
            endlessRecyclerViewScrollListener = getEndlessRecyclerScrollListener(linearLayoutManager);
        }
    }

    private Utils.InvokeCallback<Integer> getOnItemClickListener() {
        return (id) -> AssignmentDetailsActivity.start(getContext(), id);
    }

    private EndlessRecyclerViewScrollListener getEndlessRecyclerScrollListener(
            LinearLayoutManager linearLayoutManager) {
        return new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getAssignments();
            }
        };
    }

    private void getAssignments() {
        setLoading(true);
        getRequestManager().getAssignments(page, getAssignmentsCallback());
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }

    private CallbackListener<BaseResponseBody<List<Void>, PaginationData<Assignment>>>
    getAssignmentsCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, PaginationData<Assignment>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, PaginationData<Assignment>> o) {
                adapter.addAssignments(o.getData().getItems());
                setLoading(false);

                if (o.getData().getItems().size() != 0) {
                    page++;
                }
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getMessage(), Toast.LENGTH_LONG);
                setLoading(false);
            }
        };
    }

    private void setLoading(boolean isLoading) {
        swipeRefreshLayout.setRefreshing(isLoading);
        endlessRecyclerViewScrollListener.setLoading(isLoading);
    }

    private LinearLayoutManager getRecyclerLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }
}