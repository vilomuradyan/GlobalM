package com.globalm.platform.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.activities.GoProMediaBrowserActivity;
import com.globalm.platform.activities.PlacesPluginActivity;
import com.globalm.platform.activities.UploadActivity;
import com.globalm.platform.adapters.DateTimeZoneAdapter;
import com.globalm.platform.adapters.PricingUploadAdapter;
import com.globalm.platform.adapters.ShareCopyRightAdapter;
import com.globalm.platform.adapters.ShareWithIndividualsAdapter;
import com.globalm.platform.adapters.TagsAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.listeners.OnDateTimeZoneSelectedListener;
import com.globalm.platform.listeners.OnShareWithIndividualsListener;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.PricingModel;
import com.globalm.platform.models.ShareWithIndividualsModel;
import com.globalm.platform.models.Tag;
import com.globalm.platform.models.TagsModel;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.MapBoxUtil;
import com.globalm.platform.utils.Utils;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.globalm.platform.utils.Utils.getTimeZoneList;

public class UploadContentFragment extends Fragment implements View.OnClickListener, OnShareWithIndividualsListener, EasyPermissions.PermissionCallbacks, ShareWithIndividualsAdapter.OnCountListener, OnDateTimeZoneSelectedListener,CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_VIDEO = 2;
    private static final int REQUEST_GOPRO_BROWSE_MEDIA = 155;
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int REQUEST_STORAGE = 112;
    private final int TYPE_TIMEZONE_ADVANCED = 5;
    private static final int READ_REQUEST_CODE = 20000;
    private static final String SHARED_FILE_TEMP_NAME = "SHARED_TEMP_FILE";
    private static final double DEFAULT_LOCATION_LAT = 2.008751930035146;
    private static final double DEFAULT_LOCATION_LNG = 46.993565237097044;

    public static final int REQUEST_UPLOAD_DEVICE = 1;
    public static final int REQUEST_LOCATION = 20;
    public static final int REQUEST_LOCATION_ADVANCED = 10;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    public static final String KEY_GOPRO_CAMERA_FILE_PATH = "KEY_GOPRO_CAMERA_FILE_PATH";
    public static final String TAG = UploadContentFragment.class.getName();

    public final int TYPE_COPY_RIGHT = 1;
    public final int TYPE_STREAM_OPTION = 21;
    private final int TYPE_SHARE_WITH_INDIVIDUALS = 0;
    private Context mContext;
    private ScrollView mScrollView;
    private ImageView mButtonLocation;
    private EditText mFieldLocation;
    private TextView mFieldTimeDate;
    private ExpandableRelativeLayout mLayoutExpandableStreamOption;
    private ExpandableRelativeLayout mLayoutExpandableCopyRight;
    private ExpandableRelativeLayout mLayoutExpandableTimezoneAdvancedList;
    private ImageView mButtonCopyRight;
    private ImageView mImageStreamOption;
    private TextView mEditCopyRight;
    private ShareCopyRightAdapter mShareWithIndividualsSearchAdapter;
    private String[] mShareWithIndividuals = {"jack@knowlocker.com", "Alisa@knowlocker.com", "Arthur@knowlocker.com"};
    private String[] mCopyRight = {"Pool", "Public", "GlobalM content",
            "Cleared", "Host broadcaster content", "Exclusive with conditions", "Exclusive for an MO", "Embargo with time / date stamp",
            "Embargo by country", "Broadcast only", "Digital only", "Other"};
    private String[] mStreamOption = {"GlobalM app"};
    private RecyclerView mListPricing;
    private PricingUploadAdapter mPricingUploadAdapter;
    private ArrayList<PricingModel> mPricingModels = new ArrayList<>();
    private TextView mFileName;
    private ImageView mButtonUploadDevice;
    private ImageView mButtonUploadCamera;
    private ImageView mClearUploadFiles;
    private TextView mUploadFromDevice;
    private Button mButtonUpload;
    private EditText mFieldTitle;
    private EditText mFieldSubtitle;
    private TextView mFieldTags;
    private double mGeolocationLat = DEFAULT_LOCATION_LAT;
    private double mGeoLocationLng = DEFAULT_LOCATION_LNG;
    private double mGeolocationLatAdvanced = DEFAULT_LOCATION_LAT;
    private double mGeoLocationLngAdvanced = DEFAULT_LOCATION_LNG;
    private String mediaPath;
    private String postPath;
    private Uri selectedVideo;
    private VideoView mUploadVideoView;
    private boolean isFileFromFilesDir = false;

    private RecyclerView mListTags;
    private TagsAdapter mTagsAdapter;
    private ArrayList<TagsModel> mTagsModels = new ArrayList<>();

    private RecyclerView mListCategories;
    private TagsAdapter mCategoriesAdapter;
    private ArrayList<TagsModel> mCategoriesModels = new ArrayList<>();

    private ShareWithIndividualsAdapter mShareWithIndividualsAdapter;
    private ArrayList<ShareWithIndividualsModel> mShareWithIndividualsModels = new ArrayList<>();
    private RelativeLayout mLayoutCopyRight_Other;

    private MapView mMapView;
    private TextView mTextAddNew;
    private LinearLayout mLayoutNewPricing;
    private ImageView mImagePriceAdd;
    private EditText mFieldPrice;
    private TextView mTextPriceTitle;
    private String[] mPriceList = new String[]{"Exclusive", "Pool Access"};
    private String[] mCategoryList = new String[]{"NEWS", "SPORTS"};
    private String[] mCategoryNews = new String[]{"Culture","Travel","Real","Nature","Food","Elections","EU"};
    private String[] mCategorySports = new String[]{"Tennis","Rugby","Hockey","Handball","Voleyball","Soccer","Aerobics"};
    private TextView mFieldTime;
    private RelativeLayout mLayoutTags;

    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private int currentHour;
    private int currentMinute;

    private RelativeLayout mLayoutCategory;
    private EditText mFieldShareWithIndividuals;
    ShareCopyRightAdapter ShareWithIndividualsAdapterWithOutIcon;
    private ExpandableRelativeLayout mLayoutExpandableShareWithIndividuals;
    private RecyclerView mListShareWithIndividuals;
    private TextView mTextCategory;
    private LinearLayout mLayoutUploadDevice;
    private AppCompatCheckBox mCheckboxApplyRestrictions;
    private AppCompatCheckBox mCheckboxAnyoneCanPurchase;
    private LinearLayout mLayoutAdvanced;
    private RecyclerView mListSpecificMediaOutlets;
    private TagsAdapter mSpecificMediaOutletsAdapter;
    private RelativeLayout mLayoutSpecificMediaOutlets;
    private RecyclerView mListRestrictByTag;
    private TagsAdapter mRestrictByTagAdapter;
    private RelativeLayout mLayoutRestrictByTag;
    private RecyclerView mListTimeZoneAdvanced;
    private ImageView mImageTimeZoneAdvanced;
    private TextView mTextSelectedTimezoneAdvancedList;
    private TextView mFieldTimeAdvanced;
    private TextView mFieldDate;
    private MapView mMapViewAdvanced;
    private TextView mFieldEnterSelected;

    @Nullable
    private MapboxMap mapboxMap;

    @Nullable
    private MapboxMap mapboxMapAdvanced;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload_content, container, false);
        mMapView = rootView.findViewById(R.id.map_view);
        mScrollView = rootView.findViewById(R.id.scroll_view);
        mButtonLocation = rootView.findViewById(R.id.image_location);
        mFieldLocation = rootView.findViewById(R.id.field_location);
        mFieldTimeDate = rootView.findViewById(R.id.field_time_date_upload);
        mFileName = rootView.findViewById(R.id.file_name);
        mButtonUploadDevice = rootView.findViewById(R.id.button_upload_device);
        mButtonUploadCamera = rootView.findViewById(R.id.button_upload_camera);
        mUploadFromDevice = rootView.findViewById(R.id.button_upload_from_device);
        mButtonUpload = rootView.findViewById(R.id.button_upload);
        mFieldTitle = rootView.findViewById(R.id.field_title);
        mFieldSubtitle = rootView.findViewById(R.id.field_subtitle);
