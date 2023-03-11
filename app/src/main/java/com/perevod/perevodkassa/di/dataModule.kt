package com.perevod.perevodkassa.di

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.data.ApiService
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.data.global.SharedPreferenceStorage
import com.perevod.perevodkassa.data.interceptors.AppRequestInterceptor
import com.perevod.perevodkassa.data.interceptors.CacheInterceptor
import com.perevod.perevodkassa.data.network.sse.SseService
import com.perevod.perevodkassa.data.network.sse.SseServiceImpl
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val dataModule = module {

    single<PreferenceStorage> { SharedPreferenceStorage(context = androidContext()) }

    single {

        val cacheSize: Long = 10 * 1024 * 1024 // cache size in MB
        val cache = Cache(File(androidContext().cacheDir, "http-cache"), cacheSize)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AppRequestInterceptor)
            .addNetworkInterceptor(CacheInterceptor)
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .cache(cache)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    factory<ApiService> { get<Retrofit>().create(ApiService::class.java) }

    factory<SseService> {
        SseServiceImpl(
            SSE_URL.toHttpUrl(),
            get()
        )
    }
}

private const val BASE_URL = "https://api-stub.perevod.io:8443/api/v2/"
private const val SSE_URL = "http://217.107.34.221:8777/events"