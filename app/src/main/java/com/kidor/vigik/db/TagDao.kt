package com.kidor.vigik.db

import androidx.room.Dao
import androidx.room.Query
import com.kidor.vigik.db.base.BaseDao
import com.kidor.vigik.db.model.RoomTag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao : BaseDao<RoomTag> {

    @Query("SELECT * FROM tag")
    fun getAll(): Flow<List<RoomTag>>
}