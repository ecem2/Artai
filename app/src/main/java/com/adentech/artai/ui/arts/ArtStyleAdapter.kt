package com.adentech.artai.ui.arts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.R
import com.adentech.artai.core.recyclerview.ArtListAdapter
import com.adentech.artai.data.model.ArtStyleModel
import com.adentech.artai.databinding.ItemArtStyleBinding


class ArtStyleAdapter(
    private val context: Context,
    var itemClickListener: ItemClickListener,
) : ArtListAdapter<ArtStyleModel>(
    itemsSame = { old, new -> old == new },
    contentsSame = { old, new -> old == new }
) {

    var selectedItemPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val binding: ItemArtStyleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_art_style,
            parent,
            false
        )
        return ArtStyleViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtStyleViewHolder) {
            val item = getItem(position)
            holder.bind(item)

            holder.itemView.setOnClickListener {
                if (selectedItemPosition != position) {
                    notifyItemChanged(selectedItemPosition)
                    selectedItemPosition = position
                    notifyItemChanged(selectedItemPosition)
                    itemClickListener.onItemClick(item)
                }
            }
        }
    }

    inner class ArtStyleViewHolder(private val binding: ItemArtStyleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArtStyleModel) {
            binding.item = item
            if (adapterPosition == selectedItemPosition) {
                binding.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.llSelectedBg.visibility = View.VISIBLE
            } else {
                binding.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.llSelectedBg.visibility = View.GONE
            }

            binding.executePendingBindings()
        }
    }


    interface ItemClickListener {
        fun onItemClick(item: ArtStyleModel)
    }
}
