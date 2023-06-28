package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to the view from [HubViewModel].
 */
sealed class HubViewEvent : ViewEvent() {

    /**
     * Event when we have to navigate to Scan view.
     */
    object NavigateToScanView : HubViewEvent()

    /**
     * Event when we have to navigate to History view.
     */
    object NavigateToHistoryView : HubViewEvent()

    /**
     * Event when we have to navigate to Emulate view.
     */
    object NavigateToEmulateView : HubViewEvent()

    /**
     * Event when we have to navigate to Biometric view.
     */
    object NavigateToBiometricView : HubViewEvent()

    /**
     * Event when we have to navigate to REST API view.
     */
    object NavigateToRestApiView : HubViewEvent()
}
