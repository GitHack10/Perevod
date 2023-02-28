package com.perevod.perevodkassa.presentation.screens.home

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textview.MaterialTextView
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenHomeBinding
import com.perevod.perevodkassa.domain.PrintModel
import com.perevod.perevodkassa.presentation.global.BaseFragment
import com.perevod.perevodkassa.presentation.global.extensions.launchWhenStarted
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.utils.DebouncingQueryTextListener
import com.perevod.perevodkassa.utils.resColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.atol.drivers10.service.IFptrService
import timber.log.Timber

class HomeFragment : BaseFragment(R.layout.screen_home) {

    private var queryTextChangeListener: DebouncingQueryTextListener? = null
    private var dialogSuccess: Dialog? = null
    private var dialogError: Dialog? = null
    private var dialogErrorTextView: MaterialTextView? = null

    private val viewBinding: ScreenHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModel()

    private var fptrServiceBinder: IFptrService? = null

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
        initInputAmount()
        viewBinding.etAmount.requestFocus()
        showKeyboard()
        viewBinding.btnDone.onDelayedClick {
            viewModel.userIntent.tryEmit(HomeIntent.OnButtonDoneClick)
        }
    }

    override fun setupViewModel() {
        viewModel.viewState.onEach { viewState ->
            when (viewState) {
                is HomeViewState.Idle -> Unit
                is HomeViewState.ShowLoading -> showLoading()
                is HomeViewState.HideLoading -> hideLoading()
                is HomeViewState.ClearState -> clearState()
                is HomeViewState.Error -> showError(viewState.message)
                is HomeViewState.SuccessPrintReceipt -> printSlip(viewState.printModel)
                is HomeViewState.FetchInputAmount -> fetchInputAmount(viewState.amount, viewState.btnEnabled)
                else -> Unit
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun initService() {
        val intent = Intent().apply {
            action = "ru.atol.drivers10.service.SERVICE"
            setPackage("ru.atol.drivers10.service")
        }
        context?.bindService(intent, serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)
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

    private fun initInputAmount() {
        with(viewBinding) {
            etAmount.doAfterTextChanged {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(it?.trim()?.toString()))
            }
            tvAmount.onDelayedClick {
                etAmount.requestFocus()
                showKeyboard()
            }
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
        }
    }

    private fun printSlip(printModel: PrintModel, withAlert: Boolean = true) {
        lifecycleScope.launchWhenStarted {
            try {
                val resultOpen = fptrServiceBinder?.processJson(printModel.paperPrint)
                Timber.tag("PRINT_RESULT").e(resultOpen)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            if (withAlert) {
                dialogSuccess?.show()
                delay(2000)
                dialogSuccess?.dismiss()
            }
        }
    }

    private fun fetchInputAmount(amount: String, btnEnabled: Boolean) {
        viewBinding.tvAmount.text = getString(
            R.string.item_price,
            amount
        )
        updateButtonEnabledState(btnEnabled)
    }

    private fun updateButtonEnabledState(enabled: Boolean) {
        with(viewBinding.btnDone) {
            isEnabled = enabled
            isClickable = enabled
            backgroundTintList = ColorStateList.valueOf(
                resColor(
                    if (enabled) {
                        R.color.electric_violet
                    } else {
                        R.color.grey_BAB9B9
                    }
                )
            )
        }
    }

    private fun clearState() {
        viewBinding.etAmount.setText("")
    }

    override fun onDestroyView() {
        queryTextChangeListener?.onDestroy()
        queryTextChangeListener = null
        dialogSuccess?.dismiss()
        dialogError?.dismiss()
        dialogSuccess = null
        dialogError = null
        dialogErrorTextView = null
        super.onDestroyView()
    }
}