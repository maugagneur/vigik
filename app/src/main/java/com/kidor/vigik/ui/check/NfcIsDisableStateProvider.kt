package com.kidor.vigik.ui.check

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provides a set of data for the preview of NfcIsDisableState Composable.
 */
class NfcIsDisableStateProvider : PreviewParameterProvider<NfcIsDisableStateData> {
    override val values: Sequence<NfcIsDisableStateData> = sequenceOf(
        NfcIsDisableStateData()
    )
}

/**
 * Data used for the preview of NfcIsDisableState Composable.
 *
 * @param onViewAction The handler for view's actions.
 */
data class NfcIsDisableStateData(val onViewAction: (CheckViewAction) -> Unit = {})