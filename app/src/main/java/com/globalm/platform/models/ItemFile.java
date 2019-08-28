package com.globalm.platform.models;

public class ItemFile {
    private int id;
    private String status;
    private String title;
    private String subtitle;
    private Owner owner;

    public ItemFile(int id, String status, String title, String subtitle, Owner owner) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.subtitle = subtitle;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Owner getOwner() {
        return owner;
    }
}
