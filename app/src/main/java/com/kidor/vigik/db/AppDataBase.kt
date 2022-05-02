package com.kidor.vigik.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kidor.vigik.db.model.RoomTag

const val DATABASE_NAME = "vigik_database"
const val DATABASE_VERSION = 1

@Database(entities = [RoomTag::class], version = DATABASE_VERSION)
abstract class AppDataBase : RoomDatabase() {
    abstract fun tagDao(): TagDao
}
