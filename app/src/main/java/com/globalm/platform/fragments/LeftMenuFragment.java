package com.globalm.platform.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.globalm.platform.R;
import com.globalm.platform.activities.CameraActivity;
import com.globalm.platform.activities.CreateContentActivity;
import com.globalm.platform.activities.MainActivity;
import com.globalm.platform.activities.SettingsActivity;

public class LeftMenuFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private LinearLayout mButtonDashboard;
    private LinearLayout mButtonSummary;
    private LinearLayout mButtonUsers;
    private LinearLayout mButtonGoLive;
    private LinearLayout mButtonEvent;
    private LinearLayout mButtonUpload;
    private ImageView mButtonSettings;
    private ImageView mButtonLogout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_left_menu, container, false);
        mButtonDashboard = rootView.findViewById(R.id.button_dashboard);
        mButtonSummary = rootView.findViewById(R.id.button_summary);
        mButtonUsers = rootView.findViewById(R.id.button_users);
        mButtonGoLive = rootView.findViewById(R.id.button_go_live);
        mButtonEvent = rootView.findViewById(R.id.button_event);
        mButtonUpload = rootView.findViewById(R.id.button_upload);
        mButtonSettings = rootView.findViewById(R.id.button_settings);
        mButtonLogout = rootView.findViewById(R.id.button_logout);
        mButtonDashboard.setOnClickListener(this);
        mButtonSummary.setOnClickListener(this);
        mButtonUsers.setOnClickListener(this);
        mButtonGoLive.setOnClickListener(this);
        mButtonEvent.setOnClickListener(this);
        mButtonUpload.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
        mButtonLogout.setOnClickListener(this);
        return rootView;
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
        ((MainActivity) mContext).closeDrawer();
        switch (v.getId()) {
            case R.id.button_dashboard:
                ((MainActivity) mContext).getNavigation().findViewById(R.id.dashboard).performClick();
                break;

            case R.id.button_summary:
                ((MainActivity) mContext).getNavigation().findViewById(R.id.content_summary).performClick();

                break;

            case R.id.button_users:
                ((MainActivity) mContext).getNavigation().findViewById(R.id.contacts).performClick();
                break;

            case R.id.button_go_live:
                new Handler().postDelayed(() -> mContext.startActivity(new Intent(mContext, CameraActivity.class)), 100);
                break;

            case R.id.button_event:
                CreateContentActivity.start(getContext(), CreateContentFragment.POSITION_CREATE_EVENT);
                break;

            case R.id.button_upload:
                CreateContentActivity.start(getContext(), CreateContentFragment.POSITION_UPLOAD);
                break;

            case R.id.button_settings:
                new Handler().postDelayed(() -> mContext.startActivity(new Intent(mContext, SettingsActivity.class)), 100);
                break;

            case R.id.button_logout:
                ((MainActivity) mContext).logout();
                break;
        }
    }
}