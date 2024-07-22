package com.kidor.vigik.ui.nfc.emulate

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of emulate view.
 */
sealed interface EmulateViewState : ViewState {

    /**
     * State when the view have to display logs.
     *
     * @param logLines The log lines to add.
     */
    data class DisplayLogLines(val logLines: String) : EmulateViewState
}
