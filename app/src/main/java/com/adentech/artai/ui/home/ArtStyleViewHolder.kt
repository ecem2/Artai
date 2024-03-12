package com.adentech.artai.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.adentech.artai.R
import com.adentech.artai.core.adapters.ArtBaseViewHolder
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.databinding.ItemArtStyleBinding
import com.adentech.artai.extensions.executeAfter


class ArtStyleViewHolder(
    parent: ViewGroup, inflater: LayoutInflater
) : ArtBaseViewHolder<ItemArtStyleBinding>(
    binding = ItemArtStyleBinding.inflate(inflater, parent, false)
) {

    fun bind(item: ArtStyleModel, isSelected: Boolean) {
        binding.executeAfter {
            this.item = item
            if (isSelected) {
                tvItemName.setTextColor(ContextCompat.getColor(tvItemName.context, R.color.white))
                llSelectedBg.visibility = View.VISIBLE
            } else {
                tvItemName.setTextColor(ContextCompat.getColor(tvItemName.context, R.color.white))
                llSelectedBg.visibility = View.GONE
            }
        }
    }
}