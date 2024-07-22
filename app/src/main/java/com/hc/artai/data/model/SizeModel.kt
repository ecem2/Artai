package com.hc.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SizeModel(
    val icon: String? = null,
    var width: Int,
    var height: Int,
    var isSelected: Boolean = false
) : Parcelable