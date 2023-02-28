package com.perevod.perevodkassa.data

import com.perevod.perevodkassa.domain.PrintModel
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierResponse
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.InitCashierResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
interface ApiService {

    @POST("cashier/connect")
    suspend fun connectCashier(
        @Body requestBody: ConnectCashierRequest
    ): Response<ConnectCashierResponse>

    @POST("payment/cashier/init")
    suspend fun initCashier(
        @Body requestBody: InitCashierRequest
    ): Response<InitCashierResponse>

    @GET("payment/cashier/print")
    suspend fun printReceipt(
        @Query("type") type: String = "paper" // todo заменить на передачу выбранного типа
    ): Response<PrintModel>
}