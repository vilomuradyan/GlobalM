package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class SignUpResponse {

    @SerializedName("success")
    private boolean mSuccess;

    @SerializedName("error")
    private boolean mError;

    @SerializedName("type")
    private String mType;

    @SerializedName("message")
    private String mMessage;

    private transient Validation validation;

    @SerializedName("code")
    private int mCode;

    public static class Validation {
        @SerializedName("password")
        private List<String> password;
        @SerializedName("email")
        private List<String> email;

        public Validation(List<String> password, List<String> email) {
            this.password = password;
            this.email = email;
        }

        @Nullable
        public List<String> getPassword() {
            return password;
        }

        @Nullable
        public List<String> getEmail() {
            return email;
        }
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

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

    public static class SignUpDeserializer implements JsonDeserializer<SignUpResponse> {

        @Override
        public SignUpResponse deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            SignUpResponse response = new Gson().fromJson(json, SignUpResponse.class);
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("validation")) {
                JsonElement element = jsonObject.get("validation");
                if (element != null && !element.isJsonNull()) {
                    if (element.isJsonArray()) {
                        response.setValidation(null);
                    } else {
                        String valueStr = element.getAsString();
                        if (!TextUtils.isEmpty(valueStr)) {
                            Validation validation = new Gson().fromJson(valueStr, Validation.class);
                            response.setValidation(validation);
                        }
                    }
                }
            }

            return response;
        }
    }
}
