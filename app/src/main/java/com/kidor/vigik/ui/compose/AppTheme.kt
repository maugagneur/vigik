package com.kidor.vigik.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Utility class used to access to common app's dimensions.
 *
 * WIP: Should be able to switch the light/dark theme on the fly.
 */
object AppTheme {
    private var isDarkTheme: Boolean = false

    fun invertTheme() {
        isDarkTheme = !isDarkTheme
    }

    val dimensions: AppDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current
}

/**
 * Theme of the application based on [MaterialTheme].
 */
@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) LocalDarkColors.current else LocalLightColors.current,
        content = content
    )
}
