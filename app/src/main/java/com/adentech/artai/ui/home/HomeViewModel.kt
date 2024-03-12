package com.adentech.artai.ui.home

import com.adentech.artai.R
import com.adentech.artai.core.viewmodel.BaseViewModel
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.data.model.GenerateModel
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.data.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val preferences: Preferences
) : BaseViewModel() {

    val itemList: ArrayList<SizeModel> = ArrayList()
    val artList: ArrayList<ArtStyleModel> = ArrayList()
    val imageList: ArrayList<GenerateModel> = ArrayList()

    init {
       // getSizeList()
        getArtList()
       // getAnimationList()
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