package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

public class FileData {
    private Integer id;
    @Nullable
    @SerializedName("accessType")
    private String accessType;
    private Owner owner;
    private Source source;
    @Nullable
    private Preview preview;

    public FileData(Integer id,
                    String accessType,
                    Owner owner,
                    Source source,
                    @Nullable Preview preview) {
        this.id = id;
        this.accessType = accessType;
        this.owner = owner;
        this.source = source;
        this.preview = preview;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public String getAccessType() {
        return accessType;
    }

    public Owner getOwner() {
        return owner;
    }

    public Source getSource() {
        return source;
    }

    @Nullable
    public Preview getPreview() {
        return preview;
    }
}