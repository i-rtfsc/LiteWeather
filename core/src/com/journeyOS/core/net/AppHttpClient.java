package com.journeyOS.core.net;

import com.journeyOS.base.network.NetWork;
import com.journeyOS.base.persistence.FileHelper;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.core.CoreManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppHttpClient {
    private static final String BASE_URL = "https://free-api.heweather.com/s6/";
    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private final static int HTTP_TIME_OUT = 16 * 1000;
    private static final int MAX_AGE = 60 * 10;
    private static final int MAX_STALE = 60 * 60 * 24;
    private volatile static AppHttpClient sAppHttpClient;
    private Map<String, Object> mService = new HashMap<>();
    private Retrofit mRetrofit;

    private AppHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HTTP_TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(cacheInterceptor())
                .cache(cache())
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    private Cache cache() {
        final File cacheDir = FileHelper.buildFile("HttpResponseCache");
        return (new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));

    }

    private Interceptor cacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                if (!NetWork.isAvailable(CoreManager.getContext())) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }
                Response originalResponse = chain.proceed(request);

                if (NetWork.isAvailable(CoreManager.getContext())) {
                    return originalResponse.newBuilder().header("Cache-Control", "public ,max-age=" + MAX_AGE).build();
                } else {
                    return originalResponse.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE).build();
                }
            }
        };
    }

    public static AppHttpClient getInstance() {
        if (sAppHttpClient == null) {
            synchronized (AppHttpClient.class) {
                if (sAppHttpClient == null) {
                    sAppHttpClient = new AppHttpClient();
                }
            }
        }
        return sAppHttpClient;
    }

    public synchronized <T> T getService(Class<T> apiInterface) {
        String serviceName = apiInterface.getName();
        if (BaseUtils.isNull(mService.get(serviceName))) {
            T service = mRetrofit.create(apiInterface);
            mService.put(serviceName, service);
            return service;
        } else {
            return (T) mService.get(serviceName);
        }
    }

}
