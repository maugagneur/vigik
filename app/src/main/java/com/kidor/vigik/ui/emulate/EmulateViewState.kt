package com.kidor.vigik.ui.emulate

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of emulate view.
 */
sealed class EmulateViewState : ViewState() {

    /**
     * State when the view have to display logs.
     *
     * @param newLine The new log line to add.
     */
    data class DisplayLogLine(val newLine: String) : EmulateViewState()
}
