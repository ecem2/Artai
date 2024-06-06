package com.adentech.artai.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adentech.artai.data.model.ArtStyleModel

class ArtStyleViewModel : ViewModel() {

    private val _selectedArtStyle = MutableLiveData<ArtStyleModel?>()
    val selectedArtStyle: LiveData<ArtStyleModel?> = _selectedArtStyle

    fun setSelectedArtStyle(artStyle: ArtStyleModel) {
        _selectedArtStyle.value = artStyle
    }

    fun clearSelectedArtStyle() {
        _selectedArtStyle.value = null
    }
}