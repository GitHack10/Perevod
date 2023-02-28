package com.perevod.perevodkassa.di

import com.perevod.perevodkassa.data.repository.HomeRepository
import com.perevod.perevodkassa.domain.interactor.HomeInteractor
import com.perevod.perevodkassa.domain.repositoryimpl.HomeRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val domainModule = module {

    factory {
        HomeInteractor(
            get(), androidContext()
        )
    }

    factory<HomeRepository> { HomeRepositoryImpl(get()) }
}