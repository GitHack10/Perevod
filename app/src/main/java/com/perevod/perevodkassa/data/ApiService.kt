package com.perevod.perevodkassa.data

import com.perevod.perevodkassa.data.global.BaseResponse
import com.perevod.perevodkassa.domain.PrintModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
interface ApiService {

    /**
     * Запрос на привязку кассы по номеру телефона и deviceId
     *
     * @param phone - номер телефона пользователя
     */
    @POST("connect")
    suspend fun makeAuth(
        @Query("phone") phone: Int
    ): Response<BaseResponse>

    @GET("receipt")
    suspend fun printReceipt(): Response<PrintModel>

    /**
     * Запрос на открытие смены
     */
    @POST("staff")
    suspend fun staffOpen(
        @Query("cardNumber") cardNumber: String
    ): Response<BaseResponse>

    /**
     * Запрос на закрытие смены
     */
    @POST("staff_close")
    suspend fun staffClose(): Response<PrintModel>
}