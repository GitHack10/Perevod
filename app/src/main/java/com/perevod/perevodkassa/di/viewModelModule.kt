package com.perevod.perevodkassa.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.perevod.perevodkassa.presentation.screens.auth.AuthViewModel
import com.perevod.perevodkassa.presentation.screens.home.HomeViewModel

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
val viewModelModule = module {

    viewModel {
        AuthViewModel(
            get(), get(),
            get(),
        )
    }
    viewModel {
        HomeViewModel(
            get(), get(),
            get(),
        )
    }
}