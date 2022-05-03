package com.kidor.vigik.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kidor.vigik.db.model.RoomTag

/** The name of the application database. **/
const val DATABASE_NAME = "vigik_database"
/** The version of the application database. **/
const val DATABASE_VERSION = 1

/**
 * Representation of the database used by the application.
 */
@Database(entities = [RoomTag::class], version = DATABASE_VERSION)
abstract class AppDataBase : RoomDatabase() {

    /**
     * Returns the DAO (Data Access Object) for tags.
     */
    abstract fun tagDao(): TagDao
}
