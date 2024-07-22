package com.hc.artai.ui.generate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hc.artai.core.adapters.ArtBaseViewHolder
import com.hc.artai.data.model.GenerateModel
import com.hc.artai.databinding.ItemGenerateBinding
import com.hc.artai.extensions.executeAfter

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