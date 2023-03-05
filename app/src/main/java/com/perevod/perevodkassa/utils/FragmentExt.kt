package com.perevod.perevodkassa.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

fun Fragment.resColor(@ColorRes color: Int): Int =
    ContextCompat.getColor(this.requireContext(), color)

fun Context.resColor(@ColorRes color: Int): Int =
    ContextCompat.getColor(this, color)

fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any?>): T {
    arguments = bundleOf(*params)
    return this
}