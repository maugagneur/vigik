package com.kidor.vigik.data.nfc.hostapdu

/**
 * Pairs of status bytes returned after a command according to ISO 7816-4.
 *
 * @param value The value of the 2 status bytes.
 */
enum class ApduStatusBytes(val value: ByteArray) {
    /** 0x0000 **/
    UNKNOWN_COMMAND(byteArrayOf(0x00, 0x00)),
    /** 0x6D00 **/
    INSTRUCTION_NOT_SUPPORTED(byteArrayOf(0x6D, 0x00)),
    /** 0x9000 **/
    COMMAND_CORRECT(byteArrayOf(0x90.toByte(), 0x00))
}
