package com.perevod.perevodkassa.utils

import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnAttach

val WindowInsetsCompat.statusBarInsets: Int
    get() = getInsets(
        WindowInsetsCompat.Type.systemBars() or
                WindowInsetsCompat.Type.ime()
    ).top

val WindowInsetsCompat.navigationBarInsets: Int
    get() = getInsets(
        WindowInsetsCompat.Type.ime()
    ).bottom

fun View.doOnApplyWindowInsets(listener: (View, WindowInsetsCompat) -> WindowInsetsCompat) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        listener(view, insets)
    }
    requestApplyInsetsWhenAttached()
}

fun View.clearInsetsListener() {
    ViewCompat.setOnApplyWindowInsetsListener(this, null)
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        doOnAttach {
            requestApplyInsets()
        }
    }
}

fun WindowInsetsCompat.updateSystemWindowInsets(
    left: Int = systemWindowInsets.left,
    top: Int = systemWindowInsets.top,
    right: Int = systemWindowInsets.right,
    bottom: Int = systemWindowInsets.bottom
): WindowInsetsCompat {
    return WindowInsetsCompat
        .Builder(this)
        .setInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime(),
            Insets.of(left, top, right, bottom)
        )
        .build()
}

fun View.rememberBottomInsetsType(insets: WindowInsetsCompat): BottomInsetsType {
    if (tag == null) {
        tag = BottomInsetsType.BottomNav(insets.navigationBarInsets)
    } else {
        val currentType = tag as BottomInsetsType
        tag = when (currentType) {
            is BottomInsetsType.BottomNav -> if (currentType.value < insets.navigationBarInsets) {
                BottomInsetsType.Keyboard(insets.navigationBarInsets)
            } else {
                currentType.copy(value = insets.navigationBarInsets)
            }
            is BottomInsetsType.Keyboard -> if (currentType.value > insets.navigationBarInsets) {
                BottomInsetsType.BottomNav(insets.navigationBarInsets)
            } else {
                currentType.copy(value = insets.navigationBarInsets)
            }
        }
    }
    return tag as BottomInsetsType
}

sealed class BottomInsetsType(
    open val value: Int
) {

    data class BottomNav(override val value: Int) : BottomInsetsType(value)

    data class Keyboard(override val value: Int) : BottomInsetsType(value)
}

interface InsetsConsumer {

    fun consumeInsets(
        insets: WindowInsetsCompat,
        bottomInsetsType: BottomInsetsType
    ): WindowInsetsCompat = insets
}

val Window.windowInsetsController: WindowInsetsControllerCompat
    get() = WindowCompat.getInsetsController(this, decorView)

fun WindowInsetsControllerCompat.hideSystemBars() = this.hide(WindowInsetsCompat.Type.systemBars())

fun WindowInsetsControllerCompat.showSystemBars() = this.show(WindowInsetsCompat.Type.systemBars())

fun Window.hideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    WindowInsetsControllerCompat(this, decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Window.showSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(this, true)
    WindowInsetsControllerCompat(this, this.decorView).show(WindowInsetsCompat.Type.systemBars())
}