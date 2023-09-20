package com.kidor.vigik.ui.camera

import androidx.annotation.StringRes
import com.kidor.vigik.ui.base.AbsButtonData

/**
 * Data of buttons used in camera screen.
 *
 * @param textId  Button's text to be displayed.
 * @param onClick Action called when the button is clicked.
 */
data class CameraButtonData(
    @StringRes override val textId: Int,
    override val onClick: () -> Unit
) : AbsButtonData()
