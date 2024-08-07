package com.hc.artai.di

import com.hc.artai.data.preferences.Preferences
import com.hc.artai.data.remote.ApiParameters
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val preferences: Preferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val newRequestBuilder = chain.request().newBuilder()

        val token = preferences.getToken()
        if (token.isNotEmpty()) {
            newRequestBuilder.addHeader(ApiParameters.AUTH_HEADER, ApiParameters.TOKEN_TYPE) // TOKEN A BAK
        }

        return chain.proceed(newRequestBuilder.build())
    }
}

