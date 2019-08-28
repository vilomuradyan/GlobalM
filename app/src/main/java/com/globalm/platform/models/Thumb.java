package com.globalm.platform.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumb implements Serializable {

    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("items")
    @Expose
    private List<Item_> items = null;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<Item_> getItems() {
        return items;
    }

    public void setItems(List<Item_> items) {
        this.items = items;
    }

}