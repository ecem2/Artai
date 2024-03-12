package com.adentech.artai.ui.generate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.adentech.artai.core.adapters.ArtBaseViewHolder
import com.adentech.artai.data.model.GenerateModel
import com.adentech.artai.databinding.ItemGenerateBinding
import com.adentech.artai.extensions.executeAfter

class GenerateViewHolder(
    parent: ViewGroup, inflater: LayoutInflater
) : ArtBaseViewHolder<ItemGenerateBinding>(
    binding = ItemGenerateBinding.inflate(inflater, parent, false)
) {

    fun bind(
        item: GenerateModel,
        onItemClicked: ((item: GenerateModel, position: Int) -> Unit)? = null
    ) {
        binding.executeAfter {
            this.item = item
            root.setOnClickListener {
                onItemClicked?.invoke(item, adapterPosition)
            }
        }
    }
}