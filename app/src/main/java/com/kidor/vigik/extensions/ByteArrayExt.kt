package com.kidor.vigik.extensions

/**
 * Checks if this bytes array starts with a specified sequence of bytes.
 *
 * @param prefix The prefix bytes to test for
 * @return `true` if the array starts with the bytes from the prefix, otherwise `false`.
 */
@Suppress("ReturnCount")
fun ByteArray.startWith(prefix: ByteArray?): Boolean {
    if (this.contentEquals(prefix)) {
        return true
    }

    if (prefix == null || prefix.isEmpty() || (prefix.size > this.size)) {
        return false
    }

    prefix.forEachIndexed { index, byte ->
        if (this[index] != byte) {
            return false
        }
    }

    return true
}

/**
 * Converts a bytes array into human readable [String].
 *
 * @return The converted value.
 */
fun ByteArray.toHex(): String =
    joinToString(separator = " ") { eachByte -> "%02x".format(eachByte).uppercase() }
