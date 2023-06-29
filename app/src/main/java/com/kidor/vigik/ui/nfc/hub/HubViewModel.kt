package com.kidor.vigik.ui.nfc.hub

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
                HubViewAction.DisplayBiometricView -> HubViewEvent.NavigateToBiometricView
                HubViewAction.DisplayRestApiView -> HubViewEvent.NavigateToRestApiView
            }
            _viewEvent.emit(event)
        }
    }
}
