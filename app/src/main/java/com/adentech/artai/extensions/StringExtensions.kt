package com.adentech.artai.extensions

import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.adentech.artai.core.common.Util

/**
 * @param charactersToRemove The characters to remove from the receiving String.
 * @return A copy of the receiving String with the characters in `charactersToRemove` removed.
 */
fun String.removeAll(charactersToRemove: Set<Char>): String {
    return filterNot { charactersToRemove.contains(it) }
}

fun String.removeWhitespaces(): String {
    return replace(Util.SPACE, Util.EMPTY_STRING)
}

fun String.removeSlash(): String {
    return replace(Util.SLASH, Util.EMPTY_STRING)
}

fun String.removeLeftParenthesis(): String {
    return replace(Util.LEFT_PARENTHESIS, Util.EMPTY_STRING)
}

fun String.removeRightParenthesis(): String {
    return replace(Util.RIGHT_PARENTHESIS, Util.EMPTY_STRING)
}

fun String.removeDash(): String {
    return replace(Util.DASH, Util.EMPTY_STRING)
}

fun String.removePoint(): String {
    return replace(Util.POINT, Util.EMPTY_STRING)
}

fun String.removeComma(): String {
    return replace(Util.COMMA, Util.EMPTY_STRING)
}

fun String.fromHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String.toDoubleOrZero(): Double {
    return if (this.isEmpty()) {
        return Util.ZERO_DOUBLE
    } else {
        this.toDouble()
    }
}

fun String.removeAlphaNumeric(): String {
    return this.replace(Util.ALPHA_NUMERIC_REGEX, Util.EMPTY_STRING)
}

fun String.getFirstAndSecond() = mapOf("first" to this.first(), "second" to this[1])
