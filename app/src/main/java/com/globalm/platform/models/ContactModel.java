package com.globalm.platform.models;

import java.io.Serializable;

public class ContactModel implements Serializable {

    private String mName;
    private String mPosition;
    private String mCompany;
    private String mCountry;
    private boolean mAccepted;
    private boolean mPending;
    public boolean isSection;

    public ContactModel(String mName, boolean isSection) {
        this.mName = mName;
        this.isSection = isSection;
    }

    public ContactModel(String mName, String mPosition, String mCompany, String mCountry, boolean mAccepted, boolean mPending) {
        this.mName = mName;
        this.mPosition = mPosition;
        this.mCompany = mCompany;
        this.mCountry = mCountry;
        this.mAccepted = mAccepted;
        this.mPending = mPending;
    }

    public ContactModel(String mName, String mPosition, String mCompany, String mCountry, boolean mAccepted, boolean mPending, boolean isSection) {
        this.mName = mName;
        this.mPosition = mPosition;
        this.mCompany = mCompany;
        this.mCountry = mCountry;
        this.mAccepted = mAccepted;
        this.mPending = mPending;
        this.isSection = isSection;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String position) {
        mPosition = position;
    }

    public String getCompany() {
        return mCompany;
    }

    public void setCompany(String company) {
        mCompany = company;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public boolean isAccepted() {
        return mAccepted;
    }

    public void setAccepted(boolean accepted) {
        mAccepted = accepted;
    }

    public boolean isPending() {
        return mPending;
    }

    public void setPending(boolean pending) {
        mPending = pending;
    }
}
