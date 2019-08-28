package com.globalm.platform.utils;

import android.support.annotation.Nullable;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public abstract class MapBoxUtil {

    public static void putMarkerOnMap(double lat, double lng, String title, @Nullable MapboxMap mapBox) {
        if (mapBox != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(title);
            markerOptions.position(new LatLng(lat, lng));
            mapBox.addMarker(markerOptions);
        }
    }

    public static void setCameraPosition(MapboxMap mapboxMap, double lat, double lng, double zoom) {
        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(zoom)
                .build());
    }
}
