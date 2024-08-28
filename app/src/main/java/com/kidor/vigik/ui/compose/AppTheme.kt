package com.kidor.vigik.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Access to application's dimension metrics.
 */
val MaterialTheme.dimensions: AppDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current

/**
 * Theme of the application based on [MaterialTheme].
 */
@Composable
fun AppTheme(
    inverseTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDarkTheme: Boolean = isSystemInDarkTheme()
    CompositionLocalProvider(
        LocalDimensions provides AppDimensions()
    ) {
        MaterialTheme(
            colorScheme = if (isDarkTheme xor inverseTheme) LocalDarkColors.current else LocalLightColors.current,
            content = content
        )
    }
}
