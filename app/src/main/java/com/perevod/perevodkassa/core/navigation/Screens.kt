package com.perevod.perevodkassa.core.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.presentation.screens.home.HomeFragment
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentFragment
import com.perevod.perevodkassa.presentation.screens.result_message.ErrorMessageFragment
import com.perevod.perevodkassa.presentation.screens.result_message.PaymentSuccessFragment
import com.perevod.perevodkassa.utils.withArguments

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object Screens {

    fun homeScreen() = FragmentScreen { HomeFragment() }

    fun paymentScreen(amount: Float) = FragmentScreen {
        PaymentFragment().withArguments {
            putFloat(PaymentFragment.EXTRA_AMOUNT, amount)
        }
    }

    fun errorMessageScreen(message: String) = FragmentScreen {
        ErrorMessageFragment().withArguments {
            putString(ErrorMessageFragment.EXTRA_MESSAGE_ERROR, message)
        }
    }

    fun paymentSuccessScreen(message: String, paperPrint: String?) = FragmentScreen {
        PaymentSuccessFragment().withArguments {
            putString(PaymentSuccessFragment.EXTRA_MESSAGE_SUCCESS, message)
            putString(PaymentSuccessFragment.EXTRA_PAPER_PRINT, paperPrint)
        }
    }
}