package com.adentech.artai.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adentech.artai.R
import com.adentech.artai.core.common.Resource
import com.adentech.artai.core.common.Status
import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.data.model.GenerateModel
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.data.model.imagegeneration.GenerationRequest
import com.adentech.artai.data.model.output.OutputResponse
import com.adentech.artai.data.preferences.Preferences
import com.adentech.artai.data.repository.ImageRepository
import com.adentech.artai.data.repository.ImageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val preferences: Preferences,
    val repository: ImageRepositoryImpl
) : BaseViewModel() {


    private val _urlForGeneration: MutableLiveData<Resource<OutputResponse>> = MutableLiveData()
    val urlForGeneration: LiveData<Resource<OutputResponse>> get() = _urlForGeneration

    private val _resultImage: MutableLiveData<Resource<String>> = MutableLiveData()
    val resultImage: LiveData<Resource<String>> get() = _resultImage




    fun getUrlForGeneration(prompt: String, style: String, size: String) {
        _urlForGeneration.postValue(Resource.loading(null))
        viewModelScope.launch {
            val request = repository.getUrlForGeneration(prompt, style, size)
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




    val artList: ArrayList<ArtStyleModel> = ArrayList()

    init {
        getArtList()
    }

    private fun getArtList() {
        artList.add(
            ArtStyleModel(
                name = "Hyper realistic",
                filter = R.drawable.img_digital_art,
                isSelected = true
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Photographic",
                filter = R.drawable.img_realistic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "3D Rendering",
                filter = R.mipmap.img_futuristic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Neon",
                filter = R.drawable.img_scolastic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Futuristic",
                filter = R.mipmap.img_futuristic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Cyberpunk",
                filter = R.drawable.img_scolastic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Colorful",
                filter = R.mipmap.img_futuristic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Anime",
                filter = R.drawable.img_scolastic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Cartoon",
                filter = R.drawable.img_scolastic,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Pop Art",
                filter = R.drawable.img_digital_art,
                isSelected = false
            )
        )
        artList.add(
            ArtStyleModel(
                name = "Oil Painting",
                filter = R.drawable.img_digital_art, // todo
                isSelected = false
            )
        )

        artList.add(
            ArtStyleModel(
                name = "Mosaic",
                filter = R.drawable.img_digital_art, // todo
                isSelected = false
            )
        )
    }

//    private fun getSizeList() {
//        itemList.add(SizeModel(icon = "", size = "1024x1024", isSelected = true))
//        itemList.add(SizeModel(icon = "", size = "1024x1792", isSelected = true))
//        itemList.add(SizeModel(icon = "", size = "1792x1024", isSelected = true))
//    }

//    private fun getAnimationList() {
//        imageList.add(GenerateModel(image = "", animation = R.raw.loading))
//        imageList.add(GenerateModel(image = "", animation = R.raw.loading))
//        imageList.add(GenerateModel(image = "", animation = R.raw.loading))
//        imageList.add(GenerateModel(image = "", animation = R.raw.loading))
//    }
}