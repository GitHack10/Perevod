package com.perevod.perevodkassa.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import org.koin.dsl.module

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val navigationModule = module {

    val appRouter: Cicerone<Router> = Cicerone.create(Router())

    single { appRouter.router }
    single { appRouter.getNavigatorHolder() }
}