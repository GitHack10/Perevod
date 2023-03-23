package com.perevod.perevodkassa.data.network.sse

import okhttp3.Callback
import okhttp3.sse.EventSourceListener

interface SseService {

    suspend fun subscribeToEvents(
        eventSourceListener: EventSourceListener,
        responseCallback: Callback
    )
}