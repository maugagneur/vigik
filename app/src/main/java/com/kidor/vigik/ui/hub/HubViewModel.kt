package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.BaseViewModel
import com.kidor.vigik.ui.base.EventWrapper

class HubViewModel : BaseViewModel<Nothing, HubViewEvent>() {

    fun onActionEmulateTag() {
        _viewEvent.value = EventWrapper(HubViewEvent.NavigateToEmulateView)
    }

    fun onActionReadTag() {
        _viewEvent.value = EventWrapper(HubViewEvent.NavigateToScanView)
    }

    fun onActionTagHistory() {
        _viewEvent.value = EventWrapper(HubViewEvent.NavigateToHistoryView)
    }
}