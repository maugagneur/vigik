package com.kidor.vigik.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class RoomTag(
    @PrimaryKey val timestamp: Long,
    val uid: ByteArray,
    val techList: String,
    val data: String,
    val id: ByteArray
)