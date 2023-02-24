package com.perevod.perevodkassa.data.global

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
data class BaseResponse(
    @SerializedName("code") val code: Int?
)