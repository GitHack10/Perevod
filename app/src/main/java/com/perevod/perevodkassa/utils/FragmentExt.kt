package com.perevod.perevodkassa.utils

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.resColor(@ColorRes color: Int): Int =
    ContextCompat.getColor(this.requireContext(), color)