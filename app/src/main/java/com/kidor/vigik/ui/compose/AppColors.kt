package com.kidor.vigik.ui.compose

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private const val COLOR_AMBER = 0xFFFFC107
private const val COLOR_AMBER_DARK = 0xFFFFA000
private const val COLOR_LIGHT_BLUE = 0xFF03A9F4
private const val COLOR_DARK = 0xFF2C2C2C
private const val COLOR_RED_DARK = 0xFFB3261E
private const val COLOR_RED_LIGHT = 0xFFF2B8B5
private const val COLOR_TRANSPARENT = 0x00000000
private const val COLOR_BLACK = 0xFF000000
private const val COLOR_WHITE = 0xFFFFFFFF

internal val LocalLightColors = staticCompositionLocalOf { lightMaterialColors() }

internal val LocalDarkColors = staticCompositionLocalOf { darkMaterialColors() }

private fun lightMaterialColors() = lightColorScheme(
    primary = Color(COLOR_AMBER),
    onPrimary = Color(COLOR_WHITE),
    secondary = Color(COLOR_LIGHT_BLUE),
    onSecondary = Color(COLOR_WHITE),
    surface = Color(COLOR_TRANSPARENT),
    error = Color(COLOR_RED_DARK),
    onError = Color(COLOR_WHITE)
)

private fun darkMaterialColors() = darkColorScheme(
    primary = Color(COLOR_AMBER_DARK),
    onPrimary = Color(COLOR_DARK),
    secondary = Color(COLOR_LIGHT_BLUE),
    onSecondary = Color(COLOR_DARK),
    surface = Color(COLOR_DARK),
    error = Color(COLOR_RED_LIGHT),
    onError = Color(COLOR_BLACK)
)
