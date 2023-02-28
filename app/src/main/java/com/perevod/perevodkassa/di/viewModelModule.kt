package com.perevod.perevodkassa.di

import com.perevod.perevodkassa.presentation.screens.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val viewModelModule = module {

    viewModel {
        HomeViewModel(
            get(), get(),
            get(),
        )
    }
}