package com.perevod.perevodkassa.di

import com.perevod.perevodkassa.data.repository.AuthRepository
import com.perevod.perevodkassa.data.repository.HomeRepository
import com.perevod.perevodkassa.domain.interactor.AuthInteractor
import com.perevod.perevodkassa.domain.interactor.HomeInteractor
import com.perevod.perevodkassa.domain.repositoryimpl.AuthRepositoryImpl
import com.perevod.perevodkassa.domain.repositoryimpl.HomeRepositoryImpl
import org.koin.dsl.module

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val domainModule = module {

    factory { AuthInteractor(get()) }
    factory { HomeInteractor(get()) }

    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory<HomeRepository> { HomeRepositoryImpl(get()) }
}