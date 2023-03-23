package com.perevod.perevodkassa.data.network.sse

import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.data.global.PreferenceStorage
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

class SseServiceImpl(
    private val sseUrl: HttpUrl,
    private val prefs: PreferenceStorage
) : SseService {

    override suspend fun subscribeToEvents(
        eventSourceListener: EventSourceListener,
        responseCallback: Callback
    ) {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .readTimeout(15, TimeUnit.MINUTES)
            .writeTimeout(15, TimeUnit.MINUTES)
            .build()

        val httpUrl = sseUrl.newBuilder()
            .addQueryParameter("stream", prefs.lastOrderUuid)
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .addHeader("Accept", "text/event-stream")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Connection", "keep-alive")
            .build()

        EventSources.createFactory(client)
            .newEventSource(request = request, listener = eventSourceListener)

        client.newCall(request).enqueue(responseCallback)
    }
}