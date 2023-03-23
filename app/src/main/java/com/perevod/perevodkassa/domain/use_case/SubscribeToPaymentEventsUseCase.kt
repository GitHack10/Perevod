package com.perevod.perevodkassa.domain.use_case

import com.perevod.perevodkassa.data.network.sse.SseService
import okhttp3.Callback
import okhttp3.sse.EventSourceListener

class SubscribeToPaymentEventsUseCase(
    private val sseService: SseService,
) {

    suspend operator fun invoke(
        eventSourceListener: EventSourceListener,
        responseCallback: Callback
    ) {
        sseService.subscribeToEvents(
            eventSourceListener,
            responseCallback
        )
    }
}