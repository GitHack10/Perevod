package com.perevod.perevodkassa.data.repository

import com.perevod.perevodkassa.presentation.screens.auth.AuthViewState

interface AuthRepository {

    suspend fun makeAuth(cardNumber: String): AuthViewState<Any>
    suspend fun staffOpen(cardNumber: String): AuthViewState<Any>
}