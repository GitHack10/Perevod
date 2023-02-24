package com.perevod.perevodkassa.presentation.global.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.presentation.screens.auth.AuthFragment
import com.perevod.perevodkassa.presentation.screens.home.HomeFragment

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
object Screens {

    fun authScreen() = FragmentScreen { AuthFragment() }
    fun homeScreen() = FragmentScreen { HomeFragment() }
}