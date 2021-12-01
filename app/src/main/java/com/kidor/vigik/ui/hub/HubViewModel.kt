package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.BaseViewModel
import com.kidor.vigik.utils.Event

class HubViewModel : BaseViewModel<Nothing, HubViewEvent>() {

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