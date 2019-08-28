package com.globalm.platform.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipDrawable;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.activities.AssignmentDetailsActivity;
import com.globalm.platform.activities.PlacesPluginActivity;
import com.globalm.platform.adapters.ShareCopyRightAdapter;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.listeners.OnShareWithIndividualsListener;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.GeoLocation;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.Tag;
import com.globalm.platform.models.assingments.Assignment;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.DatePickerUtil;
import com.globalm.platform.utils.MapBoxUtil;
import com.globalm.platform.utils.Utils;

import java.util.List;

public class AssignmentDetailsFragment extends BaseMapViewFragment {
    private static final int LOCATION_REQUEST_CODE = 23;

    public static AssignmentDetailsFragment newInstance(int assignmentId) {
        Bundle bundle = new Bundle();
        bundle.putInt(AssignmentDetailsActivity.ASSIGNMENT_ID_KEY, assignmentId);
        AssignmentDetailsFragment fragment = new AssignmentDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private EditText title;
    private EditText description;

    private EditText chooseCountry;
    private ImageView countrySelectIv;
    private ExpandableRelativeLayout countryRl;
    private RecyclerView countriesRv;

    private EditText city;

    private EditText date;
    private ImageView dateIv;
    private EditText respondDate;
    private ImageView respondDateIv;

    private CheckBox urgent;
    private TextView rate;
    private ChipGroup skillsChipGroup;

    private TextView location;
    private ImageView locationIv;

    private Double lat;
    private Double lng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_assignment_details, container, false);
        setupView(root);
        disableFieldEditing();
        setupDatePickers();
        setupCountriesRecycler();
        dateIv.setEnabled(false);
        respondDateIv.setEnabled(false);
        locationIv.setEnabled(false);
        countrySelectIv.setEnabled(false);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String location = data.getStringExtra(PlacesPluginActivity.LOCATION_KEY);
        double lat = data.getDoubleExtra(PlacesPluginActivity.LOCATION_LAT_KEY, 0);
        double lng = data.getDoubleExtra(PlacesPluginActivity.LOCATION_LNG_KEY, 0);
        if (location != null && !location.isEmpty()) {
            this.location.setText(location);
            this.lat = lat;
            this.lng = lng;
            if (mapboxMap != null) {
                mapboxMap.clear();
                MapBoxUtil.putMarkerOnMap(lat, lng, "current position",  mapboxMap);
                MapBoxUtil.setCameraPosition(mapboxMap, lat, lng, 2.0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onMapReadyCallback() {
        getAssignmentId();
    }

    private void setupLocationPick(double lat, double lng) {
        locationIv.setOnClickListener((v) ->
                PlacesPluginActivity.startForResult(
                    AssignmentDetailsFragment.this,
                    lat,
                    lng,
                    LOCATION_REQUEST_CODE));
    }

    private void setupCountriesRecycler() {
        countriesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        countriesRv.setAdapter(setupCountryAdapter());
        countrySelectIv.setOnClickListener(getSelectCountryListener());
    }

    private ShareCopyRightAdapter setupCountryAdapter() {
        return new ShareCopyRightAdapter(
                0,
                Utils.getCountyNamesList(),
                getOnCountrySelectedCallback());
    }

    private View.OnClickListener getSelectCountryListener() {
        return (v) -> {
            if (countryRl.isExpanded()) {
                countryRl.collapse();
            } else {
                countryRl.expand();
            }
            Utils.rotateArrow(countrySelectIv, !countryRl.isExpanded());
        };
    }

    private OnShareWithIndividualsListener getOnCountrySelectedCallback() {
        return (t,s) -> {
            chooseCountry.setText(s);
            countryRl.collapse();
            Utils.rotateArrow(countrySelectIv, !countryRl.isExpanded());
        };
    }

    private void setupDatePickers() {
        DatePickerUtil.setupDatePicker(getContext(), dateIv, date);
        DatePickerUtil.setupDatePicker(getContext(), respondDateIv, respondDate);
    }

    private void getAssignmentId() {
        if (getArguments() == null) {
            return;
        }
        int assignmentId = getArguments().getInt(AssignmentDetailsActivity.ASSIGNMENT_ID_KEY);

        getRequestManager().getAssignmentById(assignmentId, getAssignmentCallback());
    }

    private CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Assignment>>> getAssignmentCallback() {
        return new CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Assignment>>>() {
            @Override
            public void onSuccess(BaseResponseBody<List<Void>, GetContentDataModel<Assignment>> o) {
                populateView(o.getData().getItem());
            }

            @Override
            public void onFailure(Throwable error) {
                showMessage(error.getMessage(), Toast.LENGTH_LONG);
            }
        };
    }

    private void populateView(Assignment assignment) {
        title.setText(assignment.getTitle());
        description.setText(assignment.getDescription());

        setDates(assignment);
        rate.setText(String.valueOf(assignment.getRateValue()));
        if (assignment.getUrgent() != null) {
            urgent.setChecked(assignment.getUrgent());
        }
        setSkills(assignment.getSkills());
        setupGeoData(assignment.getTargetGeoData());
    }

    private void setDates(Assignment assignment) {
        if (assignment.getDate() != null && assignment.getDate().getDate() != null) {
            date.setText(assignment.getDate().getDate().replace("-", "/"));
        }
        if (assignment.getRespondBy() != null && assignment.getRespondBy().getDate() != null) {
            respondDate.setText(assignment.getRespondBy().getDate().replace("-", "/"));
        }
    }

    private void setupGeoData(GeoLocation targetGeoData) {
        if (targetGeoData == null) {
            return;
        }
        chooseCountry.setText(targetGeoData.getCountry());
        city.setText(targetGeoData.getName());
        location.setText(targetGeoData.getName());
        setupMapMarker(
                targetGeoData.getLocation(),
                targetGeoData.getName());

        setupLocationPick(targetGeoData.getLocation().getLat(), targetGeoData.getLocation().getLon());
    }

    private void setupMapMarker(GeoLocation.Location location, String title) {
        if (location == null) {
            return;
        }

        MapBoxUtil.putMarkerOnMap(
                location.getLat(),
                location.getLon(),
                title,
                mapboxMap);
    }

    private void setSkills(List<Tag> skills) {
        for (Tag skill : skills) {
            skillsChipGroup.addView(getChip(getContext(), skill.getName()));
        }
    }

    private Chip getChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.chip));
        chip.setText(title);
        chip.setCloseIconVisible(false);
        return chip;
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }

