package com.perevod.perevodkassa.data.interceptors

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object CacheInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())

        // перезаписывает заголовок ответа для принудительного использования кэша
        val cacheControl: CacheControl = CacheControl.Builder()
                .maxAge(2, TimeUnit.MINUTES)
                .build()

        return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
    }
}