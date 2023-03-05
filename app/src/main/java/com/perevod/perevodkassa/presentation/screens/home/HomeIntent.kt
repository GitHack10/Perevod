package com.perevod.perevodkassa.presentation.screens.home

sealed class HomeIntent {

    object OnBackPressed : HomeIntent()

    data class OnAmountChanged(
        val inputText: KeyboardNumber
    ) : HomeIntent()

    data class GoToPaymentSuccessScreen(val amount: Int) : HomeIntent()

    object OnButtonDoneClick : HomeIntent()
}