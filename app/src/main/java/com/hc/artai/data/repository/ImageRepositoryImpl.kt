package com.hc.artai.data.repository

import com.hc.artai.core.common.Constants.VERSION_KEY
import com.hc.artai.core.common.Resource
import com.hc.artai.data.model.imagegeneration.GenerationRequest
import com.hc.artai.data.model.imagegeneration.Input
import com.hc.artai.data.model.output.OutputResponse
import com.hc.artai.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ImageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : ImageRepository {

    override suspend fun getUrlForGeneration(
        prompt: String,
        style: String,
        width: Int,
        height: Int,
    ): Resource<OutputResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val input = Input(
                width = width,
                height = height,
                prompt = "$prompt, hd, $style",
                scheduler = "K_EULER",
                num_outputs = 1,
                guidance_scale = 7.5,
                num_inference_steps = 50
            )

            val jsonRequest = GenerationRequest(
                version = VERSION_KEY,
                input = input
            )

            val response = apiService.getRequiredUrl(jsonRequest)
            if (response.isSuccessful) {
                response.body().let {
                    Resource.success(it)
                }
            } else {
                Resource.error("Failed to fetch data. HTTP code: ${response.code()}", null)
            }
        } catch (e: Exception) {
            Resource.error(e.localizedMessage?.toString() ?: "Exception occurred", null)
        }
    }

    override suspend fun getResultImage(url: String): Resource<String> =
        withContext(Dispatchers.IO) {
            var outputImage = ""
            var status = "processing"
            return@withContext try {
                while (status != "succeeded") {
                    val response = apiService.getGeneratedList(url)
                    if (response.isSuccessful) {
                        response.body().let { responseBody ->
                            status = responseBody?.status.toString()
                        }

                        if (status == "succeeded") {
                            outputImage = response.body()?.output?.get(0).toString()
                            Resource.success(outputImage)
                        } else if (status == "failed") {
                            Resource.error("STATUS FAILED", null)
                            break
                        }
                    } else {
                        Resource.error("Failed to fetch data", null)
                        break
                    }
                    delay(3000)
                }
                Resource.success(outputImage)
            } catch (e: Exception) {
                Resource.error(e.localizedMessage?.toString() ?: "Exception occurred", null)
            }
        }
}


