package com.perevod.perevodkassa.presentation.global.extensions

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.utils.hideSystemUI

fun Context.dialogBuild(dialogView: View): Dialog {
    return Dialog(this, R.style.RoundedDialogStyle)
        .apply {
            setCancelable(false)
            setContentView(dialogView)
            create()
            window?.hideSystemUI()
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
}