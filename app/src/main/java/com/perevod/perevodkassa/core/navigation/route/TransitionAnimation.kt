package com.perevod.perevodkassa.core.navigation.route

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import com.perevod.perevodkassa.R

data class TransitionAnimation(
    @AnimatorRes @AnimRes val enter: Int = R.anim.slide_from_end,
    @AnimatorRes @AnimRes val exit: Int = R.anim.slide_to_end,
    @AnimatorRes @AnimRes val popEnter: Int = R.anim.slide_from_end,
    @AnimatorRes @AnimRes val popExit: Int = R.anim.slide_to_end
)