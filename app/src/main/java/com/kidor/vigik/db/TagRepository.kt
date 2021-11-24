package com.kidor.vigik.db

import androidx.annotation.WorkerThread
import com.kidor.vigik.extensions.convert
import com.kidor.vigik.nfc.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepository @Inject constructor(private val tagDao: TagDao) {

    val allTags: Flow<List<Tag>> = tagDao.getAll().map { it.convert() }

    @WorkerThread
    suspend fun insert(tag: Tag) {
        tagDao.insert(tag.convert())
    }

    @WorkerThread
    suspend fun update(tag: Tag) {
        tagDao.update(tag.convert())
    }

    @WorkerThread
    suspend fun delete(tag: Tag): Int {
        return tagDao.delete(tag.convert())
    }
}