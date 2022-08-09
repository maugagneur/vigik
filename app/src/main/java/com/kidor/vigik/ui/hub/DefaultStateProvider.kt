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
 * @param onViewAction The handler for view's actions.
 */
data class DefaultStateData(val onViewAction: (HubViewAction) -> Unit = {})