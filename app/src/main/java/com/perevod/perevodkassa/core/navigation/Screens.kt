package com.perevod.perevodkassa.core.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.presentation.screens.home.HomeFragment
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessFragment
import com.perevod.perevodkassa.presentation.screens.result_message.ErrorMessageFragment
import com.perevod.perevodkassa.presentation.screens.result_message.SuccessMessageFragment
import com.perevod.perevodkassa.utils.withArguments

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object Screens {

    fun homeScreen() = FragmentScreen { HomeFragment() }
    fun paymentSuccessScreen(amount: Float) = FragmentScreen {
        PaymentSuccessFragment().withArguments(PaymentSuccessFragment.EXTRA_AMOUNT to amount)
    }

    fun errorMessageScreen(message: String) = FragmentScreen {
        ErrorMessageFragment() .withArguments(ErrorMessageFragment.EXTRA_MESSAGE_ERROR to message)
    }

    fun successMessageScreen(message: String) = FragmentScreen {
        SuccessMessageFragment() .withArguments(SuccessMessageFragment.EXTRA_MESSAGE_SUCCESS to message)
    }
}