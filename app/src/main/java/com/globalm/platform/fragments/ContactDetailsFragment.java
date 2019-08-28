package com.globalm.platform.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

public class ContactDetailsFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private ImageView mImageCover;
    private ImageView mImageUser;
    private TextView mTextName;
    private TextView mTextPosition;
    private TextView mTextLocation;
    private TextView mTextBiography;
    private TextView mButtonMessage;
    private TextView mButtonCall;
    private TextView mButtonFollow;
    private TextView mButtonConnect;
    private ScrollView mScrollView;
    private MapView mMapView;
    private RatingBar mRatingBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_details, container, false);
        mScrollView = rootView.findViewById(R.id.scroll_view);
        mImageCover = rootView.findViewById(R.id.image_cover);
        mImageUser = rootView.findViewById(R.id.image_user);
        mTextName = rootView.findViewById(R.id.text_name);
        mTextPosition = rootView.findViewById(R.id.text_position);
        mTextLocation = rootView.findViewById(R.id.text_location);
        mTextBiography = rootView.findViewById(R.id.text_biography);
        mButtonMessage = rootView.findViewById(R.id.button_message);
        mButtonCall = rootView.findViewById(R.id.button_call);
        mButtonFollow = rootView.findViewById(R.id.button_follow);
        mButtonConnect = rootView.findViewById(R.id.button_connect);
        mMapView = rootView.findViewById(R.id.map_view);
        mRatingBar = rootView.findViewById(R.id.rating_bar);
        mRatingBar.setRating(4);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

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

        mButtonMessage.setOnClickListener(this);
        mButtonCall.setOnClickListener(this);
        mButtonFollow.setOnClickListener(this);
        mButtonConnect.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_message:
                Toast.makeText(mContext, "Not implemeted yet", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_call:
                Toast.makeText(mContext, "Not implemeted yet", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_follow:
                Toast.makeText(mContext, "Not implemeted yet", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_connect:
                Toast.makeText(mContext, "Not implemeted yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}