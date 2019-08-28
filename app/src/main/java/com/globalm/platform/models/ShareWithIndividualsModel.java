package com.globalm.platform.models;

public class ShareWithIndividualsModel {

    private int imageUser;
    private String UserEmail;
    private int imageDelete;

    public ShareWithIndividualsModel(int imageUser, String userEmail, int imageDelete) {
        this.imageUser = imageUser;
        UserEmail = userEmail;
        this.imageDelete = imageDelete;
    }

    public int getImageUser() {
        return imageUser;
    }

    public void setImageUser(int imageUser) {
        this.imageUser = imageUser;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public int getImageDelete() {
        return imageDelete;
    }

    public void setImageDelete(int imageDelete) {
        this.imageDelete = imageDelete;
    }
}
