package com.perevod.perevodkassa.presentation.screens.home

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textview.MaterialTextView
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.core.arch.BaseFragment
import com.perevod.perevodkassa.databinding.ScreenHomeBinding
import com.perevod.perevodkassa.presentation.global.extensions.dialogBuild
import com.perevod.perevodkassa.presentation.global.extensions.launchWhenStarted
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.presentation.global.extensions.text
import com.perevod.perevodkassa.utils.DebouncingQueryTextListener
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.hideSystemUI
import com.perevod.perevodkassa.utils.resColor
import com.perevod.perevodkassa.utils.roundAllCorners
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(R.layout.screen_home) {

    companion object {
        private const val TRANSITION_ANIM_DURATION = 200
    }

    private var queryTextChangeListener: DebouncingQueryTextListener? = null
    private var dialogError: Dialog? = null
    private var dialogErrorTextView: MaterialTextView? = null

    private val viewBinding: ScreenHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModel()

    override fun prepareUi() {
        viewBinding.init()
        initErrorDialog()
        initButtonNext()
        initKeyboardButtons()
        hideKeyboard()
    }

    override fun setupViewModel() {
        viewModel.viewState.onEach { viewState ->
            when (viewState) {
                is HomeViewState.Idle -> Unit
                is HomeViewState.ShowLoading -> showLoading()
                is HomeViewState.HideLoading -> hideLoading()
                is HomeViewState.Error -> showError(viewState.message)
                is HomeViewState.FetchInputAmount -> fetchInputAmount(viewState.amount, viewState.btnEnabled)
                else -> Unit
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun initErrorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.screen_success, null)
        dialogView.roundAllCorners(24)
        dialogView.findViewById<AppCompatImageView>(R.id.successImageView).background = createCircleDrawable(
            resColor(R.color.white_10)
        )
        dialogError = requireContext().dialogBuild(dialogView)
        dialogErrorTextView = dialogView.findViewById(R.id.successTextView)
    }

    private fun initButtonNext() {
        with(viewBinding.btnNext) {
            viewModel.inputStateMutableLiveData.observe(this@HomeFragment) {
                tryStartOrReverseNextBtnTransitionAnim(it)
                updateInputFieldColors(it)
            }
            onDelayedClick {
                viewModel.userIntent.tryEmit(HomeIntent.OnButtonDoneClick)
            }
            tryStartOrReverseNextBtnTransitionAnim(TransitionAnimState.Idle)
        }
    }

    private fun initKeyboardButtons() {
        with(viewBinding) {
            tvNum1.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.One))
            }
            tvNum2.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Two))
            }
            tvNum3.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Three))
            }
            tvNum4.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Four))
            }
            tvNum5.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Five))
            }
            tvNum6.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Six))
            }
            tvNum7.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Seven))
            }
            tvNum8.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Eight))
            }
            tvNum9.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Nine))
            }
            tvNum0.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Zero))
            }
            tvNumDot.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Dot))
            }
            ivDelete.setOnClickListener {
                viewModel.userIntent.tryEmit(HomeIntent.OnAmountChanged(etAmount.text(), KeyboardNumber.Delete))
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
            activity?.window?.hideSystemUI()
        }
    }

    private fun fetchInputAmount(amount: String, btnEnabled: Boolean) {
        val withCurrency = (amount.toFloatOrNull() ?: 0f) > 0f
        val amountString = if (withCurrency) {
            getString(
                R.string.item_price,
                amount,
            )
        } else {
            amount
        }
        viewBinding.etAmount.setText(amountString)
        updateButtonEnabledState(btnEnabled)
    }

    private fun updateButtonEnabledState(enabled: Boolean) {
        with(viewBinding.btnNext) {
            isEnabled = enabled
            isClickable = enabled
        }
    }

    private fun tryStartOrReverseNextBtnTransitionAnim(transitionAnimState: TransitionAnimState) {
        with(viewBinding.btnNext) {
            isEnabled = transitionAnimState.isStart
            isClickable = transitionAnimState.isStart
            if (transitionAnimState.isStart) {
                (background as TransitionDrawable).startTransition(TRANSITION_ANIM_DURATION)
            } else {
                (background as TransitionDrawable).reverseTransition(TRANSITION_ANIM_DURATION)
            }
        }
    }

    private fun updateInputFieldColors(transitionAnimState: TransitionAnimState) {
        with(viewBinding) {
            val color = if (transitionAnimState.isStart) {
                R.color.white
            } else {
                R.color.white_67
            }
            etAmount.setTextColor(resColor(color))
            ivDelete.imageTintList = ColorStateList.valueOf(resColor(color))
            llInputAmount.backgroundTintList = if (transitionAnimState.isStart) {
                ColorStateList.valueOf(resColor(R.color.white_67))
            } else {
                null
            }
        }
    }

    override fun onDestroyView() {
        queryTextChangeListener?.onDestroy()
        queryTextChangeListener = null
        dialogError?.dismiss()
        dialogError = null
        dialogErrorTextView = null
        super.onDestroyView()
    }
}