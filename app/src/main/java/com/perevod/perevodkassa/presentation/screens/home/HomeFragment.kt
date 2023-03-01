package com.perevod.perevodkassa.presentation.screens.home

import android.app.Dialog
import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textview.MaterialTextView
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenHomeBinding
import com.perevod.perevodkassa.presentation.global.BaseFragment
import com.perevod.perevodkassa.presentation.global.extensions.launchWhenStarted
import com.perevod.perevodkassa.presentation.global.extensions.makeDisabled
import com.perevod.perevodkassa.presentation.global.extensions.makeEnabled
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.utils.DebouncingQueryTextListener
import com.perevod.perevodkassa.utils.resColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(R.layout.screen_home) {

    private var queryTextChangeListener: DebouncingQueryTextListener? = null
    private var dialogSuccess: Dialog? = null
    private var dialogError: Dialog? = null
    private var dialogErrorTextView: MaterialTextView? = null

    private val viewBinding: ScreenHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModel()

    override fun prepareUi() {
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
                is HomeViewState.EnableInput -> enableInput()
                is HomeViewState.DisableInput -> disableInput()
                is HomeViewState.Error -> showError(viewState.message)
                is HomeViewState.FetchInputAmount -> fetchInputAmount(viewState.amount, viewState.btnEnabled)
                else -> Unit
            }
        }.launchWhenStarted(lifecycleScope)
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

    private fun enableInput() {
        with(viewBinding.etAmount) {
            makeEnabled()
            requestFocus()
        }
    }

    private fun disableInput() {
        with(viewBinding.etAmount) {
            makeDisabled()
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