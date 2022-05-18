package com.kidor.vigik.ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Common dimension metrics of the application.
 */
data class AppDimensions(
    val commonSpaceSmall: Dp = 8.dp,
    val commonSpaceMedium: Dp = 16.dp,
    val commonSpaceLarge: Dp = 24.dp,
    val commonSpaceXLarge: Dp = 32.dp,
    val textSizeMedium: TextUnit = 16.sp,
    val textSizeLarge: TextUnit = 20.sp,
    val textSizeXLarge: TextUnit = 24.sp,
    val recyclerItemMinimumHeight: Dp = 40.dp,
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }
