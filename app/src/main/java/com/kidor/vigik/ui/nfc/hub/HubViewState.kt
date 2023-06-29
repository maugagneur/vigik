package com.kidor.vigik.ui.nfc.hub

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of tag hub's view.
 */
sealed class HubViewState : ViewState() {

    /**
     * Default state of the view.
     */
    object Default : HubViewState()
}
