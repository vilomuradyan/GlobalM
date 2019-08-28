package com.globalm.platform.models;

public class PricingModel {

    private String Title;
    private String price;
    private int imageRemove;

    public PricingModel(String title, String price, int imageRemove) {
        Title = title;
        this.price = price;
        this.imageRemove = imageRemove;
    }

    public String getTitle() {
        return Title;
    }

    public String getPrice() {
        return price;
    }

    public int getImageRemove() {
        return imageRemove;
    }
}
