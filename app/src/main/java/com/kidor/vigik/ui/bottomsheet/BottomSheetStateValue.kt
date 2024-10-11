package com.kidor.vigik.ui.bottomsheet

/**
 * Available states of the custom bottom sheet.
 *
 * @param draggableFraction Represents how much of the draggable component will be visible.
 */
@Suppress("MagicNumber")
enum class BottomSheetStateValue(val draggableFraction: Float) {
    /** Hidden state. **/
    HIDDEN(0f),
    /** Half expended state. **/
    HALF_EXPANDED(0.5f),
    /** Expanded state. **/
    EXPANDED(1f)
}
