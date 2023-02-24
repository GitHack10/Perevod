package com.perevod.perevodkassa.data.global

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
sealed class ErrorModel(val message: String, val errorCode: Int? = null) {

    data class SpecificError(val messageError: String) : ErrorModel(message = messageError)
    object NoConnection : ErrorModel(message = "Нет Интернета")
    object Unauthorized : ErrorModel(message = "Требуется авторизация", errorCode = 401)
    object TimeOut : ErrorModel(message = "Время ожидания вышло. Попробуйте позже")
    object ServerError : ErrorModel(message = "Ошибка получения данных с сервера. Попробуйте позже")

    data class Error(
        @SerializedName("message") val specificMessage: String?,
        @SerializedName("code") val code: Int?
    ) : ErrorModel(
        message = specificMessage ?: "Что-то пошло не так. Попробуйте позже.",
        errorCode = code
    )
}