package com.kidor.vigik.data.tag

import androidx.room.Dao
import androidx.room.Query
import com.kidor.vigik.data.BaseDao
import com.kidor.vigik.data.tag.model.RoomTag
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for tags in database.
 */
@Dao
interface TagDao : BaseDao<RoomTag> {

    /**
     * Gets all tags from the database.
     */
    @Query("SELECT * FROM tag")
    fun getAll(): Flow<List<RoomTag>>
}
