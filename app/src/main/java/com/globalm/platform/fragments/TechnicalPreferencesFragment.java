package com.globalm.platform.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.adapters.DateTimeZoneAdapter;
import com.globalm.platform.listeners.OnDateTimeZoneSelectedListener;
import com.globalm.platform.network.CloudSyncWorker;
import com.globalm.platform.utils.PermissionsUtil;
import com.globalm.platform.utils.SharedPreferencesUtil;

import java.io.File;

import timber.log.Timber;

import static com.globalm.platform.utils.Utils.getTimeZoneList;

public class TechnicalPreferencesFragment extends BaseFragment implements View.OnClickListener, OnDateTimeZoneSelectedListener {
    private static final String SYNC_FOLDER_NAME = "GlobalMSync";

    public static final String SYNC_FOLDER_PATH =
            Environment.getExternalStorageDirectory() + "/" + SYNC_FOLDER_NAME;

    private final int TYPE_TIMEZONE = 0;
    private final int TYPE_DATE_FORMAT = 1;
    private final int TYPE_TIME_FORMAT = 2;

    private Context mContext;
    private ExpandableRelativeLayout mLayoutExpandableTimezoneList;
    private ExpandableRelativeLayout mLayoutExpandableDateFormatList;
    private ExpandableRelativeLayout mLayoutExpandableTimeFormatList;
    private TextView mTextSelectedTimezoneList;
    private TextView mTextSelectedDateFormatList;
    private TextView mTextSelectedTimeFormatList;
    private ImageView mImageArrowDownTimezoneList;
    private ImageView mImageArrowDownDateFormatList;
    private ImageView mImageArrowDownTimeFormatList;
    private SwitchCompat mSitchUseCloudSync;
    private String[] mDateFormatList = {"DD/MM/YYYY", "MM/DD/YYYY", "YYYY/MM/DD", "DD.MM.YYYY", "DD-MM-YYYY"};
    private String[] mTimeFormatList = {"hh:mm:ss", "hh:mm:ss:s"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_technical_preferences, container, false);
        TextView buttonUpdateTimeZone = rootView.findViewById(R.id.button_update_timezone);
        TextView buttonUpdateDateFormat = rootView.findViewById(R.id.button_update_date_format);
        TextView buttonUpdateTimeFormat = rootView.findViewById(R.id.button_update_time_format);
        RelativeLayout buttonOpenTimezoneList = rootView.findViewById(R.id.button_open_timezone_list);
        RelativeLayout buttonOpenDateFormatList = rootView.findViewById(R.id.button_open_date_format_list);
        RelativeLayout buttonOpenTimeFormatList = rootView.findViewById(R.id.button_open_time_format_list);
        RecyclerView listTimezone = rootView.findViewById(R.id.list_timezone);
        RecyclerView listDateFormat = rootView.findViewById(R.id.list_date_format);
        RecyclerView listTimeFormat = rootView.findViewById(R.id.list_time_format);
        mLayoutExpandableTimezoneList = rootView.findViewById(R.id.layout_expandable_timezone_list);
        mLayoutExpandableDateFormatList = rootView.findViewById(R.id.layout_expandable_date_format_list);
        mLayoutExpandableTimeFormatList = rootView.findViewById(R.id.layout_expandable_time_format_list);
        mTextSelectedTimezoneList = rootView.findViewById(R.id.text_selected_timezone_list);
        mTextSelectedDateFormatList = rootView.findViewById(R.id.text_selected_date_format_list);
        mTextSelectedTimeFormatList = rootView.findViewById(R.id.text_selected_time_format_list);
        mImageArrowDownTimezoneList = rootView.findViewById(R.id.image_arrow_down_timezone_list);
        mImageArrowDownDateFormatList = rootView.findViewById(R.id.image_arrow_down_date_format_list);
        mImageArrowDownTimeFormatList = rootView.findViewById(R.id.image_arrow_down_time_format_list);
        mSitchUseCloudSync = rootView.findViewById(R.id.switch_cloud_sync);
        buttonUpdateTimeZone.setOnClickListener(this);
        buttonUpdateDateFormat.setOnClickListener(this);
        buttonUpdateTimeFormat.setOnClickListener(this);
        buttonOpenTimezoneList.setOnClickListener(this);
        buttonOpenDateFormatList.setOnClickListener(this);
        buttonOpenTimeFormatList.setOnClickListener(this);
        String[] timezoneList = getTimeZoneList();
        DateTimeZoneAdapter timeZoneAdapter = new DateTimeZoneAdapter(TYPE_TIMEZONE, timezoneList, this);
        DateTimeZoneAdapter dateFormatAdapter = new DateTimeZoneAdapter(TYPE_DATE_FORMAT, mDateFormatList, this);
        DateTimeZoneAdapter timeFormatAdapter = new DateTimeZoneAdapter(TYPE_TIME_FORMAT, mTimeFormatList, this);
        listTimezone.setLayoutManager(new LinearLayoutManager(mContext));
        listDateFormat.setLayoutManager(new LinearLayoutManager(mContext));
        listTimeFormat.setLayoutManager(new LinearLayoutManager(mContext));
        listTimezone.setAdapter(timeZoneAdapter);
        listDateFormat.setAdapter(dateFormatAdapter);
        listTimeFormat.setAdapter(timeFormatAdapter);
        mTextSelectedTimezoneList.setText(timezoneList[0]);
        mTextSelectedDateFormatList.setText(mDateFormatList[0]);
        mTextSelectedTimeFormatList.setText(mTimeFormatList[0]);
        mSitchUseCloudSync.setChecked(isSyncEnabled());
        mSitchUseCloudSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            shouldStartSyncTask(isChecked);
            SharedPreferencesUtil.setSyncFolderEnabledOption(isChecked);
            checkAndCreateSyncFolder(isSyncEnabled());
            if (!isChecked) {
                CloudSyncWorker.clearSyncRequest();
            }
        });
        checkAndCreateSyncFolder(isSyncEnabled());
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_open_timezone_list:
                if (mLayoutExpandableTimezoneList.isExpanded()) {
                    mLayoutExpandableTimezoneList.collapse();
                } else {
                    mLayoutExpandableTimezoneList.expand();
                }

                rotateArrow(TYPE_TIMEZONE, !mLayoutExpandableTimezoneList.isExpanded());
                break;

            case R.id.button_open_date_format_list:
                if (mLayoutExpandableDateFormatList.isExpanded()) {
                    mLayoutExpandableDateFormatList.collapse();
                } else {
                    mLayoutExpandableDateFormatList.expand();
                }

                rotateArrow(TYPE_DATE_FORMAT, !mLayoutExpandableDateFormatList.isExpanded());
                break;

            case R.id.button_open_time_format_list:
                if (mLayoutExpandableTimeFormatList.isExpanded()) {
                    mLayoutExpandableTimeFormatList.collapse();
                } else {
                    mLayoutExpandableTimeFormatList.expand();
                }

                rotateArrow(TYPE_TIME_FORMAT, !mLayoutExpandableTimeFormatList.isExpanded());
                break;

            case R.id.button_update_timezone:
                if (mLayoutExpandableTimezoneList.isExpanded()) {
                    mLayoutExpandableTimezoneList.collapse();
                    rotateArrow(TYPE_TIMEZONE, false);
                }
                break;

            case R.id.button_update_date_format:
                if (mLayoutExpandableDateFormatList.isExpanded()) {
                    mLayoutExpandableDateFormatList.collapse();
                    rotateArrow(TYPE_DATE_FORMAT, false);
                }
                break;

            case R.id.button_update_time_format:
                if (mLayoutExpandableTimeFormatList.isExpanded()) {
                    mLayoutExpandableTimeFormatList.collapse();
                    rotateArrow(TYPE_TIME_FORMAT, false);
                }
                break;
        }
    }

    @Override
    public void onTimeZoneSelected(int type, String timezone) {
        rotateArrow(type, false);
        switch (type) {
            case TYPE_TIMEZONE:
                mLayoutExpandableTimezoneList.collapse();
                mTextSelectedTimezoneList.setText(timezone);
                break;

            case TYPE_DATE_FORMAT:
                mLayoutExpandableDateFormatList.collapse();
                mTextSelectedDateFormatList.setText(timezone);
                break;

            case TYPE_TIME_FORMAT:
                mLayoutExpandableTimeFormatList.collapse();
                mTextSelectedTimeFormatList.setText(timezone);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        PermissionsUtil.processStoragePermissionResult(requestCode, grantResults, result ->
                checkAndCreateSyncFolder(isSyncEnabled()));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isSyncEnabled() {
        return SharedPreferencesUtil.getSyncFolderEnabledOption();
    }

    private void createSyncTask() {
        CloudSyncWorker.createSyncTask();
    }

    private void shouldStartSyncTask(boolean isChecked) {
        if (!isSyncEnabled() && isChecked) {
            createSyncTask();
        }
    }

    private void checkAndCreateSyncFolder(boolean syncEnabled) {
        if (!syncEnabled) {
            return;
        }

        if (PermissionsUtil.checkAndRequestPermissions(
                this,
                PermissionsUtil.getStoragePermissions(),
                PermissionsUtil.STORAGE_PERMISSION_REQUEST_CODE)) {
            if (!getSyncFolderFile().mkdirs()) {
                Timber.e("Unable to create sync folder");
            }
        }
    }


    private File getSyncFolderFile() {
        return new File(SYNC_FOLDER_PATH);
    }

    private void rotateArrow(int type, boolean expand) {
        switch (type) {
            case TYPE_TIMEZONE:
                ObjectAnimator.ofFloat(mImageArrowDownTimezoneList, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;

            case TYPE_DATE_FORMAT:
                ObjectAnimator.ofFloat(mImageArrowDownDateFormatList, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;

            case TYPE_TIME_FORMAT:
                ObjectAnimator.ofFloat(mImageArrowDownTimeFormatList, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
        }
    }
}
