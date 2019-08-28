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
import android.widget.EditText;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.NotificationsAdapter;
import com.globalm.platform.models.NotificationModel;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private EditText mFieldSearchNotifications;
    private RecyclerView mListNotifications;
    private TextView mTextRequestNotification;
    private Context mContext;
    private ArrayList<NotificationModel> mNotificationModels = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private NotificationsAdapter mNotificationsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        mFieldSearchNotifications = rootView.findViewById(R.id.field_search_notifications);
        mListNotifications = rootView.findViewById(R.id.list_notifications);
        mTextRequestNotification = rootView.findViewById(R.id.text_request_notification);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mNotificationsAdapter = new NotificationsAdapter();
        mListNotifications.setLayoutManager(mLinearLayoutManager);
        mListNotifications.setAdapter(mNotificationsAdapter);
        initDummyList();
        return rootView;
    }

    private void initDummyList() {
        mNotificationModels.add(new NotificationModel("John Williams", "10 years ago"));
        mNotificationModels.add(new NotificationModel("John Williams", "10 years ago"));
        mNotificationModels.add(new NotificationModel("John Williams", "10 years ago"));
        mNotificationModels.add(new NotificationModel("John Williams", "10 years ago"));
        mNotificationModels.add(new NotificationModel("John Williams", "10 years ago"));
        mNotificationModels.add(new NotificationModel("John Williams", "10 years ago"));
        mNotificationsAdapter.setData(mNotificationModels);
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