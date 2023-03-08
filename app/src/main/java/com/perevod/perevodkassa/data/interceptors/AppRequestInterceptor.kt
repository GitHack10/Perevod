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

        if (originalUrl.pathSegments.contains("connect")) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest
            .newBuilder()
            .addHeader("SDK-API-Key", "${prefs.sdkKey}")

        if (BuildConfig.DEBUG) {
            Timber.tag("REQUEST_HEADER_SDK_KEY_PRINT").d("Header: ${prefs.sdkKey}")
        }

        return chain.proceed(requestBuilder.build())
    }
}