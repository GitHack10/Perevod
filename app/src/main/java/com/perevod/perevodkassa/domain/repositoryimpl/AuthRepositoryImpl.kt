package com.perevod.perevodkassa.domain.repositoryimpl

import com.perevod.perevodkassa.data.ApiService
import com.perevod.perevodkassa.data.network.Request
import com.perevod.perevodkassa.data.repository.AuthRepository
import com.perevod.perevodkassa.presentation.global.extensions.makeRequest
import com.perevod.perevodkassa.presentation.screens.auth.AuthViewState

class AuthRepositoryImpl(
    private val api: ApiService
) : AuthRepository {

    override suspend fun makeAuth(cardNumber: String) =
        when (val result = makeRequest { api.makeAuth(phone = cardNumber.toInt()) }) {
            is Request.Success -> AuthViewState.OnSuccess(response = result.data)
            is Request.Error -> AuthViewState.OnError(message = result.exception.message)
        }

    override suspend fun staffOpen(cardNumber: String) =
        when (val result = makeRequest { api.staffOpen(cardNumber = cardNumber) }) {
            is Request.Success -> AuthViewState.OnSuccess(response = result.data)
            is Request.Error -> AuthViewState.OnError(message = result.exception.message)
        }
}