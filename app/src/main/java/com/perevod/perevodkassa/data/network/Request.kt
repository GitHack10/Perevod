package com.perevod.perevodkassa.data.network

import com.perevod.perevodkassa.data.global.ErrorModel

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
sealed class Request<out RESULT : Any> {
    data class Success<out RESULT : Any>(val data: RESULT) : Request<RESULT>()
    data class Error(val exception: ErrorModel) : Request<Nothing>()
}