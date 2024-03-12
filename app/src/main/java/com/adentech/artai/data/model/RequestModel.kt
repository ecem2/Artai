package com.adentech.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestModel(
    val prompt: String,
    val size: String,
    val model: String,
    val art: String
) : Parcelable