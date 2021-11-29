package com.kidor.vigik.ui.scan

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.api.NfcApiListener
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val nfcApi: NfcApi,
    private val tagRepository: TagRepository
) : ViewModel(), NfcApiListener {

    private val _viewState = MutableLiveData<ScanViewState>()
    val viewState: LiveData<ScanViewState> get() = _viewState

    private val _viewEvent = MutableLiveData<Event<ScanViewEvent>>()
    val viewEvent: LiveData<Event<ScanViewEvent>> get() = _viewEvent

    private var lastTagScanned: Tag? = null

    init {
        _viewState.value = ScanViewState.Loading
        nfcApi.register(this)
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        super.onCleared()
        nfcApi.unregister(this)
    }

    override fun onNfcTagRead(tag: Tag) {
        this.lastTagScanned = tag
        _viewState.value = ScanViewState.DisplayTag(tag, tag.uid != null)
    }

    fun saveTag() {
        lastTagScanned.let { tag ->
            if (tag == null) {
                Timber.w("Trying to save invalid tag into database")
                _viewEvent.value = Event(ScanViewEvent.SaveTagFailure)
            } else {
                insertTagInTheDatabase(tag)
            }
        }
    }

    private fun insertTagInTheDatabase(tag: Tag) {
        viewModelScope.launch {
            if (tagRepository.insert(tag) > 0) {
                Timber.i("Tag saved into database with success")
                _viewEvent.value = Event(ScanViewEvent.SaveTagSuccess)
            } else {
                Timber.e("Fail to save tag into database")
                _viewEvent.value = Event(ScanViewEvent.SaveTagFailure)
            }
        }
    }
}