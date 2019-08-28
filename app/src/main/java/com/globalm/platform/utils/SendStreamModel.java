package com.globalm.platform.utils;

import com.globalm.platform.models.StreamModel;

public class SendStreamModel {

    private StreamModel mStreamModel;

    public SendStreamModel(StreamModel streamModel) {
        mStreamModel = streamModel;
    }

    public StreamModel getStreamModel() {
        return mStreamModel;
    }
}
