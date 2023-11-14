package com.kidor.vigik.extensions

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult

/**
 * Returns the list of [Rect] of each character in given range. For multiline text, returns the number of boxes based
 * on the lines.
 *
 * @param start Index of the first char.
 * @param end   Index of the last char.
 */
fun TextLayoutResult.getBoundingBoxesForRange(start: Int, end: Int): List<Rect> {
    var prevRect: Rect? = null
    var firstLineCharRect: Rect? = null
    val boundingBoxes = mutableListOf<Rect>()

    for (charIndex in start..end) {
        val rect = getBoundingBox(charIndex)
        val isLastRect = charIndex == end

        // Single char case
        if (isLastRect && firstLineCharRect == null) {
            firstLineCharRect = rect
            prevRect = rect
        }

        if (firstLineCharRect == null) {
            firstLineCharRect = rect
        } else if (prevRect != null) {
            if (prevRect.bottom != rect.bottom) {
                boundingBoxes.add(firstLineCharRect.copy(right = prevRect.right))
                firstLineCharRect = rect
            } else if (isLastRect) {
                boundingBoxes.add(firstLineCharRect.copy(right = rect.right))
            }
        }
        prevRect = rect
    }

    return boundingBoxes
}
