package com.globalm.platform.models;

public class GetContentDataModel <T> {
    private T item;

    public GetContentDataModel(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
