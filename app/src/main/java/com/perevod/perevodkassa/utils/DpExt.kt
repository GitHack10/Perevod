package com.perevod.perevodkassa.utils

import android.content.res.Resources

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dpToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()