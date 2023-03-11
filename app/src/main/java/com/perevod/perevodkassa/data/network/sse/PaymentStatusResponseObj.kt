package com.perevod.perevodkassa.data.network.sse

class PaymentStatusResponseObj(
    val orderUuid: String,
    val status: String,
    val message: String,
)