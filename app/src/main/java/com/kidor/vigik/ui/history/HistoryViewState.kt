package com.kidor.vigik.ui.history

import com.kidor.vigik.nfc.model.Tag

/**
 * Possible states of tag history's view.
 */
sealed class HistoryViewState {

    /**
     * Initial state of the view.
     */
    object Initializing: HistoryViewState()

    /**
     * State when the view displays a list of tags.
     *
     * @param tags The list of tags to display.
     */
    data class DisplayTags(val tags: List<Tag>): HistoryViewState()

    /**
     * State when the view has not tags to display.
     */
    object NoTag: HistoryViewState()
}