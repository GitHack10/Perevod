package com.perevod.perevodkassa.presentation.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.presentation.global.extensions.setLaunchScreen
import com.perevod.perevodkassa.presentation.global.navigation.CustomNavigator
import com.perevod.perevodkassa.presentation.global.navigation.Screens
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

        window.setBackgroundDrawableResource(R.color.white)

        navigator = CustomNavigator(this, R.id.appContainer).apply {
            setLaunchScreen(Screens.homeScreen())
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}