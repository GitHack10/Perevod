package com.perevod.perevodkassa.core.navigation.commands

import com.github.terrakok.cicerone.Command

data class SendEventToScreen(
    val event: Any,
    val screenKey: String,
    val toVisibleOnly: Boolean = true
) : Command