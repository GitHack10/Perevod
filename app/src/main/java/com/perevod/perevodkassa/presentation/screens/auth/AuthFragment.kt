package com.perevod.perevodkassa.presentation.screens.auth

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenAuthBinding
import com.perevod.perevodkassa.presentation.global.BaseFragment
import com.perevod.perevodkassa.presentation.global.extensions.launchWhenStarted
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.presentation.global.extensions.openPhoneBook
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthFragment : BaseFragment(R.layout.screen_auth) {

    private val viewBinding: ScreenAuthBinding by viewBinding()
    private val viewModel: AuthViewModel by viewModel()

    override fun prepareUi() {
        with(viewBinding) {
            btnEnter.onDelayedClick {
                viewModel.userIntent.tryEmit(AuthIntent.OpenShiftRequest)
            }
            contactPhoneTextView.onDelayedClick {
                openPhoneBook(contactPhoneTextView.text.toString())
            }
        }
    }

    override fun setupViewModel() {
        viewModel.viewState.onEach { viewState ->
            when (viewState) {
                is AuthViewState.Idle -> Unit
                is AuthViewState.OnSuccess -> Unit
                is AuthViewState.ShowLoading -> showLoading()
                is AuthViewState.HideLoading -> hideLoading()
                is AuthViewState.OnError -> showError(message = viewState.message)
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun showLoading() {
        viewBinding.run {
            vLoading.isVisible = true
        }
    }

    private fun hideLoading() {
        viewBinding.run {
            vLoading.isVisible = false
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}