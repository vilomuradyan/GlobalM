package com.globalm.platform.network;

import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.gopro.hero4.GoProStatusResponse;
import com.globalm.platform.models.gopro.hero4.MediaResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Path;

public class GoProRequestManager extends BaseRequestManager {
    private static volatile GoProRequestManager instance;

    public static GoProRequestManager getInstance() {
        if (instance == null) {
            synchronized (GoProRequestManager.class) {
                if (instance == null) {
                    instance = new GoProRequestManager();
                }
            }
        }

        return instance;
    }

    private GoProAPI goProAPI;

    private GoProRequestManager() {
        goProAPI = ResourceFactory.createGoProApiClient();
    }

    public void getFilesList(CallbackListener<MediaResponse> callbackListener) {
        goProAPI.getFilesList().enqueue(getCallback(callbackListener));
    }

    public void getVideoResponseBody(String fileUrl, CallbackListener<ResponseBody> callbackListener) {
        goProAPI.getVideoResponseBody(fileUrl).enqueue(getCallback(callbackListener));
    }

    public void restartStream(CallbackListener<GoProStatusResponse> callbackListener) {
        goProAPI.restartStream().enqueue(getCallback(callbackListener));
    }

    public void config(String param, String option, CallbackListener<Void> callbackListener) {
        goProAPI.config(param, option).enqueue(getCallback(callbackListener));
    }
}
