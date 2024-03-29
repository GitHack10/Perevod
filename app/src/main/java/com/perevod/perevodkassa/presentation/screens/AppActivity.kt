package com.perevod.perevodkassa.presentation.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.presentation.global.extensions.setLaunchScreen
import com.perevod.perevodkassa.core.navigation.CustomNavigator
import com.perevod.perevodkassa.core.navigation.Screens
import com.perevod.perevodkassa.utils.hideSystemUI
import org.koin.android.ext.android.inject

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
class AppActivity : AppCompatActivity() {

    private val navigatorHolder: NavigatorHolder by inject()

    private lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_screen)

        window.setBackgroundDrawableResource(R.color.black)

        navigator = CustomNavigator(this, R.id.appContainer).apply {
            setLaunchScreen(Screens.homeScreen())
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
        // Configure the behavior of the hidden system bars.
        window.hideSystemUI()
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}