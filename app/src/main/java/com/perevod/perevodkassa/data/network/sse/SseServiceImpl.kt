package com.perevod.perevodkassa.data.network.sse

import com.google.gson.GsonBuilder
import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.domain.mapToPaymentStatusEvent
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

class SseServiceImpl(
    private val sseUrl: HttpUrl,
    private val prefs: PreferenceStorage
) : SseService {

    companion object {
        private const val TAG = "PaymentEvents"
    }

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    override suspend fun subscribeToEvents(): Flow<PaymentSuccessViewState<Any>> =
        channelFlow {
            val eventSourceListener = object : EventSourceListener() {

                override fun onOpen(eventSource: EventSource, response: Response) {
                    super.onOpen(eventSource, response)
                    Timber.tag(TAG).d("Connection Opened: ${response.message}")
                }

                override fun onClosed(eventSource: EventSource) {
                    super.onClosed(eventSource)
                    Timber.tag(TAG).d("Connection Closed")
                }

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    super.onEvent(eventSource, id, type, data)
                    Timber.tag(TAG).d("On Event Received! Data -: $data")
                    val paymentResponse = gson.fromJson(
                        data, PaymentStatusResponseObj::class.java
                    )
                    trySend(
                        PaymentSuccessViewState.OnUpdatePaymentStatus(paymentResponse.mapToPaymentStatusEvent())
                    )
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    super.onFailure(eventSource, t, response)
                    Timber.tag(TAG).d("On Failure -: ${response?.message}")
                    trySend(
                        PaymentSuccessViewState.PaymentError(
                            response?.message ?: ("Ошибка.\n" + "Повторите заново.")
                        )
                    )
                }
            }

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

            client.newCall(request).enqueue(responseCallback = object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Timber.tag(TAG).d("API Call Failure: %s", e.localizedMessage)
                    trySend(
                        PaymentSuccessViewState.PaymentError(
                            e.localizedMessage ?: ("Ошибка.\n" + "Повторите заново.")
                        )
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    Timber.tag(TAG).d( "APi Call Success: ${response.message}")
                }
            })
        }
            .flowOn(Dispatchers.Main)
}