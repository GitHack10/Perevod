package com.perevod.perevodkassa.core.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    protected suspend fun runOnUi(block: suspend () -> Unit) =
        withContext(Dispatchers.Main) {
            block()
        }

    open fun onAttach() {}
    open fun onBackPressed() {}
}