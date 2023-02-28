package com.perevod.perevodkassa.domain.repositoryimpl

import com.perevod.perevodkassa.data.ApiService
import com.perevod.perevodkassa.data.network.Request
import com.perevod.perevodkassa.data.repository.HomeRepository
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.presentation.global.extensions.makeRequest
import com.perevod.perevodkassa.presentation.screens.home.HomeViewState

class HomeRepositoryImpl(
    private val api: ApiService,
) : HomeRepository {

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

    override suspend fun printReceipt(): HomeViewState<Any> =
        when (val result = makeRequest { api.printReceipt() }) {
            is Request.Success -> HomeViewState.SuccessPrintReceipt(result.data)
            is Request.Error -> HomeViewState.Error(message = result.exception.message)
        }
}