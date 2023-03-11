package com.perevod.perevodkassa.core.coroutines

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

suspend fun <T : Any?, R : Any?> Flow<RequestResult<T>>.mapFlow(mapper: suspend (T) -> R): Flow<RequestResult<R>> {
    return map {
        when (it) {
            is RequestResult.Error -> RequestResult.Error(it.error)
            is RequestResult.Success -> RequestResult.Success(mapper(it.data))
        }
    }
}

suspend fun <T : Any?, R : Any?> Flow<RequestResult<T>>.mapFlowResult(mapper: suspend (T) -> RequestResult<R>): Flow<RequestResult<R>> {
    return map {
        when (it) {
            is RequestResult.Error -> RequestResult.Error(it.error)
            is RequestResult.Success -> mapper(it.data)
        }
    }
}

fun <T : Any?> Flow<T>.chunkedWithThrottle(chunkSize: Int, throttleTimeMs: Long): Flow<List<T>> = channelFlow {
    coroutineScope {
        val buffer = mutableListOf<T>()
        val bufferMutex = Mutex()
        launch {
            val upstream = this@chunkedWithThrottle.produceIn(this@coroutineScope)
            while (isActive) {
                val item = upstream.receiveCatching().getOrNull() ?: break
                bufferMutex.withLock {
                    if (buffer.size + 1 == chunkSize) {
                        buffer.add(item)
                        send(buffer.toList())
                        buffer.clear()
                    } else {
                        buffer.add(item)
                    }
                }
            }
        }
        launch {
            while (isActive) {
                delay(throttleTimeMs)
                bufferMutex.withLock {
                    if (buffer.isNotEmpty()) {
                        send(buffer.toList())
                        buffer.clear()
                    }
                }
            }
        }
    }
}

fun <T : Any?> Flow<T>.chunkedBySizeAndTime(maxSize: Int, throttleTimeMs: Long): Flow<List<T>> = callbackFlow {
    val buffer = mutableListOf<T>()
    onEach { item ->
        if (buffer.size + 1 == maxSize) {
            buffer.add(item)
            send(buffer.toList())
            buffer.clear()
        } else {
            buffer.add(item)
        }
    }.launchIn(this)
    val throttleJob = launch {
        while (isActive) {
            delay(throttleTimeMs)
            if (buffer.isNotEmpty()) {
                send(buffer.toList())
                buffer.clear()
            }
        }
    }
    awaitClose {
        buffer.clear()
        throttleJob.cancel()
    }
}

suspend fun <T : Any> Flow<RequestResult<T>>.wrapError(): Flow<RequestResult<T>> = catch {
    emit(RequestResult.Error(it))
}