package com.globalm.platform.models;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class AccountStatusResponse {
    private Boolean success;
    private Integer code;
    private Data data;

    public AccountStatusResponse(Boolean success, Integer code, Data data) {
        this.success = success;
        this.code = code;
        this.data = data;
    }

    public static class Data {
        private List<Item> items;

        public Data(List<Item> items) {
            this.items = items;
        }

        public static class Item {
            public static final String TYPE_TWITTER = "twitter";
            public static final String TYPE_FACEBOOK = "facebook";
            public static final String TYPE_GOOGLE = "google";

            public static final String STATUS_CONNECTED = "connected";

            private Long id;
            private String type;
            private String status;
            private String uid;

            public Item(Long id, String type, String status, String uid) {
                this.id = id;
                this.type = type;
                this.status = status;
                this.uid = uid;
            }

            public Long getId() {
                return id;
            }

            public String getType() {
                return type;
            }

            public String getStatus() {
                return status;
            }

            public String getUid() {
                return uid;
            }

            @StringDef({
                    TYPE_TWITTER,
                    TYPE_FACEBOOK,
                    TYPE_GOOGLE})
            @Retention(RetentionPolicy.SOURCE)
            public @interface SocialType {
            }
        }

        public List<Item> getItems() {
            return items;
        }
    }

    public Boolean getSuccess() {
        return success;
    }

    public Integer getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }
}

