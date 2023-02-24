package com.perevod.perevodkassa.domain.repositoryimpl

import com.perevod.perevodkassa.data.ApiService
import com.perevod.perevodkassa.data.network.Request
import com.perevod.perevodkassa.data.repository.HomeRepository
import com.perevod.perevodkassa.presentation.global.extensions.makeRequest
import com.perevod.perevodkassa.presentation.screens.home.HomeViewState

class HomeRepositoryImpl(
    private val api: ApiService,
) : HomeRepository {

    override suspend fun printReceipt(): HomeViewState<Any> =
        when (val result = makeRequest { api.printReceipt() }) {
            is Request.Success -> HomeViewState.SuccessPrintReceipt(result.data)
            is Request.Error -> HomeViewState.Error(message = result.exception.message)
        }

    override suspend fun staffClose(): HomeViewState<Any> =
        when (val result = makeRequest { api.staffClose() }) {
            is Request.Success -> HomeViewState.SuccessStaffClose(result.data)
            is Request.Error -> HomeViewState.Error(message = result.exception.message)
        }
}