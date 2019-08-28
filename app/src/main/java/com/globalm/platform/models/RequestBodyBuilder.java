package com.globalm.platform.models;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestBodyBuilder {
    public static final String METHOD_PATCH = "PATCH";

    private Map<String, RequestBody> map = new HashMap<>();

    public RequestBodyBuilder setTitle(String title) {
        createRequestBody("title", title);
        return this;
    }

    public RequestBodyBuilder setSubtitle(String subtitle) {
        createRequestBody("subtitle", subtitle);
        return this;
    }

    public RequestBodyBuilder setSource(String source) {
        createRequestBody("source", source);
        return this;
    }

    public RequestBodyBuilder setCategories(String categories) {
        createRequestBody("categories[]", categories);
        return this;
    }

    public RequestBodyBuilder setTags(String tags) {
        createRequestBody("tags", tags);
        return this;
    }

    public RequestBodyBuilder setGeoName(String geoName) {
        createRequestBody("geo_name", geoName);
        return this;
    }

    public RequestBodyBuilder setMethod(String method) {
        createRequestBody("_method", method);
        return this;
    }

    public RequestBodyBuilder setGeoLocationLat(String geoLocationLat) {
        createRequestBody("geo_data[location][lat]", geoLocationLat);
        return this;
    }

    public RequestBodyBuilder setGeoLocationLng(String geoLocationLng) {
        createRequestBody("geo_data[location][lng]", geoLocationLng);
        return this;
    }

    public Map<String, RequestBody> build() {
        return map;
    }

    private void createRequestBody(String name, String content) {
        map.put(name, RequestBody.create(MediaType.parse("*/*"), content));
    }
}
