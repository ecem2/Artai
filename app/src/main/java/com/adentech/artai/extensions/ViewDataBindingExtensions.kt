package com.adentech.artai.extensions

import androidx.annotation.DimenRes
import androidx.databinding.ViewDataBinding

fun <VB : ViewDataBinding> VB.executeAfter(block: VB.() -> Unit) {
    block.invoke(this)
    executePendingBindings()
}

fun <VB : ViewDataBinding> VB.dimenToPx(@DimenRes dimen: Int): Int {
    return root.dimenToPx(dimen)
}
