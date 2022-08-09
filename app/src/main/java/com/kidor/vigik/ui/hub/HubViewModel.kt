package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.BaseViewModel

/**
 * Business logic of displaying all sections of the application.
 */
class HubViewModel : BaseViewModel<HubViewAction, HubViewState, HubViewEvent>() {

    init {
        _viewState.value = HubViewState.Default
    }

    override fun handleAction(viewAction: HubViewAction) {
        _viewEvent.value = when (viewAction) {
            HubViewAction.DisplayEmulateTagView -> HubViewEvent.NavigateToEmulateView.wrap()
            HubViewAction.DisplayScanTagView -> HubViewEvent.NavigateToScanView.wrap()
            HubViewAction.DisplayTagHistoryView -> HubViewEvent.NavigateToHistoryView.wrap()
        }
    }
}
