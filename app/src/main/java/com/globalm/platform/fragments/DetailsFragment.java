package com.globalm.platform.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.activities.ContactActivity;
import com.globalm.platform.activities.MessageActivity;
import com.globalm.platform.activities.VideoActivity;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.Category;
import com.globalm.platform.models.FileData;
import com.globalm.platform.models.GeoLocation;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.Owner;
import com.globalm.platform.models.Preview;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.CircleTransformation;
import com.globalm.platform.utils.MapBoxUtil;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsFragment extends BaseFragment implements View.OnClickListener {

    private Context mContext;
    private TextView mUserName;
    private TextView mTextViewTitle;
    private TextView mTextViewStatus;
    private TextView mTextViewDate;
    private TextView mTextViewId;
    private TextView mTextViewDataShot;
    private TextView mTextViewCountry;
    private TextView mTextViewTags;
    private TextView mTextViewCategories;
    private TextView mTextViewUserCountry;
    private TextView mTextViewStream;
    private TextView mTextViewDownload;
    private ImageView mImageMessage;
    private TextView mUserSpecialist;
    private ImageView mImageUserCycle;
    private ImageView mImageViewVideo;
    private MapView mMapView;
    private Item model;

    public void setModel(Item model) {
        this.model = model;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        initViews(rootView);
        mMapView.onCreate(savedInstanceState);
        setupViews();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_user_name:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.details_user_job_title:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
                case R.id.details_cycle_image:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.image_cycle_message:
                startActivity(new Intent(mContext, MessageActivity.class));
                break;
            case R.id.details_video: {
                if (getContext() == null) {
                    return;
                }
                getVideoUrlAndStartVideoActivity(model.getId());
                break;
            }
        }
    }

    private void getVideoUrlAndStartVideoActivity(Integer contentId) {
        getRequestManager().getFileContent(contentId, getContentCallback());
    }

    private CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<FileData>>> getContentCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<FileData>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<FileData>> o) {
                Preview preview = o.getData().getItem().getPreview();
                if (preview != null) {
                    VideoActivity.start(getContext(), o.getData().getItem().getPreview().getUrl());
                }
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getMessage(), Toast.LENGTH_LONG);
            }
        };
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }

    private void initViews(View rootView) {
        mUserName = rootView.findViewById(R.id.details_user_name);
        mTextViewTitle = rootView.findViewById(R.id.details_news);
        mTextViewStatus = rootView.findViewById(R.id.details_tv_status);
        mTextViewDate = rootView.findViewById(R.id.details_date_news);
        mTextViewId = rootView.findViewById(R.id.details_id);
        mTextViewDataShot = rootView.findViewById(R.id.details_tv_data_shot);
        mTextViewCountry = rootView.findViewById(R.id.details_tv_country);
        mTextViewUserCountry = rootView.findViewById(R.id.details_user_country);
        mTextViewTags = rootView.findViewById(R.id.details_tv_tags);
        mTextViewCategories = rootView.findViewById(R.id.details_categories);
        mTextViewStream = rootView.findViewById(R.id.details_stream);
        mTextViewDownload = rootView.findViewById(R.id.details_download);
        mImageMessage = rootView.findViewById(R.id.image_cycle_message);
        mImageViewVideo = rootView.findViewById(R.id.details_video);
        mUserSpecialist = rootView.findViewById(R.id.details_user_job_title);
        mImageUserCycle = rootView.findViewById(R.id.details_cycle_image);
        mMapView = rootView.findViewById(R.id.details_map);
        mImageUserCycle.setOnClickListener(this);
        mUserSpecialist.setOnClickListener(this);
        mImageMessage.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mImageViewVideo.setOnClickListener(this);
    }

    private void setupViews() {
        mTextViewDate.setText(model.getCreatedAt());
        mTextViewTitle.setText(model.getTitle());
        Picasso.get().load(model.getThumb()).into(mImageViewVideo);
        mTextViewStatus.setText(model.getStatus());
        mTextViewId.setText("id");
        mTextViewDataShot.setText(model.getCreatedAt());
        mTextViewStream.setText("Stream 1 https://www.examplestream.com/id=478236");
        mTextViewDownload.setText("Stream 1 https://www.examplestream.com/id=478236");
        Owner owner = model.getOwner();
        if (owner != null) {
            mUserName.setText(owner.getFullName());
            Owner.Profile profile = owner.getProfile();
            if (profile != null) {
                if (profile.getThumb() != null) {
                    Picasso.get().load(profile.getThumb()).transform(new CircleTransformation()).into(mImageUserCycle);
                }
                mUserSpecialist.setText(profile.getTitle());
                mTextViewUserCountry.setText(profile.getCountry());
            }
        }
        if (model.getTags() != null && model.getTags().size() > 0) {
            //building tags string
            StringBuilder tagsText = new StringBuilder();
            List<Tag> tags = model.getTags();
            for (int i = 0; i < tags.size(); i++) {
                tagsText.append(tags.get(i).getName());
                if (i < tags.size() - 1) {
                    tagsText.append(", ");
                }
            }
            mTextViewTags.setText(tagsText.toString());
        }

        if (model.getCategories() != null && model.getCategories().size() > 0){
            StringBuilder categoriesText = new StringBuilder();
            List<Object> categories = model.getCategories();
            for (int i = 0; i < categories.size(); i++) {
                categoriesText.append(categories.get(i).toString());
                if (i < categories.size() - 1) {
                    categoriesText.append(", ");
                }
            }
            mTextViewCategories.setText(categoriesText.toString());
        }

        if (model.getGeoLocation() != null) {
            mTextViewCountry.setText(model.getGeoLocation().getCountry());
        }
        mMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            if (model.getGeoLocation() != null && model.getGeoLocation().getLocation() != null) {
                GeoLocation.Location location = model.getGeoLocation().getLocation();
                MapBoxUtil.putMarkerOnMap(
                        location.getLat(),
                        location.getLon(),
                        "",
                        mapboxMap);
            }
        }));
    }
}