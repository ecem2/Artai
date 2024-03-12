package com.adentech.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtStyleModel(
    val name: String,
    val filter: Int,
    var isSelected: Boolean
) : Parcelable