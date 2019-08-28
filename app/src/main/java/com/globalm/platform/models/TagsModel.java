package com.globalm.platform.models;

public class TagsModel {

    private int imageDelete;
    private String tagsName;

    public TagsModel(int imageDelete, String tagsName) {
        this.imageDelete = imageDelete;
        this.tagsName = tagsName;
    }

    public int getImageDelete() {
        return imageDelete;
    }

    public void setImageDelete(int imageDelete) {
        this.imageDelete = imageDelete;
    }

    public String getTagsName() {
        return tagsName;
    }

    public void setTagsName(String tagsName) {
        this.tagsName = tagsName;
    }
}
