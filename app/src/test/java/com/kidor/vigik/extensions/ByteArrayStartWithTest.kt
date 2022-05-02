package com.kidor.vigik.extensions

import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Unit test for [ByteArray.startWith] extension.
 */
@RunWith(Parameterized::class)
class ByteArrayStartWithTest(private val data: ByteArrayStartWithDataSet) {

    @Test
    fun startWithTest() {
        logTestName()

        assertEquals(
            data.isArrayStartsWithPrefix,
            data.array.startWith(data.prefix),
            "[${data.array.toHex()}] starts with [${data.prefix?.toHex()}]")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x42), true),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x42, 0x13), true),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x42, 0x13, 0x37), true),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x13), false),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x37), false),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x42, 0x13, 0x37, 0x00), false),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(), false),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), null, false),
            ByteArrayStartWithDataSet(byteArrayOf(0x42, 0x13, 0x37), byteArrayOf(0x00, 0x42), false),
        )
    }
}
