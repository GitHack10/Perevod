package com.perevod.perevodkassa.utils

import android.view.View

internal class ClickListenerWrapper : View.OnClickListener {

    private val listener1: View.OnClickListener?
    private val listener2: ((v: View) -> Unit)?
    private val milliseconds: Long

    private var lastClickTimestamp: Long = 0

    constructor(milliseconds: Long = EVENT_CONSUMPTION_WINDOW, listener: View.OnClickListener) {
        this.listener1 = listener
        this.listener2 = null
        this.milliseconds = milliseconds
    }

    constructor(milliseconds: Long = EVENT_CONSUMPTION_WINDOW, listener: (v: View) -> Unit) {
        this.listener1 = null
        this.listener2 = listener
        this.milliseconds = milliseconds
    }

    override fun onClick(v: View) {
        val timestamp = System.currentTimeMillis()

        if (canConsumeClickAt(timestamp)) {
            listener1?.onClick(v)
            listener2?.invoke(v)

            lastClickTimestamp = timestamp
        }
    }

    private fun canConsumeClickAt(timestamp: Long): Boolean =
        (timestamp - lastClickTimestamp) >= EVENT_CONSUMPTION_WINDOW

    companion object {
        private const val EVENT_CONSUMPTION_WINDOW = 350L
    }
}