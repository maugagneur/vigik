package com.kidor.vigik.db

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.WorkerThread
import com.kidor.vigik.extensions.toRoomTag
import com.kidor.vigik.extensions.toTagList
import com.kidor.vigik.nfc.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class TagRepository @Inject constructor(private val tagDao: TagDao) {

    val allTags: Flow<List<Tag>> = tagDao.getAll().map { it.toTagList() }

    /**
     * Inserts a tag in a database table.
     *
     * @param tag the object to be inserted
     * @return the new row ID for the inserted tag or -1 if an error occurred
     */
    @WorkerThread
    suspend fun insert(tag: Tag): Long {
        return try {
            tagDao.insert(tag.toRoomTag())
        } catch (exception: SQLiteConstraintException) {
            Timber.e(exception, "Error when trying to insert following item in database: $tag")
            -1
        }
    }

    /**
     * Updates a tag from a database table.
     *
     * @param tag the tag to be updated
     * @return the number of rows that were updated successfully
     */
    @WorkerThread
    suspend fun update(tag: Tag) = tagDao.update(tag.toRoomTag())

    /**
     * Deletes a tag from a database table.
     *
     * @param tag the tag to be deleted
     * @return the number of rows that were deleted successfully
     */
    @WorkerThread
    suspend fun delete(tag: Tag) = tagDao.delete(tag.toRoomTag())
}