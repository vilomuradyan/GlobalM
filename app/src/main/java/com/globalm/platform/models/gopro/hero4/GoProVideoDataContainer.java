package com.globalm.platform.models.gopro.hero4;

public class GoProVideoDataContainer {
    private String thumbnailUrl;
    private String folder;
    private String fileName;

    public GoProVideoDataContainer(String thumbnailUrl, String folder, String fileName) {
        this.thumbnailUrl = thumbnailUrl;
        this.folder = folder;
        this.fileName = fileName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getFolder() {
        return folder;
    }

    public String getFileName() {
        return fileName;
    }
}
