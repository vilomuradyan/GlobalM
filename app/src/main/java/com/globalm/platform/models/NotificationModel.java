package com.globalm.platform.models;

public class NotificationModel {

    private String mName;
    private String mTime;

    public NotificationModel(String name, String time) {
        mName = name;
        mTime = time;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
