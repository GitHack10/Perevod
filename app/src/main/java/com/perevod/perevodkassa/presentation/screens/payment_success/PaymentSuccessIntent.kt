package com.perevod.perevodkassa.presentation.screens.payment_success

sealed class PaymentSuccessIntent {

    object PrintReceipt : PaymentSuccessIntent()
    object ShowQrCode : PaymentSuccessIntent()
    object OnBackPressed : PaymentSuccessIntent()
}