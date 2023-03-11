package com.perevod.perevodkassa.data.repository

import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.domain.use_case.PrintType
import com.perevod.perevodkassa.presentation.screens.home.HomeViewState
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessViewState
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun connectCashier(request: ConnectCashierRequest): HomeViewState<Any>
    suspend fun initCashier(request: InitCashierRequest): HomeViewState<Any>
    suspend fun printOrShowQr(
        printType: PrintType,
        orderUuid: String,
    ): PaymentSuccessViewState<Any>

    suspend fun subscribeToPaymentEvents(): Flow<PaymentSuccessViewState<Any>>
}