package com.globalm.platform.models.gopro.hero4;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

import java.util.List;

public class MediaResponse {
    private String id;
    private List<Media> media;

    public MediaResponse(String id, List<Media> media) {
        this.id = id;
        this.media = media;
    }

    public static class Media {
        @Expose
        private String d;
        @Expose
        private List<FS> fs;

        public Media(String d, List<FS> fs) {
            this.d = d;
            this.fs = fs;
        }

        public static class FS {
            private String n;
            private String mod;
            @Nullable
            private String ls;
            private String s;

            public FS(String n, String mod, @Nullable String ls, String s) {
                this.n = n;
                this.mod = mod;
                this.ls = ls;
                this.s = s;
            }

            public String getN() {
                return n;
            }

            public String getMod() {
                return mod;
            }

            @Nullable
            public String getLs() {
                return ls;
            }

            public String getS() {
                return s;
            }
        }

        public String getD() {
            return d;
        }

        public List<FS> getFs() {
            return fs;
        }
    }

    public String getId() {
        return id;
    }

    public List<Media> getMedia() {
        return media;
    }
}
