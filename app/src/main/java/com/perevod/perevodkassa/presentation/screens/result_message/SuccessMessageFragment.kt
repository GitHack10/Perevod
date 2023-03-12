package com.perevod.perevodkassa.presentation.screens.result_message

import by.kirich1409.viewbindingdelegate.viewBinding
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenSuccessMessageBinding
import com.perevod.perevodkassa.core.arch.BaseFragment
import com.perevod.perevodkassa.core.navigation.AppRouter
import com.perevod.perevodkassa.core.navigation.Screens
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.createRoundedRippleDrawable
import com.perevod.perevodkassa.utils.dpToPx
import com.perevod.perevodkassa.utils.hideSystemUI
import com.perevod.perevodkassa.utils.resColor
import org.koin.android.ext.android.inject

class SuccessMessageFragment : BaseFragment(R.layout.screen_success_message) {

    private val router: AppRouter by inject()

    companion object {
        const val EXTRA_MESSAGE_SUCCESS = "EXTRA_MESSAGE_SUCCESS"
    }

    private val viewBinding: ScreenSuccessMessageBinding by viewBinding()

    override fun onResume() {
        super.onResume()
        activity?.window?.hideSystemUI()
    }

    override fun prepareUi() {
        with(viewBinding) {
            ivSuccess.background = createCircleDrawable(resColor(R.color.white_10))
            tvSuccess.text = arguments?.getString(EXTRA_MESSAGE_SUCCESS)
            btnGoBack.background = createRoundedRippleDrawable(
                resColor(R.color.ripple_primary),
                24.dpToPx.toFloat(),
                resColor(R.color.grey_4D4D4D)
            )
            btnGoBack.onDelayedClick {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        router.replaceScreen(Screens.homeScreen())
    }
}