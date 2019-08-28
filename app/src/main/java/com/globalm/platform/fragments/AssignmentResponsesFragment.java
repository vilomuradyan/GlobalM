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
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.activities.AssignmentDetailsActivity;
import com.globalm.platform.adapters.AssignmentResponseAdapter;
import com.globalm.platform.dialogs.RespondDialog;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.PaginationData;
import com.globalm.platform.models.assingments.Response;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.EndlessRecyclerViewScrollListener;
import com.globalm.platform.utils.Utils;

import java.util.List;

public class AssignmentResponsesFragment extends BaseFragment {

    public static AssignmentResponsesFragment newInstance(int assignmentId) {
        Bundle bundle = new Bundle();
        bundle.putInt(AssignmentDetailsActivity.ASSIGNMENT_ID_KEY, assignmentId);
        AssignmentResponsesFragment fragment = new AssignmentResponsesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noDataTv;

    private AssignmentResponseAdapter adapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private int page = GlobalmAPI.FIRST_PAGE;
    private int assignmentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_assignment_responses, container, false);
        recyclerView = findView(root, R.id.responses_recycler);
        swipeRefreshLayout = findView(root, R.id.swipe);
        noDataTv = findView(root, R.id.no_data_tv);

        assignmentId = getAssignmentId(getArguments());
        setupRecycler();
        loadResponses();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = GlobalmAPI.FIRST_PAGE;
            adapter.clearResponses();
            loadResponses();
        });

        return root;
    }

    private int getAssignmentId(Bundle arguments) {
        if (arguments == null) {
            return 0;
        }

        return arguments.getInt(AssignmentDetailsActivity.ASSIGNMENT_ID_KEY, 0);
    }

    private void loadResponses() {
        setLoadingStatus(true);
        getRequestManager().getResponses(assignmentId, page, getResponsesCallback());
    }

    private CallbackListener<BaseResponseBody<List<Void>, PaginationData<Response>>> getResponsesCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, PaginationData<Response>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, PaginationData<Response>> o) {
                adapter.addResponses(o.getData().getItems());
                setLoadingStatus(false);

                if (!o.getData().getPagination().getCurrentPage()
                        .equals(o.getData().getPagination().getLastPage())) {
                    page++;
                }

                if (page == GlobalmAPI.FIRST_PAGE && o.getData().getItems().size() == 0) {
                    noDataTv.setVisibility(View.VISIBLE);
                } else {
                    noDataTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                setLoadingStatus(false);
                showMessage(error.getMessage());
            }
        };
    }

    @Override
    public void onDestroy() {
        recyclerView.removeOnScrollListener(endlessRecyclerViewScrollListener);
        super.onDestroy();
    }

    private void setLoadingStatus(boolean isLoading) {
        swipeRefreshLayout.setRefreshing(isLoading);
        endlessRecyclerViewScrollListener.setLoading(isLoading);
    }

    private void setupRecycler() {
        adapter = new AssignmentResponseAdapter(getOnMessageSendListener());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        if (endlessRecyclerViewScrollListener == null) {
            endlessRecyclerViewScrollListener = getNewEndlessRecyclerScrollListener(linearLayoutManager);
        }
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        recyclerView.setAdapter(adapter);
    }

    private Utils.InvokeCallback<Integer> getOnMessageSendListener() {
        return (messageId) -> {
            if (messageId == null) {
                return;
            }
            Toast.makeText(getContext(), "This feature is not implemented on backend!", Toast.LENGTH_LONG).show();

            //RespondDialog.createDialog(getContext(), messageId).show();
            //TODO DISABLED SINCE BACKEND DOES NOT SUPPORT RESPONSE TO RESPONSE
        };
    }

    private EndlessRecyclerViewScrollListener getNewEndlessRecyclerScrollListener(LinearLayoutManager linearLayoutManager) {
        return new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadResponses();
            }
        };
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }
}