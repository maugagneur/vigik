package com.kidor.vigik.extensions

data class ByteArrayToHexDataSet(val bytes: ByteArray, val hexValue: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayToHexDataSet

        if (!bytes.contentEquals(other.bytes)) return false
        if (hexValue != other.hexValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + hexValue.hashCode()
        return result
    }
}
