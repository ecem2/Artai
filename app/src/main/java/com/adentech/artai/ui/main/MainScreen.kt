package com.adentech.artai.ui.main

import androidx.annotation.StringDef

@StringDef(
    value = [
        MainScreen.HOME,
        MainScreen.EDIT
    ]
)

annotation class MainScreen {
    companion object {
        const val HOME = "home"
        const val EDIT = "edit"
    }
}