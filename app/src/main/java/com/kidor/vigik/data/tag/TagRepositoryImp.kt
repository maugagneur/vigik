package com.kidor.vigik.data.tag

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.WorkerThread
import com.kidor.vigik.extensions.toRoomTag
import com.kidor.vigik.extensions.toTagList
import com.kidor.vigik.data.nfc.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [TagRepository].
 */
class TagRepositoryImp @Inject constructor(private val tagDao: TagDao) : TagRepository {

    override val allTags: Flow<List<Tag>> = tagDao.getAll().map { it.toTagList() }

    @WorkerThread
    override suspend fun insert(tag: Tag): Long {
        return try {
            tagDao.insert(tag.toRoomTag())
        } catch (exception: SQLiteConstraintException) {
            Timber.e(exception, "Error when trying to insert following item in database: $tag")
            -1
        }
    }

    @WorkerThread
    override suspend fun update(newValue: Tag): Int = tagDao.update(newValue.toRoomTag())

    @WorkerThread
    override suspend fun delete(tag: Tag): Int = tagDao.delete(tag.toRoomTag())
}
