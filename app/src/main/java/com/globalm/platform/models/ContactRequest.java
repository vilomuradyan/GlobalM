package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

public class ContactRequest {
    @SerializedName("target_user_id")
    private int id;

    public ContactRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
