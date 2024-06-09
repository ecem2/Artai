package com.adentech.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtStyleModel(
    val name: String? =null,
    val filter: Int? =null,
    var isSelected: Boolean? =null
) : Parcelable