package com.perevod.perevodkassa.presentation.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.data.global.PreferenceStorage
import com.perevod.perevodkassa.presentation.global.extensions.getDeviceId
import com.perevod.perevodkassa.presentation.global.extensions.setLaunchScreen
import com.perevod.perevodkassa.presentation.global.navigation.CustomNavigator
import com.perevod.perevodkassa.presentation.global.navigation.Screens
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
class AppActivity : AppCompatActivity() {

    private val prefs: PreferenceStorage by inject()
    private val navigatorHolder: NavigatorHolder by inject()

    private lateinit var navigator: Navigator

//    private lateinit var fptr: IFptr

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_screen)

        window.setBackgroundDrawableResource(R.color.white)

        prefs.deviceId = getDeviceId(this)

        Timber.tag("DEVICE_ID").e(getDeviceId(this))

        navigator = CustomNavigator(this, R.id.appContainer).apply {
            if (prefs.isLogged)
                setLaunchScreen(Screens.homeScreen())
            else
                setLaunchScreen(Screens.authScreen())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SHOW_SETTINGS) {
            // Записываем настройки в объект
//            fptr.settings = data?.getStringExtra(SettingsActivity.DEVICE_SETTINGS)
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

    companion object {

        const val REQUEST_SHOW_SETTINGS = 101
    }
}