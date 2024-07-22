package com.hc.artai.core.common

import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

object ImageManager {

    fun setImageUrl(url: Int, imageView: RoundedImageView) {
        Glide.with(imageView).load(url).into(imageView)
    }

}