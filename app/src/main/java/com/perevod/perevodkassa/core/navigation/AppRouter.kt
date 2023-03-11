package com.perevod.perevodkassa.core.navigation

import android.os.Bundle
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Router
import com.oxy.chat.core.navigation.commands.CloseDialogByKeyCommand
import com.perevod.perevodkassa.core.navigation.commands.AddScreenCommand
import com.perevod.perevodkassa.core.navigation.commands.CloseDialogCommand
import com.perevod.perevodkassa.core.navigation.commands.ExitWithFragmentResult
import com.perevod.perevodkassa.core.navigation.commands.OpenDialogCommand
import com.perevod.perevodkassa.core.navigation.commands.ReplaceTabCommand
import com.perevod.perevodkassa.core.navigation.commands.SendEventToScreen
import com.perevod.perevodkassa.core.navigation.route.FragmentRoute

class AppRouter : Router() {

    fun openDialog(screen: FragmentRoute, replaceCurrent: Boolean = false) {
        executeCommands(OpenDialogCommand(screen, replaceCurrent))
    }

    fun closeDialog(screen: FragmentRoute) {
        executeCommands(CloseDialogCommand(screen))
    }

    fun closeDialogByKey(screenKey: String) {
        executeCommands(CloseDialogByKeyCommand(screenKey))
    }

    fun addScreen(screen: FragmentRoute, hideCurrent: Boolean = true) {
        executeCommands(AddScreenCommand(screen, hideCurrent))
    }

    fun exitWithFragmentResult(requestCode: String, data: Bundle = Bundle.EMPTY) {
        executeCommands(ExitWithFragmentResult(requestCode, data))
    }

    fun replaceTab(screen: FragmentRoute) {
        executeCommands(ReplaceTabCommand(screen))
    }

    fun sendEventToScreen(screenKey: String, event: Any) {
        executeCommands(SendEventToScreen(event, screenKey))
    }

    fun runCommands(vararg commands: Command) {
        executeCommands(*commands)
    }
}