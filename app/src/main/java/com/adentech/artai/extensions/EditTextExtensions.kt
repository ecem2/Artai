package com.adentech.artai.extensions


import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

fun AppCompatEditText.multilineIme(action: Int) {
    imeOptions = action
    inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
    setHorizontallyScrolling(false)
    maxLines = Integer.MAX_VALUE
}

fun AppCompatEditText.multilineDone(callback: (() -> Unit)? = null) {
    val action = EditorInfo.IME_ACTION_DONE
    multilineIme(action)
    setOnEditorActionListener { _, actionId, _ ->
        if (action == actionId) {
            callback?.invoke()
            true
        }
        false
    }
}