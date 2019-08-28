package com.globalm.platform.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.Content;
import com.globalm.platform.models.GeoLocation;
import com.globalm.platform.models.Item;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.MapBoxUtil;
import com.mapbox.android.core.permissions.PermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends BaseMapViewFragment implements View.OnClickListener, PermissionsListener {
    private EditText mFieldSearch;
    private ImageView imageViewClearSearchText;
    private Context mContext;
    private ImageView mButtonFilterLocation;
    private ArrayList<Item> mStreamList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        bindView(rootView);
        setupView();
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            mFieldSearch.setText(bundle.getString("location"));
        }
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_filter_location:
                Toast.makeText(mContext, "Not implemented yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onMapReadyCallback() {
        getContents();
    }

    private void getContents() {
        CallbackListener<Content> callbackListener = new CallbackListener<Content>() {
            @Override
            public void onSuccess(Content response) {
                if (response != null) {
                    mStreamList.addAll(response.getData().getItems());
                    showMarkers();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getMessage(), Toast.LENGTH_LONG);
            }
        };
        String query = mFieldSearch.getText().toString();
        if (TextUtils.isEmpty(query)) {
            RequestManager.getInstance().getContent(1, callbackListener);
        } else {
            RequestManager.getInstance().getContentForLocation(query, callbackListener);
        }
    }

    private void bindView(View rootView) {
        mFieldSearch = rootView.findViewById(R.id.field_search);
        mapView = rootView.findViewById(R.id.map_view);
        mButtonFilterLocation = rootView.findViewById(R.id.button_filter_results);
        imageViewClearSearchText = rootView.findViewById(R.id.iv_clear_search_text);
    }

    private void setupView() {
        mButtonFilterLocation.setOnClickListener(this);
        mFieldSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    resetData();
                    getContents();
                    imageViewClearSearchText.setVisibility(View.INVISIBLE);
                } else {
                    imageViewClearSearchText.setVisibility(View.VISIBLE);
                }
            }
        });
        mFieldSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard(mFieldSearch);
                resetData();
                getContents();
                return true;
            }
            return false;
        });
        imageViewClearSearchText.setOnClickListener((v) -> {
            mFieldSearch.setText("");
            closeKeyboard(mFieldSearch);
        });
        imageViewClearSearchText.setVisibility(mFieldSearch.getText().length() == 0 ? View.INVISIBLE : View.VISIBLE);
    }

    private void showMarkers() {
        mapboxMap.clear();
        for (Item item : mStreamList) {
            if (item.getGeoLocation() != null && item.getGeoLocation().getLocation() != null) {
                GeoLocation.Location location = item.getGeoLocation().getLocation();
                String title = item.getTitle();
                MapBoxUtil.putMarkerOnMap(location.getLat(), location.getLon(), title, mapboxMap);
            }
        }
    }

    private void resetData() {
        mStreamList.clear();
    }
}