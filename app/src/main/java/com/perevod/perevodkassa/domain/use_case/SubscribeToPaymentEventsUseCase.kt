package com.perevod.perevodkassa.domain.use_case

import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessViewState
import kotlinx.coroutines.flow.Flow

class SubscribeToPaymentEventsUseCase(
    private val repository: MainRepository,
) {

    suspend operator fun invoke(): Flow<PaymentSuccessViewState<Any>> =
        repository.subscribeToPaymentEvents()
}