package com.kidor.vigik.ui.base

/**
 * Common data structure for buttons.
 */
abstract class AbsButtonData {
    /**
     * Button's text to be displayed.
     */
    abstract val textId: Int

    /**
     * Action called when the button is clicked.
     */
    abstract val onClick: () -> Unit
}
