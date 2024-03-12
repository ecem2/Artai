package com.adentech.artai.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adentech.artai.R
import com.adentech.artai.core.recyclerview.ArtListAdapter
import com.adentech.artai.data.model.SizeModel


class  SizeAdapter: ArtListAdapter<SizeModel>(
    itemsSame = { old, new -> old == new },
    contentsSame = { old, new -> old == new }
) {

    private var selectedPosition: Int = 0
    private var itemSelectedListener: OnItemSelectedListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return SizeViewHolder(parent, inflater)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SizeViewHolder) {
            val item = getItem(position)
            holder.bind(item, position == selectedPosition)
            holder.itemView.setOnClickListener {
                if (selectedPosition != position) {
                    val previouslySelectedPosition = selectedPosition
                    selectedPosition = position
                    updateItemsSelection(item)
                    notifyItemChanged(previouslySelectedPosition)
                    notifyItemChanged(position)
                    itemSelectedListener?.onItemSelected(item)
                } else {
                    return@setOnClickListener
                }
                holder.binding.apply {
                    if (item.isSelected) {
                        clSize.setBackgroundResource(R.drawable.bg_size)
                        tvItemText.setTextColor(ContextCompat.getColor(tvItemText.context, R.color.white))
                    } else {
                        clSize.setBackgroundResource(R.drawable.bg_selected_size)
                        tvItemText.setTextColor(ContextCompat.getColor(tvItemText.context, R.color.purple))
                    }
                }

                notifyItemChanged(position)
            }
        }
    }

    private fun updateItemsSelection(clickedItem: SizeModel) {
        for (i in 0 until itemCount) {
            val item = getItem(i)
            item.isSelected = item == clickedItem
        }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.itemSelectedListener = listener
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: SizeModel)
    }
}