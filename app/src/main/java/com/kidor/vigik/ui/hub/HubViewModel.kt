package com.kidor.vigik.ui.hub

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * Business logic of displaying all sections of the application.
 */
class HubViewModel : BaseViewModel<HubViewAction, HubViewState, HubViewEvent>() {

    init {
        _viewState.value = HubViewState.Default
    }

    override fun handleAction(viewAction: HubViewAction) {
        viewModelScope.launch {
            val event = when (viewAction) {
                HubViewAction.DisplayEmulateTagView -> HubViewEvent.NavigateToEmulateView
                HubViewAction.DisplayScanTagView -> HubViewEvent.NavigateToScanView
                HubViewAction.DisplayTagHistoryView -> HubViewEvent.NavigateToHistoryView
            }
            _viewEvent.emit(event)
        }
    }
}
