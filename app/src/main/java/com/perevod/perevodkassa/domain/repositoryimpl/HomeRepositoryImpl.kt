package com.perevod.perevodkassa.domain.repositoryimpl

import com.perevod.perevodkassa.data.ApiService
import com.perevod.perevodkassa.data.network.Request
import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.domain.use_case.PrintType
import com.perevod.perevodkassa.presentation.global.extensions.makeRequest
import com.perevod.perevodkassa.presentation.screens.home.HomeViewState
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessViewState

class HomeRepositoryImpl(
    private val api: ApiService,
) : MainRepository {

    override suspend fun connectCashier(request: ConnectCashierRequest): HomeViewState<Any> =
        when (val result = makeRequest { api.connectCashier(request) }) {
            is Request.Success -> HomeViewState.SuccessConnectCashier(result.data)
            is Request.Error -> HomeViewState.Error(message = result.exception.message)
        }

    override suspend fun initCashier(request: InitCashierRequest): HomeViewState<Any> =
        when (val result = makeRequest { api.initCashier(request) }) {
            is Request.Success -> HomeViewState.SuccessInitCashier(result.data)
            is Request.Error -> HomeViewState.Error(message = result.exception.message)
        }

    override suspend fun printReceipt(
        printType: PrintType,
        orderUuid: String
    ): PaymentSuccessViewState<Any> =
        when (val result = makeRequest {
            api.printReceipt(
                printType.value,
                orderUuid
            )
        }) {
            is Request.Success -> PaymentSuccessViewState.SuccessPrintReceipt(result.data)
            is Request.Error -> PaymentSuccessViewState.Error(message = result.exception.message)
        }
}