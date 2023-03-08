package com.perevod.perevodkassa.presentation.screens.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.domain.PrintModel
import com.perevod.perevodkassa.domain.use_case.ConnectCashierUseCase
import com.perevod.perevodkassa.domain.use_case.InitCashierUseCase
import com.perevod.perevodkassa.presentation.global.BaseViewModel
import com.perevod.perevodkassa.presentation.global.navigation.Screens
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val MAX_DIGITS = 6

class HomeViewModel(
    private val connectCashierUseCase: ConnectCashierUseCase,
    private val initCashierUseCase: InitCashierUseCase,
    private val prefs: PreferenceStorage,
    private val router: Router,
) : BaseViewModel() {

    private var printModel: PrintModel? = null

    private var currentAmount: Number = 0
    private var currentAmountString: String = ""

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

    private fun onInputAmountChanged(keyNumber: KeyboardNumber) {
        when (keyNumber) {
            KeyboardNumber.Delete -> {
                currentAmountString = currentAmountString.dropLast(1)
            }
            KeyboardNumber.Dot -> {
                if (currentAmountString.contains(keyNumber.value)) {
                    return
                }
                currentAmountString = currentAmountString.ifBlank { KeyboardNumber.Zero.value } + keyNumber.value
            }
            else -> {
                val splitString = currentAmountString.split(KeyboardNumber.Dot.value)
                val (int, dec) = splitString.first() to splitString.last()
                if (splitString.size > 1 && dec.count() >= 2) {
                    return
                }
                if (int.count() >= MAX_DIGITS && dec.count() > 2) {
                    return
                }
                if (currentAmountString.contains(KeyboardNumber.Dot.value).not()
                    && currentAmountString.startsWith(KeyboardNumber.Zero.value)
                    && keyNumber == KeyboardNumber.Zero
                ) {
                    return
                }
                if (currentAmountString == KeyboardNumber.Zero.value) {
                    currentAmountString = keyNumber.value
                } else {
                    currentAmountString += keyNumber.value
                }
            }
        }
        currentAmount = if (currentAmountString.contains(KeyboardNumber.Dot.value)) {
            currentAmountString.toFloatOrNull() ?: 0f
        } else {
            currentAmountString.toIntOrNull() ?: 0
        }
        val btnEnabled = currentAmount.toFloat() > 0
        _viewState.value = HomeViewState.FetchInputAmount(
            currentAmountString,
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
                is HomeIntent.OnAmountChanged -> onInputAmountChanged(intent.keyNumber)
                is HomeIntent.OnButtonDoneClick -> onButtonDoneClicked()
                is HomeIntent.GoToPaymentSuccessScreen -> goToPaymentSuccessScreen(intent.amount)
            }
        }.launchIn(viewModelScope)
    }

    private fun connect() {
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = connectCashierUseCase()) {
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
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = initCashierUseCase(InitCashierUseCase.Params(currentAmount))) {
                is HomeViewState.SuccessInitCashier -> {
                    val amount = currentAmount.toFloat()
                    prefs.lastOrderUuid = result.initCashierResponse.orderUuid
                    clearState()
                    userIntent.tryEmit(HomeIntent.GoToPaymentSuccessScreen(amount))
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = HomeViewState.HideLoading
        }
    }

    private fun clearState() {
        printModel = null
        inputStateMutableLiveData.value = TransitionAnimState.Idle
        currentAmount = 0f
    }

    private fun goToPaymentSuccessScreen(amount: Float) {
        router.navigateTo(Screens.paymentSuccessScreen(amount))
    }
}