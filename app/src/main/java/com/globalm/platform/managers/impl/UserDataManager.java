package com.globalm.platform.managers.impl;

import com.globalm.platform.managers.IUserDataManager;

import static com.globalm.platform.utils.Settings.loadString;

public class UserDataManager implements IUserDataManager {
    private static final String keyAccessToken = "access_token";
    private static UserDataManager instance;

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    private UserDataManager() {}

    @Override
    public boolean isUserLoggedIn() {
        return !loadString(keyAccessToken).equals("");
    }
}
