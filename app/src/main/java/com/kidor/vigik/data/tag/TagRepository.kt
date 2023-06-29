package com.kidor.vigik.data.tag

import com.kidor.vigik.data.nfc.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Interface of a tag repository.
 */
interface TagRepository {

    /**
     * Returns all tags from the database.
     */
    val allTags: Flow<List<Tag>>

    /**
     * Inserts a tag in a database table.
     *
     * @param tag the object to be inserted
     * @return the new row ID for the inserted tag or -1 if an error occurred
     */
    suspend fun insert(tag: Tag): Long

    /**
     * Updates a tag from a database table.
     *
     * @param newValue the tag to be updated with new values
     * @return the number of rows that were updated successfully
     */
    suspend fun update(newValue: Tag): Int

    /**
     * Deletes a tag from a database table.
     *
     * @param tag the tag to be deleted
     * @return the number of rows that were deleted successfully
     */
    suspend fun delete(tag: Tag): Int
}
