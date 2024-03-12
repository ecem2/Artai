package com.adentech.artai.ui.generate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.core.recyclerview.ArtListAdapter
import com.adentech.artai.data.model.GenerateModel
import com.bumptech.glide.Glide


class GenerateAdapter(
    private val onItemClicked: ((item: GenerateModel, position: Int) -> Unit)? = null
) : ArtListAdapter<GenerateModel>(
    itemsSame = { old, new -> old == new },
    contentsSame = { old, new -> old == new }
) {

    var imagesLoaded = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return GenerateViewHolder(parent, inflater)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GenerateViewHolder) {
            val item = getItem(position)
            holder.bind(item, onItemClicked)

            Glide.with(holder.itemView.context).load(item.animation).into(holder.binding.ivArtImage)
            if (imagesLoaded) {
                holder.binding.imgAnimation.cancelAnimation()
                Glide.with(holder.itemView.context).load(item.image).into(holder.binding.ivArtImage)
            }
        }
    }
}