package com.perevod.perevodkassa.presentation.screens.payment_success

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textview.MaterialTextView
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenPaymentSuccessBinding
import com.perevod.perevodkassa.presentation.global.BaseFragment
import com.perevod.perevodkassa.presentation.global.extensions.launchWhenStarted
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.utils.createCircleRippleDrawable
import com.perevod.perevodkassa.utils.resColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.atol.drivers10.service.IFptrService
import timber.log.Timber

class PaymentSuccessFragment : BaseFragment(R.layout.screen_payment_success) {

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
                is PaymentSuccessViewState.SuccessPrintReceipt -> printSlip(viewState.printModel.paperPrint)
                is PaymentSuccessViewState.ShowQrCode -> showQrCode(viewState.qrBitmap)
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
        viewBinding.ivQrCode.setImageBitmap(qrBitmap)
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
        }
    }

    private fun initSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.screen_success, null)
        dialogSuccess = Dialog(requireContext(), R.style.RoundedDialogStyle)
            .apply {
                setCancelable(false)
                setContentView(dialogView)
                create()
            }
    }

    private fun initErrorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.screen_error, null)
        dialogError = Dialog(requireContext(), R.style.RoundedDialogStyle)
            .apply {
                setCancelable(false)
                setContentView(dialogView)
                create()
            }

        dialogErrorTextView = dialogView.findViewById(R.id.errorTextView)
    }

    private fun initButtonListeners() {
        with(viewBinding) {
            btnPrintQr.onDelayedClick {
                viewModel.userIntent.tryEmit(PaymentSuccessIntent.PrintReceipt)
            }
            btnShowQr.onDelayedClick {
                viewModel.userIntent.tryEmit(PaymentSuccessIntent.ShowQrCode)
            }
            paymentSuccessToolbar.ivBackButton.setOnClickListener {
                viewModel.userIntent.tryEmit(PaymentSuccessIntent.OnBackPressed)
            }
            paymentSuccessToolbar.ivBackButton.background = createCircleRippleDrawable(
                resColor(R.color.ripple_primary)
            )
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