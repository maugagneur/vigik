package com.kidor.vigik.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class RoomTag(
    @PrimaryKey val timestamp: Long = 0,
    val uid: ByteArray? = null,
    val techList: String? = null,
    val data: String? = null,
    val id: ByteArray? = null
) {
    @SuppressWarnings("kotlin:S3776") // Ignore cognitive complexity warning for this method
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomTag

        if (timestamp != other.timestamp) return false
        if (uid != null) {
            if (other.uid == null) return false
            if (!uid.contentEquals(other.uid)) return false
        } else if (other.uid != null) return false
        if (techList != other.techList) return false
        if (data != other.data) return false
        if (id != null) {
            if (other.id == null) return false
            if (!id.contentEquals(other.id)) return false
        } else if (other.id != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 13 * result + (uid?.contentHashCode() ?: 0)
        result = 17 * result + (techList?.hashCode() ?: 0)
        result = 23 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (id?.contentHashCode() ?: 0)
        return result
    }
}
