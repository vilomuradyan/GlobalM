package com.globalm.platform.network;

import com.globalm.platform.models.gopro.hero4.MediaResponse;
import com.globalm.platform.models.gopro.hero4.GoProStatusResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface GoProAPI {
    String GO_PRO_HERO4_BASE_URL = "http://10.5.5.9/";

    String GO_PRO_HERO4_VIDEO_RESOLUTION_PARAM = "64";
    String GO_PRO_HERO4_BITRATE_PARAM = "62";

    String GO_PRO_HERO4_HD_QUALITY = "7";
    String GO_PRO_HERO4_VIDEO_BITRATE_4MPS_QUALITY = "4000000";

    @GET("gp/gpMediaList")
    Call<MediaResponse> getFilesList();

    @GET
    @Streaming
    Call<ResponseBody> getVideoResponseBody(@Url String fileUrl);

    @GET("gp/gpControl/execute?p1=gpStream&a1=proto_v2&c1=restart")
    Call<GoProStatusResponse> restartStream();

    @GET("/gp/gpControl/setting/{param}/{option}")
    Call<Void> config(@Path("param") String param, @Path("option") String option);
}
