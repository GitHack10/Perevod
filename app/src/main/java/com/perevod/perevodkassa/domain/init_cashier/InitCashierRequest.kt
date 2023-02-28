package com.perevod.perevodkassa.domain.init_cashier

import com.google.gson.annotations.SerializedName

data class InitCashierRequest(
    val amount: Float,
    val currency: String,
    val payload: Payload
)

data class Payload(
    @SerializedName("some_filed")
    val someFiled: String
)