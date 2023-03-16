package com.perevod.perevodkassa.presentation.screens.payment_success

import android.graphics.Bitmap
import com.perevod.perevodkassa.data.global.ErrorModel
import com.perevod.perevodkassa.data.network.sse.PaymentStatusEvent
import com.perevod.perevodkassa.domain.PrintModel

sealed class PaymentSuccessViewState<out T : Any> {

    object Idle : PaymentSuccessViewState<Unit>()
    object ShowLoading : PaymentSuccessViewState<Unit>()
    object HideLoading : PaymentSuccessViewState<Unit>()

    data class ShowQrCode(val qrBitmap: Bitmap?) : PaymentSuccessViewState<Unit>()
    data class SuccessPrintOrShowQr(val printModel: PrintModel) : PaymentSuccessViewState<Unit>()

    data class OnUpdatePaymentStatus(val paymentEvent: PaymentStatusEvent) : PaymentSuccessViewState<Unit>()

    data class PaymentSuccess(val message: String, val paperPrint: String?) : PaymentSuccessViewState<Unit>()
    data class PaymentError(val message: String) : PaymentSuccessViewState<Unit>()

    data class Error(val message: String) : PaymentSuccessViewState<ErrorModel>()
}