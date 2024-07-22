package com.hc.artai.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hc.artai.R
import com.hc.artai.core.common.Resource
import com.hc.artai.core.viewmodel.BaseViewModel
import com.hc.artai.data.model.ArtStyleModel
import com.hc.artai.data.model.output.OutputResponse
import com.hc.artai.data.preferences.Preferences
import com.hc.artai.data.repository.ImageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val preferences: Preferences,
    val repository: ImageRepositoryImpl,
) : BaseViewModel() {

    val artList: ArrayList<ArtStyleModel> = ArrayList()


    private val _urlForGeneration: MutableLiveData<Resource<OutputResponse>> = MutableLiveData()
    val urlForGeneration: LiveData<Resource<OutputResponse>> get() = _urlForGeneration

    private val _resultImage: MutableLiveData<Resource<String>> = MutableLiveData()
    val resultImage: LiveData<Resource<String>> get() = _resultImage


    fun getUrlForGeneration(prompt: String, style: String, width: Int, height: Int) {
        _urlForGeneration.postValue(Resource.loading(null))
        viewModelScope.launch {
            val request = repository.getUrlForGeneration(prompt, style, width, height)
            Log.d("fatosss", "API Response: ${request.data}")
            Log.d("fatosss", "URL for generation request: $request")
            _urlForGeneration.postValue(request)
        }
    }

    fun getResultImage(url: String) {
        _resultImage.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.getResultImage(url)
            _resultImage.postValue(response)
        }
    }


    init {
        getArtList()
    }

    private fun getArtList() {
        artList.add(
            ArtStyleModel(
                name = "Hyper realistic",
                filter = R.drawable.realistic,
                isSelected = true
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Photographic",
                filter = R.drawable.photografic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "3D Rendering",
                filter = R.drawable.rendering,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Neon",
                filter = R.drawable.neon,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Futuristic",
                filter = R.drawable.futuristic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Cyberpunk",
                filter = R.drawable.cyberpunk,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Colorful",
                filter = R.drawable.colorful,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Anime",
                filter = R.drawable.anime,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Cartoon",
                filter = R.drawable.cartoon,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Pop Art",
                filter = R.drawable.popart,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Oil Painting",
                filter = R.drawable.oil,
                isSelected = false
            )
        )

        artList.add(
            ArtStyleModel(
                name = "Mosaic",
                filter = R.drawable.mosaic,
                isSelected = false
            )
        )
    }


    suspend fun downloadImage(imageUrl: String, context: Context): File? {
        return withContext(Dispatchers.IO) {
            try {
                val urlConnection: HttpURLConnection =
                    URL(imageUrl).openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                val inputStream = BufferedInputStream(urlConnection.inputStream)
                val outputFile = File(context.cacheDir, "shared_image.png")
                val outputStream = FileOutputStream(outputFile)

                val data = ByteArray(1024)
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    outputStream.write(data, 0, count)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                outputFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}