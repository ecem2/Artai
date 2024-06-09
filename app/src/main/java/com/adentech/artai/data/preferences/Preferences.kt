package com.adentech.artai.data.preferences


interface Preferences {

    fun setFirstLaunch(isFirstTime: Boolean)

    fun getFirstLaunch(): Boolean

    fun getWatchAds(): Int

    fun setToken(token: String)

    fun getToken(): String

    fun setBaseUrl(url: String)

    fun getBaseUrl(): String

}