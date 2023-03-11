package com.perevod.perevodkassa.core.navigation.route

import com.github.terrakok.cicerone.androidx.ActivityScreen

abstract class ActivityRoute : ActivityScreen {

    var animation: TransitionAnimation? = null

    fun withDefaultAnim(): ActivityRoute = this.apply {
        animation = TransitionAnimation()
    }

    fun withCustomAnim(transition: TransitionAnimation): ActivityRoute = this.apply {
        animation = transition
    }
}