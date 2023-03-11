package com.perevod.perevodkassa.data.network

interface AppBaseRunnable<I : Any?, R : Any?> {

    suspend fun run(input: I): R
}