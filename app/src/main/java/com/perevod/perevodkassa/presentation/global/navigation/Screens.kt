package com.perevod.perevodkassa.presentation.global.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.presentation.screens.home.HomeFragment
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessFragment
import com.perevod.perevodkassa.utils.withArguments

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object Screens {

    fun homeScreen() = FragmentScreen { HomeFragment() }
    fun paymentSuccessScreen(amount: Int) = FragmentScreen {
        PaymentSuccessFragment().withArguments(PaymentSuccessFragment.EXTRA_AMOUNT to amount)
    }
}