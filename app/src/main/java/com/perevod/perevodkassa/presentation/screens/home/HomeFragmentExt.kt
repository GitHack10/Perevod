package com.perevod.perevodkassa.presentation.screens.home

import android.widget.TextView
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenHomeBinding
import com.perevod.perevodkassa.utils.createCircleRippleDrawable
import com.perevod.perevodkassa.utils.createRoundedDrawable
import com.perevod.perevodkassa.utils.createRoundedRippleDrawable
import com.perevod.perevodkassa.utils.dpToPx
import com.perevod.perevodkassa.utils.resColor

fun ScreenHomeBinding.init() {
    tvNum1.setRoundedRippleDrawable()
    tvNum2.setRoundedRippleDrawable()
    tvNum3.setRoundedRippleDrawable()
    tvNum4.setRoundedRippleDrawable()
    tvNum5.setRoundedRippleDrawable()
    tvNum6.setRoundedRippleDrawable()
    tvNum7.setRoundedRippleDrawable()
    tvNum8.setRoundedRippleDrawable()
    tvNum9.setRoundedRippleDrawable()
    tvNumDot.setRoundedRippleDrawable()
    tvNum0.setRoundedRippleDrawable()
    with(etAmount) {
        isCursorVisible = false
        isClickable = false
        isFocusable = false
    }
    ivDelete.background = createCircleRippleDrawable(
        root.context.resColor(R.color.ripple_primary)
    )
}

private fun TextView.setRoundedRippleDrawable() {
    background = createRoundedRippleDrawable(
        context.resColor(R.color.ripple_primary),
        24f,
        createRoundedDrawable(
            24f,
            context.resColor(R.color.white_20),
        )
    )
}