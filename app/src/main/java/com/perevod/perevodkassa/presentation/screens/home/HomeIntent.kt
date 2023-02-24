package com.perevod.perevodkassa.presentation.screens.home

sealed class HomeIntent {

    object PrintReceipt : HomeIntent()
    object StaffClose : HomeIntent()
    object OnBackPressed : HomeIntent()
    object GoToEnterScreen : HomeIntent()
}