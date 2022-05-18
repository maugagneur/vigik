package com.kidor.vigik.ui.compose

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Theme of the application.
 *
 * Contains the current theme's set of colors (depends if it is light or dark) and dimensions.
 */
object AppTheme {
    private var isDarkTheme: Boolean = false

    fun invertTheme() {
        isDarkTheme = !isDarkTheme
    }

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = if (isDarkTheme) LocalDarkColors.current else LocalLightColors.current

    val dimensions: AppDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = AppTheme.colors,
        content = content
    )
}
