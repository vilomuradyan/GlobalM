package com.globalm.platform.utils;

import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.SocialSignInResponse;
import com.globalm.platform.network.RequestManager;

import retrofit2.Response;

public abstract class SocialUtil {
    public static final int SOCIAL_NETWORK_FACEBOOK = 0;
    public static final int SOCIAL_NETWORK_LINKED_IN = 2;
    public static final int SOCIAL_NETWORK_GOOGLE = 1;
    public static final int SOCIAL_NETWORK_TWITTER = 3;

    public static void loginSocial(int networkCode, CallbackListener<String> listener) {
        socialSignIn(networkCode, listener);
    }

    public static void connectSocialNetwork(int networkCode, CallbackListener<String> listener) {
        connectNetwork(networkCode, listener);
    }

    private static void socialSignIn(int networkCode, CallbackListener<String> listener) {
        RequestManager.getInstance().socialSignIn(new CallbackListener<SocialSignInResponse>() {
            @Override
            public void onSuccess(SocialSignInResponse socialSignInResponse) {
                if (socialSignInResponse != null) {
                    listener.onSuccess(socialSignInResponse
                            .getData()
                            .getItems()
                            .get(networkCode)
                            .getUrl());
                } else {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                listener.onFailure(error);
            }
        });
    }

    private static void  connectNetwork(int networkCode, CallbackListener<String> listener) {
        RequestManager.getInstance().connectNetwork(new CallbackListener<SocialSignInResponse>() {
            @Override
            public void onSuccess(SocialSignInResponse socialSignInResponse) {
                if (socialSignInResponse != null) {
                    listener.onSuccess(socialSignInResponse
                            .getData()
                            .getItems()
                            .get(networkCode)
                            .getUrl());
                } else {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                listener.onFailure(error);
            }
        });
    }
}
