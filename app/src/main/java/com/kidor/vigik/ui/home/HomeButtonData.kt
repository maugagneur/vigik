package com.kidor.vigik.ui.home

import androidx.annotation.StringRes
import com.kidor.vigik.ui.base.AbsButtonData
import com.kidor.vigik.utils.ExcludedFromKoverReport

/**
 * Data of buttons from home screen.
 *
 * @param textId  Button's text to be displayed.
 * @param onClick Action called when the button is clicked.
 */
@ExcludedFromKoverReport
data class HomeButtonData(
    @StringRes override val textId: Int,
    override val onClick: () -> Unit
) : AbsButtonData
