package com.perevod.perevodkassa.core.coroutines

sealed class RequestResult<out T : Any?> {

    data class Success<T : Any?>(val data: T) : RequestResult<T>()

    data class Error(val error: Throwable) : RequestResult<Nothing>()
}