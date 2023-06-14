package com.kidor.vigik.extensions

import com.kidor.vigik.data.tag.model.RoomTag
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Unit tests for mapper between [Tag] and [RoomTag].
 */
@RunWith(Parameterized::class)
class TagMapperTest(private val data: TagMapperDataSet) {

    @Test
    fun convertTagToRoomTag() {
        logTestName()

        assertEquals(data.roomTag, data.tag.toRoomTag(), "Tag to RoomTag conversion")
    }

    @Test
    fun convertRoomTagToTag() {
        logTestName()

        assertEquals(data.tag, data.roomTag.toTag(), "RoomTag to Tag conversion")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            TagMapperDataSet(Tag(), RoomTag()),
            TagMapperDataSet(
                Tag(
                    123456,
                    byteArrayOf(0x42, 0x13, 0x37, 0xFF.toByte()),
                    "TAG: Tech [NfcA, MifareClassic, NdefFormatable]",
                    "Fake NDEF description",
                    byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
                ),
                RoomTag(
                    123456,
                    byteArrayOf(0x42, 0x13, 0x37, 0xFF.toByte()),
                    "TAG: Tech [NfcA, MifareClassic, NdefFormatable]",
                    "Fake NDEF description",
                    byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
                )
            )
        )
    }
}
