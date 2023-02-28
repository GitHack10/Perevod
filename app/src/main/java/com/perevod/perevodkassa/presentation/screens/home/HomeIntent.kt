package com.perevod.perevodkassa.presentation.screens.home

sealed class HomeIntent {

    object PrintReceipt : HomeIntent()
    object OnBackPressed : HomeIntent()

    data class OnAmountChanged(
        val inputText: String?
    ) : HomeIntent()

    object OnButtonDoneClick : HomeIntent()
}