package com.globalm.platform.models;

public class GetContentListModel<T> {
    private T items;

    public GetContentListModel(T item) {
        this.items = item;
    }

    public T getItems() {
        return items;
    }
}
