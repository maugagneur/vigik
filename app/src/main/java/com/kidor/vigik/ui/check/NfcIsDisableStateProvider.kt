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
 * @param onRefreshClick  The function called on a refresh click.
 * @param onSettingsClick The function called on a settings click.
 */
data class NfcIsDisableStateData(val onRefreshClick: () -> Unit = {}, val onSettingsClick: () -> Unit = {})