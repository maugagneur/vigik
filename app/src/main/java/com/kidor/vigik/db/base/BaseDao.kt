package com.kidor.vigik.db.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

/**
 * Generic DAO methods to reduce the amount of boilerplate code.
 */
interface BaseDao<T> {
    @Insert()
    suspend fun insert(obj: T): Long

    @Insert
    suspend fun insert(vararg obj: T): LongArray

    @Update
    suspend fun update(obj: T): Int

    @Delete
    suspend fun delete(obj: T): Int

    @Delete
    suspend fun delete(vararg obj: T): Int
}