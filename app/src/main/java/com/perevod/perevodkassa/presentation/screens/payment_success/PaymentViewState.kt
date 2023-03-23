package com.perevod.perevodkassa.presentation.screens.payment_success

import android.graphics.Bitmap
import com.perevod.perevodkassa.data.global.ErrorModel
import com.perevod.perevodkassa.data.network.sse.PaymentStatusEvent
import com.perevod.perevodkassa.domain.PrintModel

sealed class PaymentViewState<out T : Any> {

    object Idle : PaymentViewState<Unit>()
    object ShowLoading : PaymentViewState<Unit>()
    object HideLoading : PaymentViewState<Unit>()

    data class ShowQrCode(val qrBitmap: Bitmap?) : PaymentViewState<Unit>()
    data class SuccessPrintOrShowQr(val printModel: PrintModel) : PaymentViewState<Unit>()

    data class OnUpdatePaymentStatus(val paymentEvent: PaymentStatusEvent) : PaymentViewState<Unit>()

    data class PaymentSuccess(val message: String, val paperPrint: String?) : PaymentViewState<Unit>()
    data class PaymentError(val message: String) : PaymentViewState<Unit>()

    data class Error(val message: String) : PaymentViewState<ErrorModel>()
}