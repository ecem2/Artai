package com.adentech.artai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ArtaiApplication: Application() {

    override fun onCreate() {
        super.onCreate()

    }

    companion object {
        var hasSubscription : Boolean = true //false olacak
    }
}