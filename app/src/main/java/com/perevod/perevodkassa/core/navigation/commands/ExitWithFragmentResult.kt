package com.perevod.perevodkassa.core.navigation.commands

import android.os.Bundle
import com.github.terrakok.cicerone.Command

class ExitWithFragmentResult(
    val requestCode: String,
    val data: Bundle
) : Command