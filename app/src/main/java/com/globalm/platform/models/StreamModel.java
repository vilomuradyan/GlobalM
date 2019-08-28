package com.globalm.platform.models;

import java.io.Serializable;

public class StreamModel implements Serializable {

    private String mTitle;
    private String mDate;
    private String mDescription;
    private String mUserName;
    private String mUserSurname;
    private int mUserImage;
    private boolean mLive;

    public StreamModel(int UserImage ,String title, String date, String description, String userName, String userSurname, boolean live) {
        mUserImage = UserImage;
        mTitle = title;
        mDate = date;
        mDescription = description;
        mUserName = userName;
        mUserSurname = userSurname;
        mLive = live;
    }

    public int getUserImage() {
        return mUserImage;
    }

    public void setUserImage(int userImage) {
        mUserImage = userImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserSurname() {
        return mUserSurname;
    }

    public void setUserSurname(String userSurname) {
        mUserSurname = userSurname;
    }

    public boolean isLive() {
        return mLive;
    }

    public void setLive(boolean live) {
        mLive = live;
    }
}
