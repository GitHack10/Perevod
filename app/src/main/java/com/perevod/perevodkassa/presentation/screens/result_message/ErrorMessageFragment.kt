package com.perevod.perevodkassa.presentation.screens.result_message

import by.kirich1409.viewbindingdelegate.viewBinding
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenErrorMessageBinding
import com.perevod.perevodkassa.core.arch.BaseFragment
import com.perevod.perevodkassa.core.navigation.AppRouter
import com.perevod.perevodkassa.core.navigation.Screens
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.createRoundedRippleDrawable
import com.perevod.perevodkassa.utils.dpToPx
import com.perevod.perevodkassa.utils.resColor
import org.koin.android.ext.android.inject

class ErrorMessageFragment : BaseFragment(R.layout.screen_error_message) {

    private val router: AppRouter by inject()

    companion object {
        const val EXTRA_MESSAGE_ERROR = "EXTRA_MESSAGE_ERROR"
    }

    private val viewBinding: ScreenErrorMessageBinding by viewBinding()

    override fun prepareUi() {
        with(viewBinding) {
            ivError.background = createCircleDrawable(resColor(R.color.white_10))
            tvError.text = arguments?.getString(EXTRA_MESSAGE_ERROR)
            btnGoBack.background = createRoundedRippleDrawable(
                resColor(R.color.ripple_primary),
                24.dpToPx.toFloat(),
                resColor(R.color.grey_4D4D4D)
            )
        }
    }

    override fun onBackPressed() {
        router.replaceScreen(Screens.homeScreen())
    }
}