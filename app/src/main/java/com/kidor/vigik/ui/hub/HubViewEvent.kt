package com.kidor.vigik.ui.hub

sealed class HubViewEvent {

    /**
     * Event when we have to navigate to Scan view.
     */
    object NavigateToScanView: HubViewEvent()

    /**
     * Event when we have to navigate to History view.
     */
    object NavigateToHistoryView: HubViewEvent()

    /**
     * Event when we have to navigate to Emulate view.
     */
    object NavigateToEmulateView: HubViewEvent()

    override fun toString(): String = javaClass.simpleName
}
