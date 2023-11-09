package com.kidor.vigik.ui.animations

import androidx.annotation.StringRes
import com.kidor.vigik.ui.base.AbsButtonData

/**
 * Data of buttons from home screen.
 *
 * @param textId  Button's text to be displayed.
 * @param onClick Action called when the button is clicked.
 */
data class AnimationButtonData(
    @StringRes override val textId: Int,
    override val onClick: () -> Unit
) : AbsButtonData()
