package com.assignment.injection.module;


import com.assignment.BuildConfig;
import com.assignment.presentation.helpers.IInternetStatus;
import com.assignment.presentation.helpers.InternetStatusImpl;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    private static final String HTTP_CACHE_PATH = "http-cache";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String PRAGMA = "Pragma";
    private static final int NETWORK_CONNECTION_TIMEOUT = 30; // 30 sec
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int CACHE_MAX_AGE = 2; // 2 min
    private static final int CACHE_MAX_STALE = 7; // 7 day
    private static final int API_RETRY_COUNT = 3;

    @Provides
    @Singleton
    @Named("networkTimeoutInSeconds")
    int provideNetworkTimeoutInSeconds() {
        return NETWORK_CONNECTION_TIMEOUT;
    }

    @Provides
    @Singleton
    @Named("cacheSize")
    long provideCacheSize() {
        return CACHE_SIZE;
    }

    @Provides
    @Singleton
    @Named("cacheMaxAge")
    int provideCacheMaxAgeMinutes() {
        return CACHE_MAX_AGE;
    }

    @Provides
    @Singleton
    @Named("cacheMaxStale")
    int provideCacheMaxStaleDays() {
        return CACHE_MAX_STALE;
    }

    @Provides
    @Singleton
    @Named("retryCount")
    public int provideApiRetryCount() {
        return API_RETRY_COUNT;
    }

    @Provides
    @Singleton
    @Named("cacheDir")
    File provideCacheDir() {
        return new File("build");
    }


    @Provides
    @Singleton
    @Named("base_url")
    String provideBaseUrl() {
        return "https://api.flickr.com";
    }

    @Provides
    @Singleton
    @Named("isDebug")
    boolean provideIsDebug() {
        return BuildConfig.DEBUG;
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClientWithAuthorization(@Named("loggingInterceptor") Interceptor loggingInterceptor,
                                                             @Named("networkTimeoutInSeconds") int networkTimeoutInSeconds,
                                                             @Named("isDebug") boolean isDebug,
                                                             Cache cache,
                                                             @Named("cacheInterceptor") Interceptor cacheInterceptor,
                                                             @Named("offlineInterceptor") Interceptor offlineCacheInterceptor,
                                                             @Named("retryInterceptor") Interceptor retryInterceptor) {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(offlineCacheInterceptor)
                .addInterceptor(retryInterceptor)
                .cache(cache)
                .connectTimeout(networkTimeoutInSeconds, TimeUnit.SECONDS);

        //show logs if app is in Debug mode
        if (isDebug) {
            okHttpClient.addInterceptor(loggingInterceptor);
        }

        return okHttpClient.build();
    }


    @Provides
    @Singleton
    public Cache provideCache(@Named("cacheDir") File cacheDir, @Named("cacheSize") long cacheSize) {
        Cache cache = null;

        try {
            cache = new Cache(new File(cacheDir.getPath(), HTTP_CACHE_PATH), cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cache;
    }

    @Provides
    @Singleton
    Logger provideLogger() {
        return Logger.getAnonymousLogger();
    }


    @Singleton
    @Provides
    @Named("loggingInterceptor")
    public Interceptor loggingInterceptor(Logger logger) {
        return chain -> {
            Request request = chain.request();

            long t1 = System.nanoTime();
            logger.info(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            logger.info(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        };
    }

    @Singleton
    @Provides
    @Named("cacheInterceptor")
    public Interceptor provideCacheInterceptor(@Named("cacheMaxAge") int maxAgeMin) {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(maxAgeMin, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .removeHeader(PRAGMA)
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }


    @Provides
    @Singleton
    public IInternetStatus provideStateManager(InternetStatusImpl internetStatus) {
        return internetStatus;
    }

    @Singleton
    @Provides
    @Named("offlineInterceptor")
    public Interceptor provideOfflineCacheInterceptor(IInternetStatus internetStatus, @Named("cacheMaxStale") int maxStaleDay) {
        return chain -> {
            Request request = chain.request();

            if (!internetStatus.isConnected()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(maxStaleDay, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }


    @Singleton
    @Provides
    @Named("retryInterceptor")
    public Interceptor provideRetryInterceptor(@Named("retryCount") int retryCount) {
        return chain -> {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;

            if (request.header("No-Retry") != null) {
                return chain.proceed(request);
            }
            int tryCount = 0;
            while (tryCount < retryCount && (null == response || !response.isSuccessful())) {
                // retry the request
                try {
                    response = chain.proceed(request);
                } catch (IOException e) {
                    exception = e;
                } finally {
                    tryCount++;
                }
            }

            // throw last exception
            if (null == response && null != exception)
                throw exception;

            // otherwise just pass the original response on
            return response;
        };
    }

    @Provides
    @Singleton
    @Named("GSON_FACTORY_NORMAL")
    public Converter.Factory provideGsonConverterFactoryNormal(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    @Named("GSON_FACTORY_AUTO_VALUE")
    public Converter.Factory provideGsonConverterFactoryAutoValue(@Named("AUTO_VALUE_GSON_FACTORY") Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    public CallAdapter.Factory provideRxJavaCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @Singleton
    @Named("RETROFIT_NORMAL")
    public Retrofit provideAuthenticatedRetrofitNormal(@Named("base_url") String baseUrl, @Named("GSON_FACTORY_NORMAL") Converter.Factory converterFactory, CallAdapter.Factory callAdapterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .client(okHttpClient)
                .build();
    }


}
