package com.kidor.vigik.ui.history

import androidx.lifecycle.*
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TagRepository
) : ViewModel() {

    private val viewStateMediator: MediatorLiveData<HistoryViewState> = MediatorLiveData<HistoryViewState>()

    val viewState: LiveData<HistoryViewState> get() = viewStateMediator

    init {
        val initialViewState: MutableLiveData<HistoryViewState> = MutableLiveData<HistoryViewState>()
        initialViewState.value = HistoryViewState.Initializing

        viewStateMediator.addSource(initialViewState) { viewStateMediator.value = it }
        viewStateMediator.addSource(repository.allTags.asLiveData()) { tags ->
            viewStateMediator.removeSource(initialViewState)
            viewStateMediator.value = if (tags.isEmpty()) {
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