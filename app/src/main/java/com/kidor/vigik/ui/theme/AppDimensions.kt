package com.kidor.vigik.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidor.vigik.utils.ExcludedFromKoverReport

/**
 * Common dimension metrics of the application.
 */
@ExcludedFromKoverReport
@Suppress("UndocumentedPublicProperty")
data class AppDimensions(
    val commonSpaceXXSmall: Dp = 4.dp,
    val commonSpaceXSmall: Dp = 8.dp,
    val commonSpaceSmall: Dp = 12.dp,
    val commonSpaceMedium: Dp = 16.dp,
    val commonSpaceLarge: Dp = 24.dp,
    val commonSpaceXLarge: Dp = 32.dp,
    val commonSpaceXXLarge: Dp = 48.dp,
    val textSizeXXSmall: TextUnit = 10.sp,
    val textSizeXSmall: TextUnit = 12.sp,
    val textSizeSmall: TextUnit = 14.sp,
    val textSizeMedium: TextUnit = 16.sp,
    val textSizeLarge: TextUnit = 20.sp,
    val textSizeXLarge: TextUnit = 24.sp,
    val recyclerItemMinimumHeight: Dp = 40.dp,
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }
