package com.adentech.artai.extensions

import com.adentech.artai.core.common.Constants.BASE_URL_GENERATION
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

fun OkHttpClient.makePostRequest(
    requestBody: RequestBody,
    callback: (String?, String?) -> Unit
) {

    var jsonArray: JSONArray
    var result: String? = null
    val apiKey = "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"
    val request = Request.Builder()
        .url(BASE_URL_GENERATION)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

    this.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            return callback(null, e.message)
        }
        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            val jsonObject: JSONObject

            return try {
                if (body != null) {
                    jsonObject = JSONObject(body)
                    jsonArray = jsonObject.getJSONArray("data")
                    result = jsonArray.getJSONObject(0).getString("url").trim()
                    callback(result, null)
                } else {
                    callback(null, "Response body is null")
                }
            } catch (e: JSONException) {
                callback(null, "${e.message}")
            } catch (e: Exception) {
                callback(null, "${e.message}")
            }
        }
    })
}