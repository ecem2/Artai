package com.hc.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenerateModel(
    val image: String,
    val animation: Int
): Parcelable