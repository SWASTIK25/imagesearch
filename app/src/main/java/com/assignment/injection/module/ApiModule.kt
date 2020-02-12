package com.assignment.injection.module


import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.presentation.helpers.InternetStatusImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    @Named("networkTimeoutInSeconds")
    internal fun provideNetworkTimeoutInSeconds(): Int {
        return NETWORK_CONNECTION_TIMEOUT
    }

    @Provides
    @Singleton
    @Named("cacheSize")
    internal fun provideCacheSize(): Long {
        return CACHE_SIZE
    }

    @Provides
    @Singleton
    @Named("cacheMaxAge")
    internal fun provideCacheMaxAgeMinutes(): Int {
        return CACHE_MAX_AGE
    }

    @Provides
    @Singleton
    @Named("cacheMaxStale")
    internal fun provideCacheMaxStaleDays(): Int {
        return CACHE_MAX_STALE
    }

    @Provides
    @Singleton
    @Named("retryCount")
    fun provideApiRetryCount(): Int {
        return API_RETRY_COUNT
    }

    @Provides
    @Singleton
    @Named("cacheDir")
    internal fun provideCacheDir(): File {
        return File("build")
    }


    @Provides
    @Singleton
    @Named("base_url")
    internal fun provideBaseUrl(): String {
        return "https://api.flickr.com"
    }


    @Singleton
    @Provides
    fun provideOkHttpClientWithAuthorization(@Named("loggingInterceptor") loggingInterceptor: Interceptor,
                                             @Named("networkTimeoutInSeconds") networkTimeoutInSeconds: Int,
                                             @Named("isDebug") isDebug: Boolean,
                                             @Named("cacheInterceptor") cacheInterceptor: Interceptor,
                                             @Named("offlineInterceptor") offlineCacheInterceptor: Interceptor,
                                             @Named("retryInterceptor") retryInterceptor: Interceptor): OkHttpClient {

        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(offlineCacheInterceptor)
                .addInterceptor(retryInterceptor)
                .connectTimeout(networkTimeoutInSeconds.toLong(), TimeUnit.SECONDS)

        //show logs if app is in Debug mode
        if (isDebug) {
            okHttpClient.addInterceptor(loggingInterceptor)
        }

        return okHttpClient.build()
    }


    @Provides
    @Singleton
    internal fun provideLogger(): Logger {
        return Logger.getAnonymousLogger()
    }
    @Singleton
    @Provides
    @Named("loggingInterceptor")
    fun loggingInterceptor(logger: Logger): Interceptor {
        return Interceptor {
            val request = it.request()

            val t1 = System.nanoTime()
            logger.info(String.format("Sending request %s on %s%n%s",
                    request.url(), it.connection(), request.headers()))

            val response = it.proceed(request)

            val t2 = System.nanoTime()
            logger.info(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6, response.headers()))

            response
        }
    }

    @Singleton
    @Provides
    @Named("cacheInterceptor")
    fun provideCacheInterceptor(@Named("cacheMaxAge") maxAgeMin: Int): Interceptor {
        return Interceptor {
            val response = it.proceed(it.request())

            val cacheControl = CacheControl.Builder()
                    .maxAge(maxAgeMin, TimeUnit.MINUTES)
                    .build()

            response.newBuilder()
                    .removeHeader(PRAGMA)
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build()
        }
    }


    @Provides
    @Singleton
    fun provideStateManager(internetStatus: InternetStatusImpl): IInternetStatus {
        return internetStatus
    }

    @Singleton
    @Provides
    @Named("offlineInterceptor")
    fun provideOfflineCacheInterceptor(internetStatus: IInternetStatus, @Named("cacheMaxStale") maxStaleDay: Int): Interceptor {
        return Interceptor {
            var request = it.request()

            if (!internetStatus.isConnected) {
                val cacheControl = CacheControl.Builder()
                        .maxStale(maxStaleDay, TimeUnit.DAYS)
                        .build()

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build()
            }

            it.proceed(request)
        }
    }


    @Singleton
    @Provides
    @Named("retryInterceptor")
    fun provideRetryInterceptor(@Named("retryCount") retryCount: Int): Interceptor {
        return Interceptor {
            val request = it.request()
            var response: Response? = null
            var exception: IOException? = null

            if (request.header("No-Retry") != null) {
                 it.proceed(request)
            }
            var tryCount = 0
            while (tryCount < retryCount && (null == response || !response.isSuccessful)) {
                // retry the request
                try {
                    response = it.proceed(request)
                } catch (e: IOException) {
                    exception = e
                } finally {
                    tryCount++
                }
            }

            // throw last exception
            if (null == response && null != exception)
                throw exception

            // otherwise just pass the original response on
            response
        }
    }

    @Provides
    @Singleton
    @Named("GSON_FACTORY_NORMAL")
    fun provideGsonConverterFactoryNormal(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    @Named("GSON_FACTORY_AUTO_VALUE")
    fun provideGsonConverterFactoryAutoValue(@Named("AUTO_VALUE_GSON_FACTORY") gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRxJavaCallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    @Singleton
    fun provideAuthenticatedRetrofitNormal(@Named("base_url") baseUrl: String, @Named("GSON_FACTORY_NORMAL") converterFactory: Converter.Factory, callAdapterFactory: CallAdapter.Factory, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .client(okHttpClient)
                .build()
    }

    companion object {

        private const val HTTP_CACHE_PATH = "http-cache"
        private const val CACHE_CONTROL = "Cache-Control"
        private const val PRAGMA = "Pragma"
        private const val NETWORK_CONNECTION_TIMEOUT = 30 // 30 sec
        private const val CACHE_SIZE = (10 * 1024 * 1024).toLong() // 10 MB
        private const val CACHE_MAX_AGE = 2 // 2 min
        private const val CACHE_MAX_STALE = 7 // 7 day
        private const val API_RETRY_COUNT = 3
    }


}
