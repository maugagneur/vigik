package com.kidor.vigik.db

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.kidor.vigik.nfc.model.Tag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeTagRepository @Inject constructor() : TagRepository {

    private val tagList: MutableList<Tag> = mutableListOf()
    private val tags = MutableLiveData<List<Tag>>()

    init {
        refreshData()
    }

    override val allTags: Flow<List<Tag>>
        get() = tags.asFlow()

    override suspend fun insert(tag: Tag): Long {
        tagList.add(tag)
        refreshData()
        return tagList.size.toLong()
    }

    override suspend fun update(newValue: Tag): Int {
        tagList.find { it.timestamp == newValue.timestamp }
            ?.let { oldTag ->
                delete(oldTag)
                insert(newValue)
            }
        refreshData()
        return 1
    }

    override suspend fun delete(tag: Tag): Int {
        return if (tagList.remove(tag)) {
            refreshData()
            1
        } else {
            -1
        }
    }

    private fun refreshData() {
        tags.postValue(tagList)
    }
}