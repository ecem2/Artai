package com.adentech.artai.data.repository

import android.util.Log
import com.adentech.artai.core.common.Constants.VERSION_KEY
import com.adentech.artai.core.common.Resource
import com.adentech.artai.data.model.imagegeneration.GenerationRequest
import com.adentech.artai.data.model.imagegeneration.Input
import com.adentech.artai.data.model.output.OutputResponse
import com.adentech.artai.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject



class ImageRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): ImageRepository {

    override suspend fun getUrlForGeneration(
        prompt: String,
        style: String,
        size: String
    ): Resource<OutputResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val input = Input(
                width = 768,
                height = 768,
                prompt = "$prompt, hd, $style, $size",
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
            Log.d("fatosss", "c: ${response.raw().request.url}")
            Log.d("fatosss", "Raw: ${response.raw()}")
            Log.d("fatosss", "Request: ${response.raw().request}")
            Log.d("fatosss", "Response Code: ${response.code()}")
            Log.d("fatosss", "Response Body: ${response.body()}")
            if (response.isSuccessful) {
                response.body().let {
                    Resource.success(it)
                }
            } else {
                Log.e("fatosss", "API Error: ${response.message()}")
                Resource.error("Failed to fetch data. HTTP code: ${response.code()}", null)
            }
        } catch (e: Exception) {
            Log.e("fatosss", "Exception: ${e.localizedMessage}")
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
                            Log.d("fatosss", "Response: $response")
                            Log.d("fatosss", "ResponseBody: $responseBody")
                            Log.d("fatosss", "Status: $status")
                        }

                        if (status == "succeeded") {
                            outputImage = response.body()?.output?.get(0).toString()
                            Log.d("fatosss", "Output Image: $outputImage")
                            Resource.success(outputImage)
                        } else if (status == "failed") {
                            Log.e("fatosss", "Status failed")
                            Resource.error("STATUS FAILED", null)
                            break
                        }
                    } else {
                        Log.e("fatosss", "Failed to fetch data. HTTP code: ${response.code()}")
                        Resource.error("Failed to fetch data", null)
                        break
                    }
                    delay(3000)
                }
                Resource.success(outputImage)
            } catch (e: Exception) {
                Log.e("fatosss", "Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
                Resource.error(e.localizedMessage?.toString() ?: "Exception occurred", null)
            }
        }
    }


//    override suspend fun getResultImage(url: String): Resource<ArrayList<String>> =
//        withContext(Dispatchers.IO) {
//            val imageList = ArrayList<String>()
//            var status = "processing"
//            return@withContext try {
//                while (status != "succeeded") {
//                    val response = apiService.getGeneratedList(url)
//                    if (response.isSuccessful) {
//                        response.body().let { responseBody ->
//                            status = responseBody?.status.toString()
//                            Log.d("ecooo", "Status: $status")
//                        }
//
//                        if (status == "succeeded") {
//                            imageList.clear()
//                            val outputArray = response.body()?.output
//                            Log.d("ecooo", "Output Array: $outputArray")
//                            if (outputArray != null) {
//                                for (i in outputArray.indices) {
//                                    val outputUrl = outputArray[i]
//                                    Log.d("ecooo", "Output Url: $outputUrl")
//                                    imageList.add(outputUrl)
//                                }
//                            }
//                            Resource.success(imageList)
//                        } else if (status == "failed") {
//                            Log.e("ecooo", "Status failed")
//                            Resource.error("STATUS FAILED", null)
//                            break
//                        }
//                    } else {
//                        if (response.code() == 401) {
//                            Log.e("ecooo", "Unauthorized: ${response.message()}")
//                            Resource.error("Unauthorized: ${response.message()}", null)
//                        } else {
//                            Log.e("ecooo", "Failed to fetch data. HTTP code: ${response.code()}")
//                            Resource.error("Failed to fetch data", null)
//                        }
//                        break
//                    }
//                    delay(3000)
//                }
//                Resource.success(imageList)
//            } catch (e: Exception) {
//                Log.e("ecooo", "Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
//                Resource.error(e.localizedMessage?.toString() ?: "Exception occurred", null)
//            }
//        }
