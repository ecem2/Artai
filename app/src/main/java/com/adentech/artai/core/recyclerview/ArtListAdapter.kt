package com.adentech.artai.core.recyclerview

import com.adentech.artai.core.adapters.ArtBaseListAdapter


abstract class ArtListAdapter<T : Any>(
    itemsSame: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    contentsSame: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem -> oldItem == newItem }
) : ArtBaseListAdapter<T>(itemsSame, contentsSame)