//        mFieldTags = rootView.findViewById(R.id.field_tags_upload);
        mUploadVideoView = rootView.findViewById(R.id.upload_video_view);
        mClearUploadFiles = rootView.findViewById(R.id.clear_upload_files);
        mLayoutCopyRight_Other = rootView.findViewById(R.id.layout_copy_right_other);
        mTextAddNew = rootView.findViewById(R.id.text_add_new);
        mLayoutNewPricing = rootView.findViewById(R.id.layout_new_pricing);
        mImagePriceAdd = rootView.findViewById(R.id.image_price_add);
        mFieldPrice = rootView.findViewById(R.id.field_price);
        mTextPriceTitle = rootView.findViewById(R.id.text_price_title);
        mFieldTime = rootView.findViewById(R.id.field_time);
        mLayoutExpandableCopyRight = rootView.findViewById(R.id.layout_expandable_copy_right);
//        mLayoutExpandableStreamOption = rootView.findViewById(R.id.layout_expandable_streaming_option);
        mButtonCopyRight = rootView.findViewById(R.id.button_open_copy_right);
        mEditCopyRight = rootView.findViewById(R.id.field_copy_right);
        mListTags = rootView.findViewById(R.id.list_tags);
        mFieldTags = rootView.findViewById(R.id.field_tags);
        mLayoutTags = rootView.findViewById(R.id.layout_tags);
        mLayoutCategory = rootView.findViewById(R.id.layout_category);
        mFieldShareWithIndividuals = rootView.findViewById(R.id.field_share_with_individuals);
        mLayoutExpandableShareWithIndividuals = rootView.findViewById(R.id.layout_expandable_share_with_individuals);
        mListShareWithIndividuals = rootView.findViewById(R.id.list_share_with_individuals_with_icons);
        RecyclerView ListShareWithIndividuals = rootView.findViewById(R.id.list_share_with_individuals);
        ListShareWithIndividuals.setLayoutManager(new LinearLayoutManager(getActivity()));
        ShareWithIndividualsAdapterWithOutIcon = new ShareCopyRightAdapter(TYPE_SHARE_WITH_INDIVIDUALS, mShareWithIndividuals, this);
        ListShareWithIndividuals.setAdapter(ShareWithIndividualsAdapterWithOutIcon);
        mTextCategory = rootView.findViewById(R.id.open_category);
        mLayoutUploadDevice = rootView.findViewById(R.id.layout_upload_from_device);
        mCheckboxAnyoneCanPurchase = rootView.findViewById(R.id.checkbox_anyone_can_purchase);
        mCheckboxApplyRestrictions = rootView.findViewById(R.id.checkbox_apply_restrictions);
        mLayoutAdvanced = rootView.findViewById(R.id.layout_advanced);
        mListSpecificMediaOutlets = rootView.findViewById(R.id.list_specific_media_outlets);
        mLayoutSpecificMediaOutlets = rootView.findViewById(R.id.layout_exclude_specific_media_outlets);
        mListRestrictByTag = rootView.findViewById(R.id.list_restrict_by_tag);
        mLayoutRestrictByTag = rootView.findViewById(R.id.layout_restrict_by_tag);
        mLayoutExpandableTimezoneAdvancedList = rootView.findViewById(R.id.layout_expandable_timezone_advancedlist);
        mListTimeZoneAdvanced = rootView.findViewById(R.id.list_timezone_advanced);
        mImageTimeZoneAdvanced = rootView.findViewById(R.id.image_time_zone_advanced);
        mTextSelectedTimezoneAdvancedList = rootView.findViewById(R.id.field_time_zone_advanced);
        mFieldTimeAdvanced = rootView.findViewById(R.id.field_time_advanced);
        mFieldDate = rootView.findViewById(R.id.field_time_date);
        mMapViewAdvanced = rootView.findViewById(R.id.map_view_advanced);
        mFieldEnterSelected = rootView.findViewById(R.id.field_enter_select);
        SpannableString content = new SpannableString("Add New");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mTextAddNew.setText(content);

