package com.perevod.perevodkassa.core.navigation.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.perevod.perevodkassa.core.navigation.data.SharedTransitionInfo

private const val ARG_SHARED_ELEMENTS_NAMES = "com.perevod.perevodkassa.core.navigation.route.arg_shared_elements_names"

abstract class FragmentRoute : FragmentScreen {

    companion object {

        fun fetchArgSharedElementsNames(args: Bundle?): List<String> =
            (args?.getStringArray(ARG_SHARED_ELEMENTS_NAMES) ?: emptyArray<String>()).toList()
    }

    val sharedTransitionInfo = SharedTransitionInfo()

    var animation: TransitionAnimation? = null

    fun withDefaultAnim(): FragmentRoute = this.apply {
        animation = TransitionAnimation()
    }

    fun withCustomAnim(transition: TransitionAnimation): FragmentRoute = this.apply {
        animation = transition
    }

    open fun applySharedTransitionInfo(fragment: Fragment?) {
        fragment ?: return
        if (sharedTransitionInfo.hasSharedElements.not()) {
            return
        }
        var args = fragment.arguments
        if (args == null) {
            args = Bundle()
            fragment.arguments = args
        }
        args.putSharedElementsNamesIfExist()
    }

    private fun Bundle.putSharedElementsNamesIfExist() {
        putStringArray(ARG_SHARED_ELEMENTS_NAMES, sharedTransitionInfo.names.toTypedArray())
    }
}