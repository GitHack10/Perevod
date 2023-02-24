package com.perevod.perevodkassa.presentation.global.extensions

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.BackTo
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.Screen
import java.util.Locale

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
fun Navigator.setLaunchScreen(screen: Screen) {
    applyCommands(
        arrayOf(
            BackTo(null),
            Replace(screen)
        )
    )
}

fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showKeyboard() = showKeyboard(currentFocus ?: View(this))

fun Fragment.showKeyboard() = activity?.showKeyboard()

fun Context.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun getDeviceId(context: Context) = Settings.Secure.getString(
    context.contentResolver,
    Settings.Secure.ANDROID_ID
)

fun Number.getFormattedPrice(): String = String.format(
    Locale("ru"),
    when (this) {
        is Double, is Float -> "%,.02f"
        else -> "%,d"
    },
    this
)

fun String.encodeBase64(): String = Base64.encodeToString(toByteArray(), Base64.URL_SAFE)