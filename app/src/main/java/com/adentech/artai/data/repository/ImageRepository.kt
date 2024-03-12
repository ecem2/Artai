package com.adentech.artai.data.repository

import android.util.Log
import com.adentech.artai.core.common.Resource
import com.adentech.artai.data.model.EditResponse
import com.adentech.artai.data.remote.ApiService
import com.adentech.artai.utils.ReqBodyWithProgress
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun loadImage(imageFile: File, maskFile: File, prompt: String, n: Int, size: String): Resource<EditResponse> {
        return try {
            val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            bodyBuilder.addFormDataPart("image", "imageFile.name", imageFile.asRequestBody("image/*".toMediaTypeOrNull()))
            bodyBuilder.addFormDataPart("mask", "maskFile.name", maskFile.asRequestBody("image/*".toMediaTypeOrNull()))
            bodyBuilder.addFormDataPart("prompt", prompt)
            bodyBuilder.addFormDataPart("n", n.toString())
            bodyBuilder.addFormDataPart("size", size)

            val requestBody = bodyBuilder.build()
            val requestBodyWithProgress = ReqBodyWithProgress.ReqBodyWithProgress(requestBody) { progress ->
                Log.d("salimmm repository", "REPOSITORY Upload Progress: $progress")
            }
            val response = apiService.editImage(requestBodyWithProgress)
            if (response.isSuccessful) {
                response.body().let {
                    return Resource.success(it)
                }
            } else {
                Resource.error(response.toString(), null)
            }
        } catch (e: java.lang.Exception) {
            Resource.error("No data!", null)
        }
    }
}