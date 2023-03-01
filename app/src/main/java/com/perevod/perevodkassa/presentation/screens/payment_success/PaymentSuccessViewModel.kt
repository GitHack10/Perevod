package com.perevod.perevodkassa.presentation.screens.payment_success

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.domain.use_case.GetPrintDataUseCase
import com.perevod.perevodkassa.domain.use_case.PrintType
import com.perevod.perevodkassa.presentation.global.BaseViewModel
import com.perevod.perevodkassa.utils.createQrBitmap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PaymentSuccessViewModel(
    private val getPrintDataUseCase: GetPrintDataUseCase,
    private val prefs: PreferenceStorage,
    private val router: Router,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow<PaymentSuccessViewState<Any>>(PaymentSuccessViewState.Idle)
    val viewState: StateFlow<PaymentSuccessViewState<Any>> = _viewState

    val userIntent = MutableSharedFlow<PaymentSuccessIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onAttach() {
        handleIntents()
    }

    private fun handleIntents() {

        userIntent.onEach { intent ->
            when (intent) {
                is PaymentSuccessIntent.PrintReceipt -> printReceipt()
                is PaymentSuccessIntent.ShowQrCode -> showQrCode()
                is PaymentSuccessIntent.OnBackPressed -> onBackPressed()
            }
        }.launchIn(viewModelScope)
    }

    private fun printReceipt() {
        _viewState.value = PaymentSuccessViewState.ShowLoading
        viewModelScope.launch {
            when (val result = getPrintDataUseCase(PrintType.Print)) {
                is PaymentSuccessViewState.SuccessPrintReceipt -> {
                    _viewState.value = PaymentSuccessViewState.SuccessPrintReceipt(result.printModel)
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = PaymentSuccessViewState.HideLoading
        }
    }

    private fun showQrCode() {
        _viewState.value = PaymentSuccessViewState.ShowLoading
        viewModelScope.launch {
            when (val result = getPrintDataUseCase(PrintType.Qr)) {
                is PaymentSuccessViewState.SuccessPrintReceipt -> {
                    val qrBitmap = createBarCode(result.printModel.screenPrint)
                    _viewState.value = PaymentSuccessViewState.ShowQrCode(qrBitmap)
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = PaymentSuccessViewState.HideLoading
        }
    }

    private fun createBarCode(screenPrint: String?): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(screenPrint, BarcodeFormat.QR_CODE, 400, 600)
        return createQrBitmap(bitMatrix)
    }

    override fun onBackPressed() {
        router.exit()
    }
}