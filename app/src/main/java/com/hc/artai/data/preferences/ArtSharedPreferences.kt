package com.hc.artai.data.preferences

import android.content.Context

abstract class ArtSharedPreferences(context: Context) {

    abstract fun getPrefName(): String

    protected val prefs = context.getSharedPreferences(getPrefName(), Context.MODE_PRIVATE)
}