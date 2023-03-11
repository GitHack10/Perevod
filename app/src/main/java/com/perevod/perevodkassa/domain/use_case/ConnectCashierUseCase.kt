package com.perevod.perevodkassa.domain.use_case

import android.content.Context
import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.domain.connect_cashier.ConnectCashierRequest
import com.perevod.perevodkassa.presentation.global.extensions.getDeviceId
import com.perevod.perevodkassa.presentation.screens.home.HomeViewState
import timber.log.Timber

class ConnectCashierUseCase(
    private val repository: MainRepository,
    private val context: Context
) {

    suspend operator fun invoke(): HomeViewState<Any> = repository.connectCashier(
        ConnectCashierRequest(
            getDeviceId(context)
        )
    ).also {
        if (BuildConfig.DEBUG) {
            Timber.tag("device_id").d(getDeviceId(context))
        }
    }
}