package com.kidor.vigik.ui.base

/**
 * Common data structure for buttons.
 */
interface AbsButtonData {
    /**
     * Button's text to be displayed.
     */
    val textId: Int

    /**
     * Action called when the button is clicked.
     */
    val onClick: () -> Unit
}
