package com.kidor.vigik.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TagRepository
) : ViewModel(), HistoryContract.HistoryViewModel {

    private lateinit var view: HistoryContract.HistoryView

    override fun setView(view: HistoryContract.HistoryView) {
        this.view = view
    }

    override val allTags: LiveData<List<Tag>> = repository.allTags.asLiveData()

    override fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.delete(tag)
        }
    }
}