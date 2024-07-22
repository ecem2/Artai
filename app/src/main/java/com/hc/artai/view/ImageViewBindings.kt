package com.hc.artai.view

import androidx.databinding.BindingAdapter
import com.hc.artai.core.common.ImageManager
import com.makeramen.roundedimageview.RoundedImageView


@BindingAdapter("imageUrl")
fun RoundedImageView.setImageUrl(url: Int) {
    ImageManager.setImageUrl(url, this)
}