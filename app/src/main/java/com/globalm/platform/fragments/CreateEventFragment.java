package com.globalm.platform.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.activities.PlacesPluginActivity;
import com.globalm.platform.activities.UploadActivity;
import com.globalm.platform.adapters.DateTimeZoneAdapter;
import com.globalm.platform.adapters.PricingAdapter;
import com.globalm.platform.adapters.PricingUploadAdapter;
import com.globalm.platform.adapters.ShareCopyRightAdapter;
import com.globalm.platform.adapters.ShareWithIndividualsAdapter;
import com.globalm.platform.adapters.TagsAdapter;
import com.globalm.platform.listeners.OnDateTimeZoneSelectedListener;
import com.globalm.platform.listeners.OnShareWithIndividualsListener;
import com.globalm.platform.models.PricingModel;
import com.globalm.platform.models.ShareWithIndividualsModel;
import com.globalm.platform.models.TagsModel;
import com.globalm.platform.utils.MapBoxUtil;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.globalm.platform.utils.Utils.getTimeZoneList;

public class CreateEventFragment extends Fragment implements View.OnClickListener, OnShareWithIndividualsListener, ShareWithIndividualsAdapter.OnCountListener, OnDateTimeZoneSelectedListener, CompoundButton.OnCheckedChangeListener {

    public static final int REQUEST_PICK_VIDEO = 3;
    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";
    private static final int READ_REQUEST_CODE = 20000;
    private static final double DEFAULT_LOCATION_LAT = 2.008751930035146;
    private static final double DEFAULT_LOCATION_LNG = 46.993565237097044;
    public static final int REQUEST_LOCATION = 9;
    public static final int REQUEST_LOCATION_ADVANCED = 10;
    private static final int STORAGE_PERMISSION_CODE = 123;
    public final int TYPE_COPY_RIGHT = 1;
    public final int TYPE_STREAM_OPTION = 2;
    private final int TYPE_SHARE_WITH_INDIVIDUALS = 0;
    private final int TYPE_TIMEZONE = 4;
    private final int TYPE_TIMEZONE_ADVANCED = 5;
    private final int TYPE_SHOT_TYPE = 6;
    private final int TYPE_STREAM_TYPE = 7;
    private final int TYPE_STREAM_SOURCE = 8;
    ShareCopyRightAdapter ShareWithIndividualsAdapterWithOutIcon;
    private Context mContext;
    private ArrayList<ShareWithIndividualsModel> mShareWithIndividualsModels = new ArrayList<>();
    private String[] mShareWithIndividuals = {"jack@knowlocker.com", "Alisa@knowlocker.com", "Arthur@knowlocker.com"};
    private String[] mCopyRight = {"Pool", "Public", "GlobalM content",
            "Cleared", "Host broadcaster content", "Exclusive with conditions", "Exclusive for an MO", "Embargo with time / date stamp",
            "Embargo by country", "Broadcast only", "Digital only", "Other"};
    private String[] mStreamOption = {"GlobalM app"};
    private String[] mShotType = {"Test1","Test2","Test3"};
    private String[] mSteamType = {"Test1","Test2","Test3"};
    private String[] mSteamSource = {"Test1","Test2","Test3"};
    private ExpandableRelativeLayout mLayoutExpandableStreamOption;
    private ExpandableRelativeLayout mLayoutExpandableShareWithIndividuals;
    private ExpandableRelativeLayout mLayoutExpandableCopyRight;
    private ExpandableRelativeLayout mLayoutExpandableShotType;
    private ExpandableRelativeLayout mLayoutExpandableStreamType;
    private ExpandableRelativeLayout mLayoutExpandableStreamSource;
    private ImageView mButtonCopyRight;
    private ImageView mImageLocation;
    private ImageView mImageStreamOption;
    private ImageView mImageTimeDate;
    private ScrollView mScrollView;
    private VideoView mVideoThumbnail;
    private TextView mButtonAddNewPricingOption;
    private TextView mButtonChangeThumbnail;
    //    private EditText mEditShareWithIndividuals;
    private TextView mEditCopyRight;
    private EditText mEditStreamOption;
    private Bitmap FixBitmap;
    private RecyclerView mListPricing;
    private PricingAdapter mPricingAdapter;
    private ArrayList<PricingModel> mPricing = new ArrayList<>();
    private EditText mFieldTitle;
    private EditText mFieldSubTitle;
    private EditText mFieldDescription;
    private Button mButtonCreateEvent;
    private RelativeLayout mLayoutCopyRight_Other;
    private ShareWithIndividualsAdapter mShareWithIndividualsAdapter;
    private RecyclerView mListShareWithIndividuals;
    private RecyclerView mListTags;
    private TagsAdapter mTagsAdapter;
    private ArrayList<TagsModel> mTagsModels = new ArrayList<>();
    private Uri video;
    private String videoPath;
    private RelativeLayout mLayoutTags;
    private TextView mTextAddNew;
    private PricingUploadAdapter mPricingUploadAdapter;
    private ArrayList<PricingModel> mPricingModels = new ArrayList<>();
    private LinearLayout mLayoutNewPricing;
    private EditText mFieldPrice;
    private ImageView mImagePriceAdd;
    private TextView mTextPriceTitle;
    private String[] mPriceList = new String[]{"Exclusive", "Pool Access"};
    private EditText mFieldShareWithIndividuals;
    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;
    private ExpandableRelativeLayout mLayoutExpandableTimezoneList;
    private ExpandableRelativeLayout mLayoutExpandableTimezoneAdvancedList;
    private double mGeolocationLat = DEFAULT_LOCATION_LAT;
    private double mGeoLocationLng = DEFAULT_LOCATION_LNG;
    private double mGeolocationLatAdvanced = DEFAULT_LOCATION_LAT;
    private double mGeoLocationLngAdvanced = DEFAULT_LOCATION_LNG;
    private TextView mTextSelectedTimezoneList;
    private RecyclerView listTimezone;
    private ImageView mImageTimeZone;
    private RecyclerView mListTimeZoneAdvanced;
    private ImageView mImageTimeZoneAdvanced;
    private TextView mTextSelectedTimezoneAdvancedList;
    private TextView mButtonUploadFile;
    private VideoView mUploadFile;
    private String postPath;
    private Uri selectedVideo;
    private String mediaPath;
    private RelativeLayout mLayoutUploadDevice;
    private ImageView mClearUploadFiles;
    private MapView mMapView;
    private MapView mMapViewAdvanced;
    private TextView mFieldTimeDate;
    private TextView mFieldTime;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private int currentHour;
    private int currentMinute;
    private RecyclerView mListSpecificMediaOutlets;
    private TagsAdapter mSpecificMediaOutletsAdapter;
    private RelativeLayout mLayoutSpecificMediaOutlets;
    private RecyclerView mListRestrictByTag;
    private TagsAdapter mRestrictByTagAdapter;
    private RelativeLayout mLayoutRestrictByTag;
    private ImageView mButtonShotType;
    private TextView mFieldShotType;
    private ImageView mButtonStreamType;
    private TextView mFieldStreamType;
    private ImageView mButtonStreamSource;
    private TextView mFieldStreamSource;
    private AppCompatCheckBox mCheckboxApplyRestrictions;
    private AppCompatCheckBox mCheckboxAnyoneCanPurchase;
    private LinearLayout mLayoutAdvanced;
    private TextView mFieldLocation;
    private TextView mFieldEnterSelected;

    @Nullable
    private MapboxMap mapboxMap;

    @Nullable
    private MapboxMap mapboxMapAdvanced;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);
        mScrollView = rootView.findViewById(R.id.scroll_view);
        mVideoThumbnail = rootView.findViewById(R.id.video_thumbnail);
        mTextAddNew = rootView.findViewById(R.id.text_add_new);
        mButtonChangeThumbnail = rootView.findViewById(R.id.button_change_thumbnail);
        mLayoutExpandableShareWithIndividuals = rootView.findViewById(R.id.layout_expandable_share_with_individuals);
        mLayoutExpandableCopyRight = rootView.findViewById(R.id.layout_expandable_copy_right);
//        mLayoutExpandableStreamOption = rootView.findViewById(R.id.layout_expandable_streaming_option);
//        mEditShareWithIndividuals = rootView.findViewById(R.id.edt_share_with_individuals);
        mButtonCopyRight = rootView.findViewById(R.id.button_open_copy_right);
        mEditCopyRight = rootView.findViewById(R.id.field_copy_right);
//        mImageStreamOption = rootView.findViewById(R.id.image_stream_option);
//        mEditStreamOption = rootView.findViewById(R.id.edt_stream_option);
        mImageLocation = rootView.findViewById(R.id.image_location);
//        mImageTimeDate = rootView.findViewById(R.id.image_time_date);
        mFieldTitle = rootView.findViewById(R.id.field_title);
        mFieldSubTitle = rootView.findViewById(R.id.field_subtitle);
        mFieldTimeDate = rootView.findViewById(R.id.field_time_date);
//        mFieldDescription = rootView.findViewById(R.id.field_description);
        mButtonCreateEvent = rootView.findViewById(R.id.button_upload);
        mLayoutCopyRight_Other = rootView.findViewById(R.id.layout_copy_right_other);
        mListShareWithIndividuals = rootView.findViewById(R.id.list_share_with_individuals_with_icons);
        mLayoutTags = rootView.findViewById(R.id.layout_tags);
        mListTags = rootView.findViewById(R.id.list_tags);
        mLayoutNewPricing = rootView.findViewById(R.id.layout_new_pricing);
        mFieldPrice = rootView.findViewById(R.id.field_price);
        mImagePriceAdd = rootView.findViewById(R.id.image_price_add);
        mTextPriceTitle = rootView.findViewById(R.id.text_price_title);
        mFieldShareWithIndividuals = rootView.findViewById(R.id.field_share_with_individuals);
        mLayoutExpandableTimezoneList = rootView.findViewById(R.id.layout_expandable_timezone_list);
        mTextSelectedTimezoneList = rootView.findViewById(R.id.field_time_zone);
        listTimezone = rootView.findViewById(R.id.list_timezone);
        mImageTimeZone = rootView.findViewById(R.id.image_time_zone);
        mLayoutExpandableTimezoneAdvancedList = rootView.findViewById(R.id.layout_expandable_timezone_advancedlist);
        mListTimeZoneAdvanced = rootView.findViewById(R.id.list_timezone_advanced);
        mImageTimeZoneAdvanced = rootView.findViewById(R.id.image_time_zone_advanced);
        mTextSelectedTimezoneAdvancedList = rootView.findViewById(R.id.field_time_zone_advanced);
        mButtonUploadFile = rootView.findViewById(R.id.button_upload_file);
        mUploadFile = rootView.findViewById(R.id.plan_video_view);
        mLayoutUploadDevice = rootView.findViewById(R.id.layout_upload_device);
        mClearUploadFiles = rootView.findViewById(R.id.clear_upload_files);
        mMapView = rootView.findViewById(R.id.map_view);
        mMapViewAdvanced = rootView.findViewById(R.id.map_view_advanced);
        mFieldTime = rootView.findViewById(R.id.field_time);
        mListSpecificMediaOutlets = rootView.findViewById(R.id.list_specific_media_outlets);
        mLayoutSpecificMediaOutlets = rootView.findViewById(R.id.layout_exclude_specific_media_outlets);
        mListRestrictByTag = rootView.findViewById(R.id.list_restrict_by_tag);
        mLayoutRestrictByTag = rootView.findViewById(R.id.layout_restrict_by_tag);
        mLayoutExpandableShotType = rootView.findViewById(R.id.layout_expandable_shot_type);
        mButtonShotType = rootView.findViewById(R.id.button_open_shot_type);
        mFieldShotType = rootView.findViewById(R.id.field_shot_type);
        mLayoutExpandableStreamType = rootView.findViewById(R.id.layout_expandable_stream_type);
        mFieldStreamType = rootView.findViewById(R.id.field_stream_type);
        mButtonStreamType = rootView.findViewById(R.id.button_open_stream_type);
        mLayoutExpandableStreamSource = rootView.findViewById(R.id.layout_expandable_stream_source);
        mButtonStreamSource = rootView.findViewById(R.id.button_stream_source);
        mFieldStreamSource = rootView.findViewById(R.id.field_stream_source);
        mCheckboxAnyoneCanPurchase = rootView.findViewById(R.id.checkbox_anyone_can_purchase);
        mCheckboxApplyRestrictions = rootView.findViewById(R.id.checkbox_apply_restrictions);
        mLayoutAdvanced = rootView.findViewById(R.id.layout_advanced);
        mFieldLocation = rootView.findViewById(R.id.field_location);
        mFieldEnterSelected = rootView.findViewById(R.id.field_enter_select);
        mButtonCreateEvent.setOnClickListener(this);
//        mImageStreamOption.setOnClickListener(this);
//        mImageTimeDate.setOnClickListener(this);
        mButtonCopyRight.setOnClickListener(this);
        mLayoutTags.setOnClickListener(this);
        mTextAddNew.setOnClickListener(this);
        mImagePriceAdd.setOnClickListener(this);
        mTextPriceTitle.setOnClickListener(this);
        mImageTimeZone.setOnClickListener(this);
        mImageTimeZoneAdvanced.setOnClickListener(this);
        mButtonUploadFile.setOnClickListener(this);
        mClearUploadFiles.setOnClickListener(this);
        mFieldTimeDate.setOnClickListener(this);
        mFieldTime.setOnClickListener(this);
        mLayoutRestrictByTag.setOnClickListener(this);
        mLayoutSpecificMediaOutlets.setOnClickListener(this);
        mButtonShotType.setOnClickListener(this);
        mButtonStreamType.setOnClickListener(this);
        mButtonStreamSource.setOnClickListener(this);
        mFieldEnterSelected.setOnClickListener(this);
        mCheckboxAnyoneCanPurchase.setOnCheckedChangeListener(this);
        mCheckboxApplyRestrictions.setOnCheckedChangeListener(this);
        mFieldLocation.setOnClickListener(this);
        ShareWithIndividualsAdapterWithOutIcon = new ShareCopyRightAdapter(TYPE_SHARE_WITH_INDIVIDUALS, mShareWithIndividuals, this);
        ShareCopyRightAdapter adapterCopyRight = new ShareCopyRightAdapter(TYPE_COPY_RIGHT, mCopyRight, this);
        ShareCopyRightAdapter adapterStream = new ShareCopyRightAdapter(TYPE_STREAM_OPTION, mStreamOption, this);
        mShareWithIndividualsAdapter = new ShareWithIndividualsAdapter(getActivity(), this);
        ShareCopyRightAdapter adapterShotType = new ShareCopyRightAdapter(TYPE_SHOT_TYPE, mShotType, this);
        ShareCopyRightAdapter adapterStreamType = new ShareCopyRightAdapter(TYPE_STREAM_TYPE, mSteamType, this);
        ShareCopyRightAdapter adapterStreamSource = new ShareCopyRightAdapter(TYPE_STREAM_SOURCE, mSteamSource, this);
        mListShareWithIndividuals.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListShareWithIndividuals.setAdapter(mShareWithIndividualsAdapter);
        RecyclerView ListShareWithIndividuals = rootView.findViewById(R.id.list_share_with_individuals);
        RecyclerView mListCopyRight = rootView.findViewById(R.id.list_copy_right);
        mListCopyRight.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListShareWithIndividuals.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListTags.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mTagsAdapter = new TagsAdapter(getActivity());
        mListTags.setAdapter(mTagsAdapter);
        mListSpecificMediaOutlets.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mSpecificMediaOutletsAdapter = new TagsAdapter(getActivity());
        mListSpecificMediaOutlets.setAdapter(mSpecificMediaOutletsAdapter);
        mListRestrictByTag.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRestrictByTagAdapter = new TagsAdapter(getActivity());
        mListRestrictByTag.setAdapter(mRestrictByTagAdapter);
        mListCopyRight.setAdapter(adapterCopyRight);
        RecyclerView ListShotType = rootView.findViewById(R.id.list_shot_type);
        ListShotType.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListShotType.setAdapter(adapterShotType);
        RecyclerView ListStreamType = rootView.findViewById(R.id.list_stream_type);
        ListStreamType.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListStreamType.setAdapter(adapterStreamType);
        RecyclerView ListStreamSource = rootView.findViewById(R.id.list_stream_source);
        ListStreamSource.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListStreamSource.setAdapter(adapterStreamSource);
        ListShareWithIndividuals.setAdapter(ShareWithIndividualsAdapterWithOutIcon);
//        RecyclerView mListStreamOption = rootView.findViewById(R.id.list_streaming_option);
//        mListStreamOption.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mListStreamOption.setAdapter(adapterStream);
        SpannableString content = new SpannableString("Add New");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mTextAddNew.setText(content);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mLayoutNewPricing.setVisibility(View.GONE);
        mListPricing = rootView.findViewById(R.id.list_pricing);
        mListPricing.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPricingUploadAdapter = new PricingUploadAdapter(getActivity());
        mListPricing.setAdapter(mPricingUploadAdapter);
        initList();
        mPricingAdapter = new PricingAdapter(getActivity());
        mPricingAdapter.setData(mPricing);
        mLayoutCopyRight_Other.setVisibility(View.GONE);
        initDummyList();
        initDummyListTags();
        mFieldShareWithIndividuals.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    if (!mFieldShareWithIndividuals.getText().toString().trim().equals("")) {
                        ShareWithIndividualsModel shareWithIndividualsModel = new ShareWithIndividualsModel(R.drawable.ic_user_icon_new, "", R.drawable.ic_delete_button);
                        shareWithIndividualsModel.setUserEmail(mFieldShareWithIndividuals.getText().toString());
                        mShareWithIndividualsAdapter.addItem(shareWithIndividualsModel);
                        mFieldShareWithIndividuals.setText("");
                    } else {
                        mFieldShareWithIndividuals.setText("");
                    }
                }
                //filter(s.toString());
                // mLayoutExpandableShareWithIndividuals.expand();
            }
        });
        mFieldShareWithIndividuals.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus){
//                mLayoutExpandableShareWithIndividuals.expand();
//            }else {
//                mLayoutExpandableShareWithIndividuals.collapse();
//            }
        });
        String[] timezoneList = getTimeZoneList();
        DateTimeZoneAdapter timeZoneAdapter = new DateTimeZoneAdapter(TYPE_TIMEZONE, timezoneList, this);
        DateTimeZoneAdapter timeZoneAdvancedAdapter = new DateTimeZoneAdapter(TYPE_TIMEZONE_ADVANCED, timezoneList, this);
        listTimezone.setLayoutManager(new LinearLayoutManager(getActivity()));
        listTimezone.setAdapter(timeZoneAdapter);
        mListTimeZoneAdvanced.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListTimeZoneAdvanced.setAdapter(timeZoneAdvancedAdapter);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            this.mapboxMap = mapboxMap;
        }));

        mMapView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return mMapView.onTouchEvent(event);
        });

        mMapViewAdvanced.onCreate(savedInstanceState);
        mMapViewAdvanced.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            this.mapboxMapAdvanced = mapboxMap;
        }));

        mMapViewAdvanced.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return mMapViewAdvanced.onTouchEvent(event);
        });

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PICK_VIDEO);
            }
            requestStoragePermission();
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        mMapViewAdvanced.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        mMapViewAdvanced.onDestroy();
    }

    private void clearUploadFiles() {
        postPath = null;
        mediaPath = null;
        mUploadFile.setVisibility(View.GONE);
        mClearUploadFiles.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapViewAdvanced.onResume();
        mUploadFile.seekTo(1);
    }


    private void filter(String text) {
        ArrayList<String> list = new ArrayList<>();
        for (String item : mShareWithIndividuals) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                list.add(item);
            }
        }
        ShareWithIndividualsAdapterWithOutIcon.filterList(list);
    }

    private void initDummyList() {
        //   mShareWithIndividualsModels.add(new ShareWithIndividualsModel(R.drawable.ic_user_icon_new, "jack@knowlocker.com", R.drawable.ic_delete_button));
        mShareWithIndividualsAdapter.setData(mShareWithIndividualsModels);
    }

    private void initDummyListTags() {
        mTagsAdapter.setData(mTagsModels);
    }


    private void initList() {
//        mPricingModels.add(new PricingModel("Exclusive", "300", R.drawable.ic_remove));
//        mPricingModels.add(new PricingModel("Pool Access", "150", R.drawable.ic_remove));
        mPricingUploadAdapter.setData(mPricingModels);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mUploadFile.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Media playback takes a lot of resources, so everything should be
        // stopped and released at this time.
        releasePlayer();
        mUploadFile.stopPlayback();
    }

    private void releasePlayer() {
        //    mVideoThumbnail.stopPlayback();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current playback position (in milliseconds) to the
        // instance state bundle.
//        outState.putInt(PLAYBACK_TIME, mVideoThumbnail.getCurrentPosition());
    }

    private void showClearButton() {
        mClearUploadFiles.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_add_new:
                mLayoutNewPricing.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_tags:
                Dialog dialogTag;
                final String[] items = {" European Union", " Economics", " President", " War", " Disaster", " Weapon", " 2019"};
                final ArrayList<Integer> itemsSelected = new ArrayList<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Tags ");
                builder.setMultiChoiceItems(items, null,
                        (dialog1, selectedItemId, isSelected) -> {
                            if (isSelected) {
                                itemsSelected.add(selectedItemId);
                                for (int i = 0; i < itemsSelected.size(); i++) {
                                }
                            } else if (itemsSelected.contains(selectedItemId)) {
                                itemsSelected.remove(Integer.valueOf(selectedItemId));
                            }
                        })
                        .setPositiveButton("Done!", (dialog12, id) -> {
                            TagsModel[] tagsModels = new TagsModel[itemsSelected.size()];
                            for (int i = 0; i < tagsModels.length; i++) {
                                tagsModels[i] = new TagsModel(R.drawable.ic_delete_button, items[itemsSelected.get(i)]);
                                mTagsAdapter.addItem(tagsModels[i]);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog13, id) -> {
                        });
                dialogTag = builder.create();
                dialogTag.show();
                break;
            case R.id.layout_exclude_specific_media_outlets:
                Dialog dialogSpecificMediaOutlets;
                final String[] itemsSMO = {" Test1", " Test2", " Test3", " Test4"};
                final ArrayList<Integer> itemsSelectedSMO = new ArrayList<>();
                AlertDialog.Builder builderSMO = new AlertDialog.Builder(getActivity());
                builderSMO.setTitle("Select");
                builderSMO.setMultiChoiceItems(itemsSMO, null,
                        (dialog1, selectedItemId, isSelected) -> {
                            if (isSelected) {
                                itemsSelectedSMO.add(selectedItemId);
                                for (int i = 0; i < itemsSelectedSMO.size(); i++) {
                                }
                            } else if (itemsSelectedSMO.contains(selectedItemId)) {
                                itemsSelectedSMO.remove(Integer.valueOf(selectedItemId));
                            }
                        })
                        .setPositiveButton("Done!", (dialog12, id) -> {
                            TagsModel[] tagsModels = new TagsModel[itemsSelectedSMO.size()];
                            for (int i = 0; i < tagsModels.length; i++) {
                                tagsModels[i] = new TagsModel(R.drawable.ic_delete_button, itemsSMO[itemsSelectedSMO.get(i)]);
                                mSpecificMediaOutletsAdapter.addItem(tagsModels[i]);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog13, id) -> {
                        });
                dialogSpecificMediaOutlets = builderSMO.create();
                dialogSpecificMediaOutlets.show();
                break;
            case R.id.layout_restrict_by_tag:
                Dialog dialogRestcictByTag;
                final String[] itemsRBT = {" Tag1", " Tag12", " Tag13", " Tag14"};
                final ArrayList<Integer> itemsSelectedRBT = new ArrayList<>();
                AlertDialog.Builder builderRBT = new AlertDialog.Builder(getActivity());
                builderRBT.setTitle("Select");
                builderRBT.setMultiChoiceItems(itemsRBT, null,
                        (dialog1, selectedItemId, isSelected) -> {
                            if (isSelected) {
                                itemsSelectedRBT.add(selectedItemId);
                                for (int i = 0; i < itemsSelectedRBT.size(); i++) {
                                }
                            } else if (itemsSelectedRBT.contains(selectedItemId)) {
                                itemsSelectedRBT.remove(Integer.valueOf(selectedItemId));
                            }
                        })
                        .setPositiveButton("Done!", (dialog12, id) -> {
                            TagsModel[] tagsModels = new TagsModel[itemsSelectedRBT.size()];
                            for (int i = 0; i < tagsModels.length; i++) {
                                tagsModels[i] = new TagsModel(R.drawable.ic_delete_button, itemsRBT[itemsSelectedRBT.get(i)]);
                                mRestrictByTagAdapter.addItem(tagsModels[i]);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog13, id) -> {
                        });
                dialogRestcictByTag = builderRBT.create();
                dialogRestcictByTag.show();
                break;
            case R.id.text_price_title:
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getActivity());
                alertdialogbuilder.setTitle("Select A Item ");
                alertdialogbuilder.setItems(mPriceList, (dialog, which) -> {
                    String selectedText = Arrays.asList(mPriceList).get(which);
                    mTextPriceTitle.setText(selectedText);
                });

                AlertDialog dialog = alertdialogbuilder.create();

                dialog.show();
                break;
            case R.id.image_price_add:
                if (mTextPriceTitle.getText().toString().equals("")) {
                    Toast.makeText(mContext, "please choose the Package Name", Toast.LENGTH_SHORT).show();
                } else {
                    if (mFieldPrice.getText().toString().equals("")) {
                        mFieldPrice.setText("0");
                    }
                    PricingModel pricingModel = new PricingModel(mTextPriceTitle.getText().toString(), mFieldPrice.getText().toString(), R.drawable.ic_remove);
                    mPricingUploadAdapter.addItem(pricingModel);
                    mTextPriceTitle.setText("");
                    mFieldPrice.setText("");
                    mLayoutNewPricing.setVisibility(View.GONE);
                }

                break;
            case R.id.button_open_copy_right:
                if (mLayoutExpandableCopyRight.isExpanded()) {
                    mLayoutExpandableCopyRight.collapse();
                } else {
                    mLayoutExpandableCopyRight.expand();
                }
                rotateArrow(TYPE_COPY_RIGHT, !mLayoutExpandableCopyRight.isExpanded());
                break;
            case R.id.button_open_shot_type:
                if (mLayoutExpandableShotType.isExpanded()) {
                    mLayoutExpandableShotType.collapse();
                } else {
                    mLayoutExpandableShotType.expand();
                }
                rotateArrow(TYPE_SHOT_TYPE, !mLayoutExpandableShotType.isExpanded());
                break;
            case R.id.button_open_stream_type:
                if (mLayoutExpandableStreamType.isExpanded()) {
                    mLayoutExpandableStreamType.collapse();
                } else {
                    mLayoutExpandableStreamType.expand();
                }
                rotateArrow(TYPE_STREAM_TYPE, !mLayoutExpandableStreamType.isExpanded());
                break;
            case R.id.button_stream_source:
                if (mLayoutExpandableStreamSource.isExpanded()) {
                    mLayoutExpandableStreamSource.collapse();
                } else {
                    mLayoutExpandableStreamSource.expand();
                }
                rotateArrow(TYPE_STREAM_SOURCE, !mLayoutExpandableStreamSource.isExpanded());
                break;
//            case R.id.image_stream_option:
//                if (mLayoutExpandableStreamOption.isExpanded()) {
//                    mLayoutExpandableStreamOption.collapse();
//                } else {
//                    mLayoutExpandableStreamOption.expand();
//                }
//                rotateArrow(TYPE_STREAM_OPTION, !mLayoutExpandableStreamOption.isExpanded());
//                break;
            case R.id.image_location:

//                Intent intent = new Intent(mContext, PlacesPluginActivity.class);
//                intent.putExtra("location", mFieldLocation.getText().toString());
//                mContext.startActivity(intent);
                break;
//            case R.id.image_time_date:
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(getActivity().getFragmentManager(), "Date Picker");
//                break;
            case R.id.button_upload:
//                StreamModel streamModel = new StreamModel(mFieldTitle.getText().toString(), mFieldTimeDate.getText().toString(), mFieldDescription.getText().toString(),"Jhon", "Williams", false);
//                try {
//                    EventBus.getDefault().post(new SendStreamModel(streamModel));
//                }catch (Exception e){
//                    EventBus.getDefault().post(new SendStreamModel(streamModel));
//                }
                break;
            case R.id.image_time_zone:
                if (mLayoutExpandableTimezoneList.isExpanded()) {
                    mLayoutExpandableTimezoneList.collapse();
                } else {
                    mLayoutExpandableTimezoneList.expand();
                }

                rotateArrow(TYPE_TIMEZONE, !mLayoutExpandableTimezoneList.isExpanded());
                break;
            case R.id.image_time_zone_advanced:
                if (mLayoutExpandableTimezoneAdvancedList.isExpanded()) {
                    mLayoutExpandableTimezoneAdvancedList.collapse();
                } else {
                    mLayoutExpandableTimezoneAdvancedList.expand();
                }

                rotateArrow(TYPE_TIMEZONE_ADVANCED, !mLayoutExpandableTimezoneAdvancedList.isExpanded());
                break;
            case R.id.button_upload_file:
                startVideoPicker();
                requestStoragePermission();
                break;
            case R.id.clear_upload_files: {
                clearUploadFiles();
                mLayoutUploadDevice.setVisibility(View.VISIBLE);
                break;

            }
            case R.id.field_time_date:
                DialogFragment newFragment = new DatePickerCreateFragment();
                newFragment.show(getActivity().getFragmentManager(), "Date Picker");
                break;
            case R.id.field_time:
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, hourOfDay, minutes) ->
                        mFieldTime.setText(String.format("%02d:%02d", hourOfDay, minutes)),
                        currentHour, currentMinute, true);

                timePickerDialog.show();
                break;
            case R.id.field_location:
                if (mGeolocationLat == DEFAULT_LOCATION_LAT
                        && mGeoLocationLng == DEFAULT_LOCATION_LNG) {
                    PlacesPluginActivity.startForResult(CreateEventFragment.this, REQUEST_LOCATION);
                } else {
                    PlacesPluginActivity.startForResult(
                            CreateEventFragment.this,
                            mGeolocationLat,
                            mGeoLocationLng,
                            REQUEST_LOCATION);
                }
                break;
            case R.id.field_enter_select:
                if (mGeolocationLatAdvanced == DEFAULT_LOCATION_LAT
                        && mGeoLocationLngAdvanced == DEFAULT_LOCATION_LNG) {
                    PlacesPluginActivity.startForResult(CreateEventFragment.this, REQUEST_LOCATION_ADVANCED);
                } else {
                    PlacesPluginActivity.startForResult(
                            CreateEventFragment.this,
                            mGeolocationLatAdvanced,
                            mGeoLocationLngAdvanced,
                            REQUEST_LOCATION_ADVANCED);
                }
                break;
        }
    }

    private void processUploadVideoResult(int requestCode, int resultCode) {
        if (requestCode != UploadActivity.REQUEST_CODE_UPLOAD) {
            return;
        }

        if (resultCode == RESULT_OK) {
            postPath = null;
            mUploadFile.setVisibility(View.GONE);
            mClearUploadFiles.setVisibility(View.GONE);
        } else {
            setVideoView();
        }
    }

    private void setVideoView() {
        if (postPath == null) {
            return;
        }
        mLayoutUploadDevice.setVisibility(View.GONE);
        mUploadFile.setVisibility(View.VISIBLE);
        mUploadFile.setVideoPath(postPath);
        mUploadFile.seekTo(1);

    }

    private void startVideoPicker() {
        Intent pickVideoIntent1 = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickVideoIntent1, REQUEST_PICK_VIDEO);
    }



    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());

        //If permission is granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Displaying a toast
            //     Toast.makeText(mContext, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
        } else {
            //Displaying another toast if permission is not granted
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        processUploadVideoResult(requestCode, resultCode);
        if (data != null && requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {
            String location = data.getStringExtra(PlacesPluginActivity.LOCATION_KEY);
            double lat = data.getDoubleExtra(PlacesPluginActivity.LOCATION_LAT_KEY, 0);
            double lng = data.getDoubleExtra(PlacesPluginActivity.LOCATION_LNG_KEY, 0);
            if (location != null && !location.isEmpty()) {
                mFieldLocation.setText(location);
                mGeolocationLat = lat;
                mGeoLocationLng = lng;
                if (mapboxMap != null) {
                    MapBoxUtil.putMarkerOnMap(lat, lng, "current position", mapboxMap);
                    MapBoxUtil.setCameraPosition(mapboxMap, lat, lng, 2.0);
                }
            }
        } else if (data != null && requestCode == REQUEST_LOCATION_ADVANCED && resultCode == RESULT_OK) {
            String location = data.getStringExtra(PlacesPluginActivity.LOCATION_KEY);
            double lat = data.getDoubleExtra(PlacesPluginActivity.LOCATION_LAT_KEY, 0);
            double lng = data.getDoubleExtra(PlacesPluginActivity.LOCATION_LNG_KEY, 0);
            if (location != null && !location.isEmpty()) {
                mFieldEnterSelected.setText(location);
                mGeolocationLatAdvanced = lat;
                mGeoLocationLngAdvanced = lng;
                if (mapboxMapAdvanced != null) {
                    MapBoxUtil.putMarkerOnMap(lat, lng, "current position", mapboxMapAdvanced);
                    MapBoxUtil.setCameraPosition(mapboxMapAdvanced, lat, lng, 2.0);
                }
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_VIDEO) {
                if (data != null) {
                    if (EasyPermissions.hasPermissions(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Get the Image from data
                        selectedVideo = data.getData();
                        String[] filePathColumn = {MediaStore.Video.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        mediaPath = cursor.getString(columnIndex);
                        // Set the Image in ImageView for Previewing the Media
                        //  imageView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                        cursor.close();
                        postPath = mediaPath;
                        // mFileName.setVisibility(View.VISIBLE);
                        // mFileName.setText(postPath);
                        setVideoView();
                        showClearButton();
                    } else {
                        EasyPermissions.requestPermissions(
                                getActivity(),
                                "This app needs access to your file storage so that it can read video",
                                READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(mContext, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
            }

            if (mUploadFile.getVisibility() == View.VISIBLE) {
                mUploadFile.seekTo(1);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

        private void initializePlayer (Uri uri){
            // Show the "Buffering..." message while the video loads.
            if (uri != null) {
                mVideoThumbnail.setVideoURI(uri);
            }
            // Listener for onPrepared() event (runs after the media is prepared).
            mVideoThumbnail.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {

                            // Hide buffering message.

                            // Restore saved position, if available.
                            if (mCurrentPosition > 0) {
                                mVideoThumbnail.seekTo(mCurrentPosition);
                            } else {
                                // Skipping to 1 shows the first frame of the video.
                                mVideoThumbnail.seekTo(1);
                            }

                            // Start playing!
                            mVideoThumbnail.start();
                        }
                    });

            // Listener for onCompletion() event (runs after media has finished
            // playing).
            mVideoThumbnail.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
//                        Toast.makeText(mContext,
//                                "error",
//                                Toast.LENGTH_SHORT).show();
//
//                        // Return the video position to the start.
                            mVideoThumbnail.seekTo(mVideoThumbnail.getCurrentPosition());
                        }
                    });
        }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    private void rotateArrow(int type, boolean expand) {
        switch (type) {
            case TYPE_SHARE_WITH_INDIVIDUALS:
                break;
            case TYPE_COPY_RIGHT:
                ObjectAnimator.ofFloat(mButtonCopyRight, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_STREAM_OPTION:
                ObjectAnimator.ofFloat(mImageStreamOption, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_TIMEZONE:
                ObjectAnimator.ofFloat(mImageTimeZone, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_TIMEZONE_ADVANCED:
                ObjectAnimator.ofFloat(mImageTimeZoneAdvanced, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_SHOT_TYPE:
                ObjectAnimator.ofFloat(mButtonShotType, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_STREAM_TYPE:
                ObjectAnimator.ofFloat(mButtonStreamType, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_STREAM_SOURCE:
                ObjectAnimator.ofFloat(mButtonStreamSource, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
        }
    }

    @Override
    public void onSelected(int type, String s) {
        rotateArrow(type, false);
        ShareWithIndividualsModel shareWithIndividualsModel = new ShareWithIndividualsModel(R.drawable.ic_user_icon_new, "", R.drawable.ic_delete_button);
        switch (type) {
            case TYPE_SHARE_WITH_INDIVIDUALS:
                mLayoutExpandableShareWithIndividuals.collapse();
                shareWithIndividualsModel.setUserEmail(s);
                mFieldShareWithIndividuals.setText("");
                mShareWithIndividualsAdapter.addItem(shareWithIndividualsModel);
                break;
            case TYPE_COPY_RIGHT:
                mLayoutExpandableCopyRight.collapse();
                mEditCopyRight.setText(s);
                if (s.equals(mCopyRight[mCopyRight.length - 1])) {
                    mLayoutCopyRight_Other.setVisibility(View.VISIBLE);
                } else {
                    mLayoutCopyRight_Other.setVisibility(View.GONE);
                }
                break;
            case TYPE_STREAM_OPTION:
                mLayoutExpandableStreamOption.collapse();
                mEditStreamOption.setText(s);
                break;
            case TYPE_SHOT_TYPE:
                mLayoutExpandableShotType.collapse();
                mFieldShotType.setText(s);
                break;
            case TYPE_STREAM_TYPE:
                mLayoutExpandableStreamType.collapse();
                mFieldStreamType.setText(s);
                break;
            case TYPE_STREAM_SOURCE:
                mLayoutExpandableStreamSource.collapse();
                mFieldStreamSource.setText(s);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d("TESTING", "isVisibleToUser " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d("TESTING", "hidden " + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void sendCount(boolean isNull) {
        RelativeLayout.LayoutParams paramsEditText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsEditText.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsEditText.setMarginStart(12);
        paramsEditText.setMarginEnd(12);
        RelativeLayout.LayoutParams paramsChanged = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsChanged.addRule(RelativeLayout.BELOW, R.id.list_share_with_individuals_with_icons);
        paramsChanged.setMarginStart(12);
        paramsChanged.setMarginEnd(12);
        if (isNull) {
            mFieldShareWithIndividuals.setLayoutParams(paramsEditText);
        } else {
            mFieldShareWithIndividuals.setLayoutParams(paramsChanged);
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
            case TYPE_TIMEZONE_ADVANCED:
                mLayoutExpandableTimezoneAdvancedList.collapse();
                mTextSelectedTimezoneAdvancedList.setText(timezone);
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkbox_apply_restrictions:
                if (!mCheckboxAnyoneCanPurchase.isChecked()){
                    mCheckboxApplyRestrictions.setChecked(true);
                } else if (mCheckboxApplyRestrictions.isChecked()){
                    mCheckboxAnyoneCanPurchase.setChecked(false);
                }else {
                    mCheckboxAnyoneCanPurchase.setChecked(true);
                }
                break;
            case R.id.checkbox_anyone_can_purchase:
                if (!mCheckboxApplyRestrictions.isChecked()){
                    mCheckboxAnyoneCanPurchase.setChecked(true);
                } else if (mCheckboxAnyoneCanPurchase.isChecked()){
                    mCheckboxApplyRestrictions.setChecked(false);
                }else {
                    mCheckboxApplyRestrictions.setChecked(true);
                }
                break;
        }
        if (mCheckboxApplyRestrictions.isChecked()){
            mLayoutAdvanced.setVisibility(View.VISIBLE);
        }else {
            mLayoutAdvanced.setVisibility(View.GONE);
        }
    }
}
