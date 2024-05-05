package com.adentech.artai.data.remote

import com.adentech.artai.data.model.imagegeneration.GenerationRequest
import com.adentech.artai.data.model.output.OutputResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

//    @POST
//    suspend fun getRequiredUrl(
//       // todo request model
//    ): Resource<Response<EditResponse>> // todo output response
//
//
//    //@GET
//    //fun getImage(@Url imageUrl: String): Resource<Response<>>


    @POST("/v1/predictions")
    suspend fun getRequiredUrl(
        @Body request: GenerationRequest
    ): Response<OutputResponse>

    @GET
    suspend fun getGeneratedList(@Url url: String): Response<OutputResponse>





    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

//    @POST
//    suspend fun editImage(
//        @Body body: ReqBodyWithProgress.ReqBodyWithProgress
//    ): Response<EditResponse>


}