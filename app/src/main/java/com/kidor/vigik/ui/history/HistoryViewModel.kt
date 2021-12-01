package com.kidor.vigik.ui.history

import androidx.lifecycle.*
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TagRepository
) : BaseViewModel<HistoryViewState, Nothing>() {

    override val _viewState: MediatorLiveData<HistoryViewState> = MediatorLiveData<HistoryViewState>()

    init {
        val initialViewState: MutableLiveData<HistoryViewState> = MutableLiveData<HistoryViewState>()
        initialViewState.value = HistoryViewState.Initializing

        _viewState.addSource(initialViewState) { _viewState.value = it }
        _viewState.addSource(repository.allTags.asLiveData()) { tags ->
            _viewState.removeSource(initialViewState)
            _viewState.value = if (tags.isEmpty()) {
                HistoryViewState.NoTag
            } else {
                HistoryViewState.DisplayTags(tags.sortedByDescending { it.timestamp })
            }
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.delete(tag)
        }
    }
}