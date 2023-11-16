package com.kidor.vigik.ui.bluetooth

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kidor.vigik.utils.ExcludedFromKoverReport

/**
 * Provides a set of data for the preview of the low energy switch status.
 */
@ExcludedFromKoverReport
class LowEnergyStatusDataProvider : PreviewParameterProvider<LowEnergyStatusData> {
    override val values: Sequence<LowEnergyStatusData> = sequenceOf(
        LowEnergyStatusData(checked = false, enabled = false),
        LowEnergyStatusData(checked = false, enabled = true),
        LowEnergyStatusData(checked = true, enabled = false),
        LowEnergyStatusData(checked = true, enabled = true)
    )
}

/**
 * Data used for the preview of the low energy switch status.
 *
 * @param checked True if the switch should be checked, otherwise false.
 * @param enabled True if the switch should be enabled, otherwise false.
 */
@ExcludedFromKoverReport
data class LowEnergyStatusData(val checked: Boolean, val enabled: Boolean)
