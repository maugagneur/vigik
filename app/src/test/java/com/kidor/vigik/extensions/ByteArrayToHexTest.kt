package com.kidor.vigik.extensions

import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Unit tests for [ByteArray.toHex] extension.
 */
@RunWith(Parameterized::class)
class ByteArrayToHexTest(private val data: ByteArrayToHexDataSet) {

    @Test
    fun toHexTest() {
        logTestName()

        assertEquals(data.hexValue, data.bytes.toHex(), "Bytes array to HEX string conversion")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            ByteArrayToHexDataSet(byteArrayOf(), ""),
            ByteArrayToHexDataSet(byteArrayOf(0x42), "42"),
            ByteArrayToHexDataSet(byteArrayOf(0x13.toByte(), 0x37.toByte()), "13 37"),
            ByteArrayToHexDataSet(byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte()), "01 23 45 67 89 AB CD EF"),
            ByteArrayToHexDataSet(byteArrayOf(0x1), "01"),
            ByteArrayToHexDataSet(byteArrayOf(0x1337.toByte()), "37"),
            ByteArrayToHexDataSet(byteArrayOf(0x2A6.toByte()), "A6")
        )
    }
}