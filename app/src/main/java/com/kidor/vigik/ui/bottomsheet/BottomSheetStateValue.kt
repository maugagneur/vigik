package com.kidor.vigik.ui.bottomsheet

/**
 * Available states of the custom bottom sheet.
 *
 * @param draggableFraction Represents how much of the draggable component will be visible.
 */
@Suppress("MagicNumber")
enum class BottomSheetStateValue(val draggableFraction: Float) {
    HIDDEN(0f),
    HALF_EXPANDED(0.5f),
    EXPANDED(1f)
}
