package com.adentech.artai.data.model

import com.google.gson.annotations.SerializedName

data class EditResponse(
    @SerializedName("created")
    var created: Int? = null,
    @SerializedName("data")
    var data: ArrayList<DataModel> = arrayListOf(),
)