package com.globalm.platform.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.utils.MapBoxUtil;
import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class PlacesPluginActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";

    public static final String LOCATION_KEY = "LOCATION_KEY";
    public static final String LOCATION_LAT_KEY = "LOCATION_LAT_KEY";
    public static final String LOCATION_LNG_KEY = "LOCATION_LNG_KEY";

    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), PlacesPluginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(
            Fragment fragment,
            double lat,
            double lng,
            int requestCode) {
        Intent intent = new Intent(fragment.getContext(), PlacesPluginActivity.class);
        intent.putExtra(LOCATION_LAT_KEY, lat);
        intent.putExtra(LOCATION_LNG_KEY, lng);
        fragment.startActivityForResult(intent, requestCode);
    }

    private MapView mapView;
    private MapboxMap mapboxMap;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private EditText mFieldSearch;
    private String mLocation;

    private double mGeoLocationLat;
    private double mGeoLocationLng;

    private CarmenFeature home;
    private CarmenFeature work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_places_plugin);
        baseSetup(savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        showLocation(getIntent());

        mapboxMap.addOnMapClickListener(getOnMapClickListener());

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            initSearchFab();
            setUpSource(style);
            setupLayer(style);
            initDroppedMarker(style);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(LOCATION_KEY, mLocation);
        intent.putExtra(LOCATION_LAT_KEY, mGeoLocationLat);
        intent.putExtra(LOCATION_LNG_KEY, mGeoLocationLng);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("dropped-icon-image", BitmapFactory.decodeResource(
                getResources(), R.drawable.mapbox_marker_icon_default));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    mGeoLocationLat = ((Point) selectedCarmenFeature.geometry()).latitude();
                    mGeoLocationLng = ((Point) selectedCarmenFeature.geometry()).longitude();
                    mLocation = selectedCarmenFeature.text();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(mGeoLocationLat, mGeoLocationLng))
                                    .zoom(14)
                                    .build()), 4000);
                    clearAllMapMarkers();
                    MapBoxUtil.putMarkerOnMap(
                            mGeoLocationLat,
                            mGeoLocationLng,
                            "current position",
                            mapboxMap);
                }
            }
        }
    }

    private void showLocation(Intent intent) {
        double lat = intent.getDoubleExtra(LOCATION_LAT_KEY, 0);
        double lng = intent.getDoubleExtra(LOCATION_LNG_KEY, 0);

        if (lat != 0 && lng != 0) {
            MapBoxUtil.putMarkerOnMap(lat, lng, "current position",  mapboxMap);
            MapBoxUtil.setCameraPosition(mapboxMap, lat, lng, 2.0);
        }
    }

    private MapboxMap.OnMapClickListener getOnMapClickListener() {
        return point -> {
            clearAllMapMarkers();
            MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                    .accessToken(Mapbox.getAccessToken())
                    .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            reverseGeocode.enqueueCall(getReverseGeocodeCallback(point));

            return false;
        };
    }

    private Callback<GeocodingResponse> getReverseGeocodeCallback(LatLng point) {
        return new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();

                if (results.size() > 0) {

                    for(CarmenFeature carmenFeature: results){
                        MapBoxUtil.putMarkerOnMap(
                                point.getLatitude(),
                                point.getLongitude(),
                                "",
                                mapboxMap);
                        mLocation = carmenFeature.placeName();
                        mGeoLocationLat = point.getLatitude();
                        mGeoLocationLng = point.getLongitude();
                    }
                    Point firstResultPoint = results.get(0).center();
                } else {
                    showMessage(
                            getString(R.string.unable_to_find_location),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
    }

    private void clearAllMapMarkers() {
        mapboxMap.clear();
    }

    private void baseSetup(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.requestFocus();
        mFieldSearch = findViewById(R.id.field_search_location);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mFieldSearch.setOnFocusChangeListener(getOnFocusChangeListener());
    }

    private void initSearchFab() {
        findViewById(R.id.field_search_location).setOnClickListener(getOnSearchClickListener());
    }

    private View.OnFocusChangeListener getOnFocusChangeListener() {
        return (v, hasFocus) -> startPlaceAutocompleteActivity();
    }

    private View.OnClickListener getOnSearchClickListener() {
        return view -> startPlaceAutocompleteActivity();
    }

    private void startPlaceAutocompleteActivity() {
        Intent intent = getPlaceAutocompleteIntent();
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    private Intent getPlaceAutocompleteIntent() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#FFFFFF"))
//                                .limit(10)
//                                .addInjectedFeature(home)
//                                .addInjectedFeature(work)
                        .build())

                .build(PlacesPluginActivity.this);
        return intent;
    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.399854, 37.7884400))
                .placeName("85 2nd St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }
}

