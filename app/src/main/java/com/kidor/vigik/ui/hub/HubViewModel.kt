package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.BaseViewModel

class HubViewModel : BaseViewModel<HubViewAction, Nothing, HubViewEvent>() {

    override fun handleAction(viewAction: HubViewAction) {
        _viewEvent.value = when (viewAction) {
            HubViewAction.DisplayEmulateTagView -> HubViewEvent.NavigateToEmulateView.wrap()
            HubViewAction.DisplayScanTagView -> HubViewEvent.NavigateToScanView.wrap()
            HubViewAction.DisplayTagHistoryView -> HubViewEvent.NavigateToHistoryView.wrap()
        }
    }
}