package com.perevod.perevodkassa.presentation.screens.auth

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.domain.interactor.AuthInteractor
import com.perevod.perevodkassa.presentation.global.BaseViewModel
import com.perevod.perevodkassa.presentation.global.navigation.Screens
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel(
    private val router: Router,
    private val prefs: PreferenceStorage,
    private val interactor: AuthInteractor
) : BaseViewModel() {

    private val _viewState by lazy(LazyThreadSafetyMode.NONE) {
        MutableStateFlow<AuthViewState<Any>>(AuthViewState.Idle)
    }

    val viewState: StateFlow<AuthViewState<Any>>
        get() = _viewState

    val userIntent = MutableSharedFlow<AuthIntent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onAttach() {
        handleIntents()
    }

    private fun handleIntents() {

        userIntent.onEach { intent ->
            when (intent) {
                is AuthIntent.SendCardNumberRequest -> staffOpen(cardNumber = intent.cardNumber)
                is AuthIntent.OpenShiftRequest -> staffOpen()
            }
        }.launchIn(viewModelScope)
    }

    private fun staffOpen(cardNumber: String = "") {
        _viewState.value = AuthViewState.ShowLoading
        viewModelScope.launch {
            when (val result = interactor.staffOpen(cardNumber = cardNumber)) {
                is AuthViewState.OnSuccess -> {
                    if (result.response.code == 1) {
                        prefs.isLogged = true
                        navigateToHomeScreen()
                    } else {
                        _viewState.value = AuthViewState.OnError("Ошибка авторизации")
                        Timber.tag("AUTH_RESPONSE").e("Ошибка авторизации")
                    }
                }
                else -> _viewState.value = result
            }
        }.invokeOnCompletion {
            _viewState.value = AuthViewState.HideLoading
        }
    }

    private fun navigateToHomeScreen() = router.replaceScreen(Screens.homeScreen())
}