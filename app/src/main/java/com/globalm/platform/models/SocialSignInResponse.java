package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SocialSignInResponse {


    @SerializedName("success")
    private boolean mSuccess;

    @SerializedName("error")
    private boolean mError;

    @SerializedName("type")
    private String mType;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("code")
    private int mCode;

    @SerializedName("data")
    private DataBean mData;

    @SerializedName("validation")
    private List<?> mValidation;

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    public boolean isError() {
        return mError;
    }

    public void setError(boolean error) {
        mError = error;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public DataBean getData() {
        return mData;
    }

    public void setData(DataBean data) {
        mData = data;
    }

    public List<?> getValidation() {
        return mValidation;
    }

    public void setValidation(List<?> validation) {
        mValidation = validation;
    }

    public static class DataBean {

        @SerializedName("options")
        private OptionsBean mOptions;

        @SerializedName("items")
        private List<ItemsBean> mItems;

        public OptionsBean getOptions() {
            return mOptions;
        }

        public void setOptions(OptionsBean options) {
            mOptions = options;
        }

        public List<ItemsBean> getItems() {
            return mItems;
        }

        public void setItems(List<ItemsBean> items) {
            mItems = items;
        }

        public static class OptionsBean {

            @SerializedName("facebook")
            private List<?> mFacebook;

            @SerializedName("google")
            private List<?> mGoogle;

            @SerializedName("linkedin")
            private List<?> mLinkedin;

            @SerializedName("twitter")
            private List<?> mTwitter;

            public List<?> getFacebook() {
                return mFacebook;
            }

            public void setFacebook(List<?> facebook) {
                mFacebook = facebook;
            }

            public List<?> getGoogle() {
                return mGoogle;
            }

            public void setGoogle(List<?> google) {
                mGoogle = google;
            }

            public List<?> getLinkedin() {
                return mLinkedin;
            }

            public void setLinkedin(List<?> linkedin) {
                mLinkedin = linkedin;
            }

            public List<?> getTwitter() {
                return mTwitter;
            }

            public void setTwitter(List<?> twitter) {
                mTwitter = twitter;
            }
        }

        public static class ItemsBean {

            @SerializedName("url")
            private String mUrl;

            @SerializedName("type")
            private String mType;

            @SerializedName("title")
            private String mTitle;

            @SerializedName("groups")
            private List<String> mGroups;

            public String getUrl() {
                return mUrl;
            }

            public void setUrl(String url) {
                mUrl = url;
            }

            public String getType() {
                return mType;
            }

            public void setType(String type) {
                mType = type;
            }

            public String getTitle() {
                return mTitle;
            }

            public void setTitle(String title) {
                mTitle = title;
            }

            public List<String> getGroups() {
                return mGroups;
            }

            public void setGroups(List<String> groups) {
                mGroups = groups;
            }
        }
    }
}
