package com.globalm.platform.broadcaster.utils;

import java.io.Serializable;

public class Resolution implements Serializable {

    public final int width;
    public final int height;

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }
}