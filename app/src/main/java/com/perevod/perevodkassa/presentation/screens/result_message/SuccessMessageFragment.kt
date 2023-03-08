package com.perevod.perevodkassa.presentation.screens.result_message

import by.kirich1409.viewbindingdelegate.viewBinding
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenSuccessMessageBinding
import com.perevod.perevodkassa.presentation.global.BaseFragment
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.resColor

class SuccessMessageFragment : BaseFragment(R.layout.screen_success_message) {

    companion object {
        const val EXTRA_MESSAGE_SUCCESS = "EXTRA_MESSAGE_SUCCESS"
    }

    private val viewBinding: ScreenSuccessMessageBinding by viewBinding()

    override fun prepareUi() {
        with(viewBinding) {
            ivSuccess.background = createCircleDrawable(resColor(R.color.white_10))
            tvSuccess.text = arguments?.getString(EXTRA_MESSAGE_SUCCESS)
        }
    }
}