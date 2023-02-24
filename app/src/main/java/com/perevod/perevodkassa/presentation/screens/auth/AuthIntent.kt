package com.perevod.perevodkassa.presentation.screens.auth

sealed class AuthIntent {

    data class SendCardNumberRequest(val cardNumber: String) : AuthIntent()

    object OpenShiftRequest : AuthIntent()
}