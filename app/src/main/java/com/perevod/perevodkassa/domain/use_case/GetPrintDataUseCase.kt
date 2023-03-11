package com.perevod.perevodkassa.domain.use_case

import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessViewState

class GetPrintDataUseCase(
    private val repository: MainRepository
) {

    suspend operator fun invoke(input: Params): PaymentSuccessViewState<Any> =
        repository.printOrShowQr(
            input.printType,
            input.orderUuid,
        )

    class Params(
        val printType: PrintType,
        val orderUuid: String,
    )
}

enum class PrintType(val value: String) {
    Qr("screen"),
    Print("paper")
}