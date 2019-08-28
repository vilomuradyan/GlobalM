package com.globalm.platform.network;

import android.annotation.SuppressLint;
import android.util.Log;

import com.globalm.platform.GlobalmApplication;
import com.globalm.platform.models.SignUpResponse;
import com.globalm.platform.models.assingments.ResponseValidationError;
import com.globalm.platform.models.assingments.SendRespondResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.globalm.platform.BuildConfig.API_URL;
import static com.globalm.platform.utils.Constants.DEFAULT_DATE_FORMAT;
import static com.globalm.platform.utils.Constants.HEADER_CACHE_CONTROL;
import static com.globalm.platform.utils.Constants.HEADER_PRAGMA;
import static com.globalm.platform.utils.Settings.loadString;

class ResourceFactory {

    private static Retrofit mRootResourceFactory;
    private static Retrofit goProRetrofit;

    @SuppressWarnings("SameParameterValue")
    static <T> T createResource(Class<T> resourceClass) {
        if (mRootResourceFactory == null) {
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                OkHttpClient okHttpClient = new OkHttpClient.Builder().sslSocketFactory(new TLSSocketFactory(sslContext.getSocketFactory()), (X509TrustManager) trustAllCerts[0]).hostnameVerifier((hostname, session) -> true)
                        .addInterceptor(chain -> {
                            if(loadString("access_token").isEmpty()){
                                return chain.proceed(chain.request());
                            }
                            String loadString = loadString("access_token");
                            Request request = chain
                                    .request()
                                    .newBuilder()
                                    .addHeader("Token", loadString)
                                    .build();
                            Log.d("TESTING", "Token " + loadString("access_token"));
                            return chain.proceed(request);

                        }).connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        //TODO WARNING ! IF UPLOADING LARGE SIZED FILES, APP CAN THROW "OUT OF MEMORY" EXCEPTION BECAUSE OF LARGE LOG SIZES !
                        //DISABLE THEM WHEN USING PRODUCTION !!!!
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

                        .addInterceptor(provideOfflineCacheInterceptor())
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .cache(provideCache())
                        .build();
                mRootResourceFactory = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(createGsonConverter())
                        .client(okHttpClient)
                        .build();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }

        return mRootResourceFactory.create(resourceClass);
    }

    private static Converter.Factory createGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SignUpResponse.class, new SignUpResponse.SignUpDeserializer());
        gsonBuilder.registerTypeAdapter(SendRespondResponse.class, new SendRespondResponse.RespondDeserializer());
        gsonBuilder
                .setLenient()
                .setDateFormat(DEFAULT_DATE_FORMAT);
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);
    }

    private static Cache provideCache() {
        Cache cache = null;

        try {
            cache = new Cache(new File(GlobalmApplication.getInstance().getCacheDir(), "http-cache"), 10 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cache;
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            CacheControl cacheControl;
            if (GlobalmApplication.getInstance().isNetworkAvailable()) {
                cacheControl = new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build();
            } else {
                cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build();
            }

            return chain.proceed(chain.request()).newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            if (!GlobalmApplication.getInstance().isNetworkAvailable()) {
                CacheControl cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build();
                request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }

    static GoProAPI createGoProApiClient() {
        synchronized (ResourceFactory.class) {
            if (goProRetrofit == null) {
                goProRetrofit = new Retrofit
                        .Builder()
                        .baseUrl(GoProAPI.GO_PRO_HERO4_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getLogger())
                        .build();
            }
        }

        return goProRetrofit.create(GoProAPI.class);
    }

    private static OkHttpClient getLogger() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }
}
