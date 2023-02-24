package com.perevod.perevodkassa.utils

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.*

internal class DebouncingQueryTextListener(
    private val debouncePeriod: Long = 1000,
    private val onDebouncingQueryTextChange: (String?) -> Unit,
    private val onDebouncingQueryTextSubmit: (String?) -> Unit
) : TextWatcher {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private var searchJob: Job? = null

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(newText: Editable?) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            delay(debouncePeriod)
            onDebouncingQueryTextChange(newText.toString())
        }
    }

    fun onDestroy() = searchJob?.cancel()
}