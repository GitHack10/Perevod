package com.perevod.perevodkassa.core.navigation.data

import android.view.View

typealias SharedView = Pair<View, String>

class SharedTransitionInfo {

    private val sharedElements = mutableListOf<SharedView>()

    val names: List<String>
        get() = sharedElements.map { it.second }

    val elements: List<SharedView>
        get() = sharedElements

    val hasSharedElements: Boolean
        get() = sharedElements.isNotEmpty()

    fun add(data: SharedView) {
        sharedElements.add(data)
    }

    fun add(name: String, view: View) {
        sharedElements.add(SharedView(view, name))
    }

    fun addAll(data: List<SharedView>) {
        sharedElements.addAll(data)
    }

    fun clearResources() {
        sharedElements.clear()
    }
}