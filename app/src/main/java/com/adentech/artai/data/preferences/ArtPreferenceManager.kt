package com.adentech.artai.data.preferences

import android.content.Context
import com.adentech.artai.data.preferences.PreferenceConstants.AND_BASE_URL
import com.adentech.artai.data.preferences.PreferenceConstants.AND_GPT_TOKEN
import com.adentech.artai.data.preferences.PreferenceConstants.IS_FIRST_TIME_LAUNCH
import com.adentech.artai.extensions.set
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtPreferenceManager @Inject constructor(
    @ApplicationContext
    private val
    context: Context
): ArtSharedPreferences(context), Preferences {

    override fun getPrefName() = "ArtaiPref"

    override fun setFirstLaunch(isFirstTime: Boolean) {
        prefs.set(
            IS_FIRST_TIME_LAUNCH,
            isFirstTime
        )
    }

    override fun getFirstLaunch(): Boolean {
        return prefs.getBoolean(
            IS_FIRST_TIME_LAUNCH,
            true
        )
    }

    override fun setToken(token: String) {
        prefs.set(
            AND_GPT_TOKEN,
            token
        )
    }

    override fun getToken(): String {
        return prefs.getString(
            AND_GPT_TOKEN,
            null
        ).toString()
    }

    override fun setBaseUrl(url: String) {
        prefs.set(
            AND_BASE_URL,
            url
        )
    }

    override fun getBaseUrl(): String {
        return prefs.getString(
            AND_BASE_URL,
            null
        ).toString()
    }
}