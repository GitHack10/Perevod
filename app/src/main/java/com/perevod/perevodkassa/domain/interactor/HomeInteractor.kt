package com.perevod.perevodkassa.domain.interactor

import com.perevod.perevodkassa.data.repository.HomeRepository

class HomeInteractor(private val repository: HomeRepository) {

    suspend fun printReceipt() = repository.printReceipt()
    suspend fun staffClose() = repository.staffClose()
}