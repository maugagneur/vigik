package com.kidor.vigik.ui.animations.circleloader

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Style used for tails of [CircleLoader].
 *
 * @param width      Stroke's width.
 * @param strokeCap  Styles to use for line endings.
 * @param glowRadius Glowing effect's radius.
 */
data class StrokeStyle(
    val width: Dp = 4.dp,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val glowRadius: Dp? = 4.dp
)
