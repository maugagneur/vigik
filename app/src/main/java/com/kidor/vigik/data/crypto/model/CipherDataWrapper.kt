package com.kidor.vigik.data.crypto.model

/**
 * Wrapper to manipulate an encrypted data and its related initialization vector.
 *
 * @param data                 The encrypted data.
 * @param initializationVector The initialization vector.
 */
data class CipherDataWrapper(val data: ByteArray, val initializationVector: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CipherDataWrapper

        if (!data.contentEquals(other.data)) return false
        if (!initializationVector.contentEquals(other.initializationVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + initializationVector.contentHashCode()
        return result
    }
}
