package com.perevod.perevodkassa.presentation.screens.auth

import com.perevod.perevodkassa.data.global.BaseResponse
import com.perevod.perevodkassa.data.global.ErrorModel

sealed class AuthViewState<out A : Any> {
    object Idle : AuthViewState<Unit>()
    object ShowLoading : AuthViewState<Unit>()
    object HideLoading : AuthViewState<Unit>()
    data class OnSuccess(val response: BaseResponse) : AuthViewState<BaseResponse>()
    data class OnError(val message: String) : AuthViewState<ErrorModel>()
}