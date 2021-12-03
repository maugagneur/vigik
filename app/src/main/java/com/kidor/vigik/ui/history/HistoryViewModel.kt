package com.kidor.vigik.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TagRepository
) : BaseViewModel<HistoryViewState, Nothing>() {

    override val viewState: LiveData<HistoryViewState> = repository.allTags
        .map { tags ->
            if (tags.isEmpty()) {
                HistoryViewState.NoTag
            } else {
                HistoryViewState.DisplayTags(tags.sortedByDescending { it.timestamp })
            }
        }
        .onStart {
            emit(HistoryViewState.Initializing)
        }
        .asLiveData()

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.delete(tag)
        }
    }
}