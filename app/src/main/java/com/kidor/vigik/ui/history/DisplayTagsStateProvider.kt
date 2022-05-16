package com.kidor.vigik.ui.history

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kidor.vigik.nfc.model.Tag
import java.util.Calendar

/**
 * Provides a set of data for the preview of DisplayTagsState Composable.
 */
class DisplayTagsStateProvider : PreviewParameterProvider<DisplayTagsStateData> {
    private val defaultTag = Tag(
        timestamp = Calendar.getInstance().timeInMillis,
        uid = byteArrayOf(0x4E, 0x57, 0x86.toByte(), 0xAC.toByte()),
        techList = "TAG: Tech [android.nfc.tech.NfcA, android.nfc.tech.MifareClassic, android.nfc.tech.NdefFormatable]",
        data = "No NDEF messages",
        id = byteArrayOf(0x4E, 0x57, 0x86.toByte(), 0xAC.toByte())
    )

    override val values: Sequence<DisplayTagsStateData> = sequenceOf(
        DisplayTagsStateData(emptyList()),
        DisplayTagsStateData(listOf(Tag())),
        DisplayTagsStateData(listOf(defaultTag, defaultTag, defaultTag, defaultTag, defaultTag))
    )
}

/**
 * Data used for the preview of DisplayTagsState Composable.
 *
 * @param tags         The list of tags to display.
 * @param onViewAction The handler for view's actions.
 */
data class DisplayTagsStateData(val tags: List<Tag>, val onViewAction: (HistoryViewAction) -> Unit = {})