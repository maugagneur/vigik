package com.kidor.vigik.ui.nfc.check

import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to the view from [CheckViewModel].
 */
sealed class CheckViewEvent : ViewEvent {

    /**
     * Event when we have to navigate to phone's Settings.
     */
    data object NavigateToSettings : CheckViewEvent()

    /**
     * Event when we have to navigate to Hub view.
     */
    data object NavigateToHub : CheckViewEvent()
}
