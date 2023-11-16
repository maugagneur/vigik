package com.kidor.vigik.ui.nfc.check

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kidor.vigik.utils.ExcludedFromKoverReport

/**
 * Provides a set of data for the preview of NfcIsDisableState Composable.
 */
@ExcludedFromKoverReport
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
@ExcludedFromKoverReport
data class NfcIsDisableStateData(val onRefreshClick: () -> Unit = {}, val onSettingsClick: () -> Unit = {})
