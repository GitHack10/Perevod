package com.perevod.perevodkassa.presentation.global.extensions

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import timber.log.Timber

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
fun Fragment.openPhoneBook(phone: String) {
    try {
        startActivity(
            Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:$phone")
            }
        )
    } catch (e: ActivityNotFoundException) {
        Timber.tag("IntentExtension").e("$e")
    }
}