package com.kidor.vigik.ui.biometric

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provides a set of data for the preview of LoggedState Composable.
 */
class LoggedStateProvider : PreviewParameterProvider<LoggedStateData> {
    override val values: Sequence<LoggedStateData> = sequenceOf(LoggedStateData())
}

/**
 * Data used for the preview of LoggedState Composable.
 *
 * @param onLogoutClick The function called on logout.
 */
data class LoggedStateData(val onLogoutClick: () -> Unit = {})
