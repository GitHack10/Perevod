package com.perevod.perevodkassa.presentation.screens.home

sealed class HomeIntent {

    object OnBackPressed : HomeIntent()

    data class OnAmountChanged(
        val inputText: String,
        val keyNumber: KeyboardNumber
    ) : HomeIntent()

    data class GoToPaymentSuccessScreen(val amount: Float) : HomeIntent()

    object OnButtonDoneClick : HomeIntent()
}