//        mButtonShareWithIndividuals.setOnClickListener(this);
        mFieldTimeDate.setOnClickListener(this);
        mButtonLocation.setOnClickListener(this);
        mButtonUploadDevice.setOnClickListener(this);
        mButtonUploadCamera.setOnClickListener(this);
        mUploadFromDevice.setOnClickListener(this);
        mButtonUpload.setOnClickListener(this);
        mClearUploadFiles.setOnClickListener(this);
        mImageTimeZoneAdvanced.setOnClickListener(this);
        mTextAddNew.setOnClickListener(this);
        mImagePriceAdd.setOnClickListener(this);
        mTextPriceTitle.setOnClickListener(this);
        mFieldTime.setOnClickListener(this);
        mListTags.setOnClickListener(this);
        mLayoutTags.setOnClickListener(this);
        mLayoutCategory.setOnClickListener(this);
        mTextCategory.setOnClickListener(this);
        mLayoutRestrictByTag.setOnClickListener(this);
        mLayoutSpecificMediaOutlets.setOnClickListener(this);
        mFieldTimeAdvanced.setOnClickListener(this);
        mFieldDate.setOnClickListener(this);
        mFieldEnterSelected.setOnClickListener(this);
        mCheckboxAnyoneCanPurchase.setOnCheckedChangeListener(this);
        mCheckboxApplyRestrictions.setOnCheckedChangeListener(this);

        mLayoutExpandableCopyRight = rootView.findViewById(R.id.layout_expandable_copy_right);
//        mLayoutExpandableStreamOption = rootView.findViewById(R.id.layout_expandable_streaming_option);
        mButtonCopyRight = rootView.findViewById(R.id.button_open_copy_right);
        mEditCopyRight = rootView.findViewById(R.id.field_copy_right);
