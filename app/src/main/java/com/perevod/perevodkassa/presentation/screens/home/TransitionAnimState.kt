package com.perevod.perevodkassa.presentation.screens.home

enum class TransitionAnimState {
    Idle,
    Start;

    val isStart: Boolean
        get() = this == Start

    companion object {
        fun getStateByEnabledFlag(enabled: Boolean) = if (enabled) {
            Start
        } else {
            Idle
        }
    }
}