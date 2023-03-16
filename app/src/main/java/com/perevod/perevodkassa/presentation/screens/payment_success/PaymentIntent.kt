package com.perevod.perevodkassa.presentation.screens.payment_success

sealed class PaymentIntent {

    object ShowQrCode : PaymentIntent()
    object OnBackPressed : PaymentIntent()

    data class ShowErrorScreen(val message: String) : PaymentIntent()
    data class ShowSuccessScreen(val message: String, val paperPrint: String?) : PaymentIntent()
}