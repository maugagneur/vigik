package com.kidor.vigik.data.nfc.model

import com.kidor.vigik.extensions.toHex
import com.kidor.vigik.ui.usecases.FormatDateUseCase

/**
 * Object model for tag in the application.
 *
 * @param timestamp The timestamp when the tag was read.
 * @param uid       The UID of the tag.
 * @param techList  The technologies available in this tag, as fully qualified class names.
 * @param data      The human-readable description of the tag.
 * @param id        The low level ID of the tag.
 */
data class Tag(
    val timestamp: Long? = null,
    val uid: ByteArray? = null,
    val techList: String? = null,
    val data: String? = null,
    val id: ByteArray? = null
) {
    @Suppress("kotlin:S3776") // Ignore cognitive complexity warning for this method
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (timestamp != other.timestamp) return false
        if (uid != null) {
            if (other.uid == null) return false
            if (!uid.contentEquals(other.uid)) return false
        } else if (other.uid != null) {
            return false
        }
        if (techList != other.techList) return false
        if (data != other.data) return false
        if (id != null) {
            if (other.id == null) return false
            if (!id.contentEquals(other.id)) return false
        } else if (other.id != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 13 * result + uid.hashCode()
        result = 17 * result + techList.hashCode()
        result = 23 * result + data.hashCode()
        result = 31 * result + id.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Timestamp -> ${timestamp?.let { FormatDateUseCase().invoke(it) }}" +
                "\nTag UID -> ${uid?.toHex()}" +
                "\nTag tech list -> $techList" +
                "\nTag data -> $data" +
                "\nTag ID -> ${id?.toHex()}"
    }
}
