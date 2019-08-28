package com.globalm.platform.models;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Source implements Serializable {
    private Integer id;
    @SerializedName("created_at")
    private String createdAt;
    private String hash;
    private FileProperties props;

    public Source(Integer id, String createdAt, String hash, FileProperties props) {
        this.id = id;
        this.createdAt = createdAt;
        this.hash = hash;
        this.props = props;
    }

    public static class FileProperties {
        private Float duration;
        private String duration_human;
        private Long size;
        @SerializedName("size_human")
        private String sizeHuman;
        private Integer width;
        private Integer height;
        private String type;

        public FileProperties(Float duration,
                              String duration_human,
                              Long size,
                              String sizeHuman,
                              Integer width,
                              Integer height,
                              String type) {
            this.duration = duration;
            this.duration_human = duration_human;
            this.size = size;
            this.sizeHuman = sizeHuman;
            this.width = width;
            this.height = height;
            this.type = type;
        }

        public Float getDuration() {
            return duration;
        }

        public String getDuration_human() {
            return duration_human;
        }

        public Long getSize() {
            return size;
        }

        public String getSizeHuman() {
            return sizeHuman;
        }

        public Integer getWidth() {
            return width;
        }

        public Integer getHeight() {
            return height;
        }

        public String getType() {
            return type;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getHash() {
        return hash;
    }

    public FileProperties getProps() {
        return props;
    }
}
