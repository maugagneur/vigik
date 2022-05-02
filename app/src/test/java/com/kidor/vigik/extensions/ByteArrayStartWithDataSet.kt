package com.kidor.vigik.extensions

data class ByteArrayStartWithDataSet(val array: ByteArray, val prefix: ByteArray?, val isArrayStartsWithPrefix: Boolean) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayStartWithDataSet

        if (!array.contentEquals(other.array)) return false
        if (!prefix.contentEquals(other.prefix)) return false
        if (isArrayStartsWithPrefix != other.isArrayStartsWithPrefix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = array.contentHashCode()
        result = 31 * result + prefix.contentHashCode()
        result = 17 * result + isArrayStartsWithPrefix.hashCode()
        return result
    }
}
