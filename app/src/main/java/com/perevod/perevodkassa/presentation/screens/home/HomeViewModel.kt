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

    private var printModel: PrintModel? = null

    private var currentAmountList: List<Int> = emptyList()

    private val _viewState = MutableStateFlow<HomeViewState<Any>>(HomeViewState.Idle)

    val viewState: StateFlow<HomeViewState<Any>> = _viewState

    val userIntent = MutableSharedFlow<HomeIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val inputStateMutableLiveData: MutableLiveData<TransitionAnimState> =
        MutableLiveData(TransitionAnimState.Idle)

    init {
        if (prefs.sdkKey.isNullOrBlank()) {
            connect()
        }
    }

    override fun onAttach() {
        handleIntents()
    }

    private fun onInputAmountChanged(inputText: KeyboardNumber) {
        val newAmountList = when {
            inputText == KeyboardNumber.Delete && currentAmountList.isNotEmpty() -> {
                currentAmountList.dropLast(1)
            }
            currentAmountList.isNotEmpty() -> {
                currentAmountList + listOf(inputText.value)
            }
            else -> {
                listOf(inputText.value)
            }
        }
        val newAmount = newAmountList.joinToString("").toIntOrNull() ?: 0
        val formattedAmount = newAmount.getFormattedPrice()
        val btnEnabled = newAmount > 0
        currentAmountList = newAmountList
        _viewState.value = HomeViewState.FetchInputAmount(
            formattedAmount,
            btnEnabled
        )
        val currentAnimState = inputStateMutableLiveData.value ?: TransitionAnimState.Idle
        val newAnimState = TransitionAnimState.getStateByEnabledFlag(btnEnabled)
        if (currentAnimState == newAnimState) {
            return
        }
        inputStateMutableLiveData.value = newAnimState
    }

    private fun handleIntents() {

        userIntent.onEach { intent ->
            when (intent) {
                is HomeIntent.OnBackPressed -> onBackPressed()
                is HomeIntent.OnAmountChanged -> onInputAmountChanged(intent.inputText)
                is HomeIntent.OnButtonDoneClick -> onButtonDoneClicked()
                is HomeIntent.GoToPaymentSuccessScreen -> goToPaymentSuccessScreen(intent.amount)
            }
        }.launchIn(viewModelScope)
    }

    private fun connect() {
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = interactor.connectCashier()) {
                is HomeViewState.SuccessConnectCashier -> {
                    prefs.sdkKey = result.connectCashierResponse.sdkKey
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = HomeViewState.HideLoading
        }
    }

    private fun onButtonDoneClicked() {
        val amount = currentAmountList
            .joinToString("")
            .toIntOrNull() ?: return
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = interactor.initCashier(amount.toFloat())) {
                is HomeViewState.SuccessInitCashier -> {
                    clearState()
                    userIntent.tryEmit(HomeIntent.GoToPaymentSuccessScreen(amount))
                }
                else -> _viewState.value = result
            }
        }
    }

    private fun clearState() {
        printModel = null
        inputStateMutableLiveData.value = TransitionAnimState.Idle
        currentAmountList = emptyList()
    }

    private fun goToPaymentSuccessScreen(amount: Int) {
        router.navigateTo(Screens.paymentSuccessScreen(amount))
    }
}