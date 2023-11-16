package com.kidor.vigik.ui.nfc.scan

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kidor.vigik.data.nfc.model.Tag
import com.kidor.vigik.utils.ExcludedFromKoverReport
import java.util.Calendar

/**
 * Provides a set of data for the preview of DisplayTagState Composable.
 */
@ExcludedFromKoverReport
class DisplayTagStateDataProvider : PreviewParameterProvider<DisplayTagStateData> {
    override val values: Sequence<DisplayTagStateData> = sequenceOf(
        DisplayTagStateData(ScanViewState.DisplayTag(Tag(), false)),
        DisplayTagStateData(
            ScanViewState.DisplayTag(
                Tag(
                    timestamp = Calendar.getInstance().timeInMillis,
                    uid = byteArrayOf(0x4E, 0x57, 0x86.toByte(), 0xAC.toByte()),
                    techList = "TAG: Tech [android.nfc.tech.NfcA, " +
                            "android.nfc.tech.MifareClassic, " +
                            "android.nfc.tech.NdefFormatable]",
                    data = "No NDEF messages",
                    id = byteArrayOf(0x4E, 0x57, 0x86.toByte(), 0xAC.toByte())
                ),
                true
            )
        )
    )
}

/**
 * Data used for the preview of DisplayTagState Composable.
 *
 * @param state          The view state to be displayed.
 * @param onSaveTagClick The function called on a save tag click.
 */
@ExcludedFromKoverReport
data class DisplayTagStateData(val state: ScanViewState.DisplayTag, val onSaveTagClick: () -> Unit = {})
