package com.globalm.platform.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.globalm.platform.R;
import com.globalm.platform.adapters.SelectTagAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.Pagination;
import com.globalm.platform.models.PaginationData;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.GlobalmAPI;
import com.globalm.platform.network.RequestManager;

import java.util.List;

public class SkillsDialogUtil {

    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    public void showTagDialog(Context context,
                                     RequestManager requestManager,
                                     DialogCallback<List<Tag>> callback) {
        getTagDialog(context, requestManager, callback).show();
    }

    private AlertDialog.Builder getTagDialog(Context context,
                                                    RequestManager requestManager,
                                                    DialogCallback<List<Tag>> callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflateTagDialogLayout(context);
        builder.setTitle(R.string.select_tags);
        builder.setView(dialogView);

        SelectTagAdapter adapter = new SelectTagAdapter();
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        setupTagsList(requestManager, adapter, context, recyclerView, callback);
        initTagsLoad(adapter, requestManager, callback);

        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            recyclerView.removeOnScrollListener(endlessRecyclerViewScrollListener);
            endlessRecyclerViewScrollListener = null;
            callback.onOk(adapter.getSelectedTags());
        });
        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
            recyclerView.removeOnScrollListener(endlessRecyclerViewScrollListener);
            endlessRecyclerViewScrollListener = null;
            callback.onCancel();
        });
        return builder;
    }

    private View inflateTagDialogLayout(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.dialog_tags, null);
    }

    private void setupTagsList(RequestManager requestManager,
                                      SelectTagAdapter adapter,
                                      Context context,
                                      RecyclerView recyclerView,
                                      DialogCallback<List<Tag>> callback) {
        LinearLayoutManager linearLayoutmanager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false);
        endlessRecyclerViewScrollListener
                = new EndlessRecyclerViewScrollListener(linearLayoutmanager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        requestManager.getSkills(
                                page,
                                getSkillsCallback(callback, adapter));
                    }
        };
        recyclerView.setLayoutManager(linearLayoutmanager);
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        recyclerView.setAdapter(adapter);
    }

    private void initTagsLoad(SelectTagAdapter adapter,
                                     RequestManager requestManager,
                                     DialogCallback<List<Tag>> callback) {
        requestManager.getSkills(
                GlobalmAPI.FIRST_PAGE,
                getSkillsCallback(callback, adapter));
    }

    private CallbackListener<BaseResponseBody<List<Void>, PaginationData<Tag>>> getSkillsCallback(
            DialogCallback<List<Tag>> callback,
            SelectTagAdapter adapter) {
        return new CallbackListener<BaseResponseBody<List<Void>, PaginationData<Tag>>>() {
            @Override
            public void onSuccess(
                    BaseResponseBody<List<Void>, PaginationData<Tag>> o) {
                adapter.addTags(o.getData().getItems());
                Pagination pagination = o.getData().getPagination();
                if (pagination.getCurrentPage().equals(pagination.getLastPage())) {
                    return;
                }
                endlessRecyclerViewScrollListener.setLoading(false);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onApiFailure(error);
                endlessRecyclerViewScrollListener.setLoading(false);
            }
        };
    }

    public interface DialogCallback <T> {
        void onOk(T item);
        void onCancel();
        void onApiFailure(Throwable error);
    }
}
