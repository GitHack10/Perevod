package com.perevod.perevodkassa.presentation.global.extensions

import androidx.lifecycle.LifecycleCoroutineScope
import com.perevod.perevodkassa.data.global.ErrorModel
import com.perevod.perevodkassa.data.network.Request
import com.perevod.perevodkassa.data.network.handleErrors
import com.perevod.perevodkassa.data.network.handlerErrors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import retrofit2.Response

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
suspend fun <T : Any> makeRequest(call: suspend () -> Response<T>): Request<T> {

    val response: Response<T>

    try {
        response = call.invoke()
    } catch (t: Throwable) {
        return Request.Error(t.handlerErrors())
    }

    return if (!response.isSuccessful) {
        Request.Error(response.handleErrors())
    } else {
        response.body()?.run {
            Request.Success(this)
        } ?: Request.Error(exception = ErrorModel.ServerError)
    }
}

fun <T> Flow<T>.launchWhenCreated(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenCreated {
        this@launchWhenCreated.collect()
    }
}

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}

fun <T> Flow<T>.launchWhenResumed(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenResumed {
        this@launchWhenResumed.collect()
    }
}