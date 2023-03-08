package com.perevod.perevodkassa.domain.use_case

import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.Payload

class InitCashierUseCase(
    private val repository: MainRepository
) {

    suspend operator fun invoke(input: Params) = repository.initCashier(
        InitCashierRequest(
            input.amount.toString(),
            input.currency,
            input.payload
        )
    )

    class Params(
        val amount: Number,
        val currency: String = "rub", // todo заменить на реальные данные,
        val payload: Payload = Payload("") // todo заменить на реальные данные,
    )
}