package com.kidor.vigik.ui.hub

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provides a set of data for the preview of DefaultState Composable.
 */
class DefaultStateProvider : PreviewParameterProvider<DefaultStateData> {
    override val values: Sequence<DefaultStateData> = sequenceOf(
        DefaultStateData()
    )
}

/**
 * Data used for the preview of DefaultState Composable.
 *
 * @param onScanClick    The function called on a scan click.
 * @param onHistoryClick The function called on an history click.
 * @param onEmulateClick The function called on an emulate click.
 */
data class DefaultStateData(
    val onScanClick: () -> Unit = {},
    val onHistoryClick: () -> Unit = {},
    val onEmulateClick: () -> Unit = {}
)
