package com.kidor.vigik.ui.nfc.history

import com.kidor.vigik.data.nfc.model.Tag
import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of tag history's view.
 */
sealed interface HistoryViewState : ViewState {

    /**
     * Initial state of the view.
     */
    data object Initializing : HistoryViewState

    /**
     * State when the view displays a list of tags.
     *
     * @param tags The list of tags to display.
     */
    data class DisplayTags(val tags: List<Tag>) : HistoryViewState

    /**
     * State when the view has not tags to display.
     */
    data object NoTag : HistoryViewState
}