    private void setupView(View root) {
        title = findView(root, R.id.title);
        description = findView(root, R.id.description);
        chooseCountry = findView(root, R.id.choose_country);
        countrySelectIv = findView(root, R.id.country_iv);
        countryRl = findView(root, R.id.layout_expandable_copy_right);
        countriesRv = findView(root, R.id.list_copy_right);
        city = findView(root, R.id.city_ed);
        date = findView(root, R.id.date_ed);
        dateIv = findView(root, R.id.date_iv);
        respondDate = findView(root, R.id.respond_date);
        respondDateIv = findView(root, R.id.respond_date_iv);
        urgent = findView(root, R.id.urgent_cb);
        rate = findView(root, R.id.rate_value);
        skillsChipGroup = findView(root, R.id.skills_chip_group);
        location = findView(root, R.id.id_tv);
        locationIv = findView(root, R.id.select_location_iv);
        mapView = findView(root, R.id.map_view);
    }

    private void disableFieldEditing() {
        title.setKeyListener(null);
        description.setKeyListener(null);
        chooseCountry.setKeyListener(null);
        city.setKeyListener(null);
        date.setKeyListener(null);
        respondDate.setKeyListener(null);
        urgent.setKeyListener(null);
        rate.setKeyListener(null);
        location.setKeyListener(null);
    }
}
