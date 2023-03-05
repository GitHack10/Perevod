package com.perevod.perevodkassa.domain.interactor

import android.content.Context
import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.InitCashierRequest
import com.perevod.perevodkassa.domain.init_cashier.Payload
import com.perevod.perevodkassa.presentation.global.extensions.getDeviceId
import timber.log.Timber

class HomeInteractor(
    private val repository: MainRepository,
    private val context: Context
) {

    suspend fun connectCashier() = repository.connectCashier(
        ConnectCashierRequest(
            getDeviceId(context)
        )
    ).also {
        if (BuildConfig.DEBUG) {
            Timber.tag("device_id").d(getDeviceId(context))
        }
    }

    suspend fun initCashier(
        amount: Float
    ) = repository.initCashier(
        InitCashierRequest(
            amount = amount,
            currency = "rub", // todo заменить на получение системной
            payload = Payload("") // todo заменить на реальные данные
        )
    )
}