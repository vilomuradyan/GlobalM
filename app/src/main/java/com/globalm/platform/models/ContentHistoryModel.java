package com.globalm.platform.models;

public class ContentHistoryModel {

    private String mUserName;
    private String mUserPrice;
    private String mTime;

    public ContentHistoryModel(String userName, String userPrice, String time) {
        mUserName = userName;
        mUserPrice = userPrice;
        mTime = time;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserPrice() {
        return mUserPrice;
    }

    public void setUserPrice(String userPrice) {
        mUserPrice = userPrice;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
