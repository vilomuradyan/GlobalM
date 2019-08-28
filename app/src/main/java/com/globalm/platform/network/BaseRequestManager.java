package com.globalm.platform.network;

import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.utils.events.LogoutEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.globalm.platform.utils.Settings.loadString;

public class BaseRequestManager {

    private <T> void onResponse(Response<T> response, CallbackListener<T> callbackListener) {
        int code = response.code();
        if (code == 200) {
            callbackListener.onSuccess(response.body());
        } else if (code == 401) {
            EventBus.getDefault().post(new LogoutEvent());
        }
    }

    protected  <T> Callback<T> getCallback(CallbackListener<T> callbackListener) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                BaseRequestManager.this.onResponse(response, callbackListener);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callbackListener.onFailure(t);
            }
        };
    }

    protected String getAccessToken() {
        return "Bearer " + loadString("access_token");
    }
}
