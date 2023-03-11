package com.perevod.perevodkassa.core.navigation.commands

import com.github.terrakok.cicerone.Command
import com.perevod.perevodkassa.core.navigation.route.FragmentRoute

class CloseDialogCommand(
    val screen: FragmentRoute
) : Command