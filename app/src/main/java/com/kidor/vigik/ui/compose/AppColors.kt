package com.kidor.vigik.ui.compose

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private const val COLOR_AMBER = 0xFFFFC107
private const val COLOR_AMBER_DARK = 0xFFFFA000
private const val COLOR_LIGHT_BLUE = 0xFF03A9F4
private const val COLOR_BLACK = 0xFF000000
private const val COLOR_WHITE = 0xFFFFFFFF

internal val LocalLightColors = staticCompositionLocalOf {
    lightMaterialColors()
}

internal val LocalDarkColors = staticCompositionLocalOf {
    darkMaterialColors()
}

private fun lightMaterialColors(): Colors = lightColors(
    primary = Color(COLOR_AMBER),
    primaryVariant = Color(COLOR_AMBER_DARK),
    onPrimary = Color(COLOR_WHITE),
    secondary = Color(COLOR_LIGHT_BLUE),
    secondaryVariant = Color(COLOR_LIGHT_BLUE),
    onSecondary = Color(COLOR_WHITE)
)

private fun darkMaterialColors(): Colors = darkColors(
    primary = Color(COLOR_AMBER),
    primaryVariant = Color(COLOR_AMBER_DARK),
    onPrimary = Color(COLOR_BLACK),
    secondary = Color(COLOR_LIGHT_BLUE),
    secondaryVariant = Color(COLOR_LIGHT_BLUE),
    onSecondary = Color(COLOR_BLACK)
)
