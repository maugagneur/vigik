package com.kidor.vigik.ui.emulate

/**
 * Possible states of emulate view.
 */
sealed class EmulateViewState {

    /**
     * State when the view have to display logs.
     *
     * @param newLine The new log line to add.
     */
    data class DisplayLogLine(val newLine: String) : EmulateViewState()
}
