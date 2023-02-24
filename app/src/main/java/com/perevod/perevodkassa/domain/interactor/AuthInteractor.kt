package com.perevod.perevodkassa.domain.interactor

import com.perevod.perevodkassa.data.repository.AuthRepository

class AuthInteractor(
    private val repository: AuthRepository
) {

    suspend fun makeAuth(cardNumber: String) = repository.makeAuth(cardNumber = cardNumber)

    suspend fun staffOpen(cardNumber: String) = repository.staffOpen(cardNumber = cardNumber)
}