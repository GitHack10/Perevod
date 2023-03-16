package com.perevod.perevodkassa.domain

import com.perevod.perevodkassa.data.network.sse.PaymentStatus
import com.perevod.perevodkassa.data.network.sse.PaymentStatusEvent
import com.perevod.perevodkassa.data.network.sse.PaymentStatusResponseObj

fun PaymentStatusResponseObj.mapToPaymentStatusEvent() = PaymentStatusEvent(
    orderUuid,
    message,
    PaymentStatus.getStatusByValue(status),
    paperPrint
)