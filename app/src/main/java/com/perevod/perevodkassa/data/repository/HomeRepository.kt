package com.perevod.perevodkassa.data.repository

import com.perevod.perevodkassa.presentation.screens.home.HomeViewState

interface HomeRepository {

    suspend fun printReceipt(): HomeViewState<Any>
    suspend fun staffClose(): HomeViewState<Any>
}