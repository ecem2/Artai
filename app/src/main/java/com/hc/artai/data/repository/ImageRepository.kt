package com.hc.artai.data.repository

import com.hc.artai.core.common.Resource
import com.hc.artai.data.model.output.OutputResponse


interface ImageRepository {

    suspend fun getUrlForGeneration(prompt: String, style: String, width: Int, height: Int): Resource<OutputResponse>
    suspend fun getResultImage(url: String): Resource<String>

}