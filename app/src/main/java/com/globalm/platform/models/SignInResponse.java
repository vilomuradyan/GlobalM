package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SignInResponse {

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

        @SerializedName("token")
        private String mToken;

        @SerializedName("expires_in")
        private int mExpiresIn;

        @SerializedName("item")
        private ItemBean mItem;

        public String getToken() {
            return mToken;
        }

        public void setToken(String token) {
            mToken = token;
        }

        public int getExpiresIn() {
            return mExpiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            mExpiresIn = expiresIn;
        }

        public ItemBean getItem() {
            return mItem;
        }

        public void setItem(ItemBean item) {
            mItem = item;
        }

        public static class ItemBean {

            @SerializedName("id")
            private int mId;

            @SerializedName("uuid")
            private String mUuid;

            @SerializedName("email")
            private String mEmail;

            @SerializedName("first_name")
            private String mFirstName;

            @SerializedName("last_name")
            private String mLastName;

            @SerializedName("created_at")
            private String mCreatedAt;

            public int getId() {
                return mId;
            }

            public void setId(int id) {
                mId = id;
            }

            public String getUuid() {
                return mUuid;
            }

            public void setUuid(String uuid) {
                mUuid = uuid;
            }

            public String getEmail() {
                return mEmail;
            }

            public void setEmail(String email) {
                mEmail = email;
            }

            public String getFirstName() {
                return mFirstName;
            }

            public void setFirstName(String firstName) {
                mFirstName = firstName;
            }

            public String getLastName() {
                return mLastName;
            }

            public void setLastName(String lastName) {
                mLastName = lastName;
            }

            public String getCreatedAt() {
                return mCreatedAt;
            }

            public void setCreatedAt(String createdAt) {
                mCreatedAt = createdAt;
            }
        }
    }
}