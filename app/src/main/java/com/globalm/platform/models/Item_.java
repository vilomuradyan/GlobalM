package com.globalm.platform.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item_ implements Serializable  {

    @SerializedName("props")
    @Expose
    private Props props;
    @SerializedName("url")
    @Expose
    private String url;

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}