package com.perevod.perevodkassa.di

import com.github.terrakok.cicerone.Cicerone
import com.perevod.perevodkassa.core.navigation.AppRouter
import com.perevod.perevodkassa.core.navigation.CiceroneHolder
import org.koin.dsl.module

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val navigationModule = module {

    single { CiceroneHolder() }

    single { Cicerone.create(AppRouter()) }

    single {
        val cicerone: Cicerone<AppRouter> = get()
        cicerone.router
    }
    single {
        val cicerone: Cicerone<AppRouter> = get()
        cicerone.getNavigatorHolder()
    }
}