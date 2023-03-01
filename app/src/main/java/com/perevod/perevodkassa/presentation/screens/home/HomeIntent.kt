package com.perevod.perevodkassa.presentation.screens.home

sealed class HomeIntent {

    object OnBackPressed : HomeIntent()

    data class OnAmountChanged(
        val inputText: String?
    ) : HomeIntent()

    object OnButtonDoneClick : HomeIntent()
    object GoToPaymentSuccessScreen : HomeIntent()
}