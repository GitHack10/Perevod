package com.perevod.perevodkassa.presentation.global

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * Delay added to prevent micro-freezes on transition animation.
     */
    init {
        viewModelScope.launch {
            delay(300)
            onAttach()
        }
    }

    open fun onAttach() {}
    open fun onBackPressed() {}
}