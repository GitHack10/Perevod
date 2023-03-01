package com.perevod.perevodkassa.presentation.global.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.presentation.screens.home.HomeFragment
import com.perevod.perevodkassa.presentation.screens.payment_success.PaymentSuccessFragment

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object Screens {

    fun homeScreen() = FragmentScreen { HomeFragment() }
    fun paymentSuccessScreen() = FragmentScreen { PaymentSuccessFragment() }
}