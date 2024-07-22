package com.hc.artai.core.recyclerview

import com.hc.artai.core.adapters.ArtBaseListAdapter


abstract class ArtListAdapter<T : Any>(
    itemsSame: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    contentsSame: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem -> oldItem == newItem }
) : ArtBaseListAdapter<T>(itemsSame, contentsSame)