package com.kidor.vigik.ui.check

import com.kidor.vigik.ui.base.ViewEvent

sealed class CheckViewEvent : ViewEvent() {

    /**
     * Event when we have to navigate to phone's Settings.
     */
    object NavigateToSettings : CheckViewEvent()

    /**
     * Event when we have to navigate to Hub view.
     */
    object NavigateToHub : CheckViewEvent()
}