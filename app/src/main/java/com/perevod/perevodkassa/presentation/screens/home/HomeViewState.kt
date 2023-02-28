package com.perevod.perevodkassa.presentation.screens.home

import com.perevod.perevodkassa.data.global.ErrorModel
import com.perevod.perevodkassa.domain.PrintModel
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierResponse
import com.perevod.perevodkassa.domain.init_cashier.InitCashierResponse

sealed class HomeViewState<out T : Any> {

    object Idle : HomeViewState<Unit>()
    object ShowLoading : HomeViewState<Unit>()
    object HideLoading : HomeViewState<Unit>()
    object ClearState : HomeViewState<Unit>()

    data class FetchInputAmount(
        val amount: String,
        val btnEnabled: Boolean
    ) : HomeViewState<Unit>()

    data class SuccessConnectCashier(val connectCashierResponse: ConnectCashierResponse) : HomeViewState<Unit>()
    data class SuccessInitCashier(val initCashierResponse: InitCashierResponse) : HomeViewState<Unit>()
    data class SuccessPrintReceipt(val printModel: PrintModel) : HomeViewState<Unit>()
    data class Error(val message: String) : HomeViewState<ErrorModel>()
}