package com.perevod.perevodkassa.presentation.global.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.perevod.perevodkassa.utils.ClickListenerWrapper

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
fun TextView.setDrawables(
    startDrawable: Drawable? = null,
    topDrawable: Drawable? = null,
    endDrawable: Drawable? = null,
    bottomDrawable: Drawable? = null
) {

    setCompoundDrawablesRelativeWithIntrinsicBounds(
        startDrawable,
        topDrawable,
        endDrawable,
        bottomDrawable
    )
}

fun View.onDelayedClick(milliseconds: Long? = null, listener: (v: View) -> Unit) {
    setOnClickListener(milliseconds?.let { ClickListenerWrapper(it, listener) }
        ?: ClickListenerWrapper(listener = listener))
}

fun View.requestNewSize(
    width: Int? = null, height: Int? = null
) {
    width?.let { layoutParams.width = width }
    height?.let { layoutParams.height = height }
    layoutParams = layoutParams
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeEnabled() {
    isFocusable = true
    isEnabled = true
    isFocusable = true
}

fun View.makeDisabled() {
    isFocusable = false
    isEnabled = false
    isFocusable = false
}

fun View.setEnabled(enabled: Boolean) {
    isFocusable = enabled
    isEnabled = enabled
    isFocusable = enabled
}

fun Button.clearText() {
    text = ""
}

fun AppCompatEditText.text() = text.toString()
fun AppCompatEditText.clearText() = setText("")
fun AppCompatEditText.isNotEmpty() = text.toString().isNotEmpty()
fun AppCompatEditText.isEmpty() = text.toString().isEmpty()