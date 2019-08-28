package com.globalm.platform.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipDrawable;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.globalm.platform.R;
import com.globalm.platform.adapters.ShareCopyRightAdapter;
import com.globalm.platform.listeners.OnShareWithIndividualsListener;
import com.globalm.platform.models.Tag;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.DatePickerUtil;
import com.globalm.platform.utils.MapBoxUtil;
import com.globalm.platform.utils.SkillsDialogUtil;
import com.globalm.platform.utils.Utils;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.List;

public class CreateAssignmentActivity extends BaseMapViewActivity {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 945;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CreateAssignmentActivity.class));
    }

    private EditText title;
    private EditText description;
    private EditText chooseCountry;
    private ImageView countryIv;
    private EditText city;
    private TextView date;
    private ImageView dateIv;
    private TextView respond;
    private ImageView respondIv;
    private CheckBox urgent;
    private EditText rateValue;
    private ChipGroup skillsChipGroup;
    private TextView bidderCountry;
    private ImageView bidderCountryIv;
    private TextView bidderCity;
    private RadioButton addGeofenceToMap;
    private RadioButton AllSelectedJournalists;
    private RadioButton FavouritesOnly;
    private Button postNow;

    private ExpandableRelativeLayout countryRl;
    private RecyclerView countriesRv;
    private ExpandableRelativeLayout bidderCountriesRl;
    private RecyclerView bidderCountriesRv;

    private double lat = 0;
    private double lng = 0;
    private SkillsDialogUtil skillsDialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);
        bindView();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        skillsDialogUtil = new SkillsDialogUtil();
        setupCountryExpandableLists();
        setupDatePickers();

        setupMapListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            if (feature.placeType().get(0).equals("country")) {
                bidderCountry.setText(feature.text());
            } else {
                bidderCity.setText(feature.text());
            }
            Point point = feature.center();
            if (point != null) {
                mapboxMap.clear();
                MapBoxUtil.putMarkerOnMap(point.latitude(), point.longitude(), feature.text(),  mapboxMap);
            }
        }
    }

    private void setupMapListeners() {
        bidderCountry.setOnClickListener((v) -> startSelectLocationAutocomplete(REQUEST_CODE_AUTOCOMPLETE));
        bidderCity.setOnClickListener((v) -> startSelectLocationAutocomplete(REQUEST_CODE_AUTOCOMPLETE));
    }

    private void startSelectLocationAutocomplete(int requestCode) {
        Intent intent = getPlaceAutocompleteIntent();
        startActivityForResult(intent, requestCode);
    }

    private Intent getPlaceAutocompleteIntent() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#FFFFFF"))
                        .build())

                .build(this);
        return intent;
    }

    @Override
    protected void onMapReadyCallback() {
    }

    private void setupDatePickers() {
        DatePickerUtil.setupDatePicker(this, dateIv, date);
        DatePickerUtil.setupDatePicker(this, respondIv, respond);
    }

    private void setupCountryExpandableLists() {
        setupCountryList();
        setupBidderCountryList();
    }

    private void setupBidderCountryList() {
        bidderCountriesRv.setLayoutManager(new LinearLayoutManager(this));
        bidderCountriesRv.setAdapter(setupCountryAdapter(getSelectBidderCountryListener()));
        bidderCountryIv.setOnClickListener(getOnSelectBidderCountryClickListener());
    }

    private void setupCountryList() {
        countriesRv.setLayoutManager(new LinearLayoutManager(this));
        countriesRv.setAdapter(setupCountryAdapter(getSelectedCountryListener()));
        countryIv.setOnClickListener(getOnSelectCountryClickListener());
    }

    private ShareCopyRightAdapter setupCountryAdapter(OnShareWithIndividualsListener listener) {
        return new ShareCopyRightAdapter(
                0,
                Utils.getCountyNamesList(),
                listener);
    }

    private OnShareWithIndividualsListener getSelectedCountryListener() {
        return (type, country) -> {
            chooseCountry.setText(country);
            countryRl.collapse();
            Utils.rotateArrow(countryIv, !countryRl.isExpanded());
        };
    }

    private OnShareWithIndividualsListener getSelectBidderCountryListener() {
        return (type, country) -> {
            bidderCountry.setText(country);
            bidderCountriesRl.collapse();
            Utils.rotateArrow(bidderCountryIv, !countryRl.isExpanded());
        };
    }

    private View.OnClickListener getOnSelectBidderCountryClickListener() {
        return (v) -> {
            if (bidderCountriesRl.isExpanded()) {
                bidderCountriesRl.collapse();
            } else {
                bidderCountriesRl.expand();
            }
            Utils.rotateArrow(bidderCountryIv, !bidderCountriesRl.isExpanded());
        };
    }

    private View.OnClickListener getOnSelectCountryClickListener() {
        return (v) -> {
            if (countryRl.isExpanded()) {
                countryRl.collapse();
            } else {
                countryRl.expand();
            }
            Utils.rotateArrow(countryIv, !countryRl.isExpanded());
        };
    }

    private void bindView() {
        title = findView(R.id.title);
        description = findView(R.id.description);
        chooseCountry = findView(R.id.choose_country);
        countryIv = findView(R.id.country_iv);
        city = findView(R.id.city_ed);
        date = findView(R.id.date_ed);
        dateIv = findView(R.id.date_iv);
        respond = findView(R.id.respond_date);
        respondIv = findView(R.id.respond_date_iv);
        urgent = findView(R.id.urgent_cb);
        rateValue = findView(R.id.rate_value);
        skillsChipGroup = findView(R.id.skills_chip_group);
        bidderCountry = findView(R.id.bidder_country);
        bidderCountryIv = findView(R.id.bidder_country_iv);
        bidderCity = findView(R.id.bidder_city_ed);
        addGeofenceToMap = findView(R.id.geofence_radio_btn);
        AllSelectedJournalists = findView(R.id.all_selected_journalists_radio_btn);
        FavouritesOnly = findView(R.id.favourites_only_radio_btn);
        postNow = findView(R.id.post_now_btn);
        mapView = findView(R.id.map_view);
        countryRl = findView(R.id.layout_expandable_copy_right);
        countriesRv = findView(R.id.list_copy_right);
        bidderCountriesRl = findView(R.id.bidder_layout_expandable_country);
        bidderCountriesRv = findView(R.id.bidder_list_country);
        findView(R.id.add_skill_btn).setOnClickListener(showSkillsDialog());

        findView(R.id.back_button).setOnClickListener((v) -> finish());
        ((TextView)findView(R.id.text_title)).setText(R.string.create_assignment);

        date.setKeyListener(null);
        respond.setKeyListener(null);
    }

    private View.OnClickListener showSkillsDialog() {
        return (v) -> skillsDialogUtil.showTagDialog(this, getRequestManager(),  getDialogCallback());
    }

    private SkillsDialogUtil.DialogCallback<List<Tag>> getDialogCallback() {
        return new SkillsDialogUtil.DialogCallback<List<Tag>>() {
            @Override
            public void onOk(List<Tag> skills) {
                addSkills(skills);
            }

            @Override
            public void onCancel() { }

            @Override
            public void onApiFailure(Throwable error) {
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void addSkills(List<Tag> skills) {
        for (Tag skill : skills) {
            skillsChipGroup.addView(getChip(skill.getName()));
        }
    }

    private Chip getChip(String newChipText) {
        Chip chip = new Chip(skillsChipGroup.getContext());
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip));
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(newChipText);
        chip.setOnCloseIconClickListener(v -> skillsChipGroup.removeView(chip));
        return chip;
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }
}