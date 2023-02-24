package com.perevod.perevodkassa.data.interceptors

import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.data.global.PreferenceStorage
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object AppRequestInterceptor : Interceptor, KoinComponent {

    private val prefs: PreferenceStorage by inject()

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("deviceId", prefs.deviceId)

        val requestBuilder = originalRequest.newBuilder().url(newUrl.build())

        if (BuildConfig.DEBUG) {
            Timber.tag("REQUEST_URL_PRINT").e(newUrl.toString())
        }

        return chain.proceed(requestBuilder.build())
    }
}