package com.perevod.perevodkassa.presentation.screens.payment_success

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.perevod.perevodkassa.BuildConfig
import com.perevod.perevodkassa.core.arch.BaseViewModel
import com.perevod.perevodkassa.core.navigation.AppRouter
import com.perevod.perevodkassa.core.navigation.Screens
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.data.network.sse.PaymentStatus
import com.perevod.perevodkassa.data.network.sse.PaymentStatusResponseObj
import com.perevod.perevodkassa.domain.mapToPaymentStatusEvent
import com.perevod.perevodkassa.domain.use_case.GetPrintDataUseCase
import com.perevod.perevodkassa.domain.use_case.PrintType
import com.perevod.perevodkassa.domain.use_case.SubscribeToPaymentEventsUseCase
import com.perevod.perevodkassa.utils.createQrBitmap
import com.perevod.perevodkassa.utils.dpToPx
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

private const val TAG = "PaymentEvents"
private const val SSE_URL = "http://217.107.34.221:8777/events"

class PaymentViewModel(
    private val getPrintDataUseCase: GetPrintDataUseCase,
    private val subscribeToPaymentEventsUseCase: SubscribeToPaymentEventsUseCase,
    private val prefs: PreferenceStorage,
    private val router: AppRouter,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow<PaymentSuccessViewState<Any>>(PaymentSuccessViewState.Idle)
    val viewState: StateFlow<PaymentSuccessViewState<Any>> = _viewState

    val userIntent = MutableSharedFlow<PaymentIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val paymentStatus = AtomicReference(PaymentStatus.Idle)

    init {
        viewModelScope.launch {
            delay(7000)
            subscribeToPaymentEventsHard()
        }
//        subscribeToPaymentEvents() todo return back after fix
    }

    override fun onAttach() {
        handleIntents()
        userIntent.tryEmit(PaymentIntent.ShowQrCode)
    }

    override fun onBackPressed() {
        router.replaceScreen(Screens.homeScreen())
    }

    private fun handleIntents() {

        userIntent.onEach { intent ->
            when (intent) {
                is PaymentIntent.ShowQrCode -> showQrCode()
                is PaymentIntent.OnBackPressed -> onBackPressed()
                is PaymentIntent.ShowErrorScreen -> showErrorScreen(intent.message)
                is PaymentIntent.ShowSuccessScreen -> showSuccessScreen(intent.message, intent.paperPrint)
            }
        }.launchIn(viewModelScope)
    }

    private fun showQrCode() {
        _viewState.value = PaymentSuccessViewState.ShowLoading
        viewModelScope.launch {
            when (val result = getPrintDataUseCase(
                GetPrintDataUseCase.Params(
                    PrintType.Qr,
                    prefs.lastOrderUuid ?: ""
                )
            )) {
                is PaymentSuccessViewState.SuccessPrintOrShowQr -> {
                    val qrBitmap = createBarCode(result.printModel.screenPrint)
                    _viewState.value = PaymentSuccessViewState.ShowQrCode(qrBitmap)
                }
                else -> {
                    _viewState.value = result
                }
            }
        }.invokeOnCompletion {
            _viewState.value = PaymentSuccessViewState.HideLoading
        }
    }

    private fun createBarCode(screenPrint: String?): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(screenPrint, BarcodeFormat.QR_CODE, 340.dpToPx, 340.dpToPx)
        return createQrBitmap(bitMatrix)
    }

    private fun subscribeToPaymentEvents() {
        viewModelScope.launch {
            delay(7000)
            subscribeToPaymentEventsUseCase()
                .collect {
                    when (it) {
                        is PaymentSuccessViewState.OnUpdatePaymentStatus -> onUpdatePaymentStatus(it)
                        else -> _viewState.value = it
                    }
                }
        }
    }

    private fun subscribeToPaymentEventsHard() {
        val eventSourceListener = object : EventSourceListener() {

            override fun onOpen(eventSource: EventSource, response: Response) {
                super.onOpen(eventSource, response)
                Timber.tag(TAG).d("Connection Opened: ${response.message}")
            }

            override fun onClosed(eventSource: EventSource) {
                super.onClosed(eventSource)
                Timber.tag(TAG).d("Connection Closed")
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)
                Timber.tag(TAG).d("On Event Received! Data -: $data")
                try {
                    val paymentResponse = gson.fromJson(
                        data, PaymentStatusResponseObj::class.java
                    )
                    val paymentEvent = paymentResponse.mapToPaymentStatusEvent()
                    when (paymentEvent.status) {
                        PaymentStatus.CryptoInit -> {
                            _viewState.value = PaymentSuccessViewState.OnUpdatePaymentStatus(paymentEvent)
                        }
                        PaymentStatus.Validated -> {
                            _viewState.value = PaymentSuccessViewState.PaymentSuccess(
                                paymentEvent.message,
                                paymentEvent.paperPrint
                            )
                        }
                        else -> Unit
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).d("On Event Received! Data -: ${e.message}")
                    _viewState.value = PaymentSuccessViewState.PaymentError(
                        "Ошибка.\n" + "Повторите снова."
                    )
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Timber.tag(TAG).d("On Failure -: ${response?.message}")
                _viewState.value = PaymentSuccessViewState.PaymentError(
                    response?.message ?: ("Ошибка.\n" + "Повторите заново.")
                )
            }
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .readTimeout(15, TimeUnit.MINUTES)
            .writeTimeout(15, TimeUnit.MINUTES)
            .build()

        val httpUrl = SSE_URL.toHttpUrl().newBuilder()
            .addQueryParameter("stream", prefs.lastOrderUuid)
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .addHeader("Accept", "text/event-stream")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Connection", "keep-alive")
            .build()

        EventSources.createFactory(client)
            .newEventSource(request = request, listener = eventSourceListener)

        client.newCall(request).enqueue(responseCallback = object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Timber.tag(TAG).d("API Call Failure: %s", e.localizedMessage)
                _viewState.value = PaymentSuccessViewState.PaymentError(
                    e.localizedMessage ?: ("Ошибка.\n" + "Повторите заново.")
                )
            }

            override fun onResponse(call: Call, response: Response) {
                Timber.tag(TAG).d( "APi Call Success: ${response.message}")
            }
        })
    }

    private fun onUpdatePaymentStatus(viewState: PaymentSuccessViewState.OnUpdatePaymentStatus) {
        val paymentEvent = viewState.paymentEvent
        val status = paymentStatus.getAndSet(paymentEvent.status)
        if (status == paymentEvent.status) {
            return
        }
        when (status) {
            PaymentStatus.Validated -> {
                _viewState.value = PaymentSuccessViewState.PaymentSuccess(
                    paymentEvent.message,
                    paymentEvent.paperPrint
                )
            }
            else -> {
                _viewState.value = viewState
            }
        }
    }

    private fun showErrorScreen(message: String) {
        router.replaceScreen(Screens.errorMessageScreen(message))
    }

    private fun showSuccessScreen(message: String, paperPrint: String?) {
        router.replaceScreen(Screens.paymentSuccessScreen(message, paperPrint))
    }
}