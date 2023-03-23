package com.perevod.perevodkassa.di

import com.perevod.perevodkassa.domain.use_case.SubscribeToPaymentEventsUseCase
import com.perevod.perevodkassa.data.repository.MainRepository
import com.perevod.perevodkassa.domain.repositoryimpl.HomeRepositoryImpl
import com.perevod.perevodkassa.domain.use_case.ConnectCashierUseCase
import com.perevod.perevodkassa.domain.use_case.GetPrintDataUseCase
import com.perevod.perevodkassa.domain.use_case.InitCashierUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val domainModule = module {

    factory {
        GetPrintDataUseCase(get())
    }

    factory {
        ConnectCashierUseCase(
            get(), androidContext()
        )
    }

    factory {
        InitCashierUseCase(get())
    }

    factory<MainRepository> {
        HomeRepositoryImpl(
            get(),
        )
    }

    factory { SubscribeToPaymentEventsUseCase(get()) }
}