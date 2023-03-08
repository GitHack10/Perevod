package com.perevod.perevodkassa.presentation.screens.result_message

import by.kirich1409.viewbindingdelegate.viewBinding
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenErrorMessageBinding
import com.perevod.perevodkassa.presentation.global.BaseFragment
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.resColor

class ErrorMessageFragment : BaseFragment(R.layout.screen_error_message) {

    companion object {
        const val EXTRA_MESSAGE_ERROR = "EXTRA_MESSAGE_ERROR"
    }

    private val viewBinding: ScreenErrorMessageBinding by viewBinding()

    override fun prepareUi() {
        with(viewBinding) {
            ivError.background = createCircleDrawable(resColor(R.color.white_10))
            tvError.text = arguments?.getString(EXTRA_MESSAGE_ERROR)
        }
    }
}