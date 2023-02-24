package com.perevod.perevodkassa.presentation.screens.home

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.domain.interactor.HomeInteractor
import com.perevod.perevodkassa.presentation.global.BaseViewModel
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

    private val _viewState = MutableStateFlow<HomeViewState<Any>>(HomeViewState.Idle)

    val viewState: StateFlow<HomeViewState<Any>> = _viewState

    val userIntent = MutableSharedFlow<HomeIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onAttach() {
        handleIntents()
    }

    private fun handleIntents() {

        userIntent.onEach { intent ->
            when (intent) {
                is HomeIntent.PrintReceipt -> printReceipt()
                is HomeIntent.StaffClose -> staffClose()
                is HomeIntent.OnBackPressed -> onBackPressed()
                is HomeIntent.GoToEnterScreen -> goToEnterScreen()
            }
        }.launchIn(viewModelScope)
    }

    private fun printReceipt() {

        _viewState.value = HomeViewState.ShowLoading

        viewModelScope.launch {

            when (val result = interactor.printReceipt()) {

                is HomeViewState.SuccessPrintReceipt -> {
                    _viewState.value = HomeViewState.SuccessPrintReceipt(result.printModel)
                    _viewState.value = HomeViewState.UpdateTotalValues(0, 0.0)
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = HomeViewState.HideLoading
        }
    }

    private fun staffClose() {
        _viewState.value = HomeViewState.ShowLoading
        viewModelScope.launch {
            when (val result = interactor.staffClose()) {
                is HomeViewState.SuccessStaffClose -> {
                    _viewState.value = HomeViewState.SuccessStaffClose(result.printModel)
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = HomeViewState.HideLoading
        }
    }

    private fun goToEnterScreen() {
        prefs.isLogged = false
        router.replaceScreen(Screens.authScreen())
    }
}