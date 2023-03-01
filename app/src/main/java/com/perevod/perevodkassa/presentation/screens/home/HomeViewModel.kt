package com.perevod.perevodkassa.presentation.screens.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.domain.PrintModel
import com.perevod.perevodkassa.domain.interactor.HomeInteractor
import com.perevod.perevodkassa.presentation.global.BaseViewModel
import com.perevod.perevodkassa.presentation.global.extensions.getFormattedPrice
import com.perevod.perevodkassa.presentation.global.navigation.Screens
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val interactor: HomeInteractor,
    private val prefs: PreferenceStorage,
    private val router: Router,
) : BaseViewModel() {

    private val amountMutableLiveData: MutableLiveData<Float> = MutableLiveData(0f)
    private val printMutableLiveData: MutableLiveData<PrintModel?> = MutableLiveData()

    private val _viewState = MutableStateFlow<HomeViewState<Any>>(HomeViewState.Idle)

    val viewState: StateFlow<HomeViewState<Any>> = _viewState

    val userIntent = MutableSharedFlow<HomeIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        if (prefs.sdkKey.isNullOrBlank()) {
            _viewState.value = HomeViewState.DisableInput
            connect()
        } else {
            _viewState.value = HomeViewState.EnableInput
        }
    }

    override fun onAttach() {
        handleIntents()
    }

    private fun onInputAmountChanged(inputText: String?) {
        val text = inputText ?: return
        val currentAmount = text.toIntOrNull() ?: 0
        val formattedAmount = currentAmount.getFormattedPrice()
        val btnEnabled = currentAmount > 0
        amountMutableLiveData.value = currentAmount.toFloat()
        _viewState.value = HomeViewState.FetchInputAmount(
            formattedAmount,
            btnEnabled
        )
    }

    private fun handleIntents() {

        userIntent.onEach { intent ->
            when (intent) {
                is HomeIntent.OnBackPressed -> onBackPressed()
                is HomeIntent.OnAmountChanged -> onInputAmountChanged(intent.inputText)
                is HomeIntent.OnButtonDoneClick -> onButtonDoneClicked()
                is HomeIntent.GoToPaymentSuccessScreen -> goToPaymentSuccessScreen()
            }
        }.launchIn(viewModelScope)
    }

    private fun connect() {
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = interactor.connectCashier()) {
                is HomeViewState.SuccessConnectCashier -> {
                    prefs.sdkKey = result.connectCashierResponse.sdkKey
                    _viewState.value = HomeViewState.EnableInput
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = HomeViewState.HideLoading
        }
    }

    private fun onButtonDoneClicked() {
        val amount = amountMutableLiveData.value ?: return
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = interactor.initCashier(amount)) {
                is HomeViewState.SuccessInitCashier -> {
                    clearState()
                    userIntent.tryEmit(HomeIntent.GoToPaymentSuccessScreen)
                }
                else -> _viewState.value = result
            }
        }
    }

    private fun clearState() {
        amountMutableLiveData.value = 0f
        printMutableLiveData.value = null
        _viewState.value = HomeViewState.ClearState
    }

    private fun goToPaymentSuccessScreen() {
        router.navigateTo(Screens.paymentSuccessScreen())
    }
}