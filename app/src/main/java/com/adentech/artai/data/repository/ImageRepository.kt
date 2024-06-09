package com.adentech.artai.data.repository

import com.adentech.artai.core.common.Resource
import com.adentech.artai.data.model.output.OutputResponse

interface ImageRepository {

    suspend fun getUrlForGeneration(prompt: String, style: String, width: Int, height: Int): Resource<OutputResponse>
    suspend fun getResultImage(url: String): Resource<String>

}