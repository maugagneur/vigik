package com.kidor.vigik.ui.hub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kidor.vigik.utils.Event

class HubViewModel : ViewModel() {

    private val _viewEvent = MutableLiveData<Event<HubViewEvent>>()
    val viewEvent: LiveData<Event<HubViewEvent>> get() = _viewEvent

    fun onActionEmulateTag() {
        _viewEvent.value = Event(HubViewEvent.NavigateToEmulateView)
    }

    fun onActionReadTag() {
        _viewEvent.value = Event(HubViewEvent.NavigateToScanView)
    }

    fun onActionTagHistory() {
        _viewEvent.value = Event(HubViewEvent.NavigateToHistoryView)
    }
}