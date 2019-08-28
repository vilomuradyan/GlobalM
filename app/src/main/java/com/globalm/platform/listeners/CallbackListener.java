package com.globalm.platform.listeners;

public interface CallbackListener<T> {

    void onSuccess(T o);

    void onFailure(Throwable error);

}
