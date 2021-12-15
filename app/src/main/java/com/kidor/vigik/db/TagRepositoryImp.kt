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
    override suspend fun update(newValue: Tag) = tagDao.update(newValue.toRoomTag())

    @WorkerThread
    override suspend fun delete(tag: Tag) = tagDao.delete(tag.toRoomTag())
}