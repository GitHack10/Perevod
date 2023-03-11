package com.perevod.perevodkassa.data.network.sse

import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessViewState
import kotlinx.coroutines.flow.Flow

interface SseService {

    suspend fun subscribeToEvents(): Flow<PaymentSuccessViewState<Any>>
}