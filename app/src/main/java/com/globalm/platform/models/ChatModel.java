package com.globalm.platform.models;

public class ChatModel {

    private int image;
    private String time;
    private String name;
    private String message;

    public ChatModel(int image, String time, String name, String message) {
        this.image = image;
        this.time = time;
        this.name = name;
        this.message = message;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
