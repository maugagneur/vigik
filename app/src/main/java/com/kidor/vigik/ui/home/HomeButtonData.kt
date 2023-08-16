package com.kidor.vigik.ui.home

import androidx.annotation.StringRes

/**
 * Data of buttons from home screen.
 *
 * @param textId  Button's text to be displayed.
 * @param onClick Action called when the button is clicked.
 */
data class HomeButtonData(@StringRes val textId: Int, val onClick: () -> Unit)
