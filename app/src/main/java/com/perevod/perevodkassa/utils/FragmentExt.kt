package com.perevod.perevodkassa.utils

import android.content.Context
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.resColor(@ColorRes color: Int): Int =
    ContextCompat.getColor(this.requireContext(), color)

fun Context.resColor(@ColorRes color: Int): Int =
    ContextCompat.getColor(this, color)

inline fun Fragment.withArguments(crossinline builder: Bundle.() -> Unit): Fragment = also {
    arguments = Bundle().apply {
        builder()
    }
}