package com.perevod.perevodkassa.presentation.screens.home

import android.app.Dialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.perevod.perevodkassa.utils.createCircleRippleDrawable
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
        with(viewBinding) {
            initService()
            initSuccessDialog()
            initErrorDialog()
            hideKeyboard()
            homeToolbar.ivStaffClose.background = createCircleRippleDrawable(
                resColor(R.color.ripple_primary)
            )
            homeToolbar.ivStaffClose.onDelayedClick {
                showCloseStaffDialog()
            }
        }
    }

    override fun setupViewModel() {
        viewModel.viewState.onEach { viewState ->
            when (viewState) {
                is HomeViewState.Idle -> Unit
                is HomeViewState.ShowLoading -> showLoading()
                is HomeViewState.HideLoading -> hideLoading()
                is HomeViewState.ShowContent -> showContent()
                is HomeViewState.HideContent -> hideContent()
                is HomeViewState.UpdateTotalValues -> updateTotalValues(
                    viewState.count,
                    viewState.price
                )
                is HomeViewState.Error -> showError(viewState.message)
                is HomeViewState.SuccessPrintReceipt -> printSlip(viewState.printModel)
                is HomeViewState.SuccessStaffClose -> printSlip(viewState.printModel, false)
                is HomeViewState.ShowEmptyContent -> showEmptyContent()
                is HomeViewState.HideEmptyContent -> hideEmptyContent()
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

    private fun showCloseStaffDialog() {
        val dialog = AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setMessage(R.string.home_screen_close_shift_dialog_title)
            .setNegativeButton(
                R.string.home_screen_close_shift_dialog_negative_btn
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(
                R.string.home_screen_close_shift_dialog_positive_btn
            ) { _, _ ->
                viewModel.userIntent.tryEmit(HomeIntent.StaffClose)
            }
            .create()
        dialog.show()
        dialog
            .getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(resColor(R.color.colorPrimaryDark))
        dialog
            .getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resColor(R.color.colorPrimaryDark))
    }

    private fun showLoading() = lifecycleScope.launchWhenStarted {
        viewBinding.vLoading.isVisible = true
    }

    private fun hideLoading() = lifecycleScope.launchWhenStarted {
        viewBinding.vLoading.isVisible = false
    }

    private fun showError(message: String) {
        lifecycleScope.launchWhenStarted {
            dialogErrorTextView?.text = message
            dialogError?.show()
            delay(2000)
            dialogError?.dismiss()
        }
    }

    private fun showContent() {

    }

    private fun hideContent() {

    }

    private fun showEmptyContent() {

    }

    private fun hideEmptyContent() {

    }

    private fun updateTotalValues(count: Int, price: Double) {
        with(viewBinding) {

        }
    }

    private fun printSlip(printModel: PrintModel, withAlert: Boolean = true) {
        lifecycleScope.launchWhenStarted {
            try {
                val resultOpen = fptrServiceBinder?.processJson(printModel.print)
                Timber.tag("PRINT_RESULT").e(resultOpen)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            if (withAlert) {
                dialogSuccess?.show()
                delay(2000)
                dialogSuccess?.dismiss()
            } else {
                viewModel.userIntent.tryEmit(
                    HomeIntent.GoToEnterScreen
                )
            }
        }
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