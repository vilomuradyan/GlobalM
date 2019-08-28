package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

public class Cover {

    @SerializedName("file_name")
    private String fileName;
    @SerializedName("mime_type")
    private String mimeType;
    private String url;
    private String thumb;
    private Props props;

    public Cover(String fileName, String mimeType, String url, String thumb, Props props) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.url = url;
        this.thumb = thumb;
        this.props = props;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl() {
        return url;
    }

    public Props getProps() {
        return props;
    }

    public String getThumb() {
        return thumb;
    }
}
