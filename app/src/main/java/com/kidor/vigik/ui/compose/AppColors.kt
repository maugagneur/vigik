package com.kidor.vigik.ui.compose

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private const val COLOR_AMBER = 0xFFFFC107
private const val COLOR_AMBER_DARK = 0xFFFFA000
private const val COLOR_AMBER_BROWN = 0xFF5c4300
private const val COLOR_LIGHT_BLUE = 0xFF03A9F4
private const val COLOR_PALE_BLUE = 0xFFc8e6ff
private const val COLOR_RED_DARK = 0xFFB3261E
private const val COLOR_RED_LIGHT = 0xFFF2B8B5
private const val COLOR_BLACK = 0xFF000000
private const val COLOR_WHITE = 0xFFFFFFFF

internal val LocalLightColors = staticCompositionLocalOf { lightMaterialColors() }

internal val LocalDarkColors = staticCompositionLocalOf { darkMaterialColors() }

private fun lightMaterialColors(): Colors = lightColors(
    primary = Color(COLOR_AMBER),
    primaryVariant = Color(COLOR_AMBER_DARK),
    onPrimary = Color(COLOR_WHITE),
    secondary = Color(COLOR_LIGHT_BLUE),
    secondaryVariant = Color(COLOR_PALE_BLUE),
    onSecondary = Color(COLOR_WHITE),
    error = Color(COLOR_RED_DARK),
    onError = Color(COLOR_WHITE)
)

private fun darkMaterialColors(): Colors = darkColors(
    primary = Color(COLOR_AMBER_DARK),
    primaryVariant = Color(COLOR_AMBER_BROWN),
    onPrimary = Color(COLOR_BLACK),
    secondary = Color(COLOR_LIGHT_BLUE),
    secondaryVariant = Color(COLOR_LIGHT_BLUE),
    onSecondary = Color(COLOR_BLACK),
    error = Color(COLOR_RED_LIGHT),
    onError = Color(COLOR_BLACK)
)
