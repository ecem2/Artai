package com.adentech.artai.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.adentech.artai.R
import com.adentech.artai.core.adapters.ArtBaseViewHolder
import com.adentech.artai.data.model.SizeModel
import com.adentech.artai.databinding.ItemSizeBinding
import com.adentech.artai.extensions.executeAfter


class SizeViewHolder(
    parent: ViewGroup, inflater: LayoutInflater
) : ArtBaseViewHolder<ItemSizeBinding>(
    binding = ItemSizeBinding.inflate(inflater, parent, false)
) {
    fun bind(item: SizeModel, isSelected: Boolean) {
        binding.executeAfter {
            this.item = item
            if (isSelected) {
                clSize.setBackgroundResource(R.drawable.bg_size)
                tvItemText.setTextColor(ContextCompat.getColor(tvItemText.context, R.color.white))
            } else {
                clSize.setBackgroundResource(R.drawable.bg_selected_size)
                tvItemText.setTextColor(ContextCompat.getColor(tvItemText.context, R.color.purple))
            }
        }
    }
}