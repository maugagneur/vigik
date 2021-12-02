package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.BaseViewModel

class HubViewModel : BaseViewModel<Nothing, HubViewEvent>() {

    fun onActionEmulateTag() {
        _viewEvent.value = HubViewEvent.NavigateToEmulateView.wrap()
    }

    fun onActionReadTag() {
        _viewEvent.value = HubViewEvent.NavigateToScanView.wrap()
    }

    fun onActionTagHistory() {
        _viewEvent.value = HubViewEvent.NavigateToHistoryView.wrap()
    }
}