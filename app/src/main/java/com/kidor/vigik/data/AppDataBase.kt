package com.kidor.vigik.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kidor.vigik.data.tag.TagDao
import com.kidor.vigik.data.tag.model.RoomTag

/** The name of the application database. **/
const val DATABASE_NAME: String = "vigik_database"
/** The version of the application database. **/
private const val DATABASE_VERSION = 1

/**
 * Representation of the database used by the application.
 */
@Database(entities = [RoomTag::class], version = DATABASE_VERSION, exportSchema = true)
abstract class AppDataBase : RoomDatabase() {

    /**
     * Returns the DAO (Data Access Object) for tags.
     */
    abstract fun tagDao(): TagDao
}
