package com.perevod.perevodkassa.presentation.screens.home

import com.perevod.perevodkassa.data.global.ErrorModel
import com.perevod.perevodkassa.domain.PrintModel

sealed class HomeViewState<out T : Any> {

    object Idle : HomeViewState<Unit>()
    object ShowLoading : HomeViewState<Unit>()
    object HideLoading : HomeViewState<Unit>()
    object ShowContent : HomeViewState<Unit>()
    object HideContent : HomeViewState<Unit>()
    object ShowEmptyContent : HomeViewState<Unit>()
    object HideEmptyContent : HomeViewState<Unit>()

    data class UpdateTotalValues(
        val count: Int,
        val price: Double
    ) : HomeViewState<Int>()

    data class SuccessPrintReceipt(val printModel: PrintModel) : HomeViewState<Unit>()
    data class SuccessStaffClose(val printModel: PrintModel) : HomeViewState<Unit>()
    data class Error(val message: String) : HomeViewState<ErrorModel>()
}