//        mImageStreamOption = rootView.findViewById(R.id.image_stream_option);
        //mImageStreamOption.setOnClickListener(this);
        mButtonCopyRight.setOnClickListener(this);
        ShareCopyRightAdapter adapterCopyRight = new ShareCopyRightAdapter(TYPE_COPY_RIGHT, mCopyRight, this);
        ShareCopyRightAdapter adapterStream = new ShareCopyRightAdapter(TYPE_STREAM_OPTION, mStreamOption, this);
        RecyclerView mListCopyRight = rootView.findViewById(R.id.list_copy_right);
        mListCopyRight.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListCopyRight.setAdapter(adapterCopyRight);
//        RecyclerView mListStreamOption = rootView.findViewById(R.id.list_streaming_option);
        mShareWithIndividualsSearchAdapter = new ShareCopyRightAdapter(TYPE_SHARE_WITH_INDIVIDUALS, mShareWithIndividuals, this);
//        mListStreamOption.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mListStreamOption.setAdapter(adapterStream);


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


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mListPricing = rootView.findViewById(R.id.list_pricing);
        mListPricing.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPricingUploadAdapter = new PricingUploadAdapter(getActivity());
        mListPricing.setAdapter(mPricingUploadAdapter);
        mPricingUploadAdapter.setData(mPricingModels);
        initList();

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PICK_VIDEO);
            }
            requestStoragePermission();
        }

        mListTags.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mTagsAdapter = new TagsAdapter(getActivity());
        mListTags.setAdapter(mTagsAdapter);
        initDummyListTags();

        mListCategories = rootView.findViewById(R.id.list_categories);
        mListCategories.setOnClickListener(this);
        mListCategories.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mCategoriesAdapter = new TagsAdapter(getActivity());
        mListCategories.setAdapter(mCategoriesAdapter);
        initDummyListCategories();

        mShareWithIndividualsAdapter = new ShareWithIndividualsAdapter(getActivity(), this);
        mListShareWithIndividuals.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mListShareWithIndividuals.setAdapter(mShareWithIndividualsAdapter);
        initDummyListShareWithIndividuals();
        mLayoutCopyRight_Other.setVisibility(View.GONE);

        mLayoutNewPricing.setVisibility(View.GONE);

        mFieldShareWithIndividuals.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")){
                    if (!mFieldShareWithIndividuals.getText().toString().trim().equals("")){
                        ShareWithIndividualsModel shareWithIndividualsModel = new ShareWithIndividualsModel(R.drawable.ic_user_icon_new, "", R.drawable.ic_delete_button);
                        shareWithIndividualsModel.setUserEmail(mFieldShareWithIndividuals.getText().toString());
                        mShareWithIndividualsAdapter.addItem(shareWithIndividualsModel);
                        mFieldShareWithIndividuals.setText("");
                    }else {
                        mFieldShareWithIndividuals.setText("");
                    }
                }
//                filter(s.toString());
//                mLayoutExpandableShareWithIndividuals.expand();
            }
        });
        mFieldShareWithIndividuals.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus){
//                mLayoutExpandableShareWithIndividuals.expand();
//            }else {
//                mLayoutExpandableShareWithIndividuals.collapse();
//
//            }
        });

        mListSpecificMediaOutlets.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mSpecificMediaOutletsAdapter = new TagsAdapter(getActivity());
        mListSpecificMediaOutlets.setAdapter(mSpecificMediaOutletsAdapter);
        mListRestrictByTag.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRestrictByTagAdapter = new TagsAdapter(getActivity());
        mListRestrictByTag.setAdapter(mRestrictByTagAdapter);
        String[] timezoneList = getTimeZoneList();
        DateTimeZoneAdapter timeZoneAdvancedAdapter = new DateTimeZoneAdapter(TYPE_TIMEZONE_ADVANCED, timezoneList, this);
        mListTimeZoneAdvanced.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListTimeZoneAdvanced.setAdapter(timeZoneAdvancedAdapter);

        return rootView;
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

    private void initDummyListShareWithIndividuals(){
      //  mShareWithIndividualsModels.add(new ShareWithIndividualsModel(R.drawable.ic_user_icon_new, "jack@knowlocker.com", R.drawable.ic_delete_button));
        mShareWithIndividualsAdapter.setData(mShareWithIndividualsModels);
    }

    private void initDummyListTags() {
        mTagsAdapter.setData(mTagsModels);
    }

    private void initDummyListCategories(){
        // mCategoriesModels.add(new TagsModel(R.drawable.ic_delete_button, "Category 1"));
        mCategoriesAdapter.setData(mCategoriesModels);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }




    @SuppressLint("StaticFieldLeak")
    public void setUploadData(Item item) {
        mFieldTitle.setText(item.getTitle());
        mFieldSubtitle.setText(item.getSubtitle());
        mFieldLocation.setText(item.getGeoLocation().getName());
        ArrayList<TagsModel> listTags = new ArrayList<>();
        for (int i = 0; i < item.getTags().size(); i++) {
            listTags.add(new TagsModel(R.drawable.ic_delete_button, item.getTags().get(i).getName()));
        }
        if (listTags.size() > 0){
            mTagsAdapter.setData(listTags);
        }

        if (item.getThumb() != null && !item.getThumb().equals("")){
            mUploadVideoView.setVisibility(View.VISIBLE);
            mLayoutUploadDevice.setVisibility(View.GONE);
            mClearUploadFiles.setVisibility(View.VISIBLE);

            new AsyncTask<Void, Drawable, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Bitmap  bitmap =  Picasso.get().load(Uri.parse(item.getThumb())).get();
                        Drawable drawable = new BitmapDrawable(bitmap);
                        publishProgress(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onProgressUpdate(Drawable... values) {
                    super.onProgressUpdate(values);
                    mUploadVideoView.setBackground(values[0]);
                }
            }.execute();
        }

        if (item.getPricingOptions().size() > 0) {
            ArrayList<PricingModel> listPrice = new ArrayList<>();
            for (int i = 0; i < item.getPricingOptions().size(); i++) {
                listPrice.add(new PricingModel(item.getPricingOptions().get(i).getType(), String.valueOf(item.getPricingOptions().get(i).getPrice()), R.drawable.ic_remove));
            }
            mPricingUploadAdapter.setData(listPrice);
        }

        if (item.getCreatedAt() != null && !item.getCreatedAt().equals("")){
            String date = item.getCreatedAt().substring(0,item.getCreatedAt().indexOf(" "));
            mFieldTimeDate.setText(date);
            String time = item.getCreatedAt().substring(date.length() + 1);
            mFieldTime.setText(time);
        }

        if (item.getGeoLocation().getLocation() != null && (item.getGeoLocation().getLocation().getLat() != 0 || item.getGeoLocation().getLocation().getLon() != 0)){
            double lat = item.getGeoLocation().getLocation().getLat();
            double lng = item.getGeoLocation().getLocation().getLon();

            new Handler().postDelayed(() -> {
                if (mapboxMap != null) {
                    MapBoxUtil.putMarkerOnMap(lat, lng, "current position",mapboxMap);
                    MapBoxUtil.setCameraPosition(mapboxMap, lat, lng, 4.0);
                }
            },1000);


        }
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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapViewAdvanced.onResume();
        mUploadVideoView.seekTo(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mUploadVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (postPath != null) {
            Utils.clearFileFromFilesDir(
                    isFileFromFilesDir,
                    new File(postPath));
            postPath = null;
        }
    }

    @Override
    public void onStop() {
        mUploadVideoView.stopPlayback();
        super.onStop();
    }

    private void initList() {
//        mPricingModels.add(new PricingModel("Exclusive", "300", R.drawable.ic_remove));
//        mPricingModels.add(new PricingModel("Pool Access", "150", R.drawable.ic_remove));
        mPricingUploadAdapter.setData(mPricingModels);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_add_new:
                mLayoutNewPricing.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_tags:
                Dialog dialogTag;
                final String[] tags = {" European Union", " Economics", " President", " War", " Disaster", " Weapon", " 2019"};
                final ArrayList<Integer> itemsSelected = new ArrayList<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Tags ");
                builder.setMultiChoiceItems(tags, null,
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
                                tagsModels[i] = new TagsModel(R.drawable.ic_delete_button,tags[itemsSelected.get(i)]);
                                mTagsAdapter.addItem(tagsModels[i]);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog13, id) -> {
                        });
                dialogTag = builder.create();
                dialogTag.show();

                break;
            case R.id.open_category:
                showCategoryDialog();
                break;
            case R.id.layout_category:
                showCategoryDialog();
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
                if (mTextPriceTitle.getText().toString().equals("")){
                    Toast.makeText(mContext, "please choose the Package Name", Toast.LENGTH_SHORT).show();
                }else {
                    if (mFieldPrice.getText().toString().equals("")){
                        mFieldPrice.setText("0");
                    }
                    PricingModel pricingModel = new PricingModel(mTextPriceTitle.getText().toString(),mFieldPrice.getText().toString(),R.drawable.ic_remove);
                    mPricingUploadAdapter.addItem(pricingModel);
                    mTextPriceTitle.setText("");
                    mFieldPrice.setText("");
                    mLayoutNewPricing.setVisibility(View.GONE);
                }

                break;
            case R.id.image_location:
                if (mGeolocationLat == DEFAULT_LOCATION_LAT
                        && mGeoLocationLng == DEFAULT_LOCATION_LNG) {
                    PlacesPluginActivity.startForResult(UploadContentFragment.this, REQUEST_LOCATION);
                } else {
                    PlacesPluginActivity.startForResult(
                            UploadContentFragment.this,
                            mGeolocationLat,
                            mGeoLocationLng,
                            REQUEST_LOCATION);
                }
                break;
            case R.id.field_time_date_upload:
                DialogFragment newFragment = new DatePickerUploadFragment();
                newFragment.show(getActivity().getFragmentManager(), "Date Picker");
                break;
            case R.id.field_time_date:
                DialogFragment newFragmentAdvanced = new DatePickerUploadAdvancedFragment();
                newFragmentAdvanced.show(getActivity().getFragmentManager(), "Date Picker");
                break;
            case R.id.button_open_copy_right:
                if (mLayoutExpandableCopyRight.isExpanded()) {
                    mLayoutExpandableCopyRight.collapse();
                } else {
                    mLayoutExpandableCopyRight.expand();
                }
                rotateArrow(TYPE_COPY_RIGHT, !mLayoutExpandableCopyRight.isExpanded());
                break;
            case R.id.clear_upload_files: {
                clearUploadFiles();
                mLayoutUploadDevice.setVisibility(View.VISIBLE);
                break;
            }
//            case R.id.image_stream_option:
//                if (mLayoutExpandableStreamOption.isExpanded()) {
//                    mLayoutExpandableStreamOption.collapse();
//                } else {
//                    mLayoutExpandableStreamOption.expand();
//                }
//                rotateArrow(TYPE_STREAM_OPTION, !mLayoutExpandableStreamOption.isExpanded());
//                break;
            case R.id.button_upload_device:
                startVideoPicker();
                requestStoragePermission();
                break;
            case R.id.button_upload_camera: {
                GoProMediaBrowserActivity.startForResult(this, REQUEST_GOPRO_BROWSE_MEDIA);
                break;
            }
            case R.id.button_upload_from_device:
                startVideoPicker();
                break;
            case R.id.button_upload:

//                RequestManager.getInstance().createContent(mFieldTitle.getText().toString(),mFieldSubtitle.getText().toString(),"40.74894149554006","-73.98615270853043",new CallbackListener<Response<Item>>() {
//                    @Override
//                    public void onSuccess(Response<Item> response) {
//                        Log.d("TESTING", "create Content POST access");
//                        Log.d("TESTING", "title "+  response.body().getTitle());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable error) {
//                        Log.d("TESTING", "createContent POST failure");
//                    }
//                });

                if (mFieldTitle.getText().toString().isEmpty()) {
                    showMessage(getString(R.string.title_is_required)).show();
                    return;
                }

                uploadFile();

                mFieldSubtitle.setText("");
                mFieldTitle.setText("");
                mFieldTimeDate.setText("");
                mFieldLocation.setText("");
                mFieldTags.setText("");

                mEditCopyRight.setText("");
                mFileName.setVisibility(View.GONE);
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
            case R.id.image_time_zone_advanced:
                if (mLayoutExpandableTimezoneAdvancedList.isExpanded()) {
                    mLayoutExpandableTimezoneAdvancedList.collapse();
                } else {
                    mLayoutExpandableTimezoneAdvancedList.expand();
                }

                rotateArrow(TYPE_TIMEZONE_ADVANCED, !mLayoutExpandableTimezoneAdvancedList.isExpanded());
                break;
            case R.id.field_time_advanced:
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, hourOfDay, minutes) ->
                        mFieldTimeAdvanced.setText(String.format("%02d:%02d", hourOfDay, minutes)),
                        currentHour, currentMinute, true);

                timePickerDialog.show();
                break;
            case R.id.field_enter_select:
                if (mGeolocationLatAdvanced == DEFAULT_LOCATION_LAT
                        && mGeoLocationLngAdvanced == DEFAULT_LOCATION_LNG) {
                    PlacesPluginActivity.startForResult(UploadContentFragment.this, REQUEST_LOCATION_ADVANCED);
                } else {
                    PlacesPluginActivity.startForResult(
                            UploadContentFragment.this,
                            mGeolocationLatAdvanced,
                            mGeoLocationLngAdvanced,
                            REQUEST_LOCATION_ADVANCED);
                }
                break;
        }
    }

    private void startVideoPicker() {
        //                Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
//                upload.setType("*/*");
//                startActivityForResult(upload, REQUEST_UPLOAD_DEVICE);
//
//                Intent galleryIntent1 = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent1, REQUEST_PICK_VIDEO);

//                Intent gallery1 = new Intent();
//                gallery1.setType("image/*");
//                gallery1.setAction(Intent.ACTION_GET_CONTENT);
////                startActivityForResult(Intent.createChooser(gallery1, "Select Image From Gallery"), 1);
//
        Intent pickVideoIntent1 = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickVideoIntent1, REQUEST_PICK_VIDEO);
    }

    private void rotateArrow(int type, boolean expand) {
        switch (type) {
            case TYPE_SHARE_WITH_INDIVIDUALS:
                //ObjectAnimator.ofFloat(mButtonShareWithIndividuals, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                //TODO not initialized mButtonShareWithIndividuals was removed
                break;
            case TYPE_COPY_RIGHT:
                ObjectAnimator.ofFloat(mButtonCopyRight, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;
            case TYPE_STREAM_OPTION:
                ObjectAnimator.ofFloat(mImageStreamOption, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
                break;case TYPE_TIMEZONE_ADVANCED:
                ObjectAnimator.ofFloat(mImageTimeZoneAdvanced, "rotation", expand ? 0 : 180, expand ? 180 : 0).start();
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
                break;
        }
    }

    public void setSharedFileUri(Uri uri) {
        if (getContext() == null) {
            showMessage(getString(R.string.an_error_has_occured)).show();
            return;
        }

        Utils.copyFileToDir(
                uri,
                getFilesDirPath(),
                getContext(),
                SHARED_FILE_TEMP_NAME,
                file -> {
                    if (file == null) {
                        showMessage(getString(R.string.an_error_has_occured)).show();
                        return;
                    }

                    String filePath = file.getAbsolutePath();
                    isFileFromFilesDir = true;
                    mediaPath = filePath;
                    postPath = mediaPath;
                    setVideoView();
                    showClearButton();
                });
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
                    MapBoxUtil.putMarkerOnMap(lat, lng, "current position",  mapboxMap);
                    MapBoxUtil.setCameraPosition(mapboxMap, lat, lng, 2.0);
                }
            }

            if (mUploadVideoView.getVisibility() == View.VISIBLE){
                mUploadVideoView.seekTo(1);
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
                        isFileFromFilesDir = false;
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
            } else if (requestCode == REQUEST_GOPRO_BROWSE_MEDIA) {
                if (data != null) {
                    isFileFromFilesDir = true;
                    mediaPath = data.getStringExtra(KEY_GOPRO_CAMERA_FILE_PATH);
                    postPath = mediaPath;
                    setVideoView();
                    showClearButton();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFilesDirPath() {
        return getContext().getFilesDir().getAbsolutePath();
    }

    private void clearUploadFiles() {
        postPath = null;
        mediaPath = null;
        mUploadVideoView.setVisibility(View.GONE);
        mClearUploadFiles.setVisibility(View.GONE);
    }

    private void showClearButton() {
        mClearUploadFiles.setVisibility(View.VISIBLE);
    }

    private Toast showMessage(String msg) {
        return Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
    }

    private void setVideoView() {
        if (postPath == null) {
            return;
        }
        mLayoutUploadDevice.setVisibility(View.GONE);
        mUploadVideoView.setVisibility(View.VISIBLE);
        mUploadVideoView.setVideoPath(postPath);
        mUploadVideoView.seekTo(1);

    }

    private void processUploadVideoResult(int requestCode, int resultCode) {
        if (requestCode != UploadActivity.REQUEST_CODE_UPLOAD) {
            return;
        }

        if (resultCode == RESULT_OK) {
            postPath = null;
            mUploadVideoView.setVisibility(View.GONE);
            mClearUploadFiles.setVisibility(View.GONE);
        } else {
            setVideoView();
        }
    }

    private void uploadFile() {
        if (postPath == null || postPath.equals("")) {
            Toast.makeText(mContext, "please select an video ", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<String> filesList = new ArrayList<>();
        filesList.add(postPath);

        if (getContext() != null) {
            UploadActivity.startForResult(
                    this,
                    String.valueOf(mGeoLocationLng),
                    String.valueOf(mGeolocationLat),
                    mFieldLocation.getText().toString(),
                    mFieldSubtitle.getText().toString(),
                    mFieldTitle.getText().toString(),
                    filesList,
                    isFileFromFilesDir);
        }
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
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (selectedVideo != null) {
            if (EasyPermissions.hasPermissions(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

//    File file = new File(postPath);
//
//    // Parsing any Media type file
//
//    Tag tag = new Tag();
//            tag.setId(12);
//            tag.setName("Hello");
//            tag.setSlug("AAAAA");
//
//    Tag[] tagArrayList = new Tag[1];
//    tagArrayList[0] = tag;
//
//    Gson gson = new Gson();
//    String json = gson.toJson(tagArrayList);
//
//    MultipartBody.Part body = MultipartBody.Part.createFormData("source", file.getName(), RequestBody.create(MediaType.parse("video/*"), file));
//    RequestBody title = RequestBody.create(MediaType.parse("*/*"), mFieldTitle.getText().toString());
//    RequestBody subtitle = RequestBody.create(MediaType.parse("*/*"), mFieldSubtitle.getText().toString());
//    RequestBody geoName = RequestBody.create(MediaType.parse("*/*"), mFieldLocation.getText().toString());
//    RequestBody geoLocationLat = RequestBody.create(MediaType.parse("*/*"), mGeolocationLat);
//    RequestBody geoLocationLng = RequestBody.create(MediaType.parse("*/*"), mGeoLocationLng);
//
//            RequestManager.getInstance().content(title, subtitle, geoName, geoLocationLat, geoLocationLng, body, new CallbackListener<Response<Item>>() {
//        @Override
//        public void onSuccess(Response<Item> response) {
//            mProgressDialog.dismiss();
//            mUploadVideoView.setVisibility(View.GONE);
//            clearFileFromFilesDir(file);
//            Log.d("TESTING", "access " + "Upload Data");
//        }
//
//        @Override
//        public void onFailure(Throwable error) {
//            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
//            mProgressDialog.dismiss();
//            mUploadVideoView.setVisibility(View.GONE);
//            clearFileFromFilesDir(file);
//        }
//    });

    public void showCategoryDialog(){
        AlertDialog.Builder dialogCategory = new AlertDialog.Builder(getActivity());
        dialogCategory.setTitle("Select A Category ");
        dialogCategory.setItems(mCategoryList, (dialog, which) -> {
            String selectedCategory = Arrays.asList(mCategoryList).get(which);
            if (selectedCategory.equals("NEWS")){
                Dialog dialogNews;
                final ArrayList<Integer> itemsSelectedNews = new ArrayList<>();
                AlertDialog.Builder builderNews = new AlertDialog.Builder(getActivity());
                builderNews.setTitle("Select News ");
                builderNews.setMultiChoiceItems(mCategoryNews, null,
                        (dialog1, selectedItemId, isSelected) -> {
                            if (isSelected) {
                                itemsSelectedNews.add(selectedItemId);
                                for (int i = 0; i < itemsSelectedNews.size(); i++) {
                                }
                            } else if (itemsSelectedNews.contains(selectedItemId)) {
                                itemsSelectedNews.remove(Integer.valueOf(selectedItemId));
                            }
                        })
                        .setPositiveButton("Done!", (dialog12, id) -> {
                            TagsModel[] tagsModels = new TagsModel[itemsSelectedNews.size()];
                            for (int i = 0; i < tagsModels.length; i++) {
                                tagsModels[i] = new TagsModel(R.drawable.ic_delete_button,mCategoryNews[itemsSelectedNews.get(i)]);
                                mCategoriesAdapter.addItem(tagsModels[i]);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog13, id) -> {
                        });
                dialogNews = builderNews.create();
                dialogNews.show();

            }else if (selectedCategory.equals("SPORTS")){
                Dialog dialogSports;
                final ArrayList<Integer> itemsSelectedSports = new ArrayList<>();
                AlertDialog.Builder builderSports = new AlertDialog.Builder(getActivity());
                builderSports.setTitle("Select Sports ");
                builderSports.setMultiChoiceItems(mCategorySports, null,
                        (dialog1, selectedItemId, isSelected) -> {
                            if (isSelected) {
                                itemsSelectedSports.add(selectedItemId);
                                for (int i = 0; i < itemsSelectedSports.size(); i++) {
                                }
                            } else if (itemsSelectedSports.contains(selectedItemId)) {
                                itemsSelectedSports.remove(Integer.valueOf(selectedItemId));
                            }
                        })
                        .setPositiveButton("Done!", (dialog12, id) -> {
                            TagsModel[] tagsModels = new TagsModel[itemsSelectedSports.size()];
                            for (int i = 0; i < tagsModels.length; i++) {
                                tagsModels[i] = new TagsModel(R.drawable.ic_delete_button,mCategorySports[itemsSelectedSports.get(i)]);
                                mCategoriesAdapter.addItem(tagsModels[i]);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog13, id) -> {
                        });
                dialogSports = builderSports.create();
                dialogSports.show();

            }
        });
        AlertDialog Categorydialog = dialogCategory.create();
        Categorydialog.show();
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
        if (isNull){
            mFieldShareWithIndividuals.setLayoutParams(paramsEditText);
        }else {
            mFieldShareWithIndividuals.setLayoutParams(paramsChanged);
        }

    }

    @Override
    public void onTimeZoneSelected(int type, String timezone) {
        rotateArrow(type, false);
        switch (type) {
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