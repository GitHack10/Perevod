package com.perevod.perevodkassa.data.network

import com.perevod.perevodkassa.data.global.ErrorModel
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
fun Throwable.handlerErrors(): ErrorModel =
    when (this) {
        is HttpException -> {
            getHttpError()
        }
        is UnknownHostException -> {
            ErrorModel.NoConnection
        }
        else -> {
            ErrorModel.ServerError
        }
    }

fun <T> Response<T>.handleErrors(): ErrorModel = errorBody()?.getErrorModel(code())
    ?: ErrorModel.Error(message(), code())

fun HttpException.getHttpError(): ErrorModel = response()?.errorBody()?.getErrorModel(code())
    ?: ErrorModel.Error(message(), code())

fun ResponseBody.getErrorModel(errorCode: Int): ErrorModel {

    val jsnObj = try {
        JSONObject(this.string())
    } catch (e: JSONException) {
        return ErrorModel.ServerError
    }

    return if (jsnObj.has(ErrorModel.Error::code.name)) {
        var message: String? = null
        var code: String? = null
        jsnObj.keys().forEach {
            if (it == ErrorModel.Error::message.name) {
                message = jsnObj.getString(it)
            }
            if (it == ErrorModel.Error::code.name) {
                code = jsnObj.getString(it)
            }
        }
        ErrorModel.Error(message, code?.toInt())
    } else {

        val specificError = StringBuilder("").apply {
            jsnObj.keys().forEach {

                if (it == "error") {
                    JSONObject(jsnObj.getString(it)).run {
                        keys().forEach { k ->
                            append(getString(k) + " ")
                        }
                    }
                } else {
                    append(jsnObj.getString(it) + " ")
                }
            }
        }.toString()


        when (errorCode) {
            401 -> ErrorModel.Unauthorized
            400 -> ErrorModel.SpecificError(specificError)
            502 -> ErrorModel.TimeOut
            else -> {
                if (specificError.isNotEmpty())
                    ErrorModel.SpecificError(specificError)
                else
                    ErrorModel.ServerError
            }
        }
    }
}