package com.perevod.perevodkassa.data.repository

import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.presentation.screens.home.HomeViewState

interface HomeRepository {

    suspend fun connectCashier(request: ConnectCashierRequest): HomeViewState<Any>
    suspend fun initCashier(request: InitCashierRequest): HomeViewState<Any>
    suspend fun printReceipt(): HomeViewState<Any>
}