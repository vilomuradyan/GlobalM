package com.globalm.platform.models;

public class ChannelModel {

    private String mName;
    private int mMemberCount;

    public ChannelModel(String name, int memberCount) {
        mName = name;
        mMemberCount = memberCount;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getMemberCount() {
        return mMemberCount;
    }

    public void setMemberCount(int memberCount) {
        mMemberCount = memberCount;
    }
}
