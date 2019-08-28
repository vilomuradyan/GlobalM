package com.globalm.platform.models;

import java.util.List;

public class PaginationData<T> {
    private List<T> items;
    private Pagination pagination;

    public PaginationData(List<T> items, Pagination pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public List<T> getItems() {
        return items;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
