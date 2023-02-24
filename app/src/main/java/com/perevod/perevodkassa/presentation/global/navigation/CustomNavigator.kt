package com.perevod.perevodkassa.presentation.global.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.presentation.global.extensions.hideKeyboard

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
class CustomNavigator(activity: FragmentActivity, containerId: Int) : AppNavigator(activity, containerId) {

    private val context: FragmentActivity = activity

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        super.setupFragmentTransaction(screen, fragmentTransaction, currentFragment, nextFragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
    }

    override fun applyCommands(commands: Array<out Command>) {
        context.hideKeyboard()
        super.applyCommands(commands)
    }
}