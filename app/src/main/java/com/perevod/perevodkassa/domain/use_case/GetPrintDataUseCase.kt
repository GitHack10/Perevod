package com.perevod.perevodkassa.domain.use_case

import com.perevod.perevodkassa.data.repository.MainRepository

class GetPrintDataUseCase(
    private val repository: MainRepository
) {

    suspend operator fun invoke(printType: PrintType) = repository.printReceipt(printType)
}

enum class PrintType(val value: String) {
    Qr("screen"),
    Print("paper")
}