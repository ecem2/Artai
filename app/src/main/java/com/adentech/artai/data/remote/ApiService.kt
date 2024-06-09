package com.adentech.artai.data.remote

import com.adentech.artai.data.model.imagegeneration.GenerationRequest
import com.adentech.artai.data.model.output.OutputResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST("/v1/predictions")
    suspend fun getRequiredUrl(
        @Body request: GenerationRequest,
    ): Response<OutputResponse>

    @GET
    suspend fun getGeneratedList(@Url url: String): Response<OutputResponse>


}