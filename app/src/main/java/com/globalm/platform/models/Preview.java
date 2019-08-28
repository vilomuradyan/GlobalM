package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

public class Preview {
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("mime_type")
    private String mimeType;
    private String url;

    public Preview(String fileName, String mimeType, String url) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.url = url;
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
}
