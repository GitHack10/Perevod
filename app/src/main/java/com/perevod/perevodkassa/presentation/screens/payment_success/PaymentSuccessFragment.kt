package com.perevod.perevodkassa.presentation.screens.payment_success

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.os.RemoteException
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textview.MaterialTextView
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.core.arch.BaseFragment
import com.perevod.perevodkassa.databinding.ScreenPaymentSuccessBinding
import com.perevod.perevodkassa.presentation.global.extensions.dialogBuild
import com.perevod.perevodkassa.presentation.global.extensions.getFormattedPrice
import com.perevod.perevodkassa.presentation.global.extensions.launchWhenStarted
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.createHorizontalGradient
import com.perevod.perevodkassa.utils.createRoundedRippleDrawable
import com.perevod.perevodkassa.utils.dpToPx
import com.perevod.perevodkassa.utils.hideSystemUI
import com.perevod.perevodkassa.utils.resColor
import com.perevod.perevodkassa.utils.roundAllCorners
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.atol.drivers10.service.IFptrService
import timber.log.Timber

class PaymentSuccessFragment : BaseFragment(R.layout.screen_payment_success) {

    companion object {
        const val EXTRA_AMOUNT = "arg_amount"
    }

    private var dialogSuccess: Dialog? = null
    private var dialogError: Dialog? = null
    private var dialogErrorTextView: MaterialTextView? = null
    private var fptrServiceBinder: IFptrService? = null

    private val viewBinding: ScreenPaymentSuccessBinding by viewBinding()
    private val viewModel: PaymentSuccessViewModel by viewModel()

    private val serviceIntent = Intent().apply {
        action = "ru.atol.drivers10.service.SERVICE"
        setPackage("ru.atol.drivers10.service")
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            fptrServiceBinder = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            fptrServiceBinder = IFptrService.Stub.asInterface(service)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.hideSystemUI()
    }

    override fun prepareUi() {
        initService()
        initSuccessDialog()
        initErrorDialog()
        initButtonListeners()
    }

    override fun setupViewModel() {
        viewModel.viewState.onEach { viewState ->
            when (viewState) {
                is PaymentSuccessViewState.Idle -> Unit
                is PaymentSuccessViewState.ShowLoading -> showLoading()
                is PaymentSuccessViewState.HideLoading -> hideLoading()
                is PaymentSuccessViewState.Error -> showError(viewState.message)
                is PaymentSuccessViewState.PaymentError -> showPaymentError(viewState.message)
                is PaymentSuccessViewState.PaymentSuccess -> showPaymentSuccess(viewState.message)
                is PaymentSuccessViewState.SuccessPrintOrShowQr -> printSlip(viewState.printModel.paperPrint)
                is PaymentSuccessViewState.ShowQrCode -> showQrCode(viewState.qrBitmap)
                is PaymentSuccessViewState.OnUpdatePaymentStatus -> onUpdatePaymentStatus(viewState.paymentEvent.message)
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun initService() {
        context?.bindService(serviceIntent, serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)
    }

    private fun printSlip(paperPrint: String?) {
        lifecycleScope.launchWhenStarted {
            try {
                val resultOpen = fptrServiceBinder?.processJson(paperPrint)
                Timber.tag("PRINT_RESULT").e(resultOpen)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            dialogSuccess?.show()
            delay(2000)
            dialogSuccess?.dismiss()
        }
    }

    private fun showQrCode(qrBitmap: Bitmap?) {
        val currentAmount = (arguments?.getFloat(EXTRA_AMOUNT, 0f) ?: 0f)
        with(viewBinding) {
            ivQrCode.setImageBitmap(qrBitmap)
            tvAmountTitle.text = getString(
                R.string.payment_success_screen_amount_title,
                currentAmount.getFormattedPrice()
            )
            onUpdatePaymentStatus(getString(R.string.payment_success_screen_order_status_default))
        }
    }

    private fun showLoading() = lifecycleScope.launchWhenStarted {
        viewBinding.vLoading.isVisible = true
    }

    private fun hideLoading() = lifecycleScope.launchWhenStarted {
        viewBinding.vLoading.isVisible = false
    }

    private fun showError(message: String) {
        hideLoading()
        lifecycleScope.launchWhenStarted {
            dialogErrorTextView?.text = message
            dialogError?.show()
            delay(2000)
            dialogError?.dismiss()
            activity?.window?.hideSystemUI()
        }
    }

    private fun onUpdatePaymentStatus(statusMessage: String) {
        with(viewBinding.tvOrderStatus) {
            text = buildSpannedString {
                append(getString(R.string.payment_success_screen_order_status))
                append(" ")
                inSpans(ForegroundColorSpan(resColor(R.color.golden_tainoi))) {
                    append(statusMessage)
                }
            }
        }
        Log.d("PaymentSuccess_STATUS", statusMessage)
    }

    private fun showPaymentError(message: String) {
        hideLoading()
        viewModel.userIntent.tryEmit(PaymentSuccessIntent.ShowErrorScreen(message))
    }

    private fun showPaymentSuccess(message: String) {
        hideLoading()
        viewModel.userIntent.tryEmit(PaymentSuccessIntent.ShowSuccessScreen(message))
    }

    private fun initSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.screen_success, null)
        dialogView.roundAllCorners(24)
        dialogView.findViewById<AppCompatImageView>(R.id.successImageView).background = createCircleDrawable(
            resColor(R.color.white_10)
        )
        dialogSuccess = requireContext().dialogBuild(dialogView)
    }

    private fun initErrorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.screen_error, null)
        dialogView.findViewById<AppCompatImageView>(R.id.errorImageView).background = createCircleDrawable(
            resColor(R.color.white_10)
        )
        dialogError = requireContext().dialogBuild(dialogView)
        dialogErrorTextView = dialogView.findViewById(R.id.errorTextView)
    }

    private fun initButtonListeners() {
        with(viewBinding) {
            btnGoBack.background = createRoundedRippleDrawable(
                resColor(R.color.ripple_primary),
                24.dpToPx.toFloat(),
                resColor(R.color.grey_4D4D4D)
            )
            btnGoBack.onDelayedClick {
                viewModel.userIntent.tryEmit(PaymentSuccessIntent.OnBackPressed)
            }
            btnPrintQr.background = createRoundedRippleDrawable(
                resColor(R.color.ripple_primary),
                24f,
                createHorizontalGradient(
                    resColor(R.color.gradient_start),
                    resColor(R.color.gradient_end),
                )
            )
            btnPrintQr.roundAllCorners(24)
            btnPrintQr.onDelayedClick {
                viewModel.userIntent.tryEmit(PaymentSuccessIntent.PrintReceipt)
            }
        }
    }

    private fun unbindService() {
        context?.unbindService(serviceConnection)
    }

    override fun onDestroyView() {
        dialogSuccess?.dismiss()
        dialogError?.dismiss()
        dialogSuccess = null
        dialogError = null
        dialogErrorTextView = null
        fptrServiceBinder = null
        unbindService()
        super.onDestroyView()
    }
}