package com.adentech.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SizeModel(
    val icon: String? = null,
    val size: String? = null,
    var isSelected: Boolean = false
) : Parcelable