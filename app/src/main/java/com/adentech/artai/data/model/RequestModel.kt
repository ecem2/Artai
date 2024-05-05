package com.adentech.artai.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestModel(
    val prompt: String,
    val style: String,
    val size: String
) : Parcelable