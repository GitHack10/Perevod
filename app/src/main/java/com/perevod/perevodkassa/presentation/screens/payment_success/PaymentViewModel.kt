package com.perevod.perevodkassa.presentation.screens.payment_success

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
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
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import timber.log.Timber
import java.io.IOException

private const val TAG = "PaymentEvents"

class PaymentViewModel(
    private val getPrintDataUseCase: GetPrintDataUseCase,
    private val subscribeToPaymentEventsUseCase: SubscribeToPaymentEventsUseCase,
    private val prefs: PreferenceStorage,
    private val router: AppRouter,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow<PaymentViewState<Any>>(PaymentViewState.Idle)
    val viewState: StateFlow<PaymentViewState<Any>> = _viewState

    val userIntent = MutableSharedFlow<PaymentIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
        subscribeToPaymentEventsHard()
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
        _viewState.value = PaymentViewState.ShowLoading
        viewModelScope.launch {
            when (val result = getPrintDataUseCase(
                GetPrintDataUseCase.Params(
                    PrintType.Qr,
                    prefs.lastOrderUuid ?: ""
                )
            )) {
                is PaymentViewState.SuccessPrintOrShowQr -> {
                    val qrBitmap = createBarCode(result.printModel.screenPrint)
                    _viewState.value = PaymentViewState.ShowQrCode(qrBitmap)
                }
                else -> {
                    _viewState.value = result
                }
            }
        }.invokeOnCompletion {
            _viewState.value = PaymentViewState.HideLoading
        }
    }

    private fun createBarCode(screenPrint: String?): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(screenPrint, BarcodeFormat.QR_CODE, 340.dpToPx, 340.dpToPx)
        return createQrBitmap(bitMatrix)
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
                _viewState.value = PaymentViewState.PaymentError(
                    parseError()
                )
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
                    val paymentEvent = gson.fromJson(
                        data, PaymentStatusResponseObj::class.java
                    ).mapToPaymentStatusEvent()
                    when (paymentEvent.status) {
                        PaymentStatus.CryptoInit -> {
                            _viewState.value = PaymentViewState.OnUpdatePaymentStatus(paymentEvent)
                        }
                        PaymentStatus.Validated -> {
                            _viewState.value = PaymentViewState.PaymentSuccess(
                                paymentEvent.message,
                                paymentEvent.paperPrint
                            )
                        }
                        else -> Unit
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).d("On Event Received! Data -: ${e.message}")
                    _viewState.value = PaymentViewState.PaymentError(
                        parseError()
                    )
                }
            }
            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Timber.tag(TAG).d("On Failure -: ${response?.message}")
                _viewState.value = PaymentViewState.PaymentError(
                    parseError(response?.message)
                )
            }
        }
        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.tag(TAG).d("API Call Failure: %s", e.localizedMessage)
                _viewState.value = PaymentViewState.PaymentError(
                    parseError(e.localizedMessage)
                )
            }
            override fun onResponse(call: Call, response: Response) {
                Timber.tag(TAG).d( "APi Call Success: ${response.message}")
            }
        }
        viewModelScope.launch {
            delay(5000)
            subscribeToPaymentEventsUseCase(
                eventSourceListener,
                callback
            )
        }
    }

    private fun showErrorScreen(message: String) {
        router.replaceScreen(Screens.errorMessageScreen(message))
    }

    private fun showSuccessScreen(message: String, paperPrint: String?) {
        router.replaceScreen(Screens.paymentSuccessScreen(message, paperPrint))
    }

    private fun parseError(message: String? = null): String =
        message?.ifBlank { "Ошибка.\n" + "Повторите снова." } ?: ("Ошибка.\n" + "Повторите снова.")
}