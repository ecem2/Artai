package com.adentech.artai.data.remote

import com.adentech.artai.data.model.EditResponse
import com.adentech.artai.utils.ReqBodyWithProgress
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    @POST
    suspend fun editImage(
        @Body body: ReqBodyWithProgress.ReqBodyWithProgress
    ): Response<EditResponse>
}