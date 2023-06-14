package com.kidor.vigik.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Update

/**
 * Generic DAO methods to reduce the amount of boilerplate code.
 */
interface BaseDao<T> {

    /**
     * Inserts an object in a database table.
     *
     * @param obj the object to be inserted
     * @return the new row ID for the inserted item
     */
    @Insert(onConflict = REPLACE)
    suspend fun insert(obj: T): Long

    /**
     * Inserts objects in a database table.
     *
     * @param obj the objects to be inserted
     * @return the array of row ID for the inserted items
     */
    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg obj: T): LongArray

    /**
     * Updates an object from a database table.
     *
     * @param obj the object to be updated
     * @return the number of rows that were updated successfully
     */
    @Update
    suspend fun update(obj: T): Int

    /**
     * Deletes an object from a database table.
     *
     * @param obj the object to be deleted
     * @return the number of rows that were deleted successfully
     */
    @Delete
    suspend fun delete(obj: T): Int

    /**
     * Deletes objects from a database table.
     *
     * @param obj the objects to be deleted
     * @return the number of rows that were deleted successfully
     */
    @Delete
    suspend fun delete(vararg obj: T): Int